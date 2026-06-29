/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class GlGpuFence
implements GpuFence {
    private long handle = GlStateManager._glFenceSync(GlConst.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);

    @Override
    public void close() {
        if (this.handle != 0L) {
            GlStateManager._glDeleteSync(this.handle);
            this.handle = 0L;
        }
    }

    @Override
    public boolean awaitCompletion(long l) {
        if (this.handle == 0L) {
            return true;
        }
        int i = GlStateManager._glClientWaitSync(this.handle, 0, l);
        if (i == GlConst.GL_TIMEOUT_EXPIRED) {
            return false;
        }
        if (i == GlConst.GL_WAIT_FAILED) {
            throw new IllegalStateException("Failed to complete gpu fence");
        }
        return true;
    }
}

