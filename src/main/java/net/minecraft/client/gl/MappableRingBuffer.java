/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class MappableRingBuffer
implements AutoCloseable {
    final static private int BUFFER_COUNT = 3;
    final private GpuBuffer[] buffers = new GpuBuffer[3];
    final private GpuFence[] fences = new GpuFence[3];
    final private int size;
    private int current = 0;

    public MappableRingBuffer(Supplier<String> nameSupplier, int usage, int size) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if ((usage & 1) == 0 && (usage & 2) == 0) {
            throw new IllegalArgumentException("MappableRingBuffer requires at least one of USAGE_MAP_READ or USAGE_MAP_WRITE");
        }
        for (int i = 0; i < 3; ++i) {
            int j = i;
            this.buffers[i] = gpuDevice.createBuffer(() -> (String)nameSupplier.get() + " #" + j, usage, size);
            this.fences[i] = null;
        }
        this.size = size;
    }

    public int size() {
        return this.size;
    }

    public GpuBuffer getBlocking() {
        GpuFence gpuFence = this.fences[this.current];
        if (gpuFence != null) {
            gpuFence.awaitCompletion(Long.MAX_VALUE);
            gpuFence.close();
            this.fences[this.current] = null;
        }
        return this.buffers[this.current];
    }

    public void rotate() {
        if (this.fences[this.current] != null) {
            this.fences[this.current].close();
        }
        this.fences[this.current] = RenderSystem.getDevice().createCommandEncoder().createFence();
        this.current = (this.current + 1) % 3;
    }

    @Override
    public void close() {
        for (int i = 0; i < 3; ++i) {
            this.buffers[i].close();
            if (this.fences[i] == null) continue;
            this.fences[i].close();
        }
    }
}

