/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RealmsCreateRealmScreen
extends RealmsScreen {
    final static private Text TITLE_TEXT = Text.translatable("mco.selectServer.create");
    final static private Text WORLD_NAME_TEXT = Text.translatable("mco.configure.world.name");
    final static private Text WORLD_DESCRIPTION_TEXT = Text.translatable("mco.configure.world.description");
    final static private int field_45243 = 10;
    final static private int field_45244 = 210;
    final private RealmsMainScreen parent;
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private TextFieldWidget nameBox;
    private TextFieldWidget descriptionBox;
    final private Runnable worldCreator;

    public RealmsCreateRealmScreen(RealmsMainScreen parent, RealmsServer server, boolean prerelease) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.worldCreator = () -> this.createWorld(server, prerelease);
    }

    @Override
    public void init() {
        this.layout.addHeader(this.title, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical()).spacing(10);
        ButtonWidget buttonWidget = ButtonWidget.builder(ScreenTexts.CONTINUE, button -> this.worldCreator.run()).build();
        buttonWidget.active = false;
        this.nameBox = new TextFieldWidget(this.textRenderer, 210, 20, WORLD_NAME_TEXT);
        this.nameBox.setChangedListener(name -> {
            buttonWidget.active = !StringHelper.isBlank(name);
        });
        this.descriptionBox = new TextFieldWidget(this.textRenderer, 210, 20, WORLD_DESCRIPTION_TEXT);
        directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.nameBox, WORLD_NAME_TEXT));
        directionalLayoutWidget.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.descriptionBox, WORLD_DESCRIPTION_TEXT));
        DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(10));
        directionalLayoutWidget2.add(buttonWidget);
        directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameBox);
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    private void createWorld(RealmsServer realmsServer, boolean prerelease) {
        if (!realmsServer.isPrerelease() && prerelease) {
            AtomicBoolean atomicBoolean = new AtomicBoolean();
            this.client.setScreen(new NoticeScreen(() -> {
                atomicBoolean.set(true);
                this.parent.removeSelection();
                this.client.setScreen(this.parent);
            }, Text.translatable("mco.upload.preparing"), Text.empty()));
            ((CompletableFuture)CompletableFuture.supplyAsync(() -> RealmsCreateRealmScreen.createPrereleaseServer(realmsServer), Util.getMainWorkerExecutor()).thenAcceptAsync(prereleaseServer -> {
                if (!atomicBoolean.get()) {
                    this.createWorld((RealmsServer)prereleaseServer);
                }
            }, (Executor)this.client)).exceptionallyAsync(throwable -> {
                Text text;
                this.parent.removeSelection();
                Throwable throwable2 = throwable.getCause();
                if (throwable2 instanceof RealmsServiceException) {
                    RealmsServiceException realmsServiceException = (RealmsServiceException)throwable2;
                    text = realmsServiceException.error.getText();
                } else {
                    text = Text.translatable("mco.errorMessage.initialize.failed");
                }
                this.client.setScreen(new RealmsGenericErrorScreen(text, (Screen)this.parent));
                return null;
            }, (Executor)this.client);
        } else {
            this.createWorld(realmsServer);
        }
    }

    private static RealmsServer createPrereleaseServer(RealmsServer parent) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            return realmsClient.createPrereleaseServer(parent.id);
        }
        catch (RealmsServiceException realmsServiceException) {
            throw new RuntimeException(realmsServiceException);
        }
    }

    private void createWorld(RealmsServer server) {
        WorldCreationTask worldCreationTask = new WorldCreationTask(server.id, this.nameBox.getText(), this.descriptionBox.getText());
        RealmsCreateWorldScreen realmsCreateWorldScreen = RealmsCreateWorldScreen.newRealm(this, server, worldCreationTask, () -> this.client.execute(() -> {
            RealmsMainScreen.resetServerList();
            this.client.setScreen(this.parent);
        }));
        this.client.setScreen(realmsCreateWorldScreen);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}

