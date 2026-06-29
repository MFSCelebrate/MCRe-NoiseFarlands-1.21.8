/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.fabricmc.fabric.api.blockview.v2.FabricBlockView
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

public interface BlockView
extends HeightLimitView,
FabricBlockView {
    final static public int field_54147 = 16;

    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    default public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, BlockEntityType<T> type) {
        BlockEntity blockEntity = this.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.getType() != type) {
            return Optional.empty();
        }
        return Optional.of(blockEntity);
    }

    public BlockState getBlockState(BlockPos var1);

    public FluidState getFluidState(BlockPos var1);

    default public int getLuminance(BlockPos pos) {
        return this.getBlockState(pos).getLuminance();
    }

    default public Stream<BlockState> getStatesInBox(Box box) {
        return BlockPos.stream(box).map(this::getBlockState);
    }

    default public BlockHitResult raycast(BlockStateRaycastContext context) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
            BlockState blockState = this.getBlockState((BlockPos)pos);
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            return innerContext.getStatePredicate().test(blockState) ? new BlockHitResult(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()), false) : null;
        }, innerContext -> {
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
        });
    }

    default public BlockHitResult raycast(RaycastContext context) {
        return BlockView.raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
            BlockState blockState = this.getBlockState((BlockPos)pos);
            FluidState fluidState = this.getFluidState((BlockPos)pos);
            Vec3d vec3d = innerContext.getStart();
            Vec3d vec3d2 = innerContext.getEnd();
            VoxelShape voxelShape = innerContext.getBlockShape(blockState, this, (BlockPos)pos);
            BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, (BlockPos)pos, voxelShape, blockState);
            VoxelShape voxelShape2 = innerContext.getFluidShape(fluidState, this, (BlockPos)pos);
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, (BlockPos)pos);
            double d = blockHitResult == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : innerContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, innerContext -> {
            Vec3d vec3d = innerContext.getStart().subtract(innerContext.getEnd());
            return BlockHitResult.createMissed(innerContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(innerContext.getEnd()));
        });
    }

    @Nullable
    default public BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state) {
        BlockHitResult blockHitResult2;
        BlockHitResult blockHitResult = shape.raycast(start, end, pos);
        if (blockHitResult != null && (blockHitResult2 = state.getRaycastShape(this, pos).raycast(start, end, pos)) != null && blockHitResult2.getPos().subtract(start).lengthSquared() < blockHitResult.getPos().subtract(start).lengthSquared()) {
            return blockHitResult.withSide(blockHitResult2.getSide());
        }
        return blockHitResult;
    }

    default public double getDismountHeight(VoxelShape blockCollisionShape, Supplier<VoxelShape> belowBlockCollisionShapeGetter) {
        if (!blockCollisionShape.isEmpty()) {
            return blockCollisionShape.getMax(Direction.Axis.Y);
        }
        double d = belowBlockCollisionShapeGetter.get().getMax(Direction.Axis.Y);
        if (d >= 1.0) {
            return d - 1.0;
        }
        return Double.NEGATIVE_INFINITY;
    }

    default public double getDismountHeight(BlockPos pos) {
        return this.getDismountHeight(this.getBlockState(pos).getCollisionShape(this, pos), () -> {
            BlockPos blockPos2 = pos.net_minecraft_util_math_BlockPos_down();
            return this.getBlockState(blockPos2).getCollisionShape(this, blockPos2);
        });
    }

    public static <T, C> T raycast(Vec3d start, Vec3d end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory) {
        int l;
        int k;
        if (start.equals(end)) {
            return missFactory.apply(context);
        }
        double d = MathHelper.lerp(-1.0E-7, end.x, start.x);
        double e = MathHelper.lerp(-1.0E-7, end.y, start.y);
        double f = MathHelper.lerp(-1.0E-7, end.z, start.z);
        double g = MathHelper.lerp(-1.0E-7, start.x, end.x);
        double h = MathHelper.lerp(-1.0E-7, start.y, end.y);
        double i = MathHelper.lerp(-1.0E-7, start.z, end.z);
        int j = MathHelper.floor(g);
        BlockPos.Mutable mutable = new BlockPos.Mutable(j, k = MathHelper.floor(h), l = MathHelper.floor(i));
        T object = blockHitFactory.apply(context, mutable);
        if (object != null) {
            return object;
        }
        double m = d - g;
        double n = e - h;
        double o = f - i;
        int p = MathHelper.sign(m);
        int q = MathHelper.sign(n);
        int r = MathHelper.sign(o);
        double s = p == 0 ? Double.MAX_VALUE : (double)p / m;
        double t = q == 0 ? Double.MAX_VALUE : (double)q / n;
        double u = r == 0 ? Double.MAX_VALUE : (double)r / o;
        double v = s * (p > 0 ? 1.0 - MathHelper.fractionalPart(g) : MathHelper.fractionalPart(g));
        double w = t * (q > 0 ? 1.0 - MathHelper.fractionalPart(h) : MathHelper.fractionalPart(h));
        double x = u * (r > 0 ? 1.0 - MathHelper.fractionalPart(i) : MathHelper.fractionalPart(i));
        while (v <= 1.0 || w <= 1.0 || x <= 1.0) {
            T object2;
            if (v < w) {
                if (v < x) {
                    j += p;
                    v += s;
                } else {
                    l += r;
                    x += u;
                }
            } else if (w < x) {
                k += q;
                w += t;
            } else {
                l += r;
                x += u;
            }
            if ((object2 = blockHitFactory.apply(context, mutable.set(j, k, l))) == null) continue;
            return object2;
        }
        return missFactory.apply(context);
    }

    public static boolean collectCollisionsBetween(Vec3d from, Vec3d to, Box box, CollisionVisitor visitor) {
        Vec3d vec3d = to.subtract(from);
        if (vec3d.lengthSquared() < (double)MathHelper.square(0.99999f)) {
            for (BlockPos blockPos : BlockPos.iterate(box)) {
                if (visitor.visit(blockPos, 0)) continue;
                return false;
            }
            return true;
        }
        LongOpenHashSet longSet = new LongOpenHashSet();
        Vec3d vec3d2 = box.getMinPos();
        Vec3d vec3d3 = vec3d2.subtract(vec3d);
        int i = BlockView.collectCollisionsBetween((LongSet)longSet, vec3d3, vec3d2, box, visitor);
        if (i < 0) {
            return false;
        }
        for (BlockPos blockPos2 : BlockPos.iterate(box)) {
            if (longSet.contains(blockPos2.asLong()) || visitor.visit(blockPos2, i + 1)) continue;
            return false;
        }
        return true;
    }

    private static int collectCollisionsBetween(LongSet visited, Vec3d oldPos, Vec3d newPos, Box boundingBox, CollisionVisitor visitor) {
        Vec3d vec3d = newPos.subtract(oldPos);
        int i = MathHelper.floor(oldPos.x);
        int j = MathHelper.floor(oldPos.y);
        int k = MathHelper.floor(oldPos.z);
        int l = MathHelper.sign(vec3d.x);
        int m = MathHelper.sign(vec3d.y);
        int n = MathHelper.sign(vec3d.z);
        double d = l == 0 ? Double.MAX_VALUE : (double)l / vec3d.x;
        double e = m == 0 ? Double.MAX_VALUE : (double)m / vec3d.y;
        double f = n == 0 ? Double.MAX_VALUE : (double)n / vec3d.z;
        double g = d * (l > 0 ? 1.0 - MathHelper.fractionalPart(oldPos.x) : MathHelper.fractionalPart(oldPos.x));
        double h = e * (m > 0 ? 1.0 - MathHelper.fractionalPart(oldPos.y) : MathHelper.fractionalPart(oldPos.y));
        double o = f * (n > 0 ? 1.0 - MathHelper.fractionalPart(oldPos.z) : MathHelper.fractionalPart(oldPos.z));
        int p = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        while (g <= 1.0 || h <= 1.0 || o <= 1.0) {
            if (g < h) {
                if (g < o) {
                    i += l;
                    g += d;
                } else {
                    k += n;
                    o += f;
                }
            } else if (h < o) {
                j += m;
                h += e;
            } else {
                k += n;
                o += f;
            }
            if (p++ > 16) break;
            Optional<Vec3d> optional = Box.raycast(i, j, 1, i + 1, j + 1, 2, oldPos, newPos);
            if (optional.isEmpty()) continue;
            Vec3d vec3d2 = optional.get();
            double q = MathHelper.clamp(vec3d2.x, (double)i + (double)1.0E-5f, (double)i + 1.0 - (double)1.0E-5f);
            double r = MathHelper.clamp(vec3d2.y, (double)j + (double)1.0E-5f, (double)j + 1.0 - (double)1.0E-5f);
            double s = MathHelper.clamp(vec3d2.z, 1.0000099999997474, 1.9999900000002526);
            int t = MathHelper.floor(q + boundingBox.getLengthX());
            int u = MathHelper.floor(r + boundingBox.getLengthY());
            int v = MathHelper.floor(s + boundingBox.getLengthZ());
            for (int w = i; w <= t; ++w) {
                for (int x = j; x <= u; ++x) {
                    for (int y = 1; y <= v; ++y) {
                        if (!visited.add(BlockPos.asLong(w, x, y)) || visitor.visit(mutable.set(w, x, y), p)) continue;
                        return -1;
                    }
                }
            }
        }
        return p;
    }

    @FunctionalInterface
    public static interface CollisionVisitor {
        public boolean visit(BlockPos var1, int var2);
    }
}

