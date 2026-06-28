/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderer
extends AbstractSignBlockEntityRenderer {
    final static public float SCALE = 0.6666667f;
    final static private Vec3d TEXT_OFFSET = new Vec3d(0.0, 0.3333333432674408, 0.046666666865348816);
    final private Map<WoodType, SignModelPair> typeToModelPair = (Map)WoodType.stream().collect(ImmutableMap.toImmutableMap(signType -> signType, signType -> new SignModelPair(SignBlockEntityRenderer.createSignModel(ctx.getLoadedEntityModels(), signType, true), SignBlockEntityRenderer.createSignModel(ctx.getLoadedEntityModels(), signType, false))));

    public SignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected Model getModel(BlockState state, WoodType woodType) {
        SignModelPair signModelPair = this.typeToModelPair.get(woodType);
        return state.getBlock() instanceof SignBlock ? signModelPair.standing() : signModelPair.wall();
    }

    @Override
    protected SpriteIdentifier getTextureId(WoodType woodType) {
        return TexturedRenderLayers.getSignTextureId(woodType);
    }

    @Override
    protected float getSignScale() {
        return 0.6666667f;
    }

    @Override
    protected float getTextScale() {
        return 0.6666667f;
    }

    private static void setAngles(MatrixStack matrices, float blockRotationDegrees) {
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(blockRotationDegrees));
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state) {
        SignBlockEntityRenderer.setAngles(matrices, blockRotationDegrees);
        if (!(state.getBlock() instanceof SignBlock)) {
            matrices.translate(0.0f, -0.3125f, -0.4375f);
        }
    }

    @Override
    protected Vec3d getTextOffset() {
        return TEXT_OFFSET;
    }

    public static void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Model model, SpriteIdentifier texture) {
        matrices.push();
        SignBlockEntityRenderer.setTransformsForItem(matrices);
        VertexConsumer vertexConsumer = texture.getVertexConsumer(vertexConsumers, model::getLayer);
        model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }

    public static void setTransformsForItem(MatrixStack matrices) {
        SignBlockEntityRenderer.setAngles(matrices, 0.0f);
        matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
    }

    public static Model createSignModel(LoadedEntityModels models, WoodType type, boolean standing) {
        EntityModelLayer entityModelLayer = standing ? EntityModelLayers.createStandingSign(type) : EntityModelLayers.createWallSign(type);
        return new Model.SinglePartModel(models.getModelPart(entityModelLayer), RenderLayer::getEntityCutoutNoCull);
    }

    public static TexturedModelData getTexturedModelData(boolean standing) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("sign", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f), ModelTransform.NONE);
        if (standing) {
            modelPartData.addChild("stick", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f), ModelTransform.NONE);
        }
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Environment(value=EnvType.CLIENT)
    record SignModelPair(Model standing, Model wall) {
    }
}

