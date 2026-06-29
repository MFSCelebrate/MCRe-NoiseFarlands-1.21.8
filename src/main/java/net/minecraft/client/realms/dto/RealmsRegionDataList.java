/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RegionData;

@Environment(value=EnvType.CLIENT)
public record RealmsRegionDataList(@SerializedName(value="regionDataList") List<RegionData> regionData) implements RealmsSerializable
{
    public static RealmsRegionDataList empty() {
        return new RealmsRegionDataList(List.of());
    }
}

