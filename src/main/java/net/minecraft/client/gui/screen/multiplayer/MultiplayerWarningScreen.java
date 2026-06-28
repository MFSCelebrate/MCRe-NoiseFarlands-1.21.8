/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class MultiplayerWarningScreen
extends WarningScreen {
    final static private Text HEADER = Text.translatable("multiplayerWarning.header").formatted(Formatting.BOLD);
    final static private Text MESSAGE = Text.translatable("multiplayerWarning.message");
    final static private Text CHECK_MESSAGE = Text.translatable("multiplayerWarning.check");
    final static private Text NARRATED_TEXT = HEADER.copy().append("\n").append(MESSAGE);
    final private Screen parent;

    public MultiplayerWarningScreen(Screen parent) {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT);
        this.parent = parent;
    }

    @Override
    protected LayoutWidget getLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.PROCEED, button -> {
            if (this.checkbox.isChecked()) {
                this.client.options.skipMultiplayerWarning = true;
                this.client.options.write();
            }
            this.client.setScreen(new MultiplayerScreen(this.parent));
        }).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
        return directionalLayoutWidget;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}

