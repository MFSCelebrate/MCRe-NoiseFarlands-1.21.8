/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtType;

public interface NbtScanner {
    public Result visitEnd();

    public Result visitString(String var1);

    public Result visitByte(byte var1);

    public Result visitShort(short var1);

    public Result visitInt(int var1);

    public Result visitLong(long var1);

    public Result visitFloat(float var1);

    public Result visitDouble(double var1);

    public Result visitByteArray(byte[] var1);

    public Result visitIntArray(int[] var1);

    public Result visitLongArray(long[] var1);

    public Result visitListMeta(NbtType<?> var1, int var2);

    public NestedResult visitSubNbtType(NbtType<?> var1);

    public NestedResult startSubNbt(NbtType<?> var1, String var2);

    public NestedResult startListItem(NbtType<?> var1, int var2);

    public Result endNested();

    public Result start(NbtType<?> var1);

    public static final class NestedResult
    extends Enum<NestedResult> {
        final static public NestedResult ENTER = new NestedResult();
        final static public NestedResult SKIP = new NestedResult();
        final static public NestedResult BREAK = new NestedResult();
        final static public NestedResult HALT = new NestedResult();
        final static private NestedResult[] field_36252;

        public static NestedResult[] values() {
            return (NestedResult[])field_36252.clone();
        }

        public static NestedResult valueOf(String string) {
            return Enum.valueOf(NestedResult.class, string);
        }

        private static NestedResult[] method_39873() {
            return new NestedResult[]{ENTER, SKIP, BREAK, HALT};
        }

        static {
            field_36252 = NestedResult.method_39873();
        }
    }

    public static final class Result
    extends Enum<Result> {
        final static public Result CONTINUE = new Result();
        final static public Result BREAK = new Result();
        final static public Result HALT = new Result();
        final static private Result[] field_36256;

        public static Result[] values() {
            return (Result[])field_36256.clone();
        }

        public static Result valueOf(String string) {
            return Enum.valueOf(Result.class, string);
        }

        private static Result[] method_39874() {
            return new Result[]{CONTINUE, BREAK, HALT};
        }

        static {
            field_36256 = Result.method_39874();
        }
    }
}

