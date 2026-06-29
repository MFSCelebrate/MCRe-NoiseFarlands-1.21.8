/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface EnchantmentTags {
    final static public TagKey<Enchantment> TOOLTIP_ORDER = EnchantmentTags.of("tooltip_order");
    final static public TagKey<Enchantment> ARMOR_EXCLUSIVE_SET = EnchantmentTags.of("exclusive_set/armor");
    final static public TagKey<Enchantment> BOOTS_EXCLUSIVE_SET = EnchantmentTags.of("exclusive_set/boots");
    final static public TagKey<Enchantment> BOW_EXCLUSIVE_SET = EnchantmentTags.of("exclusive_set/bow");
    final static public TagKey<Enchantment> CROSSBOW_EXCLUSIVE_SET = EnchantmentTags.of("exclusive_set/crossbow");
    final static public TagKey<Enchantment> DAMAGE_EXCLUSIVE_SET = EnchantmentTags.of("exclusive_set/damage");
    final static public TagKey<Enchantment> MINING_EXCLUSIVE_SET = EnchantmentTags.of("exclusive_set/mining");
    final static public TagKey<Enchantment> RIPTIDE_EXCLUSIVE_SET = EnchantmentTags.of("exclusive_set/riptide");
    final static public TagKey<Enchantment> TRADEABLE = EnchantmentTags.of("tradeable");
    final static public TagKey<Enchantment> DOUBLE_TRADE_PRICE = EnchantmentTags.of("double_trade_price");
    final static public TagKey<Enchantment> IN_ENCHANTING_TABLE = EnchantmentTags.of("in_enchanting_table");
    final static public TagKey<Enchantment> ON_MOB_SPAWN_EQUIPMENT = EnchantmentTags.of("on_mob_spawn_equipment");
    final static public TagKey<Enchantment> ON_TRADED_EQUIPMENT = EnchantmentTags.of("on_traded_equipment");
    final static public TagKey<Enchantment> ON_RANDOM_LOOT = EnchantmentTags.of("on_random_loot");
    final static public TagKey<Enchantment> CURSE = EnchantmentTags.of("curse");
    final static public TagKey<Enchantment> SMELTS_LOOT = EnchantmentTags.of("smelts_loot");
    final static public TagKey<Enchantment> PREVENTS_BEE_SPAWNS_WHEN_MINING = EnchantmentTags.of("prevents_bee_spawns_when_mining");
    final static public TagKey<Enchantment> PREVENTS_DECORATED_POT_SHATTERING = EnchantmentTags.of("prevents_decorated_pot_shattering");
    final static public TagKey<Enchantment> PREVENTS_ICE_MELTING = EnchantmentTags.of("prevents_ice_melting");
    final static public TagKey<Enchantment> PREVENTS_INFESTED_SPAWNS = EnchantmentTags.of("prevents_infested_spawns");
    final static public TagKey<Enchantment> TREASURE = EnchantmentTags.of("treasure");
    final static public TagKey<Enchantment> NON_TREASURE = EnchantmentTags.of("non_treasure");
    final static public TagKey<Enchantment> DESERT_COMMON_TRADE = EnchantmentTags.of("trades/desert_common");
    final static public TagKey<Enchantment> JUNGLE_COMMON_TRADE = EnchantmentTags.of("trades/jungle_common");
    final static public TagKey<Enchantment> PLAINS_COMMON_TRADE = EnchantmentTags.of("trades/plains_common");
    final static public TagKey<Enchantment> SAVANNA_COMMON_TRADE = EnchantmentTags.of("trades/savanna_common");
    final static public TagKey<Enchantment> SNOW_COMMON_TRADE = EnchantmentTags.of("trades/snow_common");
    final static public TagKey<Enchantment> SWAMP_COMMON_TRADE = EnchantmentTags.of("trades/swamp_common");
    final static public TagKey<Enchantment> TAIGA_COMMON_TRADE = EnchantmentTags.of("trades/taiga_common");
    final static public TagKey<Enchantment> DESERT_SPECIAL_TRADE = EnchantmentTags.of("trades/desert_special");
    final static public TagKey<Enchantment> JUNGLE_SPECIAL_TRADE = EnchantmentTags.of("trades/jungle_special");
    final static public TagKey<Enchantment> PLAINS_SPECIAL_TRADE = EnchantmentTags.of("trades/plains_special");
    final static public TagKey<Enchantment> SAVANNA_SPECIAL_TRADE = EnchantmentTags.of("trades/savanna_special");
    final static public TagKey<Enchantment> SNOW_SPECIAL_TRADE = EnchantmentTags.of("trades/snow_special");
    final static public TagKey<Enchantment> SWAMP_SPECIAL_TRADE = EnchantmentTags.of("trades/swamp_special");
    final static public TagKey<Enchantment> TAIGA_SPECIAL_TRADE = EnchantmentTags.of("trades/taiga_special");

    private static TagKey<Enchantment> of(String id) {
        return TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.ofVanilla(id));
    }
}

