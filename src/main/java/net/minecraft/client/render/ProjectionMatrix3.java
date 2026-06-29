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
public class ProjectionMatrix3
implements AutoCloseable {
    final private GpuBuffer buffer;
    final private GpuBufferSlice slice;
    final private float nearZ;
    final private float farZ;
    private int width;
    private int height;
    private float fov;

    public ProjectionMatrix3(String name, float nearZ, float farZ) {
        this.nearZ = nearZ;
        this.farZ = farZ;
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.buffer = gpuDevice.createBuffer(() -> "Projection matrix UBO " + name, 136, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
        this.slice = this.buffer.slice(0, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    public GpuBufferSlice set(int width, int height, float fov) {
        block5: {
            if (this.width == width && this.height == height) {
                if (this.fov == fov) return this.slice;
            }
            Matrix4f matrix4f = this.getMatrix(width, height, fov);
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f((Matrix4fc)matrix4f).get();
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
                if (memoryStack == null) break block5;
            }
        }
        this.width = width;
        this.height = height;
        this.fov = fov;
        return this.slice;
    }

    private Matrix4f getMatrix(int width, int height, float fov) {
        return new Matrix4f().perspective(fov * ((float)Math.PI / 180), (float)width / (float)height, this.nearZ, this.farZ);
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

