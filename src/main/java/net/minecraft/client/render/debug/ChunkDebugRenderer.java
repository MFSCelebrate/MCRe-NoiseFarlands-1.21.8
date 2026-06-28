/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Vector4f
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class ChunkDebugRenderer
implements DebugRenderer.Renderer {
    final static public Direction[] DIRECTIONS = Direction.values();
    final private MinecraftClient client;

    public ChunkDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Frustum frustum;
        WorldRenderer worldRenderer = this.client.worldRenderer;
        if (this.client.debugChunkInfo || this.client.debugChunkOcclusion) {
            ChunkRenderingDataPreparer chunkRenderingDataPreparer = worldRenderer.getChunkRenderingDataPreparer();
            for (ChunkBuilder.BuiltChunk builtChunk : worldRenderer.getBuiltChunks()) {
                int i;
                VertexConsumer vertexConsumer;
                ChunkRenderingDataPreparer.ChunkInfo chunkInfo = chunkRenderingDataPreparer.getInfo(builtChunk);
                if (chunkInfo == null) continue;
                BlockPos blockPos = builtChunk.getOrigin();
                matrices.push();
                matrices.translate((double)blockPos.getX() - cameraX, (double)blockPos.getY() - cameraY, (double)blockPos.getZ() - cameraZ);
                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                if (this.client.debugChunkInfo) {
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
                    i = chunkInfo.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)chunkInfo.propagationLevel / 50.0f, 0.9f, 0.9f);
                    int j = i >> 16 & 0xFF;
                    int k = i >> 8 & 0xFF;
                    int l = i & 0xFF;
                    for (int m = 0; m < DIRECTIONS.length; ++m) {
                        if (!chunkInfo.hasDirection(m)) continue;
                        Direction direction = DIRECTIONS[m];
                        vertexConsumer.vertex(matrix4f, 8.0f, 8.0f, 8.0f).color(j, k, l, 255).normal(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
                        vertexConsumer.vertex(matrix4f, (float)(8 - 16 * direction.getOffsetX()), (float)(8 - 16 * direction.getOffsetY()), (float)(8 - 16 * direction.getOffsetZ())).color(j, k, l, 255).normal(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
                    }
                }
                if (this.client.debugChunkOcclusion && builtChunk.getCurrentRenderData().hasData()) {
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
                    i = 0;
                    for (Direction direction2 : DIRECTIONS) {
                        for (Direction direction3 : DIRECTIONS) {
                            boolean bl = builtChunk.getCurrentRenderData().isVisibleThrough(direction2, direction3);
                            if (bl) continue;
                            ++i;
                            vertexConsumer.vertex(matrix4f, (float)(8 + 8 * direction2.getOffsetX()), (float)(8 + 8 * direction2.getOffsetY()), (float)(8 + 8 * direction2.getOffsetZ())).color(255, 0, 0, 255).normal(direction2.getOffsetX(), direction2.getOffsetY(), direction2.getOffsetZ());
                            vertexConsumer.vertex(matrix4f, (float)(8 + 8 * direction3.getOffsetX()), (float)(8 + 8 * direction3.getOffsetY()), (float)(8 + 8 * direction3.getOffsetZ())).color(255, 0, 0, 255).normal(direction3.getOffsetX(), direction3.getOffsetY(), direction3.getOffsetZ());
                        }
                    }
                    if (i > 0) {
                        VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
                        float f = 0.5f;
                        float g = 0.2f;
                        vertexConsumer2.vertex(matrix4f, 0.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 0.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 15.5f, 0.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 15.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 15.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                        vertexConsumer2.vertex(matrix4f, 0.5f, 0.5f, 15.5f).color(0.9f, 0.9f, 0.0f, 0.2f);
                    }
                }
                matrices.pop();
            }
        }
        if ((frustum = worldRenderer.getCapturedFrustum()) != null) {
            matrices.push();
            matrices.translate((float)(frustum.getX() - cameraX), (float)(frustum.getY() - cameraY), (float)(frustum.getZ() - cameraZ));
            Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
            Vector4f[] vector4fs = frustum.getBoundaryPoints();
            VertexConsumer vertexConsumer3 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
            this.addFace(vertexConsumer3, matrix4f2, vector4fs, 0, 1, 2, 3, 0, 1, 1);
            this.addFace(vertexConsumer3, matrix4f2, vector4fs, 4, 5, 6, 7, 1, 0, 0);
            this.addFace(vertexConsumer3, matrix4f2, vector4fs, 0, 1, 5, 4, 1, 1, 0);
            this.addFace(vertexConsumer3, matrix4f2, vector4fs, 2, 3, 7, 6, 0, 0, 1);
            this.addFace(vertexConsumer3, matrix4f2, vector4fs, 0, 4, 7, 3, 0, 1, 0);
            this.addFace(vertexConsumer3, matrix4f2, vector4fs, 1, 5, 6, 2, 1, 0, 1);
            VertexConsumer vertexConsumer4 = vertexConsumers.getBuffer(RenderLayer.getLines());
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[0]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[1]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[1]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[2]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[2]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[3]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[3]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[0]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[4]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[5]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[5]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[6]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[6]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[7]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[7]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[4]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[0]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[4]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[1]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[5]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[2]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[6]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[3]);
            this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[7]);
            matrices.pop();
        }
    }

    private void addEndpoint(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vector4f vertex) {
        vertexConsumer.vertex(positionMatrix, vertex.x(), vertex.y(), vertex.z()).color(Colors.BLACK).normal(0.0f, 0.0f, -1.0f);
    }

    private void addFace(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vector4f[] vertices, int i1, int i2, int i3, int i4, int r, int g, int b) {
        float f = 0.25f;
        vertexConsumer.vertex(positionMatrix, vertices[i1].x(), vertices[i1].y(), vertices[i1].z()).color((float)r, (float)g, (float)b, 0.25f);
        vertexConsumer.vertex(positionMatrix, vertices[i2].x(), vertices[i2].y(), vertices[i2].z()).color((float)r, (float)g, (float)b, 0.25f);
        vertexConsumer.vertex(positionMatrix, vertices[i3].x(), vertices[i3].y(), vertices[i3].z()).color((float)r, (float)g, (float)b, 0.25f);
        vertexConsumer.vertex(positionMatrix, vertices[i4].x(), vertices[i4].y(), vertices[i4].z()).color((float)r, (float)g, (float)b, 0.25f);
    }
}

