/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.SequencedCollection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayerGroup;

@Environment(value=EnvType.CLIENT)
public record SectionRenderState(EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>> drawsPerLayer, int maxIndicesRequired, GpuBufferSlice[] dynamicTransforms) {
    public void renderSection(BlockRenderLayerGroup group) {
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = this.maxIndicesRequired == 0 ? null : shapeIndexBuffer.getIndexBuffer(this.maxIndicesRequired);
        VertexFormat.IndexType indexType = this.maxIndicesRequired == 0 ? null : shapeIndexBuffer.getIndexType();
        BlockRenderLayer[] blockRenderLayers = group.getLayers();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = false;
        Framebuffer framebuffer = group.getFramebuffer();
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Section layers for " + group.getName(), framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty());
        try {
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindSampler("Sampler2", minecraftClient.gameRenderer.getLightmapTextureManager().getGlTextureView());
            for (BlockRenderLayer blockRenderLayer : blockRenderLayers) {
                SequencedCollection<RenderPass.RenderObject<Object>> list = this.drawsPerLayer.get((Object)blockRenderLayer);
                if (list.isEmpty()) continue;
                if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
                    list = list.reversed();
                }
                renderPass.setPipeline(bl ? RenderPipelines.WIREFRAME : blockRenderLayer.getPipeline());
                renderPass.bindSampler("Sampler0", blockRenderLayer.getTextureView());
                renderPass.drawMultipleIndexed(list, gpuBuffer, indexType, List.of("DynamicTransforms"), this.dynamicTransforms);
            }
            if (renderPass != null) {
                renderPass.close();
            }
        }
        catch (Throwable throwable) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }
}

