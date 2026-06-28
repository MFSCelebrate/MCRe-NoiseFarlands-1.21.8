/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.yggdrasil.ProfileResult
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.lang3.Validate
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerLoginNetworkHandler
implements ServerLoginPacketListener,
TickablePacketListener {
    final static private AtomicInteger NEXT_AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
    final static Logger LOGGER = LogUtils.getLogger();
    final static private int TIMEOUT_TICKS = 600;
    final private byte[] nonce;
    final MinecraftServer server;
    final ClientConnection connection;
    private volatile State state = State.HELLO;
    private int loginTicks;
    @Nullable
    String profileName;
    @Nullable
    private GameProfile profile;
    final private String serverId = "";
    final private boolean transferred;

    public ServerLoginNetworkHandler(MinecraftServer server, ClientConnection connection, boolean transferred) {
        this.server = server;
        this.connection = connection;
        this.nonce = Ints.toByteArray((int)Random.create().nextInt());
        this.transferred = transferred;
    }

    @Override
    public void tick() {
        if (this.state == State.VERIFYING) {
            this.tickVerify(Objects.requireNonNull(this.profile));
        }
        if (this.state == State.WAITING_FOR_DUPE_DISCONNECT && !this.hasPlayerWithId(Objects.requireNonNull(this.profile))) {
            this.sendSuccessPacket(this.profile);
        }
        if (this.loginTicks++ == 600) {
            this.disconnect(Text.translatable("multiplayer.disconnect.slow_login"));
        }
    }

    @Override
    public boolean isConnectionOpen() {
        return this.connection.isOpen();
    }

    public void disconnect(Text reason) {
        try {
            LOGGER.info("Disconnecting {}: {}", (Object)this.getConnectionInfo(), (Object)reason.getString());
            this.connection.send(new LoginDisconnectS2CPacket(reason));
            this.connection.disconnect(reason);
        }
        catch (Exception exception) {
            LOGGER.error("Error whilst disconnecting player", (Throwable)exception);
        }
    }

    private boolean hasPlayerWithId(GameProfile profile) {
        return this.server.net_minecraft_server_PlayerManager_getPlayerManager().getPlayer(profile.getId()) != null;
    }

    @Override
    public void onDisconnected(DisconnectionInfo info) {
        LOGGER.info("{} lost connection: {}", (Object)this.getConnectionInfo(), (Object)info.reason().getString());
    }

    public String getConnectionInfo() {
        String string = this.connection.getAddressAsString(this.server.shouldLogIps());
        if (this.profileName != null) {
            return this.profileName + " (" + string + ")";
        }
        return string;
    }

    @Override
    public void onHello(LoginHelloC2SPacket packet) {
        Validate.validState((this.state == State.HELLO ? 1 : 0) != 0, (String)"Unexpected hello packet", (Object[])new Object[0]);
        Validate.validState((boolean)StringHelper.isValidPlayerName(packet.name()), (String)"Invalid characters in username", (Object[])new Object[0]);
        this.profileName = packet.name();
        GameProfile gameProfile = this.server.getHostProfile();
        if (gameProfile != null && this.profileName.equalsIgnoreCase(gameProfile.getName())) {
            this.startVerify(gameProfile);
            return;
        }
        if (this.server.isOnlineMode() && !this.connection.isLocal()) {
            this.state = State.KEY;
            this.connection.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce, true));
        } else {
            this.startVerify(Uuids.getOfflinePlayerProfile(this.profileName));
        }
    }

    void startVerify(GameProfile profile) {
        this.profile = profile;
        this.state = State.VERIFYING;
    }

    private void tickVerify(GameProfile profile) {
        PlayerManager playerManager = this.server.net_minecraft_server_PlayerManager_getPlayerManager();
        Text text = playerManager.checkCanJoin(this.connection.getAddress(), profile);
        if (text != null) {
            this.disconnect(text);
        } else {
            boolean bl;
            if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
                this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), PacketCallbacks.always(() -> this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold(), true)));
            }
            if (bl = playerManager.disconnectDuplicateLogins(profile)) {
                this.state = State.WAITING_FOR_DUPE_DISCONNECT;
            } else {
                this.sendSuccessPacket(profile);
            }
        }
    }

    private void sendSuccessPacket(GameProfile profile) {
        this.state = State.PROTOCOL_SWITCHING;
        this.connection.send(new LoginSuccessS2CPacket(profile));
    }

    @Override
    public void onKey(LoginKeyC2SPacket packet) {
        String string;
        Validate.validState((this.state == State.KEY ? 1 : 0) != 0, (String)"Unexpected key packet", (Object[])new Object[0]);
        try {
            PrivateKey privateKey = this.server.getKeyPair().getPrivate();
            if (!packet.verifySignedNonce(this.nonce, privateKey)) {
                throw new IllegalStateException("Protocol error");
            }
            SecretKey secretKey = packet.decryptSecretKey(privateKey);
            Cipher cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
            Cipher cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
            string = new BigInteger(NetworkEncryptionUtils.computeServerId("", this.server.getKeyPair().getPublic(), secretKey)).toString(16);
            this.state = State.AUTHENTICATING;
            this.connection.setupEncryption(cipher, cipher2);
        }
        catch (NetworkEncryptionException networkEncryptionException) {
            throw new IllegalStateException("Protocol error", networkEncryptionException);
        }
        Thread thread = new Thread("User Authenticator #" + NEXT_AUTHENTICATOR_THREAD_ID.incrementAndGet()){

            @Override
            public void run() {
                String string2 = Objects.requireNonNull(ServerLoginNetworkHandler.this.profileName, "Player name not initialized");
                try {
                    ProfileResult profileResult = ServerLoginNetworkHandler.this.server.getSessionService().hasJoinedServer(string2, string, this.getClientAddress());
                    if (profileResult != null) {
                        GameProfile gameProfile = profileResult.profile();
                        LOGGER.info("UUID of player {} is {}", (Object)gameProfile.getName(), (Object)gameProfile.getId());
                        ServerLoginNetworkHandler.this.startVerify(gameProfile);
                    } else if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
                        LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginNetworkHandler.this.startVerify(Uuids.getOfflinePlayerProfile(string2));
                    } else {
                        ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.unverified_username"));
                        LOGGER.error("Username '{}' tried to join with an invalid session", (Object)string2);
                    }
                }
                catch (AuthenticationUnavailableException authenticationUnavailableException) {
                    if (ServerLoginNetworkHandler.this.server.isSingleplayer()) {
                        LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginNetworkHandler.this.startVerify(Uuids.getOfflinePlayerProfile(string2));
                    }
                    ServerLoginNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.authservers_down"));
                    LOGGER.error("Couldn't verify username because servers are unavailable");
                }
            }

            @Nullable
            private InetAddress getClientAddress() {
                SocketAddress socketAddress = ServerLoginNetworkHandler.this.connection.getAddress();
                return ServerLoginNetworkHandler.this.server.shouldPreventProxyConnections() && socketAddress instanceof InetSocketAddress ? ((InetSocketAddress)socketAddress).getAddress() : null;
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    @Override
    public void onQueryResponse(LoginQueryResponseC2SPacket packet) {
        this.disconnect(ServerCommonNetworkHandler.UNEXPECTED_QUERY_RESPONSE_TEXT);
    }

    @Override
    public void onEnterConfiguration(EnterConfigurationC2SPacket packet) {
        Validate.validState((this.state == State.PROTOCOL_SWITCHING ? 1 : 0) != 0, (String)"Unexpected login acknowledgement packet", (Object[])new Object[0]);
        this.connection.transitionOutbound(ConfigurationStates.S2C);
        ConnectedClientData connectedClientData = ConnectedClientData.createDefault(Objects.requireNonNull(this.profile), this.transferred);
        ServerConfigurationNetworkHandler serverConfigurationNetworkHandler = new ServerConfigurationNetworkHandler(this.server, this.connection, connectedClientData);
        this.connection.transitionInbound(ConfigurationStates.C2S, serverConfigurationNetworkHandler);
        serverConfigurationNetworkHandler.sendConfigurations();
        this.state = State.ACCEPTED;
    }

    @Override
    public void addCustomCrashReportInfo(CrashReport report, CrashReportSection section) {
        section.add("Login phase", () -> this.state.toString());
    }

    @Override
    public void onCookieResponse(CookieResponseC2SPacket packet) {
        this.disconnect(ServerCommonNetworkHandler.UNEXPECTED_QUERY_RESPONSE_TEXT);
    }

    static final class State
    extends Enum<State> {
        final static public State HELLO = new State();
        final static public State KEY = new State();
        final static public State AUTHENTICATING = new State();
        final static public State NEGOTIATING = new State();
        final static public State VERIFYING = new State();
        final static public State WAITING_FOR_DUPE_DISCONNECT = new State();
        final static public State PROTOCOL_SWITCHING = new State();
        final static public State ACCEPTED = new State();
        final static private State[] field_14174;

        public static State[] values() {
            return (State[])field_14174.clone();
        }

        public static State valueOf(String string) {
            return Enum.valueOf(State.class, string);
        }

        private static State[] method_36581() {
            return new State[]{HELLO, KEY, AUTHENTICATING, NEGOTIATING, VERIFYING, WAITING_FOR_DUPE_DISCONNECT, PROTOCOL_SWITCHING, ACCEPTED};
        }

        static {
            field_14174 = State.method_36581();
        }
    }
}

