/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.dialog;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class AfterAction
extends Enum<AfterAction>
implements StringIdentifiable {
    final static public AfterAction CLOSE = new AfterAction(0, "close");
    final static public AfterAction NONE = new AfterAction(1, "none");
    final static public AfterAction WAIT_FOR_RESPONSE = new AfterAction(2, "wait_for_response");
    final static public IntFunction<AfterAction> INDEX_MAPPER;
    final static public StringIdentifiable.EnumCodec<AfterAction> CODEC;
    final static public PacketCodec<ByteBuf, AfterAction> PACKET_CODEC;
    final private int index;
    final private String id;
    final static private AfterAction[] field_60970;

    public static AfterAction[] values() {
        return (AfterAction[])field_60970.clone();
    }

    public static AfterAction valueOf(String string) {
        return Enum.valueOf(AfterAction.class, string);
    }

    private AfterAction(int index, String id) {
        this.index = index;
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public boolean canUnpause() {
        return this == CLOSE || this == WAIT_FOR_RESPONSE;
    }

    private static AfterAction[] method_72065() {
        return new AfterAction[]{CLOSE, NONE, WAIT_FOR_RESPONSE};
    }

    static {
        field_60970 = AfterAction.method_72065();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(afterAction -> afterAction.index, AfterAction.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(AfterAction::values);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, afterAction -> afterAction.index);
    }
}

