/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.GameVersion;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public class MinecraftVersion {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final GameVersion CURRENT = MinecraftVersion.createCurrent();

    private static GameVersion createCurrent() {
        return new GameVersion.Impl(UUID.randomUUID().toString().replaceAll("-", ""), "1.21.8", new SaveVersion(4440, "main"), SharedConstants.getProtocolVersion(), 64, 81, new Date(), true);
    }

    private static GameVersion fromJson(JsonObject json) {
        JsonObject jsonObject = JsonHelper.getObject(json, "pack_version");
        return new GameVersion.Impl(JsonHelper.getString(json, "id"), JsonHelper.getString(json, "name"), new SaveVersion(JsonHelper.getInt(json, "world_version"), JsonHelper.getString(json, "series_id", "main")), JsonHelper.getInt(json, "protocol_version"), JsonHelper.getInt(jsonObject, "resource"), JsonHelper.getInt(jsonObject, "data"), Date.from(ZonedDateTime.parse(JsonHelper.getString(json, "build_time")).toInstant()), JsonHelper.getBoolean(json, "stable"));
    }

    /*
     * Loose catch block
     */
    public static GameVersion create() {
        try (InputStream inputStream = MinecraftVersion.class.getResourceAsStream("/version.json")) {
            if (inputStream == null) {
                LOGGER.warn("Missing version information!");
                return CURRENT;
            }
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                GameVersion gameVersion = MinecraftVersion.fromJson(JsonHelper.deserialize(inputStreamReader));
                return gameVersion;
            }
        } catch (JsonParseException | IOException exception) {
            throw new IllegalStateException("Game version information is corrupt", exception);
        }
    }
}
