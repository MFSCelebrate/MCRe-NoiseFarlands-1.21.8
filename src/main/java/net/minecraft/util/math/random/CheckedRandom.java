/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package net.minecraft.util.math.random;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.BaseRandom;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.util.thread.LockHelper;

public class CheckedRandom
implements BaseRandom {
    final static private int INT_BITS = 48;
    final static private long SEED_MASK = 0xFFFFFFFFFFFFL;
    final static private long MULTIPLIER = 25214903917L;
    final static private long INCREMENT = 11L;
    final private AtomicLong seed = new AtomicLong();
    final private GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public CheckedRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public Random split() {
        return new CheckedRandom(this.nextLong());
    }

    @Override
    public RandomSplitter nextSplitter() {
        return new Splitter(this.nextLong());
    }

    @Override
    public void setSeed(long seed) {
        if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL)) {
            throw LockHelper.crash("LegacyRandomSource", null);
        }
        this.gaussianGenerator.reset();
    }

    @Override
    public int next(int bits) {
        long m;
        long l = this.seed.get();
        if (!this.seed.compareAndSet(l, m = l * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
            throw LockHelper.crash("LegacyRandomSource", null);
        }
        return (int)(m >> 48 - bits);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianGenerator.next();
    }

    public static class Splitter
    implements RandomSplitter {
        final private long seed;

        public Splitter(long seed) {
            this.seed = seed;
        }

        @Override
        public Random split(int x, int y, int z) {
            long l = MathHelper.hashCode(x, y, z);
            long m = l ^ this.seed;
            return new CheckedRandom(m);
        }

        @Override
        public Random split(String seed) {
            int i = seed.hashCode();
            return new CheckedRandom((long)i ^ this.seed);
        }

        @Override
        public Random split(long seed) {
            return new CheckedRandom(seed);
        }

        @Override
        @VisibleForTesting
        public void addDebugInfo(StringBuilder info) {
            info.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
        }
    }
}

