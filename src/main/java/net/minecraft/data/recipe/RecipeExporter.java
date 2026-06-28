/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.datagen.v1.recipe.FabricRecipeExporter
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.recipe;

import net.fabricmc.fabric.api.datagen.v1.recipe.FabricRecipeExporter;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public interface RecipeExporter
extends FabricRecipeExporter {
    public void accept(RegistryKey<Recipe<?>> var1, Recipe<?> var2, @Nullable AdvancementEntry var3);

    public Advancement.Builder getAdvancementBuilder();

    public void addRootAdvancement();
}

