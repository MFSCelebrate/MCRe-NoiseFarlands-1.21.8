/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.damage;

import com.mojang.serialization.Codec;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;

public final class DamageEffects
extends Enum<DamageEffects>
implements StringIdentifiable {
    final static public DamageEffects HURT = new DamageEffects("hurt", SoundEvents.ENTITY_PLAYER_HURT);
    final static public DamageEffects THORNS = new DamageEffects("thorns", SoundEvents.ENTITY_PLAYER_HURT);
    final static public DamageEffects DROWNING = new DamageEffects("drowning", SoundEvents.ENTITY_PLAYER_HURT_DROWN);
    final static public DamageEffects BURNING = new DamageEffects("burning", SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE);
    final static public DamageEffects POKING = new DamageEffects("poking", SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH);
    final static public DamageEffects FREEZING = new DamageEffects("freezing", SoundEvents.ENTITY_PLAYER_HURT_FREEZE);
    final static public Codec<DamageEffects> CODEC;
    final private String id;
    final private SoundEvent sound;
    final static private DamageEffects[] field_42284;

    public static DamageEffects[] values() {
        return (DamageEffects[])field_42284.clone();
    }

    public static DamageEffects valueOf(String string) {
        return Enum.valueOf(DamageEffects.class, string);
    }

    private DamageEffects(String id, SoundEvent sound) {
        this.id = id;
        this.sound = sound;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    private static DamageEffects[] method_48787() {
        return new DamageEffects[]{HURT, THORNS, DROWNING, BURNING, POKING, FREEZING};
    }

    static {
        field_42284 = DamageEffects.method_48787();
        CODEC = StringIdentifiable.createCodec(DamageEffects::values);
    }
}

