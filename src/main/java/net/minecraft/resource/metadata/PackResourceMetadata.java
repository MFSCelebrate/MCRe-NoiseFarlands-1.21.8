/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.resource.metadata;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Range;

public record PackResourceMetadata(Text description, int packFormat, Optional<Range<Integer>> supportedFormats) {
    final static public Codec<PackResourceMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)TextCodecs.CODEC.fieldOf("description").forGetter(PackResourceMetadata::description), (App)Codec.INT.fieldOf("pack_format").forGetter(PackResourceMetadata::packFormat), (App)Range.createCodec(Codec.INT).lenientOptionalFieldOf("supported_formats").forGetter(PackResourceMetadata::supportedFormats)).apply((Applicative)instance, PackResourceMetadata::new));
    final static public ResourceMetadataSerializer<PackResourceMetadata> SERIALIZER = new ResourceMetadataSerializer<PackResourceMetadata>("pack", CODEC);
}

