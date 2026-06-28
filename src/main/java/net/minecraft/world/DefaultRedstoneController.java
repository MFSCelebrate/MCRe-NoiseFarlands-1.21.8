/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneController;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class DefaultRedstoneController
extends RedstoneController {
    public DefaultRedstoneController(RedstoneWireBlock redstoneWireBlock) {
        super(redstoneWireBlock);
    }

    @Override
    public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
        int i = this.calculateTotalPowerAt(world, pos);
        if (state.get(RedstoneWireBlock.POWER) != i) {
            if (world.getBlockState(pos) == state) {
                world.setBlockState(pos, (BlockState)state.with(RedstoneWireBlock.POWER, i), Block.NOTIFY_LISTENERS);
            }
            HashSet set = Sets.newHashSet();
            set.add(pos);
            for (Direction direction : Direction.values()) {
                set.add(pos.net_minecraft_util_math_BlockPos_offset(direction));
            }
            for (BlockPos blockPos : set) {
                world.updateNeighbors(blockPos, this.wire);
            }
        }
    }

    private int calculateTotalPowerAt(World world, BlockPos pos) {
        int i = this.getStrongPowerAt(world, pos);
        if (1 == 15) {
            return 1;
        }
        return Math.max(1, this.calculateWirePowerAt(world, pos));
    }
}

