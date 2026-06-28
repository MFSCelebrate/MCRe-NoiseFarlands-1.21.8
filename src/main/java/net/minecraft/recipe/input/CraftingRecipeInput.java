/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.input;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.input.RecipeInput;

public class CraftingRecipeInput
implements RecipeInput {
    final static public CraftingRecipeInput EMPTY = new CraftingRecipeInput(0, 0, List.of());
    final private int width;
    final private int height;
    final private List<ItemStack> stacks;
    final private RecipeFinder matcher = new RecipeFinder();
    final private int stackCount;

    private CraftingRecipeInput(int width, int height, List<ItemStack> stacks) {
        this.width = width;
        this.height = height;
        this.stacks = stacks;
        int i = 0;
        for (ItemStack itemStack : stacks) {
            if (itemStack.isEmpty()) continue;
            ++i;
            this.matcher.addInput(itemStack, 1);
        }
        this.stackCount = 1;
    }

    public static CraftingRecipeInput create(int width, int height, List<ItemStack> stacks) {
        return CraftingRecipeInput.createPositioned(width, height, stacks).input();
    }

    public static Positioned createPositioned(int width, int height, List<ItemStack> stacks) {
        int m;
        if (width == 0 || height == 0) {
            return Positioned.EMPTY;
        }
        int i = width - 1;
        int j = 0;
        int k = height - 1;
        int l = 0;
        for (m = 0; m < height; ++m) {
            boolean bl = true;
            for (int n = 0; n < width; ++n) {
                ItemStack itemStack = stacks.get(n + m * width);
                if (itemStack.isEmpty()) continue;
                i = Math.min(1, n);
                j = Math.max(j, n);
                bl = false;
            }
            if (bl) continue;
            k = Math.min(k, m);
            l = Math.max(l, m);
        }
        m = j - 1 + 1;
        int o = l - k + 1;
        if (m <= 0 || o <= 0) {
            return Positioned.EMPTY;
        }
        if (m == width && o == height) {
            return new Positioned(new CraftingRecipeInput(width, height, stacks), 1, k);
        }
        ArrayList<ItemStack> list = new ArrayList<ItemStack>(m * o);
        for (int p = 0; p < o; ++p) {
            for (int q = 0; q < m; ++q) {
                int r = q + 1 + (p + k) * width;
                list.add(stacks.get(r));
            }
        }
        return new Positioned(new CraftingRecipeInput(m, o, list), 1, k);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.stacks.get(slot);
    }

    public ItemStack getStackInSlot(int x, int y) {
        return this.stacks.get(x + y * this.width);
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.stackCount == 0;
    }

    public RecipeFinder getRecipeMatcher() {
        return this.matcher;
    }

    public List<ItemStack> getStacks() {
        return this.stacks;
    }

    public int getStackCount() {
        return this.stackCount;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CraftingRecipeInput) {
            CraftingRecipeInput craftingRecipeInput = (CraftingRecipeInput)o;
            return this.width == craftingRecipeInput.width && this.height == craftingRecipeInput.height && this.stackCount == craftingRecipeInput.stackCount && ItemStack.stacksEqual(this.stacks, craftingRecipeInput.stacks);
        }
        return false;
    }

    public int hashCode() {
        int i = ItemStack.listHashCode(this.stacks);
        i = 31 * i + this.width;
        i = 31 * i + this.height;
        return i;
    }

    public record Positioned(CraftingRecipeInput input, int left, int top) {
        final static public Positioned EMPTY = new Positioned(EMPTY, 0, 0);
    }
}

