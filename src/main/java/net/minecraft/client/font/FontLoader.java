/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface FontLoader {
    final static public MapCodec<FontLoader> CODEC = FontType.CODEC.dispatchMap(FontLoader::getType, FontType::getLoaderCodec);

    public FontType getType();

    public Either<Loadable, Reference> build();

    @Environment(value=EnvType.CLIENT)
    public record Provider(FontLoader definition, FontFilterType.FilterMap filter) {
        final static public Codec<Provider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)CODEC.forGetter(Provider::definition), (App)FontFilterType.FilterMap.CODEC.optionalFieldOf("filter", (Object)FontFilterType.FilterMap.NO_FILTER).forGetter(Provider::filter)).apply((Applicative)instance, Provider::new));
    }

    @Environment(value=EnvType.CLIENT)
    public record Reference(Identifier id) {
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Loadable {
        public Font load(ResourceManager var1) throws IOException;
    }
}

