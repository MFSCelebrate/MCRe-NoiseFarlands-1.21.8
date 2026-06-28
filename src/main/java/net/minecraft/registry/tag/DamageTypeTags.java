/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface DamageTypeTags {
    final static public TagKey<DamageType> DAMAGES_HELMET = DamageTypeTags.of("damages_helmet");
    final static public TagKey<DamageType> BYPASSES_ARMOR = DamageTypeTags.of("bypasses_armor");
    final static public TagKey<DamageType> BYPASSES_SHIELD = DamageTypeTags.of("bypasses_shield");
    final static public TagKey<DamageType> BYPASSES_INVULNERABILITY = DamageTypeTags.of("bypasses_invulnerability");
    final static public TagKey<DamageType> BYPASSES_COOLDOWN = DamageTypeTags.of("bypasses_cooldown");
    final static public TagKey<DamageType> BYPASSES_EFFECTS = DamageTypeTags.of("bypasses_effects");
    final static public TagKey<DamageType> BYPASSES_RESISTANCE = DamageTypeTags.of("bypasses_resistance");
    final static public TagKey<DamageType> BYPASSES_ENCHANTMENTS = DamageTypeTags.of("bypasses_enchantments");
    final static public TagKey<DamageType> IS_FIRE = DamageTypeTags.of("is_fire");
    final static public TagKey<DamageType> IS_PROJECTILE = DamageTypeTags.of("is_projectile");
    final static public TagKey<DamageType> WITCH_RESISTANT_TO = DamageTypeTags.of("witch_resistant_to");
    final static public TagKey<DamageType> IS_EXPLOSION = DamageTypeTags.of("is_explosion");
    final static public TagKey<DamageType> IS_FALL = DamageTypeTags.of("is_fall");
    final static public TagKey<DamageType> IS_DROWNING = DamageTypeTags.of("is_drowning");
    final static public TagKey<DamageType> IS_FREEZING = DamageTypeTags.of("is_freezing");
    final static public TagKey<DamageType> IS_LIGHTNING = DamageTypeTags.of("is_lightning");
    final static public TagKey<DamageType> NO_ANGER = DamageTypeTags.of("no_anger");
    final static public TagKey<DamageType> NO_IMPACT = DamageTypeTags.of("no_impact");
    final static public TagKey<DamageType> ALWAYS_MOST_SIGNIFICANT_FALL = DamageTypeTags.of("always_most_significant_fall");
    final static public TagKey<DamageType> WITHER_IMMUNE_TO = DamageTypeTags.of("wither_immune_to");
    final static public TagKey<DamageType> IGNITES_ARMOR_STANDS = DamageTypeTags.of("ignites_armor_stands");
    final static public TagKey<DamageType> BURNS_ARMOR_STANDS = DamageTypeTags.of("burns_armor_stands");
    final static public TagKey<DamageType> AVOIDS_GUARDIAN_THORNS = DamageTypeTags.of("avoids_guardian_thorns");
    final static public TagKey<DamageType> ALWAYS_TRIGGERS_SILVERFISH = DamageTypeTags.of("always_triggers_silverfish");
    final static public TagKey<DamageType> ALWAYS_HURTS_ENDER_DRAGONS = DamageTypeTags.of("always_hurts_ender_dragons");
    final static public TagKey<DamageType> NO_KNOCKBACK = DamageTypeTags.of("no_knockback");
    final static public TagKey<DamageType> ALWAYS_KILLS_ARMOR_STANDS = DamageTypeTags.of("always_kills_armor_stands");
    final static public TagKey<DamageType> CAN_BREAK_ARMOR_STAND = DamageTypeTags.of("can_break_armor_stand");
    final static public TagKey<DamageType> BYPASSES_WOLF_ARMOR = DamageTypeTags.of("bypasses_wolf_armor");
    final static public TagKey<DamageType> IS_PLAYER_ATTACK = DamageTypeTags.of("is_player_attack");
    final static public TagKey<DamageType> BURN_FROM_STEPPING = DamageTypeTags.of("burn_from_stepping");
    final static public TagKey<DamageType> PANIC_CAUSES = DamageTypeTags.of("panic_causes");
    final static public TagKey<DamageType> PANIC_ENVIRONMENTAL_CAUSES = DamageTypeTags.of("panic_environmental_causes");
    final static public TagKey<DamageType> MACE_SMASH = DamageTypeTags.of("mace_smash");

    private static TagKey<DamageType> of(String id) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla(id));
    }
}

