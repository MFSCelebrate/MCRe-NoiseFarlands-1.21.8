/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.block.enums;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class TestBlockMode
extends Enum<TestBlockMode>
implements StringIdentifiable {
    final static public TestBlockMode START = new TestBlockMode(0, "start");
    final static public TestBlockMode LOG = new TestBlockMode(1, "log");
    final static public TestBlockMode FAIL = new TestBlockMode(2, "fail");
    final static public TestBlockMode ACCEPT = new TestBlockMode(3, "accept");
    final static private IntFunction<TestBlockMode> INDEX_MAPPER;
    final static public Codec<TestBlockMode> CODEC;
    final static public PacketCodec<ByteBuf, TestBlockMode> PACKET_CODEC;
    final private int index;
    final private String id;
    final private Text name;
    final private Text info;
    final static private TestBlockMode[] field_56035;

    public static TestBlockMode[] values() {
        return (TestBlockMode[])field_56035.clone();
    }

    public static TestBlockMode valueOf(String string) {
        return Enum.valueOf(TestBlockMode.class, string);
    }

    private TestBlockMode(int index, String id) {
        this.index = index;
        this.id = id;
        this.name = Text.translatable("test_block.mode." + id);
        this.info = Text.translatable("test_block.mode_info." + id);
    }

    @Override
    public String asString() {
        return this.id;
    }

    public Text getName() {
        return this.name;
    }

    public Text getInfo() {
        return this.info;
    }

    private static TestBlockMode[] method_66785() {
        return new TestBlockMode[]{START, LOG, FAIL, ACCEPT};
    }

    static {
        field_56035 = TestBlockMode.method_66785();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(mode -> mode.index, TestBlockMode.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(TestBlockMode::values);
        PACKET_CODEC = PacketCodecs.indexed(INDEX_MAPPER, mode -> mode.index);
    }
}

