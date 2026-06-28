/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

public final class PiglinActivity
extends Enum<PiglinActivity> {
    final static public PiglinActivity ATTACKING_WITH_MELEE_WEAPON = new PiglinActivity();
    final static public PiglinActivity CROSSBOW_HOLD = new PiglinActivity();
    final static public PiglinActivity CROSSBOW_CHARGE = new PiglinActivity();
    final static public PiglinActivity ADMIRING_ITEM = new PiglinActivity();
    final static public PiglinActivity DANCING = new PiglinActivity();
    final static public PiglinActivity DEFAULT = new PiglinActivity();
    final static private PiglinActivity[] field_22387;

    public static PiglinActivity[] values() {
        return (PiglinActivity[])field_22387.clone();
    }

    public static PiglinActivity valueOf(String string) {
        return Enum.valueOf(PiglinActivity.class, string);
    }

    private static PiglinActivity[] method_36659() {
        return new PiglinActivity[]{ATTACKING_WITH_MELEE_WEAPON, CROSSBOW_HOLD, CROSSBOW_CHARGE, ADMIRING_ITEM, DANCING, DEFAULT};
    }

    static {
        field_22387 = PiglinActivity.method_36659();
    }
}

