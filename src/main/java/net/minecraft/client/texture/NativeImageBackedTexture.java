/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class NativeImageBackedTexture
extends AbstractTexture
implements DynamicTexture {
    final static private Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private NativeImage image;

    public NativeImageBackedTexture(Supplier<String> nameSupplier, NativeImage image) {
        this.image = image;
        this.createTexture(nameSupplier);
        this.upload();
    }

    public NativeImageBackedTexture(String name, int width, int height, boolean useStb) {
        this.image = new NativeImage(width, height, useStb);
        this.createTexture(name);
    }

    public NativeImageBackedTexture(Supplier<String> nameSupplier, int width, int height, boolean useStb) {
        this.image = new NativeImage(width, height, useStb);
        this.createTexture(nameSupplier);
    }

    private void createTexture(Supplier<String> nameSupplier) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.glTexture = gpuDevice.createTexture(nameSupplier, 5, TextureFormat.RGBA8, this.image.getWidth(), this.image.getHeight(), 1, 1);
        this.glTexture.setTextureFilter(FilterMode.NEAREST, false);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
    }

    private void createTexture(String name) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.glTexture = gpuDevice.createTexture(name, 5, TextureFormat.RGBA8, this.image.getWidth(), this.image.getHeight(), 1, 1);
        this.glTexture.setTextureFilter(FilterMode.NEAREST, false);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
    }

    public void upload() {
        if (this.image != null && this.glTexture != null) {
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.glTexture, this.image);
        } else {
            LOGGER.warn("Trying to upload disposed texture {}", (Object)this.getGlTexture().getLabel());
        }
    }

    @Nullable
    public NativeImage getImage() {
        return this.image;
    }

    public void setImage(NativeImage image) {
        if (this.image != null) {
            this.image.close();
        }
        this.image = image;
    }

    @Override
    public void close() {
        if (this.image != null) {
            this.image.close();
            this.image = null;
        }
        super.close();
    }

    @Override
    public void save(Identifier id, Path path) throws IOException {
        if (this.image != null) {
            String string = id.toUnderscoreSeparatedString() + ".png";
            Path path2 = path.resolve(string);
            this.image.writeTo(path2);
        }
    }
}

