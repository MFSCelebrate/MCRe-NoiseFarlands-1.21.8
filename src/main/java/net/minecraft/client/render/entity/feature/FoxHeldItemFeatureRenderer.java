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
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class FoxHeldItemFeatureRenderer
extends FeatureRenderer<FoxEntityRenderState, FoxEntityModel> {
    public FoxHeldItemFeatureRenderer(FeatureRendererContext<FoxEntityRenderState, FoxEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, FoxEntityRenderState foxEntityRenderState, float f, float g) {
        ItemRenderState itemRenderState = foxEntityRenderState.itemRenderState;
        if (itemRenderState.isEmpty()) {
            return;
        }
        boolean bl = foxEntityRenderState.sleeping;
        boolean bl2 = foxEntityRenderState.baby;
        matrixStack.push();
        matrixStack.translate(((FoxEntityModel)this.getContextModel()).head.originX / 16.0f, ((FoxEntityModel)this.getContextModel()).head.originY / 16.0f, ((FoxEntityModel)this.getContextModel()).head.originZ / 16.0f);
        if (bl2) {
            float h = 0.75f;
            matrixStack.scale(0.75f, 0.75f, 0.75f);
        }
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotation(foxEntityRenderState.headRoll));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(g));
        if (foxEntityRenderState.baby) {
            if (bl) {
                matrixStack.translate(0.4f, 0.26f, 0.15f);
            } else {
                matrixStack.translate(0.06f, 0.26f, -0.5f);
            }
        } else if (bl) {
            matrixStack.translate(0.46f, 0.26f, 0.22f);
        } else {
            matrixStack.translate(0.06f, 0.27f, -0.5f);
        }
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        if (bl) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
        itemRenderState.render(matrixStack, vertexConsumerProvider, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }
}

