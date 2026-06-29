/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.recipe;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public interface RecipeUnlocker {
    public void setLastRecipe(@Nullable RecipeEntry<?> var1);

    @Nullable
    public RecipeEntry<?> getLastRecipe();

    default public void unlockLastRecipe(PlayerEntity player, List<ItemStack> ingredients) {
        RecipeEntry<?> recipeEntry = this.getLastRecipe();
        if (recipeEntry != null) {
            player.onRecipeCrafted(recipeEntry, ingredients);
            if (!recipeEntry.value().isIgnoredInRecipeBook()) {
                player.unlockRecipes(Collections.singleton(recipeEntry));
                this.setLastRecipe(null);
            }
        }
    }

    default public boolean shouldCraftRecipe(ServerPlayerEntity player, RecipeEntry<?> recipe) {
        if (recipe.value().isIgnoredInRecipeBook() || !player.net_minecraft_server_world_ServerWorld_getWorld().getGameRules().getBoolean(GameRules.DO_LIMITED_CRAFTING) || player.getRecipeBook().isUnlocked(recipe.id())) {
            this.setLastRecipe(recipe);
            return true;
        }
        return false;
    }
}

