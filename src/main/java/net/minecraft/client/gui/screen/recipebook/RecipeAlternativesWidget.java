/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RecipeAlternativesWidget
implements Drawable,
Element {
    final static private Identifier OVERLAY_RECIPE_TEXTURE = Identifier.ofVanilla("recipe_book/overlay_recipe");
    final static private int field_32406 = 4;
    final static private int field_32407 = 5;
    final static private float field_33739 = 0.375f;
    final static public int field_42162 = 25;
    final private List<AlternativeButtonWidget> alternativeButtons = Lists.newArrayList();
    private boolean visible;
    private int buttonX;
    private int buttonY;
    private RecipeResultCollection resultCollection = RecipeResultCollection.EMPTY;
    @Nullable
    private NetworkRecipeId lastClickedRecipe;
    final CurrentIndexProvider currentIndexProvider;
    final private boolean furnace;

    public RecipeAlternativesWidget(CurrentIndexProvider currentIndexProvider, boolean furnace) {
        this.currentIndexProvider = currentIndexProvider;
        this.furnace = furnace;
    }

    public void showAlternativesForResult(RecipeResultCollection resultCollection, ContextParameterMap context, boolean filteringCraftable, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta) {
        float o;
        float n;
        float m;
        float h;
        float g;
        this.resultCollection = resultCollection;
        List<RecipeDisplayEntry> list = resultCollection.filter(RecipeResultCollection.RecipeFilterMode.CRAFTABLE);
        List list2 = filteringCraftable ? Collections.emptyList() : resultCollection.filter(RecipeResultCollection.RecipeFilterMode.NOT_CRAFTABLE);
        int i = list.size();
        int j = i + list2.size();
        int k = j <= 16 ? 4 : 5;
        int l = (int)Math.ceil((float)j / (float)k);
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        float f = this.buttonX + Math.min(j, k) * 25;
        if (f > (g = (float)(areaCenterX + 50))) {
            this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((f - g) / delta)));
        }
        if ((h = (float)(this.buttonY + l * 25)) > (m = (float)(areaCenterY + 50))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((h - m) / delta));
        }
        if ((n = (float)this.buttonY) < (o = (float)(areaCenterY - 100))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((n - o) / delta));
        }
        this.visible = true;
        this.alternativeButtons.clear();
        for (int p = 0; p < j; ++p) {
            boolean bl = p < i;
            RecipeDisplayEntry recipeDisplayEntry = bl ? list.get(p) : (RecipeDisplayEntry)list2.get(p - i);
            int q = this.buttonX + 4 + 25 * (p % k);
            int r = this.buttonY + 5 + 25 * (p / k);
            if (this.furnace) {
                this.alternativeButtons.add(new FurnaceAlternativeButtonWidget(this, q, r, recipeDisplayEntry.id(), recipeDisplayEntry.display(), context, bl));
                continue;
            }
            this.alternativeButtons.add(new CraftingAlternativeButtonWidget(this, q, r, recipeDisplayEntry.id(), recipeDisplayEntry.display(), context, bl));
        }
        this.lastClickedRecipe = null;
    }

    public RecipeResultCollection getResults() {
        return this.resultCollection;
    }

    @Nullable
    public NetworkRecipeId getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
            if (!alternativeButtonWidget.mouseClicked(mouseX, mouseY, button)) continue;
            this.lastClickedRecipe = alternativeButtonWidget.recipeId;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!this.visible) {
            return;
        }
        int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
        int j = Math.min(this.alternativeButtons.size(), i);
        int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
        int l = 4;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, OVERLAY_RECIPE_TEXTURE, this.buttonX, this.buttonY, j * 25 + 8, k * 25 + 8);
        for (AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
            alternativeButtonWidget.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    class FurnaceAlternativeButtonWidget
    extends AlternativeButtonWidget {
        final static private Identifier FURNACE_OVERLAY = Identifier.ofVanilla("recipe_book/furnace_overlay");
        final static private Identifier FURNACE_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_highlighted");
        final static private Identifier FURNACE_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled");
        final static private Identifier FURNACE_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled_highlighted");

        public FurnaceAlternativeButtonWidget(RecipeAlternativesWidget recipeAlternativesWidget, int x, int y, NetworkRecipeId recipeId, RecipeDisplay display, ContextParameterMap context, boolean craftable) {
            super(x, y, recipeId, craftable, FurnaceAlternativeButtonWidget.alignRecipe(display, context));
        }

        private static List<AlternativeButtonWidget.InputSlot> alignRecipe(RecipeDisplay display, ContextParameterMap context) {
            FurnaceRecipeDisplay furnaceRecipeDisplay;
            List<ItemStack> list;
            if (display instanceof FurnaceRecipeDisplay && !(list = (furnaceRecipeDisplay = (FurnaceRecipeDisplay)display).ingredient().getStacks(context)).isEmpty()) {
                return List.of(FurnaceAlternativeButtonWidget.slot(1, 1, list));
            }
            return List.of();
        }

        @Override
        protected Identifier getOverlayTexture(boolean enabled) {
            if (enabled) {
                return this.isSelected() ? FURNACE_OVERLAY_HIGHLIGHTED : FURNACE_OVERLAY;
            }
            return this.isSelected() ? FURNACE_OVERLAY_DISABLED_HIGHLIGHTED : FURNACE_OVERLAY_DISABLED;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class CraftingAlternativeButtonWidget
    extends AlternativeButtonWidget {
        final static private Identifier CRAFTING_OVERLAY = Identifier.ofVanilla("recipe_book/crafting_overlay");
        final static private Identifier CRAFTING_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_highlighted");
        final static private Identifier CRAFTING_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled");
        final static private Identifier CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled_highlighted");
        final static private int field_54828 = 3;
        final static private int field_54829 = 3;

        public CraftingAlternativeButtonWidget(RecipeAlternativesWidget recipeAlternativesWidget, int x, int y, NetworkRecipeId recipeId, RecipeDisplay display, ContextParameterMap context, boolean craftable) {
            super(x, y, recipeId, craftable, CraftingAlternativeButtonWidget.collectInputSlots(display, context));
        }

        private static List<AlternativeButtonWidget.InputSlot> collectInputSlots(RecipeDisplay display, ContextParameterMap context) {
            ArrayList<AlternativeButtonWidget.InputSlot> list = new ArrayList<AlternativeButtonWidget.InputSlot>();
            RecipeDisplay recipeDisplay = display;
            Objects.requireNonNull(recipeDisplay);
            RecipeDisplay recipeDisplay2 = recipeDisplay;
            int n = 0;
            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ShapedCraftingRecipeDisplay.class, ShapelessCraftingRecipeDisplay.class}, (Object)recipeDisplay2, n)) {
                case 0: {
                    ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)recipeDisplay2;
                    RecipeGridAligner.alignRecipeToGrid(3, 3, shapedCraftingRecipeDisplay.width(), shapedCraftingRecipeDisplay.height(), shapedCraftingRecipeDisplay.ingredients(), (slot, index, x, y) -> {
                        List<ItemStack> list2 = slot.getStacks(context);
                        if (!list2.isEmpty()) {
                            list.add(CraftingAlternativeButtonWidget.slot(x, y, list2));
                        }
                    });
                    break;
                }
                case 1: {
                    ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay)recipeDisplay2;
                    List<SlotDisplay> list2 = shapelessCraftingRecipeDisplay.ingredients();
                    for (int i = 0; i < list2.size(); ++i) {
                        List<ItemStack> list3 = list2.get(i).getStacks(context);
                        if (list3.isEmpty()) continue;
                        list.add(CraftingAlternativeButtonWidget.slot(i % 3, i / 3, list3));
                    }
                    break;
                }
            }
            return list;
        }

        @Override
        protected Identifier getOverlayTexture(boolean enabled) {
            if (enabled) {
                return this.isSelected() ? CRAFTING_OVERLAY_HIGHLIGHTED : CRAFTING_OVERLAY;
            }
            return this.isSelected() ? CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED : CRAFTING_OVERLAY_DISABLED;
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class AlternativeButtonWidget
    extends ClickableWidget {
        final NetworkRecipeId recipeId;
        final private boolean craftable;
        final private List<InputSlot> inputSlots;

        public AlternativeButtonWidget(int x, int y, NetworkRecipeId recipeId, boolean craftable, List<InputSlot> inputSlots) {
            super(x, y, 24, 24, ScreenTexts.EMPTY);
            this.inputSlots = inputSlots;
            this.recipeId = recipeId;
            this.craftable = craftable;
        }

        protected static InputSlot slot(int x, int y, List<ItemStack> stacks) {
            return new InputSlot(3 + x * 7, 3 + y * 7, stacks);
        }

        protected abstract Identifier getOverlayTexture(boolean var1);

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getOverlayTexture(this.craftable), this.getX(), this.getY(), this.width, this.height);
            float f = this.getX() + 2;
            float g = this.getY() + 2;
            for (InputSlot inputSlot : this.inputSlots) {
                context.getMatrices().pushMatrix();
                context.getMatrices().translate(f + (float)inputSlot.y, g + (float)inputSlot.x);
                context.getMatrices().scale(0.375f, 0.375f);
                context.getMatrices().translate(-8.0f, -8.0f);
                context.drawItem(inputSlot.get(RecipeAlternativesWidget.this.currentIndexProvider.currentIndex()), 0, 0);
                context.getMatrices().popMatrix();
            }
        }

        @Environment(value=EnvType.CLIENT)
        protected static final class InputSlot
        extends Record {
            final int y;
            final int x;
            final private List<ItemStack> stacks;

            public InputSlot(int i, int y, List<ItemStack> list) {
                if (list.isEmpty()) {
                    throw new IllegalArgumentException("Ingredient list must be non-empty");
                }
                this.y = i;
                this.x = y;
                this.stacks = list;
            }

            public ItemStack get(int index) {
                return this.stacks.get(index % this.stacks.size());
            }

            @Override
            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this);
            }

            @Override
            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this);
            }

            @Override
            public final boolean equals(Object object) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this, object);
            }

            public int y() {
                return this.y;
            }

            public int x() {
                return this.x;
            }

            public List<ItemStack> stacks() {
                return this.stacks;
            }
        }
    }
}

