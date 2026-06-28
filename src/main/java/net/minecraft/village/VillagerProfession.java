/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;

public record VillagerProfession(Text id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
    final static public Predicate<RegistryEntry<PointOfInterestType>> IS_ACQUIRABLE_JOB_SITE = poiType -> poiType.isIn(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE);
    final static public RegistryKey<VillagerProfession> NONE = VillagerProfession.of("none");
    final static public RegistryKey<VillagerProfession> ARMORER = VillagerProfession.of("armorer");
    final static public RegistryKey<VillagerProfession> BUTCHER = VillagerProfession.of("butcher");
    final static public RegistryKey<VillagerProfession> CARTOGRAPHER = VillagerProfession.of("cartographer");
    final static public RegistryKey<VillagerProfession> CLERIC = VillagerProfession.of("cleric");
    final static public RegistryKey<VillagerProfession> FARMER = VillagerProfession.of("farmer");
    final static public RegistryKey<VillagerProfession> FISHERMAN = VillagerProfession.of("fisherman");
    final static public RegistryKey<VillagerProfession> FLETCHER = VillagerProfession.of("fletcher");
    final static public RegistryKey<VillagerProfession> LEATHERWORKER = VillagerProfession.of("leatherworker");
    final static public RegistryKey<VillagerProfession> LIBRARIAN = VillagerProfession.of("librarian");
    final static public RegistryKey<VillagerProfession> MASON = VillagerProfession.of("mason");
    final static public RegistryKey<VillagerProfession> NITWIT = VillagerProfession.of("nitwit");
    final static public RegistryKey<VillagerProfession> SHEPHERD = VillagerProfession.of("shepherd");
    final static public RegistryKey<VillagerProfession> TOOLSMITH = VillagerProfession.of("toolsmith");
    final static public RegistryKey<VillagerProfession> WEAPONSMITH = VillagerProfession.of("weaponsmith");

    private static RegistryKey<VillagerProfession> of(String id) {
        return RegistryKey.of(RegistryKeys.VILLAGER_PROFESSION, Identifier.ofVanilla(id));
    }

    private static VillagerProfession register(Registry<VillagerProfession> registry, RegistryKey<VillagerProfession> key, RegistryKey<PointOfInterestType> heldWorkstation, @Nullable SoundEvent workSound) {
        return VillagerProfession.register(registry, key, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), workSound);
    }

    private static VillagerProfession register(Registry<VillagerProfession> registry, RegistryKey<VillagerProfession> key, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, @Nullable SoundEvent workSound) {
        return VillagerProfession.register(registry, key, heldWorkstation, acquirableWorkstation, (ImmutableSet<Item>)ImmutableSet.of(), (ImmutableSet<Block>)ImmutableSet.of(), workSound);
    }

    private static VillagerProfession register(Registry<VillagerProfession> registry, RegistryKey<VillagerProfession> key, RegistryKey<PointOfInterestType> heldWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return VillagerProfession.register(registry, key, entry -> entry.matchesKey(heldWorkstation), entry -> entry.matchesKey(heldWorkstation), gatherableItems, secondaryJobSites, workSound);
    }

    private static VillagerProfession register(Registry<VillagerProfession> registry, RegistryKey<VillagerProfession> key, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(registry, key, new VillagerProfession(Text.translatable("entity." + key.getValue().getNamespace() + ".villager." + key.getValue().getPath()), heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
    }

    public static VillagerProfession registerAndGetDefault(Registry<VillagerProfession> registry) {
        VillagerProfession.register(registry, NONE, PointOfInterestType.NONE, IS_ACQUIRABLE_JOB_SITE, null);
        VillagerProfession.register(registry, ARMORER, PointOfInterestTypes.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
        VillagerProfession.register(registry, BUTCHER, PointOfInterestTypes.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
        VillagerProfession.register(registry, CARTOGRAPHER, PointOfInterestTypes.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
        VillagerProfession.register(registry, CLERIC, PointOfInterestTypes.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
        VillagerProfession.register(registry, FARMER, PointOfInterestTypes.FARMER, (ImmutableSet<Item>)ImmutableSet.of((Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS, (Object)Items.BONE_MEAL), (ImmutableSet<Block>)ImmutableSet.of((Object)Blocks.FARMLAND), SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
        VillagerProfession.register(registry, FISHERMAN, PointOfInterestTypes.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
        VillagerProfession.register(registry, FLETCHER, PointOfInterestTypes.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
        VillagerProfession.register(registry, LEATHERWORKER, PointOfInterestTypes.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER);
        VillagerProfession.register(registry, LIBRARIAN, PointOfInterestTypes.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
        VillagerProfession.register(registry, MASON, PointOfInterestTypes.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
        VillagerProfession.register(registry, NITWIT, PointOfInterestType.NONE, PointOfInterestType.NONE, null);
        VillagerProfession.register(registry, SHEPHERD, PointOfInterestTypes.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
        VillagerProfession.register(registry, TOOLSMITH, PointOfInterestTypes.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
        return VillagerProfession.register(registry, WEAPONSMITH, PointOfInterestTypes.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{VillagerProfession.class, "name;heldJobSite;acquirableJobSite;requestedItems;secondaryPoi;workSound", "id", "heldWorkstation", "acquirableWorkstation", "gatherableItems", "secondaryJobSites", "workSound"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{VillagerProfession.class, "name;heldJobSite;acquirableJobSite;requestedItems;secondaryPoi;workSound", "id", "heldWorkstation", "acquirableWorkstation", "gatherableItems", "secondaryJobSites", "workSound"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{VillagerProfession.class, "name;heldJobSite;acquirableJobSite;requestedItems;secondaryPoi;workSound", "id", "heldWorkstation", "acquirableWorkstation", "gatherableItems", "secondaryJobSites", "workSound"}, this, object);
    }
}

