/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractTexture
implements AutoCloseable {
    @Nullable
    protected GpuTexture glTexture;
    @Nullable
    protected GpuTextureView glTextureView;

    public void setClamp(boolean clamp) {
        if (this.glTexture == null) {
            throw new IllegalStateException("Texture does not exist, can't change its clamp before something initializes it");
        }
        this.glTexture.setAddressMode(clamp ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT);
    }

    public void setFilter(boolean bilinear, boolean mipmap) {
        if (this.glTexture == null) {
            throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
        }
        this.glTexture.setTextureFilter(bilinear ? FilterMode.LINEAR : FilterMode.NEAREST, mipmap);
    }

    public void setUseMipmaps(boolean useMipmaps) {
        if (this.glTexture == null) {
            throw new IllegalStateException("Texture does not exist, can't get change its filter before something initializes it");
        }
        this.glTexture.setUseMipmaps(useMipmaps);
    }

    @Override
    public void close() {
        if (this.glTexture != null) {
            this.glTexture.close();
            this.glTexture = null;
        }
        if (this.glTextureView != null) {
            this.glTextureView.close();
            this.glTextureView = null;
        }
    }

    public GpuTexture getGlTexture() {
        if (this.glTexture == null) {
            throw new IllegalStateException("Texture does not exist, can't get it before something initializes it");
        }
        return this.glTexture;
    }

    public GpuTextureView getGlTextureView() {
        if (this.glTextureView == null) {
            throw new IllegalStateException("Texture view does not exist, can't get it before something initializes it");
        }
        return this.glTextureView;
    }
}

