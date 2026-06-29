/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UndashedUuid
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsClientConfig;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.dto.BackupList;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.client.realms.dto.PendingInvitesList;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.PlayerActivities;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsConfigurationDto;
import net.minecraft.client.realms.dto.RealmsDescriptionDto;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsOptionsDto;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RealmsRegionDataList;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.RealmsWorldResetDto;
import net.minecraft.client.realms.dto.RegionData;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsHttpException;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.UploadTokenCache;
import net.minecraft.client.session.Session;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@net.fabricmc.api.Environment(value=EnvType.CLIENT)
public class RealmsClient {
    final static public Environment ENVIRONMENT = Optional.ofNullable(System.getenv("realms.environment")).or(() -> Optional.ofNullable(System.getProperty("realms.environment"))).flatMap(Environment::fromName).orElse(Environment.PRODUCTION);
    final static private Logger LOGGER = LogUtils.getLogger();
    @Nullable
    static private volatile RealmsClient instance = null;
    final private CompletableFuture<Set<String>> featureFlagsFuture;
    final private String sessionId;
    final private String username;
    final private MinecraftClient client;
    final static private String WORLDS_ENDPOINT = "worlds";
    final static private String INVITES_ENDPOINT = "invites";
    final static private String MCO_ENDPOINT = "mco";
    final static private String SUBSCRIPTIONS_ENDPOINT = "subscriptions";
    final static private String ACTIVITIES_ENDPOINT = "activities";
    final static private String OPS_ENDPOINT = "ops";
    final static private String PING_STAT_ENDPOINT = "regions/ping/stat";
    final static private String PREFERRED_REGIONS_ENDPOINT = "regions/preferredRegions";
    final static private String TRIAL_ENDPOINT = "trial";
    final static private String NOTIFICATIONS_ENDPOINT = "notifications";
    final static private String FEATURE_ENDPOINT = "feature/v1";
    final static private String LIST_USER_WORLDS_OF_TYPE_ANY_ENDPOINT = "/listUserWorldsOfType/any";
    final static private String CREATE_PRERELEASE_REALM_ENDPOINT = "/$PARENT_WORLD_ID/createPrereleaseRealm";
    final static private String LIST_PRERELEASE_ELIGIBLE_WORLDS_ENDPOINT = "/listPrereleaseEligibleWorlds";
    final static private String WORLD_INITIALIZE_ENDPOINT = "/$WORLD_ID/initialize";
    final static private String WORLD_ENDPOINT = "/$WORLD_ID";
    final static private String LIVEPLAYERLIST_ENDPOINT = "/liveplayerlist";
    final static private String WORLD_ENDPOINT_2 = "/$WORLD_ID";
    final static private String WORLD_PROFILE_ENDPOINT = "/$WORLD_ID/$PROFILE_UUID";
    final static private String MINIGAMES_ENDPOINT = "/minigames/$MINIGAME_ID/$WORLD_ID";
    final static private String AVAILABLE_ENDPOINT = "/available";
    final static private String TEMPLATES_ENDPOINT = "/templates/$WORLD_TYPE";
    final static private String JOIN_PC_ENDPOINT = "/v1/$ID/join/pc";
    final static private String ID_ENDPOINT = "/$ID";
    final static private String WORLD_ENDPOINT_3 = "/$WORLD_ID";
    final static private String INVITE_ENDPOINT = "/$WORLD_ID/invite/$UUID";
    final static private String COUNT_PENDING_ENDPOINT = "/count/pending";
    final static private String PENDING_ENDPOINT = "/pending";
    final static private String ACCEPT_INVITATION_ENDPOINT = "/accept/$INVITATION_ID";
    final static private String REJECT_INVITATION_ENDPOINT = "/reject/$INVITATION_ID";
    final static private String WORLD_ENDPOINT_4 = "/$WORLD_ID";
    final static private String WORLD_CONFIGURATION_ENDPOINT = "/$WORLD_ID/configuration";
    final static private String WORLD_SLOT_ENDPOINT = "/$WORLD_ID/slot/$SLOT_ID";
    final static private String WORLD_OPEN_ENDPOINT = "/$WORLD_ID/open";
    final static private String WORLD_CLOSE_ENDPOINT = "/$WORLD_ID/close";
    final static private String WORLD_RESET_ENDPOINT = "/$WORLD_ID/reset";
    final static private String WORLD_ENDPOINT_6 = "/$WORLD_ID";
    final static private String WORLD_BACKUPS_ENDPOINT = "/$WORLD_ID/backups";
    final static private String WORLD_SLOT_DOWNLOAD_ENDPOINT = "/$WORLD_ID/slot/$SLOT_ID/download";
    final static private String WORLD_BACKUPS_UPLOAD_ENDPOINT = "/$WORLD_ID/backups/upload";
    final static private String CLIENT_COMPATIBLE_ENDPOINT = "/client/compatible";
    final static private String TOS_AGREED_ENDPOINT = "/tos/agreed";
    final static private String NEWS_ENDPOINT = "/v1/news";
    final static private String SEEN_ENDPOINT = "/seen";
    final static private String DISMISS_ENDPOINT = "/dismiss";
    final static private CheckedGson JSON = new CheckedGson();

