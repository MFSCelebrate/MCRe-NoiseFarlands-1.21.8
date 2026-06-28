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
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WanderingTraderEntityRenderer
extends MobEntityRenderer<WanderingTraderEntity, VillagerEntityRenderState, VillagerResemblingModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wandering_trader.png");

    public WanderingTraderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.WANDERING_TRADER)), 0.5f);
        this.addFeature(new HeadFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this, context.getEntityModels()));
        this.addFeature(new VillagerHeldItemFeatureRenderer<VillagerEntityRenderState, VillagerResemblingModel>(this));
    }

    @Override
    public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public VillagerEntityRenderState net_minecraft_client_render_entity_state_VillagerEntityRenderState_createRenderState() {
        return new VillagerEntityRenderState();
    }

    @Override
    public void updateRenderState(WanderingTraderEntity wanderingTraderEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
        super.updateRenderState(wanderingTraderEntity, villagerEntityRenderState, f);
        ItemHolderEntityRenderState.update(wanderingTraderEntity, villagerEntityRenderState, this.itemModelResolver);
        villagerEntityRenderState.headRolling = wanderingTraderEntity.getHeadRollingTimeLeft() > 0;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((VillagerEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_VillagerEntityRenderState_createRenderState();
    }
}

