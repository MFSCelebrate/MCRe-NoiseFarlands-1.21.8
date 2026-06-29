/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;

@Environment(value=EnvType.CLIENT)
public final class NavigationDirection
extends Enum<NavigationDirection> {
    final static public NavigationDirection UP = new NavigationDirection();
    final static public NavigationDirection DOWN = new NavigationDirection();
    final static public NavigationDirection LEFT = new NavigationDirection();
    final static public NavigationDirection RIGHT = new NavigationDirection();
    final private IntComparator comparator = (a, b) -> a == b ? 0 : (this.isBefore(a, b) ? -1 : 1);
    final static private NavigationDirection[] field_41831;

    public static NavigationDirection[] values() {
        return (NavigationDirection[])field_41831.clone();
    }

    public static NavigationDirection valueOf(String string) {
        return Enum.valueOf(NavigationDirection.class, string);
    }

    public NavigationAxis getAxis() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 1 -> NavigationAxis.VERTICAL;
            case 2, 3 -> NavigationAxis.HORIZONTAL;
        };
    }

    public NavigationDirection getOpposite() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> DOWN;
            case 1 -> UP;
            case 2 -> RIGHT;
            case 3 -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 2 -> false;
            case 1, 3 -> true;
        };
    }

    public boolean isAfter(int a, int b) {
        if (this.isPositive()) {
            return a > b;
        }
        return b > a;
    }

    public boolean isBefore(int a, int b) {
        if (this.isPositive()) {
            return a < b;
        }
        return b < a;
    }

    public IntComparator getComparator() {
        return this.comparator;
    }

    private static NavigationDirection[] method_48244() {
        return new NavigationDirection[]{UP, DOWN, LEFT, RIGHT};
    }

    static {
        field_41831 = NavigationDirection.method_48244();
    }
}

