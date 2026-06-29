/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class RawProjectionMatrix
implements AutoCloseable {
    final private GpuBuffer buffer;
    final private GpuBufferSlice slice;

    public RawProjectionMatrix(String name) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.buffer = gpuDevice.createBuffer(() -> "Projection matrix UBO " + name, 136, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
        this.slice = this.buffer.slice(0, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public GpuBufferSlice set(Matrix4f projectionMatrix) {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f((Matrix4fc)projectionMatrix).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
            if (memoryStack == null) return this.slice;
        }
        return this.slice;
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

