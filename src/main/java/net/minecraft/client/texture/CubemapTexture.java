/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureContents;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CubemapTexture
extends ReloadableTexture {
    final static private String[] TEXTURE_SUFFIXES = new String[]{"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};

    public CubemapTexture(Identifier identifier) {
        super(identifier);
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        Identifier identifier = this.getId();
        TextureContents textureContents = TextureContents.load(resourceManager, identifier.withSuffixedPath(TEXTURE_SUFFIXES[0]));
        try {
            int i = textureContents.image().getWidth();
            int j = textureContents.image().getHeight();
            NativeImage nativeImage = new NativeImage(1, j * 6, false);
            textureContents.image().copyRect(nativeImage, 0, 0, 0, 0, 1, j, false, true);
            for (int k = 1; k < 6; ++k) {
                TextureContents textureContents2 = TextureContents.load(resourceManager, identifier.withSuffixedPath(TEXTURE_SUFFIXES[k]));
                try {
                    if (textureContents2.image().getWidth() != 1 || textureContents2.image().getHeight() != j) {
                        throw new IOException("Image dimensions of cubemap '" + String.valueOf(identifier) + "' sides do not match: part 0 is " + 1 + "x" + j + ", but part " + k + " is " + textureContents2.image().getWidth() + "x" + textureContents2.image().getHeight());
                    }
                    textureContents2.image().copyRect(nativeImage, 0, 0, 0, k * j, 1, j, false, true);
                    if (textureContents2 == null) continue;
                    textureContents2.close();
                    continue;
                }
                catch (Throwable throwable) {
                    if (textureContents2 != null) {
                        try {
                            textureContents2.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
            }
            TextureContents textureContents2 = new TextureContents(nativeImage, new TextureResourceMetadata(true, false));
            if (textureContents != null) {
                textureContents.close();
            }
            return textureContents2;
        }
        catch (Throwable throwable) {
            if (textureContents != null) {
                try {
                    textureContents.close();
                }
                catch (Throwable throwable3) {
                    throwable.addSuppressed(throwable3);
                }
            }
            throw throwable;
        }
    }

    @Override
    protected void load(NativeImage image, boolean blur, boolean clamp) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        int i = image.getWidth();
        int j = image.getHeight() / 6;
        this.close();
        this.glTexture = gpuDevice.createTexture(this.getId()::toString, 21, TextureFormat.RGBA8, i, j, 6, 1);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
        this.setFilter(blur, false);
        this.setClamp(clamp);
        for (int k = 0; k < 6; ++k) {
            gpuDevice.createCommandEncoder().writeToTexture(this.glTexture, image, 0, k, 0, 0, i, j, 0, j * k);
        }
    }
}

