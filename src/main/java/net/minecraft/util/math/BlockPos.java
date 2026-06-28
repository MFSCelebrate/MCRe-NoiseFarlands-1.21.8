/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Unmodifiable
 *  org.slf4j.Logger
 */
package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

@Unmodifiable
public class BlockPos
extends Vec3i {
    final static public Codec<BlockPos> CODEC = Codec.INT_STREAM.comapFlatMap(stream -> Util.decodeFixedLengthArray(stream, 3).map(values -> new BlockPos(values[0], values[1], values[2])), pos -> IntStream.of(pos.getX(), pos.getY(), pos.getZ())).stable();
    final static public PacketCodec<ByteBuf, BlockPos> PACKET_CODEC = new PacketCodec<ByteBuf, BlockPos>(){

        @Override
        public BlockPos decode(ByteBuf byteBuf) {
            return PacketByteBuf.readBlockPos(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, BlockPos blockPos) {
            PacketByteBuf.writeBlockPos(byteBuf, blockPos);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (BlockPos)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public BlockPos ORIGIN = new BlockPos(0, 0, 0);
    final static public int SIZE_BITS_XZ = 1 + MathHelper.floorLog2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
    final static public int SIZE_BITS_Y = 64 - 2 * SIZE_BITS_XZ;
    final static private long BITS_X = (1L << SIZE_BITS_XZ) - 1L;
    final static private long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
    final static private long BITS_Z = (1L << SIZE_BITS_XZ) - 1L;
    final static private int field_33083 = 0;
    final static private int BIT_SHIFT_Z = SIZE_BITS_Y;
    final static private int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_XZ;
    final static public int MAX_XZ = (1 << SIZE_BITS_XZ) / 2 - 1;

    public BlockPos(int i, int j, int k) {
        super(i, j, k);
    }

    public BlockPos(Vec3i pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public static long offset(long value, Direction direction) {
        return BlockPos.add(value, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    public static long add(long value, int x, int y, int z) {
        return BlockPos.asLong(BlockPos.unpackLongX(value) + x, BlockPos.unpackLongY(value) + y, BlockPos.unpackLongZ(value) + z);
    }

    public static int unpackLongX(long packedPos) {
        return (int)(packedPos << 64 - BIT_SHIFT_X - SIZE_BITS_XZ >> 64 - SIZE_BITS_XZ);
    }

    public static int unpackLongY(long packedPos) {
        return (int)(packedPos << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
    }

    public static int unpackLongZ(long packedPos) {
        return (int)(packedPos << 64 - BIT_SHIFT_Z - SIZE_BITS_XZ >> 64 - SIZE_BITS_XZ);
    }

    public static BlockPos fromLong(long packedPos) {
        return new BlockPos(BlockPos.unpackLongX(packedPos), BlockPos.unpackLongY(packedPos), BlockPos.unpackLongZ(packedPos));
    }

    public static BlockPos ofFloored(double x, double y, double z) {
        return new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    public static BlockPos ofFloored(Position pos) {
        return BlockPos.ofFloored(pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockPos min(BlockPos a, BlockPos b) {
        return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
    }

    public static BlockPos max(BlockPos a, BlockPos b) {
        return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
    }

    public long asLong() {
        return BlockPos.asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int x, int y, int z) {
        long l = 0L;
        l |= ((long)x & BITS_X) << BIT_SHIFT_X;
        l |= ((long)y & BITS_Y) << 0;
        return l |= ((long)z & BITS_Z) << BIT_SHIFT_Z;
    }

    public static long removeChunkSectionLocalY(long y) {
        return y & 0xFFFFFFFFFFFFFFF0L;
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_add(int i, int j, int k) {
        if (i == 0 && j == 0 && k == 0) {
            return this;
        }
        return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
    }

    public Vec3d toCenterPos() {
        return Vec3d.ofCenter(this);
    }

    public Vec3d toBottomCenterPos() {
        return Vec3d.ofBottomCenter(this);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_add(Vec3i vec3i) {
        return this.net_minecraft_util_math_BlockPos_add(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_subtract(Vec3i vec3i) {
        return this.net_minecraft_util_math_BlockPos_add(-vec3i.getX(), -vec3i.getY(), -vec3i.getZ());
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_multiply(int i) {
        if (i == 1) {
            return this;
        }
        if (i == 0) {
            return ORIGIN;
        }
        return new BlockPos(this.getX() * i, this.getY() * i, this.getZ() * i);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_up() {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.UP);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_up(int distance) {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.UP, distance);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_down() {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.DOWN);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_down(int i) {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.DOWN, i);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_north() {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.NORTH);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_north(int distance) {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.NORTH, distance);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_south() {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.SOUTH);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_south(int distance) {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.SOUTH, distance);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_west() {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.WEST);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_west(int distance) {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.WEST, distance);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_east() {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.EAST);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_east(int distance) {
        return this.net_minecraft_util_math_BlockPos_offset(Direction.EAST, distance);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_offset(Direction direction) {
        return new BlockPos(this.getX() + direction.getOffsetX(), this.getY() + direction.getOffsetY(), this.getZ() + direction.getOffsetZ());
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_offset(Direction direction, int i) {
        if (i == 0) {
            return this;
        }
        return new BlockPos(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_offset(Direction.Axis axis, int i) {
        if (i == 0) {
            return this;
        }
        int j = axis == Direction.Axis.X ? i : 0;
        int k = axis == Direction.Axis.Y ? i : 0;
        int l = axis == Direction.Axis.Z ? i : 0;
        return new BlockPos(this.getX() + j, this.getY() + k, this.getZ() + l);
    }

    public BlockPos rotate(BlockRotation rotation) {
        switch (rotation) {
            default: {
                return this;
            }
            case CLOCKWISE_90: {
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            }
            case CLOCKWISE_180: {
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            }
            case COUNTERCLOCKWISE_90: 
        }
        return new BlockPos(this.getZ(), this.getY(), -this.getX());
    }

    @Override
    public BlockPos net_minecraft_util_math_BlockPos_crossProduct(Vec3i pos) {
        return new BlockPos(this.getY() * pos.getZ() - this.getZ() * pos.getY(), this.getZ() * pos.getX() - this.getX() * pos.getZ(), this.getX() * pos.getY() - this.getY() * pos.getX());
    }

    public BlockPos withY(int y) {
        return new BlockPos(this.getX(), y, this.getZ());
    }

    public BlockPos toImmutable() {
        return this;
    }

    public Mutable mutableCopy() {
        return new Mutable(this.getX(), this.getY(), this.getZ());
    }

    public Vec3d clampToWithin(Vec3d pos) {
        return new Vec3d(MathHelper.clamp(pos.x, (double)((float)this.getX() + 1.0E-5f), (double)this.getX() + 1.0 - (double)1.0E-5f), MathHelper.clamp(pos.y, (double)((float)this.getY() + 1.0E-5f), (double)this.getY() + 1.0 - (double)1.0E-5f), MathHelper.clamp(pos.z, (double)((float)this.getZ() + 1.0E-5f), (double)this.getZ() + 1.0 - (double)1.0E-5f));
    }

    public static Iterable<BlockPos> iterateRandomly(Random random, int count, BlockPos around, int range) {
        return BlockPos.iterateRandomly(random, count, around.getX() - range, around.getY() - range, around.getZ() - range, around.getX() + range, around.getY() + range, around.getZ() + range);
    }

    @Deprecated
    public static Stream<BlockPos> streamSouthEastSquare(BlockPos pos) {
        return Stream.of(pos, pos.net_minecraft_util_math_BlockPos_south(), pos.net_minecraft_util_math_BlockPos_east(), pos.net_minecraft_util_math_BlockPos_south().net_minecraft_util_math_BlockPos_east());
    }

    public static Iterable<BlockPos> iterateRandomly(final Random random, final int count, final int minX, final int minY, final int minZ, int maxX, int maxY, int maxZ) {
        final int i = maxX - minX + 1;
        final int j = maxY - minY + 1;
        final int k = maxZ - minZ + 1;
        return () -> new AbstractIterator<BlockPos>(){
            final Mutable pos = new Mutable();
            int remaining = count;

            protected BlockPos net_minecraft_util_math_BlockPos_computeNext() {
                if (this.remaining <= 0) {
                    return (BlockPos)this.endOfData();
                }
                Mutable blockPos = this.pos.set(minX + random.nextInt(i), minY + random.nextInt(j), minZ + random.nextInt(k));
                --this.remaining;
                return blockPos;
            }

            protected Object java_lang_Object_computeNext() {
                return this.net_minecraft_util_math_BlockPos_computeNext();
            }
        };
    }

    public static Iterable<BlockPos> iterateOutwards(BlockPos center, int rangeX, int rangeY, int rangeZ) {
        int i = rangeX + rangeY + rangeZ;
        int j = center.getX();
        int k = center.getY();
        final int l = center.getZ();
        return () -> new AbstractIterator<BlockPos>(1, rangeX, rangeY, rangeZ, j, k){
            final private Mutable pos = new Mutable();
            private int manhattanDistance;
            private int limitX;
            private int limitY;
            private int dx;
            private int dy;
            private boolean swapZ;
            final int field_48416;
            final int field_48417;
            final int field_48418;
            final int field_48419;
            final int field_48420;
            final int field_48421;
            {
                this.field_48416 = j;
                this.field_48417 = k;
                this.field_48418 = l2;
                this.field_48419 = m;
                this.field_48420 = n;
                this.field_48421 = o;
            }

            protected BlockPos net_minecraft_util_math_BlockPos_computeNext() {
                if (this.swapZ) {
                    this.swapZ = false;
                    this.pos.net_minecraft_util_math_BlockPos$Mutable_setZ(l - (this.pos.getZ() - l));
                    return this.pos;
                }
                Mutable blockPos = null;
                while (blockPos == null) {
                    if (this.dy > this.limitY) {
                        ++this.dx;
                        if (this.dx > this.limitX) {
                            ++this.manhattanDistance;
                            if (this.manhattanDistance > this.field_48416) {
                                return (BlockPos)this.endOfData();
                            }
                            this.limitX = Math.min(this.field_48417, this.manhattanDistance);
                            this.dx = -this.limitX;
                        }
                        this.limitY = Math.min(this.field_48418, this.manhattanDistance - Math.abs(this.dx));
                        this.dy = -this.limitY;
                    }
                    int i = this.dx;
                    int j = this.dy;
                    int k = this.manhattanDistance - Math.abs(i) - Math.abs(j);
                    if (k <= this.field_48419) {
                        this.swapZ = k != 0;
                        blockPos = this.pos.set(this.field_48420 + i, this.field_48421 + j, l + k);
                    }
                    ++this.dy;
                }
                return blockPos;
            }

            protected Object java_lang_Object_computeNext() {
                return this.net_minecraft_util_math_BlockPos_computeNext();
            }
        };
    }

    public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange, Predicate<BlockPos> condition) {
        for (BlockPos blockPos : BlockPos.iterateOutwards(pos, horizontalRange, verticalRange, horizontalRange)) {
            if (!condition.test(blockPos)) continue;
            return Optional.of(blockPos);
        }
        return Optional.empty();
    }

    public static Stream<BlockPos> streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
        return StreamSupport.stream(BlockPos.iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(Box box) {
        BlockPos blockPos = BlockPos.ofFloored(box.minX, box.minY, box.minZ);
        BlockPos blockPos2 = BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ);
        return BlockPos.iterate(blockPos, blockPos2);
    }

    public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
        return BlockPos.iterate(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()), Math.max(start.getX(), end.getX()), Math.max(start.getY(), end.getY()), Math.max(start.getZ(), end.getZ()));
    }

    public static Stream<BlockPos> stream(BlockPos start, BlockPos end) {
        return StreamSupport.stream(BlockPos.iterate(start, end).spliterator(), false);
    }

    public static Stream<BlockPos> stream(BlockBox box) {
        return BlockPos.stream(Math.min(box.getMinX(), box.getMaxX()), Math.min(box.getMinY(), box.getMaxY()), Math.min(box.getMinZ(), box.getMaxZ()), Math.max(box.getMinX(), box.getMaxX()), Math.max(box.getMinY(), box.getMaxY()), Math.max(box.getMinZ(), box.getMaxZ()));
    }

    public static Stream<BlockPos> stream(Box box) {
        return BlockPos.stream(MathHelper.floor(box.minX), MathHelper.floor(box.minY), MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ));
    }

    public static Stream<BlockPos> stream(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        return StreamSupport.stream(BlockPos.iterate(startX, startY, startZ, endX, endY, endZ).spliterator(), false);
    }

    public static Iterable<BlockPos> iterate(final int startX, final int startY, final int startZ, int endX, int endY, int endZ) {
        final int i = endX - startX + 1;
        final int j = endY - startY + 1;
        int k = endZ - startZ + 1;
        final int l = i * j * k;
        return () -> new AbstractIterator<BlockPos>(){
            final private Mutable pos = new Mutable();
            private int index;

            protected BlockPos net_minecraft_util_math_BlockPos_computeNext() {
                if (this.index == l) {
                    return (BlockPos)this.endOfData();
                }
                int i2 = this.index % i;
                int j2 = this.index / i;
                int k = j2 % j;
                int l2 = j2 / j;
                ++this.index;
                return this.pos.set(startX + i2, startY + k, startZ + l2);
            }

            protected Object java_lang_Object_computeNext() {
                return this.net_minecraft_util_math_BlockPos_computeNext();
            }
        };
    }

    public static Iterable<Mutable> iterateInSquare(final BlockPos center, int radius, final Direction firstDirection, final Direction secondDirection) {
        Validate.validState((firstDirection.getAxis() != secondDirection.getAxis() ? 1 : 0) != 0, (String)"The two directions cannot be on the same axis", (Object[])new Object[0]);
        return () -> new AbstractIterator<Mutable>(1){
            final private Direction[] directions;
            final private Mutable pos;
            final private int maxDirectionChanges;
            private int directionChangeCount;
            private int maxSteps;
            private int steps;
            private int currentX;
            private int currentY;
            private int currentZ;
            final int field_48439;
            {
                this.field_48439 = 1;
                this.directions = new Direction[]{firstDirection, secondDirection, firstDirection.getOpposite(), secondDirection.getOpposite()};
                this.pos = center.mutableCopy().move(secondDirection);
                this.maxDirectionChanges = 4 * this.field_48439;
                this.directionChangeCount = -1;
                this.currentX = this.pos.getX();
                this.currentY = this.pos.getY();
                this.currentZ = this.pos.getZ();
            }

            protected Mutable net_minecraft_util_math_BlockPos$Mutable_computeNext() {
                this.pos.set(this.currentX, this.currentY, this.currentZ).move(this.directions[(this.directionChangeCount + 4) % 4]);
                this.currentX = this.pos.getX();
                this.currentY = this.pos.getY();
                this.currentZ = this.pos.getZ();
                if (this.steps >= this.maxSteps) {
                    if (this.directionChangeCount >= this.maxDirectionChanges) {
                        return (Mutable)this.endOfData();
                    }
                    ++this.directionChangeCount;
                    this.steps = 0;
                    this.maxSteps = this.directionChangeCount / 2 + 1;
                }
                ++this.steps;
                return this.pos;
            }

            protected Object java_lang_Object_computeNext() {
                return this.net_minecraft_util_math_BlockPos$Mutable_computeNext();
            }
        };
    }

    public static int iterateRecursively(BlockPos pos, int maxDepth, int maxIterations, BiConsumer<BlockPos, Consumer<BlockPos>> nextQueuer, Function<BlockPos, IterationState> callback) {
        ArrayDeque<Pair> queue = new ArrayDeque<Pair>();
        LongOpenHashSet longSet = new LongOpenHashSet();
        queue.add(Pair.of((Object)pos, (Object)0));
        int i = 0;
        while (!queue.isEmpty()) {
            IterationState iterationState;
            Pair pair = (Pair)queue.poll();
            BlockPos blockPos = (BlockPos)pair.getLeft();
            int j = (Integer)pair.getRight();
            long l = blockPos.asLong();
            if (!longSet.add(l) || (iterationState = callback.apply(blockPos)) == IterationState.SKIP) continue;
            if (iterationState == IterationState.STOP) break;
            if (++i >= maxIterations) {
                return i;
            }
            if (j >= maxDepth) continue;
            nextQueuer.accept(blockPos, queuedPos -> queue.add(Pair.of((Object)queuedPos, (Object)(j + 1))));
        }
        return i;
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_crossProduct(Vec3i vec) {
        return this.net_minecraft_util_math_BlockPos_crossProduct(vec);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_offset(Direction.Axis axis, int distance) {
        return this.net_minecraft_util_math_BlockPos_offset(axis, distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_offset(Direction direction, int distance) {
        return this.net_minecraft_util_math_BlockPos_offset(direction, distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_offset(Direction direction) {
        return this.net_minecraft_util_math_BlockPos_offset(direction);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_east(int distance) {
        return this.net_minecraft_util_math_BlockPos_east(distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_east() {
        return this.net_minecraft_util_math_BlockPos_east();
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_west(int distance) {
        return this.net_minecraft_util_math_BlockPos_west(distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_west() {
        return this.net_minecraft_util_math_BlockPos_west();
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_south(int distance) {
        return this.net_minecraft_util_math_BlockPos_south(distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_south() {
        return this.net_minecraft_util_math_BlockPos_south();
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_north(int distance) {
        return this.net_minecraft_util_math_BlockPos_north(distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_north() {
        return this.net_minecraft_util_math_BlockPos_north();
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_down(int distance) {
        return this.net_minecraft_util_math_BlockPos_down(distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_down() {
        return this.net_minecraft_util_math_BlockPos_down();
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_up(int distance) {
        return this.net_minecraft_util_math_BlockPos_up(distance);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_up() {
        return this.net_minecraft_util_math_BlockPos_up();
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_multiply(int scale) {
        return this.net_minecraft_util_math_BlockPos_multiply(scale);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_subtract(Vec3i vec) {
        return this.net_minecraft_util_math_BlockPos_subtract(vec);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_add(Vec3i vec) {
        return this.net_minecraft_util_math_BlockPos_add(vec);
    }

    @Override
    public Vec3i net_minecraft_util_math_Vec3i_add(int x, int y, int z) {
        return this.net_minecraft_util_math_BlockPos_add(x, y, z);
    }

    public static class Mutable
    extends BlockPos {
        public Mutable() {
            this(0, 0, 0);
        }

        public Mutable(int i, int j, int k) {
            super(i, j, k);
        }

        public Mutable(double x, double y, double z) {
            this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        }

        @Override
        public BlockPos net_minecraft_util_math_BlockPos_add(int i, int j, int k) {
            return super.net_minecraft_util_math_BlockPos_add(i, j, k).toImmutable();
        }

        @Override
        public BlockPos net_minecraft_util_math_BlockPos_multiply(int i) {
            return super.net_minecraft_util_math_BlockPos_multiply(i).toImmutable();
        }

        @Override
        public BlockPos net_minecraft_util_math_BlockPos_offset(Direction direction, int i) {
            return super.net_minecraft_util_math_BlockPos_offset(direction, i).toImmutable();
        }

        @Override
        public BlockPos net_minecraft_util_math_BlockPos_offset(Direction.Axis axis, int i) {
            return super.net_minecraft_util_math_BlockPos_offset(axis, i).toImmutable();
        }

        @Override
        public BlockPos rotate(BlockRotation rotation) {
            return super.rotate(rotation).toImmutable();
        }

        public Mutable set(int x, int y, int z) {
            this.net_minecraft_util_math_BlockPos$Mutable_setX(x);
            this.net_minecraft_util_math_BlockPos$Mutable_setY(y);
            this.net_minecraft_util_math_BlockPos$Mutable_setZ(z);
            return this;
        }

        public Mutable set(double x, double y, double z) {
            return this.set(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
        }

        public Mutable set(Vec3i pos) {
            return this.set(pos.getX(), pos.getY(), pos.getZ());
        }

        public Mutable set(long pos) {
            return this.set(Mutable.unpackLongX(pos), Mutable.unpackLongY(pos), Mutable.unpackLongZ(pos));
        }

        public Mutable set(AxisCycleDirection axis, int x, int y, int z) {
            return this.set(axis.choose(x, y, z, Direction.Axis.X), axis.choose(x, y, z, Direction.Axis.Y), axis.choose(x, y, z, Direction.Axis.Z));
        }

        public Mutable set(Vec3i pos, Direction direction) {
            return this.set(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ());
        }

        public Mutable set(Vec3i pos, int x, int y, int z) {
            return this.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
        }

        public Mutable set(Vec3i vec1, Vec3i vec2) {
            return this.set(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ());
        }

        public Mutable move(Direction direction) {
            return this.move(direction, 1);
        }

        public Mutable move(Direction direction, int distance) {
            return this.set(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance);
        }

        public Mutable move(int dx, int dy, int dz) {
            return this.set(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
        }

        public Mutable move(Vec3i vec) {
            return this.set(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
        }

        public Mutable clamp(Direction.Axis axis, int min, int max) {
            switch (axis) {
                case X: {
                    return this.set(MathHelper.clamp(this.getX(), min, max), this.getY(), this.getZ());
                }
                case Y: {
                    return this.set(this.getX(), MathHelper.clamp(this.getY(), min, max), this.getZ());
                }
                case Z: {
                    return this.set(this.getX(), this.getY(), MathHelper.clamp(this.getZ(), min, max));
                }
            }
            throw new IllegalStateException("Unable to clamp axis " + String.valueOf(axis));
        }

        @Override
        public Mutable net_minecraft_util_math_BlockPos$Mutable_setX(int i) {
            super.net_minecraft_util_math_Vec3i_setX(i);
            return this;
        }

        @Override
        public Mutable net_minecraft_util_math_BlockPos$Mutable_setY(int i) {
            super.net_minecraft_util_math_Vec3i_setY(i);
            return this;
        }

        @Override
        public Mutable net_minecraft_util_math_BlockPos$Mutable_setZ(int i) {
            super.net_minecraft_util_math_Vec3i_setZ(i);
            return this;
        }

        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_crossProduct(Vec3i vec) {
            return super.net_minecraft_util_math_BlockPos_crossProduct(vec);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_offset(Direction.Axis axis, int distance) {
            return this.net_minecraft_util_math_BlockPos_offset(axis, distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_offset(Direction direction, int distance) {
            return this.net_minecraft_util_math_BlockPos_offset(direction, distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_offset(Direction direction) {
            return super.net_minecraft_util_math_BlockPos_offset(direction);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_east(int distance) {
            return super.net_minecraft_util_math_BlockPos_east(distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_east() {
            return super.net_minecraft_util_math_BlockPos_east();
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_west(int distance) {
            return super.net_minecraft_util_math_BlockPos_west(distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_west() {
            return super.net_minecraft_util_math_BlockPos_west();
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_south(int distance) {
            return super.net_minecraft_util_math_BlockPos_south(distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_south() {
            return super.net_minecraft_util_math_BlockPos_south();
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_north(int distance) {
            return super.net_minecraft_util_math_BlockPos_north(distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_north() {
            return super.net_minecraft_util_math_BlockPos_north();
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_down(int distance) {
            return super.net_minecraft_util_math_BlockPos_down(distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_down() {
            return super.net_minecraft_util_math_BlockPos_down();
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_up(int distance) {
            return super.net_minecraft_util_math_BlockPos_up(distance);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_up() {
            return super.net_minecraft_util_math_BlockPos_up();
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_multiply(int scale) {
            return this.net_minecraft_util_math_BlockPos_multiply(scale);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_subtract(Vec3i vec) {
            return super.net_minecraft_util_math_BlockPos_subtract(vec);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_add(Vec3i vec) {
            return super.net_minecraft_util_math_BlockPos_add(vec);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_add(int x, int y, int z) {
            return this.net_minecraft_util_math_BlockPos_add(x, y, z);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_setZ(int z) {
            return this.net_minecraft_util_math_BlockPos$Mutable_setZ(z);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_setY(int y) {
            return this.net_minecraft_util_math_BlockPos$Mutable_setY(y);
        }

        @Override
        public Vec3i net_minecraft_util_math_Vec3i_setX(int x) {
            return this.net_minecraft_util_math_BlockPos$Mutable_setX(x);
        }
    }

    public static final class IterationState
    extends Enum<IterationState> {
        final static public IterationState ACCEPT = new IterationState();
        final static public IterationState SKIP = new IterationState();
        final static public IterationState STOP = new IterationState();
        final static private IterationState[] field_55168;

        public static IterationState[] values() {
            return (IterationState[])field_55168.clone();
        }

        public static IterationState valueOf(String string) {
            return Enum.valueOf(IterationState.class, string);
        }

        private static IterationState[] method_65259() {
            return new IterationState[]{ACCEPT, SKIP, STOP};
        }

        static {
            field_55168 = IterationState.method_65259();
        }
    }
}

