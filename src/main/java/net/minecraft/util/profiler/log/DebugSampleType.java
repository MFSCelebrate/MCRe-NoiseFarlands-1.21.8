/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiler.log;

public final class DebugSampleType
extends Enum<DebugSampleType> {
    final static public DebugSampleType TICK_TIME = new DebugSampleType();
    final static private DebugSampleType[] field_48818;

    public static DebugSampleType[] values() {
        return (DebugSampleType[])field_48818.clone();
    }

    public static DebugSampleType valueOf(String string) {
        return Enum.valueOf(DebugSampleType.class, string);
    }

    private static DebugSampleType[] method_56665() {
        return new DebugSampleType[]{TICK_TIME};
    }

    static {
        field_48818 = DebugSampleType.method_56665();
    }
}

