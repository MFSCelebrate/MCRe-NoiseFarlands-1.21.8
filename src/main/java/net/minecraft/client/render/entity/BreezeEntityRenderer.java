/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.BreezeEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BreezeEntityRenderer
extends MobEntityRenderer<BreezeEntity, BreezeEntityRenderState, BreezeEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/breeze/breeze.png");

    public BreezeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BreezeEntityModel(context.getPart(EntityModelLayers.BREEZE)), 0.5f);
        this.addFeature(new BreezeWindFeatureRenderer(context, this));
        this.addFeature(new BreezeEyesFeatureRenderer(this));
    }

    @Override
    public void render(BreezeEntityRenderState breezeEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BreezeEntityModel breezeEntityModel = (BreezeEntityModel)this.getModel();
        BreezeEntityRenderer.updatePartVisibility(breezeEntityModel, breezeEntityModel.getHead(), breezeEntityModel.getRods());
        super.render(breezeEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public Identifier getTexture(BreezeEntityRenderState breezeEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public BreezeEntityRenderState net_minecraft_client_render_entity_state_BreezeEntityRenderState_createRenderState() {
        return new BreezeEntityRenderState();
    }

    @Override
    public void updateRenderState(BreezeEntity breezeEntity, BreezeEntityRenderState breezeEntityRenderState, float f) {
        super.updateRenderState(breezeEntity, breezeEntityRenderState, f);
        breezeEntityRenderState.idleAnimationState.copyFrom(breezeEntity.idleAnimationState);
        breezeEntityRenderState.shootingAnimationState.copyFrom(breezeEntity.shootingAnimationState);
        breezeEntityRenderState.slidingAnimationState.copyFrom(breezeEntity.slidingAnimationState);
        breezeEntityRenderState.slidingBackAnimationState.copyFrom(breezeEntity.slidingBackAnimationState);
        breezeEntityRenderState.inhalingAnimationState.copyFrom(breezeEntity.inhalingAnimationState);
        breezeEntityRenderState.longJumpingAnimationState.copyFrom(breezeEntity.longJumpingAnimationState);
    }

    public static BreezeEntityModel updatePartVisibility(BreezeEntityModel model, ModelPart ... modelParts) {
        model.getHead().visible = false;
        model.getEyes().visible = false;
        model.getRods().visible = false;
        model.getWindBody().visible = false;
        for (ModelPart modelPart : modelParts) {
            modelPart.visible = true;
        }
        return model;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BreezeEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_BreezeEntityRenderState_createRenderState();
    }
}

