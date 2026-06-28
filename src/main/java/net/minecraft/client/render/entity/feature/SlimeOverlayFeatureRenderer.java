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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer
extends FeatureRenderer<SlimeEntityRenderState, SlimeEntityModel> {
    final private SlimeEntityModel model;

    public SlimeOverlayFeatureRenderer(FeatureRendererContext<SlimeEntityRenderState, SlimeEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new SlimeEntityModel(loader.getModelPart(EntityModelLayers.SLIME_OUTER));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SlimeEntityRenderState slimeEntityRenderState, float f, float g) {
        boolean bl;
        boolean bl2 = bl = slimeEntityRenderState.hasOutline && slimeEntityRenderState.invisible;
        if (slimeEntityRenderState.invisible && !bl) {
            return;
        }
        VertexConsumer vertexConsumer = bl ? vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SlimeEntityRenderer.TEXTURE)) : vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(SlimeEntityRenderer.TEXTURE));
        this.model.setAngles(slimeEntityRenderState);
        this.model.render(matrixStack, vertexConsumer, 1, LivingEntityRenderer.getOverlay(slimeEntityRenderState, 0.0f));
    }
}

