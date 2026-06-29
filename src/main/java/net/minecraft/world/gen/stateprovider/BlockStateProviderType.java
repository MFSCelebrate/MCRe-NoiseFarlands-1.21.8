/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.DualNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseThresholdBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class BlockStateProviderType<P extends BlockStateProvider> {
    final static public BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = BlockStateProviderType.register("simple_state_provider", SimpleBlockStateProvider.CODEC);
    final static public BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = BlockStateProviderType.register("weighted_state_provider", WeightedBlockStateProvider.CODEC);
    final static public BlockStateProviderType<NoiseThresholdBlockStateProvider> NOISE_THRESHOLD_PROVIDER = BlockStateProviderType.register("noise_threshold_provider", NoiseThresholdBlockStateProvider.CODEC);
    final static public BlockStateProviderType<NoiseBlockStateProvider> NOISE_PROVIDER = BlockStateProviderType.register("noise_provider", NoiseBlockStateProvider.CODEC);
    final static public BlockStateProviderType<DualNoiseBlockStateProvider> DUAL_NOISE_PROVIDER = BlockStateProviderType.register("dual_noise_provider", DualNoiseBlockStateProvider.DUAL_CODEC);
    final static public BlockStateProviderType<PillarBlockStateProvider> ROTATED_BLOCK_PROVIDER = BlockStateProviderType.register("rotated_block_provider", PillarBlockStateProvider.CODEC);
    final static public BlockStateProviderType<RandomizedIntBlockStateProvider> RANDOMIZED_INT_STATE_PROVIDER = BlockStateProviderType.register("randomized_int_state_provider", RandomizedIntBlockStateProvider.CODEC);
    final private MapCodec<P> codec;

    private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.BLOCK_STATE_PROVIDER_TYPE, id, new BlockStateProviderType<P>(codec));
    }

    public BlockStateProviderType(MapCodec<P> codec) {
        this.codec = codec;
    }

    public MapCodec<P> getCodec() {
        return this.codec;
    }
}

