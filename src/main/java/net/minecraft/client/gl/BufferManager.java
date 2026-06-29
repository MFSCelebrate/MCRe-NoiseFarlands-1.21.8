/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.ARBBufferStorage
 *  org.lwjgl.opengl.ARBDirectStateAccess
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GL31
 *  org.lwjgl.opengl.GLCapabilities
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlBackend;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

@Environment(value=EnvType.CLIENT)
public abstract class BufferManager {
    public static BufferManager create(GLCapabilities capabilities, Set<String> usedCapabilities) {
        if (capabilities.GL_ARB_direct_state_access && GlBackend.allowGlArbDirectAccess) {
            usedCapabilities.add("GL_ARB_direct_state_access");
            return new ARBBufferManager();
        }
        return new DefaultBufferManager();
    }

    abstract int createBuffer();

    abstract void setBufferData(int var1, long var2, int var4);

    abstract void setBufferData(int var1, ByteBuffer var2, int var3);

    abstract void setBufferSubData(int var1, int var2, ByteBuffer var3);

    abstract void setBufferStorage(int var1, long var2, int var4);

    abstract void setBufferStorage(int var1, ByteBuffer var2, int var3);

    @Nullable
    abstract ByteBuffer mapBufferRange(int var1, int var2, int var3, int var4);

    abstract void unmapBuffer(int var1);

    abstract int createFramebuffer();

    abstract void setupFramebuffer(int var1, int var2, int var3, int var4, int var5);

