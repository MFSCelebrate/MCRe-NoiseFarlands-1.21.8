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
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ShortPlantBlock
extends PlantBlock
implements Fertilizable {
    final static public MapCodec<ShortPlantBlock> CODEC = ShortPlantBlock.createCodec(ShortPlantBlock::new);
    final static private VoxelShape SHAPE = Block.createColumnShape(12.0, 0.0, 13.0);

    public MapCodec<ShortPlantBlock> getCodec() {
        return CODEC;
    }

    public ShortPlantBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return ShortPlantBlock.getLargeVariant(state).getDefaultState().canPlaceAt(world, pos) && world.isAir(pos.net_minecraft_util_math_BlockPos_up());
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        TallPlantBlock.placeAt(world, ShortPlantBlock.getLargeVariant(state).getDefaultState(), pos, 2);
    }

    private static TallPlantBlock getLargeVariant(BlockState state) {
        return (TallPlantBlock)(state.isOf(Blocks.FERN) ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);
    }
}

