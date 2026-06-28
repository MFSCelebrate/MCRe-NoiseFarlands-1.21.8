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

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class DecoratedPotBlockEntityRenderer
implements BlockEntityRenderer<DecoratedPotBlockEntity> {
    final static private String NECK = "neck";
    final static private String FRONT = "front";
    final static private String BACK = "back";
    final static private String LEFT = "left";
    final static private String RIGHT = "right";
    final static private String TOP = "top";
    final static private String BOTTOM = "bottom";
    final private ModelPart neck;
    final private ModelPart front;
    final private ModelPart back;
    final private ModelPart left;
    final private ModelPart right;
    final private ModelPart top;
    final private ModelPart bottom;
    final static private float field_46728 = 0.125f;

    public DecoratedPotBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this(context.getLoadedEntityModels());
    }

    public DecoratedPotBlockEntityRenderer(LoadedEntityModels models) {
        ModelPart modelPart = models.getModelPart(EntityModelLayers.DECORATED_POT_BASE);
        this.neck = modelPart.getChild(EntityModelPartNames.NECK);
        this.top = modelPart.getChild(TOP);
        this.bottom = modelPart.getChild(EntityModelPartNames.BOTTOM);
        ModelPart modelPart2 = models.getModelPart(EntityModelLayers.DECORATED_POT_SIDES);
        this.front = modelPart2.getChild(FRONT);
        this.back = modelPart2.getChild(BACK);
        this.left = modelPart2.getChild(LEFT);
        this.right = modelPart2.getChild(RIGHT);
    }

    public static TexturedModelData getTopBottomNeckTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(0.2f);
        Dilation dilation2 = new Dilation(-0.1f);
        modelPartData.addChild(EntityModelPartNames.NECK, ModelPartBuilder.create().uv(0, 0).cuboid(4.0f, 17.0f, 4.0f, 8.0f, 3.0f, 8.0f, dilation2).uv(0, 5).cuboid(5.0f, 20.0f, 5.0f, 6.0f, 1.0f, 6.0f, dilation), ModelTransform.of(0.0f, 37.0f, 16.0f, (float)Math.PI, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(-14, 13).cuboid(0.0f, 0.0f, 0.0f, 14.0f, 0.0f, 14.0f);
        modelPartData.addChild(TOP, modelPartBuilder, ModelTransform.of(1.0f, 16.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.BOTTOM, modelPartBuilder, ModelTransform.of(1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public static TexturedModelData getSidesTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(1, 0).cuboid(0.0f, 0.0f, 0.0f, 14.0f, 16.0f, 0.0f, EnumSet.of(Direction.NORTH));
        modelPartData.addChild(BACK, modelPartBuilder, ModelTransform.of(15.0f, 16.0f, 1.0f, 0.0f, 0.0f, (float)Math.PI));
        modelPartData.addChild(LEFT, modelPartBuilder, ModelTransform.of(1.0f, 16.0f, 1.0f, 0.0f, -1.5707964f, (float)Math.PI));
        modelPartData.addChild(RIGHT, modelPartBuilder, ModelTransform.of(15.0f, 16.0f, 15.0f, 0.0f, 1.5707964f, (float)Math.PI));
        modelPartData.addChild(FRONT, modelPartBuilder, ModelTransform.of(1.0f, 16.0f, 15.0f, (float)Math.PI, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 16, 16);
    }

    private static SpriteIdentifier getTextureIdFromSherd(Optional<Item> sherd) {
        SpriteIdentifier spriteIdentifier;
        if (sherd.isPresent() && (spriteIdentifier = TexturedRenderLayers.getDecoratedPotPatternTextureId(DecoratedPotPatterns.fromSherd(sherd.get()))) != null) {
            return spriteIdentifier;
        }
        return TexturedRenderLayers.DECORATED_POT_SIDE;
    }

    @Override
    public void render(DecoratedPotBlockEntity decoratedPotBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        float g;
        matrixStack.push();
        Direction direction = decoratedPotBlockEntity.getHorizontalFacing();
        matrixStack.translate(0.5, 0.0, 0.5);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - direction.getPositiveHorizontalDegrees()));
        matrixStack.translate(-0.5, 0.0, -0.5);
        DecoratedPotBlockEntity.WobbleType wobbleType = decoratedPotBlockEntity.lastWobbleType;
        if (wobbleType != null && decoratedPotBlockEntity.getWorld() != null && (g = ((float)(decoratedPotBlockEntity.getWorld().getTime() - decoratedPotBlockEntity.lastWobbleTime) + f) / (float)wobbleType.lengthInTicks) >= 0.0f && g <= 1.0f) {
            if (wobbleType == DecoratedPotBlockEntity.WobbleType.POSITIVE) {
                float h = 0.015625f;
                float k = g * ((float)Math.PI * 2);
                float l = -1.5f * (MathHelper.cos(k) + 0.5f) * MathHelper.sin(k / 2.0f);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation(l * 0.015625f), 0.5f, 0.0f, 0.5f);
                float m = MathHelper.sin(k);
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotation(m * 0.015625f), 0.5f, 0.0f, 0.5f);
            } else {
                float h = MathHelper.sin(-g * 3.0f * (float)Math.PI) * 0.125f;
                float k = 1.0f - g;
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(h * k), 0.5f, 0.0f, 0.5f);
            }
        }
        this.render(matrixStack, vertexConsumerProvider, i, j, decoratedPotBlockEntity.getSherds());
        matrixStack.pop();
    }

    public void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Sherds sherds) {
        this.render(matrices, vertexConsumers, light, overlay, sherds);
    }

    private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Sherds sherds) {
        VertexConsumer vertexConsumer = TexturedRenderLayers.DECORATED_POT_BASE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        this.neck.render(matrices, vertexConsumer, light, overlay);
        this.top.render(matrices, vertexConsumer, light, overlay);
        this.bottom.render(matrices, vertexConsumer, light, overlay);
        this.renderDecoratedSide(this.front, matrices, vertexConsumers, light, overlay, DecoratedPotBlockEntityRenderer.getTextureIdFromSherd(sherds.front()));
        this.renderDecoratedSide(this.back, matrices, vertexConsumers, light, overlay, DecoratedPotBlockEntityRenderer.getTextureIdFromSherd(sherds.back()));
        this.renderDecoratedSide(this.left, matrices, vertexConsumers, light, overlay, DecoratedPotBlockEntityRenderer.getTextureIdFromSherd(sherds.left()));
        this.renderDecoratedSide(this.right, matrices, vertexConsumers, light, overlay, DecoratedPotBlockEntityRenderer.getTextureIdFromSherd(sherds.right()));
    }

    private void renderDecoratedSide(ModelPart part, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, SpriteIdentifier textureId) {
        part.render(matrices, textureId.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid), light, overlay);
    }

    public void collectVertices(Set<Vector3f> vertices) {
        MatrixStack matrixStack = new MatrixStack();
        this.neck.collectVertices(matrixStack, vertices);
        this.top.collectVertices(matrixStack, vertices);
        this.bottom.collectVertices(matrixStack, vertices);
    }
}

