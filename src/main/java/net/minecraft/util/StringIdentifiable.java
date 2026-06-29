/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public interface StringIdentifiable {
    final static public int CACHED_MAP_THRESHOLD = 16;

    public String asString();

    public static <E extends Enum<E>> EnumCodec<E> createCodec(Supplier<E[]> enumValues) {
        return StringIdentifiable.createCodec(enumValues, id -> id);
    }

    public static <E extends Enum<E>> EnumCodec<E> createCodec(Supplier<E[]> enumValues, Function<String, String> valueNameTransformer) {
        Enum[] enums = (Enum[])enumValues.get();
        Function function = StringIdentifiable.createMapper((StringIdentifiable[])enums, valueNameTransformer);
        return new EnumCodec(enums, function);
    }

    public static <T extends StringIdentifiable> Codec<T> createBasicCodec(Supplier<T[]> values) {
        StringIdentifiable[] stringIdentifiables = (StringIdentifiable[])values.get();
        Function function = StringIdentifiable.createMapper((StringIdentifiable[])stringIdentifiables, (T valueName) -> valueName);
        ToIntFunction<StringIdentifiable> toIntFunction = Util.lastIndexGetter(Arrays.asList(stringIdentifiables));
        return new BasicCodec(stringIdentifiables, function, toIntFunction);
    }

    public static <T extends StringIdentifiable> Function<String, T> createMapper(T[] values, Function<String, String> valueNameTransformer) {
        if (values.length > 16) {
            Map<String, StringIdentifiable> map = Arrays.stream(values).collect(Collectors.toMap(value -> (String)valueNameTransformer.apply(value.asString()), value -> value));
            return name -> name == null ? null : (StringIdentifiable)map.get(name);
        }
        return name -> {
            for (StringIdentifiable stringIdentifiable : values) {
                if (!((String)valueNameTransformer.apply(stringIdentifiable.asString())).equals(name)) continue;
                return stringIdentifiable;
            }
            return null;
        };
    }

    public static Keyable toKeyable(final StringIdentifiable[] values) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Arrays.stream(values).map(StringIdentifiable::asString).map(arg_0 -> ops.createString(arg_0));
            }
        };
    }

    public static class EnumCodec<E extends Enum<E>>
    extends BasicCodec<E> {
        final private Function<String, E> idToIdentifiable;

        public EnumCodec(E[] values, Function<String, E> idToIdentifiable) {
            super(values, idToIdentifiable, enum_ -> ((Enum)enum_).ordinal());
            this.idToIdentifiable = idToIdentifiable;
        }

        @Nullable
        public E byId(@Nullable String id) {
            return (E)((Enum)this.idToIdentifiable.apply(id));
        }

        public E byId(@Nullable String id, E fallback) {
            return (E)((Enum)Objects.requireNonNullElse(this.byId(id), fallback));
        }

        public E byId(@Nullable String id, Supplier<? extends E> fallbackSupplier) {
            return (E)((Enum)Objects.requireNonNullElseGet(this.byId(id), fallbackSupplier));
        }
    }

    public static class BasicCodec<S extends StringIdentifiable>
    implements Codec<S> {
        final private Codec<S> codec;

        public BasicCodec(S[] values, Function<String, S> idToIdentifiable, ToIntFunction<S> identifiableToOrdinal) {
            this.codec = Codecs.orCompressed(Codec.stringResolver(StringIdentifiable::asString, idToIdentifiable), Codecs.rawIdChecked(identifiableToOrdinal, ordinal -> ordinal >= 0 && ordinal < values.length ? values[ordinal] : null, -1));
        }

        public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> ops, T input) {
            return this.codec.decode(ops, input);
        }

        public <T> DataResult<T> encode(S stringIdentifiable, DynamicOps<T> dynamicOps, T object) {
            return this.codec.encode(stringIdentifiable, dynamicOps, object);
        }

        public DataResult encode(Object input, DynamicOps ops, Object prefix) {
            return this.encode((S)((StringIdentifiable)input), (DynamicOps<T>)ops, (T)prefix);
        }
    }
}

