/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldDownload
extends ValueObject {
    final static private Logger LOGGER = LogUtils.getLogger();
    public String downloadLink;
    public String resourcePackUrl;
    public String resourcePackHash;

    public static WorldDownload parse(String json) {
        JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
        WorldDownload worldDownload = new WorldDownload();
        try {
            worldDownload.downloadLink = JsonUtils.getNullableStringOr("downloadLink", jsonObject, "");
            worldDownload.resourcePackUrl = JsonUtils.getNullableStringOr("resourcePackUrl", jsonObject, "");
            worldDownload.resourcePackHash = JsonUtils.getNullableStringOr("resourcePackHash", jsonObject, "");
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldDownload: {}", (Object)exception.getMessage());
        }
        return worldDownload;
    }
}

