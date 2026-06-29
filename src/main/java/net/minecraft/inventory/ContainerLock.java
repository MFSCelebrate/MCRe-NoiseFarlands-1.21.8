/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.inventory;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public record ContainerLock(ItemPredicate predicate) {
    final static public ContainerLock EMPTY = new ContainerLock(ItemPredicate.Builder.create().build());
    final static public Codec<ContainerLock> CODEC = ItemPredicate.CODEC.xmap(ContainerLock::new, ContainerLock::predicate);
    final static public String LOCK_KEY = "lock";

    public boolean canOpen(ItemStack stack) {
        return this.predicate.test(stack);
    }

    public void write(WriteView view) {
        if (this != EMPTY) {
            view.put(LOCK_KEY, CODEC, this);
        }
    }

    public static ContainerLock read(ReadView view) {
        return view.read(LOCK_KEY, CODEC).orElse(EMPTY);
    }
}

