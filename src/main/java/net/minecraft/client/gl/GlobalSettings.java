/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class GlobalSettings
implements AutoCloseable {
    final static public int SIZE = new Std140SizeCalculator().putVec2().putFloat().putFloat().putInt().get();
    final private GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Global Settings UBO", 136, SIZE);

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    public void set(int width, int height, double glintStrength, long time, RenderTickCounter tickCounter, int menuBackgroundBluriness) {
        block4: {
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, SIZE).putVec2(width, height).putFloat((float)glintStrength).putFloat(((float)(time % 24000L) + tickCounter.getTickProgress(false)) / 24000.0f).putInt(menuBackgroundBluriness).get();
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
                if (memoryStack == null) break block4;
            }
        }
        RenderSystem.setGlobalSettingsUniform(this.buffer);
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

