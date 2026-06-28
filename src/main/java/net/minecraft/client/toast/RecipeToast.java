/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

@Environment(value=EnvType.CLIENT)
public class RecipeToast
implements Toast {
    final static private Identifier TEXTURE = Identifier.ofVanilla("toast/recipe");
    final static private long DEFAULT_DURATION_MS = 5000L;
    final static private Text TITLE = Text.translatable("recipe.toast.title");
    final static private Text DESCRIPTION = Text.translatable("recipe.toast.description");
    final private List<DisplayItems> displayItems = new ArrayList<DisplayItems>();
    private long startTime;
    private boolean justUpdated;
    private Toast.Visibility visibility = Toast.Visibility.HIDE;
    private int currentItemsDisplayed;

    private RecipeToast() {
    }

    @Override
    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void update(ToastManager manager, long time) {
        if (this.justUpdated) {
            this.startTime = time;
            this.justUpdated = false;
        }
        this.visibility = this.displayItems.isEmpty() ? Toast.Visibility.HIDE : ((double)(time - this.startTime) >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW);
        this.currentItemsDisplayed = (int)((double)time / Math.max(1.0, 5000.0 * manager.getNotificationDisplayTimeMultiplier() / (double)this.displayItems.size()) % (double)this.displayItems.size());
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(textRenderer, TITLE, 30, 7, Colors.PURPLE, false);
        context.drawText(textRenderer, DESCRIPTION, 30, 18, Colors.BLACK, false);
        DisplayItems displayItems = this.displayItems.get(this.currentItemsDisplayed);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(0.6f, 0.6f);
        context.drawItemWithoutEntity(displayItems.categoryItem(), 3, 3);
        context.getMatrices().popMatrix();
        context.drawItemWithoutEntity(displayItems.unlockedItem(), 8, 8);
    }

    private void addRecipes(ItemStack categoryItem, ItemStack unlockedItem) {
        this.displayItems.add(new DisplayItems(categoryItem, unlockedItem));
        this.justUpdated = true;
    }

    public static void show(ToastManager toastManager, RecipeDisplay display) {
        RecipeToast recipeToast = toastManager.getToast(RecipeToast.class, TYPE);
        if (recipeToast == null) {
            recipeToast = new RecipeToast();
            toastManager.add(recipeToast);
        }
        ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(toastManager.getClient().world);
        ItemStack itemStack = display.craftingStation().getFirst(contextParameterMap);
        ItemStack itemStack2 = display.result().getFirst(contextParameterMap);
        recipeToast.addRecipes(itemStack, itemStack2);
    }

    @Environment(value=EnvType.CLIENT)
    record DisplayItems(ItemStack categoryItem, ItemStack unlockedItem) {
    }
}

