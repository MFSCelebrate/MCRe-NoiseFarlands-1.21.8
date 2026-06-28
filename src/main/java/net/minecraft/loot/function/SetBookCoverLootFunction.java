/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.loot.function;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.dynamic.Codecs;

public class SetBookCoverLootFunction
extends ConditionalLootFunction {
    final static public MapCodec<SetBookCoverLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> SetBookCoverLootFunction.addConditionsField(instance).and(instance.group((App)RawFilteredPair.createCodec(Codec.string(0, 32)).optionalFieldOf("title").forGetter(function -> function.title), (App)Codec.STRING.optionalFieldOf("author").forGetter(function -> function.author), (App)Codecs.rangedInt(0, 3).optionalFieldOf("generation").forGetter(function -> function.generation))).apply((Applicative)instance, SetBookCoverLootFunction::new));
    final private Optional<String> author;
    final private Optional<RawFilteredPair<String>> title;
    final private Optional<Integer> generation;

    public SetBookCoverLootFunction(List<LootCondition> conditions, Optional<RawFilteredPair<String>> title, Optional<String> author, Optional<Integer> generation) {
        super(conditions);
        this.author = author;
        this.title = title;
        this.generation = generation;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        stack.apply(DataComponentTypes.WRITTEN_BOOK_CONTENT, WrittenBookContentComponent.DEFAULT, this::apply);
        return stack;
    }

    private WrittenBookContentComponent apply(WrittenBookContentComponent current) {
        return new WrittenBookContentComponent(this.title.orElseGet(current::title), this.author.orElseGet(current::author), this.generation.orElseGet(current::generation), current.pages(), current.resolved());
    }

    public LootFunctionType<SetBookCoverLootFunction> getType() {
        return LootFunctionTypes.SET_BOOK_COVER;
    }
}

