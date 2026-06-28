/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class ConfirmLinkScreen
extends ConfirmScreen {
    final static private Text COPY = Text.translatable("chat.copy");
    final static private Text WARNING = Text.translatable("chat.link.warning").withColor(Colors.LIGHT_PINK);
    final static private int field_61000 = 100;
    final private String link;
    final private boolean drawWarning;

    public ConfirmLinkScreen(BooleanConsumer callback, String link, boolean linkTrusted) {
        this(callback, (Text)ConfirmLinkScreen.getConfirmText(linkTrusted), (Text)Text.literal(link), link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, String link, boolean linkTrusted) {
        this(callback, title, (Text)ConfirmLinkScreen.getConfirmText(linkTrusted, link), link, linkTrusted ? ScreenTexts.CANCEL : ScreenTexts.NO, linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, URI link, boolean linkTrusted) {
        this(callback, title, link.toString(), linkTrusted);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, Text message, URI link, Text noText, boolean linkTrusted) {
        this(callback, title, message, link.toString(), noText, true);
    }

    public ConfirmLinkScreen(BooleanConsumer callback, Text title, Text message, String link, Text noText, boolean linkTrusted) {
        super(callback, title, message);
        this.yesText = linkTrusted ? ScreenTexts.OPEN_LINK : ScreenTexts.YES;
        this.noText = noText;
        this.drawWarning = !linkTrusted;
        this.link = link;
    }

    protected static MutableText getConfirmText(boolean linkTrusted, String link) {
        return ConfirmLinkScreen.getConfirmText(linkTrusted).append(ScreenTexts.SPACE).append(Text.literal(link));
    }

    protected static MutableText getConfirmText(boolean linkTrusted) {
        return Text.translatable(linkTrusted ? "chat.link.confirmTrusted" : "chat.link.confirm");
    }

    @Override
    protected void initExtras() {
        if (this.drawWarning) {
            this.layout.add(new TextWidget(WARNING, this.textRenderer));
        }
    }

    @Override
    protected void addButtons(DirectionalLayoutWidget layout) {
        this.yesButton = layout.add(ButtonWidget.builder(this.yesText, button -> this.callback.accept(true)).width(100).build());
        layout.add(ButtonWidget.builder(COPY, button -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }).width(100).build());
        this.noButton = layout.add(ButtonWidget.builder(this.noText, button -> this.callback.accept(false)).width(100).build());
    }

    public void copyToClipboard() {
        this.client.keyboard.setClipboard(this.link);
    }

    public static void open(Screen parent, String url, boolean linkTrusted) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(url);
            }
            minecraftClient.setScreen(parent);
        }, url, linkTrusted));
    }

    public static void open(Screen parent, URI uri, boolean linkTrusted) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open(uri);
            }
            minecraftClient.setScreen(parent);
        }, uri.toString(), linkTrusted));
    }

    public static void open(Screen parent, URI uri) {
        ConfirmLinkScreen.open(parent, uri, true);
    }

    public static void open(Screen parent, String url) {
        ConfirmLinkScreen.open(parent, url, true);
    }

    public static ButtonWidget.PressAction opening(Screen parent, String url, boolean linkTrusted) {
        return button -> ConfirmLinkScreen.open(parent, url, linkTrusted);
    }

    public static ButtonWidget.PressAction opening(Screen parent, URI uri, boolean linkTrusted) {
        return button -> ConfirmLinkScreen.open(parent, uri, linkTrusted);
    }

    public static ButtonWidget.PressAction opening(Screen parent, String url) {
        return ConfirmLinkScreen.opening(parent, url, true);
    }

    public static ButtonWidget.PressAction opening(Screen parent, URI uri) {
        return ConfirmLinkScreen.opening(parent, uri, true);
    }
}

