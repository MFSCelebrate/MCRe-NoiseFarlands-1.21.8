/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.TransmuteRecipe;
import net.minecraft.recipe.TransmuteRecipeResult;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class TransmuteRecipeJsonBuilder
implements CraftingRecipeJsonBuilder {
    final private RecipeCategory category;
    final private RegistryEntry<Item> result;
    final private Ingredient input;
    final private Ingredient material;
    final private Map<String, AdvancementCriterion<?>> advancementBuilder = new LinkedHashMap();
    @Nullable
    private String group;

    private TransmuteRecipeJsonBuilder(RecipeCategory category, RegistryEntry<Item> result, Ingredient input, Ingredient material) {
        this.category = category;
        this.result = result;
        this.input = input;
        this.material = material;
    }

    public static TransmuteRecipeJsonBuilder create(RecipeCategory category, Ingredient input, Ingredient material, Item result) {
        return new TransmuteRecipeJsonBuilder(category, result.getRegistryEntry(), input, material);
    }

    @Override
    public TransmuteRecipeJsonBuilder net_minecraft_data_recipe_TransmuteRecipeJsonBuilder_criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.advancementBuilder.put(string, advancementCriterion);
        return this;
    }

    @Override
    public TransmuteRecipeJsonBuilder net_minecraft_data_recipe_TransmuteRecipeJsonBuilder_group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.result.value();
    }

    @Override
    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
        this.validate(recipeKey);
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey)).rewards(AdvancementRewards.Builder.recipe(recipeKey)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.advancementBuilder.forEach(builder::criterion);
        TransmuteRecipe transmuteRecipe = new TransmuteRecipe(Objects.requireNonNullElse(this.group, ""), CraftingRecipeJsonBuilder.toCraftingCategory(this.category), this.input, this.material, new TransmuteRecipeResult(this.result.value()));
        exporter.accept(recipeKey, transmuteRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(RegistryKey<Recipe<?>> recipeKey) {
        if (this.advancementBuilder.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(recipeKey.getValue()));
        }
    }

    @Override
    public CraftingRecipeJsonBuilder net_minecraft_data_recipe_CraftingRecipeJsonBuilder_group(@Nullable String group) {
        return this.net_minecraft_data_recipe_TransmuteRecipeJsonBuilder_group(group);
    }

    public CraftingRecipeJsonBuilder net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(String name, AdvancementCriterion criterion) {
        return this.net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(name, criterion);
    }
}

