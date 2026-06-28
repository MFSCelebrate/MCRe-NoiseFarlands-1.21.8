/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.tick;

import com.mojang.serialization.Codec;

public final class TickPriority
extends Enum<TickPriority> {
    final static public TickPriority EXTREMELY_HIGH = new TickPriority(-3);
    final static public TickPriority VERY_HIGH = new TickPriority(-2);
    final static public TickPriority HIGH = new TickPriority(-1);
    final static public TickPriority NORMAL = new TickPriority(0);
    final static public TickPriority LOW = new TickPriority(1);
    final static public TickPriority VERY_LOW = new TickPriority(2);
    final static public TickPriority EXTREMELY_LOW = new TickPriority(3);
    final static public Codec<TickPriority> CODEC;
    final private int index;
    final static private TickPriority[] field_9312;

    public static TickPriority[] values() {
        return (TickPriority[])field_9312.clone();
    }

    public static TickPriority valueOf(String string) {
        return Enum.valueOf(TickPriority.class, string);
    }

    private TickPriority(int index) {
        this.index = index;
    }

    public static TickPriority byIndex(int index) {
        for (TickPriority tickPriority : TickPriority.values()) {
            if (tickPriority.index != index) continue;
            return tickPriority;
        }
        if (index < TickPriority.EXTREMELY_HIGH.index) {
            return EXTREMELY_HIGH;
        }
        return EXTREMELY_LOW;
    }

    public int getIndex() {
        return this.index;
    }

    private static TickPriority[] method_36697() {
        return new TickPriority[]{EXTREMELY_HIGH, VERY_HIGH, HIGH, NORMAL, LOW, VERY_LOW, EXTREMELY_LOW};
    }

    static {
        field_9312 = TickPriority.method_36697();
        CODEC = Codec.INT.xmap(TickPriority::byIndex, TickPriority::getIndex);
    }
}

