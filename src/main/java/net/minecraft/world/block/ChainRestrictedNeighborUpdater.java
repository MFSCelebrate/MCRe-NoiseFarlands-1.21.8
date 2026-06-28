/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.block;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ChainRestrictedNeighborUpdater
implements NeighborUpdater {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private World world;
    final private int maxChainDepth;
    final private ArrayDeque<Entry> queue = new ArrayDeque();
    final private List<Entry> pending = new ArrayList<Entry>();
    private int depth = 0;

    public ChainRestrictedNeighborUpdater(World world, int maxChainDepth) {
        this.world = world;
        this.maxChainDepth = maxChainDepth;
    }

    @Override
    public void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        this.enqueue(pos, new StateReplacementEntry(direction, neighborState, pos.toImmutable(), neighborPos.toImmutable(), flags, maxUpdateDepth));
    }

    @Override
    public void updateNeighbor(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation) {
        this.enqueue(pos, new SimpleEntry(pos, sourceBlock, orientation));
    }

    @Override
    public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean notify) {
        this.enqueue(pos, new StatefulEntry(state, pos.toImmutable(), sourceBlock, orientation, notify));
    }

    @Override
    public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except, @Nullable WireOrientation orientation) {
        this.enqueue(pos, new SixWayEntry(pos.toImmutable(), sourceBlock, orientation, except));
    }

    private void enqueue(BlockPos pos, Entry entry) {
        boolean bl = this.depth > 0;
        boolean bl2 = this.maxChainDepth >= 0 && this.depth >= this.maxChainDepth;
        ++this.depth;
        if (!bl2) {
            if (bl) {
                this.pending.add(entry);
            } else {
                this.queue.push(entry);
            }
        } else if (this.depth - 1 == this.maxChainDepth) {
            LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + pos.toShortString());
        }
        if (!bl) {
            this.runQueuedUpdates();
        }
    }

    private void runQueuedUpdates() {
        try {
            block2: while (!this.queue.isEmpty() || !this.pending.isEmpty()) {
                for (int i = this.pending.size() - 1; i >= 0; --i) {
                    this.queue.push(this.pending.get(i));
                }
                this.pending.clear();
                Entry entry = this.queue.peek();
                while (this.pending.isEmpty()) {
                    if (entry.update(this.world)) continue;
                    this.queue.pop();
                    continue block2;
                }
            }
            this.queue.clear();
            this.pending.clear();
            this.depth = 0;
        }
        catch (Throwable throwable) {
            this.queue.clear();
            this.pending.clear();
            this.depth = 0;
            throw throwable;
        }
    }

    record StateReplacementEntry(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int updateFlags, int updateLimit) implements Entry
    {
        @Override
        public boolean update(World world) {
            NeighborUpdater.replaceWithStateForNeighborUpdate(world, this.direction, this.pos, this.neighborPos, this.neighborState, this.updateFlags, this.updateLimit);
            return false;
        }
    }

    static interface Entry {
        public boolean update(World var1);
    }

    record SimpleEntry(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation) implements Entry
    {
        @Override
        public boolean update(World world) {
            BlockState blockState = world.getBlockState(this.pos);
            NeighborUpdater.tryNeighborUpdate(world, blockState, this.pos, this.sourceBlock, this.orientation, false);
            return false;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{SimpleEntry.class, "pos;block;orientation", "pos", "sourceBlock", "orientation"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SimpleEntry.class, "pos;block;orientation", "pos", "sourceBlock", "orientation"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SimpleEntry.class, "pos;block;orientation", "pos", "sourceBlock", "orientation"}, this, object);
        }
    }

    record StatefulEntry(BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean movedByPiston) implements Entry
    {
        @Override
        public boolean update(World world) {
            NeighborUpdater.tryNeighborUpdate(world, this.state, this.pos, this.sourceBlock, this.orientation, this.movedByPiston);
            return false;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{StatefulEntry.class, "state;pos;block;orientation;movedByPiston", "state", "pos", "sourceBlock", "orientation", "movedByPiston"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StatefulEntry.class, "state;pos;block;orientation;movedByPiston", "state", "pos", "sourceBlock", "orientation", "movedByPiston"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StatefulEntry.class, "state;pos;block;orientation;movedByPiston", "state", "pos", "sourceBlock", "orientation", "movedByPiston"}, this, object);
        }
    }

    static final class SixWayEntry
    implements Entry {
        final private BlockPos pos;
        final private Block sourceBlock;
        @Nullable
        private WireOrientation orientation;
        @Nullable
        final private Direction except;
        private int currentDirectionIndex = 0;

        SixWayEntry(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, @Nullable Direction except) {
            this.pos = pos;
            this.sourceBlock = sourceBlock;
            this.orientation = orientation;
            this.except = except;
            if (NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == except) {
                ++this.currentDirectionIndex;
            }
        }

        @Override
        public boolean update(World world) {
            Direction direction = NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex++];
            BlockPos blockPos = this.pos.net_minecraft_util_math_BlockPos_offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            WireOrientation wireOrientation = null;
            if (world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
                if (this.orientation == null) {
                    this.orientation = OrientationHelper.getEmissionOrientation(world, this.except == null ? null : this.except.getOpposite(), null);
                }
                wireOrientation = this.orientation.withFront(direction);
            }
            NeighborUpdater.tryNeighborUpdate(world, blockState, blockPos, this.sourceBlock, wireOrientation, false);
            if (this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == this.except) {
                ++this.currentDirectionIndex;
            }
            return this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length;
        }
    }
}

