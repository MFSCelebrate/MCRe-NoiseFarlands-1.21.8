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
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.Glyph;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpaceFont
implements Font {
    final private Int2ObjectMap<Glyph.EmptyGlyph> codePointsToGlyphs;

    public SpaceFont(Map<Integer, Float> codePointsToAdvances) {
        this.codePointsToGlyphs = new Int2ObjectOpenHashMap(codePointsToAdvances.size());
        codePointsToAdvances.forEach((codePoint, glyph) -> this.codePointsToGlyphs.put(codePoint.intValue(), () -> glyph.floatValue()));
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        return (Glyph)this.codePointsToGlyphs.get(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.codePointsToGlyphs.keySet());
    }

    @Environment(value=EnvType.CLIENT)
    public record Loader(Map<Integer, Float> advances) implements FontLoader
    {
        final static public MapCodec<Loader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.unboundedMap(Codecs.CODEPOINT, (Codec)Codec.FLOAT).fieldOf("advances").forGetter(Loader::advances)).apply((Applicative)instance, Loader::new));

        @Override
        public FontType getType() {
            return FontType.SPACE;
        }

        @Override
        public Either<FontLoader.Loadable, FontLoader.Reference> build() {
            FontLoader.Loadable loadable = resourceManager -> new SpaceFont(this.advances);
            return Either.left((Object)loadable);
        }
    }
}

