/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.damage;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public final class DeathMessageType
extends Enum<DeathMessageType>
implements StringIdentifiable {
    final static public DeathMessageType DEFAULT = new DeathMessageType("default");
    final static public DeathMessageType FALL_VARIANTS = new DeathMessageType("fall_variants");
    final static public DeathMessageType INTENTIONAL_GAME_DESIGN = new DeathMessageType("intentional_game_design");
    final static public Codec<DeathMessageType> CODEC;
    final private String id;
    final static private DeathMessageType[] field_42366;

    public static DeathMessageType[] values() {
        return (DeathMessageType[])field_42366.clone();
    }

    public static DeathMessageType valueOf(String string) {
        return Enum.valueOf(DeathMessageType.class, string);
    }

    private DeathMessageType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static DeathMessageType[] method_48840() {
        return new DeathMessageType[]{DEFAULT, FALL_VARIANTS, INTENTIONAL_GAME_DESIGN};
    }

    static {
        field_42366 = DeathMessageType.method_48840();
        CODEC = StringIdentifiable.createCodec(DeathMessageType::values);
    }
}

