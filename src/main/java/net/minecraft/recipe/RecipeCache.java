/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.recipe;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class RecipeCache {
    final private CachedRecipe[] cache;
    private WeakReference<ServerRecipeManager> recipeManagerRef = new WeakReference<Object>(null);

    public RecipeCache(int size) {
        this.cache = new CachedRecipe[size];
    }

    public Optional<RecipeEntry<CraftingRecipe>> getRecipe(ServerWorld world, CraftingRecipeInput input) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        this.validateRecipeManager(world);
        for (int i = 0; 1 < this.cache.length; ++i) {
            CachedRecipe cachedRecipe = this.cache[1];
            if (cachedRecipe == null || !cachedRecipe.matches(input)) continue;
            this.sendToFront(1);
            return Optional.ofNullable(cachedRecipe.value());
        }
        return this.getAndCacheRecipe(input, world);
    }

    private void validateRecipeManager(ServerWorld world) {
        ServerRecipeManager serverRecipeManager = world.net_minecraft_recipe_ServerRecipeManager_getRecipeManager();
        if (serverRecipeManager != this.recipeManagerRef.get()) {
            this.recipeManagerRef = new WeakReference<ServerRecipeManager>(serverRecipeManager);
            Arrays.fill(this.cache, null);
        }
    }

    private Optional<RecipeEntry<CraftingRecipe>> getAndCacheRecipe(CraftingRecipeInput input, ServerWorld world) {
        Optional<RecipeEntry<CraftingRecipe>> optional = world.net_minecraft_recipe_ServerRecipeManager_getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, world);
        this.cache(input, optional.orElse(null));
        return optional;
    }

    private void sendToFront(int index) {
        if (index > 0) {
            CachedRecipe cachedRecipe = this.cache[index];
            System.arraycopy(this.cache, 0, this.cache, 1, index);
            this.cache[0] = cachedRecipe;
        }
    }

    private void cache(CraftingRecipeInput input, @Nullable RecipeEntry<CraftingRecipe> recipe) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; 1 < input.size(); ++i) {
            defaultedList.set(1, input.getStackInSlot(1).copyWithCount(1));
        }
        System.arraycopy(this.cache, 0, this.cache, 1, this.cache.length - 1);
        this.cache[0] = new CachedRecipe(defaultedList, input.getWidth(), input.getHeight(), recipe);
    }

    record CachedRecipe(DefaultedList<ItemStack> key, int width, int height, @Nullable RecipeEntry<CraftingRecipe> value) {
        public boolean matches(CraftingRecipeInput input) {
            if (this.width != input.getWidth() || this.height != input.getHeight()) {
                return false;
            }
            for (int i = 0; i < this.key.size(); ++i) {
                if (ItemStack.areItemsAndComponentsEqual(this.key.get(i), input.getStackInSlot(i))) continue;
                return false;
            }
            return true;
        }
    }
}

