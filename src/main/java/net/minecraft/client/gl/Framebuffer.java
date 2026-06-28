/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class Framebuffer {
    static private int index = 0;
    public int textureWidth;
    public int textureHeight;
    public int viewportWidth;
    public int viewportHeight;
    final protected String name;
    final public boolean useDepthAttachment;
    @Nullable
    protected GpuTexture colorAttachment;
    @Nullable
    protected GpuTextureView colorAttachmentView;
    @Nullable
    protected GpuTexture depthAttachment;
    @Nullable
    protected GpuTextureView depthAttachmentView;
    public FilterMode filterMode;

    public Framebuffer(@Nullable String name, boolean useDepthAttachment) {
        this.name = name == null ? "FBO " + index++ : name;
        this.useDepthAttachment = useDepthAttachment;
    }

    public void resize(int width, int height) {
        RenderSystem.assertOnRenderThread();
        this.delete();
        this.initFbo(width, height);
    }

    public void delete() {
        RenderSystem.assertOnRenderThread();
        if (this.depthAttachment != null) {
            this.depthAttachment.close();
            this.depthAttachment = null;
        }
        if (this.depthAttachmentView != null) {
            this.depthAttachmentView.close();
            this.depthAttachmentView = null;
        }
        if (this.colorAttachment != null) {
            this.colorAttachment.close();
            this.colorAttachment = null;
        }
        if (this.colorAttachmentView != null) {
            this.colorAttachmentView.close();
            this.colorAttachmentView = null;
        }
    }

    public void copyDepthFrom(Framebuffer framebuffer) {
        RenderSystem.assertOnRenderThread();
        if (this.depthAttachment == null) {
            throw new IllegalStateException("Trying to copy depth texture to a RenderTarget without a depth texture");
        }
        if (framebuffer.depthAttachment == null) {
            throw new IllegalStateException("Trying to copy depth texture from a RenderTarget without a depth texture");
        }
        RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(framebuffer.depthAttachment, this.depthAttachment, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
    }

    public void initFbo(int width, int height) {
        RenderSystem.assertOnRenderThread();
        GpuDevice gpuDevice = RenderSystem.getDevice();
        int i = gpuDevice.getMaxTextureSize();
        if (width <= 0 || width > i || height <= 0 || height > i) {
            throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")");
        }
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.textureWidth = width;
        this.textureHeight = height;
        if (this.useDepthAttachment) {
            this.depthAttachment = gpuDevice.createTexture(() -> this.name + " / Depth", 15, TextureFormat.DEPTH32, width, height, 1, 1);
            this.depthAttachmentView = gpuDevice.createTextureView(this.depthAttachment);
            this.depthAttachment.setTextureFilter(FilterMode.NEAREST, false);
            this.depthAttachment.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        }
        this.colorAttachment = gpuDevice.createTexture(() -> this.name + " / Color", 15, TextureFormat.RGBA8, width, height, 1, 1);
        this.colorAttachmentView = gpuDevice.createTextureView(this.colorAttachment);
        this.colorAttachment.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        this.setFilter(FilterMode.NEAREST, true);
    }

    public void setFilter(FilterMode filter) {
        this.setFilter(filter, false);
    }

    private void setFilter(FilterMode filter, boolean force) {
        if (this.colorAttachment == null) {
            throw new IllegalStateException("Can't change filter mode, color texture doesn't exist yet");
        }
        if (force || filter != this.filterMode) {
            this.filterMode = filter;
            this.colorAttachment.setTextureFilter(filter, false);
        }
    }

    public void blitToScreen() {
        if (this.colorAttachment == null) {
            throw new IllegalStateException("Can't blit to screen, color texture doesn't exist yet");
        }
        RenderSystem.getDevice().createCommandEncoder().presentTexture(this.colorAttachmentView);
    }

    public void drawBlit(GpuTextureView texture) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(6);
        GpuBuffer gpuBuffer2 = RenderSystem.getQuadVertexBuffer();
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Blit render target", texture, OptionalInt.empty());
        try {
            renderPass.setPipeline(RenderPipelines.ENTITY_OUTLINE_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setVertexBuffer(0, gpuBuffer2);
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.bindSampler("InSampler", this.colorAttachmentView);
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
    }

    @Nullable
    public GpuTexture getColorAttachment() {
        return this.colorAttachment;
    }

    @Nullable
    public GpuTextureView getColorAttachmentView() {
        return this.colorAttachmentView;
    }

    @Nullable
    public GpuTexture getDepthAttachment() {
        return this.depthAttachment;
    }

    @Nullable
    public GpuTextureView getDepthAttachmentView() {
        return this.depthAttachmentView;
    }
}

