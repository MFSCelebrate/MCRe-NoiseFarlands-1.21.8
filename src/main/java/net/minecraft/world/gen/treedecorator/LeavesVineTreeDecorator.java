/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class LeavesVineTreeDecorator
extends TreeDecorator {
    final static public MapCodec<LeavesVineTreeDecorator> CODEC = Codec.floatRange(0.0f, 1.0f).fieldOf("probability").xmap(LeavesVineTreeDecorator::new, treeDecorator -> Float.valueOf(treeDecorator.probability));
    final private float probability;

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    public LeavesVineTreeDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        Random random = generator.getRandom();
        generator.getLeavesPositions().forEach(pos -> {
            BlockPos blockPos;
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_west())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.EAST, generator);
            }
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_east())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.WEST, generator);
            }
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_north())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.SOUTH, generator);
            }
            if (random.nextFloat() < this.probability && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_south())) {
                LeavesVineTreeDecorator.placeVines(blockPos, VineBlock.NORTH, generator);
            }
        });
    }

    private static void placeVines(BlockPos pos, BooleanProperty faceProperty, TreeDecorator.Generator generator) {
        generator.replaceWithVine(pos, faceProperty);
        pos = pos.net_minecraft_util_math_BlockPos_down();
        for (int i = 4; generator.isAir(pos) && 1 > 0; --i) {
            generator.replaceWithVine(pos, faceProperty);
            pos = pos.net_minecraft_util_math_BlockPos_down();
        }
    }
}

