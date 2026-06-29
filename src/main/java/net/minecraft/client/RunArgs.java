/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.properties.PropertyMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.session.Session;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RunArgs {
    final public Network network;
    final public WindowSettings windowSettings;
    final public Directories directories;
    final public Game game;
    final public QuickPlay quickPlay;

    public RunArgs(Network network, WindowSettings windowSettings, Directories dirs, Game game, QuickPlay quickPlay) {
        this.network = network;
        this.windowSettings = windowSettings;
        this.directories = dirs;
        this.game = game;
        this.quickPlay = quickPlay;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Network {
        final public Session session;
        final public PropertyMap userProperties;
        final public PropertyMap profileProperties;
        final public Proxy netProxy;

        public Network(Session session, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy) {
            this.session = session;
            this.userProperties = userProperties;
            this.profileProperties = profileProperties;
            this.netProxy = proxy;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Directories {
        final public File runDir;
        final public File resourcePackDir;
        final public File assetDir;
        @Nullable
        final public String assetIndex;

        public Directories(File runDir, File resPackDir, File assetDir, @Nullable String assetIndex) {
            this.runDir = runDir;
            this.resourcePackDir = resPackDir;
            this.assetDir = assetDir;
            this.assetIndex = assetIndex;
        }

        public Path getAssetDir() {
            return this.assetIndex == null ? this.assetDir.toPath() : ResourceIndex.buildFileSystem(this.assetDir.toPath(), this.assetIndex);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Game {
        final public boolean demo;
        final public String version;
        final public String versionType;
        final public boolean multiplayerDisabled;
        final public boolean onlineChatDisabled;
        final public boolean tracyEnabled;
        final public boolean renderDebugLabels;

        public Game(boolean demo, String version, String versionType, boolean multiplayerDisabled, boolean onlineChatDisabled, boolean tracyEnabled, boolean renderDebugLabels) {
            this.demo = demo;
            this.version = version;
            this.versionType = versionType;
            this.multiplayerDisabled = multiplayerDisabled;
            this.onlineChatDisabled = onlineChatDisabled;
            this.tracyEnabled = tracyEnabled;
            this.renderDebugLabels = renderDebugLabels;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record QuickPlay(@Nullable String logPath, QuickPlayVariant variant) {
        public boolean isEnabled() {
            return this.variant.isEnabled();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record DisabledQuickPlay() implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record RealmsQuickPlay(String realmId) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return !StringHelper.isBlank(this.realmId);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record MultiplayerQuickPlay(String serverAddress) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return !StringHelper.isBlank(this.serverAddress);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record SingleplayerQuickPlay(@Nullable String worldId) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static sealed interface QuickPlayVariant
    permits SingleplayerQuickPlay, MultiplayerQuickPlay, RealmsQuickPlay, DisabledQuickPlay {
        final static public QuickPlayVariant DEFAULT = new DisabledQuickPlay();

        public boolean isEnabled();
    }
}

