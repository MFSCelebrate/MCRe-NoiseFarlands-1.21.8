/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.blockpredicate.AllOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.AlwaysTrueBlockPredicate;
import net.minecraft.world.gen.blockpredicate.AnyOfBlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.HasSturdyFacePredicate;
import net.minecraft.world.gen.blockpredicate.InsideWorldBoundsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingBlockTagPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingBlocksBlockPredicate;
import net.minecraft.world.gen.blockpredicate.MatchingFluidsBlockPredicate;
import net.minecraft.world.gen.blockpredicate.NotBlockPredicate;
import net.minecraft.world.gen.blockpredicate.ReplaceableBlockPredicate;
import net.minecraft.world.gen.blockpredicate.SolidBlockPredicate;
import net.minecraft.world.gen.blockpredicate.UnobstructedBlockPredicate;
import net.minecraft.world.gen.blockpredicate.WouldSurviveBlockPredicate;

public interface BlockPredicateType<P extends BlockPredicate> {
    final static public BlockPredicateType<MatchingBlocksBlockPredicate> MATCHING_BLOCKS = BlockPredicateType.register("matching_blocks", MatchingBlocksBlockPredicate.CODEC);
    final static public BlockPredicateType<MatchingBlockTagPredicate> MATCHING_BLOCK_TAG = BlockPredicateType.register("matching_block_tag", MatchingBlockTagPredicate.CODEC);
    final static public BlockPredicateType<MatchingFluidsBlockPredicate> MATCHING_FLUIDS = BlockPredicateType.register("matching_fluids", MatchingFluidsBlockPredicate.CODEC);
    final static public BlockPredicateType<HasSturdyFacePredicate> HAS_STURDY_FACE = BlockPredicateType.register("has_sturdy_face", HasSturdyFacePredicate.CODEC);
    final static public BlockPredicateType<SolidBlockPredicate> SOLID = BlockPredicateType.register("solid", SolidBlockPredicate.CODEC);
    final static public BlockPredicateType<ReplaceableBlockPredicate> REPLACEABLE = BlockPredicateType.register("replaceable", ReplaceableBlockPredicate.CODEC);
    final static public BlockPredicateType<WouldSurviveBlockPredicate> WOULD_SURVIVE = BlockPredicateType.register("would_survive", WouldSurviveBlockPredicate.CODEC);
    final static public BlockPredicateType<InsideWorldBoundsBlockPredicate> INSIDE_WORLD_BOUNDS = BlockPredicateType.register("inside_world_bounds", InsideWorldBoundsBlockPredicate.CODEC);
    final static public BlockPredicateType<AnyOfBlockPredicate> ANY_OF = BlockPredicateType.register("any_of", AnyOfBlockPredicate.CODEC);
    final static public BlockPredicateType<AllOfBlockPredicate> ALL_OF = BlockPredicateType.register("all_of", AllOfBlockPredicate.CODEC);
    final static public BlockPredicateType<NotBlockPredicate> NOT = BlockPredicateType.register("not", NotBlockPredicate.CODEC);
    final static public BlockPredicateType<AlwaysTrueBlockPredicate> TRUE = BlockPredicateType.register("true", AlwaysTrueBlockPredicate.CODEC);
    final static public BlockPredicateType<UnobstructedBlockPredicate> UNOBSTRUCTED = BlockPredicateType.register("unobstructed", UnobstructedBlockPredicate.CODEC);

    public MapCodec<P> codec();

    private static <P extends BlockPredicate> BlockPredicateType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.BLOCK_PREDICATE_TYPE, id, () -> codec);
    }
}

