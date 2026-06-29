/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class LecternScreenHandler
extends ScreenHandler {
    final static private int field_30824 = 1;
    final static private int field_30825 = 1;
    final static public int PREVIOUS_PAGE_BUTTON_ID = 1;
    final static public int NEXT_PAGE_BUTTON_ID = 2;
    final static public int TAKE_BOOK_BUTTON_ID = 3;
    final static public int BASE_JUMP_TO_PAGE_BUTTON_ID = 100;
    final private Inventory inventory;
    final private PropertyDelegate propertyDelegate;

    public LecternScreenHandler(int syncId) {
        this(syncId, new SimpleInventory(1), new ArrayPropertyDelegate(1));
    }

    public LecternScreenHandler(int syncId, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerType.LECTERN, syncId);
        LecternScreenHandler.checkSize(inventory, 1);
        LecternScreenHandler.checkDataCount(propertyDelegate, 1);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addSlot(new Slot(inventory, 0, 0, 0){

            @Override
            public void markDirty() {
                super.markDirty();
                LecternScreenHandler.this.onContentChanged(this.inventory);
            }
        });
        this.addProperties(propertyDelegate);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 100) {
            int i = id - 100;
            this.setProperty(0, 1);
            return true;
        }
        switch (id) {
            case 2: {
                int i = this.propertyDelegate.get(0);
                this.setProperty(0, i + 1);
                return true;
            }
            case 1: {
                int i = this.propertyDelegate.get(0);
                this.setProperty(0, i - 1);
                return true;
            }
            case 3: {
                if (!player.canModifyBlocks()) {
                    return false;
                }
                ItemStack itemStack = this.inventory.removeStack(0);
                this.inventory.markDirty();
                if (!player.getInventory().insertStack(itemStack)) {
                    player.dropItem(itemStack, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        this.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack getBookItem() {
        return this.inventory.getStack(0);
    }

    public int getPage() {
        return this.propertyDelegate.get(0);
    }
}

