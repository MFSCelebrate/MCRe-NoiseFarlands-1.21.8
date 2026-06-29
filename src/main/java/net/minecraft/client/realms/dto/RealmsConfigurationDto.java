/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsDescriptionDto;
import net.minecraft.client.realms.dto.RealmsOptionsDto;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record RealmsConfigurationDto(@SerializedName(value="options") RealmsOptionsDto options, @SerializedName(value="settings") List<RealmsSettingDto> settings, @Nullable @SerializedName(value="regionSelectionPreference") RealmsRegionSelectionPreference regionSelectionPreference, @Nullable @SerializedName(value="description") RealmsDescriptionDto description) implements RealmsSerializable
{
}

