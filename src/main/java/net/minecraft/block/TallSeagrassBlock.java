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
import net.minecraft.block.FluidFillable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class TallSeagrassBlock
extends TallPlantBlock
implements FluidFillable {
    final static public MapCodec<TallSeagrassBlock> CODEC = TallSeagrassBlock.createCodec(TallSeagrassBlock::new);
    final static public EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;
    final static private VoxelShape SHAPE = Block.createColumnShape(12.0, 0.0, 16.0);

    public MapCodec<TallSeagrassBlock> getCodec() {
        return CODEC;
    }

    public TallSeagrassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) && !floor.isOf(Blocks.MAGMA_BLOCK);
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Blocks.SEAGRASS);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState;
        BlockState blockState = super.getPlacementState(ctx);
        if (blockState != null && (fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos().net_minecraft_util_math_BlockPos_up())).isIn(FluidTags.WATER) && fluidState.getLevel() == 8) {
            return blockState;
        }
        return null;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState blockState = world.getBlockState(pos.net_minecraft_util_math_BlockPos_down());
            return blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.LOWER;
        }
        FluidState fluidState = world.getFluidState(pos);
        return super.canPlaceAt(state, world, pos) && fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    @Override
    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}

