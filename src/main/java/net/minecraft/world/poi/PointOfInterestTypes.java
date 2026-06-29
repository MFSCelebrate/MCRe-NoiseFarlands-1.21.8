/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterestTypes {
    final static public RegistryKey<PointOfInterestType> ARMORER = PointOfInterestTypes.of("armorer");
    final static public RegistryKey<PointOfInterestType> BUTCHER = PointOfInterestTypes.of("butcher");
    final static public RegistryKey<PointOfInterestType> CARTOGRAPHER = PointOfInterestTypes.of("cartographer");
    final static public RegistryKey<PointOfInterestType> CLERIC = PointOfInterestTypes.of("cleric");
    final static public RegistryKey<PointOfInterestType> FARMER = PointOfInterestTypes.of("farmer");
    final static public RegistryKey<PointOfInterestType> FISHERMAN = PointOfInterestTypes.of("fisherman");
    final static public RegistryKey<PointOfInterestType> FLETCHER = PointOfInterestTypes.of("fletcher");
    final static public RegistryKey<PointOfInterestType> LEATHERWORKER = PointOfInterestTypes.of("leatherworker");
    final static public RegistryKey<PointOfInterestType> LIBRARIAN = PointOfInterestTypes.of("librarian");
    final static public RegistryKey<PointOfInterestType> MASON = PointOfInterestTypes.of("mason");
    final static public RegistryKey<PointOfInterestType> SHEPHERD = PointOfInterestTypes.of("shepherd");
    final static public RegistryKey<PointOfInterestType> TOOLSMITH = PointOfInterestTypes.of("toolsmith");
    final static public RegistryKey<PointOfInterestType> WEAPONSMITH = PointOfInterestTypes.of("weaponsmith");
    final static public RegistryKey<PointOfInterestType> HOME = PointOfInterestTypes.of("home");
    final static public RegistryKey<PointOfInterestType> MEETING = PointOfInterestTypes.of("meeting");
    final static public RegistryKey<PointOfInterestType> BEEHIVE = PointOfInterestTypes.of("beehive");
    final static public RegistryKey<PointOfInterestType> BEE_NEST = PointOfInterestTypes.of("bee_nest");
    final static public RegistryKey<PointOfInterestType> NETHER_PORTAL = PointOfInterestTypes.of("nether_portal");
    final static public RegistryKey<PointOfInterestType> LODESTONE = PointOfInterestTypes.of("lodestone");
    final static public RegistryKey<PointOfInterestType> LIGHTNING_ROD = PointOfInterestTypes.of("lightning_rod");
    final static public RegistryKey<PointOfInterestType> TEST_INSTANCE = PointOfInterestTypes.of("test_instance");
    final static private Set<BlockState> BED_HEADS = (Set)ImmutableList.of((Object)Blocks.RED_BED, (Object)Blocks.BLACK_BED, (Object)Blocks.BLUE_BED, (Object)Blocks.BROWN_BED, (Object)Blocks.CYAN_BED, (Object)Blocks.GRAY_BED, (Object)Blocks.GREEN_BED, (Object)Blocks.LIGHT_BLUE_BED, (Object)Blocks.LIGHT_GRAY_BED, (Object)Blocks.LIME_BED, (Object)Blocks.MAGENTA_BED, (Object)Blocks.ORANGE_BED, (Object[])new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap(block -> block.getStateManager().getStates().stream()).filter(state -> state.get(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
    final static private Set<BlockState> CAULDRONS = (Set)ImmutableList.of((Object)Blocks.CAULDRON, (Object)Blocks.LAVA_CAULDRON, (Object)Blocks.WATER_CAULDRON, (Object)Blocks.POWDER_SNOW_CAULDRON).stream().flatMap(block -> block.getStateManager().getStates().stream()).collect(ImmutableSet.toImmutableSet());
    final static private Map<BlockState, RegistryEntry<PointOfInterestType>> POI_STATES_TO_TYPE = Maps.newHashMap();

    private static Set<BlockState> getStatesOfBlock(Block block) {
        return ImmutableSet.copyOf(block.getStateManager().getStates());
    }

    private static RegistryKey<PointOfInterestType> of(String id) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.ofVanilla(id));
    }

    private static PointOfInterestType register(Registry<PointOfInterestType> registry, RegistryKey<PointOfInterestType> key, Set<BlockState> states, int ticketCount, int searchDistance) {
        PointOfInterestType pointOfInterestType = new PointOfInterestType(states, ticketCount, searchDistance);
        Registry.register(registry, key, pointOfInterestType);
        PointOfInterestTypes.registerStates(registry.getOrThrow(key), states);
        return pointOfInterestType;
    }

    private static void registerStates(RegistryEntry<PointOfInterestType> poiTypeEntry, Set<BlockState> states) {
        states.forEach(state -> {
            RegistryEntry<PointOfInterestType> registryEntry2 = POI_STATES_TO_TYPE.put((BlockState)state, poiTypeEntry);
            if (registryEntry2 != null) {
                throw Util.getFatalOrPause(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", state)));
            }
        });
    }

    public static Optional<RegistryEntry<PointOfInterestType>> getTypeForState(BlockState state) {
        return Optional.ofNullable(POI_STATES_TO_TYPE.get(state));
    }

    public static boolean isPointOfInterest(BlockState state) {
        return POI_STATES_TO_TYPE.containsKey(state);
    }

    public static PointOfInterestType registerAndGetDefault(Registry<PointOfInterestType> registry) {
        PointOfInterestTypes.register(registry, ARMORER, PointOfInterestTypes.getStatesOfBlock(Blocks.BLAST_FURNACE), 1, 1);
        PointOfInterestTypes.register(registry, BUTCHER, PointOfInterestTypes.getStatesOfBlock(Blocks.SMOKER), 1, 1);
        PointOfInterestTypes.register(registry, CARTOGRAPHER, PointOfInterestTypes.getStatesOfBlock(Blocks.CARTOGRAPHY_TABLE), 1, 1);
        PointOfInterestTypes.register(registry, CLERIC, PointOfInterestTypes.getStatesOfBlock(Blocks.BREWING_STAND), 1, 1);
        PointOfInterestTypes.register(registry, FARMER, PointOfInterestTypes.getStatesOfBlock(Blocks.COMPOSTER), 1, 1);
        PointOfInterestTypes.register(registry, FISHERMAN, PointOfInterestTypes.getStatesOfBlock(Blocks.BARREL), 1, 1);
        PointOfInterestTypes.register(registry, FLETCHER, PointOfInterestTypes.getStatesOfBlock(Blocks.FLETCHING_TABLE), 1, 1);
        PointOfInterestTypes.register(registry, LEATHERWORKER, CAULDRONS, 1, 1);
        PointOfInterestTypes.register(registry, LIBRARIAN, PointOfInterestTypes.getStatesOfBlock(Blocks.LECTERN), 1, 1);
        PointOfInterestTypes.register(registry, MASON, PointOfInterestTypes.getStatesOfBlock(Blocks.STONECUTTER), 1, 1);
        PointOfInterestTypes.register(registry, SHEPHERD, PointOfInterestTypes.getStatesOfBlock(Blocks.LOOM), 1, 1);
        PointOfInterestTypes.register(registry, TOOLSMITH, PointOfInterestTypes.getStatesOfBlock(Blocks.SMITHING_TABLE), 1, 1);
        PointOfInterestTypes.register(registry, WEAPONSMITH, PointOfInterestTypes.getStatesOfBlock(Blocks.GRINDSTONE), 1, 1);
        PointOfInterestTypes.register(registry, HOME, BED_HEADS, 1, 1);
        PointOfInterestTypes.register(registry, MEETING, PointOfInterestTypes.getStatesOfBlock(Blocks.BELL), 32, 6);
        PointOfInterestTypes.register(registry, BEEHIVE, PointOfInterestTypes.getStatesOfBlock(Blocks.BEEHIVE), 0, 1);
        PointOfInterestTypes.register(registry, BEE_NEST, PointOfInterestTypes.getStatesOfBlock(Blocks.BEE_NEST), 0, 1);
        PointOfInterestTypes.register(registry, NETHER_PORTAL, PointOfInterestTypes.getStatesOfBlock(Blocks.NETHER_PORTAL), 0, 1);
        PointOfInterestTypes.register(registry, LODESTONE, PointOfInterestTypes.getStatesOfBlock(Blocks.LODESTONE), 0, 1);
        PointOfInterestTypes.register(registry, TEST_INSTANCE, PointOfInterestTypes.getStatesOfBlock(Blocks.TEST_INSTANCE_BLOCK), 0, 1);
        return PointOfInterestTypes.register(registry, LIGHTNING_ROD, PointOfInterestTypes.getStatesOfBlock(Blocks.LIGHTNING_ROD), 0, 1);
    }
}

