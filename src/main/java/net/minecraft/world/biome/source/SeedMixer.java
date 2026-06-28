/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

public class SeedMixer {
    final static private long field_29842 = 6364136223846793005L;
    final static private long field_29843 = 1442695040888963407L;

    public static long mixSeed(long seed, long salt) {
        seed *= seed * 6364136223846793005L + 1442695040888963407L;
        return seed += salt;
    }
}

