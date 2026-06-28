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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class RealmsOptionsDto
implements RealmsSerializable {
    @SerializedName(value="slotId")
    final public int slotId;
    @SerializedName(value="pvp")
    final private boolean pvp;
    @SerializedName(value="spawnMonsters")
    final private boolean spawnMonsters;
    @SerializedName(value="spawnProtection")
    final private int spawnProtection;
    @SerializedName(value="commandBlocks")
    final private boolean commandBlocks;
    @SerializedName(value="forceGameMode")
    final private boolean forceGameMode;
    @SerializedName(value="difficulty")
    final private int difficulty;
    @SerializedName(value="gameMode")
    final private int gameMode;
    @SerializedName(value="slotName")
    final private String slotName;
    @SerializedName(value="version")
    final private String version;
    @SerializedName(value="compatibility")
    final private RealmsServer.Compatibility compatibility;
    @SerializedName(value="worldTemplateId")
    final private long worldTemplateId;
    @Nullable
    @SerializedName(value="worldTemplateImage")
    final private String worldTemplateImage;
    @SerializedName(value="hardcore")
    final private boolean hardcore;

    public RealmsOptionsDto(int slotId, RealmsWorldOptions options, boolean hardcore) {
        this.slotId = slotId;
        this.pvp = options.pvp;
        this.spawnMonsters = options.spawnMonsters;
        this.spawnProtection = options.spawnProtection;
        this.commandBlocks = options.commandBlocks;
        this.forceGameMode = options.forceGameMode;
        this.difficulty = options.difficulty;
        this.gameMode = options.gameMode;
        this.slotName = options.getSlotName(slotId);
        this.version = options.version;
        this.compatibility = options.compatibility;
        this.worldTemplateId = options.templateId;
        this.worldTemplateImage = options.templateImage;
        this.hardcore = hardcore;
    }
}

