/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.gen.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public interface StructureKeys {
    final static public RegistryKey<Structure> PILLAGER_OUTPOST = StructureKeys.of("pillager_outpost");
    final static public RegistryKey<Structure> MINESHAFT = StructureKeys.of("mineshaft");
    final static public RegistryKey<Structure> MINESHAFT_MESA = StructureKeys.of("mineshaft_mesa");
    final static public RegistryKey<Structure> MANSION = StructureKeys.of("mansion");
    final static public RegistryKey<Structure> JUNGLE_PYRAMID = StructureKeys.of("jungle_pyramid");
    final static public RegistryKey<Structure> DESERT_PYRAMID = StructureKeys.of("desert_pyramid");
    final static public RegistryKey<Structure> IGLOO = StructureKeys.of("igloo");
    final static public RegistryKey<Structure> SHIPWRECK = StructureKeys.of("shipwreck");
    final static public RegistryKey<Structure> SHIPWRECK_BEACHED = StructureKeys.of("shipwreck_beached");
    final static public RegistryKey<Structure> SWAMP_HUT = StructureKeys.of("swamp_hut");
    final static public RegistryKey<Structure> STRONGHOLD = StructureKeys.of("stronghold");
    final static public RegistryKey<Structure> MONUMENT = StructureKeys.of("monument");
    final static public RegistryKey<Structure> OCEAN_RUIN_COLD = StructureKeys.of("ocean_ruin_cold");
    final static public RegistryKey<Structure> OCEAN_RUIN_WARM = StructureKeys.of("ocean_ruin_warm");
    final static public RegistryKey<Structure> FORTRESS = StructureKeys.of("fortress");
    final static public RegistryKey<Structure> NETHER_FOSSIL = StructureKeys.of("nether_fossil");
    final static public RegistryKey<Structure> END_CITY = StructureKeys.of("end_city");
    final static public RegistryKey<Structure> BURIED_TREASURE = StructureKeys.of("buried_treasure");
    final static public RegistryKey<Structure> BASTION_REMNANT = StructureKeys.of("bastion_remnant");
    final static public RegistryKey<Structure> VILLAGE_PLAINS = StructureKeys.of("village_plains");
    final static public RegistryKey<Structure> VILLAGE_DESERT = StructureKeys.of("village_desert");
    final static public RegistryKey<Structure> VILLAGE_SAVANNA = StructureKeys.of("village_savanna");
    final static public RegistryKey<Structure> VILLAGE_SNOWY = StructureKeys.of("village_snowy");
    final static public RegistryKey<Structure> VILLAGE_TAIGA = StructureKeys.of("village_taiga");
    final static public RegistryKey<Structure> RUINED_PORTAL = StructureKeys.of("ruined_portal");
    final static public RegistryKey<Structure> RUINED_PORTAL_DESERT = StructureKeys.of("ruined_portal_desert");
    final static public RegistryKey<Structure> RUINED_PORTAL_JUNGLE = StructureKeys.of("ruined_portal_jungle");
    final static public RegistryKey<Structure> RUINED_PORTAL_SWAMP = StructureKeys.of("ruined_portal_swamp");
    final static public RegistryKey<Structure> RUINED_PORTAL_MOUNTAIN = StructureKeys.of("ruined_portal_mountain");
    final static public RegistryKey<Structure> RUINED_PORTAL_OCEAN = StructureKeys.of("ruined_portal_ocean");
    final static public RegistryKey<Structure> RUINED_PORTAL_NETHER = StructureKeys.of("ruined_portal_nether");
    final static public RegistryKey<Structure> ANCIENT_CITY = StructureKeys.of("ancient_city");
    final static public RegistryKey<Structure> TRAIL_RUINS = StructureKeys.of("trail_ruins");
    final static public RegistryKey<Structure> TRIAL_CHAMBERS = StructureKeys.of("trial_chambers");

    private static RegistryKey<Structure> of(String id) {
        return RegistryKey.of(RegistryKeys.STRUCTURE, Identifier.ofVanilla(id));
    }
}

