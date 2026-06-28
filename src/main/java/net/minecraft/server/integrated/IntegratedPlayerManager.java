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
package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.world.PlayerSaveHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class IntegratedPlayerManager
extends PlayerManager {
    final static private Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private NbtCompound userData;

    public IntegratedPlayerManager(IntegratedServer server, CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager, PlayerSaveHandler saveHandler) {
        super(server, registryManager, saveHandler, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void savePlayerData(ServerPlayerEntity player) {
        if (this.net_minecraft_server_integrated_IntegratedServer_getServer().isHost(player.getGameProfile())) {
            try (ErrorReporter.Logging logging = new ErrorReporter.Logging(player.getErrorReporterContext(), LOGGER);){
                NbtWriteView nbtWriteView = NbtWriteView.create(logging, player.getRegistryManager());
                player.writeData(nbtWriteView);
                this.userData = nbtWriteView.getNbt();
            }
        }
        super.savePlayerData(player);
    }

    @Override
    public Text checkCanJoin(SocketAddress address, GameProfile profile) {
        if (this.net_minecraft_server_integrated_IntegratedServer_getServer().isHost(profile) && this.getPlayer(profile.getName()) != null) {
            return Text.translatable("multiplayer.disconnect.name_taken");
        }
        return super.checkCanJoin(address, profile);
    }

    @Override
    public IntegratedServer net_minecraft_server_integrated_IntegratedServer_getServer() {
        return (IntegratedServer)super.net_minecraft_server_MinecraftServer_getServer();
    }

    @Override
    @Nullable
    public NbtCompound getUserData() {
        return this.userData;
    }

    @Override
    public MinecraftServer net_minecraft_server_MinecraftServer_getServer() {
        return this.net_minecraft_server_integrated_IntegratedServer_getServer();
    }
}

