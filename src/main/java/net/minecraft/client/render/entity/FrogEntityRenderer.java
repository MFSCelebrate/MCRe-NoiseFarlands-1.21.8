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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FrogEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FrogEntityRenderer
extends MobEntityRenderer<FrogEntity, FrogEntityRenderState, FrogEntityModel> {
    public FrogEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FrogEntityModel(context.getPart(EntityModelLayers.FROG)), 0.3f);
    }

    @Override
    public Identifier getTexture(FrogEntityRenderState frogEntityRenderState) {
        return frogEntityRenderState.texture;
    }

    @Override
    public FrogEntityRenderState net_minecraft_client_render_entity_state_FrogEntityRenderState_createRenderState() {
        return new FrogEntityRenderState();
    }

    @Override
    public void updateRenderState(FrogEntity frogEntity, FrogEntityRenderState frogEntityRenderState, float f) {
        super.updateRenderState(frogEntity, frogEntityRenderState, f);
        frogEntityRenderState.insideWaterOrBubbleColumn = frogEntity.isTouchingWater();
        frogEntityRenderState.longJumpingAnimationState.copyFrom(frogEntity.longJumpingAnimationState);
        frogEntityRenderState.croakingAnimationState.copyFrom(frogEntity.croakingAnimationState);
        frogEntityRenderState.usingTongueAnimationState.copyFrom(frogEntity.usingTongueAnimationState);
        frogEntityRenderState.idlingInWaterAnimationState.copyFrom(frogEntity.idlingInWaterAnimationState);
        frogEntityRenderState.texture = frogEntity.getVariant().value().assetInfo().texturePath();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FrogEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_FrogEntityRenderState_createRenderState();
    }
}

