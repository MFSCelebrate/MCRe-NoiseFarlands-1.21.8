/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public final class MovementType
extends Enum<MovementType> {
    final static public MovementType SELF = new MovementType();
    final static public MovementType PLAYER = new MovementType();
    final static public MovementType PISTON = new MovementType();
    final static public MovementType SHULKER_BOX = new MovementType();
    final static public MovementType SHULKER = new MovementType();
    final static private MovementType[] field_6307;

    public static MovementType[] values() {
        return (MovementType[])field_6307.clone();
    }

    public static MovementType valueOf(String string) {
        return Enum.valueOf(MovementType.class, string);
    }

    private static MovementType[] method_36611() {
        return new MovementType[]{SELF, PLAYER, PISTON, SHULKER_BOX, SHULKER};
    }

    static {
        field_6307 = MovementType.method_36611();
    }
}

