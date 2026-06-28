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

public record NbtInt(int value) implements AbstractNbtNumber
{
    final static private int SIZE = 12;
    final static public NbtType<NbtInt> TYPE = new NbtType.OfFixedSize<NbtInt>(){

        @Override
        public NbtInt net_minecraft_nbt_NbtInt_read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
            return NbtInt.of(_1.readInt(dataInput, nbtSizeTracker));
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            return visitor.visitInt(_1.readInt(input, tracker));
        }

        private static int readInt(DataInput input, NbtSizeTracker tracker) throws IOException {
            tracker.add(12L);
            return input.readInt();
        }

        @Override
        public int getSizeInBytes() {
            return 4;
        }

        @Override
        public String getCrashReportName() {
            return "INT";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Int";
        }

        @Override
        public NbtElement net_minecraft_nbt_NbtElement_read(DataInput input, NbtSizeTracker tracker) throws IOException {
            return this.net_minecraft_nbt_NbtInt_read(input, tracker);
        }
    };

    public static NbtInt of(int value) {
        if (value >= -128 && value <= 1024) {
            return Cache.VALUES[value - -128];
        }
        return new NbtInt(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value);
    }

    @Override
    public int getSizeInBytes() {
        return 12;
    }

    @Override
    public byte getType() {
        return NbtElement.INT_TYPE;
    }

    public NbtType<NbtInt> getNbtType() {
        return TYPE;
    }

    @Override
    public NbtInt net_minecraft_nbt_NbtInt_copy() {
        return this;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitInt(this);
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
        return (short)(this.value & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(this.value & 0xFF);
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
        return visitor.visitInt(this.value);
    }

    @Override
    public String toString() {
        StringNbtWriter stringNbtWriter = new StringNbtWriter();
        stringNbtWriter.visitInt(this);
        return stringNbtWriter.getString();
    }

    @Override
    public NbtElement net_minecraft_nbt_NbtElement_copy() {
        return this.net_minecraft_nbt_NbtInt_copy();
    }

    static class Cache {
        final static private int MAX = 1024;
        final static private int MIN = -128;
        final static NbtInt[] VALUES = new NbtInt[1153];

        private Cache() {
        }

        static {
            for (int i = 0; i < VALUES.length; ++i) {
                Cache.VALUES[i] = new NbtInt(-128 + i);
            }
        }
    }
}

