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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StrayEntityRenderer
extends AbstractSkeletonEntityRenderer<StrayEntity, SkeletonEntityRenderState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/stray.png");
    final static private Identifier OVERLAY_TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/stray_overlay.png");

    public StrayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.STRAY, EntityModelLayers.STRAY_INNER_ARMOR, EntityModelLayers.STRAY_OUTER_ARMOR);
        this.addFeature(new SkeletonOverlayFeatureRenderer<SkeletonEntityRenderState, SkeletonEntityModel<SkeletonEntityRenderState>>(this, context.getEntityModels(), EntityModelLayers.STRAY_OUTER, OVERLAY_TEXTURE));
    }

    @Override
    public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SkeletonEntityRenderState net_minecraft_client_render_entity_state_SkeletonEntityRenderState_createRenderState() {
        return new SkeletonEntityRenderState();
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_SkeletonEntityRenderState_createRenderState();
    }
}

