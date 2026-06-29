/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.screen.slot;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;

public final class SlotActionType
extends Enum<SlotActionType> {
    final static public SlotActionType PICKUP = new SlotActionType(0);
    final static public SlotActionType QUICK_MOVE = new SlotActionType(1);
    final static public SlotActionType SWAP = new SlotActionType(2);
    final static public SlotActionType CLONE = new SlotActionType(3);
    final static public SlotActionType THROW = new SlotActionType(4);
    final static public SlotActionType QUICK_CRAFT = new SlotActionType(5);
    final static public SlotActionType PICKUP_ALL = new SlotActionType(6);
    final static private IntFunction<SlotActionType> INDEX_MAPPER;
    final static public PacketCodec<ByteBuf, SlotActionType> PACKET_CODEC;
    final private int index;
    final static private SlotActionType[] field_7792;

    public static SlotActionType[] values() {
        return (SlotActionType[])field_7792.clone();
    }

    public static SlotActionType valueOf(String string) {
        return Enum.valueOf(SlotActionType.class, string);
    }

    private SlotActionType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    private static SlotActionType[] method_36673() {
        return new SlotActionType[]{PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL};
    }

    static {
        field_7792 = SlotActionType.method_36673();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(SlotActionType::getIndex, SlotActionType.values(), ValueLists.OutOfBoundsHandling.ZERO);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, SlotActionType::getIndex);
    }
}

