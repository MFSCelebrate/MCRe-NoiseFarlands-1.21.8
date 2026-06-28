/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math.random;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.math.random.BaseRandom;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

@Deprecated
public class ThreadSafeRandom
implements BaseRandom {
    final static private int INT_BITS = 48;
    final static private long SEED_MASK = 0xFFFFFFFFFFFFL;
    final static private long MULTIPLIER = 25214903917L;
    final static private long INCREMENT = 11L;
    final private AtomicLong seed = new AtomicLong();
    final private GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public ThreadSafeRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public Random split() {
        return new ThreadSafeRandom(this.nextLong());
    }

    @Override
    public RandomSplitter nextSplitter() {
        return new CheckedRandom.Splitter(this.nextLong());
    }

    @Override
    public void setSeed(long seed) {
        this.seed.set((seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL);
    }

    @Override
    public int next(int bits) {
        long m;
        long l;
        while (!this.seed.compareAndSet(l = this.seed.get(), m = l * 25214903917L + 11L & 0xFFFFFFFFFFFFL)) {
        }
        return (int)(m >>> 48 - bits);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianGenerator.next();
    }
}

