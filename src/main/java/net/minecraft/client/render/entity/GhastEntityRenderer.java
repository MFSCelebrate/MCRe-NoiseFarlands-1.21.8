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
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GhastEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GhastEntityRenderer
extends MobEntityRenderer<GhastEntity, GhastEntityRenderState, GhastEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/ghast/ghast.png");
    final static private Identifier SHOOTING_TEXTURE = Identifier.ofVanilla("textures/entity/ghast/ghast_shooting.png");

    public GhastEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new GhastEntityModel(context.getPart(EntityModelLayers.GHAST)), 1.5f);
    }

    @Override
    public Identifier getTexture(GhastEntityRenderState ghastEntityRenderState) {
        if (ghastEntityRenderState.shooting) {
            return SHOOTING_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    public GhastEntityRenderState net_minecraft_client_render_entity_state_GhastEntityRenderState_createRenderState() {
        return new GhastEntityRenderState();
    }

    @Override
    public void updateRenderState(GhastEntity ghastEntity, GhastEntityRenderState ghastEntityRenderState, float f) {
        super.updateRenderState(ghastEntity, ghastEntityRenderState, f);
        ghastEntityRenderState.shooting = ghastEntity.isShooting();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GhastEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_GhastEntityRenderState_createRenderState();
    }
}

