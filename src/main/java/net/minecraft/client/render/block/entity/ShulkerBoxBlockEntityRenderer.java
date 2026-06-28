/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.block.entity;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderer
implements BlockEntityRenderer<ShulkerBoxBlockEntity> {
    final private ShulkerBoxBlockModel model;

    public ShulkerBoxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this(ctx.getLoadedEntityModels());
    }

    public ShulkerBoxBlockEntityRenderer(LoadedEntityModels models) {
        this.model = new ShulkerBoxBlockModel(models.getModelPart(EntityModelLayers.SHULKER_BOX));
    }

    @Override
    public void render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        Direction direction = shulkerBoxBlockEntity.getCachedState().get(ShulkerBoxBlock.FACING, Direction.UP);
        DyeColor dyeColor = shulkerBoxBlockEntity.getColor();
        SpriteIdentifier spriteIdentifier = dyeColor == null ? TexturedRenderLayers.SHULKER_TEXTURE_ID : TexturedRenderLayers.getShulkerBoxTextureId(dyeColor);
        float g = shulkerBoxBlockEntity.getAnimationProgress(f);
        this.render(matrixStack, vertexConsumerProvider, i, j, direction, g, spriteIdentifier);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, float openness, SpriteIdentifier textureId) {
        matrices.push();
        this.setTransforms(matrices, facing, openness);
        VertexConsumer vertexConsumer = textureId.getVertexConsumer(vertexConsumers, this.model::getLayer);
        this.model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }

    private void setTransforms(MatrixStack matrices, Direction facing, float openness) {
        matrices.translate(0.5f, 0.5f, 0.5f);
        float f = 0.9995f;
        matrices.scale(0.9995f, 0.9995f, 0.9995f);
        matrices.multiply((Quaternionfc)facing.getRotationQuaternion());
        matrices.scale(1.0f, -1.0f, -1.0f);
        matrices.translate(0.0f, -1.0f, 0.0f);
        this.model.animateLid(openness);
    }

    public void collectVertices(Direction facing, float openness, Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        this.setTransforms(matrixStack, facing, openness);
        this.model.getRootPart().collectVertices(matrixStack, vertices);
    }

    @Environment(value=EnvType.CLIENT)
    static class ShulkerBoxBlockModel
    extends Model {
        final private ModelPart lid;

        public ShulkerBoxBlockModel(ModelPart root) {
            super(root, RenderLayer::getEntityCutoutNoCull);
            this.lid = root.getChild("lid");
        }

        public void animateLid(float openness) {
            this.lid.setOrigin(0.0f, 24.0f - openness * 0.5f * 16.0f, 0.0f);
            this.lid.yaw = 270.0f * openness * ((float)Math.PI / 180);
        }
    }
}

