/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogScreens;
import net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ClearDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomReportDetailsS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerLinksS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class ClientCommonNetworkHandler
implements ClientCommonPacketListener {
    final static private Text LOST_CONNECTION_TEXT = Text.translatable("disconnect.lost");
    final static private Logger LOGGER = LogUtils.getLogger();
    final protected MinecraftClient client;
    final protected ClientConnection connection;
    @Nullable
    final protected ServerInfo serverInfo;
    @Nullable
    protected String brand;
    final protected WorldSession worldSession;
    @Nullable
    final protected Screen postDisconnectScreen;
    protected boolean transferring;
    final private List<QueuedPacket> queuedPackets = new ArrayList<QueuedPacket>();
    final protected Map<Identifier, byte[]> serverCookies;
    protected Map<String, String> customReportDetails;
    private ServerLinks serverLinks;

    protected ClientCommonNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        this.client = client;
        this.connection = connection;
        this.serverInfo = connectionState.serverInfo();
        this.brand = connectionState.serverBrand();
        this.worldSession = connectionState.worldSession();
        this.postDisconnectScreen = connectionState.postDisconnectScreen();
        this.serverCookies = connectionState.serverCookies();
        this.customReportDetails = connectionState.customReportDetails();
        this.serverLinks = connectionState.serverLinks();
    }

    public ServerLinks getServerLinks() {
        return this.serverLinks;
    }

    @Override
    public void onPacketException(Packet packet, Exception exception) {
        LOGGER.error("Failed to handle packet {}, disconnecting", (Object)packet, (Object)exception);
        Optional<Path> optional = this.savePacketErrorReport(packet, exception);
        Optional<URI> optional2 = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT).map(ServerLinks.Entry::link);
        this.connection.disconnect(new DisconnectionInfo(Text.translatable("disconnect.packetError"), optional, optional2));
    }

    @Override
    public DisconnectionInfo createDisconnectionInfo(Text reason, Throwable exception) {
        Optional<Path> optional = this.savePacketErrorReport(null, exception);
        Optional<URI> optional2 = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT).map(ServerLinks.Entry::link);
        return new DisconnectionInfo(reason, optional, optional2);
    }

    private Optional<Path> savePacketErrorReport(@Nullable Packet packet, Throwable exception) {
        CrashReport crashReport = CrashReport.create(exception, "Packet handling error");
        NetworkThreadUtils.fillCrashReport(crashReport, this, packet);
        Path path = this.client.runDirectory.toPath().resolve("debug");
        Path path2 = path.resolve("disconnect-" + Util.getFormattedCurrentTime() + "-client.txt");
        Optional<ServerLinks.Entry> optional = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT);
        List<String> list = optional.map(bugReportEntry -> List.of("Server bug reporting link: " + String.valueOf(bugReportEntry.link()))).orElse(List.of());
        if (crashReport.writeToFile(path2, ReportType.MINECRAFT_NETWORK_PROTOCOL_ERROR_REPORT, list)) {
            return Optional.of(path2);
        }
        return Optional.empty();
    }

    @Override
    public boolean accepts(Packet<?> packet) {
        if (ClientCommonPacketListener.super.accepts(packet)) {
            return true;
        }
        return this.transferring && (packet instanceof StoreCookieS2CPacket || packet instanceof ServerTransferS2CPacket);
    }

    @Override
    public void onKeepAlive(KeepAliveS2CPacket packet) {
        this.send(new KeepAliveC2SPacket(packet.getId()), () -> !RenderSystem.isFrozenAtPollEvents(), Duration.ofMinutes(1L));
    }

    @Override
    public void onPing(CommonPingS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.sendPacket(new CommonPongC2SPacket(packet.getParameter()));
    }

    @Override
    public void onCustomPayload(CustomPayloadS2CPacket packet) {
        CustomPayload customPayload = packet.payload();
        if (customPayload instanceof UnknownCustomPayload) {
            return;
        }
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (customPayload instanceof BrandCustomPayload) {
            BrandCustomPayload brandCustomPayload = (BrandCustomPayload)customPayload;
            this.brand = brandCustomPayload.brand();
            this.worldSession.setBrand(brandCustomPayload.brand());
        } else {
            this.onCustomPayload(customPayload);
        }
    }

    protected abstract void onCustomPayload(CustomPayload var1);

    @Override
    public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
        ServerInfo.ResourcePackPolicy resourcePackPolicy;
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        UUID uUID = packet.id();
        URL uRL = ClientCommonNetworkHandler.getParsedResourcePackUrl(packet.url());
        if (uRL == null) {
            this.connection.send(new ResourcePackStatusC2SPacket(uUID, ResourcePackStatusC2SPacket.Status.INVALID_URL));
            return;
        }
        String string = packet.hash();
        boolean bl = packet.required();
        ServerInfo.ResourcePackPolicy resourcePackPolicy2 = resourcePackPolicy = this.serverInfo != null ? this.serverInfo.getResourcePackPolicy() : ServerInfo.ResourcePackPolicy.PROMPT;
        if (resourcePackPolicy == ServerInfo.ResourcePackPolicy.PROMPT || bl && resourcePackPolicy == ServerInfo.ResourcePackPolicy.DISABLED) {
            this.client.setScreen(this.createConfirmServerResourcePackScreen(uUID, uRL, string, bl, packet.prompt().orElse(null)));
        } else {
            this.client.getServerResourcePackProvider().addResourcePack(uUID, uRL, string);
        }
    }

    @Override
    public void onResourcePackRemove(ResourcePackRemoveS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        packet.id().ifPresentOrElse(id -> this.client.getServerResourcePackProvider().remove((UUID)id), () -> this.client.getServerResourcePackProvider().removeAll());
    }

    static Text getPrompt(Text requirementPrompt, @Nullable Text customPrompt) {
        if (customPrompt == null) {
            return requirementPrompt;
        }
        return Text.translatable("multiplayer.texturePrompt.serverPrompt", requirementPrompt, customPrompt);
    }

    @Nullable
    private static URL getParsedResourcePackUrl(String url) {
        try {
            URL uRL = new URL(url);
            String string = uRL.getProtocol();
            if ("http".equals(string) || "https".equals(string)) {
                return uRL;
            }
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
        return null;
    }

    @Override
    public void onCookieRequest(CookieRequestS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.connection.send(new CookieResponseC2SPacket(packet.key(), this.serverCookies.get(packet.key())));
    }

    @Override
    public void onStoreCookie(StoreCookieS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.serverCookies.put(packet.key(), packet.payload());
    }

    @Override
    public void onCustomReportDetails(CustomReportDetailsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.customReportDetails = packet.details();
    }

    @Override
    public void onServerLinks(ServerLinksS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        List<ServerLinks.StringifiedEntry> list = packet.links();
        ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)list.size());
        for (ServerLinks.StringifiedEntry stringifiedEntry : list) {
            try {
                URI uRI = Util.validateUri(stringifiedEntry.link());
                builder.add((Object)new ServerLinks.Entry(stringifiedEntry.type(), uRI));
            }
            catch (Exception exception) {
                LOGGER.warn("Received invalid link for type {}:{}", new Object[]{stringifiedEntry.type(), stringifiedEntry.link(), exception});
            }
        }
        this.serverLinks = new ServerLinks((List<ServerLinks.Entry>)builder.build());
    }

    @Override
    public void onShowDialog(ShowDialogS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.showDialog(packet.dialog(), this.client.currentScreen);
    }

    protected abstract DialogNetworkAccess createDialogNetworkAccess();

    public void showDialog(RegistryEntry<Dialog> dialog, @Nullable Screen previousScreen) {
        this.showDialog(dialog, this.createDialogNetworkAccess(), previousScreen);
    }

    protected void showDialog(RegistryEntry<Dialog> dialog, DialogNetworkAccess networkAccess, @Nullable Screen previousScreen) {
        Screen screen3;
        if (previousScreen instanceof DialogScreen.WarningScreen) {
            Screen screen;
            DialogScreen<Dialog> dialogScreen;
            DialogScreen.WarningScreen warningScreen = (DialogScreen.WarningScreen)previousScreen;
            Screen screen2 = warningScreen.getDialogScreen();
            if (screen2 instanceof DialogScreen) {
                dialogScreen = (DialogScreen<Dialog>)screen2;
                screen = dialogScreen.getParentScreen();
            } else {
                screen = screen2;
            }
            Screen screen22 = screen;
            dialogScreen = DialogScreens.create(dialog.value(), screen22, networkAccess);
            if (dialogScreen != null) {
                warningScreen.setDialogScreen(dialogScreen);
            } else {
                LOGGER.warn("Failed to show dialog for data {}", dialog);
            }
            return;
        }
        if (previousScreen instanceof DialogScreen) {
            DialogScreen dialogScreen2 = (DialogScreen)previousScreen;
            screen3 = dialogScreen2.getParentScreen();
        } else if (previousScreen instanceof WaitingForResponseScreen) {
            WaitingForResponseScreen waitingForResponseScreen = (WaitingForResponseScreen)previousScreen;
            screen3 = waitingForResponseScreen.getParentScreen();
        } else {
            screen3 = previousScreen;
        }
        DialogScreen<Dialog> screen = DialogScreens.create(dialog.value(), screen3, networkAccess);
        if (screen != null) {
            this.client.setScreen(screen);
        } else {
            LOGGER.warn("Failed to show dialog for data {}", dialog);
        }
    }

    @Override
    public void onClearDialog(ClearDialogS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.clearDialog();
    }

    public void clearDialog() {
        Screen screen = this.client.currentScreen;
        if (screen instanceof DialogScreen.WarningScreen) {
            DialogScreen.WarningScreen warningScreen = (DialogScreen.WarningScreen)screen;
            Screen screen2 = warningScreen.getDialogScreen();
            if (screen2 instanceof DialogScreen) {
                DialogScreen dialogScreen = (DialogScreen)screen2;
                warningScreen.setDialogScreen(dialogScreen.getParentScreen());
            }
        } else {
            screen = this.client.currentScreen;
            if (screen instanceof DialogScreen) {
                DialogScreen dialogScreen2 = (DialogScreen)screen;
                this.client.setScreen(dialogScreen2.getParentScreen());
            }
        }
    }

    @Override
    public void onServerTransfer(ServerTransferS2CPacket packet) {
        this.transferring = true;
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (this.serverInfo == null) {
            throw new IllegalStateException("Cannot transfer to server from singleplayer");
        }
        this.connection.disconnect(Text.translatable("disconnect.transfer"));
        this.connection.tryDisableAutoRead();
        this.connection.handleDisconnection();
        ServerAddress serverAddress = new ServerAddress(packet.host(), packet.port());
        ConnectScreen.connect(Objects.requireNonNullElseGet(this.postDisconnectScreen, TitleScreen::new), this.client, serverAddress, this.serverInfo, false, new CookieStorage(this.serverCookies));
    }

    @Override
    public void onDisconnect(DisconnectS2CPacket packet) {
        this.connection.disconnect(packet.reason());
    }

    protected void sendQueuedPackets() {
        Iterator<QueuedPacket> iterator = this.queuedPackets.iterator();
        while (iterator.hasNext()) {
            QueuedPacket queuedPacket = iterator.next();
            if (queuedPacket.sendCondition().getAsBoolean()) {
                this.sendPacket(queuedPacket.packet);
                iterator.remove();
                continue;
            }
            if (queuedPacket.expirationTime() > Util.getMeasuringTimeMs()) continue;
            iterator.remove();
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.connection.send(packet);
    }

    @Override
    public void onDisconnected(DisconnectionInfo info) {
        this.worldSession.onUnload();
        this.client.disconnect(this.createDisconnectedScreen(info), this.transferring);
        LOGGER.warn("Client disconnected with reason: {}", (Object)info.reason().getString());
    }

    @Override
    public void addCustomCrashReportInfo(CrashReport report, CrashReportSection section) {
        section.add("Is Local", () -> String.valueOf(this.connection.isLocal()));
        section.add("Server type", () -> this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<none>");
        section.add("Server brand", () -> this.brand);
        if (!this.customReportDetails.isEmpty()) {
            CrashReportSection crashReportSection = report.addElement("Custom Server Details");
            this.customReportDetails.forEach(crashReportSection::add);
        }
    }

    protected Screen createDisconnectedScreen(DisconnectionInfo info) {
        Screen screen = Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> new MultiplayerScreen(new TitleScreen()));
        if (this.serverInfo != null && this.serverInfo.isRealm()) {
            return new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, info, ScreenTexts.BACK);
        }
        return new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, info);
    }

    @Nullable
    public String getBrand() {
        return this.brand;
    }

    private void send(Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, Duration expiry) {
        if (sendCondition.getAsBoolean()) {
            this.sendPacket(packet);
        } else {
            this.queuedPackets.add(new QueuedPacket(packet, sendCondition, Util.getMeasuringTimeMs() + expiry.toMillis()));
        }
    }

    private Screen createConfirmServerResourcePackScreen(UUID id, URL url, String hash, boolean required, @Nullable Text prompt) {
        Screen screen = this.client.currentScreen;
        if (screen instanceof ConfirmServerResourcePackScreen) {
            ConfirmServerResourcePackScreen confirmServerResourcePackScreen = (ConfirmServerResourcePackScreen)screen;
            return confirmServerResourcePackScreen.add(this.client, id, url, hash, required, prompt);
        }
        return new ConfirmServerResourcePackScreen(this.client, screen, List.of(new ConfirmServerResourcePackScreen.Pack(id, url, hash)), required, prompt);
    }

    @Environment(value=EnvType.CLIENT)
    static final class QueuedPacket
    extends Record {
        final Packet<? extends ServerPacketListener> packet;
        final private BooleanSupplier sendCondition;
        final private long expirationTime;

        QueuedPacket(Packet<? extends ServerPacketListener> packet, BooleanSupplier booleanSupplier, long l) {
            this.packet = packet;
            this.sendCondition = booleanSupplier;
            this.expirationTime = l;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{QueuedPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{QueuedPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{QueuedPacket.class, "packet;sendCondition;expirationTime", "packet", "sendCondition", "expirationTime"}, this, object);
        }

        public Packet<? extends ServerPacketListener> packet() {
            return this.packet;
        }

        public BooleanSupplier sendCondition() {
            return this.sendCondition;
        }

        public long expirationTime() {
            return this.expirationTime;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ConfirmServerResourcePackScreen
    extends ConfirmScreen {
        final private List<Pack> packs;
        @Nullable
        final private Screen parent;

        ConfirmServerResourcePackScreen(@Nullable MinecraftClient client, Screen parent, List<Pack> pack, @Nullable boolean required, Text prompt) {
            super(confirmed -> {
                client.setScreen(parent);
                ServerResourcePackLoader serverResourcePackLoader = client.getServerResourcePackProvider();
                if (confirmed) {
                    if (clientCommonNetworkHandler.serverInfo != null) {
                        clientCommonNetworkHandler.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
                    }
                    serverResourcePackLoader.acceptAll();
                } else {
                    serverResourcePackLoader.declineAll();
                    if (required) {
                        clientCommonNetworkHandler.connection.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                    } else if (clientCommonNetworkHandler.serverInfo != null) {
                        clientCommonNetworkHandler.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
                    }
                }
                for (Pack pack : pack) {
                    serverResourcePackLoader.addResourcePack(pack.id, pack.url, pack.hash);
                }
                if (clientCommonNetworkHandler.serverInfo != null) {
                    ServerList.updateServerListEntry(clientCommonNetworkHandler.serverInfo);
                }
            }, required ? Text.translatable("multiplayer.requiredTexturePrompt.line1") : Text.translatable("multiplayer.texturePrompt.line1"), ClientCommonNetworkHandler.getPrompt(required ? Text.translatable("multiplayer.requiredTexturePrompt.line2").formatted(Formatting.YELLOW, Formatting.BOLD) : Text.translatable("multiplayer.texturePrompt.line2"), prompt), required ? ScreenTexts.PROCEED : ScreenTexts.YES, required ? ScreenTexts.DISCONNECT : ScreenTexts.NO);
            this.packs = pack;
            this.parent = parent;
        }

        public ConfirmServerResourcePackScreen add(MinecraftClient client, UUID id, URL url, String hash, boolean required, @Nullable Text prompt) {
            ImmutableList list = ImmutableList.builderWithExpectedSize((int)(this.packs.size() + 1)).addAll(this.packs).add((Object)new Pack(id, url, hash)).build();
            return new ConfirmServerResourcePackScreen(client, this.parent, (List<Pack>)list, required, prompt);
        }

        @Environment(value=EnvType.CLIENT)
        static final class Pack
        extends Record {
            final UUID id;
            final URL url;
            final String hash;

            Pack(UUID uUID, URL uRL, String string) {
                this.id = uUID;
                this.url = uRL;
                this.hash = string;
            }

            @Override
            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{Pack.class, "id;url;hash", "id", "url", "hash"}, this);
            }

            @Override
            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Pack.class, "id;url;hash", "id", "url", "hash"}, this);
            }

            @Override
            public final boolean equals(Object object) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Pack.class, "id;url;hash", "id", "url", "hash"}, this, object);
            }

            public UUID id() {
                return this.id;
            }

            public URL url() {
                return this.url;
            }

            public String hash() {
                return this.hash;
            }
        }
    }
}

