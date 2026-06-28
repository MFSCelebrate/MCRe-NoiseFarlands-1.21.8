/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChunkBatchSizeCalculator {
    final static private int field_45939 = 49;
    final static private int field_45940 = 3;
    private double averageNanosPerChunk = 2000000.0;
    private int sampleSize = 1;
    private volatile long startTime = Util.getMeasuringTimeNano();

    public void onStartChunkSend() {
        this.startTime = Util.getMeasuringTimeNano();
    }

    public void onChunkSent(int batchSize) {
        if (batchSize > 0) {
            double d = Util.getMeasuringTimeNano() - this.startTime;
            double e = d / (double)batchSize;
            double f = MathHelper.clamp(e, this.averageNanosPerChunk / 3.0, this.averageNanosPerChunk * 3.0);
            this.averageNanosPerChunk = (this.averageNanosPerChunk * (double)this.sampleSize + f) / (double)(this.sampleSize + 1);
            this.sampleSize = Math.min(49, this.sampleSize + 1);
        }
    }

    public float getDesiredChunksPerTick() {
        return (float)(7000000.0 / this.averageNanosPerChunk);
    }
}

