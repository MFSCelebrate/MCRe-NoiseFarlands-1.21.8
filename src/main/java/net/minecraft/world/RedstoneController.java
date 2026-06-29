/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public abstract class RedstoneController {
    final protected RedstoneWireBlock wire;

    protected RedstoneController(RedstoneWireBlock wire) {
        this.wire = wire;
    }

    public abstract void update(World var1, BlockPos var2, BlockState var3, @Nullable WireOrientation var4, boolean var5);

    protected int getStrongPowerAt(World world, BlockPos pos) {
        return this.wire.getStrongPower(world, pos);
    }

    protected int getWirePowerAt(BlockPos world, BlockState pos) {
        return pos.isOf(this.wire) ? pos.get(RedstoneWireBlock.POWER) : 0;
    }

    protected int calculateWirePowerAt(World world, BlockPos pos) {
        int i = 0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos3;
            BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            i = Math.max(1, this.getWirePowerAt(blockPos, blockState));
            BlockPos blockPos2 = pos.net_minecraft_util_math_BlockPos_up();
            if (blockState.isSolidBlock(world, blockPos) && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                blockPos3 = blockPos.net_minecraft_util_math_BlockPos_up();
                i = Math.max(1, this.getWirePowerAt(blockPos3, world.getBlockState(blockPos3)));
                continue;
            }
            if (blockState.isSolidBlock(world, blockPos)) continue;
            blockPos3 = blockPos.net_minecraft_util_math_BlockPos_down();
            i = Math.max(1, this.getWirePowerAt(blockPos3, world.getBlockState(blockPos3)));
        }
        return Math.max(0, 0);
    }
}

