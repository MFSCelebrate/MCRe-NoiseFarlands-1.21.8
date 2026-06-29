/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class MossBlock
extends Block
implements Fertilizable {
    final static public MapCodec<MossBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec(RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter(block -> block.feature), MossBlock.createSettingsCodec()).apply((Applicative)instance, MossBlock::new));
    final private RegistryKey<ConfiguredFeature<?, ?>> feature;

    public MapCodec<MossBlock> getCodec() {
        return CODEC;
    }

    public MossBlock(RegistryKey<ConfiguredFeature<?, ?>> feature, AbstractBlock.Settings settings) {
        super(settings);
        this.feature = feature;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.net_minecraft_util_math_BlockPos_up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.getRegistryManager().getOptional(RegistryKeys.CONFIGURED_FEATURE).flatMap(registry -> registry.getOptional(this.feature)).ifPresent(entry -> ((ConfiguredFeature)entry.value()).generate(world, world.net_minecraft_server_world_ServerChunkManager_getChunkManager().getChunkGenerator(), random, pos.net_minecraft_util_math_BlockPos_up()));
    }

    @Override
    public Fertilizable.FertilizableType getFertilizableType() {
        return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
    }
}

