/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class EnderChestInventory
extends SimpleInventory {
    @Nullable
    private EnderChestBlockEntity activeBlockEntity;

    public EnderChestInventory() {
        super(27);
    }

    public void setActiveBlockEntity(EnderChestBlockEntity blockEntity) {
        this.activeBlockEntity = blockEntity;
    }

    public boolean isActiveBlockEntity(EnderChestBlockEntity blockEntity) {
        return this.activeBlockEntity == blockEntity;
    }

    public void readData(ReadView.TypedListReadView<StackWithSlot> list) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, ItemStack.EMPTY);
        }
        for (StackWithSlot stackWithSlot : list) {
            if (!stackWithSlot.isValidSlot(this.size())) continue;
            this.setStack(stackWithSlot.slot(), stackWithSlot.stack());
        }
    }

    public void writeData(WriteView.ListAppender<StackWithSlot> list) {
        for (int i = 0; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (itemStack.isEmpty()) continue;
            list.add(new StackWithSlot(i, itemStack));
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.activeBlockEntity != null && !this.activeBlockEntity.canPlayerUse(player)) {
            return false;
        }
        return super.canPlayerUse(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onOpen(player);
        }
        super.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (this.activeBlockEntity != null) {
            this.activeBlockEntity.onClose(player);
        }
        super.onClose(player);
        this.activeBlockEntity = null;
    }
}

