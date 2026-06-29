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
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VillagerEntityRenderer
extends AgeableMobEntityRenderer<VillagerEntity, VillagerEntityRenderState, VillagerResemblingModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/villager/villager.png");
    final static public HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(-0.1171875f, -0.07421875f, 1.0f);

    public VillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER)), new VillagerResemblingModel(context.getPart(EntityModelLayers.VILLAGER_BABY)), 0.5f);
        this.addFeature(new HeadFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this, context.getEntityModels(), HEAD_TRANSFORMATION));
        this.addFeature(new VillagerClothingFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this, context.getResourceManager(), "villager"));
        this.addFeature(new VillagerHeldItemFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this));
    }

    @Override
    public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    protected float getShadowRadius(VillagerEntityRenderState villagerEntityRenderState) {
        float f = super.getShadowRadius(villagerEntityRenderState);
        if (villagerEntityRenderState.baby) {
            return f * 0.5f;
        }
        return f;
    }

    @Override
    public VillagerEntityRenderState net_minecraft_client_render_entity_state_VillagerEntityRenderState_createRenderState() {
        return new VillagerEntityRenderState();
    }

    @Override
    public void updateRenderState(VillagerEntity villagerEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
        super.updateRenderState(villagerEntity, villagerEntityRenderState, f);
        ItemHolderEntityRenderState.update(villagerEntity, villagerEntityRenderState, this.itemModelResolver);
        villagerEntityRenderState.headRolling = villagerEntity.getHeadRollingTimeLeft() > 0;
        villagerEntityRenderState.villagerData = villagerEntity.getVillagerData();
    }

    @Override
    protected float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((VillagerEntityRenderState)livingEntityRenderState);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((VillagerEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_VillagerEntityRenderState_createRenderState();
    }

    @Override
    protected float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((VillagerEntityRenderState)state);
    }
}

