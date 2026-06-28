/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.world.level.storage;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.minecraft.SaveVersion;
import net.minecraft.SharedConstants;

public class SaveVersionInfo {
    final private int levelFormatVersion;
    final private long lastPlayed;
    final private String versionName;
    final private SaveVersion version;
    final private boolean stable;

    private SaveVersionInfo(int levelFormatVersion, long lastPlayed, String versionName, int versionId, String series, boolean stable) {
        this.levelFormatVersion = levelFormatVersion;
        this.lastPlayed = lastPlayed;
        this.versionName = versionName;
        this.version = new SaveVersion(versionId, series);
        this.stable = stable;
    }

    public static SaveVersionInfo fromDynamic(Dynamic<?> dynamic) {
        int i = dynamic.get("version").asInt(0);
        long l = dynamic.get("LastPlayed").asLong(0L);
        OptionalDynamic optionalDynamic = dynamic.get("Version");
        if (optionalDynamic.result().isPresent()) {
            return new SaveVersionInfo(12, l, optionalDynamic.get("Name").asString(SharedConstants.getGameVersion().name()), optionalDynamic.get("Id").asInt(SharedConstants.getGameVersion().dataVersion().id()), optionalDynamic.get("Series").asString("main"), optionalDynamic.get("Snapshot").asBoolean(!SharedConstants.getGameVersion().stable()));
        }
        return new SaveVersionInfo(12, l, "", 0, "main", false);
    }

    public int getLevelFormatVersion() {
        return this.levelFormatVersion;
    }

    public long getLastPlayed() {
        return this.lastPlayed;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public SaveVersion getVersion() {
        return this.version;
    }

    public boolean isStable() {
        return this.stable;
    }
}

