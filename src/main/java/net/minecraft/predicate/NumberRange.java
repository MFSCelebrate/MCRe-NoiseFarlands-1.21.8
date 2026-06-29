/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;

public interface NumberRange<T extends Number> {
    final static public SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType((Message)Text.translatable("argument.range.empty"));
    final static public SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType((Message)Text.translatable("argument.range.swapped"));

    public Optional<T> min();

    public Optional<T> max();

    default public boolean isDummy() {
        return this.min().isEmpty() && this.max().isEmpty();
    }

    default public Optional<T> getConstantValue() {
        Optional<T> optional2;
        Optional<T> optional = this.min();
        return optional.equals(optional2 = this.max()) ? optional : Optional.empty();
    }

    public static <T extends Number, R extends NumberRange<T>> Codec<R> createCodec(Codec<T> valueCodec, Factory<T, R> rangeFactory) {
        Codec codec = RecordCodecBuilder.create(instance -> instance.group((App)valueCodec.optionalFieldOf("min").forGetter(NumberRange::min), (App)valueCodec.optionalFieldOf("max").forGetter(NumberRange::max)).apply((Applicative)instance, rangeFactory::create));
        return Codec.either((Codec)codec, valueCodec).xmap(either -> (NumberRange)either.map(range -> range, value -> rangeFactory.create(Optional.of(value), Optional.of(value))), range -> {
            Optional optional = range.getConstantValue();
            return optional.isPresent() ? Either.right((Object)((Number)optional.get())) : Either.left((Object)range);
        });
    }

