/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome.source;

public final class BiomeCoords {
    final static public int field_33089 = 2;
    final static public int field_33090 = 4;
    final static public int field_34830 = 3;
    final static private int field_33091 = 2;

    private BiomeCoords() {
    }

    public static int fromBlock(int blockCoord) {
        return blockCoord >> 2;
    }

    public static int method_39920(int i) {
        return i & 3;
    }

    public static int toBlock(int biomeCoord) {
        return biomeCoord << 2;
    }

    public static int fromChunk(int chunkCoord) {
        return chunkCoord << 2;
    }

    public static int toChunk(int biomeCoord) {
        return biomeCoord >> 2;
    }
}