    public static RealmsClient create() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return RealmsClient.createRealmsClient(minecraftClient);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RealmsClient createRealmsClient(MinecraftClient client) {
        String string = client.getSession().getUsername();
        String string2 = client.getSession().getSessionId();
        RealmsClient realmsClient = instance;
        if (realmsClient != null) {
            return realmsClient;
        }
        Class<RealmsClient> clazz = RealmsClient.class;
        synchronized (RealmsClient.class) {
            RealmsClient realmsClient2 = instance;
            if (realmsClient2 != null) {
                // ** MonitorExit[var4_4] (shouldn't be in output)
                return realmsClient2;
            }
            instance = realmsClient2 = new RealmsClient(string2, string, client);
            // ** MonitorExit[var4_4] (shouldn't be in output)
            return realmsClient2;
        }
    }

    private RealmsClient(String sessionId, String username, MinecraftClient client) {
        this.sessionId = sessionId;
        this.username = username;
        this.client = client;
        RealmsClientConfig.setProxy(client.getNetworkProxy());
        this.featureFlagsFuture = CompletableFuture.supplyAsync(this::fetchFeatureFlags, Util.getDownloadWorkerExecutor());
    }

    public Set<String> getFeatureFlags() {
        return this.featureFlagsFuture.join();
    }

