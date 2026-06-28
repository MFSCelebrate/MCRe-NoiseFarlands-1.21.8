/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsInviteScreen
extends RealmsScreen {
    final static private Text INVITE_TEXT = Text.translatable("mco.configure.world.buttons.invite");
    final static private Text INVITE_PROFILE_NAME_TEXT = Text.translatable("mco.configure.world.invite.profile.name").withColor(Colors.LIGHT_GRAY);
    final static private Text INVITING_TEXT = Text.translatable("mco.configure.world.players.inviting").withColor(Colors.LIGHT_GRAY);
    final static private Text PLAYER_ERROR_TEXT = Text.translatable("mco.configure.world.players.error").withColor(Colors.RED);
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    @Nullable
    private TextFieldWidget nameWidget;
    @Nullable
    private ButtonWidget inviteButton;
    final private RealmsServer serverData;
    final private RealmsConfigureWorldScreen configureScreen;
    @Nullable
    private Text errorMessage;

    public RealmsInviteScreen(RealmsConfigureWorldScreen configureScreen, RealmsServer serverData) {
        super(INVITE_TEXT);
        this.configureScreen = configureScreen;
        this.serverData = serverData;
    }

    @Override
    public void init() {
        this.layout.addHeader(INVITE_TEXT, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical().spacing(8));
        this.nameWidget = new TextFieldWidget(this.client.textRenderer, 200, 20, Text.translatable("mco.configure.world.invite.profile.name"));
        directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.nameWidget, INVITE_PROFILE_NAME_TEXT));
        this.inviteButton = directionalLayoutWidget.add(ButtonWidget.builder(INVITE_TEXT, button -> this.onInvite()).width(200).build());
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build());
        this.layout.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    @Override
    protected void setInitialFocus() {
        if (this.nameWidget != null) {
            this.setInitialFocus(this.nameWidget);
        }
    }

    private void onInvite() {
        if (this.inviteButton == null || this.nameWidget == null) {
            return;
        }
        if (StringHelper.isBlank(this.nameWidget.getText())) {
            this.showError(PLAYER_ERROR_TEXT);
            return;
        }
        long l = this.serverData.id;
        String string = this.nameWidget.getText().trim();
        this.inviteButton.active = false;
        this.nameWidget.setEditable(false);
        this.showError(INVITING_TEXT);
        CompletableFuture.supplyAsync(() -> this.configureScreen.invite(l, string), Util.getIoWorkerExecutor()).thenAcceptAsync(success -> {
            if (success.booleanValue()) {
                this.client.setScreen(this.configureScreen);
            } else {
                this.showError(PLAYER_ERROR_TEXT);
            }
            this.nameWidget.setEditable(true);
            this.inviteButton.active = true;
        }, this.executor);
    }

    private void showError(Text errorMessage) {
        this.errorMessage = errorMessage;
        this.client.getNarratorManager().narrateSystemImmediately(errorMessage);
    }

    @Override
    public void close() {
        this.client.setScreen(this.configureScreen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.errorMessage != null && this.inviteButton != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.errorMessage, this.width / 2, this.inviteButton.getY() + this.inviteButton.getHeight() + 8, Colors.WHITE);
        }
    }
}

