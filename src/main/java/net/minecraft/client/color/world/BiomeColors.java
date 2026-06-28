/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.ColorResolver;

@Environment(value=EnvType.CLIENT)
public class BiomeColors {
    final static public ColorResolver GRASS_COLOR = Biome::getGrassColorAt;
    final static public ColorResolver FOLIAGE_COLOR = (biome, x, z) -> biome.getFoliageColor();
    final static public ColorResolver DRY_FOLIAGE_COLOR = (biome, x, z) -> biome.getDryFoliageColor();
    final static public ColorResolver WATER_COLOR = (biome, x, z) -> biome.getWaterColor();

    private static int getColor(BlockRenderView world, BlockPos pos, ColorResolver resolver) {
        return world.getColor(pos, resolver);
    }

    public static int getGrassColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor(world, pos, GRASS_COLOR);
    }

    public static int getFoliageColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor(world, pos, FOLIAGE_COLOR);
    }

    public static int getDryFoliageColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor(world, pos, DRY_FOLIAGE_COLOR);
    }

    public static int getWaterColor(BlockRenderView world, BlockPos pos) {
        return BiomeColors.getColor(world, pos, WATER_COLOR);
    }
}

