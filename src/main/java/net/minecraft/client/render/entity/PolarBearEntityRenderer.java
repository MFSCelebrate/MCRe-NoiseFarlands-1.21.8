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
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PolarBearEntityRenderState;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityRenderer
extends AgeableMobEntityRenderer<PolarBearEntity, PolarBearEntityRenderState, PolarBearEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bear/polarbear.png");

    public PolarBearEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR)), new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR_BABY)), 0.9f);
    }

    @Override
    public Identifier getTexture(PolarBearEntityRenderState polarBearEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public PolarBearEntityRenderState net_minecraft_client_render_entity_state_PolarBearEntityRenderState_createRenderState() {
        return new PolarBearEntityRenderState();
    }

    @Override
    public void updateRenderState(PolarBearEntity polarBearEntity, PolarBearEntityRenderState polarBearEntityRenderState, float f) {
        super.updateRenderState(polarBearEntity, polarBearEntityRenderState, f);
        polarBearEntityRenderState.warningAnimationProgress = polarBearEntity.getWarningAnimationProgress(f);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PolarBearEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_PolarBearEntityRenderState_createRenderState();
    }
}

