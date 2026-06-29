/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public abstract class GpuBufferManager {
    public static GpuBufferManager create(GLCapabilities capabilities, Set<String> usedCapabilities) {
        if (capabilities.GL_ARB_buffer_storage && GlBackend.allowGlBufferStorage) {
            usedCapabilities.add("GL_ARB_buffer_storage");
            return new ARBGpuBufferManager();
        }
        return new DirectGpuBufferManager();
    }

    public abstract GlGpuBuffer createBuffer(BufferManager var1, @Nullable Supplier<String> var2, int var3, int var4);

    public abstract GlGpuBuffer createBuffer(BufferManager var1, @Nullable Supplier<String> var2, int var3, ByteBuffer var4);

    public abstract GlGpuBuffer.Mapped mapBufferRange(BufferManager var1, GlGpuBuffer var2, int var3, int var4, int var5);

    @Environment(value=EnvType.CLIENT)
    static class ARBGpuBufferManager
    extends GpuBufferManager {
        ARBGpuBufferManager() {
        }

        @Override
        public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, int usage, int size) {
            int i = bufferManager.createBuffer();
            bufferManager.setBufferStorage(i, size, GlConst.bufferUsageToGlFlag(usage));
            ByteBuffer byteBuffer = this.mapBufferRange(bufferManager, usage, i, size);
            return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, size, i, byteBuffer);
        }

        @Override
        public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, int usage, ByteBuffer data) {
            int i = bufferManager.createBuffer();
            int j = data.remaining();
            bufferManager.setBufferStorage(i, data, GlConst.bufferUsageToGlFlag(usage));
            ByteBuffer byteBuffer = this.mapBufferRange(bufferManager, usage, i, j);
            return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, j, i, byteBuffer);
        }

        @Nullable
        private ByteBuffer mapBufferRange(BufferManager bufferManager, int usage, int buffer, int length) {
            ByteBuffer byteBuffer;
            int i = 0;
            if ((usage & 1) != 0) {
                i |= GL30.GL_MAP_READ_BIT;
            }
            if ((usage & 2) != 0) {
                i |= 0x12;
            }
            if (i != 0) {
                GlStateManager.clearGlErrors();
                byteBuffer = bufferManager.mapBufferRange(buffer, 0, length, i | 0x40);
                if (byteBuffer == null) {
                    throw new IllegalStateException("Can't persistently map buffer, opengl error " + GlStateManager._getError());
                }
            } else {
                byteBuffer = null;
            }
            return byteBuffer;
        }

        @Override
        public GlGpuBuffer.Mapped mapBufferRange(BufferManager bufferManager, GlGpuBuffer buffer, int offset, int length, int flags) {
            if (buffer.backingBuffer == null) {
                throw new IllegalStateException("Somehow trying to map an unmappable buffer");
            }
            return new GlGpuBuffer.Mapped(() -> {
                if ((flags & 2) != 0) {
                    bufferManager.flushMappedBufferRange(glGpuBuffer.id, offset, length);
                }
            }, buffer, MemoryUtil.memSlice((ByteBuffer)buffer.backingBuffer, (int)offset, (int)length));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DirectGpuBufferManager
    extends GpuBufferManager {
        DirectGpuBufferManager() {
        }

        @Override
        public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, int usage, int size) {
            int i = bufferManager.createBuffer();
            bufferManager.setBufferData(i, size, GlConst.bufferUsageToGlEnum(usage));
            return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, size, i, null);
        }

        @Override
        public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier<String> debugLabelSupplier, int usage, ByteBuffer data) {
            int i = bufferManager.createBuffer();
            int j = data.remaining();
            bufferManager.setBufferData(i, data, GlConst.bufferUsageToGlEnum(usage));
            return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, j, i, null);
        }

        @Override
        public GlGpuBuffer.Mapped mapBufferRange(BufferManager bufferManager, GlGpuBuffer buffer, int offset, int length, int flags) {
            GlStateManager.clearGlErrors();
            ByteBuffer byteBuffer = bufferManager.mapBufferRange(buffer.id, offset, length, flags);
            if (byteBuffer == null) {
                throw new IllegalStateException("Can't map buffer, opengl error " + GlStateManager._getError());
            }
            return new GlGpuBuffer.Mapped(() -> bufferManager.unmapBuffer(glGpuBuffer.id), buffer, byteBuffer);
        }
    }
}

