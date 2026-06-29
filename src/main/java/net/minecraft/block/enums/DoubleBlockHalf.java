/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public final class DoubleBlockHalf
extends Enum<DoubleBlockHalf>
implements StringIdentifiable {
    final static public DoubleBlockHalf UPPER = new DoubleBlockHalf(Direction.DOWN);
    final static public DoubleBlockHalf LOWER = new DoubleBlockHalf(Direction.UP);
    final private Direction oppositeDirection;
    final static private DoubleBlockHalf[] field_12608;

    public static DoubleBlockHalf[] values() {
        return (DoubleBlockHalf[])field_12608.clone();
    }

    public static DoubleBlockHalf valueOf(String string) {
        return Enum.valueOf(DoubleBlockHalf.class, string);
    }

    private DoubleBlockHalf(Direction oppositeDirection) {
        this.oppositeDirection = oppositeDirection;
    }

    public Direction getOppositeDirection() {
        return this.oppositeDirection;
    }

    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return this == UPPER ? "upper" : "lower";
    }

    public DoubleBlockHalf getOtherHalf() {
        return this == UPPER ? LOWER : UPPER;
    }

    private static DoubleBlockHalf[] method_36727() {
        return new DoubleBlockHalf[]{UPPER, LOWER};
    }

    static {
        field_12608 = DoubleBlockHalf.method_36727();
    }
}

