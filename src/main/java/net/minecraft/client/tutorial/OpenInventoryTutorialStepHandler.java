/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler
implements TutorialStepHandler {
    final static private int DELAY = 600;
    final static private Text TITLE = Text.translatable("tutorial.open_inventory.title");
    final static private Text DESCRIPTION = Text.translatable("tutorial.open_inventory.description", TutorialManager.keyToText("inventory"));
    final private TutorialManager manager;
    @Nullable
    private TutorialToast toast;
    private int ticks;

    public OpenInventoryTutorialStepHandler(TutorialManager manager) {
        this.manager = manager;
    }

    @Override
    public void tick() {
        ++this.ticks;
        if (!this.manager.isInSurvival()) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks >= 600 && this.toast == null) {
            MinecraftClient minecraftClient = this.manager.getClient();
            this.toast = new TutorialToast(minecraftClient.textRenderer, TutorialToast.Type.RECIPE_BOOK, TITLE, DESCRIPTION, false);
            minecraftClient.getToastManager().add(this.toast);
        }
    }

    @Override
    public void destroy() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onInventoryOpened() {
        this.manager.setStep(TutorialStep.CRAFT_PLANKS);
    }
}

