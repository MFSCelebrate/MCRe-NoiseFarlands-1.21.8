/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.exceptions.ForcedUsernameChangeException
 *  com.mojang.authlib.exceptions.InsufficientPrivilegesException
 *  com.mojang.authlib.exceptions.InvalidCredentialsException
 *  com.mojang.authlib.exceptions.UserBannedException
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.ForcedUsernameChangeException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientLoginNetworkHandler
implements ClientLoginPacketListener {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private MinecraftClient client;
    @Nullable
    final private ServerInfo serverInfo;
    @Nullable
    final private Screen parentScreen;
    final private Consumer<Text> statusConsumer;
    final private ClientConnection connection;
    final private boolean newWorld;
    @Nullable
    final private Duration worldLoadTime;
    @Nullable
    private String minigameName;
    final private Map<Identifier, byte[]> serverCookies;
    final private boolean hasCookies;
    final private AtomicReference<State> state = new AtomicReference<State>(State.CONNECTING);

    public ClientLoginNetworkHandler(ClientConnection connection, MinecraftClient client, @Nullable ServerInfo serverInfo, @Nullable Screen parentScreen, boolean newWorld, @Nullable Duration worldLoadTime, Consumer<Text> statusConsumer, @Nullable CookieStorage cookieStorage) {
        this.connection = connection;
        this.client = client;
        this.serverInfo = serverInfo;
        this.parentScreen = parentScreen;
        this.statusConsumer = statusConsumer;
        this.newWorld = newWorld;
        this.worldLoadTime = worldLoadTime;
        this.serverCookies = cookieStorage != null ? new HashMap<Identifier, byte[]>(cookieStorage.cookies()) : new HashMap();
        this.hasCookies = cookieStorage != null;
    }

    private void switchTo(State state) {
        State state2 = this.state.updateAndGet(currentState -> {
            if (!state.prevStates.contains(currentState)) {
                throw new IllegalStateException("Tried to switch to " + String.valueOf((Object)state) + " from " + String.valueOf(currentState) + ", but expected one of " + String.valueOf(state.prevStates));
            }
            return state;
        });
        this.statusConsumer.accept(state2.name);
    }

    @Override
    public void onHello(LoginHelloS2CPacket packet) {
        LoginKeyC2SPacket loginKeyC2SPacket;
        Cipher cipher2;
        Cipher cipher;
        String string;
        this.switchTo(State.AUTHORIZING);
        try {
            SecretKey secretKey = NetworkEncryptionUtils.generateSecretKey();
            PublicKey publicKey = packet.getPublicKey();
            string = new BigInteger(NetworkEncryptionUtils.computeServerId(packet.getServerId(), publicKey, secretKey)).toString(16);
            cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
            cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
            byte[] bs = packet.getNonce();
            loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, bs);
        }
        catch (Exception exception) {
            throw new IllegalStateException("Protocol error", exception);
        }
        if (packet.needsAuthentication()) {
            Util.getIoWorkerExecutor().execute(() -> {
                Text text = this.joinServerSession(string);
                if (text != null) {
                    if (this.serverInfo != null && this.serverInfo.isLocal()) {
                        LOGGER.warn(text.getString());
                    } else {
                        this.connection.disconnect(text);
                        return;
                    }
                }
                this.setupEncryption(loginKeyC2SPacket, cipher, cipher2);
            });
        } else {
            this.setupEncryption(loginKeyC2SPacket, cipher, cipher2);
        }
    }

    private void setupEncryption(LoginKeyC2SPacket keyPacket, Cipher decryptionCipher, Cipher encryptionCipher) {
        this.switchTo(State.ENCRYPTING);
        this.connection.send(keyPacket, PacketCallbacks.always(() -> this.connection.setupEncryption(decryptionCipher, encryptionCipher)));
    }

    @Nullable
    private Text joinServerSession(String serverId) {
        try {
            this.getSessionService().joinServer(this.client.getSession().getUuidOrNull(), this.client.getSession().getAccessToken(), serverId);
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException) {
            return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.serversUnavailable"));
        }
        catch (InvalidCredentialsException invalidCredentialsException) {
            return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.invalidSession"));
        }
        catch (InsufficientPrivilegesException insufficientPrivilegesException) {
            return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.insufficientPrivileges"));
        }
        catch (ForcedUsernameChangeException | UserBannedException authenticationException) {
            return Text.translatable("disconnect.loginFailedInfo", Text.translatable("disconnect.loginFailedInfo.userBanned"));
        }
        catch (AuthenticationException authenticationException) {
            return Text.translatable("disconnect.loginFailedInfo", authenticationException.getMessage());
        }
        return null;
    }

    private MinecraftSessionService getSessionService() {
        return this.client.getSessionService();
    }

    @Override
    public void onSuccess(LoginSuccessS2CPacket packet) {
        this.switchTo(State.JOINING);
        GameProfile gameProfile = packet.profile();
        this.connection.transitionInbound(ConfigurationStates.S2C, new ClientConfigurationNetworkHandler(this.client, this.connection, new ClientConnectionState(gameProfile, this.client.getTelemetryManager().createWorldSession(this.newWorld, this.worldLoadTime, this.minigameName), ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(), FeatureFlags.DEFAULT_ENABLED_FEATURES, null, this.serverInfo, this.parentScreen, this.serverCookies, null, Map.of(), ServerLinks.EMPTY)));
        this.connection.send(EnterConfigurationC2SPacket.INSTANCE);
        this.connection.transitionOutbound(ConfigurationStates.C2S);
        this.connection.send(new CustomPayloadC2SPacket(new BrandCustomPayload(ClientBrandRetriever.getClientModName())));
        this.connection.send(new ClientOptionsC2SPacket(this.client.options.getSyncedOptions()));
    }

    @Override
    public void onDisconnected(DisconnectionInfo info) {
        Text text;
        Text text2 = text = this.hasCookies ? ScreenTexts.CONNECT_FAILED_TRANSFER : ScreenTexts.CONNECT_FAILED;
        if (this.serverInfo != null && this.serverInfo.isRealm()) {
            this.client.setScreen(new DisconnectedScreen(this.parentScreen, text, info.reason(), ScreenTexts.BACK));
        } else {
            this.client.setScreen(new DisconnectedScreen(this.parentScreen, text, info));
        }
    }

    @Override
    public boolean isConnectionOpen() {
        return this.connection.isOpen();
    }

    @Override
    public void onDisconnect(LoginDisconnectS2CPacket packet) {
        this.connection.disconnect(packet.reason());
    }

    @Override
    public void onCompression(LoginCompressionS2CPacket packet) {
        if (!this.connection.isLocal()) {
            this.connection.setCompressionThreshold(packet.getCompressionThreshold(), false);
        }
    }

    @Override
    public void onQueryRequest(LoginQueryRequestS2CPacket packet) {
        this.statusConsumer.accept(Text.translatable("connect.negotiating"));
        this.connection.send(new LoginQueryResponseC2SPacket(packet.queryId(), null));
    }

    public void setMinigameName(@Nullable String minigameName) {
        this.minigameName = minigameName;
    }

    @Override
    public void onCookieRequest(CookieRequestS2CPacket packet) {
        this.connection.send(new CookieResponseC2SPacket(packet.key(), this.serverCookies.get(packet.key())));
    }

    @Override
    public void addCustomCrashReportInfo(CrashReport report, CrashReportSection section) {
        section.add("Server type", () -> this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<unknown>");
        section.add("Login phase", () -> this.state.get().toString());
        section.add("Is Local", () -> String.valueOf(this.connection.isLocal()));
    }

    @Environment(value=EnvType.CLIENT)
    static final class State
    extends Enum<State> {
        final static public State CONNECTING = new State(Text.translatable("connect.connecting"), Set.of());
        final static public State AUTHORIZING = new State(Text.translatable("connect.authorizing"), Set.of(CONNECTING));
        final static public State ENCRYPTING = new State(Text.translatable("connect.encrypting"), Set.of(AUTHORIZING));
        final static public State JOINING = new State(Text.translatable("connect.joining"), Set.of(ENCRYPTING, CONNECTING));
        final Text name;
        final Set<State> prevStates;
        final static private State[] field_46199;

        public static State[] values() {
            return (State[])field_46199.clone();
        }

        public static State valueOf(String string) {
            return Enum.valueOf(State.class, string);
        }

        private State(Text name, Set<State> prevStates) {
            this.name = name;
            this.prevStates = prevStates;
        }

        private static State[] method_53874() {
            return new State[]{CONNECTING, AUTHORIZING, ENCRYPTING, JOINING};
        }

        static {
            field_46199 = State.method_53874();
        }
    }
}

