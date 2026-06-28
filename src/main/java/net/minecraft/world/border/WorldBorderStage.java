/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.border;

public final class WorldBorderStage
extends Enum<WorldBorderStage> {
    final static public WorldBorderStage GROWING = new WorldBorderStage(4259712);
    final static public WorldBorderStage SHRINKING = new WorldBorderStage(0xFF3030);
    final static public WorldBorderStage STATIONARY = new WorldBorderStage(2138367);
    final private int color;
    final static private WorldBorderStage[] field_12752;

    public static WorldBorderStage[] values() {
        return (WorldBorderStage[])field_12752.clone();
    }

    public static WorldBorderStage valueOf(String string) {
        return Enum.valueOf(WorldBorderStage.class, string);
    }

    private WorldBorderStage(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    private static WorldBorderStage[] method_36740() {
        return new WorldBorderStage[]{GROWING, SHRINKING, STATIONARY};
    }

    static {
        field_12752 = WorldBorderStage.method_36740();
    }
}

