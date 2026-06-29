/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeamEmitter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    final static public Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/beacon_beam.png");
    final static public int MAX_BEAM_HEIGHT = 2048;
    final static private float field_56505 = 96.0f;
    final static public float field_56503 = 0.2f;
    final static public float field_56504 = 0.25f;

    public BeaconBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        long l = ((BlockEntity)entity).getWorld().getTime();
        float f = (float)cameraPos.subtract(((BlockEntity)entity).getPos().toCenterPos()).horizontalLength();
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        float g = clientPlayerEntity != null && clientPlayerEntity.isUsingSpyglass() ? 1.0f : Math.max(1.0f, f / 96.0f);
        List<BeamEmitter.BeamSegment> list = ((BeamEmitter)entity).getBeamSegments();
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            BeamEmitter.BeamSegment beamSegment = list.get(j);
            BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, tickProgress, g, l, i, j == list.size() - 1 ? 2048 : beamSegment.getHeight(), beamSegment.getColor());
            i += beamSegment.getHeight();
        }
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickProgress, float scale, long worldTime, int yOffset, int maxY, int color) {
        BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickProgress, 1.0f, worldTime, yOffset, maxY, color, 0.2f * scale, 0.25f * scale);
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickProgress, float heightScale, long worldTime, int yOffset, int maxY, int color, float innerRadius, float outerRadius) {
        int i = yOffset + maxY;
        matrices.push();
        matrices.translate(0.5, 0.0, 0.5);
        float f = (float)Math.floorMod(worldTime, 40) + tickProgress;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2f - (float)MathHelper.floor(g * 0.1f));
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f * 2.25f - 45.0f));
        float j = 0.0f;
        float k = innerRadius;
        float l = innerRadius;
        float m = 0.0f;
        float n = -innerRadius;
        float o = 0.0f;
        float p = 0.0f;
        float q = -innerRadius;
        float r = 0.0f;
        float s = 1.0f;
        float t = -1.0f + h;
        float u = (float)maxY * heightScale * (0.5f / innerRadius) + t;
        BeaconBlockEntityRenderer.renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), color, yOffset, i, 0.0f, k, l, 0.0f, n, 0.0f, 0.0f, q, 0.0f, 1.0f, u, t);
        matrices.pop();
        j = -outerRadius;
        k = -outerRadius;
        l = outerRadius;
        m = -outerRadius;
        n = -outerRadius;
        o = outerRadius;
        p = outerRadius;
        q = outerRadius;
        r = 0.0f;
        s = 1.0f;
        t = -1.0f + h;
        u = (float)maxY * heightScale + t;
        BeaconBlockEntityRenderer.renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), ColorHelper.withAlpha(32, color), yOffset, i, j, k, l, m, n, o, p, q, 0.0f, 1.0f, u, t);
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        BeaconBlockEntityRenderer.renderBeamFace(entry, vertices, color, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        BeaconBlockEntityRenderer.renderBeamFace(entry, vertices, color, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        BeaconBlockEntityRenderer.renderBeamFace(entry, vertices, color, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        BeaconBlockEntityRenderer.renderBeamFace(entry, vertices, color, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, height, x1, z1, u2, v1);
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, yOffset, x1, z1, u2, v2);
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, yOffset, x2, z2, u1, v2);
        BeaconBlockEntityRenderer.renderBeamVertex(matrix, vertices, color, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int y, float x, float z, float u, float v) {
        vertices.vertex(matrix, x, (float)y, z).color(color).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return MinecraftClient.getInstance().options.getClampedViewDistance() * 16;
    }

    @Override
    public boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return Vec3d.ofCenter(((BlockEntity)blockEntity).getPos()).multiply(1.0, 0.0, 1.0).isInRange(pos.multiply(1.0, 0.0, 1.0), this.getRenderDistance());
    }
}

