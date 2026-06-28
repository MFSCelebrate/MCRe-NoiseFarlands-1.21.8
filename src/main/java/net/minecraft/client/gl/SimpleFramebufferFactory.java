/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.ClosableFactory;

@Environment(value=EnvType.CLIENT)
public record SimpleFramebufferFactory(int width, int height, boolean useDepth, int clearColor) implements ClosableFactory<Framebuffer>
{
    @Override
    public Framebuffer net_minecraft_client_gl_Framebuffer_create() {
        return new SimpleFramebuffer(null, this.width, this.height, this.useDepth);
    }

    @Override
    public void prepare(Framebuffer framebuffer) {
        if (this.useDepth) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), this.clearColor, framebuffer.getDepthAttachment(), 1.0);
        } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(framebuffer.getColorAttachment(), this.clearColor);
        }
    }

    @Override
    public void close(Framebuffer framebuffer) {
        framebuffer.delete();
    }

    @Override
    public boolean equals(ClosableFactory<?> factory) {
        if (factory instanceof SimpleFramebufferFactory) {
            SimpleFramebufferFactory simpleFramebufferFactory = (SimpleFramebufferFactory)factory;
            return this.width == simpleFramebufferFactory.width && this.height == simpleFramebufferFactory.height && this.useDepth == simpleFramebufferFactory.useDepth;
        }
        return false;
    }

    @Override
    public void close(Object value) {
        this.close((Framebuffer)value);
    }

    @Override
    public void prepare(Object value) {
        this.prepare((Framebuffer)value);
    }

    @Override
    public Object java_lang_Object_create() {
        return this.net_minecraft_client_gl_Framebuffer_create();
    }
}

