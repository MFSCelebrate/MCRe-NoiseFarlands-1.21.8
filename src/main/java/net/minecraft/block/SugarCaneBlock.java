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
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class SugarCaneBlock
extends Block {
    final static public MapCodec<SugarCaneBlock> CODEC = SugarCaneBlock.createCodec(SugarCaneBlock::new);
    final static public IntProperty AGE = Properties.AGE_15;
    final static private VoxelShape SHAPE = Block.createColumnShape(12.0, 0.0, 16.0);

    public MapCodec<SugarCaneBlock> getCodec() {
        return CODEC;
    }

    public SugarCaneBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isAir(pos.net_minecraft_util_math_BlockPos_up())) {
            int i = 1;
            while (world.getBlockState(pos.net_minecraft_util_math_BlockPos_down(i)).isOf(this)) {
                ++i;
            }
            if (i < 3) {
                int j = state.get(AGE);
                if (j == 15) {
                    world.setBlockState(pos.net_minecraft_util_math_BlockPos_up(), this.getDefaultState());
                    world.setBlockState(pos, (BlockState)state.with(AGE, 0), Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                } else {
                    world.setBlockState(pos, (BlockState)state.with(AGE, j + 1), Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                }
            }
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, this, 1);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.net_minecraft_util_math_BlockPos_down());
        if (blockState.isOf(this)) {
            return true;
        }
        if (blockState.isIn(BlockTags.DIRT) || blockState.isIn(BlockTags.SAND)) {
            BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_down();
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockState blockState2 = world.getBlockState(blockPos.net_minecraft_util_math_BlockPos_offset(direction));
                FluidState fluidState = world.getFluidState(blockPos.net_minecraft_util_math_BlockPos_offset(direction));
                if (!fluidState.isIn(FluidTags.WATER) && !blockState2.isOf(Blocks.FROSTED_ICE)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

