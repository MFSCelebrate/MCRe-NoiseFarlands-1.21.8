/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.handler;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;

public class PacketSizeLogger {
    final private AtomicInteger packetSizeInBytes = new AtomicInteger();
    final private MultiValueDebugSampleLogImpl log;

    public PacketSizeLogger(MultiValueDebugSampleLogImpl log) {
        this.log = log;
    }

    public void increment(int bytes) {
        this.packetSizeInBytes.getAndAdd(bytes);
    }

    public void push() {
        this.log.push(this.packetSizeInBytes.getAndSet(0));
    }
}

