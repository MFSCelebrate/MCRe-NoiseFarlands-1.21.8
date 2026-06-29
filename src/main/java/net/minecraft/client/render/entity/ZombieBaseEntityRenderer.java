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
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, S extends ZombieEntityRenderState, M extends ZombieEntityModel<S>>
extends BipedEntityRenderer<T, S, M> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

    protected ZombieBaseEntityRenderer(EntityRendererFactory.Context context, M mainModel, M babyMainModel, M armorInnerModel, M armorOuterModel, M babyArmorInnerModel, M babyArmorOuterModel) {
        super(context, mainModel, babyMainModel, 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, armorInnerModel, armorOuterModel, babyArmorInnerModel, babyArmorOuterModel, context.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(S zombieEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public void updateRenderState(T zombieEntity, S zombieEntityRenderState, float f) {
        super.updateRenderState(zombieEntity, zombieEntityRenderState, f);
        ((ZombieEntityRenderState)zombieEntityRenderState).attacking = ((MobEntity)zombieEntity).isAttacking();
        ((ZombieEntityRenderState)zombieEntityRenderState).convertingInWater = ((ZombieEntity)zombieEntity).isConvertingInWater();
    }

    @Override
    protected boolean isShaking(S zombieEntityRenderState) {
        return super.isShaking(zombieEntityRenderState) || ((ZombieEntityRenderState)zombieEntityRenderState).convertingInWater;
    }

    @Override
    protected boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((S)((ZombieEntityRenderState)state));
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((S)((ZombieEntityRenderState)state));
    }
}

