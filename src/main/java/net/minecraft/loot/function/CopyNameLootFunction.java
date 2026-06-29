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
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Nameable;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;

public class CopyNameLootFunction
extends ConditionalLootFunction {
    final static public MapCodec<CopyNameLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> CopyNameLootFunction.addConditionsField(instance).and((App)Source.CODEC.fieldOf("source").forGetter(function -> function.source)).apply((Applicative)instance, CopyNameLootFunction::new));
    final private Source source;

    private CopyNameLootFunction(List<LootCondition> conditions, Source source) {
        super(conditions);
        this.source = source;
    }

    public LootFunctionType<CopyNameLootFunction> getType() {
        return LootFunctionTypes.COPY_NAME;
    }

    @Override
    public Set<ContextParameter<?>> getAllowedParameters() {
        return Set.of(this.source.parameter);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Object object = context.get(this.source.parameter);
        if (object instanceof Nameable) {
            Nameable nameable = (Nameable)object;
            stack.set(DataComponentTypes.CUSTOM_NAME, nameable.getCustomName());
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder(Source source) {
        return CopyNameLootFunction.builder((List<LootCondition> conditions) -> new CopyNameLootFunction((List<LootCondition>)conditions, source));
    }

    public static final class Source
    extends Enum<Source>
    implements StringIdentifiable {
        final static public Source THIS = new Source("this", LootContextParameters.THIS_ENTITY);
        final static public Source ATTACKING_ENTITY = new Source("attacking_entity", LootContextParameters.ATTACKING_ENTITY);
        final static public Source LAST_DAMAGE_PLAYER = new Source("last_damage_player", LootContextParameters.LAST_DAMAGE_PLAYER);
        final static public Source BLOCK_ENTITY = new Source("block_entity", LootContextParameters.BLOCK_ENTITY);
        final static public Codec<Source> CODEC;
        final private String name;
        final ContextParameter<?> parameter;
        final static private Source[] field_1021;

        public static Source[] values() {
            return (Source[])field_1021.clone();
        }

        public static Source valueOf(String string) {
            return Enum.valueOf(Source.class, string);
        }

        private Source(String name, ContextParameter<?> parameter) {
            this.name = name;
            this.parameter = parameter;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static Source[] method_36794() {
            return new Source[]{THIS, ATTACKING_ENTITY, LAST_DAMAGE_PLAYER, BLOCK_ENTITY};
        }

        static {
            field_1021 = Source.method_36794();
            CODEC = StringIdentifiable.createCodec(Source::values);
        }
    }
}

