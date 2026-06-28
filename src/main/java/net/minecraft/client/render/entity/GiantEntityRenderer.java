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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GiantEntityRenderer
extends MobEntityRenderer<GiantEntity, ZombieEntityRenderState, BipedEntityModel<ZombieEntityRenderState>> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

    public GiantEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
        super(ctx, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT)), 0.5f * scale);
        this.addFeature(new HeldItemFeatureRenderer<ZombieEntityRenderState, BipedEntityModel<ZombieEntityRenderState>>(this));
        this.addFeature(new ArmorFeatureRenderer<ZombieEntityRenderState, BipedEntityModel<ZombieEntityRenderState>, GiantEntityModel>(this, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_INNER_ARMOR)), new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_OUTER_ARMOR)), ctx.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public ZombieEntityRenderState net_minecraft_client_render_entity_state_ZombieEntityRenderState_createRenderState() {
        return new ZombieEntityRenderState();
    }

    @Override
    public void updateRenderState(GiantEntity giantEntity, ZombieEntityRenderState zombieEntityRenderState, float f) {
        super.updateRenderState(giantEntity, zombieEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState(giantEntity, zombieEntityRenderState, f, this.itemModelResolver);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ZombieEntityRenderState_createRenderState();
    }
}

