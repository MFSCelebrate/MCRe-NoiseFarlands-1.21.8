/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class MushroomBlock
extends Block {
    final static public MapCodec<MushroomBlock> CODEC = MushroomBlock.createCodec(MushroomBlock::new);
    final static public BooleanProperty NORTH = ConnectingBlock.NORTH;
    final static public BooleanProperty EAST = ConnectingBlock.EAST;
    final static public BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    final static public BooleanProperty WEST = ConnectingBlock.WEST;
    final static public BooleanProperty UP = ConnectingBlock.UP;
    final static public BooleanProperty DOWN = ConnectingBlock.DOWN;
    final static private Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

    public MapCodec<MushroomBlock> getCodec() {
        return CODEC;
    }

    public MushroomBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, true)).with(EAST, true)).with(SOUTH, true)).with(WEST, true)).with(UP, true)).with(DOWN, true));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(DOWN, !blockView.getBlockState(blockPos.net_minecraft_util_math_BlockPos_down()).isOf(this))).with(UP, !blockView.getBlockState(blockPos.net_minecraft_util_math_BlockPos_up()).isOf(this))).with(NORTH, !blockView.getBlockState(blockPos.net_minecraft_util_math_BlockPos_north()).isOf(this))).with(EAST, !blockView.getBlockState(blockPos.net_minecraft_util_math_BlockPos_east()).isOf(this))).with(SOUTH, !blockView.getBlockState(blockPos.net_minecraft_util_math_BlockPos_south()).isOf(this))).with(WEST, !blockView.getBlockState(blockPos.net_minecraft_util_math_BlockPos_west()).isOf(this));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (neighborState.isOf(this)) {
            return (BlockState)state.with(FACING_PROPERTIES.get(direction), false);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with(FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), state.get(NORTH))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), state.get(SOUTH))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), state.get(EAST))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), state.get(WEST))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), state.get(UP))).with(FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), state.get(DOWN));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with(FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), state.get(NORTH))).with(FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), state.get(SOUTH))).with(FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), state.get(EAST))).with(FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), state.get(WEST))).with(FACING_PROPERTIES.get(mirror.apply(Direction.UP)), state.get(UP))).with(FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), state.get(DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}

