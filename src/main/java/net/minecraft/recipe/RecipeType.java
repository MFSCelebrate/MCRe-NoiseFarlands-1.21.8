/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe;

import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public interface RecipeType<T extends Recipe<?>> {
    final static public RecipeType<CraftingRecipe> CRAFTING = RecipeType.register("crafting");
    final static public RecipeType<SmeltingRecipe> SMELTING = RecipeType.register("smelting");
    final static public RecipeType<BlastingRecipe> BLASTING = RecipeType.register("blasting");
    final static public RecipeType<SmokingRecipe> SMOKING = RecipeType.register("smoking");
    final static public RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = RecipeType.register("campfire_cooking");
    final static public RecipeType<StonecuttingRecipe> STONECUTTING = RecipeType.register("stonecutting");
    final static public RecipeType<SmithingRecipe> SMITHING = RecipeType.register("smithing");

    public static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(Registries.RECIPE_TYPE, Identifier.ofVanilla(id), new RecipeType<T>(){

            public String toString() {
                return id;
            }
        });
    }
}

