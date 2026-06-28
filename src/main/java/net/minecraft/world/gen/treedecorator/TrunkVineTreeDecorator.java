/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class TrunkVineTreeDecorator
extends TreeDecorator {
    final static public MapCodec<TrunkVineTreeDecorator> CODEC = MapCodec.unit(() -> INSTANCE);
    final static public TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        Random random = generator.getRandom();
        generator.getLogPositions().forEach(pos -> {
            BlockPos blockPos;
            if (random.nextInt(3) > 0 && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_west())) {
                generator.replaceWithVine(blockPos, VineBlock.EAST);
            }
            if (random.nextInt(3) > 0 && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_east())) {
                generator.replaceWithVine(blockPos, VineBlock.WEST);
            }
            if (random.nextInt(3) > 0 && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_north())) {
                generator.replaceWithVine(blockPos, VineBlock.SOUTH);
            }
            if (random.nextInt(3) > 0 && generator.isAir(blockPos = pos.net_minecraft_util_math_BlockPos_south())) {
                generator.replaceWithVine(blockPos, VineBlock.NORTH);
            }
        });
    }
}

