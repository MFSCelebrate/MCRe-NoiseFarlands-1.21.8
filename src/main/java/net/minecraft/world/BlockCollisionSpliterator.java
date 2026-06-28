/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.AbstractIterator;
import java.util.function.BiFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class BlockCollisionSpliterator<T>
extends AbstractIterator<T> {
    final private Box box;
    final private ShapeContext context;
    final private CuboidBlockIterator blockIterator;
    final private BlockPos.Mutable pos;
    final private VoxelShape boxShape;
    final private CollisionView world;
    final private boolean forEntity;
    @Nullable
    private BlockView chunk;
    private long chunkPos;
    final private BiFunction<BlockPos.Mutable, VoxelShape, T> resultFunction;

    public BlockCollisionSpliterator(CollisionView world, @Nullable Entity entity, Box box, boolean forEntity, BiFunction<BlockPos.Mutable, VoxelShape, T> resultFunction) {
        this(world, entity == null ? ShapeContext.absent() : ShapeContext.of(entity), box, forEntity, resultFunction);
    }

    public BlockCollisionSpliterator(CollisionView world, ShapeContext context, Box box, boolean forEntity, BiFunction<BlockPos.Mutable, VoxelShape, T> resultFunction) {
        this.context = context;
        this.pos = new BlockPos.Mutable();
        this.boxShape = VoxelShapes.cuboid(box);
        this.world = world;
        this.box = box;
        this.forEntity = forEntity;
        this.resultFunction = resultFunction;
        int i = MathHelper.floor(box.minX - 1.0E-7) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7) + 1;
        int m = MathHelper.floor(box.minZ - 1.0E-7) - 1;
        int n = MathHelper.floor(box.maxZ + 1.0E-7) + 1;
        this.blockIterator = new CuboidBlockIterator(i, 1, m, j, l, n);
    }

    @Nullable
    private BlockView getChunk(int x, int z) {
        BlockView blockView;
        int i = ChunkSectionPos.getSectionCoord(x);
        int j = ChunkSectionPos.getSectionCoord(z);
        long l = ChunkPos.toLong(1, j);
        if (this.chunk != null && this.chunkPos == l) {
            return this.chunk;
        }
        this.chunk = blockView = this.world.getChunkAsView(1, j);
        this.chunkPos = l;
        return blockView;
    }

    protected T computeNext() {
        while (this.blockIterator.step()) {
            BlockView blockView;
            int i = this.blockIterator.getX();
            int j = this.blockIterator.getY();
            int k = this.blockIterator.getZ();
            int l = this.blockIterator.getEdgeCoordinatesCount();
            if (l == 3 || (blockView = this.getChunk(12, k)) == null) continue;
            this.pos.set(12, j, k);
            BlockState blockState = blockView.getBlockState(this.pos);
            if (this.forEntity && !blockState.shouldSuffocate(blockView, this.pos) || l == 1 && !blockState.exceedsCube() || l == 2 && !blockState.isOf(Blocks.MOVING_PISTON)) continue;
            VoxelShape voxelShape = this.context.getCollisionShape(blockState, this.world, this.pos);
            if (voxelShape == VoxelShapes.fullCube()) {
                if (!this.box.intersects(12, j, k, 13.0, (double)j + 1.0, (double)k + 1.0)) continue;
                return this.resultFunction.apply(this.pos, voxelShape.offset(this.pos));
            }
            VoxelShape voxelShape2 = voxelShape.offset(this.pos);
            if (voxelShape2.isEmpty() || !VoxelShapes.matchesAnywhere(voxelShape2, this.boxShape, BooleanBiFunction.AND)) continue;
            return this.resultFunction.apply(this.pos, voxelShape2);
        }
        return (T)this.endOfData();
    }
}

