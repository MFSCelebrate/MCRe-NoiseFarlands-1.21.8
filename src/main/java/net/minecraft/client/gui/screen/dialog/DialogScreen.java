/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandlers;
import net.minecraft.client.gui.screen.dialog.DialogControls;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.ScrollableLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class DialogScreen<T extends Dialog>
extends Screen {
    final static public Text CUSTOM_SCREEN_REJECTED_DISCONNECT_TEXT = Text.translatable("menu.custom_screen_info.disconnect");
    final static private int field_60758 = 20;
    final static private ButtonTextures WARNING_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("dialog/warning_button"), Identifier.ofVanilla("dialog/warning_button_disabled"), Identifier.ofVanilla("dialog/warning_button_highlighted"));
    final private T dialog;
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    @Nullable
    final private Screen parent;
    @Nullable
    private ScrollableLayoutWidget contents;
    private ButtonWidget warningButton;
    final private DialogNetworkAccess networkAccess;
    private Supplier<Optional<ClickEvent>> cancelAction = DialogControls.EMPTY_ACTION_CLICK_EVENT;

    public DialogScreen(@Nullable Screen parent, T dialog, DialogNetworkAccess networkAccess) {
        super(dialog.common().title());
        this.dialog = dialog;
        this.parent = parent;
        this.networkAccess = networkAccess;
    }

    @Override
    protected final void init() {
        super.init();
        this.warningButton = this.createWarningButton();
        this.warningButton.setNavigationOrder(-10);
        DialogControls dialogControls = new DialogControls(this);
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(10);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        this.layout.addHeader(this.createHeader());
        for (DialogBody dialogBody : this.dialog.common().body()) {
            Widget widget = DialogBodyHandlers.createWidget(this, dialogBody);
            if (widget == null) continue;
            directionalLayoutWidget.add(widget);
        }
        for (DialogInput dialogInput : this.dialog.common().inputs()) {
            dialogControls.addInput(dialogInput, directionalLayoutWidget::add);
        }
        this.initBody(directionalLayoutWidget, dialogControls, this.dialog, this.networkAccess);
        this.contents = new ScrollableLayoutWidget(this.client, directionalLayoutWidget, this.layout.getContentHeight());
        this.layout.addBody(this.contents);
        this.initHeaderAndFooter(this.layout, dialogControls, this.dialog, this.networkAccess);
        this.cancelAction = dialogControls.createClickEvent(this.dialog.getCancelAction());
        this.layout.forEachChild(child -> {
            if (child != this.warningButton) {
                this.addDrawableChild(child);
            }
        });
        this.addDrawableChild(this.warningButton);
        this.refreshWidgetPositions();
    }

    protected void initBody(DirectionalLayoutWidget bodyLayout, DialogControls controls, T dialog, DialogNetworkAccess networkAccess) {
    }

    protected void initHeaderAndFooter(ThreePartsLayoutWidget layout, DialogControls controls, T dialog, DialogNetworkAccess networkAccess) {
    }

    @Override
    protected void refreshWidgetPositions() {
        this.contents.setHeight(this.layout.getContentHeight());
        this.layout.refreshPositions();
        this.resetWarningButtonPosition();
    }

    protected Widget createHeader() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(10);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter().alignVerticalCenter();
        directionalLayoutWidget.add(new TextWidget(this.title, this.textRenderer));
        directionalLayoutWidget.add(this.warningButton);
        return directionalLayoutWidget;
    }

    protected void resetWarningButtonPosition() {
        int i = this.warningButton.getX();
        int j = this.warningButton.getY();
        if (i < 0 || j < 0 || i > this.width - 20 || j > this.height - 20) {
            this.warningButton.setX(Math.max(0, this.width - 40));
            this.warningButton.setY(Math.min(5, this.height));
        }
    }

    private ButtonWidget createWarningButton() {
        TexturedButtonWidget texturedButtonWidget = new TexturedButtonWidget(0, 0, 20, 20, WARNING_BUTTON_TEXTURES, button -> this.client.setScreen(WarningScreen.create(this.client, this)), Text.translatable("menu.custom_screen_info.button_narration"));
        texturedButtonWidget.setTooltip(Tooltip.of(Text.translatable("menu.custom_screen_info.tooltip")));
        return texturedButtonWidget;
    }

    @Override
    public boolean shouldPause() {
        return this.dialog.common().pause();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.dialog.common().canCloseWithEscape();
    }

    @Override
    public void close() {
        this.runAction(this.cancelAction.get(), AfterAction.CLOSE);
    }

    public void runAction(Optional<ClickEvent> clickEvent) {
        this.runAction(clickEvent, this.dialog.common().afterAction());
    }

    public void runAction(Optional<ClickEvent> clickEvent, AfterAction afterAction) {
        Screen screen;
        switch (afterAction) {
            default: {
                throw new MatchException(null, null);
            }
            case NONE: {
                Screen screen2 = this;
                break;
            }
            case CLOSE: {
                Screen screen2 = this.parent;
                break;
            }
            case WAIT_FOR_RESPONSE: {
                Screen screen2 = screen = new WaitingForResponseScreen(this.parent);
            }
        }
        if (clickEvent.isPresent()) {
            this.handleClickEvent(clickEvent.get(), screen);
        } else {
            this.client.setScreen(screen);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    private void handleClickEvent(ClickEvent clickEvent, @Nullable Screen afterActionScreen) {
        ClickEvent clickEvent2 = clickEvent;
        Objects.requireNonNull(clickEvent2);
        ClickEvent clickEvent3 = clickEvent2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.RunCommand.class, ClickEvent.ShowDialog.class, ClickEvent.Custom.class}, (Object)clickEvent3, n)) {
            case 0: {
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent3;
                try {
                    String string;
                    String string2 = string = runCommand.command();
                    this.networkAccess.runClickEventCommand(CommandManager.stripLeadingSlash(string2), afterActionScreen);
                    return;
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
            }
            case 1: {
                ClickEvent.ShowDialog showDialog = (ClickEvent.ShowDialog)clickEvent3;
                this.networkAccess.showDialog(showDialog.dialog(), afterActionScreen);
                return;
            }
            case 2: {
                ClickEvent.Custom custom = (ClickEvent.Custom)clickEvent3;
                this.networkAccess.sendCustomClickActionPacket(custom.id(), custom.payload());
                this.client.setScreen(afterActionScreen);
                return;
            }
        }
        DialogScreen.handleBasicClickEvent(clickEvent, this.client, afterActionScreen);
    }

    @Nullable
    public Screen getParentScreen() {
        return this.parent;
    }

    protected static Widget createGridWidget(List<? extends Widget> widgets, int columns) {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().alignHorizontalCenter();
        gridWidget.setColumnSpacing(2).setRowSpacing(2);
        int i = widgets.size();
        int j = i / columns;
        int k = j * columns;
        for (int l = 0; l < k; ++l) {
            gridWidget.add(widgets.get(l), l / columns, l % columns);
        }
        if (i != k) {
            DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(2);
            directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
            for (int m = k; m < i; ++m) {
                directionalLayoutWidget.add(widgets.get(m));
            }
            gridWidget.add(directionalLayoutWidget, j, 0, 1, columns);
        }
        return gridWidget;
    }

    @Environment(value=EnvType.CLIENT)
    public static class WarningScreen
    extends ConfirmScreen {
        final private MutableObject<Screen> dialogScreen;

        public static Screen create(MinecraftClient client, Screen screen) {
            return new WarningScreen(client, (MutableObject<Screen>)new MutableObject((Object)screen));
        }

        private WarningScreen(MinecraftClient client, MutableObject<Screen> mutableObject) {
            super(bl -> {
                if (bl) {
                    GameMenuScreen.disconnect(client, CUSTOM_SCREEN_REJECTED_DISCONNECT_TEXT);
                } else {
                    client.setScreen((Screen)mutableObject.getValue());
                }
            }, Text.translatable("menu.custom_screen_info.title"), Text.translatable("menu.custom_screen_info.contents"), ScreenTexts.returnToMenuOrDisconnect(client.isInSingleplayer()), ScreenTexts.BACK);
            this.dialogScreen = mutableObject;
        }

        @Nullable
        public Screen getDialogScreen() {
            return (Screen)this.dialogScreen.getValue();
        }

        public void setDialogScreen(@Nullable Screen screen) {
            this.dialogScreen.setValue((Object)screen);
        }
    }
}

