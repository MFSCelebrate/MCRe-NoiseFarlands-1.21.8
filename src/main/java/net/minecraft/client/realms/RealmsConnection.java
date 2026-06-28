/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.mojang.logging.LogUtils;
import java.net.InetSocketAddress;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsConnection {
    final static Logger LOGGER = LogUtils.getLogger();
    final Screen onlineScreen;
    volatile boolean aborted;
    @Nullable
    ClientConnection connection;

    public RealmsConnection(Screen onlineScreen) {
        this.onlineScreen = onlineScreen;
    }

    public void connect(RealmsServer server, ServerAddress address) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.loadBlockList();
        minecraftClient.getNarratorManager().narrateSystemImmediately(Text.translatable("mco.connect.success"));
        final String string = address.getAddress();
        int i = address.getPort();
        new Thread("Realms-connect-task", i, minecraftClient, server){
            final int field_11114;
            final MinecraftClient field_22818;
            final RealmsServer field_26928;
            {
                this.field_11114 = 1;
                this.field_22818 = minecraftClient;
                this.field_26928 = realmsServer;
                super(string3);
            }

            @Override
            public void run() {
                InetSocketAddress inetSocketAddress = null;
                try {
                    inetSocketAddress = new InetSocketAddress(string, this.field_11114);
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    RealmsConnection.this.connection = ClientConnection.connect(inetSocketAddress, this.field_22818.options.shouldUseNativeTransport(), this.field_22818.getDebugHud().getPacketSizeLog());
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    ClientLoginNetworkHandler clientLoginNetworkHandler = new ClientLoginNetworkHandler(RealmsConnection.this.connection, this.field_22818, this.field_26928.createServerInfo(string), RealmsConnection.this.onlineScreen, false, null, status -> {}, null);
                    if (this.field_26928.isMinigame()) {
                        clientLoginNetworkHandler.setMinigameName(this.field_26928.minigameName);
                    }
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    RealmsConnection.this.connection.connect(string, this.field_11114, clientLoginNetworkHandler);
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    RealmsConnection.this.connection.send(new LoginHelloC2SPacket(this.field_22818.getSession().getUsername(), this.field_22818.getSession().getUuidOrNull()));
                    this.field_22818.ensureAbuseReportContext(ReporterEnvironment.ofRealm(this.field_26928));
                    this.field_22818.getQuickPlayLogger().setWorld(QuickPlayLogger.WorldType.REALMS, String.valueOf(this.field_26928.id), Objects.requireNonNullElse(this.field_26928.name, "unknown"));
                    this.field_22818.getServerResourcePackProvider().init(RealmsConnection.this.connection, ServerResourcePackManager.AcceptanceStatus.ALLOWED);
                }
                catch (Exception exception) {
                    this.field_22818.getServerResourcePackProvider().clear();
                    if (RealmsConnection.this.aborted) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to world", (Throwable)exception);
                    String string3 = exception.toString();
                    if (inetSocketAddress != null) {
                        String string2 = String.valueOf(inetSocketAddress) + ":" + this.field_11114;
                        string3 = string3.replaceAll(string2, "");
                    }
                    DisconnectedScreen disconnectedScreen = new DisconnectedScreen(RealmsConnection.this.onlineScreen, (Text)Text.translatable("mco.connect.failed"), Text.translatable("disconnect.genericReason", string3), ScreenTexts.BACK);
                    this.field_22818.execute(() -> this.field_22818.setScreen(disconnectedScreen));
                }
            }
        }.start();
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isOpen()) {
            this.connection.disconnect(Text.translatable("disconnect.genericReason"));
            this.connection.handleDisconnection();
        }
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }
}

