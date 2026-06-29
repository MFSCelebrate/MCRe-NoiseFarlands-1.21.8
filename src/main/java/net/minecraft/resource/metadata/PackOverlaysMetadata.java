/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.resource.metadata;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.dynamic.Range;

public record PackOverlaysMetadata(List<Entry> overlays) {
    final static private Pattern DIRECTORY_NAME_PATTERN = Pattern.compile("[-_a-zA-Z0-9.]+");
    final static private Codec<PackOverlaysMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Entry.CODEC.listOf().fieldOf("entries").forGetter(PackOverlaysMetadata::overlays)).apply((Applicative)instance, PackOverlaysMetadata::new));
    final static public ResourceMetadataSerializer<PackOverlaysMetadata> SERIALIZER = new ResourceMetadataSerializer<PackOverlaysMetadata>("overlays", CODEC);

    private static DataResult<String> validate(String directoryName) {
        if (!DIRECTORY_NAME_PATTERN.matcher(directoryName).matches()) {
            return DataResult.error(() -> directoryName + " is not accepted directory name");
        }
        return DataResult.success((Object)directoryName);
    }

    public List<String> getAppliedOverlays(int packFormat) {
        return this.overlays.stream().filter(overlay -> overlay.isValid(packFormat)).map(Entry::overlay).toList();
    }

    public record Entry(Range<Integer> format, String overlay) {
        final static Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Range.createCodec(Codec.INT).fieldOf("formats").forGetter(Entry::format), (App)Codec.STRING.validate(PackOverlaysMetadata::validate).fieldOf("directory").forGetter(Entry::overlay)).apply((Applicative)instance, Entry::new));

        public boolean isValid(int packFormat) {
            return this.format.contains(packFormat);
        }
    }
}

