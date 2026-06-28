/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.PlayerActivity;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;

@Environment(value=EnvType.CLIENT)
public class PlayerActivities
extends ValueObject {
    public long periodInMillis;
    public List<PlayerActivity> playerActivityDto = Lists.newArrayList();

    public static PlayerActivities parse(String json) {
        PlayerActivities playerActivities = new PlayerActivities();
        try {
            JsonElement jsonElement = LenientJsonParser.parse(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            playerActivities.periodInMillis = JsonUtils.getLongOr("periodInMillis", jsonObject, -1L);
            JsonElement jsonElement2 = jsonObject.get("playerActivityDto");
            if (jsonElement2 != null && jsonElement2.isJsonArray()) {
                JsonArray jsonArray = jsonElement2.getAsJsonArray();
                for (JsonElement jsonElement3 : jsonArray) {
                    PlayerActivity playerActivity = PlayerActivity.parse(jsonElement3.getAsJsonObject());
                    playerActivities.playerActivityDto.add(playerActivity);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return playerActivities;
    }
}