    private Set<String> fetchFeatureFlags() {
        Session session = MinecraftClient.getInstance().getSession();
        if (session.getAccountType() != Session.AccountType.MSA) {
            return Set.of();
        }
        String string = RealmsClient.url(FEATURE_ENDPOINT, null, false);
        try {
            String string2 = this.execute(Request.get(string, 5000, 10000));
            JsonArray jsonArray = LenientJsonParser.parse(string2).getAsJsonArray();
            Set<String> set = jsonArray.asList().stream().map(JsonElement::getAsString).collect(Collectors.toSet());
            LOGGER.debug("Fetched Realms feature flags: {}", set);
            return set;
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Failed to fetch Realms feature flags", (Throwable)realmsServiceException);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse Realms feature flags", (Throwable)exception);
        }
        return Set.of();
    }

    public RealmsServerList listWorlds() throws RealmsServiceException {
        Object string = this.url(WORLDS_ENDPOINT);
        if (RealmsMainScreen.isSnapshotRealmsEligible()) {
            string = (String)string + LIST_USER_WORLDS_OF_TYPE_ANY_ENDPOINT;
        }
        String string2 = this.execute(Request.get((String)string));
        return RealmsServerList.parse(JSON, string2);
    }

    public List<RealmsServer> getPrereleaseEligibleServers() throws RealmsServiceException {
        String string = this.url("worlds/listPrereleaseEligibleWorlds");
        String string2 = this.execute(Request.get(string));
        return RealmsServerList.parse((CheckedGson)RealmsClient.JSON, (String)string2).servers;
    }

    public RealmsServer createPrereleaseServer(Long parentWorldId) throws RealmsServiceException {
        String string = String.valueOf(parentWorldId);
        String string2 = this.url(WORLDS_ENDPOINT + CREATE_PRERELEASE_REALM_ENDPOINT.replace("$PARENT_WORLD_ID", string));
        return RealmsServer.parse(JSON, this.execute(Request.post(string2, string)));
    }

    public List<RealmsNotification> listNotifications() throws RealmsServiceException {
        String string = this.url(NOTIFICATIONS_ENDPOINT);
        String string2 = this.execute(Request.get(string));
        return RealmsNotification.parse(string2);
    }

    private static JsonArray toJsonArray(List<UUID> uuids) {
        JsonArray jsonArray = new JsonArray();
        for (UUID uUID : uuids) {
            if (uUID == null) continue;
            jsonArray.add(uUID.toString());
        }
        return jsonArray;
    }

    public void markNotificationsAsSeen(List<UUID> notifications) throws RealmsServiceException {
        String string = this.url("notifications/seen");
        this.execute(Request.post(string, JSON.toJson((JsonElement)RealmsClient.toJsonArray(notifications))));
    }

    public void dismissNotifications(List<UUID> notifications) throws RealmsServiceException {
        String string = this.url("notifications/dismiss");
        this.execute(Request.post(string, JSON.toJson((JsonElement)RealmsClient.toJsonArray(notifications))));
    }

    public RealmsServer getOwnWorld(long worldId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + ID_ENDPOINT.replace("$ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get(string));
        return RealmsServer.parse(JSON, string2);
    }

    public RealmsRegionDataList getRegionDataList() throws RealmsServiceException {
        String string = this.url(PREFERRED_REGIONS_ENDPOINT);
        String string2 = this.execute(Request.get(string));
        try {
            RealmsRegionDataList realmsRegionDataList = JSON.fromJson(string2, RealmsRegionDataList.class);
            if (realmsRegionDataList == null) {
                return RealmsRegionDataList.empty();
            }
            Set set = realmsRegionDataList.regionData().stream().map(RegionData::region).collect(Collectors.toSet());
            for (RealmsRegion realmsRegion : RealmsRegion.values()) {
                if (realmsRegion == RealmsRegion.INVALID_REGION || set.contains((Object)realmsRegion)) continue;
                LOGGER.debug("No realms region matching {} in server response", (Object)realmsRegion);
            }
            return realmsRegionDataList;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse PreferredRegionSelections: {}", (Object)exception.getMessage());
            return RealmsRegionDataList.empty();
        }
    }

    public PlayerActivities getPlayerActivities(long worldId) throws RealmsServiceException {
        String string = this.url(ACTIVITIES_ENDPOINT + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get(string));
        return PlayerActivities.parse(string2);
    }

    public RealmsServerPlayerList getLiveStats() throws RealmsServiceException {
        String string = this.url("activities/liveplayerlist");
        String string2 = this.execute(Request.get(string));
        return RealmsServerPlayerList.parse(string2);
    }

    public RealmsServerAddress join(long worldId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + JOIN_PC_ENDPOINT.replace("$ID", "" + worldId));
        String string2 = this.execute(Request.get(string, 5000, 30000));
        return RealmsServerAddress.parse(JSON, string2);
    }

    public void initializeWorld(long worldId, String name, String motd) throws RealmsServiceException {
        RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, motd);
        String string = this.url(WORLDS_ENDPOINT + WORLD_INITIALIZE_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = JSON.toJson(realmsDescriptionDto);
        this.execute(Request.post(string, string2, 5000, 10000));
    }

    public boolean mcoEnabled() throws RealmsServiceException {
        String string = this.url("mco/available");
        String string2 = this.execute(Request.get(string));
        return Boolean.parseBoolean(string2);
    }

    public CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
        CompatibleVersionResponse compatibleVersionResponse;
        String string = this.url("mco/client/compatible");
        String string2 = this.execute(Request.get(string));
        try {
            compatibleVersionResponse = CompatibleVersionResponse.valueOf(string2);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new RealmsServiceException(RealmsError.SimpleHttpError.unknownCompatibility(string2));
        }
        return compatibleVersionResponse;
    }

    public void uninvite(long worldId, UUID profileUuid) throws RealmsServiceException {
        String string = this.url(INVITES_ENDPOINT + INVITE_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)).replace("$UUID", UndashedUuid.toString((UUID)profileUuid)));
        this.execute(Request.delete(string));
    }

    public void uninviteMyselfFrom(long worldId) throws RealmsServiceException {
        String string = this.url(INVITES_ENDPOINT + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.delete(string));
    }

    public List<PlayerInfo> invite(long worldId, String profileName) throws RealmsServiceException {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setName(profileName);
        String string = this.url(INVITES_ENDPOINT + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.post(string, JSON.toJson(playerInfo)));
        return RealmsServer.parse((CheckedGson)RealmsClient.JSON, (String)string2).players;
    }

    public BackupList backupsFor(long worldId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + WORLD_BACKUPS_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get(string));
        return BackupList.parse(string2);
    }

    public void configure(long worldId, String name, String description, @Nullable RealmsRegionSelectionPreference regionSelectionPreference, int slotId, RealmsWorldOptions options, List<RealmsSettingDto> settings) throws RealmsServiceException {
        RealmsRegionSelectionPreference realmsRegionSelectionPreference = regionSelectionPreference != null ? regionSelectionPreference : new RealmsRegionSelectionPreference(RegionSelectionMethod.DEFAULT, null);
        RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, description);
        RealmsOptionsDto realmsOptionsDto = new RealmsOptionsDto(slotId, options, RealmsSettingDto.isHardcore(settings));
        RealmsConfigurationDto realmsConfigurationDto = new RealmsConfigurationDto(realmsOptionsDto, settings, realmsRegionSelectionPreference, realmsDescriptionDto);
        String string = this.url(WORLDS_ENDPOINT + WORLD_CONFIGURATION_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.post(string, JSON.toJson(realmsConfigurationDto)));
    }

    public void updateSlot(long worldId, int slot, RealmsWorldOptions options, List<RealmsSettingDto> settings) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + WORLD_SLOT_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
        String string2 = JSON.toJson(new RealmsOptionsDto(slot, options, RealmsSettingDto.isHardcore(settings)));
        this.execute(Request.post(string, string2));
    }

    public boolean switchSlot(long worldId, int slot) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + WORLD_SLOT_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public void restoreWorld(long worldId, String backupId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + WORLD_BACKUPS_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)), "backupId=" + backupId);
        this.execute(Request.put(string, "", 40000, 600000));
    }

    public WorldTemplatePaginatedList fetchWorldTemplates(int page, int pageSize, RealmsServer.WorldType type) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + TEMPLATES_ENDPOINT.replace("$WORLD_TYPE", type.toString()), String.format(Locale.ROOT, "page=%d&pageSize=%d", page, pageSize));
        String string2 = this.execute(Request.get(string));
        return WorldTemplatePaginatedList.parse(string2);
    }

    public Boolean putIntoMinigameMode(long worldId, String minigameId) throws RealmsServiceException {
        String string = MINIGAMES_ENDPOINT.replace("$MINIGAME_ID", minigameId).replace("$WORLD_ID", String.valueOf(worldId));
        String string2 = this.url(WORLDS_ENDPOINT + string);
        return Boolean.valueOf(this.execute(Request.put(string2, "")));
    }

    public Ops op(long worldId, UUID profileUuid) throws RealmsServiceException {
        String string = WORLD_PROFILE_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", UndashedUuid.toString((UUID)profileUuid));
        String string2 = this.url(OPS_ENDPOINT + string);
        return Ops.parse(this.execute(Request.post(string2, "")));
    }

    public Ops deop(long worldId, UUID profileUuid) throws RealmsServiceException {
        String string = WORLD_PROFILE_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", UndashedUuid.toString((UUID)profileUuid));
        String string2 = this.url(OPS_ENDPOINT + string);
        return Ops.parse(this.execute(Request.delete(string2)));
    }

    public Boolean open(long worldId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + WORLD_OPEN_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean close(long worldId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + WORLD_CLOSE_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean resetWorldWithTemplate(long worldId, String worldTemplateId) throws RealmsServiceException {
        RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(null, Long.valueOf(worldTemplateId), -1, false, Set.of());
        String string = this.url(WORLDS_ENDPOINT + WORLD_RESET_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.post(string, JSON.toJson(realmsWorldResetDto), 30000, 80000));
        return Boolean.valueOf(string2);
    }

    public Subscription subscriptionFor(long worldId) throws RealmsServiceException {
        String string = this.url(SUBSCRIPTIONS_ENDPOINT + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get(string));
        return Subscription.parse(string2);
    }

    public int pendingInvitesCount() throws RealmsServiceException {
        return this.pendingInvites().pendingInvites.size();
    }

    public PendingInvitesList pendingInvites() throws RealmsServiceException {
        String string = this.url("invites/pending");
        String string2 = this.execute(Request.get(string));
        PendingInvitesList pendingInvitesList = PendingInvitesList.parse(string2);
        pendingInvitesList.pendingInvites.removeIf(this::isOwnerBlocked);
        return pendingInvitesList;
    }

    private boolean isOwnerBlocked(PendingInvite invite) {
        return this.client.getSocialInteractionsManager().isPlayerBlocked(invite.worldOwnerUuid);
    }

    public void acceptInvitation(String invitationId) throws RealmsServiceException {
        String string = this.url(INVITES_ENDPOINT + ACCEPT_INVITATION_ENDPOINT.replace("$INVITATION_ID", invitationId));
        this.execute(Request.put(string, ""));
    }

    public WorldDownload download(long worldId, int slotId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + WORLD_SLOT_DOWNLOAD_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slotId)));
        String string2 = this.execute(Request.get(string));
        return WorldDownload.parse(string2);
    }

    @Nullable
    public UploadInfo upload(long worldId) throws RealmsServiceException {
        String string2;
        String string = this.url(WORLDS_ENDPOINT + WORLD_BACKUPS_UPLOAD_ENDPOINT.replace("$WORLD_ID", String.valueOf(worldId)));
        UploadInfo uploadInfo = UploadInfo.parse(this.execute(Request.put(string, UploadInfo.createRequestContent(string2 = UploadTokenCache.get(worldId)))));
        if (uploadInfo != null) {
            UploadTokenCache.put(worldId, uploadInfo.getToken());
        }
        return uploadInfo;
    }

    public void rejectInvitation(String invitationId) throws RealmsServiceException {
        String string = this.url(INVITES_ENDPOINT + REJECT_INVITATION_ENDPOINT.replace("$INVITATION_ID", invitationId));
        this.execute(Request.put(string, ""));
    }

    public void agreeToTos() throws RealmsServiceException {
        String string = this.url("mco/tos/agreed");
        this.execute(Request.post(string, ""));
    }

    public RealmsNews getNews() throws RealmsServiceException {
        String string = this.url("mco/v1/news");
        String string2 = this.execute(Request.get(string, 5000, 10000));
        return RealmsNews.parse(string2);
    }

    public void sendPingResults(PingResult pingResult) throws RealmsServiceException {
        String string = this.url(PING_STAT_ENDPOINT);
        this.execute(Request.post(string, JSON.toJson(pingResult)));
    }

    public Boolean trialAvailable() throws RealmsServiceException {
        String string = this.url(TRIAL_ENDPOINT);
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    public void deleteWorld(long worldId) throws RealmsServiceException {
        String string = this.url(WORLDS_ENDPOINT + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.delete(string));
    }

    private String url(String path) throws RealmsServiceException {
        return this.url(path, null);
    }

    private String url(String path, @Nullable String queryString) throws RealmsServiceException {
        return RealmsClient.url(path, queryString, this.getFeatureFlags().contains("realms_in_aks"));
    }

    private static String url(String path, @Nullable String queryString, boolean aks) {
        try {
            return new URI(RealmsClient.ENVIRONMENT.protocol, aks ? RealmsClient.ENVIRONMENT.aksUrl : RealmsClient.ENVIRONMENT.baseUrl, "/" + path, queryString, null).toASCIIString();
        }
        catch (URISyntaxException uRISyntaxException) {
            throw new IllegalArgumentException(path, uRISyntaxException);
        }
    }

    private String execute(Request<?> r) throws RealmsServiceException {
        r.cookie("sid", this.sessionId);
        r.cookie("user", this.username);
        r.cookie("version", SharedConstants.getGameVersion().name());
        r.prerelease(RealmsMainScreen.isSnapshotRealmsEligible());
        try {
            int i = r.responseCode();
            if (i == 503 || i == 277) {
                int j = r.getRetryAfterHeader();
                throw new RetryCallException(j, i);
            }
            String string = r.text();
            if (i < 200 || i >= 300) {
                if (i == 401) {
                    String string2 = r.getHeader("WWW-Authenticate");
                    LOGGER.info("Could not authorize you against Realms server: {}", (Object)string2);
                    throw new RealmsServiceException(new RealmsError.AuthenticationError(string2));
                }
                String string2 = r.connection.getContentType();
                if (string2 != null && string2.startsWith("text/html")) {
                    throw new RealmsServiceException(RealmsError.SimpleHttpError.unreadableHtmlBody(i, string));
                }
                RealmsError realmsError = RealmsError.ofHttp(i, string);
                throw new RealmsServiceException(realmsError);
            }
            return string;
        }
        catch (RealmsHttpException realmsHttpException) {
            throw new RealmsServiceException(RealmsError.SimpleHttpError.connectivity(realmsHttpException));
        }
    }

    @net.fabricmc.api.Environment(value=EnvType.CLIENT)
    public static final class CompatibleVersionResponse
    extends Enum<CompatibleVersionResponse> {
        final static public CompatibleVersionResponse COMPATIBLE = new CompatibleVersionResponse();
        final static public CompatibleVersionResponse OUTDATED = new CompatibleVersionResponse();
        final static public CompatibleVersionResponse OTHER = new CompatibleVersionResponse();
        final static private CompatibleVersionResponse[] field_19585;

        public static CompatibleVersionResponse[] values() {
            return (CompatibleVersionResponse[])field_19585.clone();
        }

        public static CompatibleVersionResponse valueOf(String name) {
            return Enum.valueOf(CompatibleVersionResponse.class, name);
        }

        private static CompatibleVersionResponse[] method_36846() {
            return new CompatibleVersionResponse[]{COMPATIBLE, OUTDATED, OTHER};
        }

        static {
            field_19585 = CompatibleVersionResponse.method_36846();
        }
    }

    @net.fabricmc.api.Environment(value=EnvType.CLIENT)
    public static final class Environment
    extends Enum<Environment> {
        final static public Environment PRODUCTION = new Environment("pc.realms.minecraft.net", "java.frontendlegacy.realms.minecraft-services.net", "https");
        final static public Environment STAGE = new Environment("pc-stage.realms.minecraft.net", "java.frontendlegacy.stage-c2a40e62.realms.minecraft-services.net", "https");
        final static public Environment LOCAL = new Environment("localhost:8080", "localhost:8080", "http");
        final public String baseUrl;
        final public String aksUrl;
        final public String protocol;
        final static private Environment[] field_19591;

        public static Environment[] values() {
            return (Environment[])field_19591.clone();
        }

        public static Environment valueOf(String name) {
            return Enum.valueOf(Environment.class, name);
        }

        private Environment(String baseUrl, String aksUrl, String protocol) {
            this.baseUrl = baseUrl;
            this.aksUrl = aksUrl;
            this.protocol = protocol;
        }

        public static Optional<Environment> fromName(String name) {
            return switch (name.toLowerCase(Locale.ROOT)) {
                case "production" -> Optional.of(PRODUCTION);
                case "local" -> Optional.of(LOCAL);
                case "stage", "staging" -> Optional.of(STAGE);
                default -> Optional.empty();
            };
        }

        private static Environment[] method_36847() {
            return new Environment[]{PRODUCTION, STAGE, LOCAL};
        }

        static {
            field_19591 = Environment.method_36847();
        }
    }
}

