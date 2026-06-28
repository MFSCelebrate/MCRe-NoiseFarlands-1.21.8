/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.context.ContextParameter;

public class EnchantedCountIncreaseLootFunction
extends ConditionalLootFunction {
    final static public int DEFAULT_LIMIT = 0;
    final static public MapCodec<EnchantedCountIncreaseLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> EnchantedCountIncreaseLootFunction.addConditionsField(instance).and(instance.group((App)Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(function -> function.enchantment), (App)LootNumberProviderTypes.CODEC.fieldOf("count").forGetter(function -> function.count), (App)Codec.INT.optionalFieldOf("limit", (Object)0).forGetter(function -> function.limit))).apply((Applicative)instance, EnchantedCountIncreaseLootFunction::new));
    final private RegistryEntry<Enchantment> enchantment;
    final private LootNumberProvider count;
    final private int limit;

    EnchantedCountIncreaseLootFunction(List<LootCondition> conditions, RegistryEntry<Enchantment> enchantment, LootNumberProvider count, int limit) {
        super(conditions);
        this.enchantment = enchantment;
        this.count = count;
        this.limit = limit;
    }

    public LootFunctionType<EnchantedCountIncreaseLootFunction> getType() {
        return LootFunctionTypes.ENCHANTED_COUNT_INCREASE;
    }

    @Override
    public Set<ContextParameter<?>> getAllowedParameters() {
        return Sets.union((Set)ImmutableSet.of(LootContextParameters.ATTACKING_ENTITY), this.count.getAllowedParameters());
    }

    private boolean hasLimit() {
        return this.limit > 0;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Entity entity = context.get(LootContextParameters.ATTACKING_ENTITY);
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            int i = EnchantmentHelper.getEquipmentLevel(this.enchantment, livingEntity);
            if (i == 0) {
                return stack;
            }
            float f = (float)i * this.count.nextFloat(context);
            stack.increment(Math.round(f));
            if (this.hasLimit()) {
                stack.capCount(this.limit);
            }
        }
        return stack;
    }

    public static Builder builder(RegistryWrapper.WrapperLookup registries, LootNumberProvider count) {
        RegistryEntryLookup impl = registries.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.ENCHANTMENT);
        return new Builder(impl.getOrThrow(Enchantments.LOOTING), count);
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        final private RegistryEntry<Enchantment> enchantment;
        final private LootNumberProvider count;
        private int limit = 0;

        public Builder(RegistryEntry<Enchantment> enchantment, LootNumberProvider count) {
            this.enchantment = enchantment;
            this.count = count;
        }

        @Override
        protected Builder net_minecraft_loot_function_EnchantedCountIncreaseLootFunction$Builder_getThisBuilder() {
            return this;
        }

        public Builder withLimit(int limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public LootFunction build() {
            return new EnchantedCountIncreaseLootFunction(this.getConditions(), this.enchantment, this.count, this.limit);
        }

        @Override
        protected ConditionalLootFunction.Builder net_minecraft_loot_function_ConditionalLootFunction$Builder_getThisBuilder() {
            return this.net_minecraft_loot_function_EnchantedCountIncreaseLootFunction$Builder_getThisBuilder();
        }
    }
}

