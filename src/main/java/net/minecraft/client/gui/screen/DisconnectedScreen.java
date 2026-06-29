/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DisconnectedScreen
extends Screen {
    final static private Text TO_MENU_TEXT = Text.translatable("gui.toMenu");
    final static private Text TO_TITLE_TEXT = Text.translatable("gui.toTitle");
    final static private Text REPORT_TO_SERVER_TEXT = Text.translatable("gui.report_to_server");
    final static private Text OPEN_REPORT_DIR_TEXT = Text.translatable("gui.open_report_dir");
    final private Screen parent;
    final private DisconnectionInfo info;
    final private Text buttonLabel;
    final private DirectionalLayoutWidget grid = DirectionalLayoutWidget.vertical();

    public DisconnectedScreen(Screen parent, Text title, Text reason) {
        this(parent, title, new DisconnectionInfo(reason));
    }

    public DisconnectedScreen(Screen parent, Text title, Text reason, Text buttonLabel) {
        this(parent, title, new DisconnectionInfo(reason), buttonLabel);
    }

    public DisconnectedScreen(Screen parent, Text title, DisconnectionInfo info) {
        this(parent, title, info, TO_MENU_TEXT);
    }

    public DisconnectedScreen(Screen parent, Text title, DisconnectionInfo info, Text buttonLabel) {
        super(title);
        this.parent = parent;
        this.info = info;
        this.buttonLabel = buttonLabel;
    }

    @Override
    protected void init() {
        this.grid.getMainPositioner().alignHorizontalCenter().net_minecraft_client_gui_widget_Positioner_margin(10);
        this.grid.add(new TextWidget(this.title, this.textRenderer));
        this.grid.add(new MultilineTextWidget(this.info.reason(), this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
        this.grid.getMainPositioner().net_minecraft_client_gui_widget_Positioner_margin(2);
        this.info.bugReportLink().ifPresent(uri -> this.grid.add(ButtonWidget.builder(REPORT_TO_SERVER_TEXT, ConfirmLinkScreen.opening((Screen)this, uri, false)).width(200).build()));
        this.info.report().ifPresent(path -> this.grid.add(ButtonWidget.builder(OPEN_REPORT_DIR_TEXT, button -> Util.getOperatingSystem().open(path.getParent())).width(200).build()));
        ButtonWidget buttonWidget = this.client.isMultiplayerEnabled() ? ButtonWidget.builder(this.buttonLabel, button -> this.client.setScreen(this.parent)).width(200).build() : ButtonWidget.builder(TO_TITLE_TEXT, button -> this.client.setScreen(new TitleScreen())).width(200).build();
        this.grid.add(buttonWidget);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        SimplePositioningWidget.setPos(this.grid, this.getNavigationFocus());
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.title, this.info.reason());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

