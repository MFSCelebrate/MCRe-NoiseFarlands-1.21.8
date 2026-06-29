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
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GoatEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GoatEntityRenderer
extends AgeableMobEntityRenderer<GoatEntity, GoatEntityRenderState, GoatEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/goat/goat.png");

    public GoatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new GoatEntityModel(context.getPart(EntityModelLayers.GOAT)), new GoatEntityModel(context.getPart(EntityModelLayers.GOAT_BABY)), 0.7f);
    }

    @Override
    public Identifier getTexture(GoatEntityRenderState goatEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public GoatEntityRenderState net_minecraft_client_render_entity_state_GoatEntityRenderState_createRenderState() {
        return new GoatEntityRenderState();
    }

    @Override
    public void updateRenderState(GoatEntity goatEntity, GoatEntityRenderState goatEntityRenderState, float f) {
        super.updateRenderState(goatEntity, goatEntityRenderState, f);
        goatEntityRenderState.hasLeftHorn = goatEntity.hasLeftHorn();
        goatEntityRenderState.hasRightHorn = goatEntity.hasRightHorn();
        goatEntityRenderState.headPitch = goatEntity.getHeadPitch();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GoatEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_GoatEntityRenderState_createRenderState();
    }
}

