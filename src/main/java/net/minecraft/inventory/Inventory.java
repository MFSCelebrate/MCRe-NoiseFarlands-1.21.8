/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Inventory
extends Clearable,
Iterable<ItemStack> {
    final static public float DEFAULT_MAX_INTERACTION_RANGE = 4.0f;

    public int size();

    public boolean isEmpty();

    public ItemStack getStack(int var1);

    public ItemStack removeStack(int var1, int var2);

    public ItemStack removeStack(int var1);

    public void setStack(int var1, ItemStack var2);

    default public int getMaxCountPerStack() {
        return 99;
    }

    default public int getMaxCount(ItemStack stack) {
        return Math.min(this.getMaxCountPerStack(), stack.getMaxCount());
    }

    public void markDirty();

    public boolean canPlayerUse(PlayerEntity var1);

    default public void onOpen(PlayerEntity player) {
    }

    default public void onClose(PlayerEntity player) {
    }

    default public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    default public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return true;
    }

    default public int count(Item item) {
        int i = 0;
        for (ItemStack itemStack : this) {
            if (!itemStack.getItem().equals(item)) continue;
            i += itemStack.getCount();
        }
        return i;
    }

    default public boolean containsAny(Set<Item> items) {
        return this.containsAny((ItemStack stack) -> !stack.isEmpty() && items.contains(stack.getItem()));
    }

    default public boolean containsAny(Predicate<ItemStack> predicate) {
        for (ItemStack itemStack : this) {
            if (!predicate.test(itemStack)) continue;
            return true;
        }
        return false;
    }

    public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player) {
        return Inventory.canPlayerUse(blockEntity, player, 4.0f);
    }

    public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player, float range) {
        World world = blockEntity.getWorld();
        BlockPos blockPos = blockEntity.getPos();
        if (world == null) {
            return false;
        }
        if (world.getBlockEntity(blockPos) != blockEntity) {
            return false;
        }
        return player.canInteractWithBlockAt(blockPos, range);
    }

    @Override
    default public java.util.Iterator<ItemStack> iterator() {
        return new Iterator(this);
    }

    public static class Iterator
    implements java.util.Iterator<ItemStack> {
        final private Inventory inventory;
        private int index;
        final private int size;

        public Iterator(Inventory inventory) {
            this.inventory = inventory;
            this.size = inventory.size();
        }

        @Override
        public boolean hasNext() {
            return this.index < this.size;
        }

        @Override
        public ItemStack net_minecraft_item_ItemStack_next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.inventory.getStack(this.index++);
        }

        @Override
        public Object java_lang_Object_next() {
            return this.net_minecraft_item_ItemStack_next();
        }
    }
}

