/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.Animator;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Sprite {
    final private Identifier atlasId;
    final private SpriteContents contents;
    final private boolean animated;
    final int x;
    final int y;
    final private float minU;
    final private float maxU;
    final private float minV;
    final private float maxV;

    protected Sprite(Identifier atlasId, SpriteContents contents, int atlasWidth, int atlasHeight, int x, int y) {
        this.atlasId = atlasId;
        this.contents = contents;
        this.animated = contents.getMetadata().decode(AnimationResourceMetadata.SERIALIZER).isPresent();
        this.x = x;
        this.y = y;
        this.minU = (float)x / (float)atlasWidth;
        this.maxU = (float)(x + contents.getWidth()) / (float)atlasWidth;
        this.minV = (float)y / (float)atlasHeight;
        this.maxV = (float)(y + contents.getHeight()) / (float)atlasHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public float getMinU() {
        return this.minU;
    }

    public float getMaxU() {
        return this.maxU;
    }

    public SpriteContents getContents() {
        return this.contents;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    @Nullable
    public TickableAnimation createAnimation() {
        final Animator animator = this.contents.createAnimator();
        if (animator != null) {
            return new TickableAnimation(){

                @Override
                public void tick(GpuTexture texture) {
                    animator.tick(Sprite.this.x, Sprite.this.y, texture);
                }

                @Override
                public void close() {
                    animator.close();
                }
            };
        }
        return null;
    }

    public float getFrameU(float frame) {
        float f = this.maxU - this.minU;
        return this.minU + f * frame;
    }

    public float getFrameFromU(float u) {
        float f = this.maxU - this.minU;
        return (u - this.minU) / f;
    }

    public float getMinV() {
        return this.minV;
    }

    public float getMaxV() {
        return this.maxV;
    }

    public float getFrameV(float frame) {
        float f = this.maxV - this.minV;
        return this.minV + f * frame;
    }

    public float getFrameFromV(float v) {
        float f = this.maxV - this.minV;
        return (v - this.minV) / f;
    }

    public Identifier getAtlasId() {
        return this.atlasId;
    }

    public String toString() {
        return "TextureAtlasSprite{contents='" + String.valueOf(this.contents) + "', u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + "}";
    }

    public void upload(GpuTexture texture) {
        this.contents.upload(this.x, this.y, texture);
    }

    private float getUvScaleDeltaFactor() {
        float f = (float)this.contents.getWidth() / (this.maxU - this.minU);
        float g = (float)this.contents.getHeight() / (this.maxV - this.minV);
        return Math.max(g, f);
    }

    public float getUvScaleDelta() {
        return 4.0f / this.getUvScaleDeltaFactor();
    }

    public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer consumer) {
        return new SpriteTexturedVertexConsumer(consumer, this);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface TickableAnimation
    extends AutoCloseable {
        public void tick(GpuTexture var1);

        @Override
        public void close();
    }
}

