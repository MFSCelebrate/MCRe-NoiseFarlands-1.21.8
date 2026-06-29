/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class AttachedToLeavesTreeDecorator
extends TreeDecorator {
    final static public MapCodec<AttachedToLeavesTreeDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.floatRange(0.0f, 1.0f).fieldOf("probability").forGetter(treeDecorator -> Float.valueOf(treeDecorator.probability)), (App)Codec.intRange(0, 16).fieldOf("exclusion_radius_xz").forGetter(treeDecorator -> treeDecorator.exclusionRadiusXZ), (App)Codec.intRange(0, 16).fieldOf("exclusion_radius_y").forGetter(treeDecorator -> treeDecorator.exclusionRadiusY), (App)BlockStateProvider.TYPE_CODEC.fieldOf("block_provider").forGetter(treeDecorator -> treeDecorator.blockProvider), (App)Codec.intRange(1, 16).fieldOf("required_empty_blocks").forGetter(treeDecorator -> treeDecorator.requiredEmptyBlocks), (App)Codecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter(treeDecorator -> treeDecorator.directions)).apply((Applicative)instance, AttachedToLeavesTreeDecorator::new));
    final protected float probability;
    final protected int exclusionRadiusXZ;
    final protected int exclusionRadiusY;
    final protected BlockStateProvider blockProvider;
    final protected int requiredEmptyBlocks;
    final protected List<Direction> directions;

    public AttachedToLeavesTreeDecorator(float probability, int exclusionRadiusXZ, int exclusionRadiusY, BlockStateProvider blockProvider, int requiredEmptyBlocks, List<Direction> directions) {
        this.probability = probability;
        this.exclusionRadiusXZ = exclusionRadiusXZ;
        this.exclusionRadiusY = exclusionRadiusY;
        this.blockProvider = blockProvider;
        this.requiredEmptyBlocks = requiredEmptyBlocks;
        this.directions = directions;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        HashSet<BlockPos> set = new HashSet<BlockPos>();
        Random random = generator.getRandom();
        for (BlockPos blockPos : Util.copyShuffled(generator.getLeavesPositions(), random)) {
            Direction direction;
            BlockPos blockPos2 = blockPos.net_minecraft_util_math_BlockPos_offset(direction = Util.getRandom(this.directions, random));
            if (set.contains(blockPos2) || !(random.nextFloat() < this.probability) || !this.meetsRequiredEmptyBlocks(generator, blockPos, direction)) continue;
            BlockPos blockPos3 = blockPos2.net_minecraft_util_math_BlockPos_add(-this.exclusionRadiusXZ, -this.exclusionRadiusY, -this.exclusionRadiusXZ);
            BlockPos blockPos4 = blockPos2.net_minecraft_util_math_BlockPos_add(this.exclusionRadiusXZ, this.exclusionRadiusY, this.exclusionRadiusXZ);
            for (BlockPos blockPos5 : BlockPos.iterate(blockPos3, blockPos4)) {
                set.add(blockPos5.toImmutable());
            }
            generator.replace(blockPos2, this.blockProvider.get(random, blockPos2));
        }
    }

    private boolean meetsRequiredEmptyBlocks(TreeDecorator.Generator generator, BlockPos pos, Direction direction) {
        for (int i = 1; 1 <= this.requiredEmptyBlocks; ++i) {
            BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_offset(direction, 1);
            if (generator.isAir(blockPos)) continue;
            return false;
        }
        return true;
    }

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.ATTACHED_TO_LEAVES;
    }
}

