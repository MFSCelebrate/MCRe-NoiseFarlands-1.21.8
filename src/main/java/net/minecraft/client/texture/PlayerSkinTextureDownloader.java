/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.path.PathUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinTextureDownloader {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int SKIN_WIDTH = 64;
    final static private int SKIN_HEIGHT = 64;
    final static private int OLD_SKIN_HEIGHT = 32;

    public static CompletableFuture<Identifier> downloadAndRegisterTexture(Identifier textureId, Path path, String uri, boolean remap) {
        return CompletableFuture.supplyAsync(() -> {
            NativeImage nativeImage;
            try {
                nativeImage = PlayerSkinTextureDownloader.download(path, uri);
            }
            catch (IOException iOException) {
                throw new UncheckedIOException(iOException);
            }
            return remap ? PlayerSkinTextureDownloader.remapTexture(nativeImage, uri) : nativeImage;
        }, Util.getDownloadWorkerExecutor().named("downloadTexture")).thenCompose(image -> PlayerSkinTextureDownloader.registerTexture(textureId, image));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static NativeImage download(Path path, String uri) throws IOException {
        if (Files.isRegularFile(path, new LinkOption[0])) {
            NativeImage nativeImage;
            block13: {
                LOGGER.debug("Loading HTTP texture from local cache ({})", (Object)path);
                InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);
                try {
                    nativeImage = NativeImage.read(inputStream);
                    if (inputStream == null) break block13;
                }
                catch (Throwable throwable) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                inputStream.close();
            }
            return nativeImage;
        }
        HttpURLConnection httpURLConnection = null;
        LOGGER.debug("Downloading HTTP texture from {} to {}", (Object)uri, (Object)path);
        URI uRI = URI.create(uri);
        try {
            httpURLConnection = (HttpURLConnection)uRI.toURL().openConnection(MinecraftClient.getInstance().getNetworkProxy());
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.connect();
            int i = httpURLConnection.getResponseCode();
            if (i / 100 != 2) {
                throw new IOException("Failed to open " + String.valueOf(uRI) + ", HTTP error code: " + i);
            }
            byte[] bs = httpURLConnection.getInputStream().readAllBytes();
            try {
                PathUtil.createDirectories(path.getParent());
                Files.write(path, bs, new OpenOption[0]);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to cache texture {} in {}", (Object)uri, (Object)path);
            }
            NativeImage nativeImage = NativeImage.read(bs);
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return nativeImage;
        }
        catch (Throwable throwable) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            throw throwable;
        }
    }

    private static CompletableFuture<Identifier> registerTexture(Identifier textureId, NativeImage image) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return CompletableFuture.supplyAsync(() -> {
            NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(textureId::toString, image);
            minecraftClient.getTextureManager().registerTexture(textureId, nativeImageBackedTexture);
            return textureId;
        }, minecraftClient);
    }

    private static NativeImage remapTexture(NativeImage image, String uri) {
        boolean bl;
        int i = image.getHeight();
        int j = image.getWidth();
        if (j != 64 || i != 32 && i != 64) {
            image.close();
            throw new IllegalStateException("Discarding incorrectly sized (" + j + "x" + i + ") skin texture from " + uri);
        }
        boolean bl2 = bl = i == 32;
        if (bl) {
            NativeImage nativeImage = new NativeImage(64, 64, true);
            nativeImage.copyFrom(image);
            image.close();
            image = nativeImage;
            image.fillRect(0, 32, 64, 32, 0);
            image.copyRect(4, 16, 16, 32, 4, 4, true, false);
            image.copyRect(8, 16, 16, 32, 4, 4, true, false);
            image.copyRect(0, 20, 24, 32, 4, 12, true, false);
            image.copyRect(4, 20, 16, 32, 4, 12, true, false);
            image.copyRect(8, 20, 8, 32, 4, 12, true, false);
            image.copyRect(12, 20, 16, 32, 4, 12, true, false);
            image.copyRect(44, 16, -8, 32, 4, 4, true, false);
            image.copyRect(48, 16, -8, 32, 4, 4, true, false);
            image.copyRect(40, 20, 0, 32, 4, 12, true, false);
            image.copyRect(44, 20, -8, 32, 4, 12, true, false);
            image.copyRect(48, 20, -16, 32, 4, 12, true, false);
            image.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        PlayerSkinTextureDownloader.stripAlpha(image, 0, 0, 32, 16);
        if (bl) {
            PlayerSkinTextureDownloader.stripColor(image, 32, 0, 64, 32);
        }
        PlayerSkinTextureDownloader.stripAlpha(image, 0, 16, 64, 32);
        PlayerSkinTextureDownloader.stripAlpha(image, 16, 48, 48, 64);
        return image;
    }

    private static void stripColor(NativeImage image, int x1, int y1, int x2, int y2) {
        int j;
        int i;
        for (i = x1; i < x2; ++i) {
            for (j = y1; j < y2; ++j) {
                int k = image.getColorArgb(i, j);
                if (ColorHelper.getAlpha(k) >= 128) continue;
                return;
            }
        }
        for (i = x1; i < x2; ++i) {
            for (j = y1; j < y2; ++j) {
                image.setColorArgb(i, j, image.getColorArgb(i, j) & 0xFFFFFF);
            }
        }
    }

    private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {
        for (int i = x1; i < x2; ++i) {
            for (int j = y1; j < y2; ++j) {
                image.setColorArgb(i, j, ColorHelper.fullAlpha(image.getColorArgb(i, j)));
            }
        }
    }
}

