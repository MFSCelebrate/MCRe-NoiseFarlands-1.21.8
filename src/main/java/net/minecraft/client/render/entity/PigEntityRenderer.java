/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BabyModelPair;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.ColdPigEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PigVariant;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PigEntityRenderer
extends MobEntityRenderer<PigEntity, PigEntityRenderState, PigEntityModel> {
    final private Map<PigVariant.Model, BabyModelPair<PigEntityModel>> modelPairs;

    public PigEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PigEntityModel(context.getPart(EntityModelLayers.PIG)), 0.7f);
        this.modelPairs = PigEntityRenderer.createModelPairs(context);
        this.addFeature(new SaddleFeatureRenderer<PigEntityRenderState, PigEntityModel, PigEntityModel>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.PIG_SADDLE, pigEntityRenderState -> pigEntityRenderState.saddleStack, new PigEntityModel(context.getPart(EntityModelLayers.PIG_SADDLE)), new PigEntityModel(context.getPart(EntityModelLayers.PIG_BABY_SADDLE))));
    }

    private static Map<PigVariant.Model, BabyModelPair<PigEntityModel>> createModelPairs(EntityRendererFactory.Context context) {
        return Maps.newEnumMap(Map.of(PigVariant.Model.NORMAL, new BabyModelPair<PigEntityModel>(new PigEntityModel(context.getPart(EntityModelLayers.PIG)), new PigEntityModel(context.getPart(EntityModelLayers.PIG_BABY))), PigVariant.Model.COLD, new BabyModelPair<ColdPigEntityModel>(new ColdPigEntityModel(context.getPart(EntityModelLayers.COLD_PIG)), new ColdPigEntityModel(context.getPart(EntityModelLayers.COLD_PIG_BABY)))));
    }

    @Override
    public void render(PigEntityRenderState pigEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (pigEntityRenderState.variant == null) {
            return;
        }
        this.model = this.modelPairs.get(pigEntityRenderState.variant.modelAndTexture().model()).get(pigEntityRenderState.baby);
        super.render(pigEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public Identifier getTexture(PigEntityRenderState pigEntityRenderState) {
        return pigEntityRenderState.variant == null ? MissingSprite.getMissingSpriteId() : pigEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public PigEntityRenderState net_minecraft_client_render_entity_state_PigEntityRenderState_createRenderState() {
        return new PigEntityRenderState();
    }

    @Override
    public void updateRenderState(PigEntity pigEntity, PigEntityRenderState pigEntityRenderState, float f) {
        super.updateRenderState(pigEntity, pigEntityRenderState, f);
        pigEntityRenderState.saddleStack = pigEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        pigEntityRenderState.variant = pigEntity.getVariant().value();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PigEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_PigEntityRenderState_createRenderState();
    }
}

