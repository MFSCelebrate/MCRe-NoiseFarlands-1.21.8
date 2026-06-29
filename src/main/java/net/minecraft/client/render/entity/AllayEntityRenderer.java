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
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AllayEntityRenderState;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class AllayEntityRenderer
extends MobEntityRenderer<AllayEntity, AllayEntityRenderState, AllayEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/allay/allay.png");

    public AllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AllayEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4f);
        this.addFeature(new HeldItemFeatureRenderer<AllayEntityRenderState, AllayEntityModel>(this));
    }

    @Override
    public Identifier getTexture(AllayEntityRenderState allayEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public AllayEntityRenderState net_minecraft_client_render_entity_state_AllayEntityRenderState_createRenderState() {
        return new AllayEntityRenderState();
    }

    @Override
    public void updateRenderState(AllayEntity allayEntity, AllayEntityRenderState allayEntityRenderState, float f) {
        super.updateRenderState(allayEntity, allayEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(allayEntity, allayEntityRenderState, this.itemModelResolver);
        allayEntityRenderState.dancing = allayEntity.isDancing();
        allayEntityRenderState.spinning = allayEntity.isSpinning();
        allayEntityRenderState.spinningAnimationTicks = allayEntity.getSpinningAnimationTicks(f);
        allayEntityRenderState.itemHoldAnimationTicks = allayEntity.getItemHoldAnimationTicks(f);
    }

    @Override
    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((AllayEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_AllayEntityRenderState_createRenderState();
    }
}

