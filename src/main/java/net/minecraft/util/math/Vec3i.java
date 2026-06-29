/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Unmodifiable
 */
package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.stream.IntStream;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class Vec3i
implements Comparable<Vec3i> {
    final static public Codec<Vec3i> CODEC = Codec.INT_STREAM.comapFlatMap(stream -> Util.decodeFixedLengthArray(stream, 3).map(coordinates -> new Vec3i(coordinates[0], coordinates[1], coordinates[2])), vec -> IntStream.of(vec.getX(), vec.getY(), vec.getZ()));
    final static public PacketCodec<ByteBuf, Vec3i> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, Vec3i::getX, PacketCodecs.VAR_INT, Vec3i::getY, PacketCodecs.VAR_INT, Vec3i::getZ, Vec3i::new);
    final static public Vec3i ZERO = new Vec3i(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public static Codec<Vec3i> createOffsetCodec(int maxAbsValue) {
        return CODEC.validate(vec -> {
            if (Math.abs(vec.getX()) < maxAbsValue && Math.abs(vec.getY()) < maxAbsValue && Math.abs(vec.getZ()) < maxAbsValue) {
                return DataResult.success((Object)vec);
            }
            return DataResult.error(() -> "Position out of range, expected at most " + maxAbsValue + ": " + String.valueOf(vec));
        });
    }

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vec3i)) {
            return false;
        }
        Vec3i vec3i = (Vec3i)o;
        if (this.getX() != vec3i.getX()) {
            return false;
        }
        if (this.getY() != vec3i.getY()) {
            return false;
        }
        return this.getZ() == vec3i.getZ();
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public int compareTo(Vec3i vec3i) {
        if (this.getY() == vec3i.getY()) {
            if (this.getZ() == vec3i.getZ()) {
                return this.getX() - vec3i.getX();
            }
            return this.getZ() - vec3i.getZ();
        }
        return this.getY() - vec3i.getY();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    protected Vec3i net_minecraft_util_math_Vec3i_setX(int x) {
        this.x = x;
        return this;
    }

    protected Vec3i net_minecraft_util_math_Vec3i_setY(int y) {
        this.y = y;
        return this;
    }

    protected Vec3i net_minecraft_util_math_Vec3i_setZ(int z) {
        this.z = z;
        return this;
    }

    public Vec3i net_minecraft_util_math_Vec3i_add(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            return this;
        }
        return new Vec3i(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public Vec3i net_minecraft_util_math_Vec3i_add(Vec3i vec) {
        return this.net_minecraft_util_math_Vec3i_add(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vec3i net_minecraft_util_math_Vec3i_subtract(Vec3i vec) {
        return this.net_minecraft_util_math_Vec3i_add(-vec.getX(), -vec.getY(), -vec.getZ());
    }

    public Vec3i net_minecraft_util_math_Vec3i_multiply(int scale) {
        if (scale == 1) {
            return this;
        }
        if (scale == 0) {
            return ZERO;
        }
        return new Vec3i(this.getX() * scale, this.getY() * scale, this.getZ() * scale);
    }

    public Vec3i net_minecraft_util_math_Vec3i_up() {
        return this.net_minecraft_util_math_Vec3i_up(1);
    }

    public Vec3i net_minecraft_util_math_Vec3i_up(int distance) {
        return this.net_minecraft_util_math_Vec3i_offset(Direction.UP, distance);
    }

    public Vec3i net_minecraft_util_math_Vec3i_down() {
        return this.net_minecraft_util_math_Vec3i_down(1);
    }

    public Vec3i net_minecraft_util_math_Vec3i_down(int distance) {
        return this.net_minecraft_util_math_Vec3i_offset(Direction.DOWN, distance);
    }

    public Vec3i net_minecraft_util_math_Vec3i_north() {
        return this.net_minecraft_util_math_Vec3i_north(1);
    }

    public Vec3i net_minecraft_util_math_Vec3i_north(int distance) {
        return this.net_minecraft_util_math_Vec3i_offset(Direction.NORTH, distance);
    }

    public Vec3i net_minecraft_util_math_Vec3i_south() {
        return this.net_minecraft_util_math_Vec3i_south(1);
    }

    public Vec3i net_minecraft_util_math_Vec3i_south(int distance) {
        return this.net_minecraft_util_math_Vec3i_offset(Direction.SOUTH, distance);
    }

    public Vec3i net_minecraft_util_math_Vec3i_west() {
        return this.net_minecraft_util_math_Vec3i_west(1);
    }

    public Vec3i net_minecraft_util_math_Vec3i_west(int distance) {
        return this.net_minecraft_util_math_Vec3i_offset(Direction.WEST, distance);
    }

    public Vec3i net_minecraft_util_math_Vec3i_east() {
        return this.net_minecraft_util_math_Vec3i_east(1);
    }

    public Vec3i net_minecraft_util_math_Vec3i_east(int distance) {
        return this.net_minecraft_util_math_Vec3i_offset(Direction.EAST, distance);
    }

    public Vec3i net_minecraft_util_math_Vec3i_offset(Direction direction) {
        return this.net_minecraft_util_math_Vec3i_offset(direction, 1);
    }

    public Vec3i net_minecraft_util_math_Vec3i_offset(Direction direction, int distance) {
        if (distance == 0) {
            return this;
        }
        return new Vec3i(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance);
    }

    public Vec3i net_minecraft_util_math_Vec3i_offset(Direction.Axis axis, int distance) {
        if (distance == 0) {
            return this;
        }
        int i = axis == Direction.Axis.X ? distance : 0;
        int j = axis == Direction.Axis.Y ? distance : 0;
        int k = axis == Direction.Axis.Z ? distance : 0;
        return new Vec3i(this.getX() + 1, this.getY() + j, this.getZ() + k);
    }

    public Vec3i net_minecraft_util_math_Vec3i_crossProduct(Vec3i vec) {
        return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    public boolean isWithinDistance(Vec3i vec, double distance) {
        return this.getSquaredDistance(vec) < MathHelper.square(distance);
    }

    public boolean isWithinDistance(Position pos, double distance) {
        return this.getSquaredDistance(pos) < MathHelper.square(distance);
    }

    public double getSquaredDistance(Vec3i vec) {
        return this.getSquaredDistance(vec.getX(), vec.getY(), vec.getZ());
    }

    public double getSquaredDistance(Position pos) {
        return this.getSquaredDistanceFromCenter(pos.getX(), pos.getY(), pos.getZ());
    }

    public double getSquaredDistanceFromCenter(double x, double y, double z) {
        double d = (double)this.getX() + 0.5 - x;
        double e = (double)this.getY() + 0.5 - y;
        double f = (double)this.getZ() + 0.5 - z;
        return d * d + e * e + f * f;
    }

    public double getSquaredDistance(double x, double y, double z) {
        double d = (double)this.getX() - x;
        double e = (double)this.getY() - y;
        double f = (double)this.getZ() - z;
        return d * d + e * e + f * f;
    }

    public int getManhattanDistance(Vec3i vec) {
        float f = Math.abs(vec.getX() - this.getX());
        float g = Math.abs(vec.getY() - this.getY());
        float h = Math.abs(vec.getZ() - this.getZ());
        return (int)(f + g + h);
    }

    public int getChebyshevDistance(Vec3i vec) {
        int i = Math.abs(this.getX() - vec.getX());
        int j = Math.abs(this.getY() - vec.getY());
        int k = Math.abs(this.getZ() - vec.getZ());
        return Math.max(Math.max(i, j), k);
    }

    public int getComponentAlongAxis(Direction.Axis axis) {
        return axis.choose(this.x, this.y, this.z);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
    }

    public String toShortString() {
        return this.getX() + ", " + this.getY() + ", " + this.getZ();
    }

    @Override
    public int compareTo(Object vec) {
        return this.compareTo((Vec3i)vec);
    }
}

