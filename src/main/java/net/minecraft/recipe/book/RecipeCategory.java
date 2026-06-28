/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.book;

public final class RecipeCategory
extends Enum<RecipeCategory> {
    final static public RecipeCategory BUILDING_BLOCKS = new RecipeCategory("building_blocks");
    final static public RecipeCategory DECORATIONS = new RecipeCategory("decorations");
    final static public RecipeCategory REDSTONE = new RecipeCategory("redstone");
    final static public RecipeCategory TRANSPORTATION = new RecipeCategory("transportation");
    final static public RecipeCategory TOOLS = new RecipeCategory("tools");
    final static public RecipeCategory COMBAT = new RecipeCategory("combat");
    final static public RecipeCategory FOOD = new RecipeCategory("food");
    final static public RecipeCategory BREWING = new RecipeCategory("brewing");
    final static public RecipeCategory MISC = new RecipeCategory("misc");
    final private String name;
    final static private RecipeCategory[] field_40644;

    public static RecipeCategory[] values() {
        return (RecipeCategory[])field_40644.clone();
    }

    public static RecipeCategory valueOf(String string) {
        return Enum.valueOf(RecipeCategory.class, string);
    }

    private RecipeCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static RecipeCategory[] method_46204() {
        return new RecipeCategory[]{BUILDING_BLOCKS, DECORATIONS, REDSTONE, TRANSPORTATION, TOOLS, COMBAT, FOOD, BREWING, MISC};
    }

    static {
        field_40644 = RecipeCategory.method_46204();
    }
}

