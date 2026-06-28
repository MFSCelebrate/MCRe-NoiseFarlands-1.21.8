/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.AbstractNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class NoiseThresholdBlockStateProvider
extends AbstractNoiseBlockStateProvider {
    final static public MapCodec<NoiseThresholdBlockStateProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> NoiseThresholdBlockStateProvider.fillCodecFields(instance).and(instance.group((App)Codec.floatRange(-1.0f, 1.0f).fieldOf("threshold").forGetter(noiseThresholdBlockStateProvider -> Float.valueOf(noiseThresholdBlockStateProvider.threshold)), (App)Codec.floatRange(0.0f, 1.0f).fieldOf("high_chance").forGetter(noiseThresholdBlockStateProvider -> Float.valueOf(noiseThresholdBlockStateProvider.highChance)), (App)BlockState.CODEC.fieldOf("default_state").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.defaultState), (App)Codecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("low_states").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.lowStates), (App)Codecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("high_states").forGetter(noiseThresholdBlockStateProvider -> noiseThresholdBlockStateProvider.highStates))).apply((Applicative)instance, NoiseThresholdBlockStateProvider::new));
    final private float threshold;
    final private float highChance;
    final private BlockState defaultState;
    final private List<BlockState> lowStates;
    final private List<BlockState> highStates;

    public NoiseThresholdBlockStateProvider(long seed, DoublePerlinNoiseSampler.NoiseParameters noiseParameters, float scale, float threshold, float highChance, BlockState defaultState, List<BlockState> lowStates, List<BlockState> highStates) {
        super(seed, noiseParameters, scale);
        this.threshold = threshold;
        this.highChance = highChance;
        this.defaultState = defaultState;
        this.lowStates = lowStates;
        this.highStates = highStates;
    }

    @Override
    protected BlockStateProviderType<?> getType() {
        return BlockStateProviderType.NOISE_THRESHOLD_PROVIDER;
    }

    @Override
    public BlockState get(Random random, BlockPos pos) {
        double d = this.getNoiseValue(pos, this.scale);
        if (d < (double)this.threshold) {
            return Util.getRandom(this.lowStates, random);
        }
        if (random.nextFloat() < this.highChance) {
            return Util.getRandom(this.highStates, random);
        }
        return this.defaultState;
    }
}

