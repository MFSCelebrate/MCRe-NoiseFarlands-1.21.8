/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public abstract class GpuTextureView
implements AutoCloseable {
    final private GpuTexture texture;
    final private int baseMipLevel;
    final private int mipLevels;

    public GpuTextureView(GpuTexture texture, int baseMipLevel, int mipLevels) {
        this.texture = texture;
        this.baseMipLevel = baseMipLevel;
        this.mipLevels = mipLevels;
    }

    @Override
    public abstract void close();

    public GpuTexture com_mojang_blaze3d_textures_GpuTexture_texture() {
        return this.texture;
    }

    public int baseMipLevel() {
        return this.baseMipLevel;
    }

    public int mipLevels() {
        return this.mipLevels;
    }

    public int getWidth(int mipLevel) {
        return this.texture.getWidth(mipLevel + this.baseMipLevel);
    }

    public int getHeight(int mipLevel) {
        return this.texture.getHeight(mipLevel + this.baseMipLevel);
    }

    public abstract boolean isClosed();
}

