/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtPrimitive;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.nbt.visitor.StringNbtWriter;

public record NbtString(String value) implements NbtPrimitive
{
    final static private int SIZE = 36;
    final static public NbtType<NbtString> TYPE = new NbtType.OfVariableSize<NbtString>(){

        @Override
        public NbtString net_minecraft_nbt_NbtString_read(DataInput dataInput, NbtSizeTracker nbtSizeTracker) throws IOException {
            return NbtString.of(_1.readString(dataInput, nbtSizeTracker));
        }

        @Override
        public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor, NbtSizeTracker tracker) throws IOException {
            return visitor.visitString(_1.readString(input, tracker));
        }

        private static String readString(DataInput input, NbtSizeTracker tracker) throws IOException {
            tracker.add(36L);
            String string = input.readUTF();
            tracker.add(2L, string.length());
            return string;
        }

        @Override
        public void skip(DataInput input, NbtSizeTracker tracker) throws IOException {
            NbtString.skip(input);
        }

        @Override
        public String getCrashReportName() {
            return "STRING";
        }

        @Override
        public String getCommandFeedbackName() {
            return "TAG_String";
        }

        @Override
        public NbtElement net_minecraft_nbt_NbtElement_read(DataInput input, NbtSizeTracker tracker) throws IOException {
            return this.net_minecraft_nbt_NbtString_read(input, tracker);
        }
    };
    final static private NbtString EMPTY = new NbtString("");
    final static private char DOUBLE_QUOTE = '\"';
    final static private char SINGLE_QUOTE = '\'';
    final static private char BACKSLASH = '\\';
    final static private char NULL = '\u0000';

    public static void skip(DataInput input) throws IOException {
        input.skipBytes(input.readUnsignedShort());
    }

    public static NbtString of(String value) {
        if (value.isEmpty()) {
            return EMPTY;
        }
        return new NbtString(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public int getSizeInBytes() {
        return 36 + 2 * this.value.length();
    }

    @Override
    public byte getType() {
        return NbtElement.STRING_TYPE;
    }

    public NbtType<NbtString> getNbtType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringNbtWriter stringNbtWriter = new StringNbtWriter();
        stringNbtWriter.visitString(this);
        return stringNbtWriter.getString();
    }

    @Override
    public NbtString net_minecraft_nbt_NbtString_copy() {
        return this;
    }

    @Override
    public Optional<String> asString() {
        return Optional.of(this.value);
    }

    @Override
    public void accept(NbtElementVisitor visitor) {
        visitor.visitString(this);
    }

    public static String escape(String value) {
        StringBuilder stringBuilder = new StringBuilder();
        NbtString.appendEscaped(value, stringBuilder);
        return stringBuilder.toString();
    }

    public static void appendEscaped(String value, StringBuilder builder) {
        int i = builder.length();
        builder.append(' ');
        int c = 0;
        for (int j = 0; j < value.length(); ++j) {
            int d = value.charAt(j);
            if (d == 92) {
                builder.append("\\\\");
                continue;
            }
            if (d == 34 || d == 39) {
                if (c == 0) {
                    int n = c = d == 34 ? 39 : 34;
                }
                if (c == d) {
                    builder.append('\\');
                }
                builder.append((char)d);
                continue;
            }
            String string = SnbtParsing.escapeSpecialChar((char)d);
            if (string != null) {
                builder.append('\\');
                builder.append(string);
                continue;
            }
            builder.append((char)d);
        }
        if (c == 0) {
            c = 34;
        }
        builder.setCharAt(i, (char)c);
        builder.append((char)c);
    }

    public static String method_72226(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        NbtString.method_72225(string, stringBuilder);
        return stringBuilder.toString();
    }

    public static void method_72225(String string, StringBuilder stringBuilder) {
        block3: for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '\"': 
                case '\'': 
                case '\\': {
                    stringBuilder.append('\\');
                    stringBuilder.append(c);
                    continue block3;
                }
                default: {
                    String string2 = SnbtParsing.escapeSpecialChar(c);
                    if (string2 != null) {
                        stringBuilder.append('\\');
                        stringBuilder.append(string2);
                        continue block3;
                    }
                    stringBuilder.append(c);
                }
            }
        }
    }

    @Override
    public NbtScanner.Result doAccept(NbtScanner visitor) {
        return visitor.visitString(this.value);
    }

    @Override
    public NbtElement net_minecraft_nbt_NbtElement_copy() {
        return this.net_minecraft_nbt_NbtString_copy();
    }
}

