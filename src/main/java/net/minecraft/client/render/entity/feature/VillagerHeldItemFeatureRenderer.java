/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class VillagerHeldItemFeatureRenderer<S extends ItemHolderEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    public VillagerHeldItemFeatureRenderer(FeatureRendererContext<S, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S itemHolderEntityRenderState, float f, float g) {
        ItemRenderState itemRenderState = ((ItemHolderEntityRenderState)itemHolderEntityRenderState).itemRenderState;
        if (itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        this.applyTransforms(itemHolderEntityRenderState, matrixStack);
        itemRenderState.render(matrixStack, vertexConsumerProvider, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }

    protected void applyTransforms(S state, MatrixStack matrices) {
        ((ModelWithHat)this.getContextModel()).rotateArms(matrices);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation(0.75f));
        matrices.scale(1.07f, 1.07f, 1.07f);
        matrices.translate(0.0f, 0.13f, -0.34f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation((float)Math.PI));
    }
}

