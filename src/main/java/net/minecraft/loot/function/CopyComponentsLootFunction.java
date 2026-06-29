/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameter;

public class CopyComponentsLootFunction
extends ConditionalLootFunction {
    final static public MapCodec<CopyComponentsLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> CopyComponentsLootFunction.addConditionsField(instance).and(instance.group((App)Source.CODEC.fieldOf("source").forGetter(function -> function.source), (App)ComponentType.CODEC.listOf().optionalFieldOf("include").forGetter(function -> function.include), (App)ComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter(function -> function.exclude))).apply((Applicative)instance, CopyComponentsLootFunction::new));
    final private Source source;
    final private Optional<List<ComponentType<?>>> include;
    final private Optional<List<ComponentType<?>>> exclude;
    final private Predicate<ComponentType<?>> filter;

    CopyComponentsLootFunction(List<LootCondition> conditions, Source source, Optional<List<ComponentType<?>>> include, Optional<List<ComponentType<?>>> exclude) {
        super(conditions);
        this.source = source;
        this.include = include.map(List::copyOf);
        this.exclude = exclude.map(List::copyOf);
        ArrayList list = new ArrayList(2);
        exclude.ifPresent(excludedTypes -> list.add(type -> !excludedTypes.contains(type)));
        include.ifPresent(includedTypes -> list.add(includedTypes::contains));
        this.filter = Util.allOf(list);
    }

    public LootFunctionType<CopyComponentsLootFunction> getType() {
        return LootFunctionTypes.COPY_COMPONENTS;
    }

    @Override
    public Set<ContextParameter<?>> getAllowedParameters() {
        return this.source.getRequiredParameters();
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ComponentMap componentMap = this.source.getComponents(context);
        stack.applyComponentsFrom(componentMap.filtered(this.filter));
        return stack;
    }

    public static Builder builder(Source source) {
        return new Builder(source);
    }

    public static final class Source
    extends Enum<Source>
    implements StringIdentifiable {
        final static public Source BLOCK_ENTITY = new Source("block_entity");
        final static public Codec<Source> CODEC;
        final private String id;
        final static private Source[] field_49439;

        public static Source[] values() {
            return (Source[])field_49439.clone();
        }

        public static Source valueOf(String string) {
            return Enum.valueOf(Source.class, string);
        }

        private Source(String id) {
            this.id = id;
        }

        public ComponentMap getComponents(LootContext context) {
            switch (this.ordinal()) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: 
            }
            BlockEntity blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);
            return blockEntity != null ? blockEntity.createComponentMap() : ComponentMap.EMPTY;
        }

        public Set<ContextParameter<?>> getRequiredParameters() {
            switch (this.ordinal()) {
                default: {
                    throw new MatchException(null, null);
                }
                case 0: 
            }
            return Set.of(LootContextParameters.BLOCK_ENTITY);
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static Source[] method_57645() {
            return new Source[]{BLOCK_ENTITY};
        }

        static {
            field_49439 = Source.method_57645();
            CODEC = StringIdentifiable.createBasicCodec(Source::values);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        final private Source source;
        private Optional<ImmutableList.Builder<ComponentType<?>>> include = Optional.empty();
        private Optional<ImmutableList.Builder<ComponentType<?>>> exclude = Optional.empty();

        Builder(Source source) {
            this.source = source;
        }

        public Builder include(ComponentType<?> type) {
            if (this.include.isEmpty()) {
                this.include = Optional.of(ImmutableList.builder());
            }
            this.include.get().add(type);
            return this;
        }

        public Builder exclude(ComponentType<?> type) {
            if (this.exclude.isEmpty()) {
                this.exclude = Optional.of(ImmutableList.builder());
            }
            this.exclude.get().add(type);
            return this;
        }

        @Override
        protected Builder net_minecraft_loot_function_CopyComponentsLootFunction$Builder_getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new CopyComponentsLootFunction(this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build));
        }

        @Override
        protected ConditionalLootFunction.Builder net_minecraft_loot_function_ConditionalLootFunction$Builder_getThisBuilder() {
            return this.net_minecraft_loot_function_CopyComponentsLootFunction$Builder_getThisBuilder();
        }
    }
}

