/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientConfigurationNetworkHandler
extends ClientCommonNetworkHandler
implements ClientConfigurationPacketListener,
TickablePacketListener {
    final static Logger LOGGER = LogUtils.getLogger();
    final private GameProfile profile;
    private FeatureSet enabledFeatures;
    final private DynamicRegistryManager.Immutable registryManager;
    final private ClientRegistries clientRegistries = new ClientRegistries();
    @Nullable
    private ClientDataPackManager dataPackManager;
    @Nullable
    protected ChatHud.ChatState chatState;

    public ClientConfigurationNetworkHandler(MinecraftClient minecraftClient, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
        super(minecraftClient, clientConnection, clientConnectionState);
        this.profile = clientConnectionState.localGameProfile();
        this.registryManager = clientConnectionState.receivedRegistries();
        this.enabledFeatures = clientConnectionState.enabledFeatures();
        this.chatState = clientConnectionState.chatState();
    }

    @Override
    public boolean isConnectionOpen() {
        return this.connection.isOpen();
    }

    @Override
    protected void onCustomPayload(CustomPayload payload) {
        this.handleCustomPayload(payload);
    }

    private void handleCustomPayload(CustomPayload payload) {
        LOGGER.warn("Unknown custom packet payload: {}", (Object)payload.getId().id());
    }

    @Override
    public void onDynamicRegistries(DynamicRegistriesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.clientRegistries.putDynamicRegistry(packet.registry(), packet.entries());
    }

    @Override
    public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        this.clientRegistries.putTags(packet.getGroups());
    }

    @Override
    public void onFeatures(FeaturesS2CPacket packet) {
        this.enabledFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(packet.features());
    }

    @Override
    public void onSelectKnownPacks(SelectKnownPacksS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        if (this.dataPackManager == null) {
            this.dataPackManager = new ClientDataPackManager();
        }
        List<VersionedIdentifier> list = this.dataPackManager.getCommonKnownPacks(packet.knownPacks());
        this.sendPacket(new SelectKnownPacksC2SPacket(list));
    }

    @Override
    public void onResetChat(ResetChatS2CPacket packet) {
        this.chatState = null;
    }

    private <T> T openClientDataPack(Function<ResourceFactory, T> opener) {
        T t;
        block6: {
            if (this.dataPackManager == null) {
                return opener.apply(ResourceFactory.MISSING);
            }
            LifecycledResourceManager lifecycledResourceManager = this.dataPackManager.createResourceManager();
            try {
                t = opener.apply(lifecycledResourceManager);
                if (lifecycledResourceManager == null) break block6;
                lifecycledResourceManager.close();
            }
            catch (Throwable throwable) {
                if (lifecycledResourceManager != null) {
                    try {
                        lifecycledResourceManager.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
        return t;
    }

    @Override
    public void onReady(ReadyS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        DynamicRegistryManager.Immutable immutable = this.openClientDataPack(factory -> this.clientRegistries.createRegistryManager((ResourceFactory)factory, this.registryManager, this.connection.isLocal()));
        this.connection.transitionInbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(immutable)), new ClientPlayNetworkHandler(this.client, this.connection, new ClientConnectionState(this.profile, this.worldSession, immutable, this.enabledFeatures, this.brand, this.serverInfo, this.postDisconnectScreen, this.serverCookies, this.chatState, this.customReportDetails, this.getServerLinks())));
        this.connection.send(ReadyC2SPacket.INSTANCE);
        this.connection.transitionOutbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(immutable), new PlayStateFactories.PacketCodecModifierContext(this){

            @Override
            public boolean isInCreativeMode() {
                return true;
            }
        }));
    }

    @Override
    public void tick() {
        this.sendQueuedPackets();
    }

    @Override
    public void onDisconnected(DisconnectionInfo info) {
        super.onDisconnected(info);
        this.client.onDisconnected();
    }

    @Override
    protected DialogNetworkAccess createDialogNetworkAccess() {
        return new DialogNetworkAccess(){

            @Override
            public void disconnect(Text reason) {
                ClientConfigurationNetworkHandler.this.connection.disconnect(reason);
            }

            @Override
            public void runClickEventCommand(String command, @Nullable Screen afterActionScreen) {
                LOGGER.warn("Commands are not supported in configuration phase, trying to run '{}'", (Object)command);
            }

            @Override
            public void showDialog(RegistryEntry<Dialog> dialog, @Nullable Screen afterActionScreen) {
                ClientConfigurationNetworkHandler.this.showDialog(dialog, this, afterActionScreen);
            }

            @Override
            public void sendCustomClickActionPacket(Identifier id, Optional<NbtElement> payload) {
                ClientConfigurationNetworkHandler.this.sendPacket(new CustomClickActionC2SPacket(id, payload));
            }

            @Override
            public ServerLinks getServerLinks() {
                return ClientConfigurationNetworkHandler.this.getServerLinks();
            }
        };
    }
}

