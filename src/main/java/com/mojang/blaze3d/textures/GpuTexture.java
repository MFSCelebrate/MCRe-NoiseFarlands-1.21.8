/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.textures;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public abstract class GpuTexture
implements AutoCloseable {
    final static public int USAGE_COPY_DST = 1;
    final static public int USAGE_COPY_SRC = 2;
    final static public int USAGE_TEXTURE_BINDING = 4;
    final static public int USAGE_RENDER_ATTACHMENT = 8;
    final static public int USAGE_CUBEMAP_COMPATIBLE = 16;
    final private TextureFormat format;
    final private int width;
    final private int height;
    final private int depthOrLayers;
    final private int mipLevels;
    final private int usage;
    final private String label;
    protected AddressMode addressModeU = AddressMode.REPEAT;
    protected AddressMode addressModeV = AddressMode.REPEAT;
    protected FilterMode minFilter = FilterMode.NEAREST;
    protected FilterMode magFilter = FilterMode.LINEAR;
    protected boolean useMipmaps = true;

    public GpuTexture(int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels) {
        this.usage = usage;
        this.label = label;
        this.format = format;
        this.width = width;
        this.height = height;
        this.depthOrLayers = depthOrLayers;
        this.mipLevels = mipLevels;
    }

    public int getWidth(int mipLevel) {
        return this.width >> mipLevel;
    }

    public int getHeight(int mipLevel) {
        return this.height >> mipLevel;
    }

    public int getDepthOrLayers() {
        return this.depthOrLayers;
    }

    public int getMipLevels() {
        return this.mipLevels;
    }

    public TextureFormat getFormat() {
        return this.format;
    }

    public int usage() {
        return this.usage;
    }

    public void setAddressMode(AddressMode addressMode) {
        this.setAddressMode(addressMode, addressMode);
    }

    public void setAddressMode(AddressMode addressModeU, AddressMode addressModeV) {
        this.addressModeU = addressModeU;
        this.addressModeV = addressModeV;
    }

    public void setTextureFilter(FilterMode filter, boolean useMipmaps) {
        this.setTextureFilter(filter, filter, useMipmaps);
    }

    public void setTextureFilter(FilterMode minFilter, FilterMode magFilter, boolean useMipmaps) {
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.setUseMipmaps(useMipmaps);
    }

    public void setUseMipmaps(boolean useMipmaps) {
        this.useMipmaps = useMipmaps;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public abstract void close();

    public abstract boolean isClosed();
}

