/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.base.Strings
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.JsonOps
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.AbstractPropertiesHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerPropertiesHandler
extends AbstractPropertiesHandler<ServerPropertiesHandler> {
    final static Logger LOGGER = LogUtils.getLogger();
    final static private Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
    final static private Splitter COMMA_SPLITTER = Splitter.on((char)',').trimResults();
    final public boolean onlineMode = this.parseBoolean("online-mode", true);
    final public boolean preventProxyConnections = this.parseBoolean("prevent-proxy-connections", false);
    final public String serverIp = this.getString("server-ip", "");
    final public boolean pvp = this.parseBoolean("pvp", true);
    final public boolean allowFlight = this.parseBoolean("allow-flight", false);
    final public String motd = this.getString("motd", "A Minecraft Server");
    final public String bugReportLink = this.getString("bug-report-link", "");
    final public boolean forceGameMode = this.parseBoolean("force-gamemode", false);
    final public boolean enforceWhitelist = this.parseBoolean("enforce-whitelist", false);
    final public Difficulty difficulty = this.get("difficulty", ServerPropertiesHandler.combineParser(Difficulty::byId, Difficulty::byName), Difficulty::getName, Difficulty.EASY);
    final public GameMode gameMode = this.get("gamemode", ServerPropertiesHandler.combineParser(GameMode::byIndex, GameMode::byId), GameMode::getId, GameMode.SURVIVAL);
    final public String levelName = this.getString("level-name", "world");
    final public int serverPort = this.getInt("server-port", 25565);
    @Nullable
    final public Boolean announcePlayerAchievements = this.getDeprecatedBoolean("announce-player-achievements");
    final public boolean enableQuery = this.parseBoolean("enable-query", false);
    final public int queryPort = this.getInt("query.port", 25565);
    final public boolean enableRcon = this.parseBoolean("enable-rcon", false);
    final public int rconPort = this.getInt("rcon.port", 25575);
    final public String rconPassword = this.getString("rcon.password", "");
    final public boolean hardcore = this.parseBoolean("hardcore", false);
    final public boolean allowNether = this.parseBoolean("allow-nether", true);
    final public boolean spawnMonsters = this.parseBoolean("spawn-monsters", true);
    final public boolean useNativeTransport = this.parseBoolean("use-native-transport", true);
    final public boolean enableCommandBlock = this.parseBoolean("enable-command-block", false);
    final public int spawnProtection = this.getInt("spawn-protection", 16);
    final public int opPermissionLevel = this.getInt("op-permission-level", 4);
    final public int functionPermissionLevel = this.getInt("function-permission-level", 2);
    final public long maxTickTime = this.parseLong("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
    final public int maxChainedNeighborUpdates = this.getInt("max-chained-neighbor-updates", 1000000);
    final public int rateLimit = this.getInt("rate-limit", 0);
    final public int viewDistance = this.getInt("view-distance", 10);
    final public int simulationDistance = this.getInt("simulation-distance", 10);
    final public int maxPlayers = this.getInt("max-players", 20);
    final public int networkCompressionThreshold = this.getInt("network-compression-threshold", 256);
    final public boolean broadcastRconToOps = this.parseBoolean("broadcast-rcon-to-ops", true);
    final public boolean broadcastConsoleToOps = this.parseBoolean("broadcast-console-to-ops", true);
    final public int maxWorldSize = this.transformedParseInt("max-world-size", maxWorldSize -> MathHelper.clamp(maxWorldSize, 1, 29999984), 29999984);
    final public boolean syncChunkWrites = this.parseBoolean("sync-chunk-writes", true);
    final public String regionFileCompression = this.getString("region-file-compression", "deflate");
    final public boolean enableJmxMonitoring = this.parseBoolean("enable-jmx-monitoring", false);
    final public boolean enableStatus = this.parseBoolean("enable-status", true);
    final public boolean hideOnlinePlayers = this.parseBoolean("hide-online-players", false);
    final public int entityBroadcastRangePercentage = this.transformedParseInt("entity-broadcast-range-percentage", percentage -> MathHelper.clamp(percentage, 10, 1000), 100);
    final public String textFilteringConfig = this.getString("text-filtering-config", "");
    final public int textFilteringVersion = this.getInt("text-filtering-version", 0);
    final public Optional<MinecraftServer.ServerResourcePackProperties> serverResourcePackProperties;
    final public DataPackSettings dataPackSettings;
    final public AbstractPropertiesHandler.PropertyAccessor<Integer> playerIdleTimeout = this.intAccessor("player-idle-timeout", 0);
    final public AbstractPropertiesHandler.PropertyAccessor<Boolean> whiteList = this.booleanAccessor("white-list", false);
    final public boolean enforceSecureProfile = this.parseBoolean("enforce-secure-profile", true);
    final public boolean logIps = this.parseBoolean("log-ips", true);
    final public int pauseWhenEmptySeconds = this.getInt("pause-when-empty-seconds", 60);
    final private WorldGenProperties worldGenProperties;
    final public GeneratorOptions generatorOptions;
    public boolean acceptsTransfers = this.parseBoolean("accepts-transfers", false);

    public ServerPropertiesHandler(Properties properties) {
        super(properties);
        String string = this.getString("level-seed", "");
        boolean bl = this.parseBoolean("generate-structures", true);
        long l = GeneratorOptions.parseSeed(string).orElse(GeneratorOptions.getRandomSeed());
        this.generatorOptions = new GeneratorOptions(l, bl, false);
        this.worldGenProperties = new WorldGenProperties(this.get("generator-settings", generatorSettings -> JsonHelper.deserialize(!generatorSettings.isEmpty() ? generatorSettings : "{}"), new JsonObject()), this.get("level-type", type -> type.toLowerCase(Locale.ROOT), WorldPresets.DEFAULT.getValue().toString()));
        this.serverResourcePackProperties = ServerPropertiesHandler.getServerResourcePackProperties(this.getString("resource-pack-id", ""), this.getString("resource-pack", ""), this.getString("resource-pack-sha1", ""), this.getDeprecatedString("resource-pack-hash"), this.parseBoolean("require-resource-pack", false), this.getString("resource-pack-prompt", ""));
        this.dataPackSettings = ServerPropertiesHandler.parseDataPackSettings(this.getString("initial-enabled-packs", String.join((CharSequence)",", DataConfiguration.SAFE_MODE.dataPacks().getEnabled())), this.getString("initial-disabled-packs", String.join((CharSequence)",", DataConfiguration.SAFE_MODE.dataPacks().getDisabled())));
    }

    public static ServerPropertiesHandler load(Path path) {
        return new ServerPropertiesHandler(ServerPropertiesHandler.loadProperties(path));
    }

    @Override
    protected ServerPropertiesHandler net_minecraft_server_dedicated_ServerPropertiesHandler_create(DynamicRegistryManager dynamicRegistryManager, Properties properties) {
        return new ServerPropertiesHandler(properties);
    }

    @Nullable
    private static Text parseResourcePackPrompt(String prompt) {
        if (!Strings.isNullOrEmpty((String)prompt)) {
            try {
                JsonElement jsonElement = StrictJsonParser.parse(prompt);
                return TextCodecs.CODEC.parse(DynamicRegistryManager.EMPTY.getOps(JsonOps.INSTANCE), (Object)jsonElement).resultOrPartial(string2 -> LOGGER.warn("Failed to parse resource pack prompt '{}': {}", (Object)prompt, string2)).orElse(null);
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to parse resource pack prompt '{}'", (Object)prompt, (Object)exception);
            }
        }
        return null;
    }

    private static Optional<MinecraftServer.ServerResourcePackProperties> getServerResourcePackProperties(String id, String url, String sha1, @Nullable String hash, boolean required, String prompt) {
        UUID uUID;
        String string;
        if (url.isEmpty()) {
            return Optional.empty();
        }
        if (!sha1.isEmpty()) {
            string = sha1;
            if (!Strings.isNullOrEmpty((String)hash)) {
                LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        } else if (!Strings.isNullOrEmpty((String)hash)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            string = hash;
        } else {
            string = "";
        }
        if (string.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        } else if (!SHA1_PATTERN.matcher(string).matches()) {
            LOGGER.warn("Invalid sha1 for resource-pack-sha1");
        }
        Text text = ServerPropertiesHandler.parseResourcePackPrompt(prompt);
        if (id.isEmpty()) {
            uUID = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
            LOGGER.warn("resource-pack-id missing, using default of {}", (Object)uUID);
        } else {
            try {
                uUID = UUID.fromString(id);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.warn("Failed to parse '{}' into UUID", (Object)id);
                return Optional.empty();
            }
        }
        return Optional.of(new MinecraftServer.ServerResourcePackProperties(uUID, url, string, required, text));
    }

    private static DataPackSettings parseDataPackSettings(String enabled, String disabled) {
        List list = COMMA_SPLITTER.splitToList((CharSequence)enabled);
        List list2 = COMMA_SPLITTER.splitToList((CharSequence)disabled);
        return new DataPackSettings(list, list2);
    }

    public DimensionOptionsRegistryHolder createDimensionsRegistryHolder(RegistryWrapper.WrapperLookup registries) {
        return this.worldGenProperties.createDimensionsRegistryHolder(registries);
    }

    @Override
    protected AbstractPropertiesHandler net_minecraft_server_dedicated_AbstractPropertiesHandler_create(DynamicRegistryManager registryManager, Properties properties) {
        return this.net_minecraft_server_dedicated_ServerPropertiesHandler_create(registryManager, properties);
    }

    record WorldGenProperties(JsonObject generatorSettings, String levelType) {
        final static private Map<String, RegistryKey<WorldPreset>> LEVEL_TYPE_TO_PRESET_KEY = Map.of("default", WorldPresets.DEFAULT, "largebiomes", WorldPresets.LARGE_BIOMES);

        public DimensionOptionsRegistryHolder createDimensionsRegistryHolder(RegistryWrapper.WrapperLookup registries) {
            RegistryEntryLookup registryWrapper = registries.net_minecraft_registry_RegistryEntryLookup_getOrThrow(RegistryKeys.WORLD_PRESET);
            RegistryEntry.Reference<WorldPreset> reference = registryWrapper.getOptional(WorldPresets.DEFAULT).or(() -> WorldGenProperties.method_41241((RegistryWrapper)registryWrapper)).orElseThrow(() -> new IllegalStateException("Invalid datapack contents: can't find default preset"));
            RegistryEntry registryEntry = Optional.ofNullable(Identifier.tryParse(this.levelType)).map(levelTypeId -> RegistryKey.of(RegistryKeys.WORLD_PRESET, levelTypeId)).or(() -> Optional.ofNullable(LEVEL_TYPE_TO_PRESET_KEY.get(this.levelType))).flatMap(((RegistryWrapper)registryWrapper)::getOptional).orElseGet(() -> {
                LOGGER.warn("Failed to parse level-type {}, defaulting to {}", (Object)this.levelType, (Object)reference.registryKey().getValue());
                return reference;
            });
            DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder = ((WorldPreset)registryEntry.value()).createDimensionsRegistryHolder();
            if (registryEntry.matchesKey(WorldPresets.FLAT)) {
                RegistryOps registryOps = registries.getOps(JsonOps.INSTANCE);
                Optional optional = FlatChunkGeneratorConfig.CODEC.parse(new Dynamic(registryOps, (Object)this.generatorSettings())).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0));
                if (optional.isPresent()) {
                    return dimensionOptionsRegistryHolder.with(registries, new FlatChunkGenerator((FlatChunkGeneratorConfig)optional.get()));
                }
            }
            return dimensionOptionsRegistryHolder;
        }

        private static Optional method_41241(RegistryWrapper registryWrapper) {
            return registryWrapper.streamEntries().findAny();
        }
    }
}

