/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix3x2f
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(value=EnvType.CLIENT)
public record TexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState
{
    public TexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, scissorArea, TexturedQuadGuiElementRenderState.createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1(), depth).texture(this.u1(), this.v1()).color(this.color());
        vertices.vertex(this.pose(), (float)this.x1(), (float)this.y2(), depth).texture(this.u1(), this.v2()).color(this.color());
        vertices.vertex(this.pose(), (float)this.x2(), (float)this.y2(), depth).texture(this.u2(), this.v2()).color(this.color());
        vertices.vertex(this.pose(), (float)this.x2(), (float)this.y1(), depth).texture(this.u2(), this.v1()).color(this.color());
    }

    @Nullable
    private static ScreenRect createBounds(int x1, int y1, int x2, int y2, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = new ScreenRect(x1, y1, x2 - x1, y2 - y1).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TexturedQuadGuiElementRenderState.class, "pipeline;textureSetup;pose;x0;y0;x1;y1;u0;u1;v0;v1;color;scissorArea;bounds", "pipeline", "textureSetup", "pose", "x1", "y1", "x2", "y2", "u1", "u2", "v1", "v2", "color", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TexturedQuadGuiElementRenderState.class, "pipeline;textureSetup;pose;x0;y0;x1;y1;u0;u1;v0;v1;color;scissorArea;bounds", "pipeline", "textureSetup", "pose", "x1", "y1", "x2", "y2", "u1", "u2", "v1", "v2", "color", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TexturedQuadGuiElementRenderState.class, "pipeline;textureSetup;pose;x0;y0;x1;y1;u0;u1;v0;v1;color;scissorArea;bounds", "pipeline", "textureSetup", "pose", "x1", "y1", "x2", "y2", "u1", "u2", "v1", "v2", "color", "scissorArea", "bounds"}, this, object);
    }
}

