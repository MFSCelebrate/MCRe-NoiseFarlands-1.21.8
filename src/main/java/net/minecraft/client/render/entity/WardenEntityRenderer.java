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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WardenEntityRenderState;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WardenEntityRenderer
extends MobEntityRenderer<WardenEntity, WardenEntityRenderState, WardenEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden.png");
    final static private Identifier BIOLUMINESCENT_LAYER_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_bioluminescent_layer.png");
    final static private Identifier HEART_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_heart.png");
    final static private Identifier PULSATING_SPOTS_1_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_pulsating_spots_1.png");
    final static private Identifier PULSATING_SPOTS_2_TEXTURE = Identifier.ofVanilla("textures/entity/warden/warden_pulsating_spots_2.png");

    public WardenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN)), 0.9f);
        this.addFeature(new EmissiveFeatureRenderer<WardenEntityRenderState, WardenEntityModel>(this, BIOLUMINESCENT_LAYER_TEXTURE, (state, tickProgress) -> 1.0f, WardenEntityModel::getHeadAndLimbs, RenderLayer::getEntityTranslucentEmissive, false));
        this.addFeature(new EmissiveFeatureRenderer<WardenEntityRenderState, WardenEntityModel>(this, PULSATING_SPOTS_1_TEXTURE, (state, tickProgress) -> Math.max(0.0f, MathHelper.cos(tickProgress * 0.045f) * 0.25f), WardenEntityModel::getBodyHeadAndLimbs, RenderLayer::getEntityTranslucentEmissive, false));
        this.addFeature(new EmissiveFeatureRenderer<WardenEntityRenderState, WardenEntityModel>(this, PULSATING_SPOTS_2_TEXTURE, (state, tickProgress) -> Math.max(0.0f, MathHelper.cos(tickProgress * 0.045f + (float)Math.PI) * 0.25f), WardenEntityModel::getBodyHeadAndLimbs, RenderLayer::getEntityTranslucentEmissive, false));
        this.addFeature(new EmissiveFeatureRenderer<WardenEntityRenderState, WardenEntityModel>(this, TEXTURE, (state, tickProgress) -> state.tendrilAlpha, WardenEntityModel::getTendrils, RenderLayer::getEntityTranslucentEmissive, false));
        this.addFeature(new EmissiveFeatureRenderer<WardenEntityRenderState, WardenEntityModel>(this, HEART_TEXTURE, (state, tickProgress) -> state.heartAlpha, WardenEntityModel::getBody, RenderLayer::getEntityTranslucentEmissive, false));
    }

    @Override
    public Identifier getTexture(WardenEntityRenderState wardenEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public WardenEntityRenderState net_minecraft_client_render_entity_state_WardenEntityRenderState_createRenderState() {
        return new WardenEntityRenderState();
    }

    @Override
    public void updateRenderState(WardenEntity wardenEntity, WardenEntityRenderState wardenEntityRenderState, float f) {
        super.updateRenderState(wardenEntity, wardenEntityRenderState, f);
        wardenEntityRenderState.tendrilAlpha = wardenEntity.getTendrilAlpha(f);
        wardenEntityRenderState.heartAlpha = wardenEntity.getHeartAlpha(f);
        wardenEntityRenderState.roaringAnimationState.copyFrom(wardenEntity.roaringAnimationState);
        wardenEntityRenderState.sniffingAnimationState.copyFrom(wardenEntity.sniffingAnimationState);
        wardenEntityRenderState.emergingAnimationState.copyFrom(wardenEntity.emergingAnimationState);
        wardenEntityRenderState.diggingAnimationState.copyFrom(wardenEntity.diggingAnimationState);
        wardenEntityRenderState.attackingAnimationState.copyFrom(wardenEntity.attackingAnimationState);
        wardenEntityRenderState.chargingSonicBoomAnimationState.copyFrom(wardenEntity.chargingSonicBoomAnimationState);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WardenEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_WardenEntityRenderState_createRenderState();
    }
}

