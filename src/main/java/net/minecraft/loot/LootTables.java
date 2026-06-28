/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class LootTables {
    final static private Set<RegistryKey<LootTable>> LOOT_TABLES = new HashSet<RegistryKey<LootTable>>();
    final static private Set<RegistryKey<LootTable>> LOOT_TABLES_READ_ONLY = Collections.unmodifiableSet(LOOT_TABLES);
    final static public RegistryKey<LootTable> SPAWN_BONUS_CHEST = LootTables.register("chests/spawn_bonus_chest");
    final static public RegistryKey<LootTable> END_CITY_TREASURE_CHEST = LootTables.register("chests/end_city_treasure");
    final static public RegistryKey<LootTable> SIMPLE_DUNGEON_CHEST = LootTables.register("chests/simple_dungeon");
    final static public RegistryKey<LootTable> VILLAGE_WEAPONSMITH_CHEST = LootTables.register("chests/village/village_weaponsmith");
    final static public RegistryKey<LootTable> VILLAGE_TOOLSMITH_CHEST = LootTables.register("chests/village/village_toolsmith");
    final static public RegistryKey<LootTable> VILLAGE_ARMORER_CHEST = LootTables.register("chests/village/village_armorer");
    final static public RegistryKey<LootTable> VILLAGE_CARTOGRAPHER_CHEST = LootTables.register("chests/village/village_cartographer");
    final static public RegistryKey<LootTable> VILLAGE_MASON_CHEST = LootTables.register("chests/village/village_mason");
    final static public RegistryKey<LootTable> VILLAGE_SHEPARD_CHEST = LootTables.register("chests/village/village_shepherd");
    final static public RegistryKey<LootTable> VILLAGE_BUTCHER_CHEST = LootTables.register("chests/village/village_butcher");
    final static public RegistryKey<LootTable> VILLAGE_FLETCHER_CHEST = LootTables.register("chests/village/village_fletcher");
    final static public RegistryKey<LootTable> VILLAGE_FISHER_CHEST = LootTables.register("chests/village/village_fisher");
    final static public RegistryKey<LootTable> VILLAGE_TANNERY_CHEST = LootTables.register("chests/village/village_tannery");
    final static public RegistryKey<LootTable> VILLAGE_TEMPLE_CHEST = LootTables.register("chests/village/village_temple");
    final static public RegistryKey<LootTable> VILLAGE_DESERT_HOUSE_CHEST = LootTables.register("chests/village/village_desert_house");
    final static public RegistryKey<LootTable> VILLAGE_PLAINS_CHEST = LootTables.register("chests/village/village_plains_house");
    final static public RegistryKey<LootTable> VILLAGE_TAIGA_HOUSE_CHEST = LootTables.register("chests/village/village_taiga_house");
    final static public RegistryKey<LootTable> VILLAGE_SNOWY_HOUSE_CHEST = LootTables.register("chests/village/village_snowy_house");
    final static public RegistryKey<LootTable> VILLAGE_SAVANNA_HOUSE_CHEST = LootTables.register("chests/village/village_savanna_house");
    final static public RegistryKey<LootTable> ABANDONED_MINESHAFT_CHEST = LootTables.register("chests/abandoned_mineshaft");
    final static public RegistryKey<LootTable> NETHER_BRIDGE_CHEST = LootTables.register("chests/nether_bridge");
    final static public RegistryKey<LootTable> STRONGHOLD_LIBRARY_CHEST = LootTables.register("chests/stronghold_library");
    final static public RegistryKey<LootTable> STRONGHOLD_CROSSING_CHEST = LootTables.register("chests/stronghold_crossing");
    final static public RegistryKey<LootTable> STRONGHOLD_CORRIDOR_CHEST = LootTables.register("chests/stronghold_corridor");
    final static public RegistryKey<LootTable> DESERT_PYRAMID_CHEST = LootTables.register("chests/desert_pyramid");
    final static public RegistryKey<LootTable> JUNGLE_TEMPLE_CHEST = LootTables.register("chests/jungle_temple");
    final static public RegistryKey<LootTable> JUNGLE_TEMPLE_DISPENSER_CHEST = LootTables.register("chests/jungle_temple_dispenser");
    final static public RegistryKey<LootTable> IGLOO_CHEST_CHEST = LootTables.register("chests/igloo_chest");
    final static public RegistryKey<LootTable> WOODLAND_MANSION_CHEST = LootTables.register("chests/woodland_mansion");
    final static public RegistryKey<LootTable> UNDERWATER_RUIN_SMALL_CHEST = LootTables.register("chests/underwater_ruin_small");
    final static public RegistryKey<LootTable> UNDERWATER_RUIN_BIG_CHEST = LootTables.register("chests/underwater_ruin_big");
    final static public RegistryKey<LootTable> BURIED_TREASURE_CHEST = LootTables.register("chests/buried_treasure");
    final static public RegistryKey<LootTable> SHIPWRECK_MAP_CHEST = LootTables.register("chests/shipwreck_map");
    final static public RegistryKey<LootTable> SHIPWRECK_SUPPLY_CHEST = LootTables.register("chests/shipwreck_supply");
    final static public RegistryKey<LootTable> SHIPWRECK_TREASURE_CHEST = LootTables.register("chests/shipwreck_treasure");
    final static public RegistryKey<LootTable> PILLAGER_OUTPOST_CHEST = LootTables.register("chests/pillager_outpost");
    final static public RegistryKey<LootTable> BASTION_TREASURE_CHEST = LootTables.register("chests/bastion_treasure");
    final static public RegistryKey<LootTable> BASTION_OTHER_CHEST = LootTables.register("chests/bastion_other");
    final static public RegistryKey<LootTable> BASTION_BRIDGE_CHEST = LootTables.register("chests/bastion_bridge");
    final static public RegistryKey<LootTable> BASTION_HOGLIN_STABLE_CHEST = LootTables.register("chests/bastion_hoglin_stable");
    final static public RegistryKey<LootTable> ANCIENT_CITY_CHEST = LootTables.register("chests/ancient_city");
    final static public RegistryKey<LootTable> ANCIENT_CITY_ICE_BOX_CHEST = LootTables.register("chests/ancient_city_ice_box");
    final static public RegistryKey<LootTable> RUINED_PORTAL_CHEST = LootTables.register("chests/ruined_portal");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_CHEST = LootTables.register("chests/trial_chambers/reward");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_COMMON_CHEST = LootTables.register("chests/trial_chambers/reward_common");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_RARE_CHEST = LootTables.register("chests/trial_chambers/reward_rare");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_UNIQUE_CHEST = LootTables.register("chests/trial_chambers/reward_unique");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_CHEST = LootTables.register("chests/trial_chambers/reward_ominous");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON_CHEST = LootTables.register("chests/trial_chambers/reward_ominous_common");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_CHEST = LootTables.register("chests/trial_chambers/reward_ominous_rare");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST = LootTables.register("chests/trial_chambers/reward_ominous_unique");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_SUPPLY_CHEST = LootTables.register("chests/trial_chambers/supply");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_CORRIDOR_CHEST = LootTables.register("chests/trial_chambers/corridor");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_INTERSECTION_CHEST = LootTables.register("chests/trial_chambers/intersection");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_INTERSECTION_BARREL_CHEST = LootTables.register("chests/trial_chambers/intersection_barrel");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_ENTRANCE_CHEST = LootTables.register("chests/trial_chambers/entrance");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_CORRIDOR_DISPENSER = LootTables.register("dispensers/trial_chambers/corridor");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_CHAMBER_DISPENSER = LootTables.register("dispensers/trial_chambers/chamber");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_WATER_DISPENSER = LootTables.register("dispensers/trial_chambers/water");
    final static public RegistryKey<LootTable> TRIAL_CHAMBERS_CORRIDOR_POT = LootTables.register("pots/trial_chambers/corridor");
    final static public RegistryKey<LootTable> TRIAL_CHAMBER_EQUIPMENT = LootTables.register("equipment/trial_chamber");
    final static public RegistryKey<LootTable> TRIAL_CHAMBER_RANGED_EQUIPMENT = LootTables.register("equipment/trial_chamber_ranged");
    final static public RegistryKey<LootTable> TRIAL_CHAMBER_MELEE_EQUIPMENT = LootTables.register("equipment/trial_chamber_melee");
    final static public Map<DyeColor, RegistryKey<LootTable>> SHEEP_DROPS_FROM_DYE_COLOR = LootTables.registerAllDyeColors("entities/sheep");
    final static public RegistryKey<LootTable> FISHING_GAMEPLAY = LootTables.register("gameplay/fishing");
    final static public RegistryKey<LootTable> FISHING_JUNK_GAMEPLAY = LootTables.register("gameplay/fishing/junk");
    final static public RegistryKey<LootTable> FISHING_TREASURE_GAMEPLAY = LootTables.register("gameplay/fishing/treasure");
    final static public RegistryKey<LootTable> FISHING_FISH_GAMEPLAY = LootTables.register("gameplay/fishing/fish");
    final static public RegistryKey<LootTable> CAT_MORNING_GIFT_GAMEPLAY = LootTables.register("gameplay/cat_morning_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/armorer_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/butcher_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/cartographer_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/cleric_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/farmer_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/fisherman_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/fletcher_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/leatherworker_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/librarian_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/mason_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/shepherd_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/toolsmith_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/weaponsmith_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_UNEMPLOYED_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/unemployed_gift");
    final static public RegistryKey<LootTable> HERO_OF_THE_VILLAGE_BABY_GIFT_GAMEPLAY = LootTables.register("gameplay/hero_of_the_village/baby_gift");
    final static public RegistryKey<LootTable> SNIFFER_DIGGING_GAMEPLAY = LootTables.register("gameplay/sniffer_digging");
    final static public RegistryKey<LootTable> PANDA_SNEEZE_GAMEPLAY = LootTables.register("gameplay/panda_sneeze");
    final static public RegistryKey<LootTable> CHICKEN_LAY_GAMEPLAY = LootTables.register("gameplay/chicken_lay");
    final static public RegistryKey<LootTable> ARMADILLO_SHED_GAMEPLAY = LootTables.register("gameplay/armadillo_shed");
    final static public RegistryKey<LootTable> PIGLIN_BARTERING_GAMEPLAY = LootTables.register("gameplay/piglin_bartering");
    final static public RegistryKey<LootTable> TRIAL_CHAMBER_KEY_SPAWNER = LootTables.register("spawners/trial_chamber/key");
    final static public RegistryKey<LootTable> TRIAL_CHAMBER_CONSUMABLES_SPAWNER = LootTables.register("spawners/trial_chamber/consumables");
    final static public RegistryKey<LootTable> OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER = LootTables.register("spawners/ominous/trial_chamber/key");
    final static public RegistryKey<LootTable> OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER = LootTables.register("spawners/ominous/trial_chamber/consumables");
    final static public RegistryKey<LootTable> TRIAL_CHAMBER_ITEMS_TO_DROP_WHEN_OMINOUS_SPAWNER = LootTables.register("spawners/trial_chamber/items_to_drop_when_ominous");
    final static public RegistryKey<LootTable> BOGGED_SHEARING = LootTables.register("shearing/bogged");
    final static public RegistryKey<LootTable> MOOSHROOM_SHEARING = LootTables.register("shearing/mooshroom");
    final static public RegistryKey<LootTable> MOOSHROOM_RED_SHEARING = LootTables.register("shearing/mooshroom/red");
    final static public RegistryKey<LootTable> MOOSHROOM_BROWN_SHEARING = LootTables.register("shearing/mooshroom/brown");
    final static public RegistryKey<LootTable> SNOW_GOLEM_SHEARING = LootTables.register("shearing/snow_golem");
    final static public RegistryKey<LootTable> SHEEP_SHEARING = LootTables.register("shearing/sheep");
    final static public Map<DyeColor, RegistryKey<LootTable>> SHEEP_SHEARING_FROM_DYE_COLOR = LootTables.registerAllDyeColors("shearing/sheep");
    final static public RegistryKey<LootTable> DESERT_WELL_ARCHAEOLOGY = LootTables.register("archaeology/desert_well");
    final static public RegistryKey<LootTable> DESERT_PYRAMID_ARCHAEOLOGY = LootTables.register("archaeology/desert_pyramid");
    final static public RegistryKey<LootTable> TRAIL_RUINS_COMMON_ARCHAEOLOGY = LootTables.register("archaeology/trail_ruins_common");
    final static public RegistryKey<LootTable> TRAIL_RUINS_RARE_ARCHAEOLOGY = LootTables.register("archaeology/trail_ruins_rare");
    final static public RegistryKey<LootTable> OCEAN_RUIN_WARM_ARCHAEOLOGY = LootTables.register("archaeology/ocean_ruin_warm");
    final static public RegistryKey<LootTable> OCEAN_RUIN_COLD_ARCHAEOLOGY = LootTables.register("archaeology/ocean_ruin_cold");

    private static Map<DyeColor, RegistryKey<LootTable>> registerAllDyeColors(String prefix) {
        return Util.mapEnum(DyeColor.class, color -> LootTables.register(prefix + "/" + color.getId()));
    }

    private static RegistryKey<LootTable> register(String id) {
        return LootTables.registerLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla(id)));
    }

    private static RegistryKey<LootTable> registerLootTable(RegistryKey<LootTable> key) {
        if (LOOT_TABLES.add(key)) {
            return key;
        }
        throw new IllegalArgumentException(String.valueOf(key.getValue()) + " is already a registered built-in loot table");
    }

    public static Set<RegistryKey<LootTable>> getAll() {
        return LOOT_TABLES_READ_ONLY;
    }
}

