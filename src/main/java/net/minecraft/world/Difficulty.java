/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.Nullable;

public final class Difficulty
extends Enum<Difficulty>
implements StringIdentifiable {
    final static public Difficulty PEACEFUL = new Difficulty(0, "peaceful");
    final static public Difficulty EASY = new Difficulty(1, "easy");
    final static public Difficulty NORMAL = new Difficulty(2, "normal");
    final static public Difficulty HARD = new Difficulty(3, "hard");
    final static public StringIdentifiable.EnumCodec<Difficulty> CODEC;
    final static private IntFunction<Difficulty> BY_ID;
    final static public PacketCodec<ByteBuf, Difficulty> PACKET_CODEC;
    final private int id;
    final private String name;
    final static private Difficulty[] field_5804;

    public static Difficulty[] values() {
        return (Difficulty[])field_5804.clone();
    }

    public static Difficulty valueOf(String string) {
        return Enum.valueOf(Difficulty.class, string);
    }

    private Difficulty(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public Text getTranslatableName() {
        return Text.translatable("options.difficulty." + this.name);
    }

    public Text getInfo() {
        return Text.translatable("options.difficulty." + this.name + ".info");
    }

    @Deprecated
    public static Difficulty byId(int id) {
        return BY_ID.apply(id);
    }

    @Nullable
    public static Difficulty byName(String name) {
        return CODEC.byId(name);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static Difficulty[] method_36597() {
        return new Difficulty[]{PEACEFUL, EASY, NORMAL, HARD};
    }

    static {
        field_5804 = Difficulty.method_36597();
        CODEC = StringIdentifiable.createCodec(Difficulty::values);
        BY_ID = ValueLists.createIndexToValueFunction(Difficulty::getId, Difficulty.values(), ValueLists.OutOfBoundsHandling.WRAP);
        PACKET_CODEC = PacketCodecs.indexed(BY_ID, Difficulty::getId);
    }
}

