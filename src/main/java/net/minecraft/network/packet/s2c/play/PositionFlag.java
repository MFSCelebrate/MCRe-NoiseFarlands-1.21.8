/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class PositionFlag
extends Enum<PositionFlag> {
    final static public PositionFlag X = new PositionFlag(0);
    final static public PositionFlag Y = new PositionFlag(1);
    final static public PositionFlag Z = new PositionFlag(2);
    final static public PositionFlag Y_ROT = new PositionFlag(3);
    final static public PositionFlag X_ROT = new PositionFlag(4);
    final static public PositionFlag DELTA_X = new PositionFlag(5);
    final static public PositionFlag DELTA_Y = new PositionFlag(6);
    final static public PositionFlag DELTA_Z = new PositionFlag(7);
    final static public PositionFlag ROTATE_DELTA = new PositionFlag(8);
    final static public Set<PositionFlag> VALUES;
    final static public Set<PositionFlag> ROT;
    final static public Set<PositionFlag> DELTA;
    final static public PacketCodec<ByteBuf, Set<PositionFlag>> PACKET_CODEC;
    final private int shift;
    final static private PositionFlag[] field_12402;

    public static PositionFlag[] values() {
        return (PositionFlag[])field_12402.clone();
    }

    public static PositionFlag valueOf(String string) {
        return Enum.valueOf(PositionFlag.class, string);
    }

    @SafeVarargs
    public static Set<PositionFlag> combine(Set<PositionFlag> ... sets) {
        HashSet<PositionFlag> hashSet = new HashSet<PositionFlag>();
        for (Set<PositionFlag> set : sets) {
            hashSet.addAll(set);
        }
        return hashSet;
    }

    private PositionFlag(int shift) {
        this.shift = shift;
    }

    private int getMask() {
        return 1 << this.shift;
    }

    private boolean isSet(int mask) {
        return (mask & this.getMask()) == this.getMask();
    }

    public static Set<PositionFlag> getFlags(int mask) {
        EnumSet<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
        for (PositionFlag positionFlag : PositionFlag.values()) {
            if (!positionFlag.isSet(mask)) continue;
            set.add(positionFlag);
        }
        return set;
    }

    public static int getBitfield(Set<PositionFlag> flags) {
        int i = 0;
        for (PositionFlag positionFlag : flags) {
            i |= positionFlag.getMask();
        }
        return i;
    }

    private static PositionFlag[] method_36952() {
        return new PositionFlag[]{X, Y, Z, Y_ROT, X_ROT, DELTA_X, DELTA_Y, DELTA_Z, ROTATE_DELTA};
    }

    static {
        field_12402 = PositionFlag.method_36952();
        VALUES = Set.of(PositionFlag.values());
        ROT = Set.of(X_ROT, Y_ROT);
        DELTA = Set.of(DELTA_X, DELTA_Y, DELTA_Z, ROTATE_DELTA);
        PACKET_CODEC = PacketCodecs.INTEGER.xmap(PositionFlag::getFlags, PositionFlag::getBitfield);
    }
}

