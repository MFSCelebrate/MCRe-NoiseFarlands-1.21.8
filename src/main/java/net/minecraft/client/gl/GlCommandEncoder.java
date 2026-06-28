/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL11C
 *  org.lwjgl.opengl.GL12
 *  org.lwjgl.opengl.GL31
 *  org.lwjgl.opengl.GL32
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.CompiledShaderPipeline;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.GlGpuFence;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.RenderPassImpl;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.GlTextureView;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.slf4j.Logger;

@Environment(value = EnvType.CLIENT)
public class GlCommandEncoder implements CommandEncoder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final GlBackend backend;
    private final int temporaryFb1;
    private final int temporaryFb2;
    @Nullable private RenderPipeline currentPipeline;
    private boolean renderPassOpen;
    @Nullable private ShaderProgram currentProgram;

    protected GlCommandEncoder(GlBackend backend) {
        this.backend = backend;
        this.temporaryFb1 = backend.getBufferManager().createFramebuffer();
        this.temporaryFb2 = backend.getBufferManager().createFramebuffer();
    }

    @Override
    public RenderPass createRenderPass(Supplier<
                    String> supplier, GpuTextureView gpuTextureView, OptionalInt optionalInt) {
        return this.createRenderPass(supplier, gpuTextureView, optionalInt, null, OptionalDouble.empty());
    }

    @Override
    public RenderPass createRenderPass(Supplier<
                    String> supplier, GpuTextureView gpuTextureView, OptionalInt optionalInt, @Nullable
                    GpuTextureView gpuTextureView2, OptionalDouble optionalDouble) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before creating a new one!");
        }
        if (optionalDouble.isPresent() && gpuTextureView2 == null) {
            LOGGER.warn("Depth clear value was provided but no depth texture is being used");
        }
        if (gpuTextureView.isClosed()) {
            throw new IllegalStateException("Color texture is closed");
        }
        if ((gpuTextureView.com_mojang_blaze3d_textures_GpuTexture_texture().usage() & 8) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT");
        }
        if (gpuTextureView.com_mojang_blaze3d_textures_GpuTexture_texture().getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported as an attachment");
        }
        if (gpuTextureView2 != null) {
            if (gpuTextureView2.isClosed()) {
                throw new IllegalStateException("Depth texture is closed");
            }
            if ((gpuTextureView2.com_mojang_blaze3d_textures_GpuTexture_texture().usage() & 8) == 0) {
                throw new IllegalStateException("Depth texture must have USAGE_RENDER_ATTACHMENT");
            }
            if (gpuTextureView2.com_mojang_blaze3d_textures_GpuTexture_texture().getDepthOrLayers() > 1) {
                throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported as an attachment");
            }
        }
        this.renderPassOpen = true;
        this.backend.getDebugLabelManager().pushDebugGroup(supplier);
        int i = ((GlTexture) gpuTextureView.com_mojang_blaze3d_textures_GpuTexture_texture()).getOrCreateFramebuffer(this.backend.getBufferManager(), gpuTextureView2 == null ? null : gpuTextureView2.com_mojang_blaze3d_textures_GpuTexture_texture());
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, i);
        int j = 0;
        if (optionalInt.isPresent()) {
            int k = optionalInt.getAsInt();
            GL11.glClearColor((float) ColorHelper.getRedFloat(k), (float) ColorHelper.getGreenFloat(k), (float) ColorHelper.getBlueFloat(k), (float) ColorHelper.getAlphaFloat(k));
            j |= GlConst.GL_COLOR_BUFFER_BIT;
        }
        if (gpuTextureView2 != null && optionalDouble.isPresent()) {
            GL11.glClearDepth((double) optionalDouble.getAsDouble());
            j |= GlConst.GL_DEPTH_BUFFER_BIT;
        }
        if (j != 0) {
            GlStateManager._disableScissorTest();
            GlStateManager._depthMask(true);
            GlStateManager._colorMask(true, true, true, true);
            GlStateManager._clear(j);
        }
        GlStateManager._viewport(0, 0, gpuTextureView.getWidth(0), gpuTextureView.getHeight(0));
        this.currentPipeline = null;
        return new RenderPassImpl(this, gpuTextureView2 != null);
    }

    @Override
    public void clearColorTexture(GpuTexture gpuTexture, int i) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before creating a new one!");
        }
        this.validateColorAttachment(gpuTexture);
        this.backend.getBufferManager().setupFramebuffer(this.temporaryFb2, ((GlTexture) gpuTexture).glId, 0, 0, GlConst.GL_FRAMEBUFFER);
        GL11.glClearColor((float) ColorHelper.getRedFloat(i), (float) ColorHelper.getGreenFloat(i), (float) ColorHelper.getBlueFloat(i), (float) ColorHelper.getAlphaFloat(i));
        GlStateManager._disableScissorTest();
        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._clear(GlConst.GL_COLOR_BUFFER_BIT);
        GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GlConst.GL_TEXTURE_2D, 0, 0);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void clearColorAndDepthTextures(GpuTexture gpuTexture, int i, GpuTexture gpuTexture2, double d) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before creating a new one!");
        }
        this.validateColorAttachment(gpuTexture);
        this.validateDepthAttachment(gpuTexture2);
        int j = ((GlTexture) gpuTexture).getOrCreateFramebuffer(this.backend.getBufferManager(), gpuTexture2);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, j);
        GlStateManager._disableScissorTest();
        GL11.glClearDepth((double) d);
        GL11.glClearColor((float) ColorHelper.getRedFloat(i), (float) ColorHelper.getGreenFloat(i), (float) ColorHelper.getBlueFloat(i), (float) ColorHelper.getAlphaFloat(i));
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._clear(GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void clearColorAndDepthTextures(GpuTexture gpuTexture, int i, GpuTexture gpuTexture2, double d, int j, int k, int l, int m) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before creating a new one!");
        }
        this.validateColorAttachment(gpuTexture);
        this.validateDepthAttachment(gpuTexture2);
        this.validate(gpuTexture, j, k, l, m);
        int n = ((GlTexture) gpuTexture).getOrCreateFramebuffer(this.backend.getBufferManager(), gpuTexture2);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, n);
        GlStateManager._scissorBox(j, k, l, m);
        GlStateManager._enableScissorTest();
        GL11.glClearDepth((double) d);
        GL11.glClearColor((float) ColorHelper.getRedFloat(i), (float) ColorHelper.getGreenFloat(i), (float) ColorHelper.getBlueFloat(i), (float) ColorHelper.getAlphaFloat(i));
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._clear(GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    private void validate(GpuTexture texture, int regionX, int regionY, int regionWidth, int regionHeight) {
        if (regionX < 0 || regionX >= texture.getWidth(0)) {
            throw new IllegalArgumentException("regionX should not be outside of the texture");
        }
        if (regionY < 0 || regionY >= texture.getHeight(0)) {
            throw new IllegalArgumentException("regionY should not be outside of the texture");
        }
        if (regionWidth <= 0) {
            throw new IllegalArgumentException("regionWidth should be greater than 0");
        }
        if (regionX + regionWidth > texture.getWidth(0)) {
            throw new IllegalArgumentException("regionWidth + regionX should be less than the texture width");
        }
        if (regionHeight <= 0) {
            throw new IllegalArgumentException("regionHeight should be greater than 0");
        }
        if (regionY + regionHeight > texture.getHeight(0)) {
            throw new IllegalArgumentException("regionWidth + regionX should be less than the texture height");
        }
    }

    @Override
    public void clearDepthTexture(GpuTexture gpuTexture, double d) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before creating a new one!");
        }
        this.validateDepthAttachment(gpuTexture);
        this.backend.getBufferManager().setupFramebuffer(this.temporaryFb2, 0, ((GlTexture) gpuTexture).glId, 0, GlConst.GL_FRAMEBUFFER);
        GL11.glDrawBuffer(0);
        GL11.glClearDepth((double) d);
        GlStateManager._depthMask(true);
        GlStateManager._disableScissorTest();
        GlStateManager._clear(GlConst.GL_DEPTH_BUFFER_BIT);
        GL11.glDrawBuffer((int) GlConst.GL_COLOR_ATTACHMENT0);
        GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_TEXTURE_2D, 0, 0);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    private void validateColorAttachment(GpuTexture texture) {
        if (!texture.getFormat().hasColorAspect()) {
            throw new IllegalStateException("Trying to clear a non-color texture as color");
        }
        if (texture.isClosed()) {
            throw new IllegalStateException("Color texture is closed");
        }
        if ((texture.usage() & 8) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT");
        }
        if (texture.getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Clearing a texture with multiple layers or depths is not yet supported");
        }
    }

    private void validateDepthAttachment(GpuTexture texture) {
        if (!texture.getFormat().hasDepthAspect()) {
            throw new IllegalStateException("Trying to clear a non-depth texture as depth");
        }
        if (texture.isClosed()) {
            throw new IllegalStateException("Depth texture is closed");
        }
        if ((texture.usage() & 8) == 0) {
            throw new IllegalStateException("Depth texture must have USAGE_RENDER_ATTACHMENT");
        }
        if (texture.getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Clearing a texture with multiple layers or depths is not yet supported");
        }
    }

    @Override
    public void writeToBuffer(GpuBufferSlice gpuBufferSlice, ByteBuffer byteBuffer) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        GlGpuBuffer glGpuBuffer = (GlGpuBuffer) gpuBufferSlice.buffer();
        if (glGpuBuffer.closed) {
            throw new IllegalStateException("Buffer already closed");
        }
        if ((glGpuBuffer.usage() & GpuBuffer.USAGE_COPY_DST) == 0) {
            throw new IllegalStateException("Buffer needs USAGE_COPY_DST to be a destination for a copy");
        }
        int i = byteBuffer.remaining();
        if (i > gpuBufferSlice.length()) {
            throw new IllegalArgumentException("Cannot write more data than the slice allows (attempting to write " + i + " bytes into a slice of length " + gpuBufferSlice.length() + ")");
        }
        if (gpuBufferSlice.length() + gpuBufferSlice.offset() > glGpuBuffer.size) {
            throw new IllegalArgumentException("Cannot write more data than this buffer can hold (attempting to write " + i + " bytes at offset " + gpuBufferSlice.offset() + " to " + glGpuBuffer.size + " size buffer)");
        }
        this.backend.getBufferManager().setBufferSubData(glGpuBuffer.id, gpuBufferSlice.offset(), byteBuffer);
    }

    @Override
    public GpuBuffer.MappedView mapBuffer(GpuBuffer gpuBuffer, boolean bl, boolean bl2) {
        return this.mapBuffer(gpuBuffer.slice(), bl, bl2);
    }

    @Override
    public GpuBuffer.MappedView mapBuffer(GpuBufferSlice gpuBufferSlice, boolean bl, boolean bl2) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        GlGpuBuffer glGpuBuffer = (GlGpuBuffer) gpuBufferSlice.buffer();
        if (glGpuBuffer.closed) {
            throw new IllegalStateException("Buffer already closed");
        }
        if (!bl && !bl2) {
            throw new IllegalArgumentException("At least read or write must be true");
        }
        if (bl && (glGpuBuffer.usage() & GpuBuffer.USAGE_MAP_READ) == 0) {
            throw new IllegalStateException("Buffer is not readable");
        }
        if (bl2 && (glGpuBuffer.usage() & GpuBuffer.USAGE_MAP_WRITE) == 0) {
            throw new IllegalStateException("Buffer is not writable");
        }
        if (gpuBufferSlice.offset() + gpuBufferSlice.length() > glGpuBuffer.size) {
            throw new IllegalArgumentException("Cannot map more data than this buffer can hold (attempting to map " + gpuBufferSlice.length() + " bytes at offset " + gpuBufferSlice.offset() + " from " + glGpuBuffer.size + " size buffer)");
        }
        int i = 0;
        if (bl) {
            i |= 1;
        }
        if (bl2) {
            i |= 0x22;
        }
        return this.backend.getGpuBufferManager().mapBufferRange(this.backend.getBufferManager(), glGpuBuffer, gpuBufferSlice.offset(), gpuBufferSlice.length(), i);
    }

    @Override
    public void copyToBuffer(GpuBufferSlice gpuBufferSlice, GpuBufferSlice gpuBufferSlice2) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        GlGpuBuffer glGpuBuffer = (GlGpuBuffer) gpuBufferSlice.buffer();
        if (glGpuBuffer.closed) {
            throw new IllegalStateException("Source buffer already closed");
        }
        if ((glGpuBuffer.usage() & GpuBuffer.USAGE_COPY_DST) == 0) {
            throw new IllegalStateException("Source buffer needs USAGE_COPY_DST to be a destination for a copy");
        }
        GlGpuBuffer glGpuBuffer2 = (GlGpuBuffer) gpuBufferSlice2.buffer();
        if (glGpuBuffer2.closed) {
            throw new IllegalStateException("Target buffer already closed");
        }
        if ((glGpuBuffer2.usage() & GpuBuffer.USAGE_COPY_DST) == 0) {
            throw new IllegalStateException("Target buffer needs USAGE_COPY_DST to be a destination for a copy");
        }
        if (gpuBufferSlice.length() != gpuBufferSlice2.length()) {
            throw new IllegalArgumentException("Cannot copy from slice of size " + gpuBufferSlice.length() + " to slice of size " + gpuBufferSlice2.length() + ", they must be equal");
        }
        if (gpuBufferSlice.offset() + gpuBufferSlice.length() > glGpuBuffer.size) {
            throw new IllegalArgumentException("Cannot copy more data than the source buffer holds (attempting to copy " + gpuBufferSlice.length() + " bytes at offset " + gpuBufferSlice.offset() + " from " + glGpuBuffer.size + " size buffer)");
        }
        if (gpuBufferSlice2.offset() + gpuBufferSlice2.length() > glGpuBuffer2.size) {
            throw new IllegalArgumentException("Cannot copy more data than the target buffer can hold (attempting to copy " + gpuBufferSlice2.length() + " bytes at offset " + gpuBufferSlice2.offset() + " to " + glGpuBuffer2.size + " size buffer)");
        }
        this.backend.getBufferManager().method_72237(glGpuBuffer.id, glGpuBuffer2.id, gpuBufferSlice.offset(), gpuBufferSlice2.offset(), gpuBufferSlice.length());
    }

    @Override
    public void writeToTexture(GpuTexture gpuTexture, NativeImage nativeImage) {
        int i = gpuTexture.getWidth(0);
        int j = gpuTexture.getHeight(0);
        if (nativeImage.getWidth() != i || nativeImage.getHeight() != j) {
            throw new IllegalArgumentException("Cannot replace texture of size " + i + "x" + j + " with image of size " + nativeImage.getWidth() + "x" + nativeImage.getHeight());
        }
        if (gpuTexture.isClosed()) {
            throw new IllegalStateException("Destination texture is closed");
        }
        if ((gpuTexture.usage() & 1) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
        }
        this.writeToTexture(gpuTexture, nativeImage, 0, 0, 0, 0, i, j, 0, 0);
    }

    @Override
    public void writeToTexture(GpuTexture gpuTexture, NativeImage nativeImage, int i, int j, int k, int l, int m, int n, int o, int p) {
        int q;
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        if (i < 0 || i >= gpuTexture.getMipLevels()) {
            throw new IllegalArgumentException("Invalid mipLevel " + i + ", must be >= 0 and < " + gpuTexture.getMipLevels());
        }
        if (o + m > nativeImage.getWidth() || p + n > nativeImage.getHeight()) {
            throw new IllegalArgumentException("Copy source (" + nativeImage.getWidth() + "x" + nativeImage.getHeight() + ") is not large enough to read a rectangle of " + m + "x" + n + " from " + o + "x" + p);
        }
        if (k + m > gpuTexture.getWidth(i) || l + n > gpuTexture.getHeight(i)) {
            throw new IllegalArgumentException("Dest texture (" + m + "x" + n + ") is not large enough to write a rectangle of " + m + "x" + n + " at " + k + "x" + l + " (at mip level " + i + ")");
        }
        if (gpuTexture.isClosed()) {
            throw new IllegalStateException("Destination texture is closed");
        }
        if ((gpuTexture.usage() & 1) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
        }
        if (j >= gpuTexture.getDepthOrLayers()) {
            throw new UnsupportedOperationException("Depth or layer is out of range, must be >= 0 and < " + gpuTexture.getDepthOrLayers());
        }
        if ((gpuTexture.usage() & 0x10) != 0) {
            q = GlConst.CUBEMAP_TARGETS[j % 6];
            GL11.glBindTexture(34067, (int) ((GlTexture) gpuTexture).glId);
        } else {
            q = GlConst.GL_TEXTURE_2D;
            GlStateManager._bindTexture(((GlTexture) gpuTexture).glId);
        }
        GlStateManager._pixelStore(GlConst.GL_UNPACK_ROW_LENGTH, nativeImage.getWidth());
        GlStateManager._pixelStore(GlConst.GL_UNPACK_SKIP_PIXELS, o);
        GlStateManager._pixelStore(GlConst.GL_UNPACK_SKIP_ROWS, p);
        GlStateManager._pixelStore(GlConst.GL_UNPACK_ALIGNMENT, nativeImage.getFormat().getChannelCount());
        GlStateManager._texSubImage2D(q, i, k, l, m, n, GlConst.toGl(nativeImage.getFormat()), GlConst.GL_UNSIGNED_BYTE, nativeImage.imageId());
    }

    @Override
    public void writeToTexture(GpuTexture gpuTexture, IntBuffer intBuffer, NativeImage.Format format, int i, int j, int k, int l, int m, int n) {
        int o;
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        if (i < 0 || i >= gpuTexture.getMipLevels()) {
            throw new IllegalArgumentException("Invalid mipLevel, must be >= 0 and < " + gpuTexture.getMipLevels());
        }
        if (m * n > intBuffer.remaining()) {
            throw new IllegalArgumentException("Copy would overrun the source buffer (remaining length of " + intBuffer.remaining() + ", but copy is " + m + "x" + n + ")");
        }
        if (k + m > gpuTexture.getWidth(i) || l + n > gpuTexture.getHeight(i)) {
            throw new IllegalArgumentException("Dest texture (" + gpuTexture.getWidth(i) + "x" + gpuTexture.getHeight(i) + ") is not large enough to write a rectangle of " + m + "x" + n + " at " + k + "x" + l);
        }
        if (gpuTexture.isClosed()) {
            throw new IllegalStateException("Destination texture is closed");
        }
        if ((gpuTexture.usage() & 1) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_COPY_DST to be a destination for a write");
        }
        if (j >= gpuTexture.getDepthOrLayers()) {
            throw new UnsupportedOperationException("Depth or layer is out of range, must be >= 0 and < " + gpuTexture.getDepthOrLayers());
        }
        if ((gpuTexture.usage() & 0x10) != 0) {
            o = GlConst.CUBEMAP_TARGETS[j % 6];
            GL11.glBindTexture(34067, (int) ((GlTexture) gpuTexture).glId);
        } else {
            o = GlConst.GL_TEXTURE_2D;
            GlStateManager._bindTexture(((GlTexture) gpuTexture).glId);
        }
        GlStateManager._pixelStore(GlConst.GL_UNPACK_ROW_LENGTH, m);
        GlStateManager._pixelStore(GlConst.GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GlConst.GL_UNPACK_SKIP_ROWS, 0);
        GlStateManager._pixelStore(GlConst.GL_UNPACK_ALIGNMENT, format.getChannelCount());
        GlStateManager._texSubImage2D(o, i, k, l, m, n, GlConst.toGl(format), GlConst.GL_UNSIGNED_BYTE, intBuffer);
    }

    @Override
    public void copyTextureToBuffer(GpuTexture gpuTexture, GpuBuffer gpuBuffer, int i, Runnable runnable, int j) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        this.copyTextureToBuffer(gpuTexture, gpuBuffer, i, runnable, j, 0, 0, gpuTexture.getWidth(j), gpuTexture.getHeight(j));
    }

    @Override
    public void copyTextureToBuffer(GpuTexture gpuTexture, GpuBuffer gpuBuffer, int i, Runnable runnable, int j, int k, int l, int m, int n) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        if (j < 0 || j >= gpuTexture.getMipLevels()) {
            throw new IllegalArgumentException("Invalid mipLevel " + j + ", must be >= 0 and < " + gpuTexture.getMipLevels());
        }
        if (gpuTexture.getWidth(j) * gpuTexture.getHeight(j) * gpuTexture.getFormat().pixelSize() + i > gpuBuffer.size()) {
            throw new IllegalArgumentException("Buffer of size " + gpuBuffer.size() + " is not large enough to hold " + m + "x" + n + " pixels (" + gpuTexture.getFormat().pixelSize() + " bytes each) starting from offset " + i);
        }
        if ((gpuTexture.usage() & 2) == 0) {
            throw new IllegalArgumentException("Texture needs USAGE_COPY_SRC to be a source for a copy");
        }
        if ((gpuBuffer.usage() & GpuBuffer.USAGE_COPY_DST) == 0) {
            throw new IllegalArgumentException("Buffer needs USAGE_COPY_DST to be a destination for a copy");
        }
        if (k + m > gpuTexture.getWidth(j) || l + n > gpuTexture.getHeight(j)) {
            throw new IllegalArgumentException("Copy source texture (" + gpuTexture.getWidth(j) + "x" + gpuTexture.getHeight(j) + ") is not large enough to read a rectangle of " + m + "x" + n + " from " + k + "," + l);
        }
        if (gpuTexture.isClosed()) {
            throw new IllegalStateException("Source texture is closed");
        }
        if (gpuBuffer.isClosed()) {
            throw new IllegalStateException("Destination buffer is closed");
        }
        if (gpuTexture.getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
        }
        GlStateManager.clearGlErrors();
        this.backend.getBufferManager().setupFramebuffer(this.temporaryFb1, ((GlTexture) gpuTexture).getGlId(), 0, j, GlConst.GL_READ_FRAMEBUFFER);
        GlStateManager._glBindBuffer(GlConst.GL_PIXEL_PACK_BUFFER, ((GlGpuBuffer) gpuBuffer).id);
        GlStateManager._pixelStore(GlConst.GL_PACK_ROW_LENGTH, m);
        GlStateManager._readPixels(k, l, m, n, GlConst.toGlExternalId(gpuTexture.getFormat()), GlConst.toGlType(gpuTexture.getFormat()), i);
        RenderSystem.queueFencedTask(runnable);
        GlStateManager._glFramebufferTexture2D(GlConst.GL_READ_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GlConst.GL_TEXTURE_2D, 0, j);
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, 0);
        GlStateManager._glBindBuffer(GlConst.GL_PIXEL_PACK_BUFFER, 0);
        int o = GlStateManager._getError();
        if (o != 0) {
            throw new IllegalStateException("Couldn't perform copyTobuffer for texture " + gpuTexture.getLabel() + ": GL error " + o);
        }
    }

    @Override
    public void copyTextureToTexture(GpuTexture gpuTexture, GpuTexture gpuTexture2, int i, int j, int k, int l, int m, int n, int o) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        if (i < 0 || i >= gpuTexture.getMipLevels() || i >= gpuTexture2.getMipLevels()) {
            throw new IllegalArgumentException("Invalid mipLevel " + i + ", must be >= 0 and < " + gpuTexture.getMipLevels() + " and < " + gpuTexture2.getMipLevels());
        }
        if (j + n > gpuTexture2.getWidth(i) || k + o > gpuTexture2.getHeight(i)) {
            throw new IllegalArgumentException("Dest texture (" + gpuTexture2.getWidth(i) + "x" + gpuTexture2.getHeight(i) + ") is not large enough to write a rectangle of " + n + "x" + o + " at " + j + "x" + k);
        }
        if (l + n > gpuTexture.getWidth(i) || m + o > gpuTexture.getHeight(i)) {
            throw new IllegalArgumentException("Source texture (" + gpuTexture.getWidth(i) + "x" + gpuTexture.getHeight(i) + ") is not large enough to read a rectangle of " + n + "x" + o + " at " + l + "x" + m);
        }
        if (gpuTexture.isClosed()) {
            throw new IllegalStateException("Source texture is closed");
        }
        if (gpuTexture2.isClosed()) {
            throw new IllegalStateException("Destination texture is closed");
        }
        if ((gpuTexture.usage() & 2) == 0) {
            throw new IllegalArgumentException("Texture needs USAGE_COPY_SRC to be a source for a copy");
        }
        if ((gpuTexture2.usage() & 1) == 0) {
            throw new IllegalArgumentException("Texture needs USAGE_COPY_DST to be a destination for a copy");
        }
        if (gpuTexture.getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
        }
        if (gpuTexture2.getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for copying");
        }
        GlStateManager.clearGlErrors();
        GlStateManager._disableScissorTest();
        boolean bl = gpuTexture.getFormat().hasDepthAspect();
        int p = ((GlTexture) gpuTexture).getGlId();
        int q = ((GlTexture) gpuTexture2).getGlId();
        this.backend.getBufferManager().setupFramebuffer(this.temporaryFb1, bl ? 0 : p, bl ? p : 0, 0, 0);
        this.backend.getBufferManager().setupFramebuffer(this.temporaryFb2, bl ? 0 : q, bl ? q : 0, 0, 0);
        this.backend.getBufferManager().setupBlitFramebuffer(this.temporaryFb1, this.temporaryFb2, l, m, n, o, j, k, n, o, bl ? GlConst.GL_DEPTH_BUFFER_BIT : GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        int r = GlStateManager._getError();
        if (r != 0) {
            throw new IllegalStateException("Couldn't perform copyToTexture for texture " + gpuTexture.getLabel() + " to " + gpuTexture2.getLabel() + ": GL error " + r);
        }
    }

    @Override
    public void presentTexture(GpuTextureView gpuTextureView) {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        if (!gpuTextureView.com_mojang_blaze3d_textures_GpuTexture_texture().getFormat().hasColorAspect()) {
            throw new IllegalStateException("Cannot present a non-color texture!");
        }
        if ((gpuTextureView.com_mojang_blaze3d_textures_GpuTexture_texture().usage() & 8) == 0) {
            throw new IllegalStateException("Color texture must have USAGE_RENDER_ATTACHMENT to presented to the screen");
        }
        if (gpuTextureView.com_mojang_blaze3d_textures_GpuTexture_texture().getDepthOrLayers() > 1) {
            throw new UnsupportedOperationException("Textures with multiple depths or layers are not yet supported for presentation");
        }
        GlStateManager._disableScissorTest();
        GlStateManager._viewport(0, 0, gpuTextureView.getWidth(0), gpuTextureView.getHeight(0));
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
        this.backend.getBufferManager().setupFramebuffer(this.temporaryFb2, ((GlTexture) gpuTextureView.com_mojang_blaze3d_textures_GpuTexture_texture()).getGlId(), 0, 0, 0);
        this.backend.getBufferManager().setupBlitFramebuffer(this.temporaryFb2, 0, 0, 0, gpuTextureView.getWidth(0), gpuTextureView.getHeight(0), 0, 0, gpuTextureView.getWidth(0), gpuTextureView.getHeight(0), GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    @Override
    public GpuFence createFence() {
        if (this.renderPassOpen) {
            throw new IllegalStateException("Close the existing render pass before performing additional commands");
        }
        return new GlGpuFence();
    }

    protected <T> void drawObjectsWithRenderPass(RenderPassImpl pass, Collection<
                    RenderPass.RenderObject<T>> objects, @Nullable GpuBuffer indexBuffer, @Nullable
                    VertexFormat.IndexType indexType, Collection<
                    String> validationSkippedUniforms, T object) {
        if (!this.setupRenderPass(pass, validationSkippedUniforms)) {
            return;
        }
        if (indexType == null) {
            indexType = VertexFormat.IndexType.SHORT;
        }
        for (RenderPass.RenderObject<T> renderObject : objects) {
            BiConsumer<T, RenderPass.UniformUploader> biConsumer;
            VertexFormat.IndexType indexType2 = renderObject.indexType() == null ? indexType : renderObject.indexType();
            pass.setIndexBuffer(renderObject.indexBuffer() == null ? indexBuffer : renderObject.indexBuffer(), indexType2);
            pass.setVertexBuffer(renderObject.slot(), renderObject.vertexBuffer());
            if (RenderPassImpl.IS_DEVELOPMENT) {
                if (pass.indexBuffer == null) {
                    throw new IllegalStateException("Missing index buffer");
                }
                if (pass.indexBuffer.isClosed()) {
                    throw new IllegalStateException("Index buffer has been closed!");
                }
                if (pass.vertexBuffers[0] == null) {
                    throw new IllegalStateException("Missing vertex buffer at slot 0");
                }
                if (pass.vertexBuffers[0].isClosed()) {
                    throw new IllegalStateException("Vertex buffer at slot 0 has been closed!");
                }
            }
            if ((biConsumer = renderObject.uniformUploaderConsumer()) != null) {
                biConsumer.accept(object, (name, gpuBufferSlice) -> {
                    GlUniform glUniform = renderPassImpl.pipeline.program().getUniform(name);
                    if (glUniform instanceof GlUniform.UniformBuffer) {
                        int j;
                        GlUniform.UniformBuffer uniformBuffer = (GlUniform.UniformBuffer) glUniform;
                        try {
                            int i;
                            j = i = uniformBuffer.blockBinding();
                        } catch (Throwable throwable) {
                            throw new MatchException(throwable.toString(), throwable);
                        }
                        GL32.glBindBufferRange((int) GlConst.GL_UNIFORM_BUFFER, (int) j, (int) ((GlGpuBuffer) gpuBufferSlice.buffer()).id, (long) gpuBufferSlice.offset(), (long) gpuBufferSlice.length());
                    }
                });
            }
            this.drawObjectWithRenderPass(pass, 0, renderObject.firstIndex(), renderObject.indexCount(), indexType2, pass.pipeline, 1);
        }
    }

    protected void drawBoundObjectWithRenderPass(RenderPassImpl pass, int baseVertex, int firstIndex, int count, @Nullable
                    VertexFormat.IndexType indexType, int instanceCount) {
        if (!this.setupRenderPass(pass, Collections.emptyList())) {
            return;
        }
        if (RenderPassImpl.IS_DEVELOPMENT) {
            if (indexType != null) {
                if (pass.indexBuffer == null) {
                    throw new IllegalStateException("Missing index buffer");
                }
                if (pass.indexBuffer.isClosed()) {
                    throw new IllegalStateException("Index buffer has been closed!");
                }
                if ((pass.indexBuffer.usage() & GpuBuffer.USAGE_INDEX) == 0) {
                    throw new IllegalStateException("Index buffer must have GpuBuffer.USAGE_INDEX!");
                }
            }
            if (pass.vertexBuffers[0] == null) {
                throw new IllegalStateException("Missing vertex buffer at slot 0");
            }
            if (pass.vertexBuffers[0].isClosed()) {
                throw new IllegalStateException("Vertex buffer at slot 0 has been closed!");
            }
            if ((pass.vertexBuffers[0].usage() & GpuBuffer.USAGE_VERTEX) == 0) {
                throw new IllegalStateException("Vertex buffer must have GpuBuffer.USAGE_VERTEX!");
            }
        }
        this.drawObjectWithRenderPass(pass, baseVertex, firstIndex, count, indexType, pass.pipeline, instanceCount);
    }

    private void drawObjectWithRenderPass(RenderPassImpl pass, int baseVertex, int firstIndex, int count, @Nullable
                    VertexFormat.IndexType indexType, CompiledShaderPipeline pipeline, int instanceCount) {
        this.backend.getVertexBufferManager().setupBuffer(pipeline.info().getVertexFormat(), (GlGpuBuffer) pass.vertexBuffers[
                0]);
        if (indexType != null) {
            GlStateManager._glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, ((GlGpuBuffer) pass.indexBuffer).id);
            if (instanceCount > 1) {
                if (baseVertex > 0) {
                    GL32.glDrawElementsInstancedBaseVertex((int) GlConst.toGl(pipeline.info().getVertexFormatMode()), (int) count, (int) GlConst.toGl(indexType), (long) ((long) firstIndex * (long) indexType.size), (int) instanceCount, (int) baseVertex);
                } else {
                    GL31.glDrawElementsInstanced((int) GlConst.toGl(pipeline.info().getVertexFormatMode()), (int) count, (int) GlConst.toGl(indexType), (long) ((long) firstIndex * (long) indexType.size), (int) instanceCount);
                }
            } else if (baseVertex > 0) {
                GL32.glDrawElementsBaseVertex((int) GlConst.toGl(pipeline.info().getVertexFormatMode()), (int) count, (int) GlConst.toGl(indexType), (long) ((long) firstIndex * (long) indexType.size), (int) baseVertex);
            } else {
                GlStateManager._drawElements(GlConst.toGl(pipeline.info().getVertexFormatMode()), count, GlConst.toGl(indexType), (long) firstIndex * (long) indexType.size);
            }
        } else if (instanceCount > 1) {
            GL31.glDrawArraysInstanced((int) GlConst.toGl(pipeline.info().getVertexFormatMode()), (int) baseVertex, (int) count, (int) instanceCount);
        } else {
            GlStateManager._drawArrays(GlConst.toGl(pipeline.info().getVertexFormatMode()), baseVertex, count);
        }
    }

    /*
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    private boolean setupRenderPass(RenderPassImpl pass, Collection<
                    String> validationSkippedUniforms) {
        try {
            if (RenderPassImpl.IS_DEVELOPMENT) {
                if (pass.pipeline == null) {
                    throw new IllegalStateException("Can't draw without a render pipeline");
                }
                if (pass.pipeline.program() == ShaderProgram.INVALID) {
                    throw new IllegalStateException("Pipeline contains invalid shader program");
                }
                for (RenderPipeline.UniformDescription uniformDescription : pass.pipeline.info().getUniforms()) {
                    gpuBufferSlice = pass.simpleUniforms.get(uniformDescription.name());
                    if (validationSkippedUniforms.contains(uniformDescription.name())) continue;
                    if (gpuBufferSlice == null) {
                        throw new IllegalStateException("Missing uniform " + uniformDescription.name() + " (should be " + String.valueOf((Object) uniformDescription.type()) + ")");
                    }
                    if (uniformDescription.type() == UniformType.UNIFORM_BUFFER) {
                        if (gpuBufferSlice.buffer().isClosed()) {
                            throw new IllegalStateException("Uniform buffer " + uniformDescription.name() + " is already closed");
                        }
                        if ((gpuBufferSlice.buffer().usage() & GpuBuffer.USAGE_UNIFORM) == 0) {
                            throw new IllegalStateException("Uniform buffer " + uniformDescription.name() + " must have GpuBuffer.USAGE_UNIFORM");
                        }
                    }
                    if (uniformDescription.type() != UniformType.TEXEL_BUFFER) continue;
                    if (gpuBufferSlice.offset() != 0 || gpuBufferSlice.length() != gpuBufferSlice.buffer().size()) {
                        throw new IllegalStateException("Uniform texel buffers do not support a slice of a buffer, must be entire buffer");
                    }
                    if (uniformDescription.textureFormat() != null) continue;
                    throw new IllegalStateException("Invalid uniform texel buffer " + uniformDescription.name() + " (missing a texture format)");
                }
                for (Map.Entry entry : pass.pipeline.program().getUniforms().entrySet()) {
                    if (!(entry.getValue() instanceof GlUniform.Sampler)) continue;
                    string = (String) entry.getKey();
                    glTextureView = (GlTextureView) pass.samplerUniforms.get(string);
                    if (glTextureView == null) {
                        throw new IllegalStateException("Missing sampler " + string);
                    }
                    if (glTextureView.isClosed()) {
                        throw new IllegalStateException("Sampler " + string + " (" + glTextureView.net_minecraft_client_texture_GlTexture_texture().getLabel() + ") has been closed!");
                    }
                    if ((glTextureView.net_minecraft_client_texture_GlTexture_texture().usage() & 4) != 0)
                        continue;
                    throw new IllegalStateException("Sampler " + string + " (" + glTextureView.net_minecraft_client_texture_GlTexture_texture().getLabel() + ") must have USAGE_TEXTURE_BINDING!");
                }
                if (pass.pipeline.info().wantsDepthTexture() && !pass.hasDepth()) {
                    GlCommandEncoder.LOGGER.warn("Render pipeline {} wants a depth texture but none was provided - this is probably a bug", (Object) pass.pipeline.info().getLocation());
                }
            } else if (pass.pipeline == null || pass.pipeline.program() == ShaderProgram.INVALID) {
                return false;
            }
            renderPipeline = pass.pipeline.info();
            shaderProgram = pass.pipeline.program();
            this.setPipelineAndApplyState(renderPipeline);
            v0 = bl = this.currentProgram != shaderProgram;
            if (bl) {
                GlStateManager._glUseProgram(shaderProgram.getGlRef());
                this.currentProgram = shaderProgram;
            }
            block15:
            for (Map.Entry<String, GlUniform> entry2 : shaderProgram.getUniforms().entrySet()) {
                string2 = entry2.getKey();
                bl2 = pass.setSimpleUniforms.contains(string2);
                Objects.requireNonNull(entry2.getValue());
                var11_13 = 0;
                switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object
                        []{GlUniform.UniformBuffer.class, GlUniform.TexelBuffer.class, GlUniform.Sampler.class}, (Object) var10_12, var11_13)) {
                    default:
                        {
                            throw new MatchException(null, null);
                        }
                    case 0:
                        {
                            var12_14 = (GlUniform.UniformBuffer) var10_12;
                            i = var14_16 = var12_14.blockBinding();
                            if (!bl2) continue block15;
                            gpuBufferSlice2 = pass.simpleUniforms.get(string2);
                            GL32.glBindBufferRange((int) GlConst.GL_UNIFORM_BUFFER, (int) i, (int) ((GlGpuBuffer) gpuBufferSlice2.buffer()).id, (long) gpuBufferSlice2.offset(), (long) gpuBufferSlice2.length());
                            continue block15;
                        }
                    case 1:
                        {
                            var14_18 = (GlUniform.TexelBuffer) var10_12;
                            j = var19_23 = var14_18.location();
                            k = var19_23 = var14_18.samplerIndex();
                            textureFormat = var19_24 = var14_18.format();
                            l = var19_25 = var14_18.texture();
                            if (bl || bl2) {
                                GlStateManager._glUniform1i(j, k);
                            }
                            GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + k);
                            GL11C.glBindTexture((int) 35882, (int) l);
                            if (!bl2) continue block15;
                            gpuBufferSlice3 = pass.simpleUniforms.get(string2);
                            GL31.glTexBuffer((int) 35882, (int) GlConst.toGlInternalId(textureFormat), (int) ((GlGpuBuffer) gpuBufferSlice3.buffer()).id);
                            continue block15;
                        }
                    case 2:
                }
                var19_27 = (GlUniform.Sampler) var10_12;
                m = var22_31 = var19_27.location();
                n = var22_31 = var19_27.samplerIndex();
                glTextureView2 = (GlTextureView) pass.samplerUniforms.get(string2);
                if (glTextureView2 == null) continue;
                if (bl || bl2) {
                    GlStateManager._glUniform1i(m, n);
                }
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + n);
                glTexture = glTextureView2.net_minecraft_client_texture_GlTexture_texture();
                if ((glTexture.usage() & 16) != 0) {
                    o = 34067;
                    GL11.glBindTexture((int) 34067, (int) glTexture.glId);
                } else {
                    o = GlConst.GL_TEXTURE_2D;
                    GlStateManager._bindTexture(glTexture.glId);
                }
                GlStateManager._texParameter(o, 33084, glTextureView2.baseMipLevel());
                GlStateManager._texParameter(o, GL12.GL_TEXTURE_MAX_LEVEL, glTextureView2.baseMipLevel() + glTextureView2.mipLevels() - 1);
                glTexture.checkDirty(o);
            }
            pass.setSimpleUniforms.clear();
            if (pass.isScissorEnabled()) {
                GlStateManager._enableScissorTest();
                GlStateManager._scissorBox(pass.getScissorX(), pass.getScissorY(), pass.getScissorWidth(), pass.getScissorHeight());
            } else {
                GlStateManager._disableScissorTest();
            }
            return true;
        } catch (Throwable var6_8) {
            throw new MatchException(var6_8.toString(), var6_8);
        }
    }

    private void setPipelineAndApplyState(RenderPipeline pipeline) {
        if (this.currentPipeline == pipeline) {
            return;
        }
        this.currentPipeline = pipeline;
        if (pipeline.getDepthTestFunction() != DepthTestFunction.NO_DEPTH_TEST) {
            GlStateManager._enableDepthTest();
            GlStateManager._depthFunc(GlConst.toGl(pipeline.getDepthTestFunction()));
        } else {
            GlStateManager._disableDepthTest();
        }
        if (pipeline.isCull()) {
            GlStateManager._enableCull();
        } else {
            GlStateManager._disableCull();
        }
        if (pipeline.getBlendFunction().isPresent()) {
            GlStateManager._enableBlend();
            BlendFunction blendFunction = pipeline.getBlendFunction().get();
            GlStateManager._blendFuncSeparate(GlConst.toGl(blendFunction.sourceColor()), GlConst.toGl(blendFunction.destColor()), GlConst.toGl(blendFunction.sourceAlpha()), GlConst.toGl(blendFunction.destAlpha()));
        } else {
            GlStateManager._disableBlend();
        }
        GlStateManager._polygonMode(GlConst.GL_FRONT_AND_BACK, GlConst.toGl(pipeline.getPolygonMode()));
        GlStateManager._depthMask(pipeline.isWriteDepth());
        GlStateManager._colorMask(pipeline.isWriteColor(), pipeline.isWriteColor(), pipeline.isWriteColor(), pipeline.isWriteAlpha());
        if (pipeline.getDepthBiasConstant() != 0.0f || pipeline.getDepthBiasScaleFactor() != 0.0f) {
            GlStateManager._polygonOffset(pipeline.getDepthBiasScaleFactor(), pipeline.getDepthBiasConstant());
            GlStateManager._enablePolygonOffset();
        } else {
            GlStateManager._disablePolygonOffset();
        }
        switch (pipeline.getColorLogic()) {
            case NONE:
                {
                    GlStateManager._disableColorLogicOp();
                    break;
                }
            case OR_REVERSE:
                {
                    GlStateManager._enableColorLogicOp();
                    GlStateManager._logicOp(GL11.GL_OR_REVERSE);
                }
        }
    }

    public void closePass() {
        this.renderPassOpen = false;
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
        this.backend.getDebugLabelManager().popDebugGroup();
    }

    protected GlBackend getBackend() {
        return this.backend;
    }
}
