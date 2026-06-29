/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BitmapFont
implements Font {
    final static Logger LOGGER = LogUtils.getLogger();
    final private NativeImage image;
    final private GlyphContainer<BitmapFontGlyph> glyphs;

    BitmapFont(NativeImage image, GlyphContainer<BitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        return this.glyphs.get(codePoint);
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.glyphs.getProvidedGlyphs());
    }

    @Environment(value=EnvType.CLIENT)
    static final class BitmapFontGlyph
    extends Record
    implements Glyph {
        final float scaleFactor;
        final NativeImage image;
        final int x;
        final int y;
        final int width;
        final int height;
        final private int advance;
        final int ascent;

        BitmapFontGlyph(float scaleFactor, NativeImage image, int x, int y, int width, int height, int advance, int ascent) {
            this.scaleFactor = scaleFactor;
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.advance = advance;
            this.ascent = ascent;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public BakedGlyph bake(Function<RenderableGlyph, BakedGlyph> function) {
            return function.apply(new RenderableGlyph(){

                @Override
                public float getOversample() {
                    return 1.0f / scaleFactor;
                }

                @Override
                public int getWidth() {
                    return width;
                }

                @Override
                public int getHeight() {
                    return height;
                }

                @Override
                public float getAscent() {
                    return ascent;
                }

                @Override
                public void upload(int x, int y, GpuTexture texture) {
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, image, 0, 0, x, y, width, height, x, y);
                }

                @Override
                public boolean hasColor() {
                    return image.getFormat().getChannelCount() > 1;
                }
            });
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BitmapFontGlyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scaleFactor", "image", "x", "y", "width", "height", "advance", "ascent"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BitmapFontGlyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scaleFactor", "image", "x", "y", "width", "height", "advance", "ascent"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BitmapFontGlyph.class, "scale;image;offsetX;offsetY;width;height;advance;ascent", "scaleFactor", "image", "x", "y", "width", "height", "advance", "ascent"}, this, object);
        }

        public float scaleFactor() {
            return this.scaleFactor;
        }

        public NativeImage image() {
            return this.image;
        }

        public int x() {
            return this.x;
        }

        public int y() {
            return this.y;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }

        public int advance() {
            return this.advance;
        }

        public int ascent() {
            return this.ascent;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Loader(Identifier file, int height, int ascent, int[][] codepointGrid) implements FontLoader
    {
        final static private Codec<int[][]> CODE_POINT_GRID_CODEC = Codec.STRING.listOf().xmap(strings -> {
            int i = strings.size();
            int[][] is = new int[i][];
            for (int j = 0; j < i; ++j) {
                is[j] = ((String)strings.get(j)).codePoints().toArray();
            }
            return is;
        }, codePointGrid -> {
            ArrayList<String> list = new ArrayList<String>(((int[][])codePointGrid).length);
            for (int[] is : codePointGrid) {
                list.add(new String(is, 0, is.length));
            }
            return list;
        }).validate(Loader::validateCodePointGrid);
        final static public MapCodec<Loader> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("file").forGetter(Loader::file), (App)Codec.INT.optionalFieldOf("height", (Object)8).forGetter(Loader::height), (App)Codec.INT.fieldOf("ascent").forGetter(Loader::ascent), (App)CODE_POINT_GRID_CODEC.fieldOf("chars").forGetter(Loader::codepointGrid)).apply((Applicative)instance, Loader::new)).validate(Loader::validate);

        private static DataResult<int[][]> validateCodePointGrid(int[][] codePointGrid) {
            int i = codePointGrid.length;
            if (i == 0) {
                return DataResult.error(() -> "Expected to find data in codepoint grid");
            }
            int[] is = codePointGrid[0];
            int j = is.length;
            if (j == 0) {
                return DataResult.error(() -> "Expected to find data in codepoint grid");
            }
            for (int k = 1; k < i; ++k) {
                int[] js = codePointGrid[k];
                if (js.length == j) continue;
                return DataResult.error(() -> "Lines in codepoint grid have to be the same length (found: " + js.length + " codepoints, expected: " + j + "), pad with \\u0000");
            }
            return DataResult.success((Object)codePointGrid);
        }

        private static DataResult<Loader> validate(Loader fontLoader) {
            if (fontLoader.ascent > fontLoader.height) {
                return DataResult.error(() -> "Ascent " + loader.ascent + " higher than height " + loader.height);
            }
            return DataResult.success((Object)fontLoader);
        }

        @Override
        public FontType getType() {
            return FontType.BITMAP;
        }

        @Override
        public Either<FontLoader.Loadable, FontLoader.Reference> build() {
            return Either.left(this::load);
        }

        private Font load(ResourceManager resourceManager) throws IOException {
            BitmapFont bitmapFont;
            block7: {
                Identifier identifier = this.file.withPrefixedPath("textures/");
                InputStream inputStream = resourceManager.open(identifier);
                try {
                    NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, inputStream);
                    int i2 = nativeImage.getWidth();
                    int j = nativeImage.getHeight();
                    int k = i2 / this.codepointGrid[0].length;
                    int l = j / this.codepointGrid.length;
                    float f = (float)this.height / (float)l;
                    GlyphContainer<BitmapFontGlyph> glyphContainer = new GlyphContainer<BitmapFontGlyph>(BitmapFontGlyph[]::new, i -> new BitmapFontGlyph[i][]);
                    for (int m = 0; m < this.codepointGrid.length; ++m) {
                        int n = 0;
                        for (int o : this.codepointGrid[m]) {
                            int q;
                            BitmapFontGlyph bitmapFontGlyph;
                            int p = n++;
                            if (o == 0 || (bitmapFontGlyph = glyphContainer.put(o, new BitmapFontGlyph(f, nativeImage, p * k, m * l, k, l, (int)(0.5 + (double)((float)(q = this.findCharacterStartX(nativeImage, k, l, p, m)) * f)) + 1, this.ascent))) == null) continue;
                            LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(o), (Object)identifier);
                        }
                    }
                    bitmapFont = new BitmapFont(nativeImage, glyphContainer);
                    if (inputStream == null) break block7;
                }
                catch (Throwable throwable) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                inputStream.close();
            }
            return bitmapFont;
        }

        private int findCharacterStartX(NativeImage image, int characterWidth, int characterHeight, int charPosX, int charPosY) {
            int i;
            for (i = characterWidth - 1; i >= 0; --i) {
                int j = charPosX * characterWidth + i;
                for (int k = 0; k < characterHeight; ++k) {
                    int l = charPosY * characterHeight + k;
                    if (image.getOpacity(j, l) == 0) continue;
                    return i + 1;
                }
            }
            return i + 1;
        }
    }
}

