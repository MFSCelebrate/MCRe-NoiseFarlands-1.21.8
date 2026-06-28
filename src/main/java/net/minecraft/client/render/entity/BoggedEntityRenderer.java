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
import net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.BoggedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.BoggedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BoggedEntityRenderer
extends AbstractSkeletonEntityRenderer<BoggedEntity, BoggedEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/bogged.png");
    final static private Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/bogged_overlay.png");

    public BoggedEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.BOGGED_INNER_ARMOR, EntityModelLayers.BOGGED_OUTER_ARMOR, new BoggedEntityModel(context.getPart(EntityModelLayers.BOGGED)));
        this.addFeature(new SkeletonOverlayFeatureRenderer<BoggedEntityRenderState, SkeletonEntityModel<BoggedEntityRenderState>>(this, context.getEntityModels(), EntityModelLayers.BOGGED_OUTER, OVERLAY_TEXTURE));
    }

    @Override
    public Identifier getTexture(BoggedEntityRenderState boggedEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public BoggedEntityRenderState net_minecraft_client_render_entity_state_BoggedEntityRenderState_createRenderState() {
        return new BoggedEntityRenderState();
    }

    @Override
    public void updateRenderState(BoggedEntity boggedEntity, BoggedEntityRenderState boggedEntityRenderState, float f) {
        super.updateRenderState(boggedEntity, boggedEntityRenderState, f);
        boggedEntityRenderState.sheared = boggedEntity.isSheared();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BoggedEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_BoggedEntityRenderState_createRenderState();
    }
}

