/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public sealed interface GlUniform
extends AutoCloseable {
    @Override
    default public void close() {
    }

    @Environment(value=EnvType.CLIENT)
    public record Sampler(int location, int samplerIndex) implements GlUniform
    {
    }

    @Environment(value=EnvType.CLIENT)
    public record TexelBuffer(int location, int samplerIndex, TextureFormat format, int texture) implements GlUniform
    {
        public TexelBuffer(int location, int samplerIndex, TextureFormat format) {
            this(location, samplerIndex, format, GlStateManager._genTexture());
        }

        @Override
        public void close() {
            GlStateManager._deleteTexture(this.texture);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record UniformBuffer(int blockBinding) implements GlUniform
    {
    }
}

