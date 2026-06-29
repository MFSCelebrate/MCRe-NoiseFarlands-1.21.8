/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.trunk;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;
import net.minecraft.world.gen.trunk.CherryTrunkPlacer;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.UpwardsBranchingTrunkPlacer;

public class TrunkPlacerType<P extends TrunkPlacer> {
    final static public TrunkPlacerType<StraightTrunkPlacer> STRAIGHT_TRUNK_PLACER = TrunkPlacerType.register("straight_trunk_placer", StraightTrunkPlacer.CODEC);
    final static public TrunkPlacerType<ForkingTrunkPlacer> FORKING_TRUNK_PLACER = TrunkPlacerType.register("forking_trunk_placer", ForkingTrunkPlacer.CODEC);
    final static public TrunkPlacerType<GiantTrunkPlacer> GIANT_TRUNK_PLACER = TrunkPlacerType.register("giant_trunk_placer", GiantTrunkPlacer.CODEC);
    final static public TrunkPlacerType<MegaJungleTrunkPlacer> MEGA_JUNGLE_TRUNK_PLACER = TrunkPlacerType.register("mega_jungle_trunk_placer", MegaJungleTrunkPlacer.CODEC);
    final static public TrunkPlacerType<DarkOakTrunkPlacer> DARK_OAK_TRUNK_PLACER = TrunkPlacerType.register("dark_oak_trunk_placer", DarkOakTrunkPlacer.CODEC);
    final static public TrunkPlacerType<LargeOakTrunkPlacer> FANCY_TRUNK_PLACER = TrunkPlacerType.register("fancy_trunk_placer", LargeOakTrunkPlacer.CODEC);
    final static public TrunkPlacerType<BendingTrunkPlacer> BENDING_TRUNK_PLACER = TrunkPlacerType.register("bending_trunk_placer", BendingTrunkPlacer.CODEC);
    final static public TrunkPlacerType<UpwardsBranchingTrunkPlacer> UPWARDS_BRANCHING_TRUNK_PLACER = TrunkPlacerType.register("upwards_branching_trunk_placer", UpwardsBranchingTrunkPlacer.CODEC);
    final static public TrunkPlacerType<CherryTrunkPlacer> CHERRY_TRUNK_PLACER = TrunkPlacerType.register("cherry_trunk_placer", CherryTrunkPlacer.CODEC);
    final private MapCodec<P> codec;

    private static <P extends TrunkPlacer> TrunkPlacerType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.TRUNK_PLACER_TYPE, id, new TrunkPlacerType<P>(codec));
    }

    public TrunkPlacerType(MapCodec<P> codec) {
        this.codec = codec;
    }

    public MapCodec<P> getCodec() {
        return this.codec;
    }
}