    abstract void setupBlitFramebuffer(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

    abstract void flushMappedBufferRange(int var1, int var2, int var3);

    abstract void method_72237(int var1, int var2, int var3, int var4, int var5);

    @Environment(value=EnvType.CLIENT)
    static class ARBBufferManager
    extends BufferManager {
        ARBBufferManager() {
        }

        @Override
        int createBuffer() {
            return ARBDirectStateAccess.glCreateBuffers();
        }

        @Override
        void setBufferData(int buffer, long size, int usage) {
            ARBDirectStateAccess.glNamedBufferData((int)buffer, (long)size, (int)usage);
        }

        @Override
        void setBufferData(int buffer, ByteBuffer data, int usage) {
            ARBDirectStateAccess.glNamedBufferData((int)buffer, (ByteBuffer)data, (int)usage);
        }

        @Override
        void setBufferSubData(int buffer, int offset, ByteBuffer data) {
            ARBDirectStateAccess.glNamedBufferSubData((int)buffer, (long)offset, (ByteBuffer)data);
        }

        @Override
        void setBufferStorage(int buffer, long size, int flags) {
            ARBDirectStateAccess.glNamedBufferStorage((int)buffer, (long)size, (int)flags);
        }

        @Override
        void setBufferStorage(int buffer, ByteBuffer data, int flags) {
            ARBDirectStateAccess.glNamedBufferStorage((int)buffer, (ByteBuffer)data, (int)flags);
        }

        @Override
        @Nullable
        ByteBuffer mapBufferRange(int buffer, int offset, int length, int access) {
            return ARBDirectStateAccess.glMapNamedBufferRange((int)buffer, (long)offset, (long)length, (int)access);
        }

        @Override
        void unmapBuffer(int buffer) {
            ARBDirectStateAccess.glUnmapNamedBuffer((int)buffer);
        }

        @Override
        public int createFramebuffer() {
            return ARBDirectStateAccess.glCreateFramebuffers();
        }

        @Override
        public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget) {
            ARBDirectStateAccess.glNamedFramebufferTexture((int)framebuffer, (int)GlConst.GL_COLOR_ATTACHMENT0, (int)colorAttachment, (int)mipLevel);
            ARBDirectStateAccess.glNamedFramebufferTexture((int)framebuffer, (int)GlConst.GL_DEPTH_ATTACHMENT, (int)depthAttachment, (int)mipLevel);
            if (bindTarget != 0) {
                GlStateManager._glBindFramebuffer(bindTarget, framebuffer);
            }
        }

        @Override
        public void setupBlitFramebuffer(int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
            ARBDirectStateAccess.glBlitNamedFramebuffer((int)readFramebuffer, (int)writeFramebuffer, (int)srcX0, (int)srcY0, (int)srcX1, (int)srcY1, (int)dstX0, (int)dstY0, (int)dstX1, (int)dstY1, (int)mask, (int)filter);
        }

        @Override
        void flushMappedBufferRange(int buffer, int offset, int length) {
            ARBDirectStateAccess.glFlushMappedNamedBufferRange((int)buffer, (long)offset, (long)length);
        }

        @Override
        void method_72237(int i, int j, int k, int l, int m) {
            ARBDirectStateAccess.glCopyNamedBufferSubData((int)i, (int)j, (long)k, (long)l, (long)m);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DefaultBufferManager
    extends BufferManager {
        DefaultBufferManager() {
        }

        @Override
        int createBuffer() {
            return GlStateManager._glGenBuffers();
        }

        @Override
        void setBufferData(int buffer, long size, int usage) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            GlStateManager._glBufferData(GlConst.GL_COPY_WRITE_BUFFER, size, GlConst.bufferUsageToGlEnum(usage));
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        void setBufferData(int buffer, ByteBuffer data, int usage) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            GlStateManager._glBufferData(GlConst.GL_COPY_WRITE_BUFFER, data, GlConst.bufferUsageToGlEnum(usage));
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        void setBufferSubData(int buffer, int offset, ByteBuffer data) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            GlStateManager._glBufferSubData(GlConst.GL_COPY_WRITE_BUFFER, offset, data);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        void setBufferStorage(int buffer, long size, int flags) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            ARBBufferStorage.glBufferStorage((int)GlConst.GL_COPY_WRITE_BUFFER, (long)size, (int)flags);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        void setBufferStorage(int buffer, ByteBuffer data, int flags) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            ARBBufferStorage.glBufferStorage((int)GlConst.GL_COPY_WRITE_BUFFER, (ByteBuffer)data, (int)flags);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        @Nullable
        ByteBuffer mapBufferRange(int buffer, int offset, int length, int access) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            ByteBuffer byteBuffer = GlStateManager._glMapBufferRange(GlConst.GL_COPY_WRITE_BUFFER, offset, length, access);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
            return byteBuffer;
        }

        @Override
        void unmapBuffer(int buffer) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            GlStateManager._glUnmapBuffer(GlConst.GL_COPY_WRITE_BUFFER);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        void flushMappedBufferRange(int buffer, int offset, int length) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, buffer);
            GL30.glFlushMappedBufferRange((int)GlConst.GL_COPY_WRITE_BUFFER, (long)offset, (long)length);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        void method_72237(int i, int j, int k, int l, int m) {
            GlStateManager._glBindBuffer(GlConst.GL_COPY_READ_BUFFER, i);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, j);
            GL31.glCopyBufferSubData(36662, 36663, (long)k, (long)l, (long)m);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_READ_BUFFER, 0);
            GlStateManager._glBindBuffer(GlConst.GL_COPY_WRITE_BUFFER, 0);
        }

        @Override
        public int createFramebuffer() {
            return GlStateManager.glGenFramebuffers();
        }

        @Override
        public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget) {
            int i = bindTarget == 0 ? GlConst.GL_DRAW_FRAMEBUFFER : bindTarget;
            int j = GlStateManager.getFrameBuffer(i);
            GlStateManager._glBindFramebuffer(i, framebuffer);
            GlStateManager._glFramebufferTexture2D(i, GlConst.GL_COLOR_ATTACHMENT0, GlConst.GL_TEXTURE_2D, colorAttachment, mipLevel);
            GlStateManager._glFramebufferTexture2D(i, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_TEXTURE_2D, depthAttachment, mipLevel);
            if (bindTarget == 0) {
                GlStateManager._glBindFramebuffer(i, j);
            }
        }

        @Override
        public void setupBlitFramebuffer(int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
            int i = GlStateManager.getFrameBuffer(GlConst.GL_READ_FRAMEBUFFER);
            int j = GlStateManager.getFrameBuffer(GlConst.GL_DRAW_FRAMEBUFFER);
            GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, readFramebuffer);
            GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, writeFramebuffer);
            GlStateManager._glBlitFrameBuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
            GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, i);
            GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, j);
        }
    }
}

