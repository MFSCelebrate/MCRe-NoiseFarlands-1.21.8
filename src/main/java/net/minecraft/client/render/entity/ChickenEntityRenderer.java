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
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.ColdChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityRenderer
extends MobEntityRenderer<ChickenEntity, ChickenEntityRenderState, ChickenEntityModel> {
    final private Map<ChickenVariant.Model, BabyModelPair<ChickenEntityModel>> babyModelPairMap;

    public ChickenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN)), 0.3f);
        this.babyModelPairMap = ChickenEntityRenderer.createBabyModelPairMap(context);
    }

    private static Map<ChickenVariant.Model, BabyModelPair<ChickenEntityModel>> createBabyModelPairMap(EntityRendererFactory.Context context) {
        return Maps.newEnumMap(Map.of(ChickenVariant.Model.NORMAL, new BabyModelPair<ChickenEntityModel>(new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN)), new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN_BABY))), ChickenVariant.Model.COLD, new BabyModelPair<ColdChickenEntityModel>(new ColdChickenEntityModel(context.getPart(EntityModelLayers.COLD_CHICKEN)), new ColdChickenEntityModel(context.getPart(EntityModelLayers.COLD_CHICKEN_BABY)))));
    }

    @Override
    public void render(ChickenEntityRenderState chickenEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (chickenEntityRenderState.variant == null) {
            return;
        }
        this.model = this.babyModelPairMap.get(chickenEntityRenderState.variant.modelAndTexture().model()).get(chickenEntityRenderState.baby);
        super.render(chickenEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public Identifier getTexture(ChickenEntityRenderState chickenEntityRenderState) {
        return chickenEntityRenderState.variant == null ? MissingSprite.getMissingSpriteId() : chickenEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public ChickenEntityRenderState net_minecraft_client_render_entity_state_ChickenEntityRenderState_createRenderState() {
        return new ChickenEntityRenderState();
    }

    @Override
    public void updateRenderState(ChickenEntity chickenEntity, ChickenEntityRenderState chickenEntityRenderState, float f) {
        super.updateRenderState(chickenEntity, chickenEntityRenderState, f);
        chickenEntityRenderState.flapProgress = MathHelper.lerp(f, chickenEntity.lastFlapProgress, chickenEntity.flapProgress);
        chickenEntityRenderState.maxWingDeviation = MathHelper.lerp(f, chickenEntity.lastMaxWingDeviation, chickenEntity.maxWingDeviation);
        chickenEntityRenderState.variant = chickenEntity.getVariant().value();
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ChickenEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ChickenEntityRenderState_createRenderState();
    }
}

