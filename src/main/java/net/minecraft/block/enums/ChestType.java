/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public final class ChestType
extends Enum<ChestType>
implements StringIdentifiable {
    final static public ChestType SINGLE = new ChestType("single");
    final static public ChestType LEFT = new ChestType("left");
    final static public ChestType RIGHT = new ChestType("right");
    final private String name;
    final static private ChestType[] field_12573;

    public static ChestType[] values() {
        return (ChestType[])field_12573.clone();
    }

    public static ChestType valueOf(String string) {
        return Enum.valueOf(ChestType.class, string);
    }

    private ChestType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public ChestType getOpposite() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> SINGLE;
            case 1 -> RIGHT;
            case 2 -> LEFT;
        };
    }

    private static ChestType[] method_36724() {
        return new ChestType[]{SINGLE, LEFT, RIGHT};
    }

    static {
        field_12573 = ChestType.method_36724();
    }
}

