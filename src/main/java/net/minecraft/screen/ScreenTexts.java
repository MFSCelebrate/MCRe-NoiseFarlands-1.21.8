/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ScreenTexts {
    final static public Text EMPTY = Text.empty();
    final static public Text ON = Text.translatable("options.on");
    final static public Text OFF = Text.translatable("options.off");
    final static public Text DONE = Text.translatable("gui.done");
    final static public Text CANCEL = Text.translatable("gui.cancel");
    final static public Text YES = Text.translatable("gui.yes");
    final static public Text NO = Text.translatable("gui.no");
    final static public Text OK = Text.translatable("gui.ok");
    final static public Text PROCEED = Text.translatable("gui.proceed");
    final static public Text CONTINUE = Text.translatable("gui.continue");
    final static public Text BACK = Text.translatable("gui.back");
    final static public Text TO_TITLE = Text.translatable("gui.toTitle");
    final static public Text ACKNOWLEDGE = Text.translatable("gui.acknowledge");
    final static public Text OPEN_LINK = Text.translatable("chat.link.open");
    final static public Text COPY_LINK_TO_CLIPBOARD = Text.translatable("gui.copy_link_to_clipboard");
    final static public Text DISCONNECT = Text.translatable("menu.disconnect");
    final static public Text RETURN_TO_MENU = Text.translatable("menu.returnToMenu");
    final static public Text CONNECT_FAILED_TRANSFER = Text.translatable("connect.failed.transfer");
    final static public Text CONNECT_FAILED = Text.translatable("connect.failed");
    final static public Text LINE_BREAK = Text.literal("\n");
    final static public Text SENTENCE_SEPARATOR = Text.literal(". ");
    final static public Text ELLIPSIS = Text.literal("...");
    final static public Text SPACE = ScreenTexts.space();

    public static MutableText space() {
        return Text.literal(" ");
    }

    public static MutableText days(long days) {
        return Text.translatable("gui.days", days);
    }

    public static MutableText hours(long hours) {
        return Text.translatable("gui.hours", hours);
    }

    public static MutableText minutes(long minutes) {
        return Text.translatable("gui.minutes", minutes);
    }

    public static Text onOrOff(boolean on) {
        return on ? ON : OFF;
    }

    public static Text returnToMenuOrDisconnect(boolean singleplayer) {
        return singleplayer ? RETURN_TO_MENU : DISCONNECT;
    }

    public static MutableText composeToggleText(Text text, boolean value) {
        return Text.translatable(value ? "options.on.composed" : "options.off.composed", text);
    }

    public static MutableText composeGenericOptionText(Text text, Text value) {
        return Text.translatable("options.generic_value", text, value);
    }

    public static MutableText joinSentences(Text ... sentences) {
        MutableText mutableText = Text.empty();
        for (int i = 0; i < sentences.length; ++i) {
            mutableText.append(sentences[i]);
            if (i == sentences.length - 1) continue;
            mutableText.append(SENTENCE_SEPARATOR);
        }
        return mutableText;
    }

    public static Text joinLines(Text ... texts) {
        return ScreenTexts.joinLines(Arrays.asList(texts));
    }

    public static Text joinLines(Collection<? extends Text> texts) {
        return Texts.join(texts, LINE_BREAK);
    }
}

