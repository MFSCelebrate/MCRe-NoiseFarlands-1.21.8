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
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityRenderer
extends AgeableMobEntityRenderer<DolphinEntity, DolphinEntityRenderState, DolphinEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/dolphin.png");

    public DolphinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN)), new DolphinEntityModel(context.getPart(EntityModelLayers.DOLPHIN_BABY)), 0.7f);
        this.addFeature(new DolphinHeldItemFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(DolphinEntityRenderState dolphinEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public DolphinEntityRenderState net_minecraft_client_render_entity_state_DolphinEntityRenderState_createRenderState() {
        return new DolphinEntityRenderState();
    }

    @Override
    public void updateRenderState(DolphinEntity dolphinEntity, DolphinEntityRenderState dolphinEntityRenderState, float f) {
        super.updateRenderState(dolphinEntity, dolphinEntityRenderState, f);
        ItemHolderEntityRenderState.update(dolphinEntity, dolphinEntityRenderState, this.itemModelResolver);
        dolphinEntityRenderState.moving = dolphinEntity.getVelocity().horizontalLengthSquared() > 1.0E-7;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((DolphinEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_DolphinEntityRenderState_createRenderState();
    }
}

