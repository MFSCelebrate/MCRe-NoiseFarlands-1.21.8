/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeTags {
    final static public TagKey<Biome> IS_DEEP_OCEAN = BiomeTags.of("is_deep_ocean");
    final static public TagKey<Biome> IS_OCEAN = BiomeTags.of("is_ocean");
    final static public TagKey<Biome> IS_BEACH = BiomeTags.of("is_beach");
    final static public TagKey<Biome> IS_RIVER = BiomeTags.of("is_river");
    final static public TagKey<Biome> IS_MOUNTAIN = BiomeTags.of("is_mountain");
    final static public TagKey<Biome> IS_BADLANDS = BiomeTags.of("is_badlands");
    final static public TagKey<Biome> IS_HILL = BiomeTags.of("is_hill");
    final static public TagKey<Biome> IS_TAIGA = BiomeTags.of("is_taiga");
    final static public TagKey<Biome> IS_JUNGLE = BiomeTags.of("is_jungle");
    final static public TagKey<Biome> IS_FOREST = BiomeTags.of("is_forest");
    final static public TagKey<Biome> IS_SAVANNA = BiomeTags.of("is_savanna");
    final static public TagKey<Biome> IS_OVERWORLD = BiomeTags.of("is_overworld");
    final static public TagKey<Biome> IS_NETHER = BiomeTags.of("is_nether");
    final static public TagKey<Biome> IS_END = BiomeTags.of("is_end");
    final static public TagKey<Biome> STRONGHOLD_BIASED_TO = BiomeTags.of("stronghold_biased_to");
    final static public TagKey<Biome> BURIED_TREASURE_HAS_STRUCTURE = BiomeTags.of("has_structure/buried_treasure");
    final static public TagKey<Biome> DESERT_PYRAMID_HAS_STRUCTURE = BiomeTags.of("has_structure/desert_pyramid");
    final static public TagKey<Biome> IGLOO_HAS_STRUCTURE = BiomeTags.of("has_structure/igloo");
    final static public TagKey<Biome> JUNGLE_TEMPLE_HAS_STRUCTURE = BiomeTags.of("has_structure/jungle_temple");
    final static public TagKey<Biome> MINESHAFT_HAS_STRUCTURE = BiomeTags.of("has_structure/mineshaft");
    final static public TagKey<Biome> MINESHAFT_MESA_HAS_STRUCTURE = BiomeTags.of("has_structure/mineshaft_mesa");
    final static public TagKey<Biome> OCEAN_MONUMENT_HAS_STRUCTURE = BiomeTags.of("has_structure/ocean_monument");
    final static public TagKey<Biome> OCEAN_RUIN_COLD_HAS_STRUCTURE = BiomeTags.of("has_structure/ocean_ruin_cold");
    final static public TagKey<Biome> OCEAN_RUIN_WARM_HAS_STRUCTURE = BiomeTags.of("has_structure/ocean_ruin_warm");
    final static public TagKey<Biome> PILLAGER_OUTPOST_HAS_STRUCTURE = BiomeTags.of("has_structure/pillager_outpost");
    final static public TagKey<Biome> RUINED_PORTAL_DESERT_HAS_STRUCTURE = BiomeTags.of("has_structure/ruined_portal_desert");
    final static public TagKey<Biome> RUINED_PORTAL_JUNGLE_HAS_STRUCTURE = BiomeTags.of("has_structure/ruined_portal_jungle");
    final static public TagKey<Biome> RUINED_PORTAL_OCEAN_HAS_STRUCTURE = BiomeTags.of("has_structure/ruined_portal_ocean");
    final static public TagKey<Biome> RUINED_PORTAL_SWAMP_HAS_STRUCTURE = BiomeTags.of("has_structure/ruined_portal_swamp");
    final static public TagKey<Biome> RUINED_PORTAL_MOUNTAIN_HAS_STRUCTURE = BiomeTags.of("has_structure/ruined_portal_mountain");
    final static public TagKey<Biome> RUINED_PORTAL_STANDARD_HAS_STRUCTURE = BiomeTags.of("has_structure/ruined_portal_standard");
    final static public TagKey<Biome> SHIPWRECK_BEACHED_HAS_STRUCTURE = BiomeTags.of("has_structure/shipwreck_beached");
    final static public TagKey<Biome> SHIPWRECK_HAS_STRUCTURE = BiomeTags.of("has_structure/shipwreck");
    final static public TagKey<Biome> STRONGHOLD_HAS_STRUCTURE = BiomeTags.of("has_structure/stronghold");
    final static public TagKey<Biome> TRIAL_CHAMBERS_HAS_STRUCTURE = BiomeTags.of("has_structure/trial_chambers");
    final static public TagKey<Biome> SWAMP_HUT_HAS_STRUCTURE = BiomeTags.of("has_structure/swamp_hut");
    final static public TagKey<Biome> VILLAGE_DESERT_HAS_STRUCTURE = BiomeTags.of("has_structure/village_desert");
    final static public TagKey<Biome> VILLAGE_PLAINS_HAS_STRUCTURE = BiomeTags.of("has_structure/village_plains");
    final static public TagKey<Biome> VILLAGE_SAVANNA_HAS_STRUCTURE = BiomeTags.of("has_structure/village_savanna");
    final static public TagKey<Biome> VILLAGE_SNOWY_HAS_STRUCTURE = BiomeTags.of("has_structure/village_snowy");
    final static public TagKey<Biome> VILLAGE_TAIGA_HAS_STRUCTURE = BiomeTags.of("has_structure/village_taiga");
    final static public TagKey<Biome> TRAIL_RUINS_HAS_STRUCTURE = BiomeTags.of("has_structure/trail_ruins");
    final static public TagKey<Biome> WOODLAND_MANSION_HAS_STRUCTURE = BiomeTags.of("has_structure/woodland_mansion");
    final static public TagKey<Biome> NETHER_FORTRESS_HAS_STRUCTURE = BiomeTags.of("has_structure/nether_fortress");
    final static public TagKey<Biome> NETHER_FOSSIL_HAS_STRUCTURE = BiomeTags.of("has_structure/nether_fossil");
    final static public TagKey<Biome> BASTION_REMNANT_HAS_STRUCTURE = BiomeTags.of("has_structure/bastion_remnant");
    final static public TagKey<Biome> ANCIENT_CITY_HAS_STRUCTURE = BiomeTags.of("has_structure/ancient_city");
    final static public TagKey<Biome> RUINED_PORTAL_NETHER_HAS_STRUCTURE = BiomeTags.of("has_structure/ruined_portal_nether");
    final static public TagKey<Biome> END_CITY_HAS_STRUCTURE = BiomeTags.of("has_structure/end_city");
    final static public TagKey<Biome> REQUIRED_OCEAN_MONUMENT_SURROUNDING = BiomeTags.of("required_ocean_monument_surrounding");
    final static public TagKey<Biome> MINESHAFT_BLOCKING = BiomeTags.of("mineshaft_blocking");
    final static public TagKey<Biome> PLAYS_UNDERWATER_MUSIC = BiomeTags.of("plays_underwater_music");
    final static public TagKey<Biome> HAS_CLOSER_WATER_FOG = BiomeTags.of("has_closer_water_fog");
    final static public TagKey<Biome> WATER_ON_MAP_OUTLINES = BiomeTags.of("water_on_map_outlines");
    final static public TagKey<Biome> PRODUCES_CORALS_FROM_BONEMEAL = BiomeTags.of("produces_corals_from_bonemeal");
    final static public TagKey<Biome> INCREASED_FIRE_BURNOUT = BiomeTags.of("increased_fire_burnout");
    final static public TagKey<Biome> SNOW_GOLEM_MELTS = BiomeTags.of("snow_golem_melts");
    final static public TagKey<Biome> WITHOUT_ZOMBIE_SIEGES = BiomeTags.of("without_zombie_sieges");
    final static public TagKey<Biome> WITHOUT_PATROL_SPAWNS = BiomeTags.of("without_patrol_spawns");
    final static public TagKey<Biome> WITHOUT_WANDERING_TRADER_SPAWNS = BiomeTags.of("without_wandering_trader_spawns");
    final static public TagKey<Biome> SPAWNS_COLD_VARIANT_FROGS = BiomeTags.of("spawns_cold_variant_frogs");
    final static public TagKey<Biome> SPAWNS_WARM_VARIANT_FROGS = BiomeTags.of("spawns_warm_variant_frogs");
    final static public TagKey<Biome> SPAWNS_COLD_VARIANT_FARM_ANIMALS = BiomeTags.of("spawns_cold_variant_farm_animals");
    final static public TagKey<Biome> SPAWNS_WARM_VARIANT_FARM_ANIMALS = BiomeTags.of("spawns_warm_variant_farm_animals");
    final static public TagKey<Biome> SPAWNS_GOLD_RABBITS = BiomeTags.of("spawns_gold_rabbits");
    final static public TagKey<Biome> SPAWNS_WHITE_RABBITS = BiomeTags.of("spawns_white_rabbits");
    final static public TagKey<Biome> REDUCE_WATER_AMBIENT_SPAWNS = BiomeTags.of("reduce_water_ambient_spawns");
    final static public TagKey<Biome> ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT = BiomeTags.of("allows_tropical_fish_spawns_at_any_height");
    final static public TagKey<Biome> POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS = BiomeTags.of("polar_bears_spawn_on_alternate_blocks");
    final static public TagKey<Biome> MORE_FREQUENT_DROWNED_SPAWNS = BiomeTags.of("more_frequent_drowned_spawns");
    final static public TagKey<Biome> ALLOWS_SURFACE_SLIME_SPAWNS = BiomeTags.of("allows_surface_slime_spawns");
    final static public TagKey<Biome> SPAWNS_SNOW_FOXES = BiomeTags.of("spawns_snow_foxes");

    private BiomeTags() {
    }

    private static TagKey<Biome> of(String id) {
        return TagKey.of(RegistryKeys.BIOME, Identifier.ofVanilla(id));
    }
}

