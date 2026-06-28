/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.GlTexture;

@Environment(value=EnvType.CLIENT)
public class GlTextureView
extends GpuTextureView {
    private boolean closed;

    protected GlTextureView(GlTexture texture, int baseMipLevel, int mipLevels) {
        super(texture, baseMipLevel, mipLevels);
        texture.incrementRefCount();
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.net_minecraft_client_texture_GlTexture_texture().decrementRefCount();
        }
    }

    @Override
    public GlTexture net_minecraft_client_texture_GlTexture_texture() {
        return (GlTexture)super.com_mojang_blaze3d_textures_GpuTexture_texture();
    }

    @Override
    public GpuTexture com_mojang_blaze3d_textures_GpuTexture_texture() {
        return this.net_minecraft_client_texture_GlTexture_texture();
    }
}

