/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArraySet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.freetype.FT_Bitmap
 *  org.lwjgl.util.freetype.FT_Face
 *  org.lwjgl.util.freetype.FT_GlyphSlot
 *  org.lwjgl.util.freetype.FT_Vector
 *  org.lwjgl.util.freetype.FreeType
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

@Environment(value=EnvType.CLIENT)
public class TrueTypeFont
implements Font {
    @Nullable
    private ByteBuffer buffer;
    @Nullable
    private FT_Face face;
    final float oversample;
    final private GlyphContainer<LazyGlyph> container = new GlyphContainer(LazyGlyph[]::new, i -> new LazyGlyph[i][]);

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public TrueTypeFont(ByteBuffer buffer, FT_Face face, float size, float oversample, float shiftX, float shiftY, String excludedCharacters) {
        this.buffer = buffer;
        this.face = face;
        this.oversample = oversample;
        IntArraySet intSet = new IntArraySet();
        excludedCharacters.codePoints().forEach(arg_0 -> ((IntSet)intSet).add(arg_0));
        int i2 = Math.round(size * oversample);
        FreeType.FT_Set_Pixel_Sizes((FT_Face)face, (int)i2, (int)i2);
        float f = shiftX * oversample;
        float g = -shiftY * oversample;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int k;
            FT_Vector fT_Vector = FreeTypeUtil.set(FT_Vector.malloc((MemoryStack)memoryStack), f, g);
            FreeType.FT_Set_Transform((FT_Face)face, null, (FT_Vector)fT_Vector);
            IntBuffer intBuffer = memoryStack.mallocInt(1);
            int j = (int)FreeType.FT_Get_First_Char((FT_Face)face, (IntBuffer)intBuffer);
            while ((k = intBuffer.get(0)) != 0) {
                if (!intSet.contains(j)) {
                    this.container.put(j, new LazyGlyph(k));
                }
                j = (int)FreeType.FT_Get_Next_Char((FT_Face)face, (long)j, (IntBuffer)intBuffer);
            }
            if (memoryStack == null) return;
        }
    }

    @Override
    @Nullable
    public Glyph getGlyph(int codePoint) {
        LazyGlyph lazyGlyph = this.container.get(codePoint);
        return lazyGlyph != null ? this.getOrLoadGlyph(codePoint, lazyGlyph) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Glyph getOrLoadGlyph(int codePoint, LazyGlyph glyph) {
        Glyph glyph2;
        block3: {
            FT_Face fT_Face;
            glyph2 = glyph.glyph;
            if (glyph2 != null) break block3;
            FT_Face fT_Face2 = fT_Face = this.getInfo();
            synchronized (fT_Face2) {
                glyph2 = glyph.glyph;
                if (glyph2 == null) {
                    glyph.glyph = glyph2 = this.loadGlyph(codePoint, fT_Face, glyph.index);
                }
            }
        }
        return glyph2;
    }

    private Glyph loadGlyph(int codePoint, FT_Face face, int index) {
        FT_GlyphSlot fT_GlyphSlot;
        int i = FreeType.FT_Load_Glyph((FT_Face)face, (int)index, 0x400008);
        if (i != 0) {
            FreeTypeUtil.checkFatalError(i, String.format(Locale.ROOT, "Loading glyph U+%06X", codePoint));
        }
        if ((fT_GlyphSlot = face.glyph()) == null) {
            throw new NullPointerException(String.format(Locale.ROOT, "Glyph U+%06X not initialized", codePoint));
        }
        float f = FreeTypeUtil.getX(fT_GlyphSlot.advance());
        FT_Bitmap fT_Bitmap = fT_GlyphSlot.bitmap();
        int j = fT_GlyphSlot.bitmap_left();
        int k = fT_GlyphSlot.bitmap_top();
        int l = fT_Bitmap.width();
        int m = fT_Bitmap.rows();
        if (l <= 0 || m <= 0) {
            return () -> f / this.oversample;
        }
        return new TtfGlyph(j, k, l, m, f, index);
    }

    FT_Face getInfo() {
        if (this.buffer == null || this.face == null) {
            throw new IllegalStateException("Provider already closed");
        }
        return this.face;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        if (this.face != null) {
            Object object = FreeTypeUtil.LOCK;
            synchronized (object) {
                FreeTypeUtil.checkError(FreeType.FT_Done_Face((FT_Face)this.face), "Deleting face");
            }
            this.face = null;
        }
        MemoryUtil.memFree((Buffer)this.buffer);
        this.buffer = null;
    }

    @Override
    public IntSet getProvidedGlyphs() {
        return this.container.getProvidedGlyphs();
    }

    @Environment(value=EnvType.CLIENT)
    static class LazyGlyph {
        final int index;
        @Nullable
        volatile Glyph glyph;

        LazyGlyph(int index) {
            this.index = index;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class TtfGlyph
    implements Glyph {
        final int width;
        final int height;
        final float bearingX;
        final float ascent;
        final private float advance;
        final int glyphIndex;

        TtfGlyph(float bearingX, float ascent, int width, int height, float advance, int glyphIndex) {
            this.width = width;
            this.height = height;
            this.advance = advance / TrueTypeFont.this.oversample;
            this.bearingX = bearingX / TrueTypeFont.this.oversample;
            this.ascent = ascent / TrueTypeFont.this.oversample;
            this.glyphIndex = glyphIndex;
        }

        @Override
        public float getAdvance() {
            return this.advance;
        }

        @Override
        public BakedGlyph bake(Function<RenderableGlyph, BakedGlyph> function) {
            return function.apply(new RenderableGlyph(){

                @Override
                public int getWidth() {
                    return TtfGlyph.this.width;
                }

                @Override
                public int getHeight() {
                    return TtfGlyph.this.height;
                }

                @Override
                public float getOversample() {
                    return TrueTypeFont.this.oversample;
                }

                @Override
                public float getBearingX() {
                    return TtfGlyph.this.bearingX;
                }

                @Override
                public float getAscent() {
                    return TtfGlyph.this.ascent;
                }

                @Override
                public void upload(int x, int y, GpuTexture texture) {
                    FT_Face fT_Face = TrueTypeFont.this.getInfo();
                    try (NativeImage nativeImage = new NativeImage(NativeImage.Format.LUMINANCE, TtfGlyph.this.width, TtfGlyph.this.height, false);){
                        if (nativeImage.makeGlyphBitmapSubpixel(fT_Face, TtfGlyph.this.glyphIndex)) {
                            RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, nativeImage, 0, 0, x, y, TtfGlyph.this.width, TtfGlyph.this.height, 0, 0);
                        }
                    }
                }

                @Override
                public boolean hasColor() {
                    return false;
                }
            });
        }
    }
}

