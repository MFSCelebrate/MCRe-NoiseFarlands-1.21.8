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

@Environment(value=EnvType.CLIENT)
public record RealmsSettingDto(@SerializedName(value="name") String name, @SerializedName(value="value") String value) implements RealmsSerializable
{
    public static RealmsSettingDto ofHardcore(boolean hardcore) {
        return new RealmsSettingDto("hardcore", Boolean.toString(hardcore));
    }

    public static boolean isHardcore(List<RealmsSettingDto> settings) {
        for (RealmsSettingDto realmsSettingDto : settings) {
            if (!realmsSettingDto.name().equals("hardcore")) continue;
            return Boolean.parseBoolean(realmsSettingDto.value());
        }
        return false;
    }
}

