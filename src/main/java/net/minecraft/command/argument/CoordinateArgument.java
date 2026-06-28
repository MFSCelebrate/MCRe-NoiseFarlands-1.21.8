/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.text.Text;

public class CoordinateArgument {
    final static private char TILDE = '~';
    final static public SimpleCommandExceptionType MISSING_COORDINATE = new SimpleCommandExceptionType((Message)Text.translatable("argument.pos.missing.double"));
    final static public SimpleCommandExceptionType MISSING_BLOCK_POSITION = new SimpleCommandExceptionType((Message)Text.translatable("argument.pos.missing.int"));
    final private boolean relative;
    final private double value;

    public CoordinateArgument(boolean relative, double value) {
        this.relative = relative;
        this.value = value;
    }

    public double toAbsoluteCoordinate(double offset) {
        if (this.relative) {
            return this.value + offset;
        }
        return this.value;
    }

    public static CoordinateArgument parse(StringReader reader, boolean centerIntegers) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        if (!reader.canRead()) {
            throw MISSING_COORDINATE.createWithContext((ImmutableStringReader)reader);
        }
        boolean bl = CoordinateArgument.isRelative(reader);
        int i = reader.getCursor();
        double d = reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0;
        String string = reader.getString().substring(1, reader.getCursor());
        if (bl && string.isEmpty()) {
            return new CoordinateArgument(true, 0.0);
        }
        if (!string.contains(".") && !bl && centerIntegers) {
            d += 0.5;
        }
        return new CoordinateArgument(bl, d);
    }

    public static CoordinateArgument parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        if (!reader.canRead()) {
            throw MISSING_BLOCK_POSITION.createWithContext((ImmutableStringReader)reader);
        }
        boolean bl = CoordinateArgument.isRelative(reader);
        double d = reader.canRead() && reader.peek() != ' ' ? (bl ? reader.readDouble() : (double)reader.readInt()) : 0.0;
        return new CoordinateArgument(bl, d);
    }

    public static boolean isRelative(StringReader reader) {
        boolean bl;
        if (reader.peek() == '~') {
            bl = true;
            reader.skip();
        } else {
            bl = false;
        }
        return bl;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CoordinateArgument)) {
            return false;
        }
        CoordinateArgument coordinateArgument = (CoordinateArgument)o;
        if (this.relative != coordinateArgument.relative) {
            return false;
        }
        return Double.compare(coordinateArgument.value, this.value) == 0;
    }

    public int hashCode() {
        int i = this.relative ? 1 : 0;
        long l = Double.doubleToLongBits(this.value);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public boolean isRelative() {
        return this.relative;
    }
}

