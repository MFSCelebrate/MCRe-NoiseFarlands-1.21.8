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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BreezeWindFeatureRenderer
extends FeatureRenderer<BreezeEntityRenderState, BreezeEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/breeze/breeze_wind.png");
    final private BreezeEntityModel model;

    public BreezeWindFeatureRenderer(EntityRendererFactory.Context entityRendererContext, FeatureRendererContext<BreezeEntityRenderState, BreezeEntityModel> featureContext) {
        super(featureContext);
        this.model = new BreezeEntityModel(entityRendererContext.getPart(EntityModelLayers.BREEZE_WIND));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, BreezeEntityRenderState breezeEntityRenderState, float f, float g) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getBreezeWind(TEXTURE, this.getXOffset(breezeEntityRenderState.age) % 1.0f, 0.0f));
        this.model.setAngles(breezeEntityRenderState);
        BreezeEntityRenderer.updatePartVisibility(this.model, this.model.getWindBody()).render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
    }

    private float getXOffset(float tickProgress) {
        return tickProgress * 0.02f;
    }
}

