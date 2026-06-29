/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public final class EnchantmentEffectTarget
extends Enum<EnchantmentEffectTarget>
implements StringIdentifiable {
    final static public EnchantmentEffectTarget ATTACKER = new EnchantmentEffectTarget("attacker");
    final static public EnchantmentEffectTarget DAMAGING_ENTITY = new EnchantmentEffectTarget("damaging_entity");
    final static public EnchantmentEffectTarget VICTIM = new EnchantmentEffectTarget("victim");
    final static public Codec<EnchantmentEffectTarget> CODEC;
    final private String id;
    final static private EnchantmentEffectTarget[] field_51688;

    public static EnchantmentEffectTarget[] values() {
        return (EnchantmentEffectTarget[])field_51688.clone();
    }

    public static EnchantmentEffectTarget valueOf(String string) {
        return Enum.valueOf(EnchantmentEffectTarget.class, string);
    }

    private EnchantmentEffectTarget(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static EnchantmentEffectTarget[] method_60182() {
        return new EnchantmentEffectTarget[]{ATTACKER, DAMAGING_ENTITY, VICTIM};
    }

    static {
        field_51688 = EnchantmentEffectTarget.method_60182();
        CODEC = StringIdentifiable.createCodec(EnchantmentEffectTarget::values);
    }
}

