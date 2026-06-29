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
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer
extends FeatureRenderer<DolphinEntityRenderState, DolphinEntityModel> {
    public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntityRenderState, DolphinEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, DolphinEntityRenderState dolphinEntityRenderState, float f, float g) {
        ItemRenderState itemRenderState = dolphinEntityRenderState.itemRenderState;
        if (itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        float h = 1.0f;
        float j = -1.0f;
        float k = MathHelper.abs(dolphinEntityRenderState.pitch) / 60.0f;
        if (dolphinEntityRenderState.pitch < 0.0f) {
            matrixStack.translate(0.0f, 1.0f - k * 0.5f, -1.0f + k * 0.5f);
        } else {
            matrixStack.translate(0.0f, 1.0f + k * 0.8f, -1.0f + k * 0.2f);
        }
        itemRenderState.render(matrixStack, vertexConsumerProvider, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}

