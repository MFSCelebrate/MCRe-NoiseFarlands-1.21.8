/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderer
implements BlockEntityRenderer<ConduitBlockEntity> {
    final static public SpriteMapper SPRITE_MAPPER = new SpriteMapper(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, "entity/conduit");
    final static public SpriteIdentifier BASE_TEXTURE = SPRITE_MAPPER.mapVanilla("base");
    final static public SpriteIdentifier CAGE_TEXTURE = SPRITE_MAPPER.mapVanilla("cage");
    final static public SpriteIdentifier WIND_TEXTURE = SPRITE_MAPPER.mapVanilla("wind");
    final static public SpriteIdentifier WIND_VERTICAL_TEXTURE = SPRITE_MAPPER.mapVanilla("wind_vertical");
    final static public SpriteIdentifier OPEN_EYE_TEXTURE = SPRITE_MAPPER.mapVanilla("open_eye");
    final static public SpriteIdentifier CLOSED_EYE_TEXTURE = SPRITE_MAPPER.mapVanilla("closed_eye");
    final private ModelPart conduitEye;
    final private ModelPart conduitWind;
    final private ModelPart conduitShell;
    final private ModelPart conduit;
    final private BlockEntityRenderDispatcher dispatcher;

    public ConduitBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.dispatcher = ctx.getRenderDispatcher();
        this.conduitEye = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_EYE);
        this.conduitWind = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_WIND);
        this.conduitShell = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_SHELL);
        this.conduit = ctx.getLayerModelPart(EntityModelLayers.CONDUIT);
    }

    public static TexturedModelData getEyeTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("eye", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, new Dilation(0.01f)), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 16, 16);
    }

    public static TexturedModelData getWindTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("wind", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public static TexturedModelData getShellTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 16);
    }

    public static TexturedModelData getPlainTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 16);
    }

    @Override
    public void render(ConduitBlockEntity conduitBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        float g = (float)conduitBlockEntity.ticks + f;
        if (!conduitBlockEntity.isActive()) {
            float h = conduitBlockEntity.getRotation(0.0f);
            VertexConsumer vertexConsumer = BASE_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
            matrixStack.push();
            matrixStack.translate(0.5f, 0.5f, 0.5f);
            matrixStack.multiply((Quaternionfc)new Quaternionf().rotationY(h * ((float)Math.PI / 180)));
            this.conduitShell.render(matrixStack, vertexConsumer, i, j);
            matrixStack.pop();
            return;
        }
        float h = conduitBlockEntity.getRotation(f) * 57.295776f;
        float k = MathHelper.sin(g * 0.1f) / 2.0f + 0.5f;
        k = k * k + k;
        matrixStack.push();
        matrixStack.translate(0.5f, 0.3f + k * 0.2f, 0.5f);
        Vector3f vector3f = new Vector3f(0.5f, 1.0f, 0.5f).normalize();
        matrixStack.multiply((Quaternionfc)new Quaternionf().rotationAxis(h * ((float)Math.PI / 180), (Vector3fc)vector3f));
        this.conduit.render(matrixStack, CAGE_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
        matrixStack.pop();
        int l = conduitBlockEntity.ticks / 66 % 3;
        matrixStack.push();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        if (l == 1) {
            matrixStack.multiply((Quaternionfc)new Quaternionf().rotationX(1.5707964f));
        } else if (l == 2) {
            matrixStack.multiply((Quaternionfc)new Quaternionf().rotationZ(1.5707964f));
        }
        VertexConsumer vertexConsumer2 = (l == 1 ? WIND_VERTICAL_TEXTURE : WIND_TEXTURE).getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
        this.conduitWind.render(matrixStack, vertexConsumer2, i, j);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply((Quaternionfc)new Quaternionf().rotationXYZ((float)Math.PI, 0.0f, (float)Math.PI));
        this.conduitWind.render(matrixStack, vertexConsumer2, i, j);
        matrixStack.pop();
        Camera camera = this.dispatcher.camera;
        matrixStack.push();
        matrixStack.translate(0.5f, 0.3f + k * 0.2f, 0.5f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        float m = -camera.getYaw();
        matrixStack.multiply((Quaternionfc)new Quaternionf().rotationYXZ(m * ((float)Math.PI / 180), camera.getPitch() * ((float)Math.PI / 180), (float)Math.PI));
        float n = 1.3333334f;
        matrixStack.scale(1.3333334f, 1.3333334f, 1.3333334f);
        this.conduitEye.render(matrixStack, (conduitBlockEntity.isEyeOpen() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
        matrixStack.pop();
    }
}

