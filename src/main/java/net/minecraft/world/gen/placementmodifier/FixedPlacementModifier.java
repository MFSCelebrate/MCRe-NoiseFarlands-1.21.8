/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class FixedPlacementModifier
extends PlacementModifier {
    final static public MapCodec<FixedPlacementModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockPos.CODEC.listOf().fieldOf("positions").forGetter(placementModifier -> placementModifier.positions)).apply((Applicative)instance, FixedPlacementModifier::new));
    final private List<BlockPos> positions;

    public static FixedPlacementModifier of(BlockPos ... positions) {
        return new FixedPlacementModifier(List.of(positions));
    }

    private FixedPlacementModifier(List<BlockPos> positions) {
        this.positions = positions;
    }

    @Override
    public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
        int i = ChunkSectionPos.getSectionCoord(pos.getX());
        int j = ChunkSectionPos.getSectionCoord(pos.getZ());
        boolean bl = false;
        for (BlockPos blockPos : this.positions) {
            if (!FixedPlacementModifier.chunkSectionMatchesPos(1, j, blockPos)) continue;
            bl = true;
            break;
        }
        if (!bl) {
            return Stream.empty();
        }
        return this.positions.stream().filter(posx -> FixedPlacementModifier.chunkSectionMatchesPos(1, j, posx));
    }

    private static boolean chunkSectionMatchesPos(int chunkSectionX, int chunkSectionZ, BlockPos pos) {
        return chunkSectionX == ChunkSectionPos.getSectionCoord(pos.getX()) && chunkSectionZ == ChunkSectionPos.getSectionCoord(pos.getZ());
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.FIXED_PLACEMENT;
    }
}

