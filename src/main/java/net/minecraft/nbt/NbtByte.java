/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public record NbtByte(byte value) implements AbstractNbtNumber
{
    final static private int SIZE = 9;
    final static public NbtType<NbtByte> TYPE = new NbtType.OfFixedSize<NbtByte>(){

        @Override
        public NbtByte net_minecraft_nbt_NbtByte_read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
            return NbtByte.of(_1.readByte(dataInput, nbtSizeTracker));
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            return visitor.visitByte(_1.readByte(input, tracker));
        }

        private static byte readByte(DataInput input, NbtSizeTracker tracker) throws IOException {
            tracker.add(9L);
            return input.readByte();
        }

        @Override
        public int getSizeInBytes() {
            return 1;
        }

        @Override
        public String getCrashReportName() {
            return "BYTE";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Byte";
        }

        @Override
        public NbtElement net_minecraft_nbt_NbtElement_read(DataInput input, NbtSizeTracker tracker) throws IOException {
            return this.net_minecraft_nbt_NbtByte_read(input, tracker);
        }
    };
    final static public NbtByte ZERO = NbtByte.of(0);
    final static public NbtByte ONE = NbtByte.of(1);

    public static NbtByte of(byte value) {
        return Cache.VALUES[128 + value];
    }

    public static NbtByte of(boolean value) {
        return value ? ONE : ZERO;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.value);
    }

    @Override
    public int getSizeInBytes() {
        return 9;
    }

    @Override
    public byte getType() {
        return NbtElement.BYTE_TYPE;
    }

    public NbtType<NbtByte> getNbtType() {
        return TYPE;
    }

    @Override
    public NbtByte net_minecraft_nbt_NbtByte_copy() {
        return this;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitByte(this);
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public short shortValue() {
        return this.value;
    }

    @Override
    public byte byteValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitByte(this.value);
    }

    @Override
    public String toString() {
        StringNbtWriter stringNbtWriter = new StringNbtWriter();
        stringNbtWriter.visitByte(this);
        return stringNbtWriter.getString();
    }

    @Override
    public NbtElement net_minecraft_nbt_NbtElement_copy() {
        return this.net_minecraft_nbt_NbtByte_copy();
    }

    static class Cache {
        final static NbtByte[] VALUES = new NbtByte[256];

        private Cache() {
        }

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new NbtByte((byte)(i - 128));
            }
        }
    }
}

