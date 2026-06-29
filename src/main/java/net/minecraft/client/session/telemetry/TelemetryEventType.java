/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetryEvent
 *  com.mojang.authlib.minecraft.TelemetryPropertyContainer
 *  com.mojang.authlib.minecraft.TelemetrySession
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.SentTelemetryEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class TelemetryEventType {
    final static Map<String, TelemetryEventType> TYPES = new Object2ObjectLinkedOpenHashMap();
    final static public Codec<TelemetryEventType> CODEC = Codec.STRING.comapFlatMap(id -> {
        TelemetryEventType telemetryEventType = TYPES.get(id);
        if (telemetryEventType != null) {
            return DataResult.success((Object)telemetryEventType);
        }
        return DataResult.error(() -> "No TelemetryEventType with key: '" + id + "'");
    }, TelemetryEventType::getId);
    final static private List<TelemetryEventProperty<?>> BASIC_PROPERTIES = List.of(TelemetryEventProperty.USER_ID, TelemetryEventProperty.CLIENT_ID, TelemetryEventProperty.MINECRAFT_SESSION_ID, TelemetryEventProperty.GAME_VERSION, TelemetryEventProperty.OPERATING_SYSTEM, TelemetryEventProperty.PLATFORM, TelemetryEventProperty.CLIENT_MODDED, TelemetryEventProperty.LAUNCHER_NAME, TelemetryEventProperty.EVENT_TIMESTAMP_UTC, TelemetryEventProperty.OPT_IN);
    final static private List<TelemetryEventProperty<?>> REQUIRED_PROPERTIES = Stream.concat(BASIC_PROPERTIES.stream(), Stream.of(TelemetryEventProperty.WORLD_SESSION_ID, TelemetryEventProperty.SERVER_MODDED, TelemetryEventProperty.SERVER_TYPE)).toList();
    final static public TelemetryEventType WORLD_LOADED = TelemetryEventType.builder("world_loaded", "WorldLoaded").properties(REQUIRED_PROPERTIES).properties(TelemetryEventProperty.GAME_MODE).properties(TelemetryEventProperty.REALMS_MAP_CONTENT).build();
    final static public TelemetryEventType PERFORMANCE_METRICS = TelemetryEventType.builder("performance_metrics", "PerformanceMetrics").properties(REQUIRED_PROPERTIES).properties(TelemetryEventProperty.FRAME_RATE_SAMPLES).properties(TelemetryEventProperty.RENDER_TIME_SAMPLES).properties(TelemetryEventProperty.USED_MEMORY_SAMPLES).properties(TelemetryEventProperty.NUMBER_OF_SAMPLES).properties(TelemetryEventProperty.RENDER_DISTANCE).properties(TelemetryEventProperty.DEDICATED_MEMORY_KB).optional().build();
    final static public TelemetryEventType WORLD_LOAD_TIMES = TelemetryEventType.builder("world_load_times", "WorldLoadTimes").properties(REQUIRED_PROPERTIES).properties(TelemetryEventProperty.WORLD_LOAD_TIME_MS).properties(TelemetryEventProperty.NEW_WORLD).optional().build();
    final static public TelemetryEventType WORLD_UNLOADED = TelemetryEventType.builder("world_unloaded", "WorldUnloaded").properties(REQUIRED_PROPERTIES).properties(TelemetryEventProperty.SECONDS_SINCE_LOAD).properties(TelemetryEventProperty.TICKS_SINCE_LOAD).build();
    final static public TelemetryEventType ADVANCEMENT_MADE = TelemetryEventType.builder("advancement_made", "AdvancementMade").properties(REQUIRED_PROPERTIES).properties(TelemetryEventProperty.ADVANCEMENT_ID).properties(TelemetryEventProperty.ADVANCEMENT_GAME_TIME).optional().build();
    final static public TelemetryEventType GAME_LOAD_TIMES = TelemetryEventType.builder("game_load_times", "GameLoadTimes").properties(BASIC_PROPERTIES).properties(TelemetryEventProperty.LOAD_TIME_TOTAL_TIME_MS).properties(TelemetryEventProperty.LOAD_TIME_PRE_WINDOW_MS).properties(TelemetryEventProperty.LOAD_TIME_BOOTSTRAP_MS).properties(TelemetryEventProperty.LOAD_TIME_LOADING_OVERLAY_MS).optional().build();
    final private String id;
    final private String exportKey;
    final private List<TelemetryEventProperty<?>> properties;
    final private boolean optional;
    final private MapCodec<SentTelemetryEvent> codec;

    TelemetryEventType(String id, String exportKey, List<TelemetryEventProperty<?>> properties, boolean optional) {
        this.id = id;
        this.exportKey = exportKey;
        this.properties = properties;
        this.optional = optional;
        this.codec = PropertyMap.createCodec(properties).xmap(map -> new SentTelemetryEvent(this, (PropertyMap)map), SentTelemetryEvent::properties);
    }

    public static Builder builder(String id, String sentEventId) {
        return new Builder(id, sentEventId);
    }

    public String getId() {
        return this.id;
    }

    public List<TelemetryEventProperty<?>> getProperties() {
        return this.properties;
    }

    public MapCodec<SentTelemetryEvent> getCodec() {
        return this.codec;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public TelemetryEvent createEvent(TelemetrySession session, PropertyMap properties) {
        TelemetryEvent telemetryEvent = session.createNewEvent(this.exportKey);
        for (TelemetryEventProperty<?> telemetryEventProperty : this.properties) {
            telemetryEventProperty.addTo(properties, (TelemetryPropertyContainer)telemetryEvent);
        }
        return telemetryEvent;
    }

    public <T> boolean hasProperty(TelemetryEventProperty<T> property) {
        return this.properties.contains(property);
    }

    public String toString() {
        return "TelemetryEventType[" + this.id + "]";
    }

    public MutableText getTitle() {
        return this.getText("title");
    }

    public MutableText getDescription() {
        return this.getText("description");
    }

    private MutableText getText(String key) {
        return Text.translatable("telemetry.event." + this.id + "." + key);
    }

    public static List<TelemetryEventType> getTypes() {
        return List.copyOf(TYPES.values());
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final private String id;
        final private String exportKey;
        final private List<TelemetryEventProperty<?>> properties = new ArrayList();
        private boolean optional;

        Builder(String id, String exportKey) {
            this.id = id;
            this.exportKey = exportKey;
        }

        public Builder properties(List<TelemetryEventProperty<?>> properties) {
            this.properties.addAll(properties);
            return this;
        }

        public <T> Builder properties(TelemetryEventProperty<T> property) {
            this.properties.add(property);
            return this;
        }

        public Builder optional() {
            this.optional = true;
            return this;
        }

        public TelemetryEventType build() {
            TelemetryEventType telemetryEventType = new TelemetryEventType(this.id, this.exportKey, List.copyOf(this.properties), this.optional);
            if (TYPES.putIfAbsent(this.id, telemetryEventType) != null) {
                throw new IllegalStateException("Duplicate TelemetryEventType with key: '" + this.id + "'");
            }
            return telemetryEventType;
        }
    }
}

