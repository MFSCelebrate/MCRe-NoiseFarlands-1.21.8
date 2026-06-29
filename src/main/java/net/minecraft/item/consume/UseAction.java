/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.item.consume;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class UseAction
extends Enum<UseAction>
implements StringIdentifiable {
    final static public UseAction NONE = new UseAction(0, "none");
    final static public UseAction EAT = new UseAction(1, "eat");
    final static public UseAction DRINK = new UseAction(2, "drink");
    final static public UseAction BLOCK = new UseAction(3, "block");
    final static public UseAction BOW = new UseAction(4, "bow");
    final static public UseAction SPEAR = new UseAction(5, "spear");
    final static public UseAction CROSSBOW = new UseAction(6, "crossbow");
    final static public UseAction SPYGLASS = new UseAction(7, "spyglass");
    final static public UseAction TOOT_HORN = new UseAction(8, "toot_horn");
    final static public UseAction BRUSH = new UseAction(9, "brush");
    final static public UseAction BUNDLE = new UseAction(10, "bundle");
    final static private IntFunction<UseAction> BY_ID;
    final static public Codec<UseAction> CODEC;
    final static public PacketCodec<ByteBuf, UseAction> PACKET_CODEC;
    final private int id;
    final private String name;
    final static private UseAction[] field_8948;

    public static UseAction[] values() {
        return (UseAction[])field_8948.clone();
    }

    public static UseAction valueOf(String string) {
        return Enum.valueOf(UseAction.class, string);
    }

    private UseAction(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static UseAction[] method_36686() {
        return new UseAction[]{NONE, EAT, DRINK, BLOCK, BOW, SPEAR, CROSSBOW, SPYGLASS, TOOT_HORN, BRUSH, BUNDLE};
    }

    static {
        field_8948 = UseAction.method_36686();
        BY_ID = ValueLists.createIndexToValueFunction(UseAction::getId, UseAction.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(UseAction::values);
        PACKET_CODEC = PacketCodecs.indexed(BY_ID, UseAction::getId);
    }
}

