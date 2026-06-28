/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.structure.processor;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class JigsawReplacementStructureProcessor
extends StructureProcessor {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public MapCodec<JigsawReplacementStructureProcessor> CODEC = MapCodec.unit(() -> INSTANCE);
    final static public JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

    private JigsawReplacementStructureProcessor() {
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        BlockState blockState2;
        BlockState blockState = currentBlockInfo.state();
        if (!blockState.isOf(Blocks.JIGSAW)) {
            return currentBlockInfo;
        }
        if (currentBlockInfo.nbt() == null) {
            LOGGER.warn("Jigsaw block at {} is missing nbt, will not replace", (Object)pos);
            return currentBlockInfo;
        }
        String string = currentBlockInfo.nbt().getString("final_state", "minecraft:air");
        try {
            BlockArgumentParser.BlockResult blockResult = BlockArgumentParser.block(world.createCommandRegistryWrapper(RegistryKeys.BLOCK), string, true);
            blockState2 = blockResult.blockState();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.error("Failed to parse jigsaw replacement state '{}' at {}: {}", new Object[]{string, pos, commandSyntaxException.getMessage()});
            return null;
        }
        if (blockState2.isOf(Blocks.STRUCTURE_VOID)) {
            return null;
        }
        return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), blockState2, null);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.JIGSAW_REPLACEMENT;
    }
}

