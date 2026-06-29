/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(value=EnvType.CLIENT)
public class ClientRecipeBook
extends RecipeBook {
    final private Map<NetworkRecipeId, RecipeDisplayEntry> recipes = new HashMap<NetworkRecipeId, RecipeDisplayEntry>();
    final private Set<NetworkRecipeId> highlightedRecipes = new HashSet<NetworkRecipeId>();
    private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByCategory = Map.of();
    private List<RecipeResultCollection> orderedResults = List.of();

    public void add(RecipeDisplayEntry entry) {
        this.recipes.put(entry.id(), entry);
    }

    public void remove(NetworkRecipeId recipeId) {
        this.recipes.remove(recipeId);
        this.highlightedRecipes.remove(recipeId);
    }

    public void clear() {
        this.recipes.clear();
        this.highlightedRecipes.clear();
    }

    public boolean isHighlighted(NetworkRecipeId recipeId) {
        return this.highlightedRecipes.contains(recipeId);
    }

    public void unmarkHighlighted(NetworkRecipeId recipeId) {
        this.highlightedRecipes.remove(recipeId);
    }

    public void markHighlighted(NetworkRecipeId recipeId) {
        this.highlightedRecipes.add(recipeId);
    }

    public void refresh() {
        Map<RecipeBookCategory, List<List<RecipeDisplayEntry>>> map = ClientRecipeBook.toGroupedMap(this.recipes.values());
        HashMap<RecipeBookType, List> map2 = new HashMap<RecipeBookType, List>();
        ImmutableList.Builder builder = ImmutableList.builder();
        map.forEach((group, resultCollections) -> map2.put((RecipeBookType)group, (List)resultCollections.stream().map(RecipeResultCollection::new).peek(arg_0 -> ((ImmutableList.Builder)builder).add(arg_0)).collect(ImmutableList.toImmutableList())));
        for (RecipeBookType recipeBookType : RecipeBookType.values()) {
            map2.put(recipeBookType, (List)recipeBookType.getCategories().stream().flatMap(group -> map2.getOrDefault(group, List.of()).stream()).collect(ImmutableList.toImmutableList()));
        }
        this.resultsByCategory = Map.copyOf(map2);
        this.orderedResults = builder.build();
    }

    private static Map<RecipeBookCategory, List<List<RecipeDisplayEntry>>> toGroupedMap(Iterable<RecipeDisplayEntry> recipes) {
        HashMap<RecipeBookCategory, List<List<RecipeDisplayEntry>>> map = new HashMap<RecipeBookCategory, List<List<RecipeDisplayEntry>>>();
        HashBasedTable table = HashBasedTable.create();
        for (RecipeDisplayEntry recipeDisplayEntry : recipes) {
            RecipeBookCategory recipeBookCategory = recipeDisplayEntry.category();
            OptionalInt optionalInt = recipeDisplayEntry.group();
            if (optionalInt.isEmpty()) {
                map.computeIfAbsent(recipeBookCategory, group -> new ArrayList()).add(List.of(recipeDisplayEntry));
                continue;
            }
            ArrayList<RecipeDisplayEntry> list = (ArrayList<RecipeDisplayEntry>)table.get((Object)recipeBookCategory, (Object)optionalInt.getAsInt());
            if (list == null) {
                list = new ArrayList<RecipeDisplayEntry>();
                table.put((Object)recipeBookCategory, (Object)optionalInt.getAsInt(), list);
                map.computeIfAbsent(recipeBookCategory, group -> new ArrayList()).add(list);
            }
            list.add(recipeDisplayEntry);
        }
        return map;
    }

    public List<RecipeResultCollection> getOrderedResults() {
        return this.orderedResults;
    }

    public List<RecipeResultCollection> getResultsForCategory(RecipeBookGroup category) {
        return this.resultsByCategory.getOrDefault(category, Collections.emptyList());
    }
}