    public static <B extends ByteBuf, T extends Number, R extends NumberRange<T>> PacketCodec<B, R> createPacketCodec(final PacketCodec<B, T> valuePacketCodec, final Factory<T, R> rangeFactory) {
        return new PacketCodec<B, R>(){
            final static private int field_56292 = 1;
            final static public int field_56289 = 2;

            @Override
            public R decode(B byteBuf) {
                byte b = byteBuf.readByte();
                Optional optional = (b & 1) != 0 ? Optional.of((Number)valuePacketCodec.decode(byteBuf)) : Optional.empty();
                Optional optional2 = (b & 2) != 0 ? Optional.of((Number)valuePacketCodec.decode(byteBuf)) : Optional.empty();
                return rangeFactory.create(optional, optional2);
            }

            @Override
            public void encode(B byteBuf, R numberRange) {
                Optional<Number> optional = numberRange.min();
                Optional<Number> optional2 = numberRange.max();
                byteBuf.writeByte((optional.isPresent() ? 1 : 0) | (optional2.isPresent() ? 2 : 0));
                optional.ifPresent(number -> valuePacketCodec.encode(byteBuf, number));
                optional2.ifPresent(number -> valuePacketCodec.encode(byteBuf, number));
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((B)((ByteBuf)object), (R)((NumberRange)object2));
            }

            @Override
            public Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <T extends Number, R extends NumberRange<T>> R parse(StringReader commandReader, CommandFactory<T, R> commandFactory, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier, Function<T, T> mapper) throws CommandSyntaxException {
        if (!commandReader.canRead()) {
            throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)commandReader);
        }
        int i = commandReader.getCursor();
        try {
            Optional<T> optional2;
            Optional<T> optional = NumberRange.fromStringReader(commandReader, converter, exceptionTypeSupplier).map(mapper);
            if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
                commandReader.skip();
                commandReader.skip();
                optional2 = NumberRange.fromStringReader(commandReader, converter, exceptionTypeSupplier).map(mapper);
                if (optional.isEmpty() && optional2.isEmpty()) {
                    throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)commandReader);
                }
            } else {
                optional2 = optional;
            }
            if (optional.isEmpty() && optional2.isEmpty()) {
                throw EXCEPTION_EMPTY.createWithContext((ImmutableStringReader)commandReader);
            }
            return commandFactory.create(commandReader, optional, optional2);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            commandReader.setCursor(i);
            throw new CommandSyntaxException(commandSyntaxException.getType(), commandSyntaxException.getRawMessage(), commandSyntaxException.getInput(), i);
        }
    }

    private static <T extends Number> Optional<T> fromStringReader(StringReader reader, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier) throws CommandSyntaxException {
        int i = reader.getCursor();
        while (reader.canRead() && NumberRange.isNextCharValid(reader)) {
            reader.skip();
        }
        String string = reader.getString().substring(1, reader.getCursor());
        if (string.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of((Number)converter.apply(string));
        }
        catch (NumberFormatException numberFormatException) {
            throw exceptionTypeSupplier.get().createWithContext((ImmutableStringReader)reader, (Object)string);
        }
    }

    private static boolean isNextCharValid(StringReader reader) {
        char c = reader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (c == '.') {
            return !reader.canRead(2) || reader.peek(1) != '.';
        }
        return false;
    }

    @FunctionalInterface
    public static interface Factory<T extends Number, R extends NumberRange<T>> {
        public R create(Optional<T> var1, Optional<T> var2);
    }

    @FunctionalInterface
    public static interface CommandFactory<T extends Number, R extends NumberRange<T>> {
        public R create(StringReader var1, Optional<T> var2, Optional<T> var3) throws CommandSyntaxException;
    }

    public record DoubleRange(Optional<Double> min, Optional<Double> max, Optional<Double> squaredMin, Optional<Double> squaredMax) implements NumberRange<Double>
    {
        final static public DoubleRange ANY = new DoubleRange(Optional.empty(), Optional.empty());
        final static public Codec<DoubleRange> CODEC = NumberRange.createCodec(Codec.DOUBLE, DoubleRange::new);
        final static public PacketCodec<ByteBuf, DoubleRange> PACKET_CODEC = NumberRange.createPacketCodec(PacketCodecs.DOUBLE, DoubleRange::new);

        private DoubleRange(Optional<Double> min, Optional<Double> max) {
            this(min, max, DoubleRange.square(min), DoubleRange.square(max));
        }

        private static DoubleRange create(StringReader reader, Optional<Double> min, Optional<Double> max) throws CommandSyntaxException {
            if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
                throw EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)reader);
            }
            return new DoubleRange(min, max);
        }

        private static Optional<Double> square(Optional<Double> value) {
            return value.map(d -> d * d);
        }

        public static DoubleRange exactly(double value) {
            return new DoubleRange(Optional.of(value), Optional.of(value));
        }

        public static DoubleRange between(double min, double max) {
            return new DoubleRange(Optional.of(min), Optional.of(max));
        }

        public static DoubleRange atLeast(double value) {
            return new DoubleRange(Optional.of(value), Optional.empty());
        }

        public static DoubleRange atMost(double value) {
            return new DoubleRange(Optional.empty(), Optional.of(value));
        }

        public boolean test(double value) {
            if (this.min.isPresent() && this.min.get() > value) {
                return false;
            }
            return this.max.isEmpty() || !(this.max.get() < value);
        }

        public boolean testSqrt(double value) {
            if (this.squaredMin.isPresent() && this.squaredMin.get() > value) {
                return false;
            }
            return this.squaredMax.isEmpty() || !(this.squaredMax.get() < value);
        }

        public static DoubleRange parse(StringReader reader) throws CommandSyntaxException {
            return DoubleRange.parse(reader, value -> value);
        }

        public static DoubleRange parse(StringReader reader, Function<Double, Double> mapper) throws CommandSyntaxException {
            return NumberRange.parse(reader, DoubleRange::create, Double::parseDouble, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidDouble(), mapper);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DoubleRange.class, "min;max;minSq;maxSq", "min", "max", "squaredMin", "squaredMax"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DoubleRange.class, "min;max;minSq;maxSq", "min", "max", "squaredMin", "squaredMax"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DoubleRange.class, "min;max;minSq;maxSq", "min", "max", "squaredMin", "squaredMax"}, this, object);
        }
    }

    public record IntRange(Optional<Integer> min, Optional<Integer> max, Optional<Long> minSquared, Optional<Long> maxSquared) implements NumberRange<Integer>
    {
        final static public IntRange ANY = new IntRange(Optional.empty(), Optional.empty());
        final static public Codec<IntRange> CODEC = NumberRange.createCodec(Codec.INT, IntRange::new);
        final static public PacketCodec<ByteBuf, IntRange> PACKET_CODEC = NumberRange.createPacketCodec(PacketCodecs.INTEGER, IntRange::new);

        private IntRange(Optional<Integer> min, Optional<Integer> max) {
            this(min, max, min.map(i -> i.longValue() * i.longValue()), IntRange.square(max));
        }

        private static IntRange parse(StringReader reader, Optional<Integer> min, Optional<Integer> max) throws CommandSyntaxException {
            if (min.isPresent() && max.isPresent() && min.get() > max.get()) {
                throw EXCEPTION_SWAPPED.createWithContext((ImmutableStringReader)reader);
            }
            return new IntRange(min, max);
        }

        private static Optional<Long> square(Optional<Integer> value) {
            return value.map(i -> i.longValue() * i.longValue());
        }

        public static IntRange exactly(int value) {
            return new IntRange(Optional.of(value), Optional.of(value));
        }

        public static IntRange between(int min, int max) {
            return new IntRange(Optional.of(min), Optional.of(max));
        }

        public static IntRange atLeast(int value) {
            return new IntRange(Optional.of(value), Optional.empty());
        }

        public static IntRange atMost(int value) {
            return new IntRange(Optional.empty(), Optional.of(value));
        }

        public boolean test(int value) {
            if (this.min.isPresent() && this.min.get() > value) {
                return false;
            }
            return this.max.isEmpty() || this.max.get() >= value;
        }

        public boolean testSqrt(long value) {
            if (this.minSquared.isPresent() && this.minSquared.get() > value) {
                return false;
            }
            return this.maxSquared.isEmpty() || this.maxSquared.get() >= value;
        }

        public static IntRange parse(StringReader reader) throws CommandSyntaxException {
            return IntRange.fromStringReader(reader, value -> value);
        }

        public static IntRange fromStringReader(StringReader reader, Function<Integer, Integer> converter) throws CommandSyntaxException {
            return NumberRange.parse(reader, IntRange::parse, Integer::parseInt, () -> ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS).readerInvalidInt(), converter);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{IntRange.class, "min;max;minSq;maxSq", "min", "max", "minSquared", "maxSquared"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{IntRange.class, "min;max;minSq;maxSq", "min", "max", "minSquared", "maxSquared"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{IntRange.class, "min;max;minSq;maxSq", "min", "max", "minSquared", "maxSquared"}, this, object);
        }
    }
}

