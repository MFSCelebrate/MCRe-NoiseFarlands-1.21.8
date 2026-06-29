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
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.HappyGhastRopesFeatureRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HappyGhastEntityModel;
import net.minecraft.client.render.entity.model.HappyGhastHarnessEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HappyGhastEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public class HappyGhastEntityRenderer
extends AgeableMobEntityRenderer<HappyGhastEntity, HappyGhastEntityRenderState, HappyGhastEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/ghast/happy_ghast.png");
    final static private Identifier BABY_TEXTURE = Identifier.ofVanilla("textures/entity/ghast/happy_ghast_baby.png");
    final static private Identifier ROPES_TEXTURE = Identifier.ofVanilla("textures/entity/ghast/happy_ghast_ropes.png");

    public HappyGhastEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new HappyGhastEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST)), new HappyGhastEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST_BABY)), 2.0f);
        this.addFeature(new SaddleFeatureRenderer<HappyGhastEntityRenderState, HappyGhastEntityModel, HappyGhastHarnessEntityModel>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.HAPPY_GHAST_BODY, state -> state.harnessStack, new HappyGhastHarnessEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST_HARNESS)), new HappyGhastHarnessEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST_BABY_HARNESS))));
        this.addFeature(new HappyGhastRopesFeatureRenderer<HappyGhastEntityModel>(this, context.getEntityModels(), ROPES_TEXTURE));
    }

    @Override
    public Identifier getTexture(HappyGhastEntityRenderState happyGhastEntityRenderState) {
        if (happyGhastEntityRenderState.baby) {
            return BABY_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    public HappyGhastEntityRenderState net_minecraft_client_render_entity_state_HappyGhastEntityRenderState_createRenderState() {
        return new HappyGhastEntityRenderState();
    }

    @Override
    protected Box getBoundingBox(HappyGhastEntity happyGhastEntity) {
        Box box = super.getBoundingBox(happyGhastEntity);
        float f = happyGhastEntity.getHeight();
        return box.withMinY(box.minY - (double)(f / 2.0f));
    }

    @Override
    public void updateRenderState(HappyGhastEntity happyGhastEntity, HappyGhastEntityRenderState happyGhastEntityRenderState, float f) {
        super.updateRenderState(happyGhastEntity, happyGhastEntityRenderState, f);
        happyGhastEntityRenderState.harnessStack = happyGhastEntity.getEquippedStack(EquipmentSlot.BODY).copy();
        happyGhastEntityRenderState.hasPassengers = happyGhastEntity.hasPassengers();
        happyGhastEntityRenderState.hasRopes = happyGhastEntity.hasRopes();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HappyGhastEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_HappyGhastEntityRenderState_createRenderState();
    }
}

