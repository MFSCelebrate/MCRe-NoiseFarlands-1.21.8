/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class PostEffectPass
implements AutoCloseable {
    final static private int SIZE = new Std140SizeCalculator().putVec2().get();
    final private String id;
    final private RenderPipeline pipeline;
    final private Identifier outputTargetId;
    final private Map<String, GpuBuffer> uniformBuffers = new HashMap<String, GpuBuffer>();
    final private MappableRingBuffer samplerInfoBuffer;
    final private List<Sampler> samplers;

    public PostEffectPass(RenderPipeline pipeline, Identifier outputTargetId, Map<String, List<UniformValue>> uniforms, List<Sampler> samplers) {
        this.pipeline = pipeline;
        this.id = pipeline.getLocation().toString();
        this.outputTargetId = outputTargetId;
        this.samplers = samplers;
        for (Map.Entry<String, List<UniformValue>> entry : uniforms.entrySet()) {
            List<UniformValue> list = entry.getValue();
            if (list.isEmpty()) continue;
            Std140SizeCalculator std140SizeCalculator = new Std140SizeCalculator();
            for (UniformValue uniformValue : list) {
                uniformValue.addSize(std140SizeCalculator);
            }
            int i = std140SizeCalculator.get();
            MemoryStack memoryStack = MemoryStack.stackPush();
            try {
                Std140Builder std140Builder = Std140Builder.onStack(memoryStack, i);
                for (UniformValue uniformValue2 : list) {
                    uniformValue2.write(std140Builder);
                }
                this.uniformBuffers.put(entry.getKey(), RenderSystem.getDevice().createBuffer(() -> this.id + " / " + (String)entry.getKey(), 128, std140Builder.get()));
                if (memoryStack == null) continue;
            }
            catch (Throwable throwable) {
                if (memoryStack != null) {
                    try {
                        memoryStack.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            memoryStack.close();
        }
        this.samplerInfoBuffer = new MappableRingBuffer(() -> this.id + " SamplerInfo", 130, (samplers.size() + 1) * SIZE);
    }

    public void render(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, GpuBufferSlice gpuBufferSlice) {
        FramePass framePass = builder.createPass(this.id);
        for (Sampler sampler : this.samplers) {
            sampler.preRender(framePass, handles);
        }
        Handle handle2 = handles.computeIfPresent(this.outputTargetId, (id, handle) -> framePass.transfer(handle));
        if (handle2 == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
        }
        framePass.setRenderer(() -> {
            Framebuffer framebuffer = (Framebuffer)handle2.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(gpuBufferSlice, ProjectionType.ORTHOGRAPHIC);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            List<Pair> list = this.samplers.stream().map(sampler -> Pair.of((Object)sampler.samplerName(), (Object)sampler.getTexture(handles))).toList();
            GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.samplerInfoBuffer.getBlocking(), false, true);
            try {
                Std140Builder std140Builder = Std140Builder.intoBuffer(mappedView.data());
                std140Builder.putVec2(framebuffer.textureWidth, framebuffer.textureHeight);
                for (Pair pair : list) {
                    std140Builder.putVec2(((GpuTextureView)pair.getSecond()).getWidth(0), ((GpuTextureView)pair.getSecond()).getHeight(0));
                }
                if (mappedView != null) {
                    mappedView.close();
                }
            }
            catch (Throwable std140Builder) {
                if (mappedView != null) {
                    try {
                        mappedView.close();
                    }
                    catch (Throwable throwable) {
                        std140Builder.addSuppressed(throwable);
                    }
                }
                throw std140Builder;
            }
            GpuBuffer gpuBuffer = RenderSystem.getQuadVertexBuffer();
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
            GpuBuffer gpuBuffer2 = shapeIndexBuffer.getIndexBuffer(6);
            RenderPass renderPass = commandEncoder.createRenderPass(() -> "Post pass " + this.id, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null, OptionalDouble.empty());
            try {
                renderPass.setPipeline(this.pipeline);
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("SamplerInfo", this.samplerInfoBuffer.getBlocking());
                for (Map.Entry<String, GpuBuffer> entry : this.uniformBuffers.entrySet()) {
                    renderPass.setUniform(entry.getKey(), entry.getValue());
                }
                renderPass.setVertexBuffer(0, gpuBuffer);
                renderPass.setIndexBuffer(gpuBuffer2, shapeIndexBuffer.getIndexType());
                for (Pair pair2 : list) {
                    renderPass.bindSampler((String)pair2.getFirst() + "Sampler", (GpuTextureView)pair2.getSecond());
                }
                renderPass.drawIndexed(0, 0, 6, 1);
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
            this.samplerInfoBuffer.rotate();
            RenderSystem.restoreProjectionMatrix();
            for (Sampler sampler2 : this.samplers) {
                sampler2.postRender(handles);
            }
        });
    }

    @Override
    public void close() {
        for (GpuBuffer gpuBuffer : this.uniformBuffers.values()) {
            gpuBuffer.close();
        }
        this.samplerInfoBuffer.close();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Sampler {
        public void preRender(FramePass var1, Map<Identifier, Handle<Framebuffer>> var2);

        default public void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
        }

        public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> var1);

        public String samplerName();
    }

    @Environment(value=EnvType.CLIENT)
    public record TargetSampler(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements Sampler
    {
        private Handle<Framebuffer> getTarget(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            Handle<Framebuffer> handle = internalTargets.get(this.targetId);
            if (handle == null) {
                throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
            }
            return handle;
        }

        @Override
        public void preRender(FramePass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
            pass.dependsOn(this.getTarget(internalTargets));
        }

        @Override
        public void postRender(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            if (this.bilinear) {
                this.getTarget(internalTargets).get().setFilter(FilterMode.NEAREST);
            }
        }

        @Override
        public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            GpuTextureView gpuTextureView;
            Handle<Framebuffer> handle = this.getTarget(internalTargets);
            Framebuffer framebuffer = handle.get();
            framebuffer.setFilter(this.bilinear ? FilterMode.LINEAR : FilterMode.NEAREST);
            GpuTextureView gpuTextureView2 = gpuTextureView = this.depthBuffer ? framebuffer.getDepthAttachmentView() : framebuffer.getColorAttachmentView();
            if (gpuTextureView == null) {
                throw new IllegalStateException("Missing " + (this.depthBuffer ? "depth" : "color") + "texture for target " + String.valueOf(this.targetId));
            }
            return gpuTextureView;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record TextureSampler(String samplerName, AbstractTexture texture, int width, int height) implements Sampler
    {
        @Override
        public void preRender(FramePass pass, Map<Identifier, Handle<Framebuffer>> internalTargets) {
        }

        @Override
        public GpuTextureView getTexture(Map<Identifier, Handle<Framebuffer>> internalTargets) {
            return this.texture.getGlTextureView();
        }
    }
}

