/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.display.RecipeDisplay;

@Environment(value=EnvType.CLIENT)
public class RecipeResultCollection {
    final static public RecipeResultCollection EMPTY = new RecipeResultCollection(List.of());
    final private List<RecipeDisplayEntry> entries;
    final private Set<NetworkRecipeId> craftableRecipes = new HashSet<NetworkRecipeId>();
    final private Set<NetworkRecipeId> displayableRecipes = new HashSet<NetworkRecipeId>();

    public RecipeResultCollection(List<RecipeDisplayEntry> entries) {
        this.entries = entries;
    }

    public void populateRecipes(RecipeFinder finder, Predicate<RecipeDisplay> displayablePredicate) {
        for (RecipeDisplayEntry recipeDisplayEntry : this.entries) {
            boolean bl = displayablePredicate.test(recipeDisplayEntry.display());
            if (bl) {
                this.displayableRecipes.add(recipeDisplayEntry.id());
            } else {
                this.displayableRecipes.remove(recipeDisplayEntry.id());
            }
            if (bl && recipeDisplayEntry.isCraftable(finder)) {
                this.craftableRecipes.add(recipeDisplayEntry.id());
                continue;
            }
            this.craftableRecipes.remove(recipeDisplayEntry.id());
        }
    }

    public boolean isCraftable(NetworkRecipeId recipeId) {
        return this.craftableRecipes.contains(recipeId);
    }

    public boolean hasCraftableRecipes() {
        return !this.craftableRecipes.isEmpty();
    }

    public boolean hasDisplayableRecipes() {
        return !this.displayableRecipes.isEmpty();
    }

    public List<RecipeDisplayEntry> getAllRecipes() {
        return this.entries;
    }

    public List<RecipeDisplayEntry> filter(RecipeFilterMode filterMode) {
        Predicate<NetworkRecipeId> predicate = switch (filterMode.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.displayableRecipes::contains;
            case 1 -> this.craftableRecipes::contains;
            case 2 -> recipeId -> this.displayableRecipes.contains(recipeId) && !this.craftableRecipes.contains(recipeId);
        };
        ArrayList<RecipeDisplayEntry> list = new ArrayList<RecipeDisplayEntry>();
        for (RecipeDisplayEntry recipeDisplayEntry : this.entries) {
            if (!predicate.test(recipeDisplayEntry.id())) continue;
            list.add(recipeDisplayEntry);
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class RecipeFilterMode
    extends Enum<RecipeFilterMode> {
        final static public RecipeFilterMode ANY = new RecipeFilterMode();
        final static public RecipeFilterMode CRAFTABLE = new RecipeFilterMode();
        final static public RecipeFilterMode NOT_CRAFTABLE = new RecipeFilterMode();
        final static private RecipeFilterMode[] field_52850;

        public static RecipeFilterMode[] values() {
            return (RecipeFilterMode[])field_52850.clone();
        }

        public static RecipeFilterMode valueOf(String string) {
            return Enum.valueOf(RecipeFilterMode.class, string);
        }

        private static RecipeFilterMode[] method_62052() {
            return new RecipeFilterMode[]{ANY, CRAFTABLE, NOT_CRAFTABLE};
        }

        static {
            field_52850 = RecipeFilterMode.method_62052();
        }
    }
}

