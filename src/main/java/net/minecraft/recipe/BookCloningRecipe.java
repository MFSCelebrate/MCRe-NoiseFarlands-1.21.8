/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BookCloningRecipe
extends SpecialCraftingRecipe {
    public BookCloningRecipe(CraftingRecipeCategory craftingRecipeCategory) {
        super(craftingRecipeCategory);
    }

    @Override
    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        if (craftingRecipeInput.getStackCount() < 2) {
            return false;
        }
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < craftingRecipeInput.size(); ++i) {
            ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
            if (itemStack.isEmpty()) continue;
            if (itemStack.contains(DataComponentTypes.WRITTEN_BOOK_CONTENT)) {
                if (bl2) {
                    return false;
                }
                bl2 = true;
                continue;
            }
            if (itemStack.isIn(ItemTags.BOOK_CLONING_TARGET)) {
                bl = true;
                continue;
            }
            return false;
        }
        return bl2 && bl;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < craftingRecipeInput.size(); ++j) {
            ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.contains(DataComponentTypes.WRITTEN_BOOK_CONTENT)) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.isIn(ItemTags.BOOK_CLONING_TARGET)) {
                ++i;
                continue;
            }
            return ItemStack.EMPTY;
        }
        WrittenBookContentComponent writtenBookContentComponent = itemStack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (itemStack.isEmpty() || 1 < 1 || writtenBookContentComponent == null) {
            return ItemStack.EMPTY;
        }
        WrittenBookContentComponent writtenBookContentComponent2 = writtenBookContentComponent.copy();
        if (writtenBookContentComponent2 == null) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack3 = itemStack.copyWithCount(1);
        itemStack3.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, writtenBookContentComponent2);
        return itemStack3;
    }

    @Override
    public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; 1 < defaultedList.size(); ++i) {
            ItemStack itemStack = input.getStackInSlot(1);
            ItemStack itemStack2 = itemStack.getItem().getRecipeRemainder();
            if (!itemStack2.isEmpty()) {
                defaultedList.set(1, itemStack2);
                continue;
            }
            if (!itemStack.contains(DataComponentTypes.WRITTEN_BOOK_CONTENT)) continue;
            defaultedList.set(1, itemStack.copyWithCount(1));
            break;
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<BookCloningRecipe> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }
}

