/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RecipeGroupButtonWidget
extends ToggleButtonWidget {
    final static private ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/tab"), Identifier.ofVanilla("recipe_book/tab_selected"));
    final private RecipeBookWidget.Tab tab;
    final static private float field_32412 = 15.0f;
    private float bounce;

    public RecipeGroupButtonWidget(RecipeBookWidget.Tab tab) {
        super(0, 0, 35, 27, false);
        this.tab = tab;
        this.setTextures(TEXTURES);
    }

    public void checkForNewRecipes(ClientRecipeBook recipeBook, boolean filteringCraftable) {
        RecipeResultCollection.RecipeFilterMode recipeFilterMode = filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY;
        List<RecipeResultCollection> list = recipeBook.getResultsForCategory(this.tab.category());
        for (RecipeResultCollection recipeResultCollection : list) {
            for (RecipeDisplayEntry recipeDisplayEntry : recipeResultCollection.filter(recipeFilterMode)) {
                if (!recipeBook.isHighlighted(recipeDisplayEntry.id())) continue;
                this.bounce = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.textures == null) {
            return;
        }
        if (this.bounce > 0.0f) {
            float f = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
            context.getMatrices().scale(1.0f, f);
            context.getMatrices().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
        }
        Identifier identifier = this.textures.get(true, this.toggled);
        int i = this.getX();
        if (this.toggled) {
            i -= 2;
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i, this.getY(), this.width, this.height);
        this.renderIcons(context);
        if (this.bounce > 0.0f) {
            context.getMatrices().popMatrix();
            this.bounce -= deltaTicks;
        }
    }

    private void renderIcons(DrawContext context) {
        int i;
        int n = i = this.toggled ? -2 : 0;
        if (this.tab.secondaryIcon().isPresent()) {
            context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 3 + i, this.getY() + 5);
            context.drawItemWithoutEntity(this.tab.secondaryIcon().get(), this.getX() + 14 + i, this.getY() + 5);
        } else {
            context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 9 + i, this.getY() + 5);
        }
    }

    public RecipeBookGroup getCategory() {
        return this.tab.category();
    }

    public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
        List<RecipeResultCollection> list = recipeBook.getResultsForCategory(this.tab.category());
        this.visible = false;
        for (RecipeResultCollection recipeResultCollection : list) {
            if (!recipeResultCollection.hasDisplayableRecipes()) continue;
            this.visible = true;
            break;
        }
        return this.visible;
    }
}

