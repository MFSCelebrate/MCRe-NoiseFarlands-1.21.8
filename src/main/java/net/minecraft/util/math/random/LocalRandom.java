/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math.random;

import net.minecraft.util.math.random.BaseRandom;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

public class LocalRandom
implements BaseRandom {
    final static private int INT_BITS = 48;
    final static private long SEED_MASK = 0xFFFFFFFFFFFFL;
    final static private long MULTIPLIER = 25214903917L;
    final static private long INCREMENT = 11L;
    private long seed;
    final private GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

    public LocalRandom(long seed) {
        this.setSeed(seed);
    }

    @Override
    public Random split() {
        return new LocalRandom(this.nextLong());
    }

    @Override
    public RandomSplitter nextSplitter() {
        return new CheckedRandom.Splitter(this.nextLong());
    }

    @Override
    public void setSeed(long seed) {
        this.seed = (seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
        this.gaussianGenerator.reset();
    }

    @Override
    public int next(int bits) {
        long l;
        this.seed = l = this.seed * 25214903917L + 11L & 0xFFFFFFFFFFFFL;
        return (int)(l >> 48 - bits);
    }

    @Override
    public double nextGaussian() {
        return this.gaussianGenerator.next();
    }
}

