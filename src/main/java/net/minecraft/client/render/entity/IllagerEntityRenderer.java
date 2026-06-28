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
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;

@Environment(value=EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity, S extends IllagerEntityRenderState>
extends MobEntityRenderer<T, S, IllagerEntityModel<S>> {
    protected IllagerEntityRenderer(EntityRendererFactory.Context ctx, IllagerEntityModel<S> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addFeature(new HeadFeatureRenderer(this, ctx.getEntityModels()));
    }

    @Override
    public void updateRenderState(T illagerEntity, S illagerEntityRenderState, float f) {
        super.updateRenderState(illagerEntity, illagerEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(illagerEntity, illagerEntityRenderState, this.itemModelResolver);
        ((IllagerEntityRenderState)illagerEntityRenderState).hasVehicle = ((Entity)illagerEntity).hasVehicle();
        ((IllagerEntityRenderState)illagerEntityRenderState).illagerMainArm = ((MobEntity)illagerEntity).getMainArm();
        ((IllagerEntityRenderState)illagerEntityRenderState).illagerState = ((IllagerEntity)illagerEntity).getState();
        ((IllagerEntityRenderState)illagerEntityRenderState).crossbowPullTime = ((IllagerEntityRenderState)illagerEntityRenderState).illagerState == IllagerEntity.State.CROSSBOW_CHARGE ? CrossbowItem.getPullTime(((LivingEntity)illagerEntity).getActiveItem(), illagerEntity) : 0;
        ((IllagerEntityRenderState)illagerEntityRenderState).itemUseTime = ((LivingEntity)illagerEntity).getItemUseTime();
        ((IllagerEntityRenderState)illagerEntityRenderState).handSwingProgress = ((LivingEntity)illagerEntity).getHandSwingProgress(f);
        ((IllagerEntityRenderState)illagerEntityRenderState).attacking = ((MobEntity)illagerEntity).isAttacking();
    }
}

