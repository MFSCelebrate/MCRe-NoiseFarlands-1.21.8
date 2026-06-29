/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.util.Locale;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.structure.WoodlandMansionGenerator;

public interface StructurePieceType {
    final static public StructurePieceType MINESHAFT_CORRIDOR = StructurePieceType.register(MineshaftGenerator.MineshaftCorridor::new, "MSCorridor");
    final static public StructurePieceType MINESHAFT_CROSSING = StructurePieceType.register(MineshaftGenerator.MineshaftCrossing::new, "MSCrossing");
    final static public StructurePieceType MINESHAFT_ROOM = StructurePieceType.register(MineshaftGenerator.MineshaftRoom::new, "MSRoom");
    final static public StructurePieceType MINESHAFT_STAIRS = StructurePieceType.register(MineshaftGenerator.MineshaftStairs::new, "MSStairs");
    final static public StructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = StructurePieceType.register(NetherFortressGenerator.BridgeCrossing::new, "NeBCr");
    final static public StructurePieceType NETHER_FORTRESS_BRIDGE_END = StructurePieceType.register(NetherFortressGenerator.BridgeEnd::new, "NeBEF");
    final static public StructurePieceType NETHER_FORTRESS_BRIDGE = StructurePieceType.register(NetherFortressGenerator.Bridge::new, "NeBS");
    final static public StructurePieceType NETHER_FORTRESS_CORRIDOR_STAIRS = StructurePieceType.register(NetherFortressGenerator.CorridorStairs::new, "NeCCS");
    final static public StructurePieceType NETHER_FORTRESS_CORRIDOR_BALCONY = StructurePieceType.register(NetherFortressGenerator.CorridorBalcony::new, "NeCTB");
    final static public StructurePieceType NETHER_FORTRESS_CORRIDOR_EXIT = StructurePieceType.register(NetherFortressGenerator.CorridorExit::new, "NeCE");
    final static public StructurePieceType NETHER_FORTRESS_CORRIDOR_CROSSING = StructurePieceType.register(NetherFortressGenerator.CorridorCrossing::new, "NeSCSC");
    final static public StructurePieceType NETHER_FORTRESS_CORRIDOR_LEFT_TURN = StructurePieceType.register(NetherFortressGenerator.CorridorLeftTurn::new, "NeSCLT");
    final static public StructurePieceType NETHER_FORTRESS_SMALL_CORRIDOR = StructurePieceType.register(NetherFortressGenerator.SmallCorridor::new, "NeSC");
    final static public StructurePieceType NETHER_FORTRESS_CORRIDOR_RIGHT_TURN = StructurePieceType.register(NetherFortressGenerator.CorridorRightTurn::new, "NeSCRT");
    final static public StructurePieceType NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM = StructurePieceType.register(NetherFortressGenerator.CorridorNetherWartsRoom::new, "NeCSR");
    final static public StructurePieceType NETHER_FORTRESS_BRIDGE_PLATFORM = StructurePieceType.register(NetherFortressGenerator.BridgePlatform::new, "NeMT");
    final static public StructurePieceType NETHER_FORTRESS_BRIDGE_SMALL_CROSSING = StructurePieceType.register(NetherFortressGenerator.BridgeSmallCrossing::new, "NeRC");
    final static public StructurePieceType NETHER_FORTRESS_BRIDGE_STAIRS = StructurePieceType.register(NetherFortressGenerator.BridgeStairs::new, "NeSR");
    final static public StructurePieceType NETHER_FORTRESS_START = StructurePieceType.register(NetherFortressGenerator.Start::new, "NeStart");
    final static public StructurePieceType STRONGHOLD_CHEST_CORRIDOR = StructurePieceType.register(StrongholdGenerator.ChestCorridor::new, "SHCC");
    final static public StructurePieceType STRONGHOLD_SMALL_CORRIDOR = StructurePieceType.register(StrongholdGenerator.SmallCorridor::new, "SHFC");
    final static public StructurePieceType STRONGHOLD_FIVE_WAY_CROSSING = StructurePieceType.register(StrongholdGenerator.FiveWayCrossing::new, "SH5C");
    final static public StructurePieceType STRONGHOLD_LEFT_TURN = StructurePieceType.register(StrongholdGenerator.LeftTurn::new, "SHLT");
    final static public StructurePieceType STRONGHOLD_LIBRARY = StructurePieceType.register(StrongholdGenerator.Library::new, "SHLi");
    final static public StructurePieceType STRONGHOLD_PORTAL_ROOM = StructurePieceType.register(StrongholdGenerator.PortalRoom::new, "SHPR");
    final static public StructurePieceType STRONGHOLD_PRISON_HALL = StructurePieceType.register(StrongholdGenerator.PrisonHall::new, "SHPH");
    final static public StructurePieceType STRONGHOLD_RIGHT_TURN = StructurePieceType.register(StrongholdGenerator.RightTurn::new, "SHRT");
    final static public StructurePieceType STRONGHOLD_SQUARE_ROOM = StructurePieceType.register(StrongholdGenerator.SquareRoom::new, "SHRC");
    final static public StructurePieceType STRONGHOLD_SPIRAL_STAIRCASE = StructurePieceType.register(StrongholdGenerator.SpiralStaircase::new, "SHSD");
    final static public StructurePieceType STRONGHOLD_START = StructurePieceType.register(StrongholdGenerator.Start::new, "SHStart");
    final static public StructurePieceType STRONGHOLD_CORRIDOR = StructurePieceType.register(StrongholdGenerator.Corridor::new, "SHS");
    final static public StructurePieceType STRONGHOLD_STAIRS = StructurePieceType.register(StrongholdGenerator.Stairs::new, "SHSSD");
    final static public StructurePieceType JUNGLE_TEMPLE = StructurePieceType.register(JungleTempleGenerator::new, "TeJP");
    final static public StructurePieceType OCEAN_TEMPLE = StructurePieceType.register(OceanRuinGenerator.Piece::fromNbt, "ORP");
    final static public StructurePieceType IGLOO = StructurePieceType.register(IglooGenerator.Piece::new, "Iglu");
    final static public StructurePieceType RUINED_PORTAL = StructurePieceType.register(RuinedPortalStructurePiece::new, "RUPO");
    final static public StructurePieceType SWAMP_HUT = StructurePieceType.register(SwampHutGenerator::new, "TeSH");
    final static public StructurePieceType DESERT_TEMPLE = StructurePieceType.register(DesertTempleGenerator::new, "TeDP");
    final static public StructurePieceType OCEAN_MONUMENT_BASE = StructurePieceType.register(OceanMonumentGenerator.Base::new, "OMB");
    final static public StructurePieceType OCEAN_MONUMENT_CORE_ROOM = StructurePieceType.register(OceanMonumentGenerator.CoreRoom::new, "OMCR");
    final static public StructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleXRoom::new, "OMDXR");
    final static public StructurePieceType OCEAN_MONUMENT_DOUBLE_X_Y_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleXYRoom::new, "OMDXYR");
    final static public StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleYRoom::new, "OMDYR");
    final static public StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleYZRoom::new, "OMDYZR");
    final static public StructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleZRoom::new, "OMDZR");
    final static public StructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = StructurePieceType.register(OceanMonumentGenerator.Entry::new, "OMEntry");
    final static public StructurePieceType OCEAN_MONUMENT_PENTHOUSE = StructurePieceType.register(OceanMonumentGenerator.Penthouse::new, "OMPenthouse");
    final static public StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = StructurePieceType.register(OceanMonumentGenerator.SimpleRoom::new, "OMSimple");
    final static public StructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = StructurePieceType.register(OceanMonumentGenerator.SimpleRoomTop::new, "OMSimpleT");
    final static public StructurePieceType OCEAN_MONUMENT_WING_ROOM = StructurePieceType.register(OceanMonumentGenerator.WingRoom::new, "OMWR");
    final static public StructurePieceType END_CITY = StructurePieceType.register(EndCityGenerator.Piece::new, "ECP");
    final static public StructurePieceType WOODLAND_MANSION = StructurePieceType.register(WoodlandMansionGenerator.Piece::new, "WMP");
    final static public StructurePieceType BURIED_TREASURE = StructurePieceType.register(BuriedTreasureGenerator.Piece::new, "BTP");
    final static public StructurePieceType SHIPWRECK = StructurePieceType.register(ShipwreckGenerator.Piece::new, "Shipwreck");
    final static public StructurePieceType NETHER_FOSSIL = StructurePieceType.register(NetherFossilGenerator.Piece::new, "NeFos");
    final static public StructurePieceType JIGSAW = StructurePieceType.register(PoolStructurePiece::new, "jigsaw");

    public StructurePiece load(StructureContext var1, NbtCompound var2);

    private static StructurePieceType register(StructurePieceType type, String id) {
        return Registry.register(Registries.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), type);
    }

    private static StructurePieceType register(Simple type, String id) {
        return StructurePieceType.register((StructurePieceType)type, id);
    }

    private static StructurePieceType register(ManagerAware type, String id) {
        return StructurePieceType.register((StructurePieceType)type, id);
    }

    public static interface Simple
    extends StructurePieceType {
        public StructurePiece load(NbtCompound var1);

        @Override
        default public StructurePiece load(StructureContext structureContext, NbtCompound nbtCompound) {
            return this.load(nbtCompound);
        }
    }

    public static interface ManagerAware
    extends StructurePieceType {
        public StructurePiece load(StructureTemplateManager var1, NbtCompound var2);

        @Override
        default public StructurePiece load(StructureContext structureContext, NbtCompound nbtCompound) {
            return this.load(structureContext.structureTemplateManager(), nbtCompound);
        }
    }
}

