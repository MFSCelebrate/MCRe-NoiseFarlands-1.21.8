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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.state.MooshroomEntityRenderState;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer
extends FeatureRenderer<MooshroomEntityRenderState, CowEntityModel> {
    final private BlockRenderManager blockRenderManager;

    public MooshroomMushroomFeatureRenderer(FeatureRendererContext<MooshroomEntityRenderState, CowEntityModel> context, BlockRenderManager blockRenderManager) {
        super(context);
        this.blockRenderManager = blockRenderManager;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, MooshroomEntityRenderState mooshroomEntityRenderState, float f, float g) {
        boolean bl;
        if (mooshroomEntityRenderState.baby) {
            return;
        }
        boolean bl2 = bl = mooshroomEntityRenderState.hasOutline && mooshroomEntityRenderState.invisible;
        if (mooshroomEntityRenderState.invisible && !bl) {
            return;
        }
        BlockState blockState = mooshroomEntityRenderState.type.getMushroomState();
        int j = LivingEntityRenderer.getOverlay(mooshroomEntityRenderState, 0.0f);
        BlockStateModel blockStateModel = this.blockRenderManager.getModel(blockState);
        matrixStack.push();
        matrixStack.translate(0.2f, -0.35f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-48.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroom(matrixStack, vertexConsumerProvider, 1, bl, blockState, j, blockStateModel);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.2f, -0.35f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(42.0f));
        matrixStack.translate(0.1f, 0.0f, -0.6f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-48.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroom(matrixStack, vertexConsumerProvider, 1, bl, blockState, j, blockStateModel);
        matrixStack.pop();
        matrixStack.push();
        ((CowEntityModel)this.getContextModel()).getHead().applyTransform(matrixStack);
        matrixStack.translate(0.0f, -0.7f, -0.2f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-78.0f));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        this.renderMushroom(matrixStack, vertexConsumerProvider, 1, bl, blockState, j, blockStateModel);
        matrixStack.pop();
    }

    private void renderMushroom(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean renderAsModel, BlockState mushroomState, int overlay, BlockStateModel mushroomModel) {
        if (renderAsModel) {
            BlockModelRenderer.render(matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), mushroomModel, 0.0f, 0.0f, 0.0f, light, overlay);
        } else {
            this.blockRenderManager.renderBlockAsEntity(mushroomState, matrices, vertexConsumers, light, overlay);
        }
    }
}

