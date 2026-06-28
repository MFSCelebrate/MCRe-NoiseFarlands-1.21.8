/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsNews
extends ValueObject {
    final static private Logger LOGGER = LogUtils.getLogger();
    @Nullable
    public String newsLink;

    public static RealmsNews parse(String json) {
        RealmsNews realmsNews = new RealmsNews();
        try {
            JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
            realmsNews.newsLink = JsonUtils.getNullableStringOr("newsLink", jsonObject, null);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsNews: {}", (Object)exception.getMessage());
        }
        return realmsNews;
    }
}

