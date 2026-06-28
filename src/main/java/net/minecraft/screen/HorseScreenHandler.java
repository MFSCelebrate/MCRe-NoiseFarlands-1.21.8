/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class HorseScreenHandler
extends ScreenHandler {
    final static private Identifier EMPTY_SADDLE_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/saddle");
    final static private Identifier EMPTY_LLAMA_ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/llama_armor");
    final static private Identifier EMPTY_HORSE_ARMOR_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/horse_armor");
    final private Inventory inventory;
    final private AbstractHorseEntity entity;
    final static private int field_55978 = 0;
    final static private int field_48835 = 1;
    final static private int field_48836 = 2;

    public HorseScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, final AbstractHorseEntity entity, int slotColumnCount) {
        super(null, syncId);
        this.inventory = inventory;
        this.entity = entity;
        inventory.onOpen(playerInventory.player);
        Inventory inventory2 = entity.createEquipmentInventory(EquipmentSlot.SADDLE);
        this.addSlot(new ArmorSlot(this, inventory2, entity, EquipmentSlot.SADDLE, 0, 8, 18, EMPTY_SADDLE_SLOT_TEXTURE){

            @Override
            public boolean isEnabled() {
                return entity.canUseSlot(EquipmentSlot.SADDLE) && entity.getType().isIn(EntityTypeTags.CAN_EQUIP_SADDLE);
            }
        });
        final boolean bl = entity instanceof LlamaEntity;
        Identifier identifier = bl ? EMPTY_LLAMA_ARMOR_SLOT_TEXTURE : EMPTY_HORSE_ARMOR_SLOT_TEXTURE;
        Inventory inventory3 = entity.createEquipmentInventory(EquipmentSlot.BODY);
        this.addSlot(new ArmorSlot(this, inventory3, entity, EquipmentSlot.BODY, 0, 8, 36, identifier){

            @Override
            public boolean isEnabled() {
                return entity.canUseSlot(EquipmentSlot.BODY) && (entity.getType().isIn(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || bl);
            }
        });
        if (slotColumnCount > 0) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < slotColumnCount; ++j) {
                    this.addSlot(new Slot(inventory, j + i * slotColumnCount, 80 + j * 18, 18 + i * 18));
                }
            }
        }
        this.addPlayerSlots(playerInventory, 8, 84);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return !this.entity.areInventoriesDifferent(this.inventory) && this.inventory.canPlayerUse(player) && this.entity.isAlive() && player.canInteractWithEntity(this.entity, 4.0);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            int i = 2 + this.inventory.size();
            if (slot < i) {
                if (!this.insertItem(itemStack2, i, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
                if (!this.insertItem(itemStack2, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).canInsert(itemStack2) && !this.getSlot(0).hasStack()) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.inventory.size() == 0 || !this.insertItem(itemStack2, 2, i, false)) {
                int j;
                int k = j = i + 27;
                int l = k + 9;
                if (slot >= k && slot < l ? !this.insertItem(itemStack2, i, j, false) : (slot >= i && slot < j ? !this.insertItem(itemStack2, k, l, false) : !this.insertItem(itemStack2, k, j, false))) {
                    return ItemStack.EMPTY;
                }
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}

