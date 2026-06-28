/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public record GpuBufferSlice(GpuBuffer buffer, int offset, int length) {
    public GpuBufferSlice slice(int offset, int length) {
        if (offset < 0 || length < 0 || offset + length >= this.length) {
            throw new IllegalArgumentException("Offset of " + offset + " and length " + length + " would put new slice outside existing slice's range (of " + offset + "," + length + ")");
        }
        return new GpuBufferSlice(this.buffer, this.offset + offset, length);
    }
}

