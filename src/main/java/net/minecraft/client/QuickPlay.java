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
package net.minecraft.client;

import com.mojang.logging.LogUtils;
import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class QuickPlay {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public Text ERROR_TITLE = Text.translatable("quickplay.error.title");
    final static private Text ERROR_INVALID_IDENTIFIER = Text.translatable("quickplay.error.invalid_identifier");
    final static private Text ERROR_REALM_CONNECT = Text.translatable("quickplay.error.realm_connect");
    final static private Text ERROR_REALM_PERMISSION = Text.translatable("quickplay.error.realm_permission");
    final static private Text TO_TITLE = Text.translatable("gui.toTitle");
    final static private Text TO_WORLD = Text.translatable("gui.toWorld");
    final static private Text TO_REALMS = Text.translatable("gui.toRealms");

    public static void startQuickPlay(MinecraftClient client, RunArgs.QuickPlayVariant variant, RealmsClient realmsClient) {
        if (!variant.isEnabled()) {
            LOGGER.error("Quick play disabled");
            client.setScreen(new TitleScreen());
            return;
        }
        RunArgs.QuickPlayVariant quickPlayVariant = variant;
        Objects.requireNonNull(quickPlayVariant);
        RunArgs.QuickPlayVariant quickPlayVariant2 = quickPlayVariant;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{RunArgs.MultiplayerQuickPlay.class, RunArgs.RealmsQuickPlay.class, RunArgs.SingleplayerQuickPlay.class, RunArgs.DisabledQuickPlay.class}, (Object)quickPlayVariant2, n)) {
            default: {
                throw new MatchException(null, null);
            }
            case 0: {
                RunArgs.MultiplayerQuickPlay multiplayerQuickPlay = (RunArgs.MultiplayerQuickPlay)quickPlayVariant2;
                QuickPlay.startMultiplayer(client, multiplayerQuickPlay.serverAddress());
                break;
            }
            case 1: {
                RunArgs.RealmsQuickPlay realmsQuickPlay = (RunArgs.RealmsQuickPlay)quickPlayVariant2;
                QuickPlay.startRealms(client, realmsClient, realmsQuickPlay.realmId());
                break;
            }
            case 2: {
                RunArgs.SingleplayerQuickPlay singleplayerQuickPlay = (RunArgs.SingleplayerQuickPlay)quickPlayVariant2;
                String string = singleplayerQuickPlay.worldId();
                if (StringHelper.isBlank(string)) {
                    string = QuickPlay.getLatestLevelName(client.getLevelStorage());
                }
                QuickPlay.startSingleplayer(client, string);
                break;
            }
            case 3: {
                RunArgs.DisabledQuickPlay disabledQuickPlay = (RunArgs.DisabledQuickPlay)quickPlayVariant2;
                LOGGER.error("Quick play disabled");
                client.setScreen(new TitleScreen());
            }
        }
    }

    @Nullable
    private static String getLatestLevelName(LevelStorage storage) {
        try {
            List<LevelSummary> list = storage.loadSummaries(storage.getLevelList()).get();
            if (list.isEmpty()) {
                LOGGER.warn("no latest singleplayer world found");
                return null;
            }
            return list.getFirst().getName();
        }
        catch (InterruptedException | ExecutionException exception) {
            LOGGER.error("failed to load singleplayer world summaries", (Throwable)exception);
            return null;
        }
    }

    private static void startSingleplayer(MinecraftClient client, @Nullable String levelName) {
        if (StringHelper.isBlank(levelName) || !client.getLevelStorage().levelExists(levelName)) {
            SelectWorldScreen screen = new SelectWorldScreen(new TitleScreen());
            client.setScreen(new DisconnectedScreen((Screen)screen, ERROR_TITLE, ERROR_INVALID_IDENTIFIER, TO_WORLD));
            return;
        }
        client.createIntegratedServerLoader().start(levelName, () -> client.setScreen(new TitleScreen()));
    }

    private static void startMultiplayer(MinecraftClient client, String serverAddress) {
        ServerList serverList = new ServerList(client);
        serverList.loadFile();
        ServerInfo serverInfo = serverList.get(serverAddress);
        if (serverInfo == null) {
            serverInfo = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), serverAddress, ServerInfo.ServerType.OTHER);
            serverList.add(serverInfo, true);
            serverList.saveFile();
        }
        ServerAddress serverAddress2 = ServerAddress.parse(serverAddress);
        ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), client, serverAddress2, serverInfo, true, null);
    }

    private static void startRealms(MinecraftClient client, RealmsClient realmsClient, String realmId) {
        RealmsServerList realmsServerList;
        long l;
        try {
            l = Long.parseLong(realmId);
            realmsServerList = realmsClient.listWorlds();
        }
        catch (NumberFormatException numberFormatException) {
            RealmsMainScreen screen = new RealmsMainScreen(new TitleScreen());
            client.setScreen(new DisconnectedScreen((Screen)screen, ERROR_TITLE, ERROR_INVALID_IDENTIFIER, TO_REALMS));
            return;
        }
        catch (RealmsServiceException realmsServiceException) {
            TitleScreen screen = new TitleScreen();
            client.setScreen(new DisconnectedScreen((Screen)screen, ERROR_TITLE, ERROR_REALM_CONNECT, TO_TITLE));
            return;
        }
        RealmsServer realmsServer = realmsServerList.servers.stream().filter(server -> server.id == l).findFirst().orElse(null);
        if (realmsServer == null) {
            RealmsMainScreen screen = new RealmsMainScreen(new TitleScreen());
            client.setScreen(new DisconnectedScreen((Screen)screen, ERROR_TITLE, ERROR_REALM_PERMISSION, TO_REALMS));
            return;
        }
        TitleScreen titleScreen = new TitleScreen();
        client.setScreen(new RealmsLongRunningMcoTaskScreen(titleScreen, new RealmsPrepareConnectionTask(titleScreen, realmsServer)));
    }
}

