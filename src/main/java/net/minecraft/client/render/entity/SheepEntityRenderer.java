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
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolUndercoatFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SheepEntityRenderer
extends AgeableMobEntityRenderer<SheepEntity, SheepEntityRenderState, SheepEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sheep/sheep.png");

    public SheepEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SheepEntityModel(context.getPart(EntityModelLayers.SHEEP)), new SheepEntityModel(context.getPart(EntityModelLayers.SHEEP_BABY)), 0.7f);
        this.addFeature(new SheepWoolUndercoatFeatureRenderer(this, context.getEntityModels()));
        this.addFeature(new SheepWoolFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    public Identifier getTexture(SheepEntityRenderState sheepEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SheepEntityRenderState net_minecraft_client_render_entity_state_SheepEntityRenderState_createRenderState() {
        return new SheepEntityRenderState();
    }

    @Override
    public void updateRenderState(SheepEntity sheepEntity, SheepEntityRenderState sheepEntityRenderState, float f) {
        super.updateRenderState(sheepEntity, sheepEntityRenderState, f);
        sheepEntityRenderState.headAngle = sheepEntity.getHeadAngle(f);
        sheepEntityRenderState.neckAngle = sheepEntity.getNeckAngle(f);
        sheepEntityRenderState.sheared = sheepEntity.isSheared();
        sheepEntityRenderState.color = sheepEntity.getColor();
        sheepEntityRenderState.id = sheepEntity.getId();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SheepEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_SheepEntityRenderState_createRenderState();
    }
}

