/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.predicate.component.ComponentSubPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;

public abstract class EnchantmentsPredicate
implements ComponentSubPredicate<ItemEnchantmentsComponent> {
    final private List<EnchantmentPredicate> enchantments;

    protected EnchantmentsPredicate(List<EnchantmentPredicate> enchantments) {
        this.enchantments = enchantments;
    }

    public static <T extends EnchantmentsPredicate> Codec<T> createCodec(Function<List<EnchantmentPredicate>, T> predicateFunction) {
        return EnchantmentPredicate.CODEC.listOf().xmap(predicateFunction, EnchantmentsPredicate::getEnchantments);
    }

    protected List<EnchantmentPredicate> getEnchantments() {
        return this.enchantments;
    }

    @Override
    public boolean test(ItemEnchantmentsComponent itemEnchantmentsComponent) {
        for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
            if (enchantmentPredicate.test(itemEnchantmentsComponent)) continue;
            return false;
        }
        return true;
    }

    public static Enchantments enchantments(List<EnchantmentPredicate> enchantments) {
        return new Enchantments(enchantments);
    }

    public static StoredEnchantments storedEnchantments(List<EnchantmentPredicate> storedEnchantments) {
        return new StoredEnchantments(storedEnchantments);
    }

    public static class Enchantments
    extends EnchantmentsPredicate {
        final static public Codec<Enchantments> CODEC = Enchantments.createCodec(Enchantments::new);

        protected Enchantments(List<EnchantmentPredicate> list) {
            super(list);
        }

        @Override
        public ComponentType<ItemEnchantmentsComponent> getComponentType() {
            return DataComponentTypes.ENCHANTMENTS;
        }
    }

    public static class StoredEnchantments
    extends EnchantmentsPredicate {
        final static public Codec<StoredEnchantments> CODEC = StoredEnchantments.createCodec(StoredEnchantments::new);

        protected StoredEnchantments(List<EnchantmentPredicate> list) {
            super(list);
        }

        @Override
        public ComponentType<ItemEnchantmentsComponent> getComponentType() {
            return DataComponentTypes.STORED_ENCHANTMENTS;
        }
    }
}

