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
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.StringHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldOptions
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="pvp")
    public boolean pvp = true;
    @SerializedName(value="spawnMonsters")
    public boolean spawnMonsters = true;
    @SerializedName(value="spawnProtection")
    public int spawnProtection = 0;
    @SerializedName(value="commandBlocks")
    public boolean commandBlocks = false;
    @SerializedName(value="forceGameMode")
    public boolean forceGameMode = false;
    @SerializedName(value="difficulty")
    public int difficulty = 2;
    @SerializedName(value="gameMode")
    public int gameMode = 0;
    @SerializedName(value="slotName")
    private String slotName = "";
    @SerializedName(value="version")
    public String version = "";
    @SerializedName(value="compatibility")
    public RealmsServer.Compatibility compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
    @SerializedName(value="worldTemplateId")
    public long templateId = -1L;
    @Nullable
    @SerializedName(value="worldTemplateImage")
    public String templateImage = null;
    public boolean empty;

    private RealmsWorldOptions() {
    }

    public RealmsWorldOptions(boolean pvp, boolean spawnAnimals, int spawnProtection, boolean commandBlocks, int difficulty, int gameMode, boolean hardcore, String slotName, String version, RealmsServer.Compatibility compatibility) {
        this.pvp = pvp;
        this.spawnMonsters = spawnAnimals;
        this.spawnProtection = spawnProtection;
        this.commandBlocks = commandBlocks;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.forceGameMode = hardcore;
        this.slotName = slotName;
        this.version = version;
        this.compatibility = compatibility;
    }

    public static RealmsWorldOptions getDefaults() {
        return new RealmsWorldOptions();
    }

    public static RealmsWorldOptions create(GameMode gameMode, boolean commandBlocks, Difficulty difficulty, boolean hardcore, String version, String slotName) {
        RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.getDefaults();
        realmsWorldOptions.commandBlocks = commandBlocks;
        realmsWorldOptions.difficulty = difficulty.getId();
        realmsWorldOptions.gameMode = gameMode.getIndex();
        realmsWorldOptions.slotName = slotName;
        realmsWorldOptions.version = version;
        return realmsWorldOptions;
    }

    public static RealmsWorldOptions create(LevelInfo levelInfo, boolean commandBlocks, String version) {
        return RealmsWorldOptions.create(levelInfo.getGameMode(), commandBlocks, levelInfo.getDifficulty(), levelInfo.isHardcore(), version, levelInfo.getLevelName());
    }

    public static RealmsWorldOptions getEmptyDefaults() {
        RealmsWorldOptions realmsWorldOptions = RealmsWorldOptions.getDefaults();
        realmsWorldOptions.setEmpty(true);
        return realmsWorldOptions;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public static RealmsWorldOptions fromJson(CheckedGson gson, String json) {
        RealmsWorldOptions realmsWorldOptions = gson.fromJson(json, RealmsWorldOptions.class);
        if (realmsWorldOptions == null) {
            return RealmsWorldOptions.getDefaults();
        }
        RealmsWorldOptions.replaceNullsWithDefaults(realmsWorldOptions);
        return realmsWorldOptions;
    }

    private static void replaceNullsWithDefaults(RealmsWorldOptions options) {
        if (options.slotName == null) {
            options.slotName = "";
        }
        if (options.version == null) {
            options.version = "";
        }
        if (options.compatibility == null) {
            options.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
        }
    }

    public String getSlotName(int index) {
        if (StringHelper.isBlank(this.slotName)) {
            if (this.empty) {
                return I18n.translate("mco.configure.world.slot.empty", new Object[0]);
            }
            return this.getDefaultSlotName(index);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int index) {
        return I18n.translate("mco.configure.world.slot", index);
    }

    public RealmsWorldOptions net_minecraft_client_realms_dto_RealmsWorldOptions_clone() {
        return new RealmsWorldOptions(this.pvp, this.spawnMonsters, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName, this.version, this.compatibility);
    }

    public Object java_lang_Object_clone() throws CloneNotSupportedException {
        return this.net_minecraft_client_realms_dto_RealmsWorldOptions_clone();
    }
}

