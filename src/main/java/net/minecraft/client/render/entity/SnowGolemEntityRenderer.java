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
import net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SnowGolemEntityRenderState;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityRenderer
extends MobEntityRenderer<SnowGolemEntity, SnowGolemEntityRenderState, SnowGolemEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/snow_golem.png");

    public SnowGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SnowGolemEntityModel(context.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5f);
        this.addFeature(new SnowGolemPumpkinFeatureRenderer(this, context.getBlockRenderManager()));
    }

    @Override
    public Identifier getTexture(SnowGolemEntityRenderState snowGolemEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SnowGolemEntityRenderState net_minecraft_client_render_entity_state_SnowGolemEntityRenderState_createRenderState() {
        return new SnowGolemEntityRenderState();
    }

    @Override
    public void updateRenderState(SnowGolemEntity snowGolemEntity, SnowGolemEntityRenderState snowGolemEntityRenderState, float f) {
        super.updateRenderState(snowGolemEntity, snowGolemEntityRenderState, f);
        snowGolemEntityRenderState.hasPumpkin = snowGolemEntity.hasPumpkin();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SnowGolemEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_SnowGolemEntityRenderState_createRenderState();
    }
}

