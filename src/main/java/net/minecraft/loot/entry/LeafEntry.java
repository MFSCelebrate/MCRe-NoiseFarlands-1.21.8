/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.loot.entry;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.MathHelper;

public abstract class LeafEntry
extends LootPoolEntry {
    final static public int DEFAULT_WEIGHT = 1;
    final static public int DEFAULT_QUALITY = 0;
    final protected int weight;
    final protected int quality;
    final protected List<LootFunction> functions;
    final BiFunction<ItemStack, LootContext, ItemStack> compiledFunctions;
    final private LootChoice choice = new Choice(){

        @Override
        public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
            LeafEntry.this.generateLoot(LootFunction.apply(LeafEntry.this.compiledFunctions, lootConsumer, context), context);
        }
    };

    protected LeafEntry(int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
        super(conditions);
        this.weight = weight;
        this.quality = quality;
        this.functions = functions;
        this.compiledFunctions = LootFunctionTypes.join(functions);
    }

    protected static <T extends LeafEntry> Products.P4<RecordCodecBuilder.Mu<T>, Integer, Integer, List<LootCondition>, List<LootFunction>> addLeafFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group((App)Codec.INT.optionalFieldOf("weight", (Object)1).forGetter(entry -> entry.weight), (App)Codec.INT.optionalFieldOf("quality", (Object)0).forGetter(entry -> entry.quality)).and(LeafEntry.addConditionsField(instance).t1()).and((App)LootFunctionTypes.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(entry -> entry.functions));
    }

    @Override
    public void validate(LootTableReporter reporter) {
        super.validate(reporter);
        for (int i = 0; i < this.functions.size(); ++i) {
            this.functions.get(i).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("functions", i)));
        }
    }

    protected abstract void generateLoot(Consumer<ItemStack> var1, LootContext var2);

    @Override
    public boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
        if (this.test(lootContext)) {
            consumer.accept(this.choice);
            return true;
        }
        return false;
    }

    public static Builder<?> builder(Factory factory) {
        return new BasicBuilder(factory);
    }

    static class BasicBuilder
    extends Builder<BasicBuilder> {
        final private Factory factory;

        public BasicBuilder(Factory factory) {
            this.factory = factory;
        }

        @Override
        protected BasicBuilder net_minecraft_loot_entry_LeafEntry$BasicBuilder_getThisBuilder() {
            return this;
        }

        @Override
        public LootPoolEntry build() {
            return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }

        @Override
        protected LootPoolEntry.Builder net_minecraft_loot_entry_LootPoolEntry$Builder_getThisBuilder() {
            return this.net_minecraft_loot_entry_LeafEntry$BasicBuilder_getThisBuilder();
        }
    }

    @FunctionalInterface
    protected static interface Factory {
        public LeafEntry build(int var1, int var2, List<LootCondition> var3, List<LootFunction> var4);
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootPoolEntry.Builder<T>
    implements LootFunctionConsumingBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        final private ImmutableList.Builder<LootFunction> functions = ImmutableList.builder();

        @Override
        public T apply(LootFunction.Builder builder) {
            this.functions.add((Object)builder.build());
            return (T)((Builder)this.getThisBuilder());
        }

        protected List<LootFunction> getFunctions() {
            return this.functions.build();
        }

        public T weight(int weight) {
            this.weight = weight;
            return (T)((Builder)this.getThisBuilder());
        }

        public T quality(int quality) {
            this.quality = quality;
            return (T)((Builder)this.getThisBuilder());
        }

        @Override
        public LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
            return (LootFunctionConsumingBuilder)((Object)super.getThisConditionConsumingBuilder());
        }

        @Override
        public LootFunctionConsumingBuilder apply(LootFunction.Builder function) {
            return this.apply(function);
        }
    }

    protected abstract class Choice
    implements LootChoice {
        protected Choice() {
        }

        @Override
        public int getWeight(float luck) {
            return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * luck), 0);
        }
    }
}

