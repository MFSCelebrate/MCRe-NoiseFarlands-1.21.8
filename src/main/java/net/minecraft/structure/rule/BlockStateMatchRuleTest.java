/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.math.random.Random;

public class BlockStateMatchRuleTest
extends RuleTest {
    final static public MapCodec<BlockStateMatchRuleTest> CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchRuleTest::new, ruleTest -> ruleTest.blockState);
    final private BlockState blockState;

    public BlockStateMatchRuleTest(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public boolean test(BlockState state, Random random) {
        return state == this.blockState;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.BLOCKSTATE_MATCH;
    }
}

