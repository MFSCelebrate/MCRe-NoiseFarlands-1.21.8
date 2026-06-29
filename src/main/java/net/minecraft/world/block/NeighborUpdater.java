/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.block;

import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public interface NeighborUpdater {
    final static public Direction[] UPDATE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH};

    public void replaceWithStateForNeighborUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6);

    public void updateNeighbor(BlockPos var1, Block var2, @Nullable WireOrientation var3);

    public void updateNeighbor(BlockState var1, BlockPos var2, Block var3, @Nullable WireOrientation var4, boolean var5);

    default public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except, @Nullable WireOrientation orientation) {
        for (Direction direction : UPDATE_ORDER) {
            if (direction == except) continue;
            this.updateNeighbor(pos.net_minecraft_util_math_BlockPos_offset(direction), sourceBlock, null);
        }
    }

    public static void replaceWithStateForNeighborUpdate(WorldAccess world, Direction direction, BlockPos pos, BlockPos neighborPos, BlockState neighborState, int flags, int maxUpdateDepth) {
        BlockState blockState = world.getBlockState(pos);
        if ((flags & Block.SKIP_REDSTONE_WIRE_STATE_REPLACEMENT) != 0 && blockState.isOf(Blocks.REDSTONE_WIRE)) {
            return;
        }
        BlockState blockState2 = blockState.getStateForNeighborUpdate(world, world, pos, direction, neighborPos, neighborState, world.getRandom());
        Block.replace(blockState, blockState2, world, pos, flags, maxUpdateDepth);
    }

    public static void tryNeighborUpdate(World world, BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean notify) {
        try {
            state.neighborUpdate(world, pos, sourceBlock, orientation, notify);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while updating neighbours");
            CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
            crashReportSection.add("Source block type", () -> {
                try {
                    return String.format(Locale.ROOT, "ID #%s (%s // %s)", Registries.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
                }
                catch (Throwable throwable) {
                    return "ID #" + String.valueOf(Registries.BLOCK.getId(sourceBlock));
                }
            });
            CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
            throw new CrashException(crashReport);
        }
    }
}

