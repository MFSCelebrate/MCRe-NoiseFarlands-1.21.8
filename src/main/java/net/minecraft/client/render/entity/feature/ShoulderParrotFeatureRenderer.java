/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;

@Environment(value=EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer
extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    final private ParrotEntityModel model;
    final private ParrotEntityRenderState parrotState = new ParrotEntityRenderState();

    public ShoulderParrotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new ParrotEntityModel(loader.getModelPart(EntityModelLayers.PARROT));
        this.parrotState.parrotPose = ParrotEntityModel.Pose.ON_SHOULDER;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        ParrotEntity.Variant variant2;
        ParrotEntity.Variant variant = playerEntityRenderState.leftShoulderParrotVariant;
        if (variant != null) {
            this.render(matrixStack, vertexConsumerProvider, 1, playerEntityRenderState, variant, f, g, true);
        }
        if ((variant2 = playerEntityRenderState.rightShoulderParrotVariant) != null) {
            this.render(matrixStack, vertexConsumerProvider, 1, playerEntityRenderState, variant2, f, g, false);
        }
    }

    private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, ParrotEntity.Variant parrotVariant, float headYaw, float headPitch, boolean left) {
        matrices.push();
        matrices.translate(left ? 0.4f : -0.4f, state.isInSneakingPose ? -1.3f : -1.5f, 0.0f);
        this.parrotState.age = state.age;
        this.parrotState.limbSwingAnimationProgress = state.limbSwingAnimationProgress;
        this.parrotState.limbSwingAmplitude = state.limbSwingAmplitude;
        this.parrotState.relativeHeadYaw = headYaw;
        this.parrotState.pitch = headPitch;
        this.model.setAngles(this.parrotState);
        this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(ParrotEntityRenderer.getTexture(parrotVariant))), light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}

