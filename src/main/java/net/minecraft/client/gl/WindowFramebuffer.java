/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.TextureAllocationException;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WindowFramebuffer
extends Framebuffer {
    final static public int DEFAULT_WIDTH = 854;
    final static public int DEFAULT_HEIGHT = 480;
    final static Size DEFAULT = new Size(854, 480);

    public WindowFramebuffer(int width, int height) {
        super("Main", true);
        this.init(width, height);
    }

    private void init(int width, int height) {
        Size size = this.findSuitableSize(width, height);
        if (this.colorAttachment == null || this.depthAttachment == null) {
            throw new IllegalStateException("Missing color and/or depth textures");
        }
        this.colorAttachment.setTextureFilter(FilterMode.NEAREST, false);
        this.colorAttachment.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        this.colorAttachment.setTextureFilter(FilterMode.NEAREST, false);
        this.colorAttachment.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        this.viewportWidth = size.width;
        this.viewportHeight = size.height;
        this.textureWidth = size.width;
        this.textureHeight = size.height;
    }

    private Size findSuitableSize(int width, int height) {
        RenderSystem.assertOnRenderThread();
        for (Size size : Size.findCompatible(width, height)) {
            if (this.colorAttachment != null) {
                this.colorAttachment.close();
                this.colorAttachment = null;
            }
            if (this.colorAttachmentView != null) {
                this.colorAttachmentView.close();
                this.colorAttachmentView = null;
            }
            if (this.depthAttachment != null) {
                this.depthAttachment.close();
                this.depthAttachment = null;
            }
            if (this.depthAttachmentView != null) {
                this.depthAttachmentView.close();
                this.depthAttachmentView = null;
            }
            this.colorAttachment = this.createColorAttachment(size);
            this.depthAttachment = this.createDepthAttachment(size);
            if (this.colorAttachment == null || this.depthAttachment == null) continue;
            this.colorAttachmentView = RenderSystem.getDevice().createTextureView(this.colorAttachment);
            this.depthAttachmentView = RenderSystem.getDevice().createTextureView(this.depthAttachment);
            return size;
        }
        throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (" + (this.colorAttachment == null ? "missing color" : "have color") + ", " + (this.depthAttachment == null ? "missing depth" : "have depth") + ")");
    }

    @Nullable
    private GpuTexture createColorAttachment(Size size) {
        try {
            return RenderSystem.getDevice().createTexture(() -> this.name + " / Color", 15, TextureFormat.RGBA8, size.width, size.height, 1, 1);
        }
        catch (TextureAllocationException textureAllocationException) {
            return null;
        }
    }

    @Nullable
    private GpuTexture createDepthAttachment(Size size) {
        try {
            return RenderSystem.getDevice().createTexture(() -> this.name + " / Depth", 15, TextureFormat.DEPTH32, size.width, size.height, 1, 1);
        }
        catch (TextureAllocationException textureAllocationException) {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Size {
        final public int width;
        final public int height;

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        static List<Size> findCompatible(int width, int height) {
            RenderSystem.assertOnRenderThread();
            int i = RenderSystem.getDevice().getMaxTextureSize();
            if (width <= 0 || width > i || height <= 0 || height > i) {
                return ImmutableList.of((Object)DEFAULT);
            }
            return ImmutableList.of((Object)new Size(width, height), (Object)DEFAULT);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Size size = (Size)o;
            return this.width == size.width && this.height == size.height;
        }

        public int hashCode() {
            return Objects.hash(this.width, this.height);
        }

        public String toString() {
            return this.width + "x" + this.height;
        }
    }
}

