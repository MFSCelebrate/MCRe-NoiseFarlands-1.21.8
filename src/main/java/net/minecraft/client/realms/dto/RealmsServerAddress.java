/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public record RealmsServerAddress(@SerializedName(value="address") @Nullable String address, @SerializedName(value="resourcePackUrl") @Nullable String resourcePackUrl, @SerializedName(value="resourcePackHash") @Nullable String resourcePackHash, @SerializedName(value="sessionRegionData") @Nullable RegionData regionData) implements RealmsSerializable
{
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private RealmsServerAddress NULL = new RealmsServerAddress(null, null, null, null);

    public static RealmsServerAddress parse(CheckedGson gson, String json) {
        try {
            RealmsServerAddress realmsServerAddress = gson.fromJson(json, RealmsServerAddress.class);
            if (realmsServerAddress == null) {
                LOGGER.error("Could not parse RealmsServerAddress: {}", (Object)json);
                return NULL;
            }
            return realmsServerAddress;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerAddress: {}", (Object)exception.getMessage());
            return NULL;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record RegionData(@SerializedName(value="regionName") @JsonAdapter(value=RealmsRegion.RegionTypeAdapter.class) @Nullable RealmsRegion region, @SerializedName(value="serviceQuality") @JsonAdapter(value=ServiceQuality.ServiceQualityTypeAdapter.class) @Nullable ServiceQuality serviceQuality) implements RealmsSerializable
    {
    }
}

