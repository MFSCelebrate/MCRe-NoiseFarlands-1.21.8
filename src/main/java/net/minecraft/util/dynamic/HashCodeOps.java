/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.HashFunction
 *  com.google.common.hash.Hasher
 *  com.google.common.hash.Hashing
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.ListBuilder
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.RecordBuilder$AbstractUniversalBuilder
 */
package net.minecraft.util.dynamic;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.lang.runtime.SwitchBootstraps;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.AbstractListBuilder;

public class HashCodeOps
implements DynamicOps<HashCode> {
    final static private byte field_58094 = 1;
    final static private byte field_58095 = 2;
    final static private byte field_58096 = 3;
    final static private byte field_58097 = 4;
    final static private byte field_58098 = 5;
    final static private byte field_58099 = 6;
    final static private byte field_58100 = 7;
    final static private byte field_58101 = 8;
    final static private byte field_58102 = 9;
    final static private byte field_58103 = 10;
    final static private byte field_58104 = 11;
    final static private byte field_58105 = 12;
    final static private byte field_58106 = 13;
    final static private byte field_58107 = 14;
    final static private byte field_58108 = 15;
    final static private byte field_58109 = 16;
    final static private byte field_58110 = 17;
    final static private byte field_58111 = 18;
    final static private byte field_58112 = 19;
    final static private byte[] emptyByteArray = new byte[]{1};
    final static private byte[] falseByteArray = new byte[]{13, 0};
    final static private byte[] trueByteArray = new byte[]{13, 1};
    final static public byte[] emptyMapByteArray = new byte[]{2, 3};
    final static public byte[] emptyListByteArray = new byte[]{4, 5};
    final static private DataResult<Object> ERROR = DataResult.error(() -> "Unsupported operation");
    final static private Comparator<HashCode> HASH_CODE_COMPARATOR = Comparator.comparingLong(HashCode::padToLong);
    final static private Comparator<Map.Entry<HashCode, HashCode>> ENTRY_COMPARATOR = Map.Entry.comparingByKey(HASH_CODE_COMPARATOR).thenComparing(Map.Entry.comparingByValue(HASH_CODE_COMPARATOR));
    final static private Comparator<Pair<HashCode, HashCode>> PAIR_COMPARATOR = Comparator.comparing(Pair::getFirst, HASH_CODE_COMPARATOR).thenComparing(Pair::getSecond, HASH_CODE_COMPARATOR);
    final static public HashCodeOps INSTANCE = new HashCodeOps(Hashing.crc32c());
    final HashFunction function;
    final HashCode empty;
    final private HashCode emptyMap;
    final private HashCode emptyList;
    final private HashCode hashTrue;
    final private HashCode hashFalse;

    public HashCodeOps(HashFunction function) {
        this.function = function;
        this.empty = function.hashBytes(emptyByteArray);
        this.emptyMap = function.hashBytes(emptyMapByteArray);
        this.emptyList = function.hashBytes(emptyListByteArray);
        this.hashFalse = function.hashBytes(falseByteArray);
        this.hashTrue = function.hashBytes(trueByteArray);
    }

    public HashCode com_google_common_hash_HashCode_empty() {
        return this.empty;
    }

    public HashCode com_google_common_hash_HashCode_emptyMap() {
        return this.emptyMap;
    }

    public HashCode com_google_common_hash_HashCode_emptyList() {
        return this.emptyList;
    }

    public HashCode com_google_common_hash_HashCode_createNumeric(Number number) {
        Number number2 = number;
        Objects.requireNonNull(number2);
        Number number3 = number2;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{Byte.class, Short.class, Integer.class, Long.class, Double.class, Float.class}, (Object)number3, n)) {
            case 0 -> {
                Byte byte_ = (Byte)number3;
                yield this.com_google_common_hash_HashCode_createByte(byte_);
            }
            case 1 -> {
                Short short_ = (Short)number3;
                yield this.com_google_common_hash_HashCode_createShort(short_);
            }
            case 2 -> {
                Integer integer = (Integer)number3;
                yield this.com_google_common_hash_HashCode_createInt(integer);
            }
            case 3 -> {
                Long long_ = (Long)number3;
                yield this.com_google_common_hash_HashCode_createLong(long_);
            }
            case 4 -> {
                Double double_ = (Double)number3;
                yield this.com_google_common_hash_HashCode_createDouble(double_);
            }
            case 5 -> {
                Float float_ = (Float)number3;
                yield this.com_google_common_hash_HashCode_createFloat(float_.floatValue());
            }
            default -> this.com_google_common_hash_HashCode_createDouble(number.doubleValue());
        };
    }

    public HashCode com_google_common_hash_HashCode_createByte(byte b) {
        return this.function.newHasher(2).putByte(6).putByte(b).hash();
    }

    public HashCode com_google_common_hash_HashCode_createShort(short s) {
        return this.function.newHasher(3).putByte(7).putShort(s).hash();
    }

    public HashCode com_google_common_hash_HashCode_createInt(int i) {
        return this.function.newHasher(5).putByte(8).putInt(i).hash();
    }

    public HashCode com_google_common_hash_HashCode_createLong(long l) {
        return this.function.newHasher(9).putByte(9).putLong(l).hash();
    }

    public HashCode com_google_common_hash_HashCode_createFloat(float f) {
        return this.function.newHasher(5).putByte(10).putFloat(f).hash();
    }

    public HashCode com_google_common_hash_HashCode_createDouble(double d) {
        return this.function.newHasher(9).putByte(11).putDouble(d).hash();
    }

    public HashCode com_google_common_hash_HashCode_createString(String string) {
        return this.function.newHasher().putByte(12).putInt(string.length()).putUnencodedChars((CharSequence)string).hash();
    }

    public HashCode com_google_common_hash_HashCode_createBoolean(boolean bl) {
        return bl ? this.hashTrue : this.hashFalse;
    }

    private static Hasher hash(Hasher hasher, Map<HashCode, HashCode> map) {
        hasher.putByte(2);
        map.entrySet().stream().sorted(ENTRY_COMPARATOR).forEach(entry -> hasher.putBytes(((HashCode)entry.getKey()).asBytes()).putBytes(((HashCode)entry.getValue()).asBytes()));
        hasher.putByte(3);
        return hasher;
    }

    static Hasher hash(Hasher hasher, Stream<Pair<HashCode, HashCode>> pairs) {
        hasher.putByte(2);
        pairs.sorted(PAIR_COMPARATOR).forEach(pair -> hasher.putBytes(((HashCode)pair.getFirst()).asBytes()).putBytes(((HashCode)pair.getSecond()).asBytes()));
        hasher.putByte(3);
        return hasher;
    }

    public HashCode com_google_common_hash_HashCode_createMap(Stream<Pair<HashCode, HashCode>> stream) {
        return HashCodeOps.hash(this.function.newHasher(), stream).hash();
    }

    public HashCode com_google_common_hash_HashCode_createMap(Map<HashCode, HashCode> map) {
        return HashCodeOps.hash(this.function.newHasher(), map).hash();
    }

    public HashCode com_google_common_hash_HashCode_createList(Stream<HashCode> stream) {
        Hasher hasher = this.function.newHasher();
        hasher.putByte(4);
        stream.forEach(hashCode -> hasher.putBytes(hashCode.asBytes()));
        hasher.putByte(5);
        return hasher.hash();
    }

    public HashCode com_google_common_hash_HashCode_createByteList(ByteBuffer byteBuffer) {
        Hasher hasher = this.function.newHasher();
        hasher.putByte(14);
        hasher.putBytes(byteBuffer);
        hasher.putByte(15);
        return hasher.hash();
    }

    public HashCode com_google_common_hash_HashCode_createIntList(IntStream intStream) {
        Hasher hasher = this.function.newHasher();
        hasher.putByte(16);
        intStream.forEach(arg_0 -> ((Hasher)hasher).putInt(arg_0));
        hasher.putByte(17);
        return hasher.hash();
    }

    public HashCode com_google_common_hash_HashCode_createLongList(LongStream longStream) {
        Hasher hasher = this.function.newHasher();
        hasher.putByte(18);
        longStream.forEach(arg_0 -> ((Hasher)hasher).putLong(arg_0));
        hasher.putByte(19);
        return hasher.hash();
    }

    public HashCode remove(HashCode hashCode, String string) {
        return hashCode;
    }

    public RecordBuilder<HashCode> mapBuilder() {
        return new Builder();
    }

    public com.mojang.serialization.ListBuilder<HashCode> listBuilder() {
        return new ListBuilder();
    }

    public String toString() {
        return "Hash " + String.valueOf(this.function);
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, HashCode hashCode) {
        throw new UnsupportedOperationException("Can't convert from this type");
    }

    public Number getNumberValue(HashCode hashCode, Number number) {
        return number;
    }

    public HashCode set(HashCode hashCode, String string, HashCode hashCode2) {
        return hashCode;
    }

    public HashCode update(HashCode hashCode, String string, Function<HashCode, HashCode> function) {
        return hashCode;
    }

    public HashCode updateGeneric(HashCode hashCode, HashCode hashCode2, Function<HashCode, HashCode> function) {
        return hashCode;
    }

    private static <T> DataResult<T> error() {
        return ERROR;
    }

    public DataResult<HashCode> get(HashCode hashCode, String string) {
        return HashCodeOps.error();
    }

    public DataResult<HashCode> getGeneric(HashCode hashCode, HashCode hashCode2) {
        return HashCodeOps.error();
    }

    public DataResult<Number> getNumberValue(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<Boolean> getBooleanValue(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<String> getStringValue(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<HashCode> mergeToList(HashCode hashCode, HashCode hashCode2) {
        return HashCodeOps.error();
    }

    public DataResult<HashCode> mergeToList(HashCode hashCode, List<HashCode> list) {
        return HashCodeOps.error();
    }

    public DataResult<HashCode> mergeToMap(HashCode hashCode, HashCode hashCode2, HashCode hashCode3) {
        return HashCodeOps.error();
    }

    public DataResult<HashCode> mergeToMap(HashCode hashCode, Map<HashCode, HashCode> map) {
        return HashCodeOps.error();
    }

    public DataResult<HashCode> mergeToMap(HashCode hashCode, MapLike<HashCode> mapLike) {
        return HashCodeOps.error();
    }

    public DataResult<Stream<Pair<HashCode, HashCode>>> getMapValues(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<Consumer<BiConsumer<HashCode, HashCode>>> getMapEntries(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<Stream<HashCode>> getStream(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<Consumer<Consumer<HashCode>>> getList(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<MapLike<HashCode>> getMap(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<ByteBuffer> getByteBuffer(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<IntStream> getIntStream(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public DataResult<LongStream> getLongStream(HashCode hashCode) {
        return HashCodeOps.error();
    }

    public Object updateGeneric(Object object, Object object2, Function function) {
        return this.updateGeneric((HashCode)object, (HashCode)object2, (Function<HashCode, HashCode>)function);
    }

    public Object update(Object object, String string, Function function) {
        return this.update((HashCode)object, string, (Function<HashCode, HashCode>)function);
    }

    public Object set(Object object, String string, Object object2) {
        return this.set((HashCode)object, string, (HashCode)object2);
    }

    public DataResult getGeneric(Object object, Object object2) {
        return this.getGeneric((HashCode)object, (HashCode)object2);
    }

    public DataResult get(Object object, String string) {
        return this.get((HashCode)object, string);
    }

    public Object remove(Object object, String string) {
        return this.remove((HashCode)object, string);
    }

    public Object java_lang_Object_createLongList(LongStream longStream) {
        return this.com_google_common_hash_HashCode_createLongList(longStream);
    }

    public DataResult getLongStream(Object object) {
        return this.getLongStream((HashCode)object);
    }

    public Object java_lang_Object_createIntList(IntStream intStream) {
        return this.com_google_common_hash_HashCode_createIntList(intStream);
    }

    public DataResult getIntStream(Object object) {
        return this.getIntStream((HashCode)object);
    }

    public Object java_lang_Object_createByteList(ByteBuffer byteBuffer) {
        return this.com_google_common_hash_HashCode_createByteList(byteBuffer);
    }

    public DataResult getByteBuffer(Object object) {
        return this.getByteBuffer((HashCode)object);
    }

    public Object java_lang_Object_createList(Stream stream) {
        return this.com_google_common_hash_HashCode_createList((Stream<HashCode>)stream);
    }

    public DataResult getList(Object object) {
        return this.getList((HashCode)object);
    }

    public DataResult getStream(Object object) {
        return this.getStream((HashCode)object);
    }

    public Object java_lang_Object_createMap(Map map) {
        return this.com_google_common_hash_HashCode_createMap((Map<HashCode, HashCode>)map);
    }

    public DataResult getMap(Object object) {
        return this.getMap((HashCode)object);
    }

    public Object java_lang_Object_createMap(Stream stream) {
        return this.com_google_common_hash_HashCode_createMap((Stream<Pair<HashCode, HashCode>>)stream);
    }

    public DataResult getMapEntries(Object object) {
        return this.getMapEntries((HashCode)object);
    }

    public DataResult getMapValues(Object object) {
        return this.getMapValues((HashCode)object);
    }

    public DataResult mergeToMap(Object object, MapLike mapLike) {
        return this.mergeToMap((HashCode)object, (MapLike<HashCode>)mapLike);
    }

    public DataResult mergeToMap(Object object, Map map) {
        return this.mergeToMap((HashCode)object, (Map<HashCode, HashCode>)map);
    }

    public DataResult mergeToMap(Object object, Object object2, Object object3) {
        return this.mergeToMap((HashCode)object, (HashCode)object2, (HashCode)object3);
    }

    public DataResult mergeToList(Object object, List list) {
        return this.mergeToList((HashCode)object, (List<HashCode>)list);
    }

    public DataResult mergeToList(Object object, Object object2) {
        return this.mergeToList((HashCode)object, (HashCode)object2);
    }

    public Object java_lang_Object_createString(String string) {
        return this.com_google_common_hash_HashCode_createString(string);
    }

    public DataResult getStringValue(Object object) {
        return this.getStringValue((HashCode)object);
    }

    public Object java_lang_Object_createBoolean(boolean bl) {
        return this.com_google_common_hash_HashCode_createBoolean(bl);
    }

    public DataResult getBooleanValue(Object object) {
        return this.getBooleanValue((HashCode)object);
    }

    public Object java_lang_Object_createDouble(double d) {
        return this.com_google_common_hash_HashCode_createDouble(d);
    }

    public Object java_lang_Object_createFloat(float f) {
        return this.com_google_common_hash_HashCode_createFloat(f);
    }

    public Object java_lang_Object_createLong(long l) {
        return this.com_google_common_hash_HashCode_createLong(l);
    }

    public Object java_lang_Object_createInt(int i) {
        return this.com_google_common_hash_HashCode_createInt(i);
    }

    public Object java_lang_Object_createShort(short s) {
        return this.com_google_common_hash_HashCode_createShort(s);
    }

    public Object java_lang_Object_createByte(byte b) {
        return this.com_google_common_hash_HashCode_createByte(b);
    }

    public Object java_lang_Object_createNumeric(Number number) {
        return this.com_google_common_hash_HashCode_createNumeric(number);
    }

    public Number getNumberValue(Object object, Number number) {
        return this.getNumberValue((HashCode)object, number);
    }

    public DataResult getNumberValue(Object object) {
        return this.getNumberValue((HashCode)object);
    }

    public Object convertTo(DynamicOps dynamicOps, Object object) {
        return this.convertTo(dynamicOps, (HashCode)object);
    }

    public Object java_lang_Object_emptyList() {
        return this.com_google_common_hash_HashCode_emptyList();
    }

    public Object java_lang_Object_emptyMap() {
        return this.com_google_common_hash_HashCode_emptyMap();
    }

    public Object java_lang_Object_empty() {
        return this.com_google_common_hash_HashCode_empty();
    }

    final class Builder
    extends RecordBuilder.AbstractUniversalBuilder<HashCode, List<Pair<HashCode, HashCode>>> {
        public Builder() {
            super((DynamicOps)HashCodeOps.this);
        }

        protected List<Pair<HashCode, HashCode>> initBuilder() {
            return new ArrayList<Pair<HashCode, HashCode>>();
        }

        protected List<Pair<HashCode, HashCode>> append(HashCode hashCode, HashCode hashCode2, List<Pair<HashCode, HashCode>> list) {
            list.add((Pair<HashCode, HashCode>)Pair.of((Object)hashCode, (Object)hashCode2));
            return list;
        }

        protected DataResult<HashCode> build(List<Pair<HashCode, HashCode>> list, HashCode hashCode) {
            assert (hashCode.equals((Object)HashCodeOps.this.com_google_common_hash_HashCode_empty()));
            return DataResult.success((Object)HashCodeOps.hash(HashCodeOps.this.function.newHasher(), list.stream()).hash());
        }

        protected Object append(Object object, Object object2, Object object3) {
            return this.append((HashCode)object, (HashCode)object2, (List)object3);
        }

        protected DataResult build(Object object, Object object2) {
            return this.build((List)object, (HashCode)object2);
        }

        protected Object initBuilder() {
            return this.initBuilder();
        }
    }

    class ListBuilder
    extends AbstractListBuilder<HashCode, Hasher> {
        public ListBuilder() {
            super(HashCodeOps.this);
        }

        @Override
        protected Hasher com_google_common_hash_Hasher_initBuilder() {
            return HashCodeOps.this.function.newHasher().putByte(4);
        }

        @Override
        protected Hasher add(Hasher hasher, HashCode hashCode) {
            return hasher.putBytes(hashCode.asBytes());
        }

        @Override
        protected DataResult<HashCode> build(Hasher hasher, HashCode hashCode) {
            assert (hashCode.equals((Object)HashCodeOps.this.empty));
            hasher.putByte(5);
            return DataResult.success((Object)hasher.hash());
        }

        @Override
        protected Object java_lang_Object_initBuilder() {
            return this.com_google_common_hash_Hasher_initBuilder();
        }
    }
}

