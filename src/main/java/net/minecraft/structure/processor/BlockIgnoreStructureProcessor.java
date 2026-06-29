/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.MapCodec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockIgnoreStructureProcessor
extends StructureProcessor {
    final static public MapCodec<BlockIgnoreStructureProcessor> CODEC = BlockState.CODEC.xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState).listOf().fieldOf("blocks").xmap(BlockIgnoreStructureProcessor::new, processor -> processor.blocks);
    final static public BlockIgnoreStructureProcessor IGNORE_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.STRUCTURE_BLOCK));
    final static public BlockIgnoreStructureProcessor IGNORE_AIR = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.AIR));
    final static public BlockIgnoreStructureProcessor IGNORE_AIR_AND_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor((List<Block>)ImmutableList.of((Object)Blocks.AIR, (Object)Blocks.STRUCTURE_BLOCK));
    final private ImmutableList<Block> blocks;

    public BlockIgnoreStructureProcessor(List<Block> blocks) {
        this.blocks = ImmutableList.copyOf(blocks);
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        if (this.blocks.contains((Object)currentBlockInfo.state().getBlock())) {
            return null;
        }
        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_IGNORE;
    }
}

