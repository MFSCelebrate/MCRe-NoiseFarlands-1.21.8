/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SmokerScreen
extends AbstractFurnaceScreen<SmokerScreenHandler> {
    final static private Identifier LIT_PROGRESS_TEXTURE = Identifier.ofVanilla("container/smoker/lit_progress");
    final static private Identifier BURN_PROGRESS_TEXTURE = Identifier.ofVanilla("container/smoker/burn_progress");
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/smoker.png");
    final static private Text TOGGLE_SMOKABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.smokable");
    final static private List<RecipeBookWidget.Tab> TABS = List.of(new RecipeBookWidget.Tab(RecipeBookType.SMOKER), new RecipeBookWidget.Tab(Items.PORKCHOP, RecipeBookCategories.SMOKER_FOOD));

    public SmokerScreen(SmokerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TOGGLE_SMOKABLE_TEXT, TEXTURE, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE, TABS);
    }
}

