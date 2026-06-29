/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;
import org.apache.commons.lang3.ArrayUtils;

public final class NbtLongArray
implements AbstractNbtList {
    final static private int SIZE = 24;
    final static public NbtType<NbtLongArray> TYPE = new NbtType.OfVariableSize<NbtLongArray>(){

        @Override
        public NbtLongArray net_minecraft_nbt_NbtLongArray_read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
            return new NbtLongArray(_1.readLongArray(dataInput, nbtSizeTracker));
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            return visitor.visitLongArray(_1.readLongArray(input, tracker));
        }

        private static long[] readLongArray(DataInput input, NbtSizeTracker tracker) throws IOException {
            tracker.add(24L);
            int i = input.readInt();
            tracker.add(8L, i);
            long[] ls = new long[i];
            for (int j = 0; j < i; ++j) {
                ls[j] = input.readLong();
            }
            return ls;
        }

        @Override
        public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
            input.skipBytes(input.readInt() * 8);
        }

        @Override
        public String getCrashReportName() {
            return "LONG[]";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_Long_Array";
        }

        @Override
        public NbtElement net_minecraft_nbt_NbtElement_read(DataInput input, NbtSizeTracker tracker) throws IOException {
            return this.net_minecraft_nbt_NbtLongArray_read(input, tracker);
        }
    };
    private long[] value;

    public NbtLongArray(long[] value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value.length);
        for (long l : this.value) {
            output.writeLong(l);
        }
    }

    @Override
    public int getSizeInBytes() {
        return 24 + 8 * this.value.length;
    }

    @Override
    public byte getType() {
        return NbtElement.LONG_ARRAY_TYPE;
    }

    public NbtType<NbtLongArray> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringNbtWriter stringNbtWriter = new StringNbtWriter();
        stringNbtWriter.visitLongArray(this);
        return stringNbtWriter.getString();
    }

    @Override
    public NbtLongArray net_minecraft_nbt_NbtLongArray_copy() {
        long[] ls = new long[this.value.length];
        System.arraycopy(this.value, 0, ls, 0, this.value.length);
        return new NbtLongArray(ls);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof NbtLongArray && Arrays.equals(this.value, ((NbtLongArray)o).value);
    }

    public int hashCode() {
        return Arrays.hashCode(this.value);
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitLongArray(this);
    }

    public long[] getLongArray() {
        return this.value;
    }

    @Override
    public int size() {
        return this.value.length;
    }

    @Override
    public NbtLong net_minecraft_nbt_NbtLong_method_10534(int i) {
        return NbtLong.of(this.value[i]);
    }

    @Override
    public boolean setElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            AbstractNbtNumber abstractNbtNumber = (AbstractNbtNumber)element;
            this.value[index] = abstractNbtNumber.longValue();
            return true;
        }
        return false;
    }

    @Override
    public boolean addElement(int index, NbtElement element) {
        if (element instanceof AbstractNbtNumber) {
            AbstractNbtNumber abstractNbtNumber = (AbstractNbtNumber)element;
            this.value = ArrayUtils.add((long[])this.value, (int)index, (long)abstractNbtNumber.longValue());
            return true;
        }
        return false;
    }

    @Override
    public NbtLong net_minecraft_nbt_NbtLong_method_10536(int i) {
        long l = this.value[i];
        this.value = ArrayUtils.remove((long[])this.value, (int)i);
        return NbtLong.of(l);
    }

    @Override
    public void clear() {
        this.value = new long[0];
    }

    @Override
    public Optional<long[]> asLongArray() {
        return Optional.of(this.value);
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitLongArray(this.value);
    }

    @Override
    public NbtElement net_minecraft_nbt_NbtElement_method_10534(int i) {
        return this.net_minecraft_nbt_NbtLong_method_10534(i);
    }

    @Override
    public NbtElement net_minecraft_nbt_NbtElement_method_10536(int i) {
        return this.net_minecraft_nbt_NbtLong_method_10536(i);
    }

    @Override
    public NbtElement net_minecraft_nbt_NbtElement_copy() {
        return this.net_minecraft_nbt_NbtLongArray_copy();
    }
}

