/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface EntityTypeTags {
    final static public TagKey<EntityType<?>> SKELETONS = EntityTypeTags.of("skeletons");
    final static public TagKey<EntityType<?>> ZOMBIES = EntityTypeTags.of("zombies");
    final static public TagKey<EntityType<?>> RAIDERS = EntityTypeTags.of("raiders");
    final static public TagKey<EntityType<?>> UNDEAD = EntityTypeTags.of("undead");
    final static public TagKey<EntityType<?>> BEEHIVE_INHABITORS = EntityTypeTags.of("beehive_inhabitors");
    final static public TagKey<EntityType<?>> ARROWS = EntityTypeTags.of("arrows");
    final static public TagKey<EntityType<?>> IMPACT_PROJECTILES = EntityTypeTags.of("impact_projectiles");
    final static public TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = EntityTypeTags.of("powder_snow_walkable_mobs");
    final static public TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = EntityTypeTags.of("axolotl_always_hostiles");
    final static public TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = EntityTypeTags.of("axolotl_hunt_targets");
    final static public TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = EntityTypeTags.of("freeze_immune_entity_types");
    final static public TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = EntityTypeTags.of("freeze_hurts_extra_types");
    final static public TagKey<EntityType<?>> CAN_BREATHE_UNDER_WATER = EntityTypeTags.of("can_breathe_under_water");
    final static public TagKey<EntityType<?>> FROG_FOOD = EntityTypeTags.of("frog_food");
    final static public TagKey<EntityType<?>> FALL_DAMAGE_IMMUNE = EntityTypeTags.of("fall_damage_immune");
    final static public TagKey<EntityType<?>> DISMOUNTS_UNDERWATER = EntityTypeTags.of("dismounts_underwater");
    final static public TagKey<EntityType<?>> NON_CONTROLLING_RIDER = EntityTypeTags.of("non_controlling_rider");
    final static public TagKey<EntityType<?>> DEFLECTS_PROJECTILES = EntityTypeTags.of("deflects_projectiles");
    final static public TagKey<EntityType<?>> CAN_TURN_IN_BOATS = EntityTypeTags.of("can_turn_in_boats");
    final static public TagKey<EntityType<?>> ILLAGER = EntityTypeTags.of("illager");
    final static public TagKey<EntityType<?>> AQUATIC = EntityTypeTags.of("aquatic");
    final static public TagKey<EntityType<?>> ARTHROPOD = EntityTypeTags.of("arthropod");
    final static public TagKey<EntityType<?>> IGNORES_POISON_AND_REGEN = EntityTypeTags.of("ignores_poison_and_regen");
    final static public TagKey<EntityType<?>> INVERTED_HEALING_AND_HARM = EntityTypeTags.of("inverted_healing_and_harm");
    final static public TagKey<EntityType<?>> WITHER_FRIENDS = EntityTypeTags.of("wither_friends");
    final static public TagKey<EntityType<?>> ILLAGER_FRIENDS = EntityTypeTags.of("illager_friends");
    final static public TagKey<EntityType<?>> NOT_SCARY_FOR_PUFFERFISH = EntityTypeTags.of("not_scary_for_pufferfish");
    final static public TagKey<EntityType<?>> SENSITIVE_TO_IMPALING = EntityTypeTags.of("sensitive_to_impaling");
    final static public TagKey<EntityType<?>> SENSITIVE_TO_BANE_OF_ARTHROPODS = EntityTypeTags.of("sensitive_to_bane_of_arthropods");
    final static public TagKey<EntityType<?>> SENSITIVE_TO_SMITE = EntityTypeTags.of("sensitive_to_smite");
    final static public TagKey<EntityType<?>> NO_ANGER_FROM_WIND_CHARGE = EntityTypeTags.of("no_anger_from_wind_charge");
    final static public TagKey<EntityType<?>> IMMUNE_TO_OOZING = EntityTypeTags.of("immune_to_oozing");
    final static public TagKey<EntityType<?>> IMMUNE_TO_INFESTED = EntityTypeTags.of("immune_to_infested");
    final static public TagKey<EntityType<?>> REDIRECTABLE_PROJECTILE = EntityTypeTags.of("redirectable_projectile");
    final static public TagKey<EntityType<?>> BOAT = EntityTypeTags.of("boat");
    final static public TagKey<EntityType<?>> CAN_EQUIP_SADDLE = EntityTypeTags.of("can_equip_saddle");
    final static public TagKey<EntityType<?>> CAN_EQUIP_HARNESS = EntityTypeTags.of("can_equip_harness");
    final static public TagKey<EntityType<?>> CAN_WEAR_HORSE_ARMOR = EntityTypeTags.of("can_wear_horse_armor");
    final static public TagKey<EntityType<?>> FOLLOWABLE_FRIENDLY_MOBS = EntityTypeTags.of("followable_friendly_mobs");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.ofVanilla(id));
    }
}

