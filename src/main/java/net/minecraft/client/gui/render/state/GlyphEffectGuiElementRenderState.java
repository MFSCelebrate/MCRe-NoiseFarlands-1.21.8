/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public record GlyphEffectGuiElementRenderState(Matrix3x2f pose, BakedGlyph whiteGlyph, BakedGlyph.Rectangle effect, @Nullable ScreenRect scissorArea) implements SimpleGuiElementRenderState
{
    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        Matrix4f matrix4f = new Matrix4f().mul((Matrix3x2fc)this.pose).translate(0.0f, 0.0f, depth);
        this.whiteGlyph.drawRectangle(this.effect, matrix4f, vertices, 0xF000F0, true);
    }

    @Override
    public RenderPipeline pipeline() {
        return this.whiteGlyph.getPipeline();
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.of(Objects.requireNonNull(this.whiteGlyph.getTexture()));
    }

    @Override
    @Nullable
    public ScreenRect bounds() {
        return null;
    }
}

