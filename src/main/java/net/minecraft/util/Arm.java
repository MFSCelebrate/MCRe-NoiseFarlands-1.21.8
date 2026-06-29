/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public final class Arm
extends Enum<Arm>
implements TranslatableOption,
StringIdentifiable {
    final static public Arm LEFT = new Arm(0, "left", "options.mainHand.left");
    final static public Arm RIGHT = new Arm(1, "right", "options.mainHand.right");
    final static public Codec<Arm> CODEC;
    final static public IntFunction<Arm> BY_ID;
    final private int id;
    final private String name;
    final private String translationKey;
    final static private Arm[] field_6180;

    public static Arm[] values() {
        return (Arm[])field_6180.clone();
    }

    public static Arm valueOf(String string) {
        return Enum.valueOf(Arm.class, string);
    }

    private Arm(int id, String name, String translationKey) {
        this.id = id;
        this.name = name;
        this.translationKey = translationKey;
    }

    public Arm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static Arm[] method_36606() {
        return new Arm[]{LEFT, RIGHT};
    }

    static {
        field_6180 = Arm.method_36606();
        CODEC = StringIdentifiable.createCodec(Arm::values);
        BY_ID = ValueLists.createIndexToValueFunction(Arm::getId, Arm.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}

