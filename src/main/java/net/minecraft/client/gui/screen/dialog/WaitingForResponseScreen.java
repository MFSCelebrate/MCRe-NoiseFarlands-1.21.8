/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WaitingForResponseScreen
extends Screen {
    final static private Text TITLE = Text.translatable("gui.waitingForResponse.title");
    final static private Text[] BUTTON_TEXTS = new Text[]{Text.empty(), Text.translatable("gui.waitingForResponse.button.inactive", 4), Text.translatable("gui.waitingForResponse.button.inactive", 3), Text.translatable("gui.waitingForResponse.button.inactive", 2), Text.translatable("gui.waitingForResponse.button.inactive", 1), ScreenTexts.BACK};
    final static private int SECONDS_BEFORE_BACK_BUTTON_APPEARS = 1;
    final static private int SECONDS_BEFORE_BACK_BUTTON_ACTIVATES = 5;
    @Nullable
    final private Screen parent;
    final private ThreePartsLayoutWidget layout;
    final private ButtonWidget backButton;
    private int inactiveTicks;

    public WaitingForResponseScreen(@Nullable Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.layout = new ThreePartsLayoutWidget(this, 33, 0);
        this.backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build();
    }

    @Override
    protected void init() {
        super.init();
        this.layout.addHeader(TITLE, this.textRenderer);
        this.layout.addBody(this.backButton);
        this.backButton.visible = false;
        this.backButton.active = false;
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.backButton.active) {
            int i;
            this.backButton.visible = (i = this.inactiveTicks++ / 20) >= 1;
            this.backButton.setMessage(BUTTON_TEXTS[i]);
            if (i == 5) {
                this.backButton.active = true;
                this.narrateScreenIfNarrationEnabled(true);
            }
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.backButton.active;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Nullable
    public Screen getParentScreen() {
        return this.parent;
    }
}

