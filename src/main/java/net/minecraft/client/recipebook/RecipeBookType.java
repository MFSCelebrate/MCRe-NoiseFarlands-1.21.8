/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(value=EnvType.CLIENT)
public final class RecipeBookType
extends Enum<RecipeBookType>
implements RecipeBookGroup {
    final static public RecipeBookType CRAFTING = new RecipeBookType(RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE);
    final static public RecipeBookType FURNACE = new RecipeBookType(RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC);
    final static public RecipeBookType BLAST_FURNACE = new RecipeBookType(RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC);
    final static public RecipeBookType SMOKER = new RecipeBookType(RecipeBookCategories.SMOKER_FOOD);
    final private List<RecipeBookCategory> categories;
    final static private RecipeBookType[] field_54842;

    public static RecipeBookType[] values() {
        return (RecipeBookType[])field_54842.clone();
    }

    public static RecipeBookType valueOf(String string) {
        return Enum.valueOf(RecipeBookType.class, string);
    }

    private RecipeBookType(RecipeBookCategory ... categories) {
        this.categories = List.of(categories);
    }

    public List<RecipeBookCategory> getCategories() {
        return this.categories;
    }

    private static RecipeBookType[] method_64889() {
        return new RecipeBookType[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
    }

    static {
        field_54842 = RecipeBookType.method_64889();
    }
}

