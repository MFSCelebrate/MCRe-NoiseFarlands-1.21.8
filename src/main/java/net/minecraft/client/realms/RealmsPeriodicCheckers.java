/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsNewsUpdater;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.util.Backoff;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RealmsPeriodicCheckers {
    final public PeriodicRunnerFactory runnerFactory = new PeriodicRunnerFactory(Util.getIoWorkerExecutor(), TimeUnit.MILLISECONDS, Util.nanoTimeSupplier);
    final private List<PeriodicRunnerFactory.PeriodicRunner<?>> checkers;
    final public PeriodicRunnerFactory.PeriodicRunner<List<RealmsNotification>> notifications;
    final public PeriodicRunnerFactory.PeriodicRunner<AvailableServers> serverList;
    final public PeriodicRunnerFactory.PeriodicRunner<Integer> pendingInvitesCount;
    final public PeriodicRunnerFactory.PeriodicRunner<Boolean> trialAvailability;
    final public PeriodicRunnerFactory.PeriodicRunner<RealmsNews> news;
    final public PeriodicRunnerFactory.PeriodicRunner<RealmsServerPlayerList> onlinePlayers;
    final public RealmsNewsUpdater newsUpdater = new RealmsNewsUpdater(new RealmsPersistence());

    public RealmsPeriodicCheckers(RealmsClient client) {
        this.serverList = this.runnerFactory.create("server list", () -> {
            RealmsServerList realmsServerList = client.listWorlds();
            if (RealmsMainScreen.isSnapshotRealmsEligible()) {
                return new AvailableServers(realmsServerList.servers, client.getPrereleaseEligibleServers());
            }
            return new AvailableServers(realmsServerList.servers, List.of());
        }, Duration.ofSeconds(60L), Backoff.ONE_CYCLE);
        this.pendingInvitesCount = this.runnerFactory.create("pending invite count", client::pendingInvitesCount, Duration.ofSeconds(10L), Backoff.exponential(360));
        this.trialAvailability = this.runnerFactory.create("trial availablity", client::trialAvailable, Duration.ofSeconds(60L), Backoff.exponential(60));
        this.news = this.runnerFactory.create("unread news", client::getNews, Duration.ofMinutes(5L), Backoff.ONE_CYCLE);
        this.notifications = this.runnerFactory.create("notifications", client::listNotifications, Duration.ofMinutes(5L), Backoff.ONE_CYCLE);
        this.onlinePlayers = this.runnerFactory.create("online players", client::getLiveStats, Duration.ofSeconds(10L), Backoff.ONE_CYCLE);
        this.checkers = List.of(this.notifications, this.serverList, this.pendingInvitesCount, this.trialAvailability, this.news, this.onlinePlayers);
    }

    public List<PeriodicRunnerFactory.PeriodicRunner<?>> getCheckers() {
        return this.checkers;
    }

    @Environment(value=EnvType.CLIENT)
    public record AvailableServers(List<RealmsServer> serverList, List<RealmsServer> availableSnapshotServers) {
    }
}

