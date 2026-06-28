/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;

public record DamageImmunityEnchantmentEffect() {
    final static public DamageImmunityEnchantmentEffect INSTANCE = new DamageImmunityEnchantmentEffect();
    final static public Codec<DamageImmunityEnchantmentEffect> CODEC = Codec.unit(() -> INSTANCE);
}

