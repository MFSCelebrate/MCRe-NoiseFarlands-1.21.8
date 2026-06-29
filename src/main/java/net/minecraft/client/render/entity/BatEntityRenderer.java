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
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BatEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BatEntityRenderer
extends MobEntityRenderer<BatEntity, BatEntityRenderState, BatEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/bat.png");

    public BatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BatEntityModel(context.getPart(EntityModelLayers.BAT)), 0.25f);
    }

    @Override
    public Identifier getTexture(BatEntityRenderState batEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public BatEntityRenderState net_minecraft_client_render_entity_state_BatEntityRenderState_createRenderState() {
        return new BatEntityRenderState();
    }

    @Override
    public void updateRenderState(BatEntity batEntity, BatEntityRenderState batEntityRenderState, float f) {
        super.updateRenderState(batEntity, batEntityRenderState, f);
        batEntityRenderState.roosting = batEntity.isRoosting();
        batEntityRenderState.flyingAnimationState.copyFrom(batEntity.flyingAnimationState);
        batEntityRenderState.roostingAnimationState.copyFrom(batEntity.roostingAnimationState);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BatEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_BatEntityRenderState_createRenderState();
    }
}

