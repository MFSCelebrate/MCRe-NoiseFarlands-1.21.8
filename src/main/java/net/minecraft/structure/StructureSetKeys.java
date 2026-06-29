/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;

public interface StructureSetKeys {
    final static public RegistryKey<StructureSet> VILLAGES = StructureSetKeys.of("villages");
    final static public RegistryKey<StructureSet> DESERT_PYRAMIDS = StructureSetKeys.of("desert_pyramids");
    final static public RegistryKey<StructureSet> IGLOOS = StructureSetKeys.of("igloos");
    final static public RegistryKey<StructureSet> JUNGLE_TEMPLES = StructureSetKeys.of("jungle_temples");
    final static public RegistryKey<StructureSet> SWAMP_HUTS = StructureSetKeys.of("swamp_huts");
    final static public RegistryKey<StructureSet> PILLAGER_OUTPOSTS = StructureSetKeys.of("pillager_outposts");
    final static public RegistryKey<StructureSet> OCEAN_MONUMENTS = StructureSetKeys.of("ocean_monuments");
    final static public RegistryKey<StructureSet> WOODLAND_MANSIONS = StructureSetKeys.of("woodland_mansions");
    final static public RegistryKey<StructureSet> BURIED_TREASURES = StructureSetKeys.of("buried_treasures");
    final static public RegistryKey<StructureSet> MINESHAFTS = StructureSetKeys.of("mineshafts");
    final static public RegistryKey<StructureSet> RUINED_PORTALS = StructureSetKeys.of("ruined_portals");
    final static public RegistryKey<StructureSet> SHIPWRECKS = StructureSetKeys.of("shipwrecks");
    final static public RegistryKey<StructureSet> OCEAN_RUINS = StructureSetKeys.of("ocean_ruins");
    final static public RegistryKey<StructureSet> NETHER_COMPLEXES = StructureSetKeys.of("nether_complexes");
    final static public RegistryKey<StructureSet> NETHER_FOSSILS = StructureSetKeys.of("nether_fossils");
    final static public RegistryKey<StructureSet> END_CITIES = StructureSetKeys.of("end_cities");
    final static public RegistryKey<StructureSet> ANCIENT_CITIES = StructureSetKeys.of("ancient_cities");
    final static public RegistryKey<StructureSet> STRONGHOLDS = StructureSetKeys.of("strongholds");
    final static public RegistryKey<StructureSet> TRAIL_RUINS = StructureSetKeys.of("trail_ruins");
    final static public RegistryKey<StructureSet> TRIAL_CHAMBERS = StructureSetKeys.of("trial_chambers");

    private static RegistryKey<StructureSet> of(String id) {
        return RegistryKey.of(RegistryKeys.STRUCTURE_SET, Identifier.ofVanilla(id));
    }
}

