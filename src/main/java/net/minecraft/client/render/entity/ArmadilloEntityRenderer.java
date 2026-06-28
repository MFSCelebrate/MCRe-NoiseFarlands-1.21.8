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
import net.minecraft.client.render.entity.model.ArmadilloEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmadilloEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ArmadilloEntityRenderer
extends AgeableMobEntityRenderer<ArmadilloEntity, ArmadilloEntityRenderState, ArmadilloEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armadillo.png");

    public ArmadilloEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ArmadilloEntityModel(context.getPart(EntityModelLayers.ARMADILLO)), new ArmadilloEntityModel(context.getPart(EntityModelLayers.ARMADILLO_BABY)), 0.4f);
    }

    @Override
    public Identifier getTexture(ArmadilloEntityRenderState armadilloEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public ArmadilloEntityRenderState net_minecraft_client_render_entity_state_ArmadilloEntityRenderState_createRenderState() {
        return new ArmadilloEntityRenderState();
    }

    @Override
    public void updateRenderState(ArmadilloEntity armadilloEntity, ArmadilloEntityRenderState armadilloEntityRenderState, float f) {
        super.updateRenderState(armadilloEntity, armadilloEntityRenderState, f);
        armadilloEntityRenderState.rolledUp = armadilloEntity.isRolledUp();
        armadilloEntityRenderState.scaredAnimationState.copyFrom(armadilloEntity.scaredAnimationState);
        armadilloEntityRenderState.unrollingAnimationState.copyFrom(armadilloEntity.unrollingAnimationState);
        armadilloEntityRenderState.rollingAnimationState.copyFrom(armadilloEntity.rollingAnimationState);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ArmadilloEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ArmadilloEntityRenderState_createRenderState();
    }
}

