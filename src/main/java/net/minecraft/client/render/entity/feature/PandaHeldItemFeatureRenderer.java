/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer
extends FeatureRenderer<PandaEntityRenderState, PandaEntityModel> {
    public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntityRenderState, PandaEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PandaEntityRenderState pandaEntityRenderState, float f, float g) {
        ItemRenderState itemRenderState = pandaEntityRenderState.itemRenderState;
        if (itemRenderState.isEmpty() || !pandaEntityRenderState.sitting || pandaEntityRenderState.scaredByThunderstorm) {
            return;
        }
        float h = -0.6f;
        float j = 1.4f;
        if (pandaEntityRenderState.eating) {
            h -= 0.2f * MathHelper.sin(pandaEntityRenderState.age * 0.6f) + 0.2f;
            j -= 0.09f * MathHelper.sin(pandaEntityRenderState.age * 0.6f);
        }
        matrixStack.push();
        matrixStack.translate(0.1f, j, h);
        itemRenderState.render(matrixStack, vertexConsumerProvider, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}

