/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;

public interface RuleTestType<P extends RuleTest> {
    final static public RuleTestType<AlwaysTrueRuleTest> ALWAYS_TRUE = RuleTestType.register("always_true", AlwaysTrueRuleTest.CODEC);
    final static public RuleTestType<BlockMatchRuleTest> BLOCK_MATCH = RuleTestType.register("block_match", BlockMatchRuleTest.CODEC);
    final static public RuleTestType<BlockStateMatchRuleTest> BLOCKSTATE_MATCH = RuleTestType.register("blockstate_match", BlockStateMatchRuleTest.CODEC);
    final static public RuleTestType<TagMatchRuleTest> TAG_MATCH = RuleTestType.register("tag_match", TagMatchRuleTest.CODEC);
    final static public RuleTestType<RandomBlockMatchRuleTest> RANDOM_BLOCK_MATCH = RuleTestType.register("random_block_match", RandomBlockMatchRuleTest.CODEC);
    final static public RuleTestType<RandomBlockStateMatchRuleTest> RANDOM_BLOCKSTATE_MATCH = RuleTestType.register("random_blockstate_match", RandomBlockStateMatchRuleTest.CODEC);

    public MapCodec<P> codec();

    public static <P extends RuleTest> RuleTestType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.RULE_TEST, id, () -> codec);
    }
}

