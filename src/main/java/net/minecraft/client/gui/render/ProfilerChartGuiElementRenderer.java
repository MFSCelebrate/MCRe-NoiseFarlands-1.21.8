/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.ProfilerChartGuiElementRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.ProfilerTiming;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class ProfilerChartGuiElementRenderer
extends SpecialGuiElementRenderer<ProfilerChartGuiElementRenderState> {
    public ProfilerChartGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
        super(immediate);
    }

    @Override
    public Class<ProfilerChartGuiElementRenderState> getElementClass() {
        return ProfilerChartGuiElementRenderState.class;
    }

    @Override
    protected void render(ProfilerChartGuiElementRenderState profilerChartGuiElementRenderState, MatrixStack matrixStack) {
        double d = 0.0;
        matrixStack.translate(0.0f, -5.0f, 0.0f);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        for (ProfilerTiming profilerTiming : profilerChartGuiElementRenderState.chartData()) {
            float h;
            float g;
            float f;
            int l;
            int i = MathHelper.floor(profilerTiming.parentSectionUsagePercentage / 4.0) + 1;
            VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getDebugTriangleFan());
            int j = ColorHelper.fullAlpha(profilerTiming.getColor());
            int k = ColorHelper.mix(j, Colors.GRAY);
            vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(j);
            for (l = i; l >= 0; --l) {
                f = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)l / (double)i) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 105.0f;
                h = MathHelper.cos(f) * 105.0f * 0.5f;
                vertexConsumer.vertex(matrix4f, g, h, 0.0f).color(j);
            }
            vertexConsumer = this.vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
            for (l = i; l > 0; --l) {
                f = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)l / (double)i) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin(f) * 105.0f;
                h = MathHelper.cos(f) * 105.0f * 0.5f;
                float m = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)(l - 1) / (double)i) * 6.2831854820251465 / 100.0);
                float n = MathHelper.sin(m) * 105.0f;
                float o = MathHelper.cos(m) * 105.0f * 0.5f;
                if ((h + o) / 2.0f < 0.0f) continue;
                vertexConsumer.vertex(matrix4f, g, h, 0.0f).color(k);
                vertexConsumer.vertex(matrix4f, g, h + 10.0f, 0.0f).color(k);
                vertexConsumer.vertex(matrix4f, n, o + 10.0f, 0.0f).color(k);
                vertexConsumer.vertex(matrix4f, n, o, 0.0f).color(k);
            }
            d += profilerTiming.parentSectionUsagePercentage;
        }
    }

    @Override
    protected float getYOffset(int height, int windowScaleFactor) {
        return (float)height / 2.0f;
    }

    @Override
    protected String getName() {
        return "profiler chart";
    }
}

