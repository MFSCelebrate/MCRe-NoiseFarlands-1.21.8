/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiler;

public final class SampleType
extends Enum<SampleType> {
    final static public SampleType PATH_FINDING = new SampleType("pathfinding");
    final static public SampleType EVENT_LOOPS = new SampleType("event-loops");
    final static public SampleType CONSECUTIVE_EXECUTORS = new SampleType("consecutive-executors");
    final static public SampleType TICK_LOOP = new SampleType("ticking");
    final static public SampleType JVM = new SampleType("jvm");
    final static public SampleType CHUNK_RENDERING = new SampleType("chunk rendering");
    final static public SampleType CHUNK_RENDERING_DISPATCHING = new SampleType("chunk rendering dispatching");
    final static public SampleType CPU = new SampleType("cpu");
    final static public SampleType GPU = new SampleType("gpu");
    final private String name;
    final static private SampleType[] field_29554;

    public static SampleType[] values() {
        return (SampleType[])field_29554.clone();
    }

    public static SampleType valueOf(String string) {
        return Enum.valueOf(SampleType.class, string);
    }

    private SampleType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static SampleType[] method_36594() {
        return new SampleType[]{PATH_FINDING, EVENT_LOOPS, CONSECUTIVE_EXECUTORS, TICK_LOOP, JVM, CHUNK_RENDERING, CHUNK_RENDERING_DISPATCHING, CPU, GPU};
    }

    static {
        field_29554 = SampleType.method_36594();
    }
}

