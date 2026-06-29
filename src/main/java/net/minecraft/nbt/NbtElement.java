/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtPrimitive;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;

public sealed interface NbtElement
permits NbtCompound, AbstractNbtList, NbtPrimitive, NbtEnd {
    final static public int field_33246 = 8;
    final static public int field_33247 = 12;
    final static public int field_33248 = 4;
    final static public int field_33249 = 28;
    final static public byte END_TYPE = 0;
    final static public byte BYTE_TYPE = 1;
    final static public byte SHORT_TYPE = 2;
    final static public byte INT_TYPE = 3;
    final static public byte LONG_TYPE = 4;
    final static public byte FLOAT_TYPE = 5;
    final static public byte DOUBLE_TYPE = 6;
    final static public byte BYTE_ARRAY_TYPE = 7;
    final static public byte STRING_TYPE = 8;
    final static public byte LIST_TYPE = 9;
    final static public byte COMPOUND_TYPE = 10;
    final static public byte INT_ARRAY_TYPE = 11;
    final static public byte LONG_ARRAY_TYPE = 12;
    final static public int MAX_DEPTH = 512;

    public void write(DataOutput var1) throws IOException;

    public String toString();

    public byte getType();

    public NbtType<?> getNbtType();

    public NbtElement net_minecraft_nbt_NbtElement_copy();

    public int getSizeInBytes();

    public void accept(NbtElementVisitor var1);

    public NbtScanner.Result doAccept(NbtScanner var1);

    default public void accept(NbtScanner visitor) {
        NbtScanner.Result result = visitor.start(this.getNbtType());
        if (result == NbtScanner.Result.CONTINUE) {
            this.doAccept(visitor);
        }
    }

    default public Optional<String> asString() {
        return Optional.empty();
    }

    default public Optional<Number> asNumber() {
        return Optional.empty();
    }

    default public Optional<Byte> asByte() {
        return this.asNumber().map(Number::byteValue);
    }

    default public Optional<Short> asShort() {
        return this.asNumber().map(Number::shortValue);
    }

    default public Optional<Integer> asInt() {
        return this.asNumber().map(Number::intValue);
    }

    default public Optional<Long> asLong() {
        return this.asNumber().map(Number::longValue);
    }

    default public Optional<Float> asFloat() {
        return this.asNumber().map(Number::floatValue);
    }

    default public Optional<Double> asDouble() {
        return this.asNumber().map(Number::doubleValue);
    }

    default public Optional<Boolean> asBoolean() {
        return this.asByte().map(b -> b != 0);
    }

    default public Optional<byte[]> asByteArray() {
        return Optional.empty();
    }

    default public Optional<int[]> asIntArray() {
        return Optional.empty();
    }

    default public Optional<long[]> asLongArray() {
        return Optional.empty();
    }

    default public Optional<NbtCompound> asCompound() {
        return Optional.empty();
    }

    default public Optional<NbtList> asNbtList() {
        return Optional.empty();
    }
}

