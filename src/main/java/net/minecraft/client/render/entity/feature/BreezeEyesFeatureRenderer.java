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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BreezeEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BreezeEyesFeatureRenderer
extends FeatureRenderer<BreezeEntityRenderState, BreezeEntityModel> {
    final static private RenderLayer TEXTURE = RenderLayer.getEntityTranslucentEmissiveNoOutline(Identifier.ofVanilla("textures/entity/breeze/breeze_eyes.png"));

    public BreezeEyesFeatureRenderer(FeatureRendererContext<BreezeEntityRenderState, BreezeEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, BreezeEntityRenderState breezeEntityRenderState, float f, float g) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(TEXTURE);
        BreezeEntityModel breezeEntityModel = (BreezeEntityModel)this.getContextModel();
        BreezeEntityRenderer.updatePartVisibility(breezeEntityModel, breezeEntityModel.getHead(), breezeEntityModel.getEyes()).render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
    }
}

