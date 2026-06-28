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
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractHoglinEntityRenderer<T extends MobEntity>
extends AgeableMobEntityRenderer<T, HoglinEntityRenderState, HoglinEntityModel> {
    public AbstractHoglinEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer, float scale) {
        super(context, new HoglinEntityModel(context.getPart(layer)), new HoglinEntityModel(context.getPart(babyLayer)), scale);
    }

    @Override
    public HoglinEntityRenderState net_minecraft_client_render_entity_state_HoglinEntityRenderState_createRenderState() {
        return new HoglinEntityRenderState();
    }

    @Override
    public void updateRenderState(T mobEntity, HoglinEntityRenderState hoglinEntityRenderState, float f) {
        super.updateRenderState(mobEntity, hoglinEntityRenderState, f);
        hoglinEntityRenderState.movementCooldownTicks = ((Hoglin)mobEntity).getMovementCooldownTicks();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_HoglinEntityRenderState_createRenderState();
    }
}

