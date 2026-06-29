/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.nbt;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.text.Text;
import net.minecraft.util.packrat.PackratParser;

public class StringNbtReader<T> {
    final static public SimpleCommandExceptionType TRAILING = new SimpleCommandExceptionType((Message)Text.translatable("argument.nbt.trailing"));
    final static public SimpleCommandExceptionType EXPECTED_COMPOUND = new SimpleCommandExceptionType((Message)Text.translatable("argument.nbt.expected.compound"));
    final static public char COMMA = ',';
    final static public char COLON = ':';
    final static private StringNbtReader<NbtElement> DEFAULT_READER = StringNbtReader.fromOps(NbtOps.INSTANCE);
    final static public Codec<NbtCompound> STRINGIFIED_CODEC = Codec.STRING.comapFlatMap(snbt -> {
        try {
            NbtElement nbtElement = DEFAULT_READER.read((String)snbt);
            if (nbtElement instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)nbtElement;
                return DataResult.success((Object)nbtCompound, (Lifecycle)Lifecycle.stable());
            }
            return DataResult.error(() -> "Expected compound tag, got " + String.valueOf(nbtElement));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return DataResult.error(() -> ((CommandSyntaxException)commandSyntaxException).getMessage());
        }
    }, NbtCompound::toString);
    final static public Codec<NbtCompound> NBT_COMPOUND_CODEC = Codec.withAlternative(STRINGIFIED_CODEC, NbtCompound.CODEC);
    final private DynamicOps<T> ops;
    final private PackratParser<T> parser;

    private StringNbtReader(DynamicOps<T> ops, PackratParser<T> parser) {
        this.ops = ops;
        this.parser = parser;
    }

    public DynamicOps<T> getOps() {
        return this.ops;
    }

    public static <T> StringNbtReader<T> fromOps(DynamicOps<T> ops) {
        return new StringNbtReader<T>(ops, SnbtParsing.createParser(ops));
    }

    private static NbtCompound expectCompound(StringReader reader, NbtElement nbtElement) throws CommandSyntaxException {
        if (nbtElement instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)nbtElement;
            return nbtCompound;
        }
        throw EXPECTED_COMPOUND.createWithContext((ImmutableStringReader)reader);
    }

    public static NbtCompound readCompound(String snbt) throws CommandSyntaxException {
        StringReader stringReader = new StringReader(snbt);
        return StringNbtReader.expectCompound(stringReader, DEFAULT_READER.read(stringReader));
    }

    public T read(String snbt) throws CommandSyntaxException {
        return this.read(new StringReader(snbt));
    }

    public T read(StringReader reader) throws CommandSyntaxException {
        T object = this.parser.parse(reader);
        reader.skipWhitespace();
        if (reader.canRead()) {
            throw TRAILING.createWithContext((ImmutableStringReader)reader);
        }
        return object;
    }

    public T readAsArgument(StringReader reader) throws CommandSyntaxException {
        return this.parser.parse(reader);
    }

    public static NbtCompound readCompoundAsArgument(StringReader reader) throws CommandSyntaxException {
        NbtElement nbtElement = DEFAULT_READER.readAsArgument(reader);
        return StringNbtReader.expectCompound(reader, nbtElement);
    }
}

