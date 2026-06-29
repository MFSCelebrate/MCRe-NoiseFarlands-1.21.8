/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.ProfilesTooltipComponent;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.RealmsAvailability;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RegionPingResult;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.BuyRealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsCreateRealmScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsMainScreen
extends RealmsScreen {
    final static Identifier INFO_ICON_TEXTURE = Identifier.ofVanilla("icon/info");
    final static Identifier NEW_REALM_ICON_TEXTURE = Identifier.ofVanilla("icon/new_realm");
    final static Identifier EXPIRED_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/expired");
    final static Identifier EXPIRES_SOON_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/expires_soon");
    final static Identifier OPEN_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/open");
    final static Identifier CLOSED_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/closed");
    final static private Identifier INVITE_ICON_TEXTURE = Identifier.ofVanilla("icon/invite");
    final static private Identifier NEWS_ICON_TEXTURE = Identifier.ofVanilla("icon/news");
    final static public Identifier HARDCORE_ICON_TEXTURE = Identifier.ofVanilla("hud/heart/hardcore_full");
    final static Logger LOGGER = LogUtils.getLogger();
    final static private Identifier NO_REALMS_TEXTURE = Identifier.ofVanilla("textures/gui/realms/no_realms.png");
    final static private Text MENU_TEXT = Text.translatable("menu.online");
    final static private Text LOADING_TEXT = Text.translatable("mco.selectServer.loading");
    final static Text UNINITIALIZED_TEXT = Text.translatable("mco.selectServer.uninitialized");
    final static Text EXPIRED_LIST_TEXT = Text.translatable("mco.selectServer.expiredList");
    final static private Text EXPIRED_RENEW_TEXT = Text.translatable("mco.selectServer.expiredRenew");
    final static Text EXPIRED_TRIAL_TEXT = Text.translatable("mco.selectServer.expiredTrial");
    final static private Text PLAY_TEXT = Text.translatable("mco.selectServer.play");
    final static private Text LEAVE_TEXT = Text.translatable("mco.selectServer.leave");
    final static private Text CONFIGURE_TEXT = Text.translatable("mco.selectServer.configure");
    final static Text EXPIRED_TEXT = Text.translatable("mco.selectServer.expired");
    final static Text EXPIRES_SOON_TEXT = Text.translatable("mco.selectServer.expires.soon");
    final static Text EXPIRES_IN_A_DAY_TEXT = Text.translatable("mco.selectServer.expires.day");
    final static Text OPEN_TEXT = Text.translatable("mco.selectServer.open");
    final static Text CLOSED_TEXT = Text.translatable("mco.selectServer.closed");
    final static Text UNINITIALIZED_BUTTON_NARRATION = Text.translatable("gui.narrate.button", UNINITIALIZED_TEXT);
    final static private Text NO_REALMS_TEXT = Text.translatable("mco.selectServer.noRealms");
    final static private Text NO_PENDING_TOOLTIP = Text.translatable("mco.invites.nopending");
    final static private Text PENDING_TOOLTIP = Text.translatable("mco.invites.pending");
    final static private Text INCOMPATIBLE_POPUP_TITLE = Text.translatable("mco.compatibility.incompatible.popup.title");
    final static private Text INCOMPATIBLE_RELEASE_TYPE_MESSAGE = Text.translatable("mco.compatibility.incompatible.releaseType.popup.message");
    final static private int field_42862 = 100;
    final static private int field_45209 = 3;
    final static private int field_45210 = 4;
    final static private int field_45211 = 308;
    final static private int field_44513 = 5;
    final static private int field_44514 = 44;
    final static private int field_45212 = 11;
    final static private int field_46670 = 40;
    final static private int field_46671 = 20;
    final static private int field_46215 = 216;
    final static private int field_46216 = 36;
    final static private boolean GAME_ON_SNAPSHOT;
    static private boolean showingSnapshotRealms;
    final private CompletableFuture<RealmsAvailability.Info> availabilityInfo = RealmsAvailability.check();
    @Nullable
    private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
    final private Set<UUID> seenNotifications = new HashSet<UUID>();
    static private boolean regionsPinged;
    final private RateLimiter rateLimiter;
    final private Screen parent;
    private ButtonWidget playButton;
    private ButtonWidget backButton;
    private ButtonWidget renewButton;
    private ButtonWidget configureButton;
    private ButtonWidget leaveButton;
    RealmSelectionList realmSelectionList;
    RealmsServerFilterer serverFilterer;
    List<RealmsServer> availableSnapshotServers = List.of();
    RealmsServerPlayerList onlinePlayers = new RealmsServerPlayerList();
    private volatile boolean trialAvailable;
    @Nullable
    private volatile String newsLink;
    long lastPlayButtonClickTime;
    final List<RealmsNotification> notifications = new ArrayList<RealmsNotification>();
    private ButtonWidget purchaseButton;
    private NotificationButtonWidget inviteButton;
    private NotificationButtonWidget newsButton;
    private LoadStatus loadStatus;
    @Nullable
    private ThreePartsLayoutWidget layout;

    public RealmsMainScreen(Screen parent) {
        super(MENU_TEXT);
        this.parent = parent;
        this.rateLimiter = RateLimiter.create(0.01666666753590107);
    }

    @Override
    public void init() {
        this.serverFilterer = new RealmsServerFilterer(this.client);
        this.realmSelectionList = new RealmSelectionList();
        MutableText text = Text.translatable("mco.invites.title");
        this.inviteButton = new NotificationButtonWidget(text, INVITE_ICON_TEXTURE, button -> this.client.setScreen(new RealmsPendingInvitesScreen(this, text)));
        MutableText text2 = Text.translatable("mco.news");
        this.newsButton = new NotificationButtonWidget(text2, NEWS_ICON_TEXTURE, button -> {
            String string = this.newsLink;
            if (string == null) {
                return;
            }
            ConfirmLinkScreen.open((Screen)this, string);
            if (this.newsButton.getNotificationCount() != 0) {
                RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
                realmsPersistenceData.hasUnreadNews = false;
                RealmsPersistence.writeFile(realmsPersistenceData);
                this.newsButton.setNotificationCount(0);
            }
        });
        this.newsButton.setTooltip(Tooltip.of(text2));
        this.playButton = ButtonWidget.builder(PLAY_TEXT, button -> RealmsMainScreen.play(this.getSelectedServer(), this)).width(100).build();
        this.configureButton = ButtonWidget.builder(CONFIGURE_TEXT, button -> this.configureClicked(this.getSelectedServer())).width(100).build();
        this.renewButton = ButtonWidget.builder(EXPIRED_RENEW_TEXT, button -> this.onRenew(this.getSelectedServer())).width(100).build();
        this.leaveButton = ButtonWidget.builder(LEAVE_TEXT, button -> this.leaveClicked(this.getSelectedServer())).width(100).build();
        this.purchaseButton = ButtonWidget.builder(Text.translatable("mco.selectServer.purchase"), button -> this.showBuyRealmsScreen()).size(100, 20).build();
        this.backButton = ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(100).build();
        if (RealmsClient.ENVIRONMENT == RealmsClient.Environment.STAGE) {
            this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("Snapshot"), Text.literal("Release")).build(5, 5, 100, 20, Text.literal("Realm"), (button, snapshot) -> {
                showingSnapshotRealms = snapshot;
                this.availableSnapshotServers = List.of();
                this.resetPeriodicCheckers();
            }));
        }
        this.onLoadStatusChange(LoadStatus.LOADING);
        this.refreshButtons();
        this.availabilityInfo.thenAcceptAsync(availabilityInfo -> {
            Screen screen = availabilityInfo.createScreen(this.parent);
            if (screen == null) {
                this.periodicRunnersManager = this.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers());
            } else {
                this.client.setScreen(screen);
            }
        }, this.executor);
    }

    public static boolean isSnapshotRealmsEligible() {
        return GAME_ON_SNAPSHOT && showingSnapshotRealms;
    }

    @Override
    protected void refreshWidgetPositions() {
        if (this.layout != null) {
            this.realmSelectionList.position(this.width, this.layout);
            this.layout.refreshPositions();
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private void updateLoadStatus() {
        if (this.serverFilterer.isEmpty() && this.availableSnapshotServers.isEmpty() && this.notifications.isEmpty()) {
            this.onLoadStatusChange(LoadStatus.NO_REALMS);
        } else {
            this.onLoadStatusChange(LoadStatus.LIST);
        }
    }

    private void onLoadStatusChange(LoadStatus loadStatus) {
        if (this.loadStatus == loadStatus) {
            return;
        }
        if (this.layout != null) {
            this.layout.forEachChild(child -> this.remove((Element)child));
        }
        this.layout = this.makeLayoutFor(loadStatus);
        this.loadStatus = loadStatus;
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    private ThreePartsLayoutWidget makeLayoutFor(LoadStatus loadStatus) {
        ThreePartsLayoutWidget threePartsLayoutWidget = new ThreePartsLayoutWidget(this);
        threePartsLayoutWidget.setHeaderHeight(44);
        threePartsLayoutWidget.addHeader(this.makeHeader());
        LayoutWidget layoutWidget = this.makeInnerLayout(loadStatus);
        layoutWidget.refreshPositions();
        threePartsLayoutWidget.setFooterHeight(layoutWidget.getHeight() + 22);
        threePartsLayoutWidget.addFooter(layoutWidget);
        switch (loadStatus.ordinal()) {
            case 0: {
                threePartsLayoutWidget.addBody(new LoadingWidget(this.textRenderer, LOADING_TEXT));
                break;
            }
            case 1: {
                threePartsLayoutWidget.addBody(this.makeNoRealmsLayout());
                break;
            }
            case 2: {
                threePartsLayoutWidget.addBody(this.realmSelectionList);
            }
        }
        return threePartsLayoutWidget;
    }

    private LayoutWidget makeHeader() {
        int i = 90;
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
        directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
        directionalLayoutWidget.add(this.inviteButton);
        directionalLayoutWidget.add(this.newsButton);
        DirectionalLayoutWidget directionalLayoutWidget2 = DirectionalLayoutWidget.horizontal();
        directionalLayoutWidget2.getMainPositioner().alignVerticalCenter();
        directionalLayoutWidget2.add(EmptyWidget.ofWidth(90));
        directionalLayoutWidget2.add(RealmsMainScreen.createRealmsLogoIconWidget(), Positioner::alignHorizontalCenter);
        directionalLayoutWidget2.add(new SimplePositioningWidget(90, 44)).add(directionalLayoutWidget, Positioner::alignRight);
        return directionalLayoutWidget2;
    }

    private LayoutWidget makeInnerLayout(LoadStatus loadStatus) {
        GridWidget gridWidget = new GridWidget().setSpacing(4);
        GridWidget.Adder adder = gridWidget.createAdder(3);
        if (loadStatus == LoadStatus.LIST) {
            adder.add(this.playButton);
            adder.add(this.configureButton);
            adder.add(this.renewButton);
            adder.add(this.leaveButton);
        }
        adder.add(this.purchaseButton);
        adder.add(this.backButton);
        return gridWidget;
    }

    private DirectionalLayoutWidget makeNoRealmsLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add(IconWidget.create(130, 64, NO_REALMS_TEXTURE, 130, 64));
        NarratedMultilineTextWidget narratedMultilineTextWidget = new NarratedMultilineTextWidget(308, NO_REALMS_TEXT, this.textRenderer, false, true, 4);
        directionalLayoutWidget.add(narratedMultilineTextWidget);
        return directionalLayoutWidget;
    }

    void refreshButtons() {
        RealmsServer realmsServer = this.getSelectedServer();
        boolean bl = realmsServer != null;
        this.purchaseButton.active = this.loadStatus != LoadStatus.LOADING;
        boolean bl2 = this.playButton.active = bl && realmsServer.shouldAllowPlay();
        if (!this.playButton.active && bl && realmsServer.state == RealmsServer.State.CLOSED) {
            this.playButton.setTooltip(Tooltip.of(RealmsServer.REALM_CLOSED_TEXT));
        }
        this.renewButton.active = bl && this.shouldRenewButtonBeActive(realmsServer);
        this.leaveButton.active = bl && this.shouldLeaveButtonBeActive(realmsServer);
        this.configureButton.active = bl && this.shouldConfigureButtonBeActive(realmsServer);
    }

    private boolean shouldRenewButtonBeActive(RealmsServer server) {
        return server.expired && RealmsMainScreen.isSelfOwnedServer(server);
    }

    private boolean shouldConfigureButtonBeActive(RealmsServer server) {
        return RealmsMainScreen.isSelfOwnedServer(server) && server.state != RealmsServer.State.UNINITIALIZED;
    }

    private boolean shouldLeaveButtonBeActive(RealmsServer server) {
        return !RealmsMainScreen.isSelfOwnedServer(server);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.periodicRunnersManager != null) {
            this.periodicRunnersManager.runAll();
        }
    }

    public static void resetPendingInvitesCount() {
        MinecraftClient.getInstance().getRealmsPeriodicCheckers().pendingInvitesCount.reset();
    }

    public static void resetServerList() {
        MinecraftClient.getInstance().getRealmsPeriodicCheckers().serverList.reset();
    }

    private void resetPeriodicCheckers() {
        for (PeriodicRunnerFactory.PeriodicRunner<?> periodicRunner : this.client.getRealmsPeriodicCheckers().getCheckers()) {
            periodicRunner.reset();
        }
    }

    private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
        PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
        runnersManager.add(periodicCheckers.serverList, availableServers -> {
            this.serverFilterer.filterAndSort(availableServers.serverList());
            this.availableSnapshotServers = availableServers.availableSnapshotServers();
            this.refresh();
            boolean bl = false;
            for (RealmsServer realmsServer : this.serverFilterer) {
                if (!this.isOwnedNotExpired(realmsServer)) continue;
                bl = true;
            }
            if (!regionsPinged && bl) {
                regionsPinged = true;
                this.pingRegions();
            }
        });
        RealmsMainScreen.request(RealmsClient::listNotifications, notifications -> {
            this.notifications.clear();
            this.notifications.addAll((Collection<RealmsNotification>)notifications);
            for (RealmsNotification realmsNotification : notifications) {
                RealmsNotification.InfoPopup infoPopup;
                PopupScreen popupScreen;
                if (!(realmsNotification instanceof RealmsNotification.InfoPopup) || (popupScreen = (infoPopup = (RealmsNotification.InfoPopup)realmsNotification).createScreen(this, this::dismissNotification)) == null) continue;
                this.client.setScreen(popupScreen);
                this.markAsSeen(List.of(realmsNotification));
                break;
            }
            if (!this.notifications.isEmpty() && this.loadStatus != LoadStatus.LOADING) {
                this.refresh();
            }
        });
        runnersManager.add(periodicCheckers.pendingInvitesCount, pendingInvitesCount -> {
            this.inviteButton.setNotificationCount((int)pendingInvitesCount);
            this.inviteButton.setTooltip(pendingInvitesCount == 0 ? Tooltip.of(NO_PENDING_TOOLTIP) : Tooltip.of(PENDING_TOOLTIP));
            if (pendingInvitesCount > 0 && this.rateLimiter.tryAcquire(1)) {
                this.client.getNarratorManager().narrateSystemImmediately(Text.translatable("mco.configure.world.invite.narration", pendingInvitesCount));
            }
        });
        runnersManager.add(periodicCheckers.trialAvailability, trialAvailable -> {
            this.trialAvailable = trialAvailable;
        });
        runnersManager.add(periodicCheckers.onlinePlayers, onlinePlayers -> {
            this.onlinePlayers = onlinePlayers;
        });
        runnersManager.add(periodicCheckers.news, news -> {
            realmsPeriodicCheckers.newsUpdater.updateNews((RealmsNews)news);
            this.newsLink = realmsPeriodicCheckers.newsUpdater.getNewsLink();
            this.newsButton.setNotificationCount(realmsPeriodicCheckers.newsUpdater.hasUnreadNews() ? Integer.MAX_VALUE : 0);
        });
        return runnersManager;
    }

    void markAsSeen(Collection<RealmsNotification> notifications) {
        ArrayList<UUID> list = new ArrayList<UUID>(notifications.size());
        for (RealmsNotification realmsNotification : notifications) {
            if (realmsNotification.isSeen() || this.seenNotifications.contains(realmsNotification.getUuid())) continue;
            list.add(realmsNotification.getUuid());
        }
        if (!list.isEmpty()) {
            RealmsMainScreen.request(client -> {
                client.markNotificationsAsSeen(list);
                return null;
            }, result -> this.seenNotifications.addAll(list));
        }
    }

    private static <T> void request(Request<T> request, Consumer<T> resultConsumer) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
            try {
                return request.request(RealmsClient.createRealmsClient(minecraftClient));
            }
            catch (RealmsServiceException realmsServiceException) {
                throw new RuntimeException(realmsServiceException);
            }
        }).thenAcceptAsync(resultConsumer, (Executor)minecraftClient)).exceptionally(throwable -> {
            LOGGER.error("Failed to execute call to Realms Service", throwable);
            return null;
        });
    }

    private void refresh() {
        this.realmSelectionList.refresh(this, this.getSelectedServer());
        this.updateLoadStatus();
        this.refreshButtons();
    }

    private void pingRegions() {
        new Thread(() -> {
            List<RegionPingResult> list = Ping.pingAllRegions();
            RealmsClient realmsClient = RealmsClient.create();
            PingResult pingResult = new PingResult();
            pingResult.pingResults = list;
            pingResult.worldIds = this.getOwnedNonExpiredWorldIds();
            try {
                realmsClient.sendPingResults(pingResult);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not send ping result to Realms: ", throwable);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        ArrayList list = Lists.newArrayList();
        for (RealmsServer realmsServer : this.serverFilterer) {
            if (!this.isOwnedNotExpired(realmsServer)) continue;
            list.add(realmsServer.id);
        }
        return list;
    }

    private void onRenew(@Nullable RealmsServer realmsServer) {
        if (realmsServer != null) {
            String string = Urls.getExtendJavaRealmsUrl(realmsServer.remoteSubscriptionId, this.client.getSession().getUuidOrNull(), realmsServer.expiredTrial);
            this.client.keyboard.setClipboard(string);
            Util.getOperatingSystem().open(string);
        }
    }

    private void configureClicked(@Nullable RealmsServer serverData) {
        if (serverData != null && this.client.uuidEquals(serverData.ownerUUID)) {
            this.client.setScreen(new RealmsConfigureWorldScreen(this, serverData.id));
        }
    }

    private void leaveClicked(@Nullable RealmsServer selectedServer) {
        if (selectedServer != null && !this.client.uuidEquals(selectedServer.ownerUUID)) {
            MutableText text = Text.translatable("mco.configure.world.leave.question.line1");
            this.client.setScreen(RealmsPopups.createInfoPopup(this, text, popup -> this.leaveServer(selectedServer)));
        }
    }

    @Nullable
    private RealmsServer getSelectedServer() {
        Object e = this.realmSelectionList.getSelectedOrNull();
        if (e instanceof RealmSelectionListEntry) {
            RealmSelectionListEntry realmSelectionListEntry = (RealmSelectionListEntry)e;
            return realmSelectionListEntry.getRealmsServer();
        }
        return null;
    }

    private void leaveServer(final RealmsServer server) {
        new Thread("Realms-leave-server"){

            @Override
            public void run() {
                try {
                    RealmsClient realmsClient = RealmsClient.create();
                    realmsClient.uninviteMyselfFrom(server.id);
                    RealmsMainScreen.this.client.execute(RealmsMainScreen::resetServerList);
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't configure world", (Throwable)realmsServiceException);
                    RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)RealmsMainScreen.this)));
                }
            }
        }.start();
        this.client.setScreen(this);
    }

    void dismissNotification(UUID notification) {
        RealmsMainScreen.request(client -> {
            client.dismissNotifications(List.of(notification));
            return null;
        }, void_ -> {
            this.notifications.removeIf(notificationId -> notificationId.isDismissable() && notification.equals(notificationId.getUuid()));
            this.refresh();
        });
    }

    public void removeSelection() {
        this.realmSelectionList.setSelected((Entry)null);
        RealmsMainScreen.resetServerList();
    }

    @Override
    public Text getNarratedTitle() {
        return switch (this.loadStatus.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> ScreenTexts.joinSentences(super.getNarratedTitle(), LOADING_TEXT);
            case 1 -> ScreenTexts.joinSentences(super.getNarratedTitle(), NO_REALMS_TEXT);
            case 2 -> super.getNarratedTitle();
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (RealmsMainScreen.isSnapshotRealmsEligible()) {
            context.drawTextWithShadow(this.textRenderer, "Minecraft " + SharedConstants.getGameVersion().name(), 2, this.height - 10, Colors.WHITE);
        }
        if (this.trialAvailable && this.purchaseButton.active) {
            BuyRealmsScreen.drawTrialAvailableTexture(context, this.purchaseButton);
        }
        switch (RealmsClient.ENVIRONMENT) {
            case STAGE: {
                this.drawEnvironmentText(context, "STAGE!", -256);
                break;
            }
            case LOCAL: {
                this.drawEnvironmentText(context, "LOCAL!", -8388737);
            }
        }
    }

    private void showBuyRealmsScreen() {
        this.client.setScreen(new BuyRealmsScreen(this, this.trialAvailable));
    }

    public static void play(@Nullable RealmsServer serverData, Screen parent) {
        RealmsMainScreen.play(serverData, parent, false);
    }

    public static void play(@Nullable RealmsServer server, Screen parent, boolean needsPreparation) {
        if (server != null) {
            if (!RealmsMainScreen.isSnapshotRealmsEligible() || needsPreparation || server.isMinigame()) {
                MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new RealmsPrepareConnectionTask(parent, server)));
                return;
            }
            switch (server.compatibility) {
                case COMPATIBLE: {
                    MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new RealmsPrepareConnectionTask(parent, server)));
                    break;
                }
                case UNVERIFIABLE: {
                    RealmsMainScreen.showCompatibilityScreen(server, parent, Text.translatable("mco.compatibility.unverifiable.title").withColor(Colors.LIGHT_YELLOW), Text.translatable("mco.compatibility.unverifiable.message"), ScreenTexts.CONTINUE);
                    break;
                }
                case NEEDS_DOWNGRADE: {
                    RealmsMainScreen.showCompatibilityScreen(server, parent, Text.translatable("selectWorld.backupQuestion.downgrade").withColor(Colors.LIGHT_RED), Text.translatable("mco.compatibility.downgrade.description", Text.literal(server.activeVersion).withColor(Colors.LIGHT_YELLOW), Text.literal(SharedConstants.getGameVersion().name()).withColor(Colors.LIGHT_YELLOW)), Text.translatable("mco.compatibility.downgrade"));
                    break;
                }
                case NEEDS_UPGRADE: {
                    RealmsMainScreen.showNeedsUpgradeScreen(server, parent);
                    break;
                }
                case INCOMPATIBLE: {
                    MinecraftClient.getInstance().setScreen(new PopupScreen.Builder(parent, INCOMPATIBLE_POPUP_TITLE).message(Text.translatable("mco.compatibility.incompatible.series.popup.message", Text.literal(server.activeVersion).withColor(Colors.LIGHT_YELLOW), Text.literal(SharedConstants.getGameVersion().name()).withColor(Colors.LIGHT_YELLOW))).button(ScreenTexts.BACK, PopupScreen::close).build());
                    break;
                }
                case RELEASE_TYPE_INCOMPATIBLE: {
                    MinecraftClient.getInstance().setScreen(new PopupScreen.Builder(parent, INCOMPATIBLE_POPUP_TITLE).message(INCOMPATIBLE_RELEASE_TYPE_MESSAGE).button(ScreenTexts.BACK, PopupScreen::close).build());
                }
            }
        }
    }

    private static void showCompatibilityScreen(RealmsServer server, Screen parent, Text title, Text description, Text confirmText) {
        MinecraftClient.getInstance().setScreen(new PopupScreen.Builder(parent, title).message(description).button(confirmText, popup -> {
            MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new RealmsPrepareConnectionTask(parent, server)));
            RealmsMainScreen.resetServerList();
        }).button(ScreenTexts.CANCEL, PopupScreen::close).build());
    }

    private static void showNeedsUpgradeScreen(RealmsServer serverData, Screen parent) {
        MutableText text = Text.translatable("mco.compatibility.upgrade.title").withColor(Colors.LIGHT_YELLOW);
        MutableText text2 = Text.translatable("mco.compatibility.upgrade");
        MutableText text3 = Text.literal(serverData.activeVersion).withColor(Colors.LIGHT_YELLOW);
        MutableText text4 = Text.literal(SharedConstants.getGameVersion().name()).withColor(Colors.LIGHT_YELLOW);
        MutableText text5 = RealmsMainScreen.isSelfOwnedServer(serverData) ? Text.translatable("mco.compatibility.upgrade.description", text3, text4) : Text.translatable("mco.compatibility.upgrade.friend.description", text3, text4);
        RealmsMainScreen.showCompatibilityScreen(serverData, parent, text, text5, text2);
    }

    public static Text getVersionText(String version, boolean compatible) {
        return RealmsMainScreen.getVersionText(version, compatible ? -8355712 : -2142128);
    }

    public static Text getVersionText(String version, int color) {
        if (StringUtils.isBlank((CharSequence)version)) {
            return ScreenTexts.EMPTY;
        }
        return Text.literal(version).withColor(color);
    }

    public static Text getGameModeText(int id, boolean hardcore) {
        if (hardcore) {
            return Text.translatable("gameMode.hardcore").withColor(Colors.RED);
        }
        return GameMode.byIndex(id).getTranslatableName();
    }

    static boolean isSelfOwnedServer(RealmsServer server) {
        return MinecraftClient.getInstance().uuidEquals(server.ownerUUID);
    }

    private boolean isOwnedNotExpired(RealmsServer serverData) {
        return RealmsMainScreen.isSelfOwnedServer(serverData) && !serverData.expired;
    }

    private void drawEnvironmentText(DrawContext context, String text, int color) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)(this.width / 2 - 25), 20.0f);
        context.getMatrices().rotate(-0.34906584f);
        context.getMatrices().scale(1.5f, 1.5f);
        context.drawTextWithShadow(this.textRenderer, text, 0, 0, color);
        context.getMatrices().popMatrix();
    }

    static {
        showingSnapshotRealms = GAME_ON_SNAPSHOT = !SharedConstants.getGameVersion().stable();
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionList
    extends AlwaysSelectedEntryListWidget<Entry> {
        public RealmSelectionList() {
            super(MinecraftClient.getInstance(), RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            RealmsMainScreen.this.refreshButtons();
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        void refresh(RealmsMainScreen mainScreen, @Nullable RealmsServer selectedServer) {
            this.clearEntries();
            for (RealmsNotification realmsNotification : RealmsMainScreen.this.notifications) {
                if (!(realmsNotification instanceof RealmsNotification.VisitUrl)) continue;
                RealmsNotification.VisitUrl visitUrl = (RealmsNotification.VisitUrl)realmsNotification;
                this.addVisitEntries(visitUrl, mainScreen);
                RealmsMainScreen.this.markAsSeen(List.of(realmsNotification));
                break;
            }
            this.addServerEntries(selectedServer);
        }

        private void addServerEntries(@Nullable RealmsServer selectedServer) {
            for (RealmsServer realmsServer : RealmsMainScreen.this.availableSnapshotServers) {
                this.addEntry(new SnapshotEntry(realmsServer));
            }
            for (RealmsServer realmsServer : RealmsMainScreen.this.serverFilterer) {
                Entry entry;
                if (RealmsMainScreen.isSnapshotRealmsEligible() && !realmsServer.isPrerelease()) {
                    if (realmsServer.state == RealmsServer.State.UNINITIALIZED) continue;
                    entry = new ParentRealmSelectionListEntry(RealmsMainScreen.this, realmsServer);
                } else {
                    entry = new RealmSelectionListEntry(realmsServer);
                }
                this.addEntry(entry);
                if (selectedServer == null || selectedServer.id != realmsServer.id) continue;
                this.setSelected(entry);
            }
        }

        private void addVisitEntries(RealmsNotification.VisitUrl url, RealmsMainScreen mainScreen) {
            Text text = url.getDefaultMessage();
            int i = RealmsMainScreen.this.textRenderer.getWrappedLinesHeight(text, 216);
            int j = MathHelper.ceilDiv(8, 36) - 1;
            this.addEntry(new VisitUrlNotification(text, j + 2, url));
            for (int k = 0; k < j; ++k) {
                this.addEntry(new EmptyEntry(RealmsMainScreen.this));
            }
            this.addEntry(new VisitButtonEntry(url.createButton(mainScreen)));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class NotificationButtonWidget
    extends TextIconButtonWidget.IconOnly {
        final static private Identifier[] TEXTURES = new Identifier[]{Identifier.ofVanilla("notification/1"), Identifier.ofVanilla("notification/2"), Identifier.ofVanilla("notification/3"), Identifier.ofVanilla("notification/4"), Identifier.ofVanilla("notification/5"), Identifier.ofVanilla("notification/more")};
        final static private int field_45228 = Integer.MAX_VALUE;
        final static private int SIZE = 20;
        final static private int TEXTURE_SIZE = 14;
        private int notificationCount;

        public NotificationButtonWidget(Text message, Identifier texture, ButtonWidget.PressAction onPress) {
            super(20, 20, message, 14, 14, texture, onPress, null);
        }

        int getNotificationCount() {
            return this.notificationCount;
        }

        public void setNotificationCount(int notificationCount) {
            this.notificationCount = notificationCount;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            super.renderWidget(context, mouseX, mouseY, deltaTicks);
            if (this.active && this.notificationCount != 0) {
                this.render(context);
            }
        }

        private void render(DrawContext context) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURES[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class LoadStatus
    extends Enum<LoadStatus> {
        final static public LoadStatus LOADING = new LoadStatus();
        final static public LoadStatus NO_REALMS = new LoadStatus();
        final static public LoadStatus LIST = new LoadStatus();
        final static private LoadStatus[] field_45226;

        public static LoadStatus[] values() {
            return (LoadStatus[])field_45226.clone();
        }

        public static LoadStatus valueOf(String string) {
            return Enum.valueOf(LoadStatus.class, string);
        }

        private static LoadStatus[] method_52650() {
            return new LoadStatus[]{LOADING, NO_REALMS, LIST};
        }

        static {
            field_45226 = LoadStatus.method_52650();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static interface Request<T> {
        public T request(RealmsClient var1) throws RealmsServiceException;
    }

    @Environment(value=EnvType.CLIENT)
    class RealmSelectionListEntry
    extends Entry {
        final static private Text ONLINE_PLAYERS_TEXT = Text.translatable("mco.onlinePlayers");
        final static private int field_52120 = 9;
        final static private int field_32054 = 36;
        final private RealmsServer server;
        final private TooltipState tooltip;

        public RealmSelectionListEntry(RealmsServer server) {
            this.tooltip = new TooltipState();
            this.server = server;
            boolean bl = RealmsMainScreen.isSelfOwnedServer(server);
            if (RealmsMainScreen.isSnapshotRealmsEligible() && bl && server.isPrerelease()) {
                this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.paired", server.parentWorldName)));
            } else if (!bl && server.needsDowngrade()) {
                this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.friendsRealm.downgrade", server.activeVersion)));
            }
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            if (this.server.state == RealmsServer.State.UNINITIALIZED) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, NEW_REALM_ICON_TEXTURE, x - 5, y + entryHeight / 2 - 10, 40, 20);
                int i = y + entryHeight / 2 - ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight / 2;
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, UNINITIALIZED_TEXT, x + 40 - 2, i, RealmsScreen.GREEN);
                return;
            }
            RealmsUtil.drawPlayerHead(context, x, y, 32, this.server.ownerUUID);
            this.drawServerNameAndVersion(context, y, x, entryWidth, -1, this.server);
            this.drawDescription(context, y, x, entryWidth, this.server);
            this.drawOwnerOrExpiredText(context, y, x, this.server);
            this.renderStatusIcon(this.server, context, x + entryWidth, y, mouseX, mouseY);
            boolean bl = this.drawPlayers(context, y, x, entryWidth, entryHeight, mouseX, mouseY, tickProgress);
            if (!bl) {
                this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
            }
        }

        private boolean drawPlayers(DrawContext context, int top, int left, int width, int height, int mouseX, int mouseY, float tickProgress) {
            List<ProfileResult> list = RealmsMainScreen.this.onlinePlayers.get(this.server.id);
            if (!list.isEmpty()) {
                int i = left + width - 21;
                int j = top + height - 9 - 2;
                int k = i;
                for (int l = 0; l < list.size(); ++l) {
                    PlayerSkinDrawer.draw(context, MinecraftClient.getInstance().getSkinProvider().getSkinTextures(list.get(l).profile()), k -= 9 + (l == 0 ? 0 : 3), j, 9);
                }
                if (mouseX >= k && mouseX <= i && mouseY >= j && mouseY <= j + 9) {
                    context.drawTooltip(RealmsMainScreen.this.textRenderer, List.of(ONLINE_PLAYERS_TEXT), Optional.of(new ProfilesTooltipComponent.ProfilesData(list)), mouseX, mouseY);
                    return true;
                }
            }
            return false;
        }

        private void play() {
            RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            RealmsMainScreen.play(this.server, RealmsMainScreen.this);
        }

        private void createRealm() {
            RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            RealmsCreateRealmScreen realmsCreateRealmScreen = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server, this.server.isPrerelease());
            RealmsMainScreen.this.client.setScreen(realmsCreateRealmScreen);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.server.state == RealmsServer.State.UNINITIALIZED) {
                this.createRealm();
            } else if (this.server.shouldAllowPlay()) {
                if (Util.getMeasuringTimeMs() - RealmsMainScreen.this.lastPlayButtonClickTime < 250L && this.isFocused()) {
                    this.play();
                }
                RealmsMainScreen.this.lastPlayButtonClickTime = Util.getMeasuringTimeMs();
            }
            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (KeyCodes.isToggle(keyCode)) {
                if (this.server.state == RealmsServer.State.UNINITIALIZED) {
                    this.createRealm();
                    return true;
                }
                if (this.server.shouldAllowPlay()) {
                    this.play();
                    return true;
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public Text getNarration() {
            if (this.server.state == RealmsServer.State.UNINITIALIZED) {
                return UNINITIALIZED_BUTTON_NARRATION;
            }
            return Text.translatable("narrator.select", Objects.requireNonNullElse(this.server.name, "unknown server"));
        }

        public RealmsServer getRealmsServer() {
            return this.server;
        }
    }

    @Environment(value=EnvType.CLIENT)
    abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        final static protected int field_46680 = 10;
        final static private int field_46681 = 28;
        final static protected int field_52117 = 7;
        final static protected int field_52118 = 2;

        Entry() {
        }

        protected void renderStatusIcon(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
            int i = x - 10 - 7;
            int j = y + 2;
            if (server.expired) {
                this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, EXPIRED_STATUS_TEXTURE, () -> EXPIRED_TEXT);
            } else if (server.state == RealmsServer.State.CLOSED) {
                this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, CLOSED_STATUS_TEXTURE, () -> CLOSED_TEXT);
            } else if (RealmsMainScreen.isSelfOwnedServer(server) && server.daysLeft < 7) {
                this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, EXPIRES_SOON_STATUS_TEXTURE, () -> {
                    if (realmsServer.daysLeft <= 0) {
                        return EXPIRES_SOON_TEXT;
                    }
                    if (realmsServer.daysLeft == 1) {
                        return EXPIRES_IN_A_DAY_TEXT;
                    }
                    return Text.translatable("mco.selectServer.expires.days", realmsServer.daysLeft);
                });
            } else if (server.state == RealmsServer.State.OPEN) {
                this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, OPEN_STATUS_TEXTURE, () -> OPEN_TEXT);
            }
        }

        private void drawTextureWithTooltip(DrawContext context, int x, int y, int mouseX, int mouseY, Identifier texture, Supplier<Text> tooltip) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 10, 28);
            if (RealmsMainScreen.this.realmSelectionList.isMouseOver(mouseX, mouseY) && mouseX >= x && mouseX <= x + 10 && mouseY >= y && mouseY <= y + 28) {
                context.drawTooltip(tooltip.get(), mouseX, mouseY);
            }
        }

        protected void drawServerNameAndVersion(DrawContext context, int y, int x, int width, int color, RealmsServer server) {
            int i = this.getNameX(x);
            int j = this.getNameY(y);
            Text text = RealmsMainScreen.getVersionText(server.activeVersion, server.isCompatible());
            int k = this.getVersionRight(x, width, text);
            this.drawTrimmedText(context, server.getName(), i, j, k, color);
            if (text != ScreenTexts.EMPTY && !server.isMinigame()) {
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, k, j, Colors.GRAY);
            }
        }

        protected void drawDescription(DrawContext context, int y, int x, int width, RealmsServer server) {
            int i = this.getNameX(x);
            int j = this.getNameY(y);
            int k = this.getDescriptionY(j);
            String string = server.getMinigameName();
            boolean bl = server.isMinigame();
            if (bl && string != null) {
                MutableText text = Text.literal(string).formatted(Formatting.GRAY);
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, Text.translatable("mco.selectServer.minigameName", text).withColor(Colors.LIGHT_YELLOW), i, k, Colors.WHITE);
            } else {
                int l = this.drawGameMode(server, context, x, width, j);
                this.drawTrimmedText(context, server.getDescription(), i, this.getDescriptionY(j), l, -8355712);
            }
        }

        protected void drawOwnerOrExpiredText(DrawContext context, int y, int x, RealmsServer server) {
            int i = this.getNameX(x);
            int j = this.getNameY(y);
            int k = this.getStatusY(j);
            if (!RealmsMainScreen.isSelfOwnedServer(server)) {
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, server.owner, i, this.getStatusY(j), Colors.GRAY);
            } else if (server.expired) {
                Text text = server.expiredTrial ? EXPIRED_TRIAL_TEXT : EXPIRED_LIST_TEXT;
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, i, k, Colors.LIGHT_RED);
            }
        }

        protected void drawTrimmedText(DrawContext context, @Nullable String string, int left, int y, int right, int color) {
            if (string == null) {
                return;
            }
            int i = right - left;
            if (RealmsMainScreen.this.textRenderer.getWidth(string) > i) {
                String string2 = RealmsMainScreen.this.textRenderer.trimToWidth(string, i - RealmsMainScreen.this.textRenderer.getWidth("... "));
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, string2 + "...", left, y, color);
            } else {
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, string, left, y, color);
            }
        }

        protected int getVersionRight(int x, int width, Text version) {
            return x + width - RealmsMainScreen.this.textRenderer.getWidth(version) - 20;
        }

        protected int getGameModeRight(int x, int width, Text gameMode) {
            return x + width - RealmsMainScreen.this.textRenderer.getWidth(gameMode) - 20;
        }

        protected int drawGameMode(RealmsServer server, DrawContext context, int x, int entryWidth, int y) {
            boolean bl = server.hardcore;
            int i = server.gameMode;
            int j = x;
            if (GameMode.isValid(i)) {
                Text text = RealmsMainScreen.getGameModeText(i, bl);
                j = this.getGameModeRight(x, entryWidth, text);
                context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, j, this.getDescriptionY(y), Colors.GRAY);
            }
            if (bl) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HARDCORE_ICON_TEXTURE, j -= 10, this.getDescriptionY(y), 8, 8);
            }
            return j;
        }

        protected int getNameY(int y) {
            return y + 1;
        }

        protected int getTextHeight() {
            return 2 + ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight;
        }

        protected int getNameX(int x) {
            return x + 36 + 2;
        }

        protected int getDescriptionY(int y) {
            return y + this.getTextHeight();
        }

        protected int getStatusY(int y) {
            return y + this.getTextHeight() * 2;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CrossButton
    extends TexturedButtonWidget {
        final static private ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/cross_button"), Identifier.ofVanilla("widget/cross_button_highlighted"));

        protected CrossButton(ButtonWidget.PressAction onPress, Text tooltip) {
            super(0, 0, 14, 14, TEXTURES, onPress);
            this.setTooltip(Tooltip.of(tooltip));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ParentRealmSelectionListEntry
    extends Entry {
        final private RealmsServer server;
        final private TooltipState tooltip = new TooltipState();

        public ParentRealmSelectionListEntry(RealmsMainScreen realmsMainScreen, RealmsServer server) {
            this.server = server;
            if (!server.expired) {
                this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.parent.tooltip")));
            }
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            this.renderStatusIcon(this.server, context, x + entryWidth, y, mouseX, mouseY);
            RealmsUtil.drawPlayerHead(context, x, y, 32, this.server.ownerUUID);
            this.drawServerNameAndVersion(context, y, x, entryWidth, -8355712, this.server);
            this.drawDescription(context, y, x, entryWidth, this.server);
            this.drawOwnerOrExpiredText(context, y, x, this.server);
            this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
        }

        @Override
        public Text getNarration() {
            return Text.literal(Objects.requireNonNullElse(this.server.name, "unknown server"));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SnapshotEntry
    extends Entry {
        final static private Text START_TEXT = Text.translatable("mco.snapshot.start");
        final static private int field_46677 = 5;
        final private TooltipState tooltip = new TooltipState();
        final private RealmsServer server;

        public SnapshotEntry(RealmsServer server) {
            this.server = server;
            this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.tooltip")));
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, NEW_REALM_ICON_TEXTURE, x - 5, y + entryHeight / 2 - 10, 40, 20);
            int i = y + entryHeight / 2 - ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight / 2;
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, START_TEXT, x + 40 - 2, i - 5, RealmsScreen.GREEN);
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, Text.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.server.name, "unknown server")), x + 40 - 2, i + 5, Colors.GRAY);
            this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.showPopup();
            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (KeyCodes.isToggle(keyCode)) {
                this.showPopup();
                return false;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void showPopup() {
            RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            RealmsMainScreen.this.client.setScreen(new PopupScreen.Builder(RealmsMainScreen.this, Text.translatable("mco.snapshot.createSnapshotPopup.title")).message(Text.translatable("mco.snapshot.createSnapshotPopup.text")).button(Text.translatable("mco.selectServer.create"), screen -> RealmsMainScreen.this.client.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server, true))).button(ScreenTexts.CANCEL, PopupScreen::close).build());
        }

        @Override
        public Text getNarration() {
            return Text.translatable("gui.narrate.button", ScreenTexts.joinSentences(START_TEXT, Text.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.server.name, "unknown server"))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class VisitButtonEntry
    extends Entry {
        final private ButtonWidget button;

        public VisitButtonEntry(ButtonWidget button) {
            this.button = button;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.button.mouseClicked(mouseX, mouseY, button);
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.button.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            this.button.setPosition(RealmsMainScreen.this.width / 2 - 75, y + 4);
            this.button.render(context, mouseX, mouseY, tickProgress);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            this.button.setFocused(focused);
        }

        @Override
        public Text getNarration() {
            return this.button.getMessage();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class EmptyEntry
    extends Entry {
        EmptyEntry(RealmsMainScreen realmsMainScreen) {
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        }

        @Override
        public Text getNarration() {
            return Text.empty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class VisitUrlNotification
    extends Entry {
        final static private int field_43002 = 40;
        final static private int field_43004 = -12303292;
        final private Text message;
        final private int lines;
        final private List<ClickableWidget> gridChildren = new ArrayList<ClickableWidget>();
        @Nullable
        final private CrossButton dismissButton;
        final private MultilineTextWidget textWidget;
        final private GridWidget grid;
        final private SimplePositioningWidget textGrid;
        private int width = -1;

        public VisitUrlNotification(Text message, int lines, RealmsNotification notification) {
            this.message = message;
            this.lines = lines;
            this.grid = new GridWidget();
            int i = 7;
            this.grid.add(IconWidget.create(20, 20, INFO_ICON_TEXTURE), 0, 0, this.grid.copyPositioner().net_minecraft_client_gui_widget_Positioner_margin(7, 7, 0, 0));
            this.grid.add(EmptyWidget.ofWidth(40), 0, 0);
            this.textGrid = this.grid.add(new SimplePositioningWidget(0, ((RealmsMainScreen)RealmsMainScreen.this).textRenderer.fontHeight * 3 * (lines - 1)), 0, 1, this.grid.copyPositioner().net_minecraft_client_gui_widget_Positioner_marginTop(7));
            this.textWidget = this.textGrid.add(new MultilineTextWidget(message, RealmsMainScreen.this.textRenderer).setCentered(true), this.textGrid.copyPositioner().alignHorizontalCenter().alignTop());
            this.grid.add(EmptyWidget.ofWidth(40), 0, 2);
            this.dismissButton = notification.isDismissable() ? this.grid.add(new CrossButton(button -> RealmsMainScreen.this.dismissNotification(notification.getUuid()), Text.translatable("mco.notification.dismiss")), 0, 2, this.grid.copyPositioner().alignRight().net_minecraft_client_gui_widget_Positioner_margin(0, 7, 7, 0)) : null;
            this.grid.forEachChild(this.gridChildren::add);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (this.dismissButton != null && this.dismissButton.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void setWidth(int width) {
            if (this.width != width) {
                this.updateWidth(width);
                this.width = width;
            }
        }

        private void updateWidth(int width) {
            int i = width - 80;
            this.textGrid.setMinWidth(i);
            this.textWidget.setMaxWidth(i);
            this.grid.refreshPositions();
        }

        @Override
        public void drawBorder(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            super.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
            context.drawBorder(x - 2, y - 2, entryWidth, 36 * this.lines - 2, -12303292);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            this.grid.setPosition(x, y);
            this.setWidth(entryWidth - 4);
            this.gridChildren.forEach(child -> child.render(context, mouseX, mouseY, tickProgress));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.dismissButton != null) {
                this.dismissButton.mouseClicked(mouseX, mouseY, button);
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public Text getNarration() {
            return this.message;
        }
    }
}

