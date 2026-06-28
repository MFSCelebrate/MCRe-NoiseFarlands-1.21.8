/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiler;

import java.time.Instant;
import net.minecraft.util.profiler.ProfileResult;

public final class Deviation {
    final public Instant instant;
    final public int ticks;
    final public ProfileResult result;

    public Deviation(Instant instant, int ticks, ProfileResult result) {
        this.instant = instant;
        this.ticks = ticks;
        this.result = result;
    }
}

