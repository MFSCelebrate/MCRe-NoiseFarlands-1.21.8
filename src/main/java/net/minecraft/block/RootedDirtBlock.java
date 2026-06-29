/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RootedDirtBlock
extends Block
implements Fertilizable {
    final static public MapCodec<RootedDirtBlock> CODEC = RootedDirtBlock.createCodec(RootedDirtBlock::new);

    public MapCodec<RootedDirtBlock> getCodec() {
        return CODEC;
    }

    public RootedDirtBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.net_minecraft_util_math_BlockPos_down()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos.net_minecraft_util_math_BlockPos_down(), Blocks.HANGING_ROOTS.getDefaultState());
    }

    @Override
    public BlockPos getFertilizeParticlePos(BlockPos pos) {
        return pos.net_minecraft_util_math_BlockPos_down();
    }
}

