/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCoralBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DeadCoralBlock
extends AbstractCoralBlock {
    final static public MapCodec<DeadCoralBlock> CODEC = DeadCoralBlock.createCodec(DeadCoralBlock::new);
    final static private VoxelShape SHAPE = Block.createColumnShape(12.0, 0.0, 15.0);

    public MapCodec<DeadCoralBlock> getCodec() {
        return CODEC;
    }

    public DeadCoralBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}

