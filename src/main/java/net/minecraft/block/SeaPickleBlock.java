/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  org.jetbrains.annotations.Nullable
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
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class SeaPickleBlock
extends PlantBlock
implements Fertilizable,
Waterloggable {
    final static public MapCodec<SeaPickleBlock> CODEC = SeaPickleBlock.createCodec(SeaPickleBlock::new);
    final static public int MAX_PICKLES = 4;
    final static public IntProperty PICKLES = Properties.PICKLES;
    final static public BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    final static private VoxelShape ONE_PICKLE_SHAPE = Block.createColumnShape(4.0, 0.0, 6.0);
    final static private VoxelShape TWO_PICKLES_SHAPE = Block.createColumnShape(10.0, 0.0, 6.0);
    final static private VoxelShape THREE_PICKLES_SHAPE = Block.createColumnShape(12.0, 0.0, 6.0);
    final static private VoxelShape FOUR_PICKLES_SHAPE = Block.createColumnShape(12.0, 0.0, 7.0);

    public MapCodec<SeaPickleBlock> getCodec() {
        return CODEC;
    }

    public SeaPickleBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(PICKLES, 1)).with(WATERLOGGED, true));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            return (BlockState)blockState.with(PICKLES, Math.min(4, blockState.get(PICKLES) + 1));
        }
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)super.getPlacementState(ctx).with(WATERLOGGED, bl);
    }

    public static boolean isDry(BlockState state) {
        return state.get(WATERLOGGED) == false;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return !floor.getCollisionShape(world, pos).getFace(Direction.UP).isEmpty() || floor.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_down();
        return this.canPlantOnTop(world.getBlockState(blockPos), world, blockPos);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (state.get(WATERLOGGED).booleanValue()) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(PICKLES) < 4) {
            return true;
        }
        return super.canReplace(state, context);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(PICKLES)) {
            default -> ONE_PICKLE_SHAPE;
            case 2 -> TWO_PICKLES_SHAPE;
            case 3 -> THREE_PICKLES_SHAPE;
            case 4 -> FOUR_PICKLES_SHAPE;
        };
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PICKLES, WATERLOGGED);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !SeaPickleBlock.isDry(state) && world.getBlockState(pos.net_minecraft_util_math_BlockPos_down()).isIn(BlockTags.CORAL_BLOCKS);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = 5;
        int j = 1;
        int k = 2;
        int l = 0;
        int m = pos.getX() - 2;
        int n = 0;
        for (int o = 0; o < 5; ++o) {
            for (int p = 0; p < j; ++p) {
                int q = 2 + pos.getY() - 1;
                for (int r = q - 2; r < q; ++r) {
                    BlockState blockState;
                    BlockPos blockPos = new BlockPos(m + o, r, pos.getZ() - n + p);
                    if (blockPos == pos || random.nextInt(6) != 0 || !world.getBlockState(blockPos).isOf(Blocks.WATER) || !(blockState = world.getBlockState(blockPos.net_minecraft_util_math_BlockPos_down())).isIn(BlockTags.CORAL_BLOCKS)) continue;
                    world.setBlockState(blockPos, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, random.nextInt(4) + 1), Block.NOTIFY_ALL);
                }
            }
            if (l < 2) {
                j += 2;
                ++n;
            } else {
                j -= 2;
                --n;
            }
            ++l;
        }
        world.setBlockState(pos, (BlockState)state.with(PICKLES, 4), Block.NOTIFY_LISTENERS);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

