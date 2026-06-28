/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.RealmsAvailability;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsNotificationsScreen
extends RealmsScreen {
    final static private Identifier UNSEEN_NOTIFICATION_ICON_TEXTURE = Identifier.ofVanilla("icon/unseen_notification");
    final static private Identifier NEWS_ICON_TEXTURE = Identifier.ofVanilla("icon/news");
    final static private Identifier INVITE_ICON_TEXTURE = Identifier.ofVanilla("icon/invite");
    final static private Identifier TRIAL_AVAILABLE_ICON_TEXTURE = Identifier.ofVanilla("icon/trial_available");
    final private CompletableFuture<Boolean> validClient = RealmsAvailability.check().thenApply(info -> info.type() == RealmsAvailability.Type.SUCCESS);
    @Nullable
    private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
    @Nullable
    private NotificationRunnersFactory currentRunnersFactory;
    private volatile int pendingInvitesCount;
    static private boolean trialAvailable;
    static private boolean hasUnreadNews;
    static private boolean hasUnseenNotification;
    final private NotificationRunnersFactory newsAndNotifications = new NotificationRunnersFactory(){

        @Override
        public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers checkers) {
            PeriodicRunnerFactory.RunnersManager runnersManager = checkers.runnerFactory.create();
            RealmsNotificationsScreen.this.addRunners(checkers, runnersManager);
            RealmsNotificationsScreen.this.addNotificationRunner(checkers, runnersManager);
            return runnersManager;
        }

        @Override
        public boolean isNews() {
            return true;
        }
    };
    final private NotificationRunnersFactory notificationsOnly = new NotificationRunnersFactory(){

        @Override
        public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers checkers) {
            PeriodicRunnerFactory.RunnersManager runnersManager = checkers.runnerFactory.create();
            RealmsNotificationsScreen.this.addNotificationRunner(checkers, runnersManager);
            return runnersManager;
        }

        @Override
        public boolean isNews() {
            return false;
        }
    };

    public RealmsNotificationsScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    public void init() {
        if (this.periodicRunnersManager != null) {
            this.periodicRunnersManager.forceRunListeners();
        }
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        this.client.getRealmsPeriodicCheckers().notifications.reset();
    }

    @Nullable
    private NotificationRunnersFactory getRunnersFactory() {
        boolean bl;
        boolean bl2 = bl = this.isTitleScreen() && this.validClient.getNow(false) != false;
        if (!bl) {
            return null;
        }
        return this.shouldShowRealmsNews() ? this.newsAndNotifications : this.notificationsOnly;
    }

    @Override
    public void tick() {
        NotificationRunnersFactory notificationRunnersFactory = this.getRunnersFactory();
        if (!Objects.equals(this.currentRunnersFactory, notificationRunnersFactory)) {
            this.currentRunnersFactory = notificationRunnersFactory;
            this.periodicRunnersManager = this.currentRunnersFactory != null ? this.currentRunnersFactory.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers()) : null;
        }
        if (this.periodicRunnersManager != null) {
            this.periodicRunnersManager.runAll();
        }
    }

    private boolean shouldShowRealmsNews() {
        return this.client.options.getRealmsNotifications().getValue();
    }

    private boolean isTitleScreen() {
        return this.client.currentScreen instanceof TitleScreen;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.validClient.getNow(false).booleanValue()) {
            this.drawIcons(context);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
    }

    private void drawIcons(DrawContext context) {
        int i = this.pendingInvitesCount;
        int j = 24;
        int k = this.height / 4 + 48;
        int l = this.width / 2 + 100;
        int m = k + 48 + 2;
        int n = l - 3;
        if (hasUnseenNotification) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, UNSEEN_NOTIFICATION_ICON_TEXTURE, n - 12, m + 3, 10, 10);
            n -= 16;
        }
        if (this.currentRunnersFactory != null && this.currentRunnersFactory.isNews()) {
            if (hasUnreadNews) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, NEWS_ICON_TEXTURE, n - 14, m + 1, 14, 14);
                n -= 16;
            }
            if (i != 0) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, INVITE_ICON_TEXTURE, n - 14, m + 1, 14, 14);
                n -= 16;
            }
            if (trialAvailable) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TRIAL_AVAILABLE_ICON_TEXTURE, n - 10, m + 4, 8, 8);
            }
        }
    }

    void addRunners(RealmsPeriodicCheckers checkers, PeriodicRunnerFactory.RunnersManager manager) {
        manager.add(checkers.pendingInvitesCount, pendingInvitesCount -> {
            this.pendingInvitesCount = pendingInvitesCount;
        });
        manager.add(checkers.trialAvailability, trialAvailable -> {
            RealmsNotificationsScreen.trialAvailable = trialAvailable;
        });
        manager.add(checkers.news, news -> {
            realmsPeriodicCheckers.newsUpdater.updateNews((RealmsNews)news);
            hasUnreadNews = realmsPeriodicCheckers.newsUpdater.hasUnreadNews();
        });
    }

    void addNotificationRunner(RealmsPeriodicCheckers checkers, PeriodicRunnerFactory.RunnersManager manager) {
        manager.add(checkers.notifications, notifications -> {
            hasUnseenNotification = false;
            for (RealmsNotification realmsNotification : notifications) {
                if (realmsNotification.isSeen()) continue;
                hasUnseenNotification = true;
                break;
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    static interface NotificationRunnersFactory {
        public PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers var1);

        public boolean isNews();
    }
}

