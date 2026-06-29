/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.TracyClient
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util.tracy;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;

@Environment(value=EnvType.CLIENT)
public class TracyFrameCapturer
implements AutoCloseable {
    final static private int MAX_WIDTH = 320;
    final static private int MAX_HEIGHT = 180;
    final static private int field_54254 = 4;
    private int framebufferWidth;
    private int framebufferHeight;
    private int width = 320;
    private int height = 180;
    private GpuTexture texture;
    private GpuTextureView textureView;
    private GpuBuffer buffer;
    private int offset;
    private boolean captured;
    private Status status = Status.WAITING_FOR_CAPTURE;

    public TracyFrameCapturer() {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.texture = gpuDevice.createTexture("Tracy Frame Capture", 10, TextureFormat.RGBA8, this.width, this.height, 1, 1);
        this.textureView = gpuDevice.createTextureView(this.texture);
        this.buffer = gpuDevice.createBuffer(() -> "Tracy Frame Capture buffer", 9, this.width * this.height * 4);
    }

    private void resize(int framebufferWidth, int framebufferHeight) {
        float f = (float)framebufferWidth / (float)framebufferHeight;
        if (framebufferWidth > 320) {
            framebufferWidth = 320;
            framebufferHeight = (int)(320.0f / f);
        }
        if (framebufferHeight > 180) {
            framebufferWidth = (int)(180.0f * f);
            framebufferHeight = 180;
        }
        framebufferWidth = framebufferWidth / 4 * 4;
        framebufferHeight = framebufferHeight / 4 * 4;
        if (this.width != framebufferWidth || this.height != framebufferHeight) {
            this.width = framebufferWidth;
            this.height = framebufferHeight;
            GpuDevice gpuDevice = RenderSystem.getDevice();
            this.texture.close();
            this.texture = gpuDevice.createTexture("Tracy Frame Capture", 10, TextureFormat.RGBA8, framebufferWidth, framebufferHeight, 1, 1);
            this.textureView.close();
            this.textureView = gpuDevice.createTextureView(this.texture);
            this.buffer.close();
            this.buffer = gpuDevice.createBuffer(() -> "Tracy Frame Capture buffer", 9, framebufferWidth * framebufferHeight * 4);
        }
    }

    public void capture(Framebuffer framebuffer) {
        if (this.status != Status.WAITING_FOR_CAPTURE || this.captured || framebuffer.getColorAttachment() == null) {
            return;
        }
        this.captured = true;
        if (framebuffer.textureWidth != this.framebufferWidth || framebuffer.textureHeight != this.framebufferHeight) {
            this.framebufferWidth = framebuffer.textureWidth;
            this.framebufferHeight = framebuffer.textureHeight;
            this.resize(this.framebufferWidth, this.framebufferHeight);
        }
        this.status = Status.WAITING_FOR_COPY;
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(6);
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Tracy blit", this.textureView, OptionalInt.empty());
        try {
            renderPass.setPipeline(RenderPipelines.TRACY_BLIT);
            renderPass.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.bindSampler("InSampler", framebuffer.getColorAttachmentView());
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
        commandEncoder.copyTextureToBuffer(this.texture, this.buffer, 0, () -> {
            this.status = Status.WAITING_FOR_UPLOAD;
        }, 0);
        this.offset = 0;
    }

    public void upload() {
        if (this.status != Status.WAITING_FOR_UPLOAD) {
            return;
        }
        this.status = Status.WAITING_FOR_CAPTURE;
        GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.buffer, true, false);
        try {
            TracyClient.frameImage((ByteBuffer)mappedView.data(), (int)this.width, (int)this.height, (int)this.offset, (boolean)true);
            if (mappedView != null) {
                mappedView.close();
            }
        }
        catch (Throwable throwable) {
            if (mappedView != null) {
                try {
                    mappedView.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
    }

    public void markFrame() {
        ++this.offset;
        this.captured = false;
        TracyClient.markFrame();
    }

    @Override
    public void close() {
        this.texture.close();
        this.textureView.close();
        this.buffer.close();
    }

    @Environment(value=EnvType.CLIENT)
    static final class Status
    extends Enum<Status> {
        final static public Status WAITING_FOR_CAPTURE = new Status();
        final static public Status WAITING_FOR_COPY = new Status();
        final static public Status WAITING_FOR_UPLOAD = new Status();
        final static private Status[] field_57837;

        public static Status[] values() {
            return (Status[])field_57837.clone();
        }

        public static Status valueOf(String string) {
            return Enum.valueOf(Status.class, string);
        }

        private static Status[] method_68340() {
            return new Status[]{WAITING_FOR_CAPTURE, WAITING_FOR_COPY, WAITING_FOR_UPLOAD};
        }

        static {
            field_57837 = Status.method_68340();
        }
    }
}

