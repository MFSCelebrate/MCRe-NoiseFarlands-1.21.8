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
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SingleStackRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public class StonecuttingRecipeJsonBuilder
implements CraftingRecipeJsonBuilder {
    final private RecipeCategory category;
    final private Item output;
    final private Ingredient input;
    final private int count;
    final private Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap();
    @Nullable
    private String group;
    final private SingleStackRecipe.RecipeFactory<?> recipeFactory;

    public StonecuttingRecipeJsonBuilder(RecipeCategory category, SingleStackRecipe.RecipeFactory<?> recipeFactory, Ingredient input, ItemConvertible output, int count) {
        this.category = category;
        this.recipeFactory = recipeFactory;
        this.output = output.asItem();
        this.input = input;
        this.count = count;
    }

    public static StonecuttingRecipeJsonBuilder createStonecutting(Ingredient input, RecipeCategory category, ItemConvertible output) {
        return new StonecuttingRecipeJsonBuilder(category, StonecuttingRecipe::new, input, output, 1);
    }

    public static StonecuttingRecipeJsonBuilder createStonecutting(Ingredient input, RecipeCategory category, ItemConvertible output, int count) {
        return new StonecuttingRecipeJsonBuilder(category, StonecuttingRecipe::new, input, output, count);
    }

    @Override
    public StonecuttingRecipeJsonBuilder net_minecraft_data_recipe_StonecuttingRecipeJsonBuilder_criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.criteria.put(string, advancementCriterion);
        return this;
    }

    @Override
    public StonecuttingRecipeJsonBuilder net_minecraft_data_recipe_StonecuttingRecipeJsonBuilder_group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output;
    }

    @Override
    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
        this.validate(recipeKey);
        Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey)).rewards(AdvancementRewards.Builder.recipe(recipeKey)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(builder::criterion);
        Object singleStackRecipe = this.recipeFactory.create(Objects.requireNonNullElse(this.group, ""), this.input, new ItemStack(this.output, this.count));
        exporter.accept(recipeKey, (Recipe<?>)singleStackRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    private void validate(RegistryKey<Recipe<?>> recipeKey) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(recipeKey.getValue()));
        }
    }

    @Override
    public CraftingRecipeJsonBuilder net_minecraft_data_recipe_CraftingRecipeJsonBuilder_group(@Nullable String group) {
        return this.net_minecraft_data_recipe_StonecuttingRecipeJsonBuilder_group(group);
    }

    public CraftingRecipeJsonBuilder net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(String name, AdvancementCriterion criterion) {
        return this.net_minecraft_data_recipe_CraftingRecipeJsonBuilder_criterion(name, criterion);
    }
}

