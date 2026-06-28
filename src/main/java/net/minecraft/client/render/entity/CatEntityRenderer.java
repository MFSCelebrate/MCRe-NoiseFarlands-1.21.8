/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class CatEntityRenderer
extends AgeableMobEntityRenderer<CatEntity, CatEntityRenderState, CatEntityModel> {
    public CatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CatEntityModel(context.getPart(EntityModelLayers.CAT)), new CatEntityModel(context.getPart(EntityModelLayers.CAT_BABY)), 0.4f);
        this.addFeature(new CatCollarFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    public Identifier getTexture(CatEntityRenderState catEntityRenderState) {
        return catEntityRenderState.texture;
    }

    @Override
    public CatEntityRenderState net_minecraft_client_render_entity_state_CatEntityRenderState_createRenderState() {
        return new CatEntityRenderState();
    }

    @Override
    public void updateRenderState(CatEntity catEntity, CatEntityRenderState catEntityRenderState, float f) {
        super.updateRenderState(catEntity, catEntityRenderState, f);
        catEntityRenderState.texture = catEntity.getVariant().value().assetInfo().texturePath();
        catEntityRenderState.inSneakingPose = catEntity.isInSneakingPose();
        catEntityRenderState.sprinting = catEntity.isSprinting();
        catEntityRenderState.inSittingPose = catEntity.isInSittingPose();
        catEntityRenderState.sleepAnimationProgress = catEntity.getSleepAnimationProgress(f);
        catEntityRenderState.tailCurlAnimationProgress = catEntity.getTailCurlAnimationProgress(f);
        catEntityRenderState.headDownAnimationProgress = catEntity.getHeadDownAnimationProgress(f);
        catEntityRenderState.nearSleepingPlayer = catEntity.isNearSleepingPlayer();
        catEntityRenderState.collarColor = catEntity.isTamed() ? catEntity.getCollarColor() : null;
    }

    @Override
    protected void setupTransforms(CatEntityRenderState catEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(catEntityRenderState, matrixStack, f, g);
        float h = catEntityRenderState.sleepAnimationProgress;
        if (h > 0.0f) {
            matrixStack.translate(0.4f * h, 0.15f * h, 0.1f * h);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerpAngleDegrees(h, 0.0f, 90.0f)));
            if (catEntityRenderState.nearSleepingPlayer) {
                matrixStack.translate(0.15f * h, 0.0f, 0.0f);
            }
        }
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CatEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_CatEntityRenderState_createRenderState();
    }
}

