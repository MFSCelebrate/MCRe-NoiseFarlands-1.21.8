/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public final class VerticalSurfaceType
extends Enum<VerticalSurfaceType>
implements StringIdentifiable {
    final static public VerticalSurfaceType CEILING = new VerticalSurfaceType(Direction.UP, 1, "ceiling");
    final static public VerticalSurfaceType FLOOR = new VerticalSurfaceType(Direction.DOWN, -1, "floor");
    final static public Codec<VerticalSurfaceType> CODEC;
    final private Direction direction;
    final private int offset;
    final private String name;
    final static private VerticalSurfaceType[] field_29320;

    public static VerticalSurfaceType[] values() {
        return (VerticalSurfaceType[])field_29320.clone();
    }

    public static VerticalSurfaceType valueOf(String string) {
        return Enum.valueOf(VerticalSurfaceType.class, string);
    }

    private VerticalSurfaceType(Direction direction, int offset, String name) {
        this.direction = direction;
        this.offset = offset;
        this.name = name;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getOffset() {
        return this.offset;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static VerticalSurfaceType[] method_36759() {
        return new VerticalSurfaceType[]{CEILING, FLOOR};
    }

    static {
        field_29320 = VerticalSurfaceType.method_36759();
        CODEC = StringIdentifiable.createCodec(VerticalSurfaceType::values);
    }
}

