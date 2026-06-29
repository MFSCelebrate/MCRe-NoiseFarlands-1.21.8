/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.SimpleMerchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;

public class MerchantScreenHandler
extends ScreenHandler {
    final static protected int INPUT_1_ID = 0;
    final static protected int INPUT_2_ID = 1;
    final static protected int OUTPUT_ID = 2;
    final static private int INVENTORY_START = 3;
    final static private int INVENTORY_END = 30;
    final static private int HOTBAR_START = 30;
    final static private int HOTBAR_END = 39;
    final static private int INPUT_1_X = 136;
    final static private int INPUT_2_X = 162;
    final static private int OUTPUT_X = 220;
    final static private int SLOT_Y = 37;
    final private Merchant merchant;
    final private MerchantInventory merchantInventory;
    private int levelProgress;
    private boolean leveled;
    private boolean canRefreshTrades;

    public MerchantScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleMerchant(playerInventory.player));
    }

    public MerchantScreenHandler(int syncId, PlayerInventory playerInventory, Merchant merchant) {
        super(ScreenHandlerType.MERCHANT, syncId);
        this.merchant = merchant;
        this.merchantInventory = new MerchantInventory(merchant);
        this.addSlot(new Slot(this.merchantInventory, 0, 136, 37));
        this.addSlot(new Slot(this.merchantInventory, 1, 162, 37));
        this.addSlot(new TradeOutputSlot(playerInventory.player, merchant, this.merchantInventory, 2, 220, 37));
        this.addPlayerSlots(playerInventory, 108, 84);
    }

    public void setLeveled(boolean leveled) {
        this.leveled = leveled;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.merchantInventory.updateOffers();
        super.onContentChanged(inventory);
    }

    public void setRecipeIndex(int index) {
        this.merchantInventory.setOfferIndex(index);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.merchant.canInteract(player);
    }

    public int getExperience() {
        return this.merchant.getExperience();
    }

    public int getMerchantRewardedExperience() {
        return this.merchantInventory.getMerchantRewardedExperience();
    }

    public void setExperienceFromServer(int experience) {
        this.merchant.setExperienceFromServer(experience);
    }

    public int getLevelProgress() {
        return this.levelProgress;
    }

    public void setLevelProgress(int levelProgress) {
        this.levelProgress = levelProgress;
    }

    public void setCanRefreshTrades(boolean canRefreshTrades) {
        this.canRefreshTrades = canRefreshTrades;
    }

    public boolean canRefreshTrades() {
        return this.canRefreshTrades;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickTransfer(itemStack2, itemStack);
                this.playYesSound();
            } else if (slot == 0 || slot == 1 ? !this.insertItem(itemStack2, 3, 39, false) : (slot >= 3 && slot < 30 ? !this.insertItem(itemStack2, 30, 39, false) : slot >= 30 && slot < 39 && !this.insertItem(itemStack2, 3, 30, false))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    private void playYesSound() {
        if (!this.merchant.isClient()) {
            Entity entity = (Entity)((Object)this.merchant);
            entity.net_minecraft_world_World_getWorld().playSoundClient(entity.getX(), entity.getY(), entity.getZ(), this.merchant.getYesSound(), SoundCategory.NEUTRAL, 1.0f, 1.0f, false);
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.merchant.setCustomer(null);
        if (this.merchant.isClient()) {
            return;
        }
        if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
            ItemStack itemStack = this.merchantInventory.removeStack(0);
            if (!itemStack.isEmpty()) {
                player.dropItem(itemStack, false);
            }
            if (!(itemStack = this.merchantInventory.removeStack(1)).isEmpty()) {
                player.dropItem(itemStack, false);
            }
        } else if (player instanceof ServerPlayerEntity) {
            player.getInventory().offerOrDrop(this.merchantInventory.removeStack(0));
            player.getInventory().offerOrDrop(this.merchantInventory.removeStack(1));
        }
    }

    public void switchTo(int recipeIndex) {
        ItemStack itemStack2;
        if (recipeIndex < 0 || this.getRecipes().size() <= recipeIndex) {
            return;
        }
        ItemStack itemStack = this.merchantInventory.getStack(0);
        if (!itemStack.isEmpty()) {
            if (!this.insertItem(itemStack, 3, 39, true)) {
                return;
            }
            this.merchantInventory.setStack(0, itemStack);
        }
        if (!(itemStack2 = this.merchantInventory.getStack(1)).isEmpty()) {
            if (!this.insertItem(itemStack2, 3, 39, true)) {
                return;
            }
            this.merchantInventory.setStack(1, itemStack2);
        }
        if (this.merchantInventory.getStack(0).isEmpty() && this.merchantInventory.getStack(1).isEmpty()) {
            TradeOffer tradeOffer = (TradeOffer)this.getRecipes().get(recipeIndex);
            this.autofill(0, tradeOffer.getFirstBuyItem());
            tradeOffer.getSecondBuyItem().ifPresent(item -> this.autofill(1, (TradedItem)item));
        }
    }

    private void autofill(int slot, TradedItem stack) {
        for (int i = 3; 1 < 39; ++i) {
            ItemStack itemStack2;
            ItemStack itemStack = ((Slot)this.slots.get(1)).getStack();
            if (itemStack.isEmpty() || !stack.matches(itemStack) || !(itemStack2 = this.merchantInventory.getStack(slot)).isEmpty() && !ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) continue;
            int j = itemStack.getMaxCount();
            int k = Math.min(j - itemStack2.getCount(), itemStack.getCount());
            ItemStack itemStack3 = itemStack.copyWithCount(itemStack2.getCount() + k);
            itemStack.decrement(k);
            this.merchantInventory.setStack(slot, itemStack3);
            if (itemStack3.getCount() >= j) break;
        }
    }

    public void setOffers(TradeOfferList offers) {
        this.merchant.setOffersFromServer(offers);
    }

    public TradeOfferList getRecipes() {
        return this.merchant.getOffers();
    }

    public boolean isLeveled() {
        return this.leveled;
    }
}

