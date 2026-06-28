/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class TextureUtil {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public int MIN_MIPMAP_LEVEL = 0;
    final static private int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

    public static ByteBuffer readResource(InputStream inputStream) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
        if (readableByteChannel instanceof SeekableByteChannel) {
            SeekableByteChannel seekableByteChannel = (SeekableByteChannel)readableByteChannel;
            return TextureUtil.readResource(readableByteChannel, (int)seekableByteChannel.size() + 1);
        }
        return TextureUtil.readResource(readableByteChannel, 8192);
    }

    private static ByteBuffer readResource(ReadableByteChannel channel, int bufSize) throws IOException {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)bufSize);
        try {
            while (channel.read(byteBuffer) != -1) {
                if (byteBuffer.hasRemaining()) continue;
                byteBuffer = MemoryUtil.memRealloc((ByteBuffer)byteBuffer, (int)(byteBuffer.capacity() * 2));
            }
            return byteBuffer;
        }
        catch (IOException iOException) {
            MemoryUtil.memFree((Buffer)byteBuffer);
            throw iOException;
        }
    }

    public static void writeAsPNG(Path directory, String prefix, GpuTexture texture, int scales, IntUnaryOperator colorFunction) {
        RenderSystem.assertOnRenderThread();
        int i = 0;
        for (int j = 0; j <= scales; ++j) {
            i += texture.getFormat().pixelSize() * texture.getWidth(j) * texture.getHeight(j);
        }
        GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Texture output buffer", 9, i);
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        Runnable runnable = () -> {
            GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(gpuBuffer, true, false);
            try {
                int j = 0;
                for (int k = 0; k <= scales; ++k) {
                    int l = texture.getWidth(k);
                    int m = texture.getHeight(k);
                    try (NativeImage nativeImage = new NativeImage(l, m, false);){
                        for (int n = 0; n < m; ++n) {
                            for (int o = 0; o < l; ++o) {
                                int p = mappedView.data().getInt(j + (o + n * l) * texture.getFormat().pixelSize());
                                nativeImage.setColor(o, n, colorFunction.applyAsInt(p));
                            }
                        }
                        Path path2 = directory.resolve(prefix + "_" + k + ".png");
                        nativeImage.writeTo(path2);
                        LOGGER.debug("Exported png to: {}", (Object)path2.toAbsolutePath());
                    }
                    catch (IOException iOException) {
                        LOGGER.debug("Unable to write: ", (Throwable)iOException);
                    }
                    j += texture.getFormat().pixelSize() * l * m;
                }
                if (mappedView != null) {
                    mappedView.close();
                }
            }
            catch (Throwable throwable) {
                if (mappedView != null) {
                    try {
                        mappedView.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            gpuBuffer.close();
        };
        AtomicInteger atomicInteger = new AtomicInteger();
        int k = 0;
        for (int l = 0; l <= scales; ++l) {
            commandEncoder.copyTextureToBuffer(texture, gpuBuffer, k, () -> {
                if (atomicInteger.getAndIncrement() == scales) {
                    runnable.run();
                }
            }, l);
            k += texture.getFormat().pixelSize() * texture.getWidth(l) * texture.getHeight(l);
        }
    }

    public static Path getDebugTexturePath(Path path) {
        return path.resolve("screenshots").resolve("debug");
    }

    public static Path getDebugTexturePath() {
        return TextureUtil.getDebugTexturePath(Path.of(".", new String[0]));
    }
}

