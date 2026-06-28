/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.BuriedTreasureStructure;
import net.minecraft.world.gen.structure.DesertPyramidStructure;
import net.minecraft.world.gen.structure.EndCityStructure;
import net.minecraft.world.gen.structure.IglooStructure;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.JungleTempleStructure;
import net.minecraft.world.gen.structure.MineshaftStructure;
import net.minecraft.world.gen.structure.NetherFortressStructure;
import net.minecraft.world.gen.structure.NetherFossilStructure;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import net.minecraft.world.gen.structure.OceanRuinStructure;
import net.minecraft.world.gen.structure.RuinedPortalStructure;
import net.minecraft.world.gen.structure.ShipwreckStructure;
import net.minecraft.world.gen.structure.StrongholdStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.SwampHutStructure;
import net.minecraft.world.gen.structure.WoodlandMansionStructure;

public interface StructureType<S extends Structure> {
    final static public StructureType<BuriedTreasureStructure> BURIED_TREASURE = StructureType.register("buried_treasure", BuriedTreasureStructure.CODEC);
    final static public StructureType<DesertPyramidStructure> DESERT_PYRAMID = StructureType.register("desert_pyramid", DesertPyramidStructure.CODEC);
    final static public StructureType<EndCityStructure> END_CITY = StructureType.register("end_city", EndCityStructure.CODEC);
    final static public StructureType<NetherFortressStructure> FORTRESS = StructureType.register("fortress", NetherFortressStructure.CODEC);
    final static public StructureType<IglooStructure> IGLOO = StructureType.register("igloo", IglooStructure.CODEC);
    final static public StructureType<JigsawStructure> JIGSAW = StructureType.register("jigsaw", JigsawStructure.CODEC);
    final static public StructureType<JungleTempleStructure> JUNGLE_TEMPLE = StructureType.register("jungle_temple", JungleTempleStructure.CODEC);
    final static public StructureType<MineshaftStructure> MINESHAFT = StructureType.register("mineshaft", MineshaftStructure.CODEC);
    final static public StructureType<NetherFossilStructure> NETHER_FOSSIL = StructureType.register("nether_fossil", NetherFossilStructure.CODEC);
    final static public StructureType<OceanMonumentStructure> OCEAN_MONUMENT = StructureType.register("ocean_monument", OceanMonumentStructure.CODEC);
    final static public StructureType<OceanRuinStructure> OCEAN_RUIN = StructureType.register("ocean_ruin", OceanRuinStructure.CODEC);
    final static public StructureType<RuinedPortalStructure> RUINED_PORTAL = StructureType.register("ruined_portal", RuinedPortalStructure.CODEC);
    final static public StructureType<ShipwreckStructure> SHIPWRECK = StructureType.register("shipwreck", ShipwreckStructure.CODEC);
    final static public StructureType<StrongholdStructure> STRONGHOLD = StructureType.register("stronghold", StrongholdStructure.CODEC);
    final static public StructureType<SwampHutStructure> SWAMP_HUT = StructureType.register("swamp_hut", SwampHutStructure.CODEC);
    final static public StructureType<WoodlandMansionStructure> WOODLAND_MANSION = StructureType.register("woodland_mansion", WoodlandMansionStructure.CODEC);

    public MapCodec<S> codec();

    private static <S extends Structure> StructureType<S> register(String id, MapCodec<S> codec) {
        return Registry.register(Registries.STRUCTURE_TYPE, id, () -> codec);
    }
}

