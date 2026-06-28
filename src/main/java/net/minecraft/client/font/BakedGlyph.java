/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 */
package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderLayerSet;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class BakedGlyph {
    final static public float Z_OFFSET = 0.001f;
    final private TextRenderLayerSet textRenderLayers;
    @Nullable
    final private GpuTextureView gpuTexture;
    final private float minU;
    final private float maxU;
    final private float minV;
    final private float maxV;
    final private float minX;
    final private float maxX;
    final private float minY;
    final private float maxY;

    public BakedGlyph(TextRenderLayerSet textRenderLayers, @Nullable GpuTextureView gpuTexture, float minU, float maxU, float minV, float maxV, float minX, float maxX, float minY, float maxY) {
        this.textRenderLayers = textRenderLayers;
        this.gpuTexture = gpuTexture;
        this.minU = minU;
        this.maxU = maxU;
        this.minV = minV;
        this.maxV = maxV;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public float getEffectiveMinX(DrawnGlyph glyph) {
        return glyph.x + this.minX + (glyph.style.isItalic() ? Math.min(this.getItalicOffsetAtMinY(), this.getItalicOffsetAtMaxY()) : 0.0f) - BakedGlyph.getXExpansion(glyph.style.isBold());
    }

    public float getEffectiveMinY(DrawnGlyph glyph) {
        return glyph.y + this.minY - BakedGlyph.getXExpansion(glyph.style.isBold());
    }

    public float getEffectiveMaxX(DrawnGlyph glyph) {
        return glyph.x + this.maxX + (glyph.hasShadow() ? glyph.shadowOffset : 0.0f) + (glyph.style.isItalic() ? Math.max(this.getItalicOffsetAtMinY(), this.getItalicOffsetAtMaxY()) : 0.0f) + BakedGlyph.getXExpansion(glyph.style.isBold());
    }

    public float getEffectiveMaxY(DrawnGlyph glyph) {
        return glyph.y + this.maxY + (glyph.hasShadow() ? glyph.shadowOffset : 0.0f) + BakedGlyph.getXExpansion(glyph.style.isBold());
    }

    public void draw(DrawnGlyph glyph, Matrix4f matrix, VertexConsumer vertexConsumer, int light, boolean fixedZ) {
        float k;
        float h;
        Style style = glyph.style();
        boolean bl = style.isItalic();
        float f = glyph.x();
        float g = glyph.y();
        int i = glyph.color();
        boolean bl2 = style.isBold();
        float f2 = h = fixedZ ? 0.0f : 0.001f;
        if (glyph.hasShadow()) {
            int j = glyph.shadowColor();
            this.draw(bl, f + glyph.shadowOffset(), g + glyph.shadowOffset(), 0.0f, matrix, vertexConsumer, j, bl2, light);
            if (bl2) {
                this.draw(bl, f + glyph.boldOffset() + glyph.shadowOffset(), g + glyph.shadowOffset(), h, matrix, vertexConsumer, j, true, light);
            }
            k = fixedZ ? 0.0f : 0.03f;
        } else {
            k = 0.0f;
        }
        this.draw(bl, f, g, k, matrix, vertexConsumer, i, bl2, light);
        if (bl2) {
            this.draw(bl, f + glyph.boldOffset(), g, k + h, matrix, vertexConsumer, i, true, light);
        }
    }

    private void draw(boolean italic, float x, float y, float z, Matrix4f matrix, VertexConsumer vertexConsumer, int color, boolean bold, int light) {
        float f = x + this.minX;
        float g = x + this.maxX;
        float h = y + this.minY;
        float i = y + this.maxY;
        float j = italic ? this.getItalicOffsetAtMinY() : 0.0f;
        float k = italic ? this.getItalicOffsetAtMaxY() : 0.0f;
        float l = BakedGlyph.getXExpansion(bold);
        vertexConsumer.vertex(matrix, f + j - l, h - l, z).color(color).texture(this.minU, this.minV).light(light);
        vertexConsumer.vertex(matrix, f + k - l, i + l, z).color(color).texture(this.minU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, g + k + l, i + l, z).color(color).texture(this.maxU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, g + j + l, h - l, z).color(color).texture(this.maxU, this.minV).light(light);
    }

    private static float getXExpansion(boolean bold) {
        return bold ? 0.1f : 0.0f;
    }

    private float getItalicOffsetAtMaxY() {
        return 1.0f - 0.25f * this.maxY;
    }

    private float getItalicOffsetAtMinY() {
        return 1.0f - 0.25f * this.minY;
    }

    public void drawRectangle(Rectangle rectangle, Matrix4f matrix, VertexConsumer vertexConsumer, int light, boolean fixedZ) {
        float f;
        float f2 = f = fixedZ ? 0.0f : rectangle.zIndex;
        if (rectangle.hasShadow()) {
            this.drawRectangle(rectangle, rectangle.shadowOffset(), f, rectangle.shadowColor(), vertexConsumer, light, matrix);
            f += fixedZ ? 0.0f : 0.03f;
        }
        this.drawRectangle(rectangle, 0.0f, f, rectangle.color, vertexConsumer, light, matrix);
    }

    private void drawRectangle(Rectangle rectangle, float shadowOffset, float zOffset, int color, VertexConsumer vertexConsumer, int light, Matrix4f matrix) {
        vertexConsumer.vertex(matrix, rectangle.minX + shadowOffset, rectangle.maxY + shadowOffset, zOffset).color(color).texture(this.minU, this.minV).light(light);
        vertexConsumer.vertex(matrix, rectangle.maxX + shadowOffset, rectangle.maxY + shadowOffset, zOffset).color(color).texture(this.minU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, rectangle.maxX + shadowOffset, rectangle.minY + shadowOffset, zOffset).color(color).texture(this.maxU, this.maxV).light(light);
        vertexConsumer.vertex(matrix, rectangle.minX + shadowOffset, rectangle.minY + shadowOffset, zOffset).color(color).texture(this.maxU, this.minV).light(light);
    }

    @Nullable
    public GpuTextureView getTexture() {
        return this.gpuTexture;
    }

    public RenderPipeline getPipeline() {
        return this.textRenderLayers.guiPipeline();
    }

    public RenderLayer getLayer(TextRenderer.TextLayerType layerType) {
        return this.textRenderLayers.getRenderLayer(layerType);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DrawnGlyph
    extends Record {
        final float x;
        final float y;
        final private int color;
        final private int shadowColor;
        final private BakedGlyph glyph;
        final Style style;
        final private float boldOffset;
        final float shadowOffset;

        public DrawnGlyph(float f, float g, int i, int j, BakedGlyph bakedGlyph, Style style, float h, float k) {
            this.x = f;
            this.y = g;
            this.color = i;
            this.shadowColor = j;
            this.glyph = bakedGlyph;
            this.style = style;
            this.boldOffset = h;
            this.shadowOffset = k;
        }

        public float getEffectiveMinX() {
            return this.glyph.getEffectiveMinX(this);
        }

        public float getEffectiveMinY() {
            return this.glyph.getEffectiveMinY(this);
        }

        public float getEffectiveMaxX() {
            return this.glyph.getEffectiveMaxX(this);
        }

        public float getEffectiveMaxY() {
            return this.glyph.getEffectiveMaxY(this);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DrawnGlyph.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DrawnGlyph.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DrawnGlyph.class, "x;y;color;shadowColor;glyph;style;boldOffset;shadowOffset", "x", "y", "color", "shadowColor", "glyph", "style", "boldOffset", "shadowOffset"}, this, object);
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }

        public int color() {
            return this.color;
        }

        public int shadowColor() {
            return this.shadowColor;
        }

        public BakedGlyph glyph() {
            return this.glyph;
        }

        public Style style() {
            return this.style;
        }

        public float boldOffset() {
            return this.boldOffset;
        }

        public float shadowOffset() {
            return this.shadowOffset;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Rectangle
    extends Record {
        final float minX;
        final float minY;
        final float maxX;
        final float maxY;
        final float zIndex;
        final int color;
        final private int shadowColor;
        final private float shadowOffset;

        public Rectangle(float minX, float minY, float maxX, float maxY, float zIndex, int color) {
            this(minX, minY, maxX, maxY, zIndex, color, 0, 0.0f);
        }

        public Rectangle(float f, float g, float h, float i, float j, int k, int l, float m) {
            this.minX = f;
            this.minY = g;
            this.maxX = h;
            this.maxY = i;
            this.zIndex = j;
            this.color = k;
            this.shadowColor = l;
            this.shadowOffset = m;
        }

        public float getEffectiveMinX() {
            return this.minX;
        }

        public float getEffectiveMinY() {
            return this.minY;
        }

        public float getEffectiveMaxX() {
            return this.maxX + (this.hasShadow() ? this.shadowOffset : 0.0f);
        }

        public float getEffectiveMaxY() {
            return this.maxY + (this.hasShadow() ? this.shadowOffset : 0.0f);
        }

        boolean hasShadow() {
            return this.shadowColor() != 0;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Rectangle.class, "x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Rectangle.class, "x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Rectangle.class, "x0;y0;x1;y1;depth;color;shadowColor;shadowOffset", "minX", "minY", "maxX", "maxY", "zIndex", "color", "shadowColor", "shadowOffset"}, this, object);
        }

        public float minX() {
            return this.minX;
        }

        public float minY() {
            return this.minY;
        }

        public float maxX() {
            return this.maxX;
        }

        public float maxY() {
            return this.maxY;
        }

        public float zIndex() {
            return this.zIndex;
        }

        public int color() {
            return this.color;
        }

        public int shadowColor() {
            return this.shadowColor;
        }

        public float shadowOffset() {
            return this.shadowOffset;
        }
    }
}

