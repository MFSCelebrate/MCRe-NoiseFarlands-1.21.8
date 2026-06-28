/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.util.math;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public final class EightWayDirection
extends Enum<EightWayDirection> {
    final static public EightWayDirection NORTH = new EightWayDirection(Direction.NORTH);
    final static public EightWayDirection NORTH_EAST = new EightWayDirection(Direction.NORTH, Direction.EAST);
    final static public EightWayDirection EAST = new EightWayDirection(Direction.EAST);
    final static public EightWayDirection SOUTH_EAST = new EightWayDirection(Direction.SOUTH, Direction.EAST);
    final static public EightWayDirection SOUTH = new EightWayDirection(Direction.SOUTH);
    final static public EightWayDirection SOUTH_WEST = new EightWayDirection(Direction.SOUTH, Direction.WEST);
    final static public EightWayDirection WEST = new EightWayDirection(Direction.WEST);
    final static public EightWayDirection NORTH_WEST = new EightWayDirection(Direction.NORTH, Direction.WEST);
    final private Set<Direction> directions;
    final private Vec3i offset;
    final static private EightWayDirection[] field_11071;

    public static EightWayDirection[] values() {
        return (EightWayDirection[])field_11071.clone();
    }

    public static EightWayDirection valueOf(String string) {
        return Enum.valueOf(EightWayDirection.class, string);
    }

    private EightWayDirection(Direction ... directions) {
        this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
        this.offset = new Vec3i(0, 0, 0);
        for (Direction direction : directions) {
            this.offset.net_minecraft_util_math_Vec3i_setX(this.offset.getX() + direction.getOffsetX()).net_minecraft_util_math_Vec3i_setY(this.offset.getY() + direction.getOffsetY()).net_minecraft_util_math_Vec3i_setZ(this.offset.getZ() + direction.getOffsetZ());
        }
    }

    public Set<Direction> getDirections() {
        return this.directions;
    }

    public int getOffsetX() {
        return this.offset.getX();
    }

    public int getOffsetZ() {
        return this.offset.getZ();
    }

    private static EightWayDirection[] method_36935() {
        return new EightWayDirection[]{NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
    }

    static {
        field_11071 = EightWayDirection.method_36935();
    }
}

