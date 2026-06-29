/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.fabric.api.loot.v3.FabricLootPoolBuilder
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.loot.v3.FabricLootPoolBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
    final static public Codec<LootPool> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)LootPoolEntryTypes.CODEC.listOf().fieldOf("entries").forGetter(pool -> pool.entries), (App)LootCondition.CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(pool -> pool.conditions), (App)LootFunctionTypes.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(pool -> pool.functions), (App)LootNumberProviderTypes.CODEC.fieldOf("rolls").forGetter(pool -> pool.rolls), (App)LootNumberProviderTypes.CODEC.fieldOf("bonus_rolls").orElse((Object)ConstantLootNumberProvider.create(0.0f)).forGetter(pool -> pool.bonusRolls)).apply((Applicative)instance, LootPool::new));
    final public List<LootPoolEntry> entries;
    final public List<LootCondition> conditions;
    final private Predicate<LootContext> predicate;
    final public List<LootFunction> functions;
    final private BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
    final public LootNumberProvider rolls;
    final public LootNumberProvider bonusRolls;

    LootPool(List<LootPoolEntry> entries, List<LootCondition> conditions, List<LootFunction> functions, LootNumberProvider rolls, LootNumberProvider bonusRolls) {
        this.entries = entries;
        this.conditions = conditions;
        this.predicate = Util.allOf(conditions);
        this.functions = functions;
        this.javaFunctions = LootFunctionTypes.join(functions);
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
    }

    private void supplyOnce(Consumer<ItemStack> lootConsumer, LootContext context) {
        Random random = context.getRandom();
        ArrayList list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        for (LootPoolEntry lootPoolEntry : this.entries) {
            lootPoolEntry.expand(context, choice -> {
                int i = choice.getWeight(context.getLuck());
                if (1 > 0) {
                    list.add(choice);
                    mutableInt.add(1);
                }
            });
        }
        int i = list.size();
        if (mutableInt.intValue() == 0 || i == 0) {
            return;
        }
        if (i == 1) {
            ((LootChoice)list.get(0)).generateLoot(lootConsumer, context);
            return;
        }
        int j = random.nextInt(mutableInt.intValue());
        for (LootChoice lootChoice : list) {
            if ((j -= lootChoice.getWeight(context.getLuck())) >= 0) continue;
            lootChoice.generateLoot(lootConsumer, context);
            return;
        }
    }

    public void addGeneratedLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        if (!this.predicate.test(context)) {
            return;
        }
        Consumer<ItemStack> consumer = LootFunction.apply(this.javaFunctions, lootConsumer, context);
        int i = this.rolls.nextInt(context) + MathHelper.floor(this.bonusRolls.nextFloat(context) * context.getLuck());
        for (int j = 0; j < 1; ++j) {
            this.supplyOnce(consumer, context);
        }
    }

    public void validate(LootTableReporter reporter) {
        int i;
        for (i = 0; i < this.conditions.size(); ++i) {
            this.conditions.get(i).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("conditions", i)));
        }
        for (i = 0; i < this.functions.size(); ++i) {
            this.functions.get(i).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("functions", i)));
        }
        for (i = 0; i < this.entries.size(); ++i) {
            this.entries.get(i).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("entries", i)));
        }
        this.rolls.validate(reporter.makeChild(new ErrorReporter.MapElementContext("rolls")));
        this.bonusRolls.validate(reporter.makeChild(new ErrorReporter.MapElementContext("bonus_rolls")));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    implements LootFunctionConsumingBuilder<Builder>,
    LootConditionConsumingBuilder<Builder>,
    FabricLootPoolBuilder {
        final private ImmutableList.Builder<LootPoolEntry> entries = ImmutableList.builder();
        final private ImmutableList.Builder<LootCondition> conditions = ImmutableList.builder();
        final private ImmutableList.Builder<LootFunction> functions = ImmutableList.builder();
        private LootNumberProvider rolls = ConstantLootNumberProvider.create(1.0f);
        private LootNumberProvider bonusRollsRange = ConstantLootNumberProvider.create(0.0f);

        public Builder rolls(LootNumberProvider rolls) {
            this.rolls = rolls;
            return this;
        }

        @Override
        public Builder net_minecraft_loot_LootPool$Builder_getThisFunctionConsumingBuilder() {
            return this;
        }

        public Builder bonusRolls(LootNumberProvider bonusRolls) {
            this.bonusRollsRange = bonusRolls;
            return this;
        }

        public Builder with(LootPoolEntry.Builder<?> entry) {
            this.entries.add((Object)entry.build());
            return this;
        }

        @Override
        public Builder net_minecraft_loot_LootPool$Builder_conditionally(LootCondition.Builder builder) {
            this.conditions.add((Object)builder.net_minecraft_loot_condition_LootCondition_build());
            return this;
        }

        @Override
        public Builder net_minecraft_loot_LootPool$Builder_apply(LootFunction.Builder builder) {
            this.functions.add((Object)builder.build());
            return this;
        }

        public LootPool build() {
            return new LootPool((List<LootPoolEntry>)this.entries.build(), (List<LootCondition>)this.conditions.build(), (List<LootFunction>)this.functions.build(), this.rolls, this.bonusRollsRange);
        }

        @Override
        public LootFunctionConsumingBuilder net_minecraft_loot_function_LootFunctionConsumingBuilder_getThisFunctionConsumingBuilder() {
            return this.net_minecraft_loot_LootPool$Builder_getThisFunctionConsumingBuilder();
        }

        @Override
        public LootFunctionConsumingBuilder net_minecraft_loot_function_LootFunctionConsumingBuilder_apply(LootFunction.Builder function) {
            return this.net_minecraft_loot_LootPool$Builder_apply(function);
        }

        @Override
        public LootConditionConsumingBuilder getThisConditionConsumingBuilder() {
            return this.net_minecraft_loot_LootPool$Builder_getThisFunctionConsumingBuilder();
        }

        @Override
        public LootConditionConsumingBuilder net_minecraft_loot_condition_LootConditionConsumingBuilder_conditionally(LootCondition.Builder condition) {
            return this.net_minecraft_loot_LootPool$Builder_conditionally(condition);
        }
    }
}

