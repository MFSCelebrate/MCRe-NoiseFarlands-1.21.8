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
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.dto.ValueObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsRegionSelectionPreference
extends ValueObject
implements RealmsSerializable {
    final static public RealmsRegionSelectionPreference DEFAULT = new RealmsRegionSelectionPreference(RegionSelectionMethod.AUTOMATIC_OWNER, null);
    final static private Logger LOGGER = LogUtils.getLogger();
    @SerializedName(value="regionSelectionPreference")
    @JsonAdapter(value=RegionSelectionMethod.SelectionMethodTypeAdapter.class)
    public RegionSelectionMethod selectionMethod;
    @SerializedName(value="preferredRegion")
    @JsonAdapter(value=RealmsRegion.RegionTypeAdapter.class)
    @Nullable
    public RealmsRegion preferredRegion;

    public RealmsRegionSelectionPreference(RegionSelectionMethod selectionMethod, @Nullable RealmsRegion preferredRegion) {
        this.selectionMethod = selectionMethod;
        this.preferredRegion = preferredRegion;
    }

    private RealmsRegionSelectionPreference() {
    }

    public static RealmsRegionSelectionPreference parse(CheckedGson gson, String json) {
        try {
            RealmsRegionSelectionPreference realmsRegionSelectionPreference = gson.fromJson(json, RealmsRegionSelectionPreference.class);
            if (realmsRegionSelectionPreference == null) {
                LOGGER.error("Could not parse RegionSelectionPreference: {}", (Object)json);
                return new RealmsRegionSelectionPreference();
            }
            return realmsRegionSelectionPreference;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RegionSelectionPreference: {}", (Object)exception.getMessage());
            return new RealmsRegionSelectionPreference();
        }
    }

    public RealmsRegionSelectionPreference net_minecraft_client_realms_dto_RealmsRegionSelectionPreference_clone() {
        return new RealmsRegionSelectionPreference(this.selectionMethod, this.preferredRegion);
    }

    public Object java_lang_Object_clone() throws CloneNotSupportedException {
        return this.net_minecraft_client_realms_dto_RealmsRegionSelectionPreference_clone();
    }
}

