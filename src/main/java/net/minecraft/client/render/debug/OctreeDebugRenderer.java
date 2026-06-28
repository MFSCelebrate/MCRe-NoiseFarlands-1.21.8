/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;

@Environment(value=EnvType.CLIENT)
public class OctreeDebugRenderer {
    final private MinecraftClient client;

    public OctreeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrices, Frustum frustum, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Octree octree = this.client.worldRenderer.getChunkRenderingDataPreparer().getOctree();
        MutableInt mutableInt = new MutableInt(0);
        octree.visit((node, skipVisibilityCheck, depth, bl) -> this.renderNode(node, matrices, vertexConsumers, cameraX, cameraY, cameraZ, depth, skipVisibilityCheck, mutableInt, bl), frustum, 32);
    }

    private void renderNode(Octree.Node node, MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ, int depth, boolean skipVisibilityCheck, MutableInt id, boolean bl) {
        Box box = node.getBoundingBox();
        double d = box.getLengthX();
        long l = Math.round(d / 16.0);
        if (l == 1L) {
            id.add(1);
            double e = box.getCenter().x;
            double f = box.getCenter().y;
            double g = box.getCenter().z;
            int i = bl ? Colors.GREEN : Colors.WHITE;
            DebugRenderer.drawString(matrices, vertexConsumers, String.valueOf(id.getValue()), e, f, g, i, 0.3f);
        }
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        long m = l + 5L;
        VertexRendering.drawBox(matrices, vertexConsumer, box.contract(0.1 * (double)depth).offset(-cameraX, -cameraY, -cameraZ), OctreeDebugRenderer.getColorComponent(m, 0.3f), OctreeDebugRenderer.getColorComponent(m, 0.8f), OctreeDebugRenderer.getColorComponent(m, 0.5f), skipVisibilityCheck ? 0.4f : 1.0f);
    }

    private static float getColorComponent(long size, float gradient) {
        float f = 0.1f;
        return MathHelper.fractionalPart(gradient * (float)size) * 0.9f + 0.1f;
    }
}

