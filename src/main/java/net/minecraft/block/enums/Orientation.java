/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public final class Orientation
extends Enum<Orientation>
implements StringIdentifiable {
    final static public Orientation DOWN_EAST = new Orientation("down_east", Direction.DOWN, Direction.EAST);
    final static public Orientation DOWN_NORTH = new Orientation("down_north", Direction.DOWN, Direction.NORTH);
    final static public Orientation DOWN_SOUTH = new Orientation("down_south", Direction.DOWN, Direction.SOUTH);
    final static public Orientation DOWN_WEST = new Orientation("down_west", Direction.DOWN, Direction.WEST);
    final static public Orientation UP_EAST = new Orientation("up_east", Direction.UP, Direction.EAST);
    final static public Orientation UP_NORTH = new Orientation("up_north", Direction.UP, Direction.NORTH);
    final static public Orientation UP_SOUTH = new Orientation("up_south", Direction.UP, Direction.SOUTH);
    final static public Orientation UP_WEST = new Orientation("up_west", Direction.UP, Direction.WEST);
    final static public Orientation WEST_UP = new Orientation("west_up", Direction.WEST, Direction.UP);
    final static public Orientation EAST_UP = new Orientation("east_up", Direction.EAST, Direction.UP);
    final static public Orientation NORTH_UP = new Orientation("north_up", Direction.NORTH, Direction.UP);
    final static public Orientation SOUTH_UP = new Orientation("south_up", Direction.SOUTH, Direction.UP);
    final static private int DIRECTIONS;
    final static private Orientation[] VALUES;
    final private String name;
    final private Direction rotation;
    final private Direction facing;
    final static private Orientation[] field_23397;

    public static Orientation[] values() {
        return (Orientation[])field_23397.clone();
    }

    public static Orientation valueOf(String string) {
        return Enum.valueOf(Orientation.class, string);
    }

    private static int getIndex(Direction facing, Direction rotation) {
        return facing.ordinal() * DIRECTIONS + rotation.ordinal();
    }

    private Orientation(String name, Direction facing, Direction rotation) {
        this.name = name;
        this.facing = facing;
        this.rotation = rotation;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static Orientation byDirections(Direction facing, Direction rotation) {
        return VALUES[Orientation.getIndex(facing, rotation)];
    }

    public Direction getFacing() {
        return this.facing;
    }

    public Direction getRotation() {
        return this.rotation;
    }

    private static Orientation[] method_36936() {
        return new Orientation[]{DOWN_EAST, DOWN_NORTH, DOWN_SOUTH, DOWN_WEST, UP_EAST, UP_NORTH, UP_SOUTH, UP_WEST, WEST_UP, EAST_UP, NORTH_UP, SOUTH_UP};
    }

    static {
        field_23397 = Orientation.method_36936();
        DIRECTIONS = Direction.values().length;
        VALUES = Util.make(new Orientation[DIRECTIONS * DIRECTIONS], values -> {
            Orientation[] orientationArray = Orientation.values();
            int n = orientationArray.length;
            for (int i = 0; i < n; ++i) {
                Orientation orientation;
                values[Orientation.getIndex((Direction)orientation.facing, (Direction)orientation.rotation)] = orientation = orientationArray[i];
            }
        });
    }
}

