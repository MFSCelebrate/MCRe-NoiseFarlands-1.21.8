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
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.RavagerEntityRenderState;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityRenderer
extends MobEntityRenderer<RavagerEntity, RavagerEntityRenderState, RavagerEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/ravager.png");

    public RavagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new RavagerEntityModel(context.getPart(EntityModelLayers.RAVAGER)), 1.1f);
    }

    @Override
    public Identifier getTexture(RavagerEntityRenderState ravagerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public RavagerEntityRenderState net_minecraft_client_render_entity_state_RavagerEntityRenderState_createRenderState() {
        return new RavagerEntityRenderState();
    }

    @Override
    public void updateRenderState(RavagerEntity ravagerEntity, RavagerEntityRenderState ravagerEntityRenderState, float f) {
        super.updateRenderState(ravagerEntity, ravagerEntityRenderState, f);
        ravagerEntityRenderState.stunTick = (float)ravagerEntity.getStunTick() > 0.0f ? (float)ravagerEntity.getStunTick() - f : 0.0f;
        ravagerEntityRenderState.attackTick = (float)ravagerEntity.getAttackTick() > 0.0f ? (float)ravagerEntity.getAttackTick() - f : 0.0f;
        ravagerEntityRenderState.roarTick = ravagerEntity.getRoarTick() > 0 ? ((float)(20 - ravagerEntity.getRoarTick()) + f) / 20.0f : 0.0f;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((RavagerEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_RavagerEntityRenderState_createRenderState();
    }
}

