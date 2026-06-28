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
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PlayerHeldItemFeatureRenderer<S extends PlayerEntityRenderState, M extends EntityModel<S> & ModelWithHead>
extends HeldItemFeatureRenderer<S, M> {
    final static private float HEAD_YAW = -0.5235988f;
    final static private float HEAD_ROLL = 1.5707964f;

    public PlayerHeldItemFeatureRenderer(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    protected void renderItem(S playerEntityRenderState, ItemRenderState itemRenderState, Arm arm, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Hand hand;
        if (itemRenderState.isEmpty()) {
            return;
        }
        Hand hand2 = hand = arm == ((PlayerEntityRenderState)playerEntityRenderState).mainArm ? Hand.MAIN_HAND : Hand.OFF_HAND;
        if (((PlayerEntityRenderState)playerEntityRenderState).isUsingItem && ((PlayerEntityRenderState)playerEntityRenderState).activeHand == hand && ((PlayerEntityRenderState)playerEntityRenderState).handSwingProgress < 1.0E-5f && !((PlayerEntityRenderState)playerEntityRenderState).spyglassState.isEmpty()) {
            this.renderSpyglass(((PlayerEntityRenderState)playerEntityRenderState).spyglassState, arm, matrixStack, vertexConsumerProvider, i);
        } else {
            super.renderItem(playerEntityRenderState, itemRenderState, arm, matrixStack, vertexConsumerProvider, i);
        }
    }

    private void renderSpyglass(ItemRenderState spyglassState, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        ((Model)this.getContextModel()).getRootPart().applyTransform(matrices);
        ModelPart modelPart = ((ModelWithHead)this.getContextModel()).getHead();
        float f = modelPart.pitch;
        modelPart.pitch = MathHelper.clamp(modelPart.pitch, -0.5235988f, 1.5707964f);
        modelPart.applyTransform(matrices);
        modelPart.pitch = f;
        HeadFeatureRenderer.translate(matrices, HeadFeatureRenderer.HeadTransformation.DEFAULT);
        boolean bl = arm == Arm.LEFT;
        matrices.translate((bl ? -2.5f : 2.5f) / 16.0f, -0.0625f, 0.0f);
        spyglassState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}

