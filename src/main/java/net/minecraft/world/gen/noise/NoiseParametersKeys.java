/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.noise;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.RandomSplitter;

public class NoiseParametersKeys {
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE = NoiseParametersKeys.of("temperature");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION = NoiseParametersKeys.of("vegetation");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS = NoiseParametersKeys.of("continentalness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION = NoiseParametersKeys.of("erosion");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> TEMPERATURE_LARGE = NoiseParametersKeys.of("temperature_large");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> VEGETATION_LARGE = NoiseParametersKeys.of("vegetation_large");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CONTINENTALNESS_LARGE = NoiseParametersKeys.of("continentalness_large");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> EROSION_LARGE = NoiseParametersKeys.of("erosion_large");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> RIDGE = NoiseParametersKeys.of("ridge");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> OFFSET = NoiseParametersKeys.of("offset");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_BARRIER = NoiseParametersKeys.of("aquifer_barrier");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_FLOODEDNESS = NoiseParametersKeys.of("aquifer_fluid_level_floodedness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_LAVA = NoiseParametersKeys.of("aquifer_lava");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> AQUIFER_FLUID_LEVEL_SPREAD = NoiseParametersKeys.of("aquifer_fluid_level_spread");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR = NoiseParametersKeys.of("pillar");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_RARENESS = NoiseParametersKeys.of("pillar_rareness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PILLAR_THICKNESS = NoiseParametersKeys.of("pillar_thickness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D = NoiseParametersKeys.of("spaghetti_2d");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_ELEVATION = NoiseParametersKeys.of("spaghetti_2d_elevation");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_MODULATOR = NoiseParametersKeys.of("spaghetti_2d_modulator");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_2D_THICKNESS = NoiseParametersKeys.of("spaghetti_2d_thickness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_1 = NoiseParametersKeys.of("spaghetti_3d_1");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_2 = NoiseParametersKeys.of("spaghetti_3d_2");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_RARITY = NoiseParametersKeys.of("spaghetti_3d_rarity");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_3D_THICKNESS = NoiseParametersKeys.of("spaghetti_3d_thickness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS = NoiseParametersKeys.of("spaghetti_roughness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SPAGHETTI_ROUGHNESS_MODULATOR = NoiseParametersKeys.of("spaghetti_roughness_modulator");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_ENTRANCE = NoiseParametersKeys.of("cave_entrance");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_LAYER = NoiseParametersKeys.of("cave_layer");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CAVE_CHEESE = NoiseParametersKeys.of("cave_cheese");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEININESS = NoiseParametersKeys.of("ore_veininess");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_A = NoiseParametersKeys.of("ore_vein_a");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_VEIN_B = NoiseParametersKeys.of("ore_vein_b");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ORE_GAP = NoiseParametersKeys.of("ore_gap");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE = NoiseParametersKeys.of("noodle");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_THICKNESS = NoiseParametersKeys.of("noodle_thickness");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_A = NoiseParametersKeys.of("noodle_ridge_a");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NOODLE_RIDGE_B = NoiseParametersKeys.of("noodle_ridge_b");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> JAGGED = NoiseParametersKeys.of("jagged");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE = NoiseParametersKeys.of("surface");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SECONDARY = NoiseParametersKeys.of("surface_secondary");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CLAY_BANDS_OFFSET = NoiseParametersKeys.of("clay_bands_offset");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR = NoiseParametersKeys.of("badlands_pillar");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_PILLAR_ROOF = NoiseParametersKeys.of("badlands_pillar_roof");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> BADLANDS_SURFACE = NoiseParametersKeys.of("badlands_surface");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR = NoiseParametersKeys.of("iceberg_pillar");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_PILLAR_ROOF = NoiseParametersKeys.of("iceberg_pillar_roof");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICEBERG_SURFACE = NoiseParametersKeys.of("iceberg_surface");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SURFACE_SWAMP = NoiseParametersKeys.of("surface_swamp");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> CALCITE = NoiseParametersKeys.of("calcite");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL = NoiseParametersKeys.of("gravel");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> POWDER_SNOW = NoiseParametersKeys.of("powder_snow");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PACKED_ICE = NoiseParametersKeys.of("packed_ice");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> ICE = NoiseParametersKeys.of("ice");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> SOUL_SAND_LAYER = NoiseParametersKeys.of("soul_sand_layer");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> GRAVEL_LAYER = NoiseParametersKeys.of("gravel_layer");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> PATCH = NoiseParametersKeys.of("patch");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHERRACK = NoiseParametersKeys.of("netherrack");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_WART = NoiseParametersKeys.of("nether_wart");
    final static public RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> NETHER_STATE_SELECTOR = NoiseParametersKeys.of("nether_state_selector");

    private static RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> of(String id) {
        return RegistryKey.of(RegistryKeys.NOISE_PARAMETERS, Identifier.ofVanilla(id));
    }

    public static DoublePerlinNoiseSampler createNoiseSampler(RegistryEntryLookup<DoublePerlinNoiseSampler.NoiseParameters> noiseParametersLookup, RandomSplitter splitter, RegistryKey<DoublePerlinNoiseSampler.NoiseParameters> key) {
        RegistryEntry.Reference<DoublePerlinNoiseSampler.NoiseParameters> registryEntry = noiseParametersLookup.getOrThrow(key);
        return DoublePerlinNoiseSampler.create(splitter.split(registryEntry.getKey().orElseThrow().getValue()), (DoublePerlinNoiseSampler.NoiseParameters)registryEntry.value());
    }
}

