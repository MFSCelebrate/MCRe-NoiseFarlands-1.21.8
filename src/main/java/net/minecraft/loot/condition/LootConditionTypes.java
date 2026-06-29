/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.loot.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.condition.AllOfLootCondition;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EnchantmentActiveCheckLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.EntityScoresLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithEnchantedBonusLootCondition;
import net.minecraft.loot.condition.ReferenceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.condition.TimeCheckLootCondition;
import net.minecraft.loot.condition.ValueCheckLootCondition;
import net.minecraft.loot.condition.WeatherCheckLootCondition;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootConditionTypes {
    final static public LootConditionType INVERTED = LootConditionTypes.register("inverted", InvertedLootCondition.CODEC);
    final static public LootConditionType ANY_OF = LootConditionTypes.register("any_of", AnyOfLootCondition.CODEC);
    final static public LootConditionType ALL_OF = LootConditionTypes.register("all_of", AllOfLootCondition.CODEC);
    final static public LootConditionType RANDOM_CHANCE = LootConditionTypes.register("random_chance", RandomChanceLootCondition.CODEC);
    final static public LootConditionType RANDOM_CHANCE_WITH_ENCHANTED_BONUS = LootConditionTypes.register("random_chance_with_enchanted_bonus", RandomChanceWithEnchantedBonusLootCondition.CODEC);
    final static public LootConditionType ENTITY_PROPERTIES = LootConditionTypes.register("entity_properties", EntityPropertiesLootCondition.CODEC);
    final static public LootConditionType KILLED_BY_PLAYER = LootConditionTypes.register("killed_by_player", KilledByPlayerLootCondition.CODEC);
    final static public LootConditionType ENTITY_SCORES = LootConditionTypes.register("entity_scores", EntityScoresLootCondition.CODEC);
    final static public LootConditionType BLOCK_STATE_PROPERTY = LootConditionTypes.register("block_state_property", BlockStatePropertyLootCondition.CODEC);
    final static public LootConditionType MATCH_TOOL = LootConditionTypes.register("match_tool", MatchToolLootCondition.CODEC);
    final static public LootConditionType TABLE_BONUS = LootConditionTypes.register("table_bonus", TableBonusLootCondition.CODEC);
    final static public LootConditionType SURVIVES_EXPLOSION = LootConditionTypes.register("survives_explosion", SurvivesExplosionLootCondition.CODEC);
    final static public LootConditionType DAMAGE_SOURCE_PROPERTIES = LootConditionTypes.register("damage_source_properties", DamageSourcePropertiesLootCondition.CODEC);
    final static public LootConditionType LOCATION_CHECK = LootConditionTypes.register("location_check", LocationCheckLootCondition.CODEC);
    final static public LootConditionType WEATHER_CHECK = LootConditionTypes.register("weather_check", WeatherCheckLootCondition.CODEC);
    final static public LootConditionType REFERENCE = LootConditionTypes.register("reference", ReferenceLootCondition.CODEC);
    final static public LootConditionType TIME_CHECK = LootConditionTypes.register("time_check", TimeCheckLootCondition.CODEC);
    final static public LootConditionType VALUE_CHECK = LootConditionTypes.register("value_check", ValueCheckLootCondition.CODEC);
    final static public LootConditionType ENCHANTMENT_ACTIVE_CHECK = LootConditionTypes.register("enchantment_active_check", EnchantmentActiveCheckLootCondition.CODEC);

    private static LootConditionType register(String id, MapCodec<? extends LootCondition> codec) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, Identifier.ofVanilla(id), new LootConditionType(codec));
    }
}

