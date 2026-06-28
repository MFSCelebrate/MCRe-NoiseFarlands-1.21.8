/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.CappedStructureProcessor;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.LavaSubmergedBlockStructureProcessor;
import net.minecraft.structure.processor.NopStructureProcessor;
import net.minecraft.structure.processor.ProtectedBlocksStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;

public interface StructureProcessorType<P extends StructureProcessor> {
    final static public Codec<StructureProcessor> CODEC = Registries.STRUCTURE_PROCESSOR.getCodec().dispatch("processor_type", StructureProcessor::getType, StructureProcessorType::codec);
    final static public Codec<StructureProcessorList> LIST_CODEC = CODEC.listOf().xmap(StructureProcessorList::new, StructureProcessorList::getList);
    final static public Codec<StructureProcessorList> PROCESSORS_CODEC = Codec.withAlternative((Codec)LIST_CODEC.fieldOf("processors").codec(), LIST_CODEC);
    final static public Codec<RegistryEntry<StructureProcessorList>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PROCESSOR_LIST, PROCESSORS_CODEC);
    final static public StructureProcessorType<BlockIgnoreStructureProcessor> BLOCK_IGNORE = StructureProcessorType.register("block_ignore", BlockIgnoreStructureProcessor.CODEC);
    final static public StructureProcessorType<BlockRotStructureProcessor> BLOCK_ROT = StructureProcessorType.register("block_rot", BlockRotStructureProcessor.CODEC);
    final static public StructureProcessorType<GravityStructureProcessor> GRAVITY = StructureProcessorType.register("gravity", GravityStructureProcessor.CODEC);
    final static public StructureProcessorType<JigsawReplacementStructureProcessor> JIGSAW_REPLACEMENT = StructureProcessorType.register("jigsaw_replacement", JigsawReplacementStructureProcessor.CODEC);
    final static public StructureProcessorType<RuleStructureProcessor> RULE = StructureProcessorType.register("rule", RuleStructureProcessor.CODEC);
    final static public StructureProcessorType<NopStructureProcessor> NOP = StructureProcessorType.register("nop", NopStructureProcessor.CODEC);
    final static public StructureProcessorType<BlockAgeStructureProcessor> BLOCK_AGE = StructureProcessorType.register("block_age", BlockAgeStructureProcessor.CODEC);
    final static public StructureProcessorType<BlackstoneReplacementStructureProcessor> BLACKSTONE_REPLACE = StructureProcessorType.register("blackstone_replace", BlackstoneReplacementStructureProcessor.CODEC);
    final static public StructureProcessorType<LavaSubmergedBlockStructureProcessor> LAVA_SUBMERGED_BLOCK = StructureProcessorType.register("lava_submerged_block", LavaSubmergedBlockStructureProcessor.CODEC);
    final static public StructureProcessorType<ProtectedBlocksStructureProcessor> PROTECTED_BLOCKS = StructureProcessorType.register("protected_blocks", ProtectedBlocksStructureProcessor.CODEC);
    final static public StructureProcessorType<CappedStructureProcessor> CAPPED = StructureProcessorType.register("capped", CappedStructureProcessor.CODEC);

    public MapCodec<P> codec();

    public static <P extends StructureProcessor> StructureProcessorType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.STRUCTURE_PROCESSOR, id, () -> codec);
    }
}

