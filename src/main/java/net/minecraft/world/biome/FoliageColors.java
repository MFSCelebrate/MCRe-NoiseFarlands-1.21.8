/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.biome;

import net.minecraft.world.biome.BiomeColors;

public class FoliageColors {
    final static public int SPRUCE = -10380959;
    final static public int BIRCH = -8345771;
    final static public int DEFAULT = -12012264;
    final static public int MANGROVE = -7158200;
    static private int[] colorMap = new int[65536];

    public static void setColorMap(int[] pixels) {
        colorMap = pixels;
    }

    public static int getColor(double temperature, double downfall) {
        return BiomeColors.getColor(temperature, downfall, colorMap, -12012264);
    }
}

