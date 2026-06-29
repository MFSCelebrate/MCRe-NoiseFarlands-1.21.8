/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.rule.blockentity.AppendLootRuleBlockEntityModifier;
import net.minecraft.structure.rule.blockentity.AppendStaticRuleBlockEntityModifier;
import net.minecraft.structure.rule.blockentity.ClearRuleBlockEntityModifier;
import net.minecraft.structure.rule.blockentity.PassthroughRuleBlockEntityModifier;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifier;

public interface RuleBlockEntityModifierType<P extends RuleBlockEntityModifier> {
    final static public RuleBlockEntityModifierType<ClearRuleBlockEntityModifier> CLEAR = RuleBlockEntityModifierType.register("clear", ClearRuleBlockEntityModifier.CODEC);
    final static public RuleBlockEntityModifierType<PassthroughRuleBlockEntityModifier> PASSTHROUGH = RuleBlockEntityModifierType.register("passthrough", PassthroughRuleBlockEntityModifier.CODEC);
    final static public RuleBlockEntityModifierType<AppendStaticRuleBlockEntityModifier> APPEND_STATIC = RuleBlockEntityModifierType.register("append_static", AppendStaticRuleBlockEntityModifier.CODEC);
    final static public RuleBlockEntityModifierType<AppendLootRuleBlockEntityModifier> APPEND_LOOT = RuleBlockEntityModifierType.register("append_loot", AppendLootRuleBlockEntityModifier.CODEC);

    public MapCodec<P> codec();

    private static <P extends RuleBlockEntityModifier> RuleBlockEntityModifierType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.RULE_BLOCK_ENTITY_MODIFIER, id, () -> codec);
    }
}

