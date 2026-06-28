/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class QuickPlayLogger {
    final static private QuickPlayLogger NOOP = new QuickPlayLogger(""){

        @Override
        public void save(MinecraftClient client) {
        }

        @Override
        public void setWorld(WorldType worldType, String id, String name) {
        }
    };
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Gson GSON = new GsonBuilder().create();
    final private Path path;
    @Nullable
    private QuickPlayWorld world;

    QuickPlayLogger(String relativePath) {
        this.path = MinecraftClient.getInstance().runDirectory.toPath().resolve(relativePath);
    }

    public static QuickPlayLogger create(@Nullable String relativePath) {
        if (relativePath == null) {
            return NOOP;
        }
        return new QuickPlayLogger(relativePath);
    }

    public void setWorld(WorldType worldType, String id, String name) {
        this.world = new QuickPlayWorld(worldType, id, name);
    }

    public void save(MinecraftClient client) {
        if (client.interactionManager == null || this.world == null) {
            LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
            return;
        }
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                Files.deleteIfExists(this.path);
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to delete quickplay log file {}", (Object)this.path, (Object)iOException);
            }
            Log log = new Log(this.world, Instant.now(), minecraftClient.interactionManager.getCurrentGameMode());
            Codec.list(Log.CODEC).encodeStart((DynamicOps)JsonOps.INSTANCE, List.of(log)).resultOrPartial(Util.addPrefix("Quick Play: ", arg_0 -> ((Logger)LOGGER).error(arg_0))).ifPresent(json -> {
                try {
                    Files.createDirectories(this.path.getParent(), new FileAttribute[0]);
                    Files.writeString(this.path, (CharSequence)GSON.toJson(json), new OpenOption[0]);
                }
                catch (IOException iOException) {
                    LOGGER.error("Failed to write to quickplay log file {}", (Object)this.path, (Object)iOException);
                }
            });
        });
    }

    @Environment(value=EnvType.CLIENT)
    record QuickPlayWorld(WorldType type, String id, String name) {
        final static public MapCodec<QuickPlayWorld> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)WorldType.CODEC.fieldOf("type").forGetter(QuickPlayWorld::type), (App)Codecs.ESCAPED_STRING.fieldOf("id").forGetter(QuickPlayWorld::id), (App)Codec.STRING.fieldOf("name").forGetter(QuickPlayWorld::name)).apply((Applicative)instance, QuickPlayWorld::new));
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldType
    extends Enum<WorldType>
    implements StringIdentifiable {
        final static public WorldType SINGLEPLAYER = new WorldType("singleplayer");
        final static public WorldType MULTIPLAYER = new WorldType("multiplayer");
        final static public WorldType REALMS = new WorldType("realms");
        final static Codec<WorldType> CODEC;
        final private String id;
        final static private WorldType[] field_44573;

        public static WorldType[] values() {
            return (WorldType[])field_44573.clone();
        }

        public static WorldType valueOf(String string) {
            return Enum.valueOf(WorldType.class, string);
        }

        private WorldType(String id) {
            this.id = id;
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static WorldType[] method_51271() {
            return new WorldType[]{SINGLEPLAYER, MULTIPLAYER, REALMS};
        }

        static {
            field_44573 = WorldType.method_51271();
            CODEC = StringIdentifiable.createCodec(WorldType::values);
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Log(QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameMode gameMode) {
        final static public Codec<Log> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)QuickPlayWorld.CODEC.forGetter(Log::quickPlayWorld), (App)Codecs.INSTANT.fieldOf("lastPlayedTime").forGetter(Log::lastPlayedTime), (App)GameMode.CODEC.fieldOf("gamemode").forGetter(Log::gameMode)).apply((Applicative)instance, Log::new));

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Log.class, "quickPlayWorld;lastPlayedTime;gamemode", "quickPlayWorld", "lastPlayedTime", "gameMode"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Log.class, "quickPlayWorld;lastPlayedTime;gamemode", "quickPlayWorld", "lastPlayedTime", "gameMode"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Log.class, "quickPlayWorld;lastPlayedTime;gamemode", "quickPlayWorld", "lastPlayedTime", "gameMode"}, this, object);
        }
    }
}

