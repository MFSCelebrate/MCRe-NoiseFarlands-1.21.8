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
import net.minecraft.util.math.MathHelper;

public record NbtDouble(double value) implements AbstractNbtNumber
{
    final static private int SIZE = 16;
    final static public NbtDouble ZERO = new NbtDouble(0.0);
    final static public NbtType<NbtDouble> TYPE = new NbtType.OfFixedSize<NbtDouble>(){

        @Override
        public NbtDouble net_minecraft_nbt_NbtDouble_read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
            return NbtDouble.of(_1.readDouble(dataInput, nbtSizeTracker));
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            return visitor.visitDouble(_1.readDouble(input, tracker));
        }

        private static double readDouble(DataInput input, NbtSizeTracker tracker) throws IOException {
            tracker.add(16L);
            return input.readDouble();
        }

        @Override
        public int getSizeInBytes() {
            return 8;
        }

        @Override
        public String getCrashReportName() {
            return "DOUBLE";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Double";
        }

        @Override
        public NbtElement net_minecraft_nbt_NbtElement_read(DataInput input, NbtSizeTracker tracker) throws IOException {
            return this.net_minecraft_nbt_NbtDouble_read(input, tracker);
        }
    };

    public static NbtDouble of(double value) {
        if (value == 0.0) {
            return ZERO;
        }
        return new NbtDouble(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.value);
    }

    @Override
    public int getSizeInBytes() {
        return 16;
    }

    @Override
    public byte getType() {
        return NbtElement.DOUBLE_TYPE;
    }

    public NbtType<NbtDouble> getNbtType() {
        return TYPE;
    }

    @Override
    public NbtDouble net_minecraft_nbt_NbtDouble_copy() {
        return this;
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitDouble(this);
    }

    @Override
    public long longValue() {
        return (long)Math.floor(this.value);
    }

    @Override
    public int intValue() {
        return MathHelper.floor(this.value);
    }

    @Override
    public short shortValue() {
        return (short)(MathHelper.floor(this.value) & 0xFFFF);
    }

    @Override
    public byte byteValue() {
        return (byte)(MathHelper.floor(this.value) & 0xFF);
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return (float)this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitDouble(this.value);
    }

    @Override
    public String toString() {
        StringNbtWriter stringNbtWriter = new StringNbtWriter();
        stringNbtWriter.visitDouble(this);
        return stringNbtWriter.getString();
    }

    @Override
    public NbtElement net_minecraft_nbt_NbtElement_copy() {
        return this.net_minecraft_nbt_NbtDouble_copy();
    }
}

