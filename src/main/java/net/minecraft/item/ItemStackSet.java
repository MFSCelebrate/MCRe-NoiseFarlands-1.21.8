/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackSet {
    final static private Hash.Strategy<? super ItemStack> HASH_STRATEGY = new Hash.Strategy<ItemStack>(){

        public int hashCode(@Nullable ItemStack itemStack) {
            return ItemStack.hashCode(itemStack);
        }

        public boolean equals(@Nullable ItemStack itemStack, @Nullable ItemStack itemStack2) {
            return itemStack == itemStack2 || itemStack != null && itemStack2 != null && itemStack.isEmpty() == itemStack2.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2);
        }

        public boolean equals(@Nullable Object first, @Nullable Object second) {
            return this.equals((ItemStack)first, (ItemStack)second);
        }

        public int hashCode(@Nullable Object stack) {
            return this.hashCode((ItemStack)stack);
        }
    };

    public static Set<ItemStack> create() {
        return new ObjectLinkedOpenCustomHashSet(HASH_STRATEGY);
    }
}

