/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

final class ChaseBoatState
extends Enum<ChaseBoatState> {
    final static public ChaseBoatState GO_TO_BOAT = new ChaseBoatState();
    final static public ChaseBoatState GO_IN_BOAT_DIRECTION = new ChaseBoatState();
    final static private ChaseBoatState[] field_6399;

    public static ChaseBoatState[] values() {
        return (ChaseBoatState[])field_6399.clone();
    }

    public static ChaseBoatState valueOf(String string) {
        return Enum.valueOf(ChaseBoatState.class, string);
    }

    private static ChaseBoatState[] method_36620() {
        return new ChaseBoatState[]{GO_TO_BOAT, GO_IN_BOAT_DIRECTION};
    }

    static {
        field_6399 = ChaseBoatState.method_36620();
    }
}

