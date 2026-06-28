/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.ListBuilder
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  com.mojang.serialization.RecordBuilder$AbstractUniversalBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.AbstractListBuilder;

public class NullOps
implements DynamicOps<Unit> {
    final static public NullOps INSTANCE = new NullOps();

    private NullOps() {
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, Unit unit) {
        return (U)dynamicOps.empty();
    }

    public Unit net_minecraft_util_Unit_empty() {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_emptyMap() {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_emptyList() {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createNumeric(Number number) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createByte(byte b) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createShort(short s) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createInt(int i) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createLong(long l) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createFloat(float f) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createDouble(double d) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createBoolean(boolean bl) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createString(String string) {
        return Unit.INSTANCE;
    }

    public DataResult<Number> getNumberValue(Unit unit) {
        return DataResult.error(() -> "Not a number");
    }

    public DataResult<Boolean> getBooleanValue(Unit unit) {
        return DataResult.error(() -> "Not a boolean");
    }

    public DataResult<String> getStringValue(Unit unit) {
        return DataResult.error(() -> "Not a string");
    }

    public DataResult<Unit> mergeToList(Unit unit, Unit unit2) {
        return DataResult.success((Object)((Object)Unit.INSTANCE));
    }

    public DataResult<Unit> mergeToList(Unit unit, List<Unit> list) {
        return DataResult.success((Object)((Object)Unit.INSTANCE));
    }

    public DataResult<Unit> mergeToMap(Unit unit, Unit unit2, Unit unit3) {
        return DataResult.success((Object)((Object)Unit.INSTANCE));
    }

    public DataResult<Unit> mergeToMap(Unit unit, Map<Unit, Unit> map) {
        return DataResult.success((Object)((Object)Unit.INSTANCE));
    }

    public DataResult<Unit> mergeToMap(Unit unit, MapLike<Unit> mapLike) {
        return DataResult.success((Object)((Object)Unit.INSTANCE));
    }

    public DataResult<Stream<Pair<Unit, Unit>>> getMapValues(Unit unit) {
        return DataResult.error(() -> "Not a map");
    }

    public DataResult<Consumer<BiConsumer<Unit, Unit>>> getMapEntries(Unit unit) {
        return DataResult.error(() -> "Not a map");
    }

    public DataResult<MapLike<Unit>> getMap(Unit unit) {
        return DataResult.error(() -> "Not a map");
    }

    public DataResult<Stream<Unit>> getStream(Unit unit) {
        return DataResult.error(() -> "Not a list");
    }

    public DataResult<Consumer<Consumer<Unit>>> getList(Unit unit) {
        return DataResult.error(() -> "Not a list");
    }

    public DataResult<ByteBuffer> getByteBuffer(Unit unit) {
        return DataResult.error(() -> "Not a byte list");
    }

    public DataResult<IntStream> getIntStream(Unit unit) {
        return DataResult.error(() -> "Not an int list");
    }

    public DataResult<LongStream> getLongStream(Unit unit) {
        return DataResult.error(() -> "Not a long list");
    }

    public Unit net_minecraft_util_Unit_createMap(Stream<Pair<Unit, Unit>> stream) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createMap(Map<Unit, Unit> map) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createList(Stream<Unit> stream) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createByteList(ByteBuffer byteBuffer) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createIntList(IntStream intStream) {
        return Unit.INSTANCE;
    }

    public Unit net_minecraft_util_Unit_createLongList(LongStream longStream) {
        return Unit.INSTANCE;
    }

    public Unit remove(Unit unit, String string) {
        return unit;
    }

    public RecordBuilder<Unit> mapBuilder() {
        return new NullMapBuilder(this);
    }

    public ListBuilder<Unit> listBuilder() {
        return new NullListBuilder(this);
    }

    public String toString() {
        return "Null";
    }

    public Object remove(Object input, String key) {
        return this.remove((Unit)((Object)input), key);
    }

    public Object java_lang_Object_createLongList(LongStream stream) {
        return this.net_minecraft_util_Unit_createLongList(stream);
    }

    public DataResult getLongStream(Object input) {
        return this.getLongStream((Unit)((Object)input));
    }

    public Object java_lang_Object_createIntList(IntStream stream) {
        return this.net_minecraft_util_Unit_createIntList(stream);
    }

    public DataResult getIntStream(Object input) {
        return this.getIntStream((Unit)((Object)input));
    }

    public Object java_lang_Object_createByteList(ByteBuffer buf) {
        return this.net_minecraft_util_Unit_createByteList(buf);
    }

    public DataResult getByteBuffer(Object input) {
        return this.getByteBuffer((Unit)((Object)input));
    }

    public Object java_lang_Object_createList(Stream list) {
        return this.net_minecraft_util_Unit_createList((Stream<Unit>)list);
    }

    public DataResult getList(Object input) {
        return this.getList((Unit)((Object)input));
    }

    public DataResult getStream(Object input) {
        return this.getStream((Unit)((Object)input));
    }

    public Object java_lang_Object_createMap(Map map) {
        return this.net_minecraft_util_Unit_createMap((Map<Unit, Unit>)map);
    }

    public DataResult getMap(Object input) {
        return this.getMap((Unit)((Object)input));
    }

    public Object java_lang_Object_createMap(Stream map) {
        return this.net_minecraft_util_Unit_createMap((Stream<Pair<Unit, Unit>>)map);
    }

    public DataResult getMapEntries(Object input) {
        return this.getMapEntries((Unit)((Object)input));
    }

    public DataResult getMapValues(Object input) {
        return this.getMapValues((Unit)((Object)input));
    }

    public DataResult mergeToMap(Object map, MapLike values) {
        return this.mergeToMap((Unit)((Object)map), (MapLike<Unit>)values);
    }

    public DataResult mergeToMap(Object map, Map values) {
        return this.mergeToMap((Unit)((Object)map), (Map<Unit, Unit>)values);
    }

    public DataResult mergeToMap(Object map, Object key, Object value) {
        return this.mergeToMap((Unit)((Object)map), (Unit)((Object)key), (Unit)((Object)value));
    }

    public DataResult mergeToList(Object list, List values) {
        return this.mergeToList((Unit)((Object)list), (List<Unit>)values);
    }

    public DataResult mergeToList(Object list, Object value) {
        return this.mergeToList((Unit)((Object)list), (Unit)((Object)value));
    }

    public Object java_lang_Object_createString(String string) {
        return this.net_minecraft_util_Unit_createString(string);
    }

    public DataResult getStringValue(Object input) {
        return this.getStringValue((Unit)((Object)input));
    }

    public Object java_lang_Object_createBoolean(boolean bl) {
        return this.net_minecraft_util_Unit_createBoolean(bl);
    }

    public DataResult getBooleanValue(Object input) {
        return this.getBooleanValue((Unit)((Object)input));
    }

    public Object java_lang_Object_createDouble(double d) {
        return this.net_minecraft_util_Unit_createDouble(d);
    }

    public Object java_lang_Object_createFloat(float f) {
        return this.net_minecraft_util_Unit_createFloat(f);
    }

    public Object java_lang_Object_createLong(long l) {
        return this.net_minecraft_util_Unit_createLong(l);
    }

    public Object java_lang_Object_createInt(int i) {
        return this.net_minecraft_util_Unit_createInt(i);
    }

    public Object java_lang_Object_createShort(short s) {
        return this.net_minecraft_util_Unit_createShort(s);
    }

    public Object java_lang_Object_createByte(byte b) {
        return this.net_minecraft_util_Unit_createByte(b);
    }

    public Object java_lang_Object_createNumeric(Number number) {
        return this.net_minecraft_util_Unit_createNumeric(number);
    }

    public DataResult getNumberValue(Object input) {
        return this.getNumberValue((Unit)((Object)input));
    }

    public Object convertTo(DynamicOps ops, Object unit) {
        return this.convertTo(ops, (Unit)((Object)unit));
    }

    public Object java_lang_Object_emptyList() {
        return this.net_minecraft_util_Unit_emptyList();
    }

    public Object java_lang_Object_emptyMap() {
        return this.net_minecraft_util_Unit_emptyMap();
    }

    public Object java_lang_Object_empty() {
        return this.net_minecraft_util_Unit_empty();
    }

    static final class NullMapBuilder
    extends RecordBuilder.AbstractUniversalBuilder<Unit, Unit> {
        public NullMapBuilder(DynamicOps<Unit> ops) {
            super(ops);
        }

        protected Unit net_minecraft_util_Unit_initBuilder() {
            return Unit.INSTANCE;
        }

        protected Unit append(Unit unit, Unit unit2, Unit unit3) {
            return unit3;
        }

        protected DataResult<Unit> build(Unit unit, Unit unit2) {
            return DataResult.success((Object)((Object)unit2));
        }

        protected Object append(Object key, Object value, Object builder) {
            return this.append((Unit)((Object)key), (Unit)((Object)value), (Unit)((Object)builder));
        }

        protected DataResult build(Object builder, Object prefix) {
            return this.build((Unit)((Object)builder), (Unit)((Object)prefix));
        }

        protected Object java_lang_Object_initBuilder() {
            return this.net_minecraft_util_Unit_initBuilder();
        }
    }

    static final class NullListBuilder
    extends AbstractListBuilder<Unit, Unit> {
        public NullListBuilder(DynamicOps<Unit> dynamicOps) {
            super(dynamicOps);
        }

        @Override
        protected Unit net_minecraft_util_Unit_initBuilder() {
            return Unit.INSTANCE;
        }

        @Override
        protected Unit add(Unit unit, Unit unit2) {
            return unit;
        }

        @Override
        protected DataResult<Unit> build(Unit unit, Unit unit2) {
            return DataResult.success((Object)((Object)unit));
        }

        @Override
        protected Object java_lang_Object_initBuilder() {
            return this.net_minecraft_util_Unit_initBuilder();
        }
    }
}

