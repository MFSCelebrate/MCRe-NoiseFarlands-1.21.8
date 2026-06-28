/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public final class CubeFace
extends Enum<CubeFace> {
    final static public CubeFace DOWN = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH));
    final static public CubeFace UP = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH));
    final static public CubeFace NORTH = new CubeFace(new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH));
    final static public CubeFace SOUTH = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH));
    final static public CubeFace WEST = new CubeFace(new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.WEST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.WEST, DirectionIds.UP, DirectionIds.SOUTH));
    final static public CubeFace EAST = new CubeFace(new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.SOUTH), new Corner(DirectionIds.EAST, DirectionIds.DOWN, DirectionIds.NORTH), new Corner(DirectionIds.EAST, DirectionIds.UP, DirectionIds.NORTH));
    final static private CubeFace[] DIRECTION_LOOKUP;
    final private Corner[] corners;
    final static private CubeFace[] field_3964;

    public static CubeFace[] values() {
        return (CubeFace[])field_3964.clone();
    }

    public static CubeFace valueOf(String string) {
        return Enum.valueOf(CubeFace.class, string);
    }

    public static CubeFace getFace(Direction direction) {
        return DIRECTION_LOOKUP[direction.getIndex()];
    }

    private CubeFace(Corner ... corners) {
        this.corners = corners;
    }

    public Corner getCorner(int corner) {
        return this.corners[corner];
    }

    private static CubeFace[] method_36913() {
        return new CubeFace[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
    }

    static {
        field_3964 = CubeFace.method_36913();
        DIRECTION_LOOKUP = Util.make(new CubeFace[6], lookup -> {
            lookup[DirectionIds.DOWN] = DOWN;
            lookup[DirectionIds.UP] = UP;
            lookup[DirectionIds.NORTH] = NORTH;
            lookup[DirectionIds.SOUTH] = SOUTH;
            lookup[DirectionIds.WEST] = WEST;
            lookup[DirectionIds.EAST] = EAST;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class Corner {
        final public int xSide;
        final public int ySide;
        final public int zSide;

        Corner(int xSide, int ySide, int zSide) {
            this.xSide = xSide;
            this.ySide = ySide;
            this.zSide = zSide;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DirectionIds {
        final static public int SOUTH = Direction.SOUTH.getIndex();
        final static public int UP = Direction.UP.getIndex();
        final static public int EAST = Direction.EAST.getIndex();
        final static public int NORTH = Direction.NORTH.getIndex();
        final static public int DOWN = Direction.DOWN.getIndex();
        final static public int WEST = Direction.WEST.getIndex();
    }
}

