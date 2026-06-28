/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.nbt.NbtSizeValidationException;

public class NbtSizeTracker {
    final static private int DEFAULT_MAX_DEPTH = 512;
    final private long maxBytes;
    private long allocatedBytes;
    final private int maxDepth;
    private int depth;

    public NbtSizeTracker(long maxBytes, int maxDepth) {
        this.maxBytes = maxBytes;
        this.maxDepth = maxDepth;
    }

    public static NbtSizeTracker of(long maxBytes) {
        return new NbtSizeTracker(maxBytes, 512);
    }

    public static NbtSizeTracker ofUnlimitedBytes() {
        return new NbtSizeTracker(Long.MAX_VALUE, 512);
    }

    public void add(long multiplier, long bytes) {
        this.add(multiplier * bytes);
    }

    public void add(long bytes) {
        if (bytes < 0L) {
            throw new IllegalArgumentException("Tried to account NBT tag with negative size: " + bytes);
        }
        if (this.allocatedBytes + bytes > this.maxBytes) {
            throw new NbtSizeValidationException("Tried to read NBT tag that was too big; tried to allocate: " + this.allocatedBytes + " + " + bytes + " bytes where max allowed: " + this.maxBytes);
        }
        this.allocatedBytes += bytes;
    }

    public void pushStack() {
        if (this.depth >= this.maxDepth) {
            throw new NbtSizeValidationException("Tried to read NBT tag with too high complexity, depth > " + this.maxDepth);
        }
        ++this.depth;
    }

    public void popStack() {
        if (this.depth <= 0) {
            throw new NbtSizeValidationException("NBT-Accounter tried to pop stack-depth at top-level");
        }
        --this.depth;
    }

    @VisibleForTesting
    public long getAllocatedBytes() {
        return this.allocatedBytes;
    }

    @VisibleForTesting
    public int getDepth() {
        return this.depth;
    }
}

