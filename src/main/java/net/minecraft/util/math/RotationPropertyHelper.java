/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import java.util.Optional;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationCalculator;

public class RotationPropertyHelper {
    final static private RotationCalculator CALCULATOR = new RotationCalculator(4);
    final static private int MAX = CALCULATOR.getMax();
    final static private int NORTH = 0;
    final static private int EAST = 4;
    final static private int SOUTH = 8;
    final static private int WEST = 12;

    public static int getMax() {
        return MAX;
    }

    public static int fromDirection(Direction direction) {
        return CALCULATOR.toRotation(direction);
    }

    public static int fromYaw(float yaw) {
        return CALCULATOR.toClampedRotation(yaw);
    }

    public static Optional<Direction> toDirection(int rotation) {
        Direction direction = switch (rotation) {
            case 0 -> Direction.NORTH;
            case 4 -> Direction.EAST;
            case 8 -> Direction.SOUTH;
            case 12 -> Direction.WEST;
            default -> null;
        };
        return Optional.ofNullable(direction);
    }

    public static float toDegrees(int rotation) {
        return CALCULATOR.toWrappedDegrees(rotation);
    }
}

