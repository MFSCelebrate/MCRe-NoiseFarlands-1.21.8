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
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
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

public class MushroomPlantBlock
extends PlantBlock
implements Fertilizable {
    final static public MapCodec<MushroomPlantBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RegistryKey.createCodec(RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter(block -> block.featureKey), MushroomPlantBlock.createSettingsCodec()).apply((Applicative)instance, MushroomPlantBlock::new));
    final static private VoxelShape SHAPE = Block.createColumnShape(6.0, 0.0, 6.0);
    final private RegistryKey<ConfiguredFeature<?, ?>> featureKey;

    public MapCodec<MushroomPlantBlock> getCodec() {
        return CODEC;
    }

    public MushroomPlantBlock(RegistryKey<ConfiguredFeature<?, ?>> featureKey, AbstractBlock.Settings settings) {
        super(settings);
        this.featureKey = featureKey;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            int j = 4;
            for (BlockPos blockPos : BlockPos.iterate(pos.net_minecraft_util_math_BlockPos_add(-4, -1, -4), pos.net_minecraft_util_math_BlockPos_add(4, 1, 4))) {
                if (!world.getBlockState(blockPos).isOf(this) || --i > 0) continue;
                return;
            }
            BlockPos blockPos2 = pos.net_minecraft_util_math_BlockPos_add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; ++k) {
                if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                    pos = blockPos2;
                }
                blockPos2 = pos.net_minecraft_util_math_BlockPos_add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
                world.setBlockState(blockPos2, state, Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOpaqueFullCube();
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_down();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }
        return world.getBaseLightLevel(pos, 0) < 13 && this.canPlantOnTop(blockState, world, blockPos);
    }

    public boolean trySpawningBigMushroom(ServerWorld world, BlockPos pos, BlockState state, Random random) {
        Optional optional = world.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.CONFIGURED_FEATURE).getOptional(this.featureKey);
        if (optional.isEmpty()) {
            return false;
        }
        world.removeBlock(pos, false);
        if (((ConfiguredFeature)((RegistryEntry)optional.get()).value()).generate(world, world.net_minecraft_server_world_ServerChunkManager_getChunkManager().getChunkGenerator(), random, pos)) {
            return true;
        }
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        return false;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.trySpawningBigMushroom(world, pos, state, random);
    }
}

