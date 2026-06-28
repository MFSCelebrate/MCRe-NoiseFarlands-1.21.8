/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;

public class FeatureSizeType<P extends FeatureSize> {
    final static public FeatureSizeType<TwoLayersFeatureSize> TWO_LAYERS_FEATURE_SIZE = FeatureSizeType.register("two_layers_feature_size", TwoLayersFeatureSize.CODEC);
    final static public FeatureSizeType<ThreeLayersFeatureSize> THREE_LAYERS_FEATURE_SIZE = FeatureSizeType.register("three_layers_feature_size", ThreeLayersFeatureSize.CODEC);
    final private MapCodec<P> codec;

    private static <P extends FeatureSize> FeatureSizeType<P> register(String id, MapCodec<P> mapCodec) {
        return Registry.register(Registries.FEATURE_SIZE_TYPE, id, new FeatureSizeType<P>(mapCodec));
    }

    private FeatureSizeType(MapCodec<P> mapCodec) {
        this.codec = mapCodec;
    }

    public MapCodec<P> getCodec() {
        return this.codec;
    }
}

