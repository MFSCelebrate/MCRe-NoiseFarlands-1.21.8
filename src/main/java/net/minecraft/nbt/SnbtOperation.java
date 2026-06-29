/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.serialization.DynamicOps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.nbt;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.ParsingState;
import net.minecraft.util.packrat.Suggestable;
import org.jetbrains.annotations.Nullable;

public class SnbtOperation {
    final static CursorExceptionType<CommandSyntaxException> EXPECTED_STRING_UUID_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType((Message)Text.translatable("snbt.parser.expected_string_uuid")));
    final static CursorExceptionType<CommandSyntaxException> EXPECTED_NUMBER_OR_BOOLEAN_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType((Message)Text.translatable("snbt.parser.expected_number_or_boolean")));
    final static public String TRUE = "true";
    final static public String FALSE = "false";
    final static public Map<Type, Operator> OPERATIONS = Map.of(new Type("bool", 1), new Operator(){

        @Override
        public <T> T apply(DynamicOps<T> ops, List<T> args, ParsingState<StringReader> state) {
            Boolean boolean_ = _1.asBoolean(ops, args.getFirst());
            if (boolean_ == null) {
                state.getErrors().add(state.getCursor(), EXPECTED_NUMBER_OR_BOOLEAN_EXCEPTION);
                return null;
            }
            return (T)ops.createBoolean(boolean_.booleanValue());
        }

        @Nullable
        private static <T> Boolean asBoolean(DynamicOps<T> ops, T value) {
            Optional optional = ops.getBooleanValue(value).result();
            if (optional.isPresent()) {
                return (Boolean)optional.get();
            }
            Optional optional2 = ops.getNumberValue(value).result();
            if (optional2.isPresent()) {
                return ((Number)optional2.get()).doubleValue() != 0.0;
            }
            return null;
        }
    }, new Type("uuid", 1), new Operator(){

        @Override
        public <T> T apply(DynamicOps<T> ops, List<T> args, ParsingState<StringReader> state) {
            UUID uUID;
            Optional optional = ops.getStringValue(args.getFirst()).result();
            if (optional.isEmpty()) {
                state.getErrors().add(state.getCursor(), EXPECTED_STRING_UUID_EXCEPTION);
                return null;
            }
            try {
                uUID = UUID.fromString((String)optional.get());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                state.getErrors().add(state.getCursor(), EXPECTED_STRING_UUID_EXCEPTION);
                return null;
            }
            return (T)ops.createIntList(IntStream.of(Uuids.toIntArray(uUID)));
        }
    });
    final static public Suggestable<StringReader> SUGGESTIONS = new Suggestable<StringReader>(){
        final private Set<String> values = Stream.concat(Stream.of("false", "true"), OPERATIONS.keySet().stream().map(Type::id)).collect(Collectors.toSet());

        @Override
        public Stream<String> possibleValues(ParsingState<StringReader> parsingState) {
            return this.values.stream();
        }
    };

    public record Type(String id, int argCount) {
        @Override
        public String toString() {
            return this.id + "/" + this.argCount;
        }
    }

    public static interface Operator {
        @Nullable
        public <T> T apply(DynamicOps<T> var1, List<T> var2, ParsingState<StringReader> var3);
    }
}

