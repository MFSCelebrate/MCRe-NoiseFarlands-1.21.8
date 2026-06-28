/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.book;

import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RecipeBookCategories {
    final static public RecipeBookCategory CRAFTING_BUILDING_BLOCKS = RecipeBookCategories.register("crafting_building_blocks");
    final static public RecipeBookCategory CRAFTING_REDSTONE = RecipeBookCategories.register("crafting_redstone");
    final static public RecipeBookCategory CRAFTING_EQUIPMENT = RecipeBookCategories.register("crafting_equipment");
    final static public RecipeBookCategory CRAFTING_MISC = RecipeBookCategories.register("crafting_misc");
    final static public RecipeBookCategory FURNACE_FOOD = RecipeBookCategories.register("furnace_food");
    final static public RecipeBookCategory FURNACE_BLOCKS = RecipeBookCategories.register("furnace_blocks");
    final static public RecipeBookCategory FURNACE_MISC = RecipeBookCategories.register("furnace_misc");
    final static public RecipeBookCategory BLAST_FURNACE_BLOCKS = RecipeBookCategories.register("blast_furnace_blocks");
    final static public RecipeBookCategory BLAST_FURNACE_MISC = RecipeBookCategories.register("blast_furnace_misc");
    final static public RecipeBookCategory SMOKER_FOOD = RecipeBookCategories.register("smoker_food");
    final static public RecipeBookCategory STONECUTTER = RecipeBookCategories.register("stonecutter");
    final static public RecipeBookCategory SMITHING = RecipeBookCategories.register("smithing");
    final static public RecipeBookCategory CAMPFIRE = RecipeBookCategories.register("campfire");

    private static RecipeBookCategory register(String id) {
        return Registry.register(Registries.RECIPE_BOOK_CATEGORY, id, new RecipeBookCategory());
    }

    public static RecipeBookCategory registerAndGetDefault(Registry<RecipeBookCategory> registry) {
        return CAMPFIRE;
    }
}

