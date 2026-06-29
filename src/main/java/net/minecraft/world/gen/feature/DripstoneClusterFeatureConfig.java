/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class DripstoneClusterFeatureConfig
implements FeatureConfig {
    final static public Codec<DripstoneClusterFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter(config -> config.floorToCeilingSearchRange), (App)IntProvider.createValidatingCodec(1, 128).fieldOf("height").forGetter(config -> config.height), (App)IntProvider.createValidatingCodec(1, 128).fieldOf("radius").forGetter(config -> config.radius), (App)Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter(config -> config.maxStalagmiteStalactiteHeightDiff), (App)Codec.intRange(1, 64).fieldOf("height_deviation").forGetter(config -> config.heightDeviation), (App)IntProvider.createValidatingCodec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter(config -> config.dripstoneBlockLayerThickness), (App)FloatProvider.createValidatedCodec(0.0f, 2.0f).fieldOf("density").forGetter(config -> config.density), (App)FloatProvider.createValidatedCodec(0.0f, 2.0f).fieldOf("wetness").forGetter(config -> config.wetness), (App)Codec.floatRange(0.0f, 1.0f).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter(config -> Float.valueOf(config.chanceOfDripstoneColumnAtMaxDistanceFromCenter)), (App)Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter(config -> config.maxDistanceFromCenterAffectingChanceOfDripstoneColumn), (App)Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter(config -> config.maxDistanceFromCenterAffectingHeightBias)).apply((Applicative)instance, DripstoneClusterFeatureConfig::new));
    final public int floorToCeilingSearchRange;
    final public IntProvider height;
    final public IntProvider radius;
    final public int maxStalagmiteStalactiteHeightDiff;
    final public int heightDeviation;
    final public IntProvider dripstoneBlockLayerThickness;
    final public FloatProvider density;
    final public FloatProvider wetness;
    final public float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
    final public int maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
    final public int maxDistanceFromCenterAffectingHeightBias;

    public DripstoneClusterFeatureConfig(int floorToCeilingSearchRange, IntProvider height, IntProvider radius, int maxStalagmiteStalactiteHeightDiff, int heightDeviation, IntProvider dripstoneBlockLayerThickness, FloatProvider density, FloatProvider wetness, float wetnessMean, int maxDistanceFromCenterAffectingChanceOfDripstoneColumn, int maxDistanceFromCenterAffectingHeightBias) {
        this.floorToCeilingSearchRange = floorToCeilingSearchRange;
        this.height = height;
        this.radius = radius;
        this.maxStalagmiteStalactiteHeightDiff = maxStalagmiteStalactiteHeightDiff;
        this.heightDeviation = heightDeviation;
        this.dripstoneBlockLayerThickness = dripstoneBlockLayerThickness;
        this.density = density;
        this.wetness = wetness;
        this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = wetnessMean;
        this.maxDistanceFromCenterAffectingChanceOfDripstoneColumn = maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
        this.maxDistanceFromCenterAffectingHeightBias = maxDistanceFromCenterAffectingHeightBias;
    }
}

