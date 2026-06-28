/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetryPropertyContainer
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record TelemetryEventProperty<T>(String id, String exportKey, Codec<T> codec, PropertyExporter<T> exporter) {
    final static private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
    final static public TelemetryEventProperty<String> USER_ID = TelemetryEventProperty.ofString("user_id", "userId");
    final static public TelemetryEventProperty<String> CLIENT_ID = TelemetryEventProperty.ofString("client_id", "clientId");
    final static public TelemetryEventProperty<UUID> MINECRAFT_SESSION_ID = TelemetryEventProperty.ofUuid("minecraft_session_id", "deviceSessionId");
    final static public TelemetryEventProperty<String> GAME_VERSION = TelemetryEventProperty.ofString("game_version", "buildDisplayName");
    final static public TelemetryEventProperty<String> OPERATING_SYSTEM = TelemetryEventProperty.ofString("operating_system", "buildPlatform");
    final static public TelemetryEventProperty<String> PLATFORM = TelemetryEventProperty.ofString("platform", "platform");
    final static public TelemetryEventProperty<Boolean> CLIENT_MODDED = TelemetryEventProperty.ofBoolean("client_modded", "clientModded");
    final static public TelemetryEventProperty<String> LAUNCHER_NAME = TelemetryEventProperty.ofString("launcher_name", "launcherName");
    final static public TelemetryEventProperty<UUID> WORLD_SESSION_ID = TelemetryEventProperty.ofUuid("world_session_id", "worldSessionId");
    final static public TelemetryEventProperty<Boolean> SERVER_MODDED = TelemetryEventProperty.ofBoolean("server_modded", "serverModded");
    final static public TelemetryEventProperty<ServerType> SERVER_TYPE = TelemetryEventProperty.of("server_type", "serverType", ServerType.CODEC, (container, exportKey, value) -> container.addProperty(exportKey, value.asString()));
    final static public TelemetryEventProperty<Boolean> OPT_IN = TelemetryEventProperty.ofBoolean("opt_in", "isOptional");
    final static public TelemetryEventProperty<Instant> EVENT_TIMESTAMP_UTC = TelemetryEventProperty.of("event_timestamp_utc", "eventTimestampUtc", Codecs.INSTANT, (container, exportKey, value) -> container.addProperty(exportKey, DATE_TIME_FORMATTER.format((TemporalAccessor)value)));
    final static public TelemetryEventProperty<GameMode> GAME_MODE = TelemetryEventProperty.of("game_mode", "playerGameMode", GameMode.CODEC, (container, exportKey, value) -> container.addProperty(exportKey, value.getRawId()));
    final static public TelemetryEventProperty<String> REALMS_MAP_CONTENT = TelemetryEventProperty.ofString("realms_map_content", "realmsMapContent");
    final static public TelemetryEventProperty<Integer> SECONDS_SINCE_LOAD = TelemetryEventProperty.ofInteger("seconds_since_load", "secondsSinceLoad");
    final static public TelemetryEventProperty<Integer> TICKS_SINCE_LOAD = TelemetryEventProperty.ofInteger("ticks_since_load", "ticksSinceLoad");
    final static public TelemetryEventProperty<LongList> FRAME_RATE_SAMPLES = TelemetryEventProperty.ofLongList("frame_rate_samples", "serializedFpsSamples");
    final static public TelemetryEventProperty<LongList> RENDER_TIME_SAMPLES = TelemetryEventProperty.ofLongList("render_time_samples", "serializedRenderTimeSamples");
    final static public TelemetryEventProperty<LongList> USED_MEMORY_SAMPLES = TelemetryEventProperty.ofLongList("used_memory_samples", "serializedUsedMemoryKbSamples");
    final static public TelemetryEventProperty<Integer> NUMBER_OF_SAMPLES = TelemetryEventProperty.ofInteger("number_of_samples", "numSamples");
    final static public TelemetryEventProperty<Integer> RENDER_DISTANCE = TelemetryEventProperty.ofInteger("render_distance", "renderDistance");
    final static public TelemetryEventProperty<Integer> DEDICATED_MEMORY_KB = TelemetryEventProperty.ofInteger("dedicated_memory_kb", "dedicatedMemoryKb");
    final static public TelemetryEventProperty<Integer> WORLD_LOAD_TIME_MS = TelemetryEventProperty.ofInteger("world_load_time_ms", "worldLoadTimeMs");
    final static public TelemetryEventProperty<Boolean> NEW_WORLD = TelemetryEventProperty.ofBoolean("new_world", "newWorld");
    final static public TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_TOTAL_TIME_MS = TelemetryEventProperty.ofTimeMeasurement("load_time_total_time_ms", "loadTimeTotalTimeMs");
    final static public TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_PRE_WINDOW_MS = TelemetryEventProperty.ofTimeMeasurement("load_time_pre_window_ms", "loadTimePreWindowMs");
    final static public TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_BOOTSTRAP_MS = TelemetryEventProperty.ofTimeMeasurement("load_time_bootstrap_ms", "loadTimeBootstrapMs");
    final static public TelemetryEventProperty<GameLoadTimeEvent.Measurement> LOAD_TIME_LOADING_OVERLAY_MS = TelemetryEventProperty.ofTimeMeasurement("load_time_loading_overlay_ms", "loadTimeLoadingOverlayMs");
    final static public TelemetryEventProperty<String> ADVANCEMENT_ID = TelemetryEventProperty.ofString("advancement_id", "advancementId");
    final static public TelemetryEventProperty<Long> ADVANCEMENT_GAME_TIME = TelemetryEventProperty.ofLong("advancement_game_time", "advancementGameTime");

    public static <T> TelemetryEventProperty<T> of(String id, String exportKey, Codec<T> codec, PropertyExporter<T> exporter) {
        return new TelemetryEventProperty<T>(id, exportKey, codec, exporter);
    }

    public static TelemetryEventProperty<Boolean> ofBoolean(String id, String exportKey) {
        return TelemetryEventProperty.of(id, exportKey, Codec.BOOL, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<String> ofString(String id, String exportKey) {
        return TelemetryEventProperty.of(id, exportKey, Codec.STRING, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<Integer> ofInteger(String id, String exportKey) {
        return TelemetryEventProperty.of(id, exportKey, Codec.INT, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<Long> ofLong(String id, String exportKey) {
        return TelemetryEventProperty.of(id, exportKey, Codec.LONG, TelemetryPropertyContainer::addProperty);
    }

    public static TelemetryEventProperty<UUID> ofUuid(String id, String exportKey) {
        return TelemetryEventProperty.of(id, exportKey, Uuids.STRING_CODEC, (container, key, value) -> container.addProperty(key, value.toString()));
    }

    public static TelemetryEventProperty<GameLoadTimeEvent.Measurement> ofTimeMeasurement(String id, String exportKey) {
        return TelemetryEventProperty.of(id, exportKey, GameLoadTimeEvent.Measurement.CODEC, (container, key, value) -> container.addProperty(key, value.millis()));
    }

    public static TelemetryEventProperty<LongList> ofLongList(String id, String exportKey) {
        return TelemetryEventProperty.of(id, exportKey, Codec.LONG.listOf().xmap(LongArrayList::new, Function.identity()), (container, key, value) -> container.addProperty(key, value.longStream().mapToObj(String::valueOf).collect(Collectors.joining(";"))));
    }

    public void addTo(PropertyMap map, TelemetryPropertyContainer container) {
        Object object = map.get(this);
        if (object != null) {
            this.exporter.apply(container, this.exportKey, object);
        } else {
            container.addNullProperty(this.exportKey);
        }
    }

    public MutableText getTitle() {
        return Text.translatable("telemetry.property." + this.id + ".title");
    }

    @Override
    public String toString() {
        return "TelemetryProperty[" + this.id + "]";
    }

    @Environment(value=EnvType.CLIENT)
    public static interface PropertyExporter<T> {
        public void apply(TelemetryPropertyContainer var1, String var2, T var3);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class GameMode
    extends Enum<GameMode>
    implements StringIdentifiable {
        final static public GameMode SURVIVAL = new GameMode("survival", 0);
        final static public GameMode CREATIVE = new GameMode("creative", 1);
        final static public GameMode ADVENTURE = new GameMode("adventure", 2);
        final static public GameMode SPECTATOR = new GameMode("spectator", 6);
        final static public GameMode HARDCORE = new GameMode("hardcore", 99);
        final static public Codec<GameMode> CODEC;
        final private String id;
        final private int rawId;
        final static private GameMode[] field_41489;

        public static GameMode[] values() {
            return (GameMode[])field_41489.clone();
        }

        public static GameMode valueOf(String string) {
            return Enum.valueOf(GameMode.class, string);
        }

        private GameMode(String id, int rawId) {
            this.id = id;
            this.rawId = rawId;
        }

        public int getRawId() {
            return this.rawId;
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static GameMode[] method_47757() {
            return new GameMode[]{SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR, HARDCORE};
        }

        static {
            field_41489 = GameMode.method_47757();
            CODEC = StringIdentifiable.createCodec(GameMode::values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ServerType
    extends Enum<ServerType>
    implements StringIdentifiable {
        final static public ServerType REALM = new ServerType("realm");
        final static public ServerType LOCAL = new ServerType("local");
        final static public ServerType OTHER = new ServerType("server");
        final static public Codec<ServerType> CODEC;
        final private String id;
        final static private ServerType[] field_41495;

        public static ServerType[] values() {
            return (ServerType[])field_41495.clone();
        }

        public static ServerType valueOf(String string) {
            return Enum.valueOf(ServerType.class, string);
        }

        private ServerType(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static ServerType[] method_47758() {
            return new ServerType[]{REALM, LOCAL, OTHER};
        }

        static {
            field_41495 = ServerType.method_47758();
            CODEC = StringIdentifiable.createCodec(ServerType::values);
        }
    }
}

