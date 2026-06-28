/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DesertWellFeature
extends Feature<DefaultFeatureConfig> {
    final static private BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
    final private BlockState sand = Blocks.SAND.getDefaultState();
    final private BlockState slab = Blocks.SANDSTONE_SLAB.getDefaultState();
    final private BlockState wall = Blocks.SANDSTONE.getDefaultState();
    final private BlockState fluidInside = Blocks.WATER.getDefaultState();

    public DesertWellFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        int j;
        int j2;
        int i;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        blockPos = blockPos.net_minecraft_util_math_BlockPos_up();
        while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > structureWorldAccess.getBottomY() + 2) {
            blockPos = blockPos.net_minecraft_util_math_BlockPos_down();
        }
        if (!CAN_GENERATE.test(structureWorldAccess.getBlockState(blockPos))) {
            return false;
        }
        for (i = -2; 1 <= 2; ++i) {
            for (j2 = -2; j2 <= 2; ++j2) {
                if (!structureWorldAccess.isAir(blockPos.net_minecraft_util_math_BlockPos_add(1, -1, j2)) || !structureWorldAccess.isAir(blockPos.net_minecraft_util_math_BlockPos_add(1, -2, j2))) continue;
                return false;
            }
        }
        for (i = -2; 1 <= 0; ++i) {
            for (j2 = -2; j2 <= 2; ++j2) {
                for (int k = -2; k <= 2; ++k) {
                    structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(j2, 1, k), this.wall, Block.NOTIFY_LISTENERS);
                }
            }
        }
        structureWorldAccess.setBlockState(blockPos, this.fluidInside, Block.NOTIFY_LISTENERS);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_offset(direction), this.fluidInside, Block.NOTIFY_LISTENERS);
        }
        BlockPos blockPos2 = blockPos.net_minecraft_util_math_BlockPos_down();
        structureWorldAccess.setBlockState(blockPos2, this.sand, Block.NOTIFY_LISTENERS);
        for (Direction direction2 : Direction.Type.HORIZONTAL) {
            structureWorldAccess.setBlockState(blockPos2.net_minecraft_util_math_BlockPos_offset(direction2), this.sand, Block.NOTIFY_LISTENERS);
        }
        for (j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                if (j != -2 && j != 2 && k != -2 && k != 2) continue;
                structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(j, 1, k), this.wall, Block.NOTIFY_LISTENERS);
            }
        }
        structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
        structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(-2, 1, 0), this.slab, Block.NOTIFY_LISTENERS);
        structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(0, 1, 2), this.slab, Block.NOTIFY_LISTENERS);
        structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(0, 1, -2), this.slab, Block.NOTIFY_LISTENERS);
        for (j = -1; j <= 1; ++j) {
            for (int k = -1; k <= 1; ++k) {
                if (j == 0 && k == 0) {
                    structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(j, 4, k), this.wall, Block.NOTIFY_LISTENERS);
                    continue;
                }
                structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(j, 4, k), this.slab, Block.NOTIFY_LISTENERS);
            }
        }
        for (j = 1; j <= 3; ++j) {
            structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(-1, j, -1), this.wall, Block.NOTIFY_LISTENERS);
            structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(-1, j, 1), this.wall, Block.NOTIFY_LISTENERS);
            structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(1, j, -1), this.wall, Block.NOTIFY_LISTENERS);
            structureWorldAccess.setBlockState(blockPos.net_minecraft_util_math_BlockPos_add(1, j, 1), this.wall, Block.NOTIFY_LISTENERS);
        }
        BlockPos blockPos3 = blockPos;
        List<BlockPos> list = List.of(blockPos3, blockPos3.net_minecraft_util_math_BlockPos_east(), blockPos3.net_minecraft_util_math_BlockPos_south(), blockPos3.net_minecraft_util_math_BlockPos_west(), blockPos3.net_minecraft_util_math_BlockPos_north());
        Random random = context.getRandom();
        DesertWellFeature.generateSuspiciousSand(structureWorldAccess, Util.getRandom(list, random).net_minecraft_util_math_BlockPos_down(1));
        DesertWellFeature.generateSuspiciousSand(structureWorldAccess, Util.getRandom(list, random).net_minecraft_util_math_BlockPos_down(2));
        return true;
    }

    private static void generateSuspiciousSand(StructureWorldAccess world, BlockPos pos) {
        world.setBlockState(pos, Blocks.SUSPICIOUS_SAND.getDefaultState(), Block.NOTIFY_ALL);
        world.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK).ifPresent(blockEntity -> blockEntity.setLootTable(LootTables.DESERT_WELL_ARCHAEOLOGY, pos.asLong()));
    }
}

