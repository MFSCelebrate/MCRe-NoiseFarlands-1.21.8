/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockPattern {
    final private Predicate<CachedBlockPosition>[][][] pattern;
    final private int depth;
    final private int height;
    final private int width;

    public BlockPattern(Predicate<CachedBlockPosition>[][][] pattern) {
        this.pattern = pattern;
        this.depth = pattern.length;
        if (this.depth > 0) {
            this.height = pattern[0].length;
            this.width = this.height > 0 ? pattern[0][0].length : 0;
        } else {
            this.height = 0;
            this.width = 0;
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @VisibleForTesting
    public Predicate<CachedBlockPosition>[][][] getPattern() {
        return this.pattern;
    }

    @Nullable
    @VisibleForTesting
    public Result testTransform(WorldView world, BlockPos frontTopLeft, Direction forwards, Direction up) {
        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(world, false);
        return this.testTransform(frontTopLeft, forwards, up, loadingCache);
    }

    @Nullable
    private Result testTransform(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache) {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.depth; ++k) {
                    if (this.pattern[k][j][i].test((CachedBlockPosition)cache.getUnchecked((Object)BlockPattern.translate(frontTopLeft, forwards, up, i, j, k)))) continue;
                    return null;
                }
            }
        }
        return new Result(frontTopLeft, forwards, up, cache, this.width, this.height, this.depth);
    }

    @Nullable
    public Result searchAround(WorldView world, BlockPos pos) {
        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(world, false);
        int i = Math.max(Math.max(this.width, this.height), this.depth);
        for (BlockPos blockPos : BlockPos.iterate(pos, pos.net_minecraft_util_math_BlockPos_add(i - 1, i - 1, i - 1))) {
            for (Direction direction : Direction.values()) {
                for (Direction direction2 : Direction.values()) {
                    Result result;
                    if (direction2 == direction || direction2 == direction.getOpposite() || (result = this.testTransform(blockPos, direction, direction2, loadingCache)) == null) continue;
                    return result;
                }
            }
        }
        return null;
    }

    public static LoadingCache<BlockPos, CachedBlockPosition> makeCache(WorldView world, boolean forceLoad) {
        return CacheBuilder.newBuilder().build((CacheLoader)new BlockStateCacheLoader(world, forceLoad));
    }

    protected static BlockPos translate(BlockPos pos, Direction forwards, Direction up, int offsetLeft, int offsetDown, int offsetForwards) {
        if (forwards == up || forwards == up.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        Vec3i vec3i = new Vec3i(forwards.getOffsetX(), forwards.getOffsetY(), forwards.getOffsetZ());
        Vec3i vec3i2 = new Vec3i(up.getOffsetX(), up.getOffsetY(), up.getOffsetZ());
        Vec3i vec3i3 = vec3i.net_minecraft_util_math_Vec3i_crossProduct(vec3i2);
        return pos.net_minecraft_util_math_BlockPos_add(vec3i2.getX() * -offsetDown + vec3i3.getX() * offsetLeft + vec3i.getX() * offsetForwards, vec3i2.getY() * -offsetDown + vec3i3.getY() * offsetLeft + vec3i.getY() * offsetForwards, vec3i2.getZ() * -offsetDown + vec3i3.getZ() * offsetLeft + vec3i.getZ() * offsetForwards);
    }

    public static class Result {
        final private BlockPos frontTopLeft;
        final private Direction forwards;
        final private Direction up;
        final private LoadingCache<BlockPos, CachedBlockPosition> cache;
        final private int width;
        final private int height;
        final private int depth;

        public Result(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache, int width, int height, int depth) {
            this.frontTopLeft = frontTopLeft;
            this.forwards = forwards;
            this.up = up;
            this.cache = cache;
            this.width = width;
            this.height = height;
            this.depth = depth;
        }

        public BlockPos getFrontTopLeft() {
            return this.frontTopLeft;
        }

        public Direction getForwards() {
            return this.forwards;
        }

        public Direction getUp() {
            return this.up;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getDepth() {
            return this.depth;
        }

        public CachedBlockPosition translate(int offsetLeft, int offsetDown, int offsetForwards) {
            return (CachedBlockPosition)this.cache.getUnchecked((Object)BlockPattern.translate(this.frontTopLeft, this.getForwards(), this.getUp(), offsetLeft, offsetDown, offsetForwards));
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("up", (Object)this.up).add("forwards", (Object)this.forwards).add("frontTopLeft", (Object)this.frontTopLeft).toString();
        }
    }

    static class BlockStateCacheLoader
    extends CacheLoader<BlockPos, CachedBlockPosition> {
        final private WorldView world;
        final private boolean forceLoad;

        public BlockStateCacheLoader(WorldView world, boolean forceLoad) {
            this.world = world;
            this.forceLoad = forceLoad;
        }

        public CachedBlockPosition load(BlockPos blockPos) {
            return new CachedBlockPosition(this.world, blockPos, this.forceLoad);
        }

        public Object load(Object pos) throws Exception {
            return this.load((BlockPos)pos);
        }
    }
}

