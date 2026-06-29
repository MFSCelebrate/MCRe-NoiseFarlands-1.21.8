/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public class WorldSavePath {
    final static public WorldSavePath ADVANCEMENTS = new WorldSavePath("advancements");
    final static public WorldSavePath STATS = new WorldSavePath("stats");
    final static public WorldSavePath PLAYERDATA = new WorldSavePath("playerdata");
    final static public WorldSavePath PLAYERS = new WorldSavePath("players");
    final static public WorldSavePath LEVEL_DAT = new WorldSavePath("level.dat");
    final static public WorldSavePath LEVEL_DAT_OLD = new WorldSavePath("level.dat_old");
    final static public WorldSavePath ICON_PNG = new WorldSavePath("icon.png");
    final static public WorldSavePath SESSION_LOCK = new WorldSavePath("session.lock");
    final static public WorldSavePath GENERATED = new WorldSavePath("generated");
    final static public WorldSavePath DATAPACKS = new WorldSavePath("datapacks");
    final static public WorldSavePath RESOURCES_ZIP = new WorldSavePath("resources.zip");
    final static public WorldSavePath ROOT = new WorldSavePath(".");
    final private String relativePath;

    private WorldSavePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public String toString() {
        return "/" + this.relativePath;
    }
}

