/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.book;

public final class RecipeBookType
extends Enum<RecipeBookType> {
    final static public RecipeBookType CRAFTING = new RecipeBookType();
    final static public RecipeBookType FURNACE = new RecipeBookType();
    final static public RecipeBookType BLAST_FURNACE = new RecipeBookType();
    final static public RecipeBookType SMOKER = new RecipeBookType();
    final static private RecipeBookType[] field_25767;

    public static RecipeBookType[] values() {
        return (RecipeBookType[])field_25767.clone();
    }

    public static RecipeBookType valueOf(String string) {
        return Enum.valueOf(RecipeBookType.class, string);
    }

    private static RecipeBookType[] method_36674() {
        return new RecipeBookType[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
    }

    static {
        field_25767 = RecipeBookType.method_36674();
    }
}

