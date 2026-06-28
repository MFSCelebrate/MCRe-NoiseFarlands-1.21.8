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
import net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderer
extends AgeableMobEntityRenderer<WolfEntity, WolfEntityRenderState, WolfEntityModel> {
    public WolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WolfEntityModel(context.getPart(EntityModelLayers.WOLF)), new WolfEntityModel(context.getPart(EntityModelLayers.WOLF_BABY)), 0.5f);
        this.addFeature(new WolfArmorFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature(new WolfCollarFeatureRenderer(this));
    }

    @Override
    protected int getMixColor(WolfEntityRenderState wolfEntityRenderState) {
        float f = wolfEntityRenderState.furWetBrightnessMultiplier;
        if (f == 1.0f) {
            return -1;
        }
        return ColorHelper.fromFloats(1.0f, f, f, f);
    }

    @Override
    public Identifier getTexture(WolfEntityRenderState wolfEntityRenderState) {
        return wolfEntityRenderState.texture;
    }

    @Override
    public WolfEntityRenderState net_minecraft_client_render_entity_state_WolfEntityRenderState_createRenderState() {
        return new WolfEntityRenderState();
    }

    @Override
    public void updateRenderState(WolfEntity wolfEntity, WolfEntityRenderState wolfEntityRenderState, float f) {
        super.updateRenderState(wolfEntity, wolfEntityRenderState, f);
        wolfEntityRenderState.angerTime = wolfEntity.hasAngerTime();
        wolfEntityRenderState.inSittingPose = wolfEntity.isInSittingPose();
        wolfEntityRenderState.tailAngle = wolfEntity.getTailAngle();
        wolfEntityRenderState.begAnimationProgress = wolfEntity.getBegAnimationProgress(f);
        wolfEntityRenderState.shakeProgress = wolfEntity.getShakeProgress(f);
        wolfEntityRenderState.texture = wolfEntity.getTextureId();
        wolfEntityRenderState.furWetBrightnessMultiplier = wolfEntity.getFurWetBrightnessMultiplier(f);
        wolfEntityRenderState.collarColor = wolfEntity.isTamed() ? wolfEntity.getCollarColor() : null;
        wolfEntityRenderState.bodyArmor = wolfEntity.getBodyArmor().copy();
    }

    @Override
    protected int getMixColor(LivingEntityRenderState state) {
        return this.getMixColor((WolfEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_WolfEntityRenderState_createRenderState();
    }
}

