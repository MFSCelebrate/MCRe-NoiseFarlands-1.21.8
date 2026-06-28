/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.toast.NowPlayingToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.dialog.Dialogs;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.DialogTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GameMenuScreen
extends Screen {
    final static private Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla("icon/draft_report");
    final static private int GRID_COLUMNS = 2;
    final static private int BUTTONS_TOP_MARGIN = 50;
    final static private int GRID_MARGIN = 4;
    final static private int WIDE_BUTTON_WIDTH = 204;
    final static private int NORMAL_BUTTON_WIDTH = 98;
    final static private Text RETURN_TO_GAME_TEXT = Text.translatable("menu.returnToGame");
    final static private Text ADVANCEMENTS_TEXT = Text.translatable("gui.advancements");
    final static private Text STATS_TEXT = Text.translatable("gui.stats");
    final static private Text SEND_FEEDBACK_TEXT = Text.translatable("menu.sendFeedback");
    final static private Text REPORT_BUGS_TEXT = Text.translatable("menu.reportBugs");
    final static private Text FEEDBACK_TEXT = Text.translatable("menu.feedback");
    final static private Text OPTIONS_TEXT = Text.translatable("menu.options");
    final static private Text SHARE_TO_LAN_TEXT = Text.translatable("menu.shareToLan");
    final static private Text PLAYER_REPORTING_TEXT = Text.translatable("menu.playerReporting");
    final static private Text GAME_TEXT = Text.translatable("menu.game");
    final static private Text PAUSED_TEXT = Text.translatable("menu.paused");
    final static private Tooltip CUSTOM_OPTIONS_TOOLTIP = Tooltip.of(Text.translatable("menu.custom_options.tooltip"));
    final private boolean showMenu;
    @Nullable
    private ButtonWidget exitButton;

    public GameMenuScreen(boolean showMenu) {
        super(showMenu ? GAME_TEXT : PAUSED_TEXT);
        this.showMenu = showMenu;
    }

    public boolean shouldShowMenu() {
        return this.showMenu;
    }

    @Override
    protected void init() {
        if (this.showMenu) {
            this.initWidgets();
        }
        this.addDrawableChild(new TextWidget(0, this.showMenu ? 40 : 10, this.width, this.textRenderer.fontHeight, this.title, this.textRenderer));
    }

    private void initWidgets() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().net_minecraft_client_gui_widget_Positioner_margin(4, 4, 4, 0);
        GridWidget.Adder adder = gridWidget.createAdder(2);
        adder.add(ButtonWidget.builder(RETURN_TO_GAME_TEXT, button -> {
            this.client.setScreen(null);
            this.client.mouse.lockCursor();
        }).width(204).build(), 2, gridWidget.copyPositioner().net_minecraft_client_gui_widget_Positioner_marginTop(50));
        adder.add(this.createButton(ADVANCEMENTS_TEXT, () -> new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler(), this)));
        adder.add(this.createButton(STATS_TEXT, () -> new StatsScreen(this, this.client.player.getStatHandler())));
        Optional<? extends RegistryEntry<Dialog>> optional = this.getCustomOptionsDialog();
        if (optional.isEmpty()) {
            GameMenuScreen.addFeedbackAndBugsButtons(this, adder);
        } else {
            this.addFeedbackAndCustomOptionsButtons(this.client, optional.get(), adder);
        }
        adder.add(this.createButton(OPTIONS_TEXT, () -> new OptionsScreen(this, this.client.options)));
        if (this.client.isIntegratedServerRunning() && !this.client.getServer().isRemote()) {
            adder.add(this.createButton(SHARE_TO_LAN_TEXT, () -> new OpenToLanScreen(this)));
        } else {
            adder.add(this.createButton(PLAYER_REPORTING_TEXT, () -> new SocialInteractionsScreen(this)));
        }
        this.exitButton = adder.add(ButtonWidget.builder(ScreenTexts.returnToMenuOrDisconnect(this.client.isInSingleplayer()), button -> {
            button.active = false;
            this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, () -> GameMenuScreen.disconnect(this.client, ClientWorld.QUITTING_MULTIPLAYER_TEXT), true);
        }).width(204).build(), 2);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height, 0.5f, 0.25f);
        gridWidget.forEachChild(this::addDrawableChild);
    }

    private Optional<? extends RegistryEntry<Dialog>> getCustomOptionsDialog() {
        RegistryEntryList registryEntryList;
        RegistryWrapper.Impl registry = this.client.player.networkHandler.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.DIALOG);
        Optional optional = registry.getOptional(DialogTags.PAUSE_SCREEN_ADDITIONS);
        if (optional.isPresent() && (registryEntryList = (RegistryEntryList)optional.get()).size() > 0) {
            if (registryEntryList.size() == 1) {
                return Optional.of(registryEntryList.get(0));
            }
            return registry.getOptional(Dialogs.CUSTOM_OPTIONS);
        }
        ServerLinks serverLinks = this.client.player.networkHandler.getServerLinks();
        if (!serverLinks.isEmpty()) {
            return registry.getOptional(Dialogs.SERVER_LINKS);
        }
        return Optional.empty();
    }

    static void addFeedbackAndBugsButtons(Screen parentScreen, GridWidget.Adder gridAdder) {
        gridAdder.add(GameMenuScreen.createUrlButton(parentScreen, SEND_FEEDBACK_TEXT, SharedConstants.getGameVersion().stable() ? Urls.JAVA_FEEDBACK : Urls.SNAPSHOT_FEEDBACK));
        gridAdder.add(GameMenuScreen.createUrlButton((Screen)parentScreen, (Text)GameMenuScreen.REPORT_BUGS_TEXT, (URI)Urls.SNAPSHOT_BUGS)).active = !SharedConstants.getGameVersion().dataVersion().isNotMainSeries();
    }

    private void addFeedbackAndCustomOptionsButtons(MinecraftClient client, RegistryEntry<Dialog> dialog, GridWidget.Adder gridAdder) {
        gridAdder.add(this.createButton(FEEDBACK_TEXT, () -> new FeedbackScreen(this)));
        gridAdder.add(ButtonWidget.builder(dialog.value().common().getExternalTitle(), button -> minecraftClient.player.networkHandler.showDialog(dialog, this)).width(98).tooltip(CUSTOM_OPTIONS_TOOLTIP).build());
    }

    public static void disconnect(MinecraftClient client, Text disconnectReason) {
        boolean bl = client.isInSingleplayer();
        ServerInfo serverInfo = client.getCurrentServerEntry();
        if (client.world != null) {
            client.world.disconnect(disconnectReason);
        }
        if (bl) {
            client.disconnectWithSavingScreen();
        } else {
            client.disconnectWithProgressScreen();
        }
        TitleScreen titleScreen = new TitleScreen();
        if (bl) {
            client.setScreen(titleScreen);
        } else if (serverInfo != null && serverInfo.isRealm()) {
            client.setScreen(new RealmsMainScreen(titleScreen));
        } else {
            client.setScreen(new MultiplayerScreen(titleScreen));
        }
    }

    @Override
    public void tick() {
        if (this.shouldShowNowPlayingToast()) {
            NowPlayingToast.tick();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.shouldShowNowPlayingToast()) {
            NowPlayingToast.draw(context, this.textRenderer);
        }
        if (this.showMenu && this.client != null && this.client.getAbuseReportContext().hasDraft() && this.exitButton != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_ICON_TEXTURE, this.exitButton.getX() + this.exitButton.getWidth() - 17, this.exitButton.getY() + 3, 15, 15);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.showMenu) {
            super.renderBackground(context, mouseX, mouseY, deltaTicks);
        }
    }

    public boolean shouldShowNowPlayingToast() {
        GameOptions gameOptions = this.client.options;
        return gameOptions.getShowNowPlayingToast().getValue() != false && gameOptions.getSoundVolume(SoundCategory.MUSIC) > 0.0f && this.showMenu;
    }

    private ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier) {
        return ButtonWidget.builder(text, button -> this.client.setScreen((Screen)screenSupplier.get())).width(98).build();
    }

    private static ButtonWidget createUrlButton(Screen parent, Text text, URI uri) {
        return ButtonWidget.builder(text, ConfirmLinkScreen.opening(parent, uri)).width(98).build();
    }

    @Environment(value=EnvType.CLIENT)
    static class FeedbackScreen
    extends Screen {
        final static private Text TITLE = Text.translatable("menu.feedback.title");
        final public Screen parent;
        final private ThreePartsLayoutWidget layoutWidget = new ThreePartsLayoutWidget(this);

        protected FeedbackScreen(Screen parent) {
            super(TITLE);
            this.parent = parent;
        }

        @Override
        protected void init() {
            this.layoutWidget.addHeader(TITLE, this.textRenderer);
            GridWidget gridWidget = this.layoutWidget.addBody(new GridWidget());
            gridWidget.getMainPositioner().net_minecraft_client_gui_widget_Positioner_margin(4, 4, 4, 0);
            GridWidget.Adder adder = gridWidget.createAdder(2);
            GameMenuScreen.addFeedbackAndBugsButtons(this, adder);
            this.layoutWidget.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(200).build());
            this.layoutWidget.forEachChild(this::addDrawableChild);
            this.refreshWidgetPositions();
        }

        @Override
        protected void refreshWidgetPositions() {
            this.layoutWidget.refreshPositions();
        }

        @Override
        public void close() {
            this.client.setScreen(this.parent);
        }
    }
}

