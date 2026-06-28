/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

public final class SpreadType
extends Enum<SpreadType>
implements StringIdentifiable {
    final static public SpreadType LINEAR = new SpreadType("linear");
    final static public SpreadType TRIANGULAR = new SpreadType("triangular");
    final static public Codec<SpreadType> CODEC;
    final private String name;
    final static private SpreadType[] field_36426;

    public static SpreadType[] values() {
        return (SpreadType[])field_36426.clone();
    }

    public static SpreadType valueOf(String string) {
        return Enum.valueOf(SpreadType.class, string);
    }

    private SpreadType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public int get(Random random, int bound) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> random.nextInt(bound);
            case 1 -> (random.nextInt(bound) + random.nextInt(bound)) / 2;
        };
    }

    private static SpreadType[] method_40175() {
        return new SpreadType[]{LINEAR, TRIANGULAR};
    }

    static {
        field_36426 = SpreadType.method_40175();
        CODEC = StringIdentifiable.createCodec(SpreadType::values);
    }
}

