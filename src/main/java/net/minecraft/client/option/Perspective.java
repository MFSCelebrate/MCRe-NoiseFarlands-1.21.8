/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Perspective
extends Enum<Perspective> {
    final static public Perspective FIRST_PERSON = new Perspective(true, false);
    final static public Perspective THIRD_PERSON_BACK = new Perspective(false, false);
    final static public Perspective THIRD_PERSON_FRONT = new Perspective(false, true);
    final static private Perspective[] VALUES;
    final private boolean firstPerson;
    final private boolean frontView;
    final static private Perspective[] field_26670;

    public static Perspective[] values() {
        return (Perspective[])field_26670.clone();
    }

    public static Perspective valueOf(String string) {
        return Enum.valueOf(Perspective.class, string);
    }

    private Perspective(boolean firstPerson, boolean frontView) {
        this.firstPerson = firstPerson;
        this.frontView = frontView;
    }

    public boolean isFirstPerson() {
        return this.firstPerson;
    }

    public boolean isFrontView() {
        return this.frontView;
    }

    public Perspective next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    private static Perspective[] method_36859() {
        return new Perspective[]{FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT};
    }

    static {
        field_26670 = Perspective.method_36859();
        VALUES = Perspective.values();
    }
}

