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
import net.minecraft.client.input.Input;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MovementTutorialStepHandler
implements TutorialStepHandler {
    final static private int field_33029 = 40;
    final static private int field_33030 = 40;
    final static private int field_33031 = 100;
    final static private int field_33032 = 20;
    final static private int field_33033 = -1;
    final static private Text MOVE_TITLE = Text.translatable("tutorial.move.title", TutorialManager.keyToText("forward"), TutorialManager.keyToText("left"), TutorialManager.keyToText("back"), TutorialManager.keyToText("right"));
    final static private Text MOVE_DESCRIPTION = Text.translatable("tutorial.move.description", TutorialManager.keyToText("jump"));
    final static private Text LOOK_TITLE = Text.translatable("tutorial.look.title");
    final static private Text LOOK_DESCRIPTION = Text.translatable("tutorial.look.description");
    final private TutorialManager manager;
    @Nullable
    private TutorialToast moveToast;
    @Nullable
    private TutorialToast lookAroundToast;
    private int ticks;
    private int movedTicks;
    private int lookedAroundTicks;
    private boolean movedLastTick;
    private boolean lookedAroundLastTick;
    private int moveAroundCompletionTicks = -1;
    private int lookAroundCompletionTicks = -1;

    public MovementTutorialStepHandler(TutorialManager manager) {
        this.manager = manager;
    }

    @Override
    public void tick() {
        ++this.ticks;
        if (this.movedLastTick) {
            ++this.movedTicks;
            this.movedLastTick = false;
        }
        if (this.lookedAroundLastTick) {
            ++this.lookedAroundTicks;
            this.lookedAroundLastTick = false;
        }
        if (this.moveAroundCompletionTicks == -1 && this.movedTicks > 40) {
            if (this.moveToast != null) {
                this.moveToast.hide();
                this.moveToast = null;
            }
            this.moveAroundCompletionTicks = this.ticks;
        }
        if (this.lookAroundCompletionTicks == -1 && this.lookedAroundTicks > 40) {
            if (this.lookAroundToast != null) {
                this.lookAroundToast.hide();
                this.lookAroundToast = null;
            }
            this.lookAroundCompletionTicks = this.ticks;
        }
        if (this.moveAroundCompletionTicks != -1 && this.lookAroundCompletionTicks != -1) {
            if (this.manager.isInSurvival()) {
                this.manager.setStep(TutorialStep.FIND_TREE);
            } else {
                this.manager.setStep(TutorialStep.NONE);
            }
        }
        if (this.moveToast != null) {
            this.moveToast.setProgress((float)this.movedTicks / 40.0f);
        }
        if (this.lookAroundToast != null) {
            this.lookAroundToast.setProgress((float)this.lookedAroundTicks / 40.0f);
        }
        if (this.ticks >= 100) {
            MinecraftClient minecraftClient = this.manager.getClient();
            if (this.moveAroundCompletionTicks == -1 && this.moveToast == null) {
                this.moveToast = new TutorialToast(minecraftClient.textRenderer, TutorialToast.Type.MOVEMENT_KEYS, MOVE_TITLE, MOVE_DESCRIPTION, true);
                minecraftClient.getToastManager().add(this.moveToast);
            } else if (this.moveAroundCompletionTicks != -1 && this.ticks - this.moveAroundCompletionTicks >= 20 && this.lookAroundCompletionTicks == -1 && this.lookAroundToast == null) {
                this.lookAroundToast = new TutorialToast(minecraftClient.textRenderer, TutorialToast.Type.MOUSE, LOOK_TITLE, LOOK_DESCRIPTION, true);
                minecraftClient.getToastManager().add(this.lookAroundToast);
            }
        }
    }

    @Override
    public void destroy() {
        if (this.moveToast != null) {
            this.moveToast.hide();
            this.moveToast = null;
        }
        if (this.lookAroundToast != null) {
            this.lookAroundToast.hide();
            this.lookAroundToast = null;
        }
    }

    @Override
    public void onMovement(Input input) {
        if (input.playerInput.forward() || input.playerInput.backward() || input.playerInput.left() || input.playerInput.right() || input.playerInput.jump()) {
            this.movedLastTick = true;
        }
    }

    @Override
    public void onMouseUpdate(double deltaX, double deltaY) {
        if (Math.abs(deltaX) > 0.01 || Math.abs(deltaY) > 0.01) {
            this.lookedAroundLastTick = true;
        }
    }
}

