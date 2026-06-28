/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.effect;

import net.minecraft.util.Formatting;

public final class StatusEffectCategory
extends Enum<StatusEffectCategory> {
    final static public StatusEffectCategory BENEFICIAL = new StatusEffectCategory(Formatting.BLUE);
    final static public StatusEffectCategory HARMFUL = new StatusEffectCategory(Formatting.RED);
    final static public StatusEffectCategory NEUTRAL = new StatusEffectCategory(Formatting.BLUE);
    final private Formatting formatting;
    final static private StatusEffectCategory[] field_18275;

    public static StatusEffectCategory[] values() {
        return (StatusEffectCategory[])field_18275.clone();
    }

    public static StatusEffectCategory valueOf(String string) {
        return Enum.valueOf(StatusEffectCategory.class, string);
    }

    private StatusEffectCategory(Formatting format) {
        this.formatting = format;
    }

    public Formatting getFormatting() {
        return this.formatting;
    }

    private static StatusEffectCategory[] method_36600() {
        return new StatusEffectCategory[]{BENEFICIAL, HARMFUL, NEUTRAL};
    }

    static {
        field_18275 = StatusEffectCategory.method_36600();
    }
}

