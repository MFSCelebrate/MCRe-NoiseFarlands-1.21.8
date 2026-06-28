/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

@Environment(value=EnvType.CLIENT)
public class RealmsPopups {
    final static private int INFO_TEXT_COLOR = 8226750;
    final static private Text INFO_TEXT = Text.translatable("mco.info").withColor(8226750);
    final static private Text WARNING_TEXT = Text.translatable("mco.warning").withColor(Colors.RED);

    public static PopupScreen createCustomPopup(Screen parent, Text title, Text message, Consumer<PopupScreen> onContinuePressed) {
        return new PopupScreen.Builder(parent, title).message(message).button(ScreenTexts.CONTINUE, onContinuePressed).button(ScreenTexts.CANCEL, PopupScreen::close).build();
    }

    public static PopupScreen createInfoPopup(Screen parent, Text message, Consumer<PopupScreen> onContinuePressed) {
        return new PopupScreen.Builder(parent, INFO_TEXT).message(message).button(ScreenTexts.CONTINUE, onContinuePressed).button(ScreenTexts.CANCEL, PopupScreen::close).build();
    }

    public static PopupScreen createContinuableWarningPopup(Screen parent, Text message, Consumer<PopupScreen> onContinuePressed) {
        return new PopupScreen.Builder(parent, WARNING_TEXT).message(message).button(ScreenTexts.CONTINUE, onContinuePressed).button(ScreenTexts.CANCEL, PopupScreen::close).build();
    }

    public static PopupScreen createNonContinuableWarningPopup(Screen parent, Text message, Consumer<PopupScreen> onOkPressed) {
        return new PopupScreen.Builder(parent, WARNING_TEXT).message(message).button(ScreenTexts.OK, onOkPressed).build();
    }
}

