/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.pathing;

public final class PathNodeType
extends Enum<PathNodeType> {
    final static public PathNodeType BLOCKED = new PathNodeType(-1.0f);
    final static public PathNodeType OPEN = new PathNodeType(0.0f);
    final static public PathNodeType WALKABLE = new PathNodeType(0.0f);
    final static public PathNodeType WALKABLE_DOOR = new PathNodeType(0.0f);
    final static public PathNodeType TRAPDOOR = new PathNodeType(0.0f);
    final static public PathNodeType POWDER_SNOW = new PathNodeType(-1.0f);
    final static public PathNodeType DANGER_POWDER_SNOW = new PathNodeType(0.0f);
    final static public PathNodeType FENCE = new PathNodeType(-1.0f);
    final static public PathNodeType LAVA = new PathNodeType(-1.0f);
    final static public PathNodeType WATER = new PathNodeType(8.0f);
    final static public PathNodeType WATER_BORDER = new PathNodeType(8.0f);
    final static public PathNodeType RAIL = new PathNodeType(0.0f);
    final static public PathNodeType UNPASSABLE_RAIL = new PathNodeType(-1.0f);
    final static public PathNodeType DANGER_FIRE = new PathNodeType(8.0f);
    final static public PathNodeType DAMAGE_FIRE = new PathNodeType(16.0f);
    final static public PathNodeType DANGER_OTHER = new PathNodeType(8.0f);
    final static public PathNodeType DAMAGE_OTHER = new PathNodeType(-1.0f);
    final static public PathNodeType DOOR_OPEN = new PathNodeType(0.0f);
    final static public PathNodeType DOOR_WOOD_CLOSED = new PathNodeType(-1.0f);
    final static public PathNodeType DOOR_IRON_CLOSED = new PathNodeType(-1.0f);
    final static public PathNodeType BREACH = new PathNodeType(4.0f);
    final static public PathNodeType LEAVES = new PathNodeType(-1.0f);
    final static public PathNodeType STICKY_HONEY = new PathNodeType(8.0f);
    final static public PathNodeType COCOA = new PathNodeType(0.0f);
    final static public PathNodeType DAMAGE_CAUTIOUS = new PathNodeType(0.0f);
    final static public PathNodeType DANGER_TRAPDOOR = new PathNodeType(0.0f);
    final private float defaultPenalty;
    final static private PathNodeType[] field_24;

    public static PathNodeType[] values() {
        return (PathNodeType[])field_24.clone();
    }

    public static PathNodeType valueOf(String string) {
        return Enum.valueOf(PathNodeType.class, string);
    }

    private PathNodeType(float defaultPenalty) {
        this.defaultPenalty = defaultPenalty;
    }

    public float getDefaultPenalty() {
        return this.defaultPenalty;
    }

    private static PathNodeType[] method_36788() {
        return new PathNodeType[]{BLOCKED, OPEN, WALKABLE, WALKABLE_DOOR, TRAPDOOR, POWDER_SNOW, DANGER_POWDER_SNOW, FENCE, LAVA, WATER, WATER_BORDER, RAIL, UNPASSABLE_RAIL, DANGER_FIRE, DAMAGE_FIRE, DANGER_OTHER, DAMAGE_OTHER, DOOR_OPEN, DOOR_WOOD_CLOSED, DOOR_IRON_CLOSED, BREACH, LEAVES, STICKY_HONEY, COCOA, DAMAGE_CAUTIOUS, DANGER_TRAPDOOR};
    }

    static {
        field_24 = PathNodeType.method_36788();
    }
}

