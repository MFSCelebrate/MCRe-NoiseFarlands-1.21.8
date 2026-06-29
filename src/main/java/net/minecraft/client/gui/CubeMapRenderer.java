/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.ProjectionMatrix3;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class CubeMapRenderer
implements AutoCloseable {
    final static private int FACES_COUNT = 6;
    final private GpuBuffer buffer;
    final private ProjectionMatrix3 projectionMatrix;
    final private Identifier id;

    public CubeMapRenderer(Identifier id) {
        this.id = id;
        this.projectionMatrix = new ProjectionMatrix3("cubemap", 0.05f, 10.0f);
        this.buffer = CubeMapRenderer.upload();
    }

    public void draw(MinecraftClient client, float x, float y) {
        RenderSystem.setProjectionMatrix(this.projectionMatrix.set(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), 85.0f), ProjectionType.PERSPECTIVE);
        RenderPipeline renderPipeline = RenderPipelines.POSITION_TEX_PANORAMA;
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        GpuTextureView gpuTextureView = framebuffer.getColorAttachmentView();
        GpuTextureView gpuTextureView2 = framebuffer.getDepthAttachmentView();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(36);
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.rotationX((float)Math.PI);
        matrix4fStack.rotateX(x * ((float)Math.PI / 180));
        matrix4fStack.rotateY(y * ((float)Math.PI / 180));
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)new Matrix4f((Matrix4fc)matrix4fStack), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 0.0f);
        matrix4fStack.popMatrix();
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Cubemap", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());
        try {
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setVertexBuffer(0, this.buffer);
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.bindSampler("Sampler0", client.getTextureManager().getTexture(this.id).getGlTextureView());
            renderPass.drawIndexed(0, 0, 36, 1);
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

    private static GpuBuffer upload() {
        BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION.getVertexSize() * 4 * 6);
        BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(-1.0f, -1.0f, 1.0f);
        bufferBuilder.vertex(-1.0f, 1.0f, 1.0f);
        bufferBuilder.vertex(1.0f, 1.0f, 1.0f);
        bufferBuilder.vertex(1.0f, -1.0f, 1.0f);
        bufferBuilder.vertex(1.0f, -1.0f, 1.0f);
        bufferBuilder.vertex(1.0f, 1.0f, 1.0f);
        bufferBuilder.vertex(1.0f, 1.0f, -1.0f);
        bufferBuilder.vertex(1.0f, -1.0f, -1.0f);
        bufferBuilder.vertex(1.0f, -1.0f, -1.0f);
        bufferBuilder.vertex(1.0f, 1.0f, -1.0f);
        bufferBuilder.vertex(-1.0f, 1.0f, -1.0f);
        bufferBuilder.vertex(-1.0f, -1.0f, -1.0f);
        bufferBuilder.vertex(-1.0f, -1.0f, -1.0f);
        bufferBuilder.vertex(-1.0f, 1.0f, -1.0f);
        bufferBuilder.vertex(-1.0f, 1.0f, 1.0f);
        bufferBuilder.vertex(-1.0f, -1.0f, 1.0f);
        bufferBuilder.vertex(-1.0f, -1.0f, -1.0f);
        bufferBuilder.vertex(-1.0f, -1.0f, 1.0f);
        bufferBuilder.vertex(1.0f, -1.0f, 1.0f);
        bufferBuilder.vertex(1.0f, -1.0f, -1.0f);
        bufferBuilder.vertex(-1.0f, 1.0f, 1.0f);
        bufferBuilder.vertex(-1.0f, 1.0f, -1.0f);
        bufferBuilder.vertex(1.0f, 1.0f, -1.0f);
        bufferBuilder.vertex(1.0f, 1.0f, 1.0f);
        BuiltBuffer builtBuffer = bufferBuilder.end();
        try {
            GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, builtBuffer.getBuffer());
            if (builtBuffer != null) {
                builtBuffer.close();
            }
            if (bufferAllocator != null) {
                bufferAllocator.close();
            }
            return gpuBuffer;
        }
        catch (Throwable throwable) {
            try {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (Throwable throwable3) {
                if (bufferAllocator != null) {
                    try {
                        bufferAllocator.close();
                    }
                    catch (Throwable throwable4) {
                        throwable3.addSuppressed(throwable4);
                    }
                }
                throw throwable3;
            }
        }
    }

    public void registerTextures(TextureManager textureManager) {
        textureManager.registerTexture(this.id, (AbstractTexture)new CubemapTexture(this.id));
    }

    @Override
    public void close() {
        this.buffer.close();
        this.projectionMatrix.close();
    }
}

