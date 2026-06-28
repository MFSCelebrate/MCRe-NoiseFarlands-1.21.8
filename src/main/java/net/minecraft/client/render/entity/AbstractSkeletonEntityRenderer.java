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
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSkeletonEntityRenderer<T extends AbstractSkeletonEntity, S extends SkeletonEntityRenderState>
extends BipedEntityRenderer<T, S, SkeletonEntityModel<S>> {
    public AbstractSkeletonEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer) {
        this(context, armorInnerLayer, armorOuterLayer, new SkeletonEntityModel(context.getPart(layer)));
    }

    public AbstractSkeletonEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer, SkeletonEntityModel<S> model) {
        super(context, model, 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new SkeletonEntityModel(context.getPart(armorInnerLayer)), new SkeletonEntityModel(context.getPart(armorOuterLayer)), context.getEquipmentRenderer()));
    }

    @Override
    public void updateRenderState(T abstractSkeletonEntity, S skeletonEntityRenderState, float f) {
        super.updateRenderState(abstractSkeletonEntity, skeletonEntityRenderState, f);
        ((SkeletonEntityRenderState)skeletonEntityRenderState).attacking = ((MobEntity)abstractSkeletonEntity).isAttacking();
        ((SkeletonEntityRenderState)skeletonEntityRenderState).shaking = ((AbstractSkeletonEntity)abstractSkeletonEntity).isShaking();
        ((SkeletonEntityRenderState)skeletonEntityRenderState).holdingBow = ((LivingEntity)abstractSkeletonEntity).getMainHandStack().isOf(Items.BOW);
    }

    @Override
    protected boolean isShaking(S skeletonEntityRenderState) {
        return ((SkeletonEntityRenderState)skeletonEntityRenderState).shaking;
    }

    @Override
    protected BipedEntityModel.ArmPose getArmPose(AbstractSkeletonEntity abstractSkeletonEntity, Arm arm) {
        if (abstractSkeletonEntity.getMainArm() == arm && abstractSkeletonEntity.isAttacking() && abstractSkeletonEntity.getMainHandStack().isOf(Items.BOW)) {
            return BipedEntityModel.ArmPose.BOW_AND_ARROW;
        }
        return BipedEntityModel.ArmPose.EMPTY;
    }
}

