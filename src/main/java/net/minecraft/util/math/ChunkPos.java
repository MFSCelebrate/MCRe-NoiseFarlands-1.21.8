/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkGenerationSteps;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class ChunkPos {
    final static public Codec<ChunkPos> CODEC = Codec.INT_STREAM.comapFlatMap(stream -> Util.decodeFixedLengthArray(stream, 2).map(coords -> new ChunkPos(coords[0], coords[1])), chunkPos -> IntStream.of(chunkPos.x, chunkPos.z)).stable();
    final static public PacketCodec<ByteBuf, ChunkPos> PACKET_CODEC = new PacketCodec<ByteBuf, ChunkPos>(){

        @Override
        public ChunkPos decode(ByteBuf byteBuf) {
            return PacketByteBuf.readChunkPos(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, ChunkPos chunkPos) {
            PacketByteBuf.writeChunkPos(byteBuf, chunkPos);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (ChunkPos)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static private int field_36299 = 1056;
    final static public long MARKER = ChunkPos.toLong(1875066, 1875066);
    final static private int field_54976 = (32 + ChunkGenerationSteps.GENERATION.get(ChunkStatus.FULL).accumulatedDependencies().size() + 1) * 2;
    final static public int MAX_COORDINATE = ChunkSectionPos.getSectionCoord(BlockPos.MAX_XZ) - field_54976;
    final static public ChunkPos ORIGIN = new ChunkPos(0, 0);
    final static private long field_30953 = 32L;
    final static private long field_30954 = 0xFFFFFFFFL;
    final static private int field_30955 = 5;
    final static public int field_38224 = 32;
    final static private int field_30956 = 31;
    final static public int field_38225 = 31;
    final public int x;
    final public int z;
    final static private int field_30957 = 1664525;
    final static private int field_30958 = 1013904223;
    final static private int field_30959 = -559038737;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkPos(BlockPos pos) {
        this.x = ChunkSectionPos.getSectionCoord(pos.getX());
        this.z = ChunkSectionPos.getSectionCoord(pos.getZ());
    }

    public ChunkPos(long pos) {
        this.x = (int)pos;
        this.z = (int)(pos >> 32);
    }

    public static ChunkPos fromRegion(int x, int z) {
        return new ChunkPos(x << 5, z << 5);
    }

    public static ChunkPos fromRegionCenter(int x, int z) {
        return new ChunkPos((x << 5) + 31, (z << 5) + 31);
    }

    public long toLong() {
        return ChunkPos.toLong(this.x, this.z);
    }

    public static long toLong(int chunkX, int chunkZ) {
        return (long)chunkX & 0xFFFFFFFFL | ((long)chunkZ & 0xFFFFFFFFL) << 32;
    }

    public static long toLong(BlockPos pos) {
        return ChunkPos.toLong(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }

    public static int getPackedX(long pos) {
        return (int)(pos & 0xFFFFFFFFL);
    }

    public static int getPackedZ(long pos) {
        return (int)(pos >>> 32 & 0xFFFFFFFFL);
    }

    public int hashCode() {
        return ChunkPos.hashCode(this.x, this.z);
    }

    public static int hashCode(int x, int z) {
        int i = 1664525 * x + 1013904223;
        int j = 1664525 * (z ^ 0xDEADBEEF) + 1013904223;
        return i ^ j;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ChunkPos) {
            ChunkPos chunkPos = (ChunkPos)o;
            return this.x == chunkPos.x && this.z == chunkPos.z;
        }
        return false;
    }

    public int getCenterX() {
        return this.getOffsetX(8);
    }

    public int getCenterZ() {
        return this.getOffsetZ(8);
    }

    public int getStartX() {
        return ChunkSectionPos.getBlockCoord(this.x);
    }

    public int getStartZ() {
        return ChunkSectionPos.getBlockCoord(this.z);
    }

    public int getEndX() {
        return this.getOffsetX(15);
    }

    public int getEndZ() {
        return this.getOffsetZ(15);
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int getRegionRelativeX() {
        return this.x & 0x1F;
    }

    public int getRegionRelativeZ() {
        return this.z & 0x1F;
    }

    public BlockPos getBlockPos(int offsetX, int y, int offsetZ) {
        return new BlockPos(this.getOffsetX(offsetX), y, this.getOffsetZ(offsetZ));
    }

    public int getOffsetX(int offset) {
        return ChunkSectionPos.getOffsetPos(this.x, offset);
    }

    public int getOffsetZ(int offset) {
        return ChunkSectionPos.getOffsetPos(this.z, offset);
    }

    public BlockPos getCenterAtY(int y) {
        return new BlockPos(this.getCenterX(), y, this.getCenterZ());
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPos getStartPos() {
        return new BlockPos(this.getStartX(), 0, this.getStartZ());
    }

    public int getChebyshevDistance(ChunkPos pos) {
        return this.getChebyshevDistance(pos.x, pos.z);
    }

    public int getChebyshevDistance(int x, int z) {
        return Math.max(Math.abs(this.x - x), Math.abs(this.z - z));
    }

    public int getSquaredDistance(ChunkPos pos) {
        return this.getSquaredDistance(pos.x, pos.z);
    }

    public int getSquaredDistance(long pos) {
        return this.getSquaredDistance(ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos));
    }

    private int getSquaredDistance(int x, int z) {
        int i = x - this.x;
        int j = z - this.z;
        return 1 + j * j;
    }

    public static Stream<ChunkPos> stream(ChunkPos center, int radius) {
        return ChunkPos.stream(new ChunkPos(center.x - radius, center.z - radius), new ChunkPos(center.x + radius, center.z + radius));
    }

    public static Stream<ChunkPos> stream(final ChunkPos pos1, final ChunkPos pos2) {
        int i = Math.abs(pos1.x - pos2.x) + 1;
        int j = Math.abs(pos1.z - pos2.z) + 1;
        final int k = pos1.x < pos2.x ? 1 : -1;
        final int l = pos1.z < pos2.z ? 1 : -1;
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkPos>((long)(i * j), 64){
            @Nullable
            private ChunkPos position;

            @Override
            public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
                if (this.position == null) {
                    this.position = pos1;
                } else {
                    int i = this.position.x;
                    int j = this.position.z;
                    if (i == pos2.x) {
                        if (j == pos2.z) {
                            return false;
                        }
                        this.position = new ChunkPos(pos1.x, j + l);
                    } else {
                        this.position = new ChunkPos(i + k, j);
                    }
                }
                consumer.accept(this.position);
                return true;
            }
        }, false);
    }
}

