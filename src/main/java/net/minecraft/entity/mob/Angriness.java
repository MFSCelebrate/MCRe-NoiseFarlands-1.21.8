/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.Arrays;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

public final class Angriness
extends Enum<Angriness> {
    final static public Angriness CALM = new Angriness(0, SoundEvents.ENTITY_WARDEN_AMBIENT, SoundEvents.ENTITY_WARDEN_LISTENING);
    final static public Angriness AGITATED = new Angriness(40, SoundEvents.ENTITY_WARDEN_AGITATED, SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY);
    final static public Angriness ANGRY = new Angriness(80, SoundEvents.ENTITY_WARDEN_ANGRY, SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY);
    final static private Angriness[] VALUES;
    final private int threshold;
    final private SoundEvent sound;
    final private SoundEvent listeningSound;
    final static private Angriness[] field_38126;

    public static Angriness[] values() {
        return (Angriness[])field_38126.clone();
    }

    public static Angriness valueOf(String string) {
        return Enum.valueOf(Angriness.class, string);
    }

    private Angriness(int threshold, SoundEvent sound, SoundEvent listeningSound) {
        this.threshold = threshold;
        this.sound = sound;
        this.listeningSound = listeningSound;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public SoundEvent getListeningSound() {
        return this.listeningSound;
    }

    public static Angriness getForAnger(int anger) {
        for (Angriness angriness : VALUES) {
            if (anger < angriness.threshold) continue;
            return angriness;
        }
        return CALM;
    }

    public boolean isAngry() {
        return this == ANGRY;
    }

    private static Angriness[] method_42175() {
        return new Angriness[]{CALM, AGITATED, ANGRY};
    }

    static {
        field_38126 = Angriness.method_42175();
        VALUES = Util.make(Angriness.values(), values -> Arrays.sort(values, (a, b) -> Integer.compare(b.threshold, a.threshold)));
    }
}

