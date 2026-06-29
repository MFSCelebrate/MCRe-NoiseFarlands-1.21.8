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
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FungusBlock
extends PlantBlock
implements Fertilizable {
    final static public MapCodec<FungusBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec(RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter(block -> block.featureKey), (App)Registries.BLOCK.getCodec().fieldOf("grows_on").forGetter(block -> block.nylium), FungusBlock.createSettingsCodec()).apply((Applicative)instance, FungusBlock::new));
    final static private double GROW_CHANCE = 0.4;
    final static private VoxelShape SHAPE = Block.createColumnShape(8.0, 0.0, 9.0);
    final private Block nylium;
    final private RegistryKey<ConfiguredFeature<?, ?>> featureKey;

    public MapCodec<FungusBlock> getCodec() {
        return CODEC;
    }

    public FungusBlock(RegistryKey<ConfiguredFeature<?, ?>> featureKey, Block nylium, AbstractBlock.Settings settings) {
        super(settings);
        this.featureKey = featureKey;
        this.nylium = nylium;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(BlockTags.NYLIUM) || floor.isOf(Blocks.MYCELIUM) || floor.isOf(Blocks.SOUL_SOIL) || super.canPlantOnTop(floor, world, pos);
    }

    private Optional<? extends RegistryEntry<ConfiguredFeature<?, ?>>> getFeatureEntry(WorldView world) {
        return world.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.CONFIGURED_FEATURE).getOptional(this.featureKey);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        BlockState blockState = world.getBlockState(pos.net_minecraft_util_math_BlockPos_down());
        return blockState.isOf(this.nylium);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.getFeatureEntry(world).ifPresent(featureEntry -> ((ConfiguredFeature)featureEntry.value()).generate(world, world.net_minecraft_server_world_ServerChunkManager_getChunkManager().getChunkGenerator(), random, pos));
    }
}

