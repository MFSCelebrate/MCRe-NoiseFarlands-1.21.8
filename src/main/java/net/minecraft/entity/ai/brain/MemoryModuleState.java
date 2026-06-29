/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain;

public final class MemoryModuleState
extends Enum<MemoryModuleState> {
    final static public MemoryModuleState VALUE_PRESENT = new MemoryModuleState();
    final static public MemoryModuleState VALUE_ABSENT = new MemoryModuleState();
    final static public MemoryModuleState REGISTERED = new MemoryModuleState();
    final static private MemoryModuleState[] field_18459;

    public static MemoryModuleState[] values() {
        return (MemoryModuleState[])field_18459.clone();
    }

    public static MemoryModuleState valueOf(String string) {
        return Enum.valueOf(MemoryModuleState.class, string);
    }

    private static MemoryModuleState[] method_36624() {
        return new MemoryModuleState[]{VALUE_PRESENT, VALUE_ABSENT, REGISTERED};
    }

    static {
        field_18459 = MemoryModuleState.method_36624();
    }
}

