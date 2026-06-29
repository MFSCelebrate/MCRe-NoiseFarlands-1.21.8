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
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.ValueObject;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldResetDto
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="seed")
    final private String seed;
    @SerializedName(value="worldTemplateId")
    final private long worldTemplateId;
    @SerializedName(value="levelType")
    final private int levelType;
    @SerializedName(value="generateStructures")
    final private boolean generateStructures;
    @SerializedName(value="experiments")
    final private Set<String> experiments;

    public RealmsWorldResetDto(String seed, long worldTemplateId, int levelType, boolean generateStructures, Set<String> experiments) {
        this.seed = seed;
        this.worldTemplateId = worldTemplateId;
        this.levelType = levelType;
        this.generateStructures = generateStructures;
        this.experiments = experiments;
    }
}

