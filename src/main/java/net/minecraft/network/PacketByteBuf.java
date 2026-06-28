/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  io.netty.util.ByteProcessor
 *  io.netty.util.ReferenceCounted
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.encoding.VarLongs;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PacketByteBuf
extends ByteBuf {
    final static public int MAX_READ_NBT_SIZE = 0x200000;
    final private ByteBuf parent;
    final static public short DEFAULT_MAX_STRING_LENGTH = Short.MAX_VALUE;
    final static public int MAX_TEXT_LENGTH = 262144;
    final static private int field_39381 = 256;
    final static private int field_39382 = 256;
    final static private int field_39383 = 512;
    final static private Gson GSON = new Gson();

    public PacketByteBuf(ByteBuf parent) {
        this.parent = parent;
    }

    @Deprecated
    public <T> T decode(DynamicOps<NbtElement> ops, Codec<T> codec) {
        return this.decode(ops, codec, NbtSizeTracker.ofUnlimitedBytes());
    }

    @Deprecated
    public <T> T decode(DynamicOps<NbtElement> ops, Codec<T> codec, NbtSizeTracker sizeTracker) {
        NbtElement nbtElement = this.readNbt(sizeTracker);
        return (T)codec.parse(ops, (Object)nbtElement).getOrThrow(error -> new DecoderException("Failed to decode: " + error + " " + String.valueOf(nbtElement)));
    }

    @Deprecated
    public <T> PacketByteBuf encode(DynamicOps<NbtElement> ops, Codec<T> codec, T value) {
        NbtElement nbtElement = (NbtElement)codec.encodeStart(ops, value).getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + String.valueOf(value)));
        this.writeNbt(nbtElement);
        return this;
    }

    public <T> T decodeAsJson(Codec<T> codec) {
        JsonElement jsonElement = LenientJsonParser.parse(this.readString());
        DataResult dataResult = codec.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement);
        return (T)dataResult.getOrThrow(error -> new DecoderException("Failed to decode JSON: " + error));
    }

    public <T> void encodeAsJson(Codec<T> codec, T value) {
        DataResult dataResult = codec.encodeStart((DynamicOps)JsonOps.INSTANCE, value);
        this.writeString(GSON.toJson((JsonElement)dataResult.getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + String.valueOf(value)))));
    }

    public static <T> IntFunction<T> getMaxValidator(IntFunction<T> applier, int max) {
        return value -> {
            if (value > max) {
                throw new DecoderException("Value " + value + " is larger than limit " + max);
            }
            return applier.apply(value);
        };
    }

    public <T, C extends Collection<T>> C readCollection(IntFunction<C> collectionFactory, PacketDecoder<? super PacketByteBuf, T> reader) {
        int i = this.readVarInt();
        Collection collection = (Collection)collectionFactory.apply(1);
        for (int j = 0; j < 1; ++j) {
            collection.add(reader.decode(this));
        }
        return (C)collection;
    }

    public <T> void writeCollection(Collection<T> collection, PacketEncoder<? super PacketByteBuf, T> writer) {
        this.writeVarInt(collection.size());
        for (T object : collection) {
            writer.encode(this, object);
        }
    }

    public <T> List<T> readList(PacketDecoder<? super PacketByteBuf, T> reader) {
        return this.readCollection(Lists::newArrayListWithCapacity, reader);
    }

    public IntList readIntList() {
        int i = this.readVarInt();
        IntArrayList intList = new IntArrayList();
        for (int j = 0; j < i; ++j) {
            intList.add(this.readVarInt());
        }
        return intList;
    }

    public void writeIntList(IntList list) {
        this.writeVarInt(list.size());
        list.forEach(this::writeVarInt);
    }

    public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> mapFactory, PacketDecoder<? super PacketByteBuf, K> keyReader, PacketDecoder<? super PacketByteBuf, V> valueReader) {
        int i = this.readVarInt();
        Map map = (Map)mapFactory.apply(1);
        for (int j = 0; j < 1; ++j) {
            K object = keyReader.decode(this);
            V object2 = valueReader.decode(this);
            map.put(object, object2);
        }
        return (M)map;
    }

    public <K, V> Map<K, V> readMap(PacketDecoder<? super PacketByteBuf, K> keyReader, PacketDecoder<? super PacketByteBuf, V> valueReader) {
        return this.readMap(Maps::newHashMapWithExpectedSize, keyReader, valueReader);
    }

    public <K, V> void writeMap(Map<K, V> map, PacketEncoder<? super PacketByteBuf, K> keyWriter, PacketEncoder<? super PacketByteBuf, V> valueWriter) {
        this.writeVarInt(map.size());
        map.forEach((key, value) -> {
            keyWriter.encode(this, key);
            valueWriter.encode(this, value);
        });
    }

    public void forEachInCollection(Consumer<PacketByteBuf> consumer) {
        int i = this.readVarInt();
        for (int j = 0; j < i; ++j) {
            consumer.accept(this);
        }
    }

    public <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumSet, Class<E> type) {
        Enum[] enums = (Enum[])type.getEnumConstants();
        BitSet bitSet = new BitSet(enums.length);
        for (int i = 0; i < enums.length; ++i) {
            bitSet.set(i, enumSet.contains(enums[i]));
        }
        this.writeBitSet(bitSet, enums.length);
    }

    public <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> type) {
        Enum[] enums = (Enum[])type.getEnumConstants();
        BitSet bitSet = this.readBitSet(enums.length);
        EnumSet<Enum> enumSet = EnumSet.noneOf(type);
        for (int i = 0; i < enums.length; ++i) {
            if (!bitSet.get(i)) continue;
            enumSet.add(enums[i]);
        }
        return enumSet;
    }

    public <T> void writeOptional(Optional<T> value, PacketEncoder<? super PacketByteBuf, T> writer) {
        if (value.isPresent()) {
            this.net_minecraft_network_PacketByteBuf_writeBoolean(true);
            writer.encode(this, value.get());
        } else {
            this.net_minecraft_network_PacketByteBuf_writeBoolean(false);
        }
    }

    public <T> Optional<T> readOptional(PacketDecoder<? super PacketByteBuf, T> reader) {
        if (this.readBoolean()) {
            return Optional.of(reader.decode(this));
        }
        return Optional.empty();
    }

    public <L, R> void writeEither(Either<L, R> either, PacketEncoder<? super PacketByteBuf, L> leftEncoder, PacketEncoder<? super PacketByteBuf, R> rightEncoder) {
        either.ifLeft(object -> {
            this.net_minecraft_network_PacketByteBuf_writeBoolean(true);
            leftEncoder.encode(this, object);
        }).ifRight(object -> {
            this.net_minecraft_network_PacketByteBuf_writeBoolean(false);
            rightEncoder.encode(this, object);
        });
    }

    public <L, R> Either<L, R> readEither(PacketDecoder<? super PacketByteBuf, L> leftDecoder, PacketDecoder<? super PacketByteBuf, R> rightDecoder) {
        if (this.readBoolean()) {
            return Either.left(leftDecoder.decode(this));
        }
        return Either.right(rightDecoder.decode(this));
    }

    @Nullable
    public <T> T readNullable(PacketDecoder<? super PacketByteBuf, T> reader) {
        return PacketByteBuf.readNullable(this, reader);
    }

    @Nullable
    public static <T, B extends ByteBuf> T readNullable(B buf, PacketDecoder<? super B, T> reader) {
        if (buf.readBoolean()) {
            return reader.decode(buf);
        }
        return null;
    }

    public <T> void writeNullable(@Nullable T value, PacketEncoder<? super PacketByteBuf, T> writer) {
        PacketByteBuf.writeNullable(this, value, writer);
    }

    public static <T, B extends ByteBuf> void writeNullable(B buf, @Nullable T value, PacketEncoder<? super B, T> writer) {
        if (value != null) {
            buf.writeBoolean(true);
            writer.encode(buf, value);
        } else {
            buf.writeBoolean(false);
        }
    }

    public byte[] readByteArray() {
        return PacketByteBuf.readByteArray(this);
    }

    public static byte[] readByteArray(ByteBuf buf) {
        return PacketByteBuf.readByteArray(buf, buf.readableBytes());
    }

    public PacketByteBuf writeByteArray(byte[] array) {
        PacketByteBuf.writeByteArray(this, array);
        return this;
    }

    public static void writeByteArray(ByteBuf buf, byte[] array) {
        VarInts.write(buf, array.length);
        buf.writeBytes(array);
    }

    public byte[] readByteArray(int maxSize) {
        return PacketByteBuf.readByteArray(this, maxSize);
    }

    public static byte[] readByteArray(ByteBuf buf, int maxSize) {
        int i = VarInts.read(buf);
        if (i > maxSize) {
            throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + maxSize);
        }
        byte[] bs = new byte[i];
        buf.readBytes(bs);
        return bs;
    }

    public PacketByteBuf writeIntArray(int[] array) {
        this.writeVarInt(array.length);
        for (int i : array) {
            this.writeVarInt(i);
        }
        return this;
    }

    public int[] readIntArray() {
        return this.readIntArray(this.readableBytes());
    }

    public int[] readIntArray(int maxSize) {
        int i = this.readVarInt();
        if (i > maxSize) {
            throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxSize);
        }
        int[] is = new int[i];
        for (int j = 0; j < is.length; ++j) {
            is[j] = this.readVarInt();
        }
        return is;
    }

    public PacketByteBuf writeLongArray(long[] values) {
        PacketByteBuf.writeLongArray(this, values);
        return this;
    }

    public static void writeLongArray(ByteBuf buf, long[] values) {
        VarInts.write(buf, values.length);
        PacketByteBuf.writeFixedLengthLongArray(buf, values);
    }

    public PacketByteBuf writeFixedLengthLongArray(long[] values) {
        PacketByteBuf.writeFixedLengthLongArray(this, values);
        return this;
    }

    public static void writeFixedLengthLongArray(ByteBuf buf, long[] values) {
        for (long l : values) {
            buf.writeLong(l);
        }
    }

    public long[] readLongArray() {
        return PacketByteBuf.readLongArray(this);
    }

    public long[] readFixedLengthLongArray(long[] values) {
        return PacketByteBuf.readFixedLengthLongArray(this, values);
    }

    public static long[] readLongArray(ByteBuf buf) {
        int j;
        int i = VarInts.read(buf);
        if (i > (j = buf.readableBytes() / 8)) {
            throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + j);
        }
        return PacketByteBuf.readFixedLengthLongArray(buf, new long[i]);
    }

    public static long[] readFixedLengthLongArray(ByteBuf buf, long[] values) {
        for (int i = 0; i < values.length; ++i) {
            values[i] = buf.readLong();
        }
        return values;
    }

    public BlockPos readBlockPos() {
        return PacketByteBuf.readBlockPos(this);
    }

    public static BlockPos readBlockPos(ByteBuf buf) {
        return BlockPos.fromLong(buf.readLong());
    }

    public PacketByteBuf writeBlockPos(BlockPos pos) {
        PacketByteBuf.writeBlockPos(this, pos);
        return this;
    }

    public static void writeBlockPos(ByteBuf buf, BlockPos pos) {
        buf.writeLong(pos.asLong());
    }

    public ChunkPos readChunkPos() {
        return new ChunkPos(this.readLong());
    }

    public PacketByteBuf writeChunkPos(ChunkPos pos) {
        this.net_minecraft_network_PacketByteBuf_writeLong(pos.toLong());
        return this;
    }

    public static ChunkPos readChunkPos(ByteBuf buf) {
        return new ChunkPos(buf.readLong());
    }

    public static void writeChunkPos(ByteBuf buf, ChunkPos pos) {
        buf.writeLong(pos.toLong());
    }

    public ChunkSectionPos readChunkSectionPos() {
        return ChunkSectionPos.from(this.readLong());
    }

    public PacketByteBuf writeChunkSectionPos(ChunkSectionPos pos) {
        this.net_minecraft_network_PacketByteBuf_writeLong(pos.asLong());
        return this;
    }

    public GlobalPos readGlobalPos() {
        RegistryKey<World> registryKey = this.readRegistryKey(RegistryKeys.WORLD);
        BlockPos blockPos = this.readBlockPos();
        return GlobalPos.create(registryKey, blockPos);
    }

    public void writeGlobalPos(GlobalPos pos) {
        this.writeRegistryKey(pos.dimension());
        this.writeBlockPos(pos.pos());
    }

    public Vector3f readVector3f() {
        return PacketByteBuf.readVector3f(this);
    }

    public static Vector3f readVector3f(ByteBuf buf) {
        return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public void writeVector3f(Vector3f vector3f) {
        PacketByteBuf.writeVector3f(this, vector3f);
    }

    public static void writeVector3f(ByteBuf buf, Vector3f vector) {
        buf.writeFloat(vector.x());
        buf.writeFloat(vector.y());
        buf.writeFloat(vector.z());
    }

    public Quaternionf readQuaternionf() {
        return PacketByteBuf.readQuaternionf(this);
    }

    public static Quaternionf readQuaternionf(ByteBuf buf) {
        return new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public void writeQuaternionf(Quaternionf quaternionf) {
        PacketByteBuf.writeQuaternionf(this, quaternionf);
    }

    public static void writeQuaternionf(ByteBuf buf, Quaternionf quaternion) {
        buf.writeFloat(quaternion.x);
        buf.writeFloat(quaternion.y);
        buf.writeFloat(quaternion.z);
        buf.writeFloat(quaternion.w);
    }

    public static Vec3d readVec3d(ByteBuf buf) {
        return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public Vec3d readVec3d() {
        return PacketByteBuf.readVec3d(this);
    }

    public static void writeVec3d(ByteBuf buf, Vec3d vec) {
        buf.writeDouble(vec.getX());
        buf.writeDouble(vec.getY());
        buf.writeDouble(vec.getZ());
    }

    public void writeVec3d(Vec3d vec) {
        PacketByteBuf.writeVec3d(this, vec);
    }

    public <T extends Enum<T>> T readEnumConstant(Class<T> enumClass) {
        return (T)((Enum[])enumClass.getEnumConstants())[this.readVarInt()];
    }

    public PacketByteBuf writeEnumConstant(Enum<?> instance) {
        return this.writeVarInt(instance.ordinal());
    }

    public <T> T decode(IntFunction<T> idToValue) {
        int i = this.readVarInt();
        return idToValue.apply(i);
    }

    public <T> PacketByteBuf encode(ToIntFunction<T> valueToId, T value) {
        int i = valueToId.applyAsInt(value);
        return this.writeVarInt(1);
    }

    public int readVarInt() {
        return VarInts.read(this.parent);
    }

    public long readVarLong() {
        return VarLongs.read(this.parent);
    }

    public PacketByteBuf writeUuid(UUID uuid) {
        PacketByteBuf.writeUuid(this, uuid);
        return this;
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUuid() {
        return PacketByteBuf.readUuid(this);
    }

    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public PacketByteBuf writeVarInt(int value) {
        VarInts.write(this.parent, value);
        return this;
    }

    public PacketByteBuf writeVarLong(long value) {
        VarLongs.write(this.parent, value);
        return this;
    }

    public PacketByteBuf writeNbt(@Nullable NbtElement nbt) {
        PacketByteBuf.writeNbt(this, nbt);
        return this;
    }

    public static void writeNbt(ByteBuf buf, @Nullable NbtElement nbt) {
        if (nbt == null) {
            nbt = NbtEnd.INSTANCE;
        }
        try {
            NbtIo.writeForPacket(nbt, (DataOutput)new ByteBufOutputStream(buf));
        }
        catch (IOException iOException) {
            throw new EncoderException((Throwable)iOException);
        }
    }

    @Nullable
    public NbtCompound readNbt() {
        return PacketByteBuf.readNbt(this);
    }

    @Nullable
    public static NbtCompound readNbt(ByteBuf buf) {
        NbtElement nbtElement = PacketByteBuf.readNbt(buf, NbtSizeTracker.of(0x200000L));
        if (nbtElement == null || nbtElement instanceof NbtCompound) {
            return (NbtCompound)nbtElement;
        }
        throw new DecoderException("Not a compound tag: " + String.valueOf(nbtElement));
    }

    @Nullable
    public static NbtElement readNbt(ByteBuf buf, NbtSizeTracker sizeTracker) {
        try {
            NbtElement nbtElement = NbtIo.read((DataInput)new ByteBufInputStream(buf), sizeTracker);
            if (nbtElement.getType() == 0) {
                return null;
            }
            return nbtElement;
        }
        catch (IOException iOException) {
            throw new EncoderException((Throwable)iOException);
        }
    }

    @Nullable
    public NbtElement readNbt(NbtSizeTracker sizeTracker) {
        return PacketByteBuf.readNbt(this, sizeTracker);
    }

    public String readString() {
        return this.readString(DEFAULT_MAX_STRING_LENGTH);
    }

    public String readString(int maxLength) {
        return StringEncoding.decode(this.parent, maxLength);
    }

    public PacketByteBuf writeString(String string) {
        return this.writeString(string, DEFAULT_MAX_STRING_LENGTH);
    }

    public PacketByteBuf writeString(String string, int maxLength) {
        StringEncoding.encode(this.parent, string, maxLength);
        return this;
    }

    public Identifier readIdentifier() {
        return Identifier.of(this.readString(DEFAULT_MAX_STRING_LENGTH));
    }

    public PacketByteBuf writeIdentifier(Identifier id) {
        this.writeString(id.toString());
        return this;
    }

    public <T> RegistryKey<T> readRegistryKey(RegistryKey<? extends Registry<T>> registryRef) {
        Identifier identifier = this.readIdentifier();
        return RegistryKey.of(registryRef, identifier);
    }

    public void writeRegistryKey(RegistryKey<?> key) {
        this.writeIdentifier(key.getValue());
    }

    public <T> RegistryKey<? extends Registry<T>> readRegistryRefKey() {
        Identifier identifier = this.readIdentifier();
        return RegistryKey.ofRegistry(identifier);
    }

    public Date readDate() {
        return new Date(this.readLong());
    }

    public PacketByteBuf writeDate(Date date) {
        this.net_minecraft_network_PacketByteBuf_writeLong(date.getTime());
        return this;
    }

    public Instant readInstant() {
        return Instant.ofEpochMilli(this.readLong());
    }

    public void writeInstant(Instant instant) {
        this.net_minecraft_network_PacketByteBuf_writeLong(instant.toEpochMilli());
    }

    public PublicKey readPublicKey() {
        try {
            return NetworkEncryptionUtils.decodeEncodedRsaPublicKey(this.readByteArray(512));
        }
        catch (NetworkEncryptionException networkEncryptionException) {
            throw new DecoderException("Malformed public key bytes", (Throwable)networkEncryptionException);
        }
    }

    public PacketByteBuf writePublicKey(PublicKey publicKey) {
        this.writeByteArray(publicKey.getEncoded());
        return this;
    }

    public BlockHitResult readBlockHitResult() {
        BlockPos blockPos = this.readBlockPos();
        Direction direction = this.readEnumConstant(Direction.class);
        float f = this.readFloat();
        float g = this.readFloat();
        float h = this.readFloat();
        boolean bl = this.readBoolean();
        boolean bl2 = this.readBoolean();
        return new BlockHitResult(new Vec3d((double)blockPos.getX() + (double)f, (double)blockPos.getY() + (double)g, (double)blockPos.getZ() + (double)h), direction, blockPos, bl, bl2);
    }

    public void writeBlockHitResult(BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();
        this.writeBlockPos(blockPos);
        this.writeEnumConstant(hitResult.getSide());
        Vec3d vec3d = hitResult.getPos();
        this.net_minecraft_network_PacketByteBuf_writeFloat((float)(vec3d.x - (double)blockPos.getX()));
        this.net_minecraft_network_PacketByteBuf_writeFloat((float)(vec3d.y - (double)blockPos.getY()));
        this.net_minecraft_network_PacketByteBuf_writeFloat((float)(vec3d.z - (double)blockPos.getZ()));
        this.net_minecraft_network_PacketByteBuf_writeBoolean(hitResult.isInsideBlock());
        this.net_minecraft_network_PacketByteBuf_writeBoolean(hitResult.isAgainstWorldBorder());
    }

    public BitSet readBitSet() {
        return BitSet.valueOf(this.readLongArray());
    }

    public void writeBitSet(BitSet bitSet) {
        this.writeLongArray(bitSet.toLongArray());
    }

    public BitSet readBitSet(int size) {
        byte[] bs = new byte[MathHelper.ceilDiv(size, 8)];
        this.net_minecraft_network_PacketByteBuf_readBytes(bs);
        return BitSet.valueOf(bs);
    }

    public void writeBitSet(BitSet bitSet, int size) {
        if (bitSet.length() > size) {
            throw new EncoderException("BitSet is larger than expected size (" + bitSet.length() + ">" + size + ")");
        }
        byte[] bs = bitSet.toByteArray();
        this.net_minecraft_network_PacketByteBuf_writeBytes(Arrays.copyOf(bs, MathHelper.ceilDiv(size, 8)));
    }

    public static int readSyncId(ByteBuf buf) {
        return VarInts.read(buf);
    }

    public int readSyncId() {
        return PacketByteBuf.readSyncId(this.parent);
    }

    public static void writeSyncId(ByteBuf buf, int syncId) {
        VarInts.write(buf, syncId);
    }

    public void writeSyncId(int syncId) {
        PacketByteBuf.writeSyncId(this.parent, syncId);
    }

    public boolean isContiguous() {
        return this.parent.isContiguous();
    }

    public int maxFastWritableBytes() {
        return this.parent.maxFastWritableBytes();
    }

    public int capacity() {
        return this.parent.capacity();
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_capacity(int i) {
        this.parent.capacity(i);
        return this;
    }

    public int maxCapacity() {
        return this.parent.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.parent.alloc();
    }

    public ByteOrder order() {
        return this.parent.order();
    }

    public ByteBuf order(ByteOrder byteOrder) {
        return this.parent.order(byteOrder);
    }

    public ByteBuf unwrap() {
        return this.parent;
    }

    public boolean isDirect() {
        return this.parent.isDirect();
    }

    public boolean isReadOnly() {
        return this.parent.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.parent.asReadOnly();
    }

    public int readerIndex() {
        return this.parent.readerIndex();
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readerIndex(int i) {
        this.parent.readerIndex(i);
        return this;
    }

    public int writerIndex() {
        return this.parent.writerIndex();
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writerIndex(int i) {
        this.parent.writerIndex(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setIndex(int i, int j) {
        this.parent.setIndex(i, j);
        return this;
    }

    public int readableBytes() {
        return this.parent.readableBytes();
    }

    public int writableBytes() {
        return this.parent.writableBytes();
    }

    public int maxWritableBytes() {
        return this.parent.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.parent.isReadable();
    }

    public boolean isReadable(int size) {
        return this.parent.isReadable(size);
    }

    public boolean isWritable() {
        return this.parent.isWritable();
    }

    public boolean isWritable(int size) {
        return this.parent.isWritable(size);
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_clear() {
        this.parent.clear();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_markReaderIndex() {
        this.parent.markReaderIndex();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_resetReaderIndex() {
        this.parent.resetReaderIndex();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_markWriterIndex() {
        this.parent.markWriterIndex();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_resetWriterIndex() {
        this.parent.resetWriterIndex();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_discardReadBytes() {
        this.parent.discardReadBytes();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_discardSomeReadBytes() {
        this.parent.discardSomeReadBytes();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_ensureWritable(int i) {
        this.parent.ensureWritable(i);
        return this;
    }

    public int ensureWritable(int minBytes, boolean force) {
        return this.parent.ensureWritable(minBytes, force);
    }

    public boolean getBoolean(int index) {
        return this.parent.getBoolean(index);
    }

    public byte getByte(int index) {
        return this.parent.getByte(index);
    }

    public short getUnsignedByte(int index) {
        return this.parent.getUnsignedByte(index);
    }

    public short getShort(int index) {
        return this.parent.getShort(index);
    }

    public short getShortLE(int index) {
        return this.parent.getShortLE(index);
    }

    public int getUnsignedShort(int index) {
        return this.parent.getUnsignedShort(index);
    }

    public int getUnsignedShortLE(int index) {
        return this.parent.getUnsignedShortLE(index);
    }

    public int getMedium(int index) {
        return this.parent.getMedium(index);
    }

    public int getMediumLE(int index) {
        return this.parent.getMediumLE(index);
    }

    public int getUnsignedMedium(int index) {
        return this.parent.getUnsignedMedium(index);
    }

    public int getUnsignedMediumLE(int index) {
        return this.parent.getUnsignedMediumLE(index);
    }

    public int getInt(int index) {
        return this.parent.getInt(index);
    }

    public int getIntLE(int index) {
        return this.parent.getIntLE(index);
    }

    public long getUnsignedInt(int index) {
        return this.parent.getUnsignedInt(index);
    }

    public long getUnsignedIntLE(int index) {
        return this.parent.getUnsignedIntLE(index);
    }

    public long getLong(int index) {
        return this.parent.getLong(index);
    }

    public long getLongLE(int index) {
        return this.parent.getLongLE(index);
    }

    public char getChar(int index) {
        return this.parent.getChar(index);
    }

    public float getFloat(int index) {
        return this.parent.getFloat(index);
    }

    public double getDouble(int index) {
        return this.parent.getDouble(index);
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_getBytes(int i, ByteBuf byteBuf) {
        this.parent.getBytes(i, byteBuf);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_getBytes(int i, ByteBuf byteBuf, int j) {
        this.parent.getBytes(i, byteBuf, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_getBytes(int i, ByteBuf byteBuf, int j, int k) {
        this.parent.getBytes(i, byteBuf, j, k);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_getBytes(int i, byte[] bs) {
        this.parent.getBytes(i, bs);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_getBytes(int i, byte[] bs, int j, int k) {
        this.parent.getBytes(i, bs, j, k);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_getBytes(int i, ByteBuffer byteBuffer) {
        this.parent.getBytes(i, byteBuffer);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_getBytes(int i, OutputStream outputStream, int j) throws IOException {
        this.parent.getBytes(i, outputStream, j);
        return this;
    }

    public int getBytes(int index, GatheringByteChannel channel, int length) throws IOException {
        return this.parent.getBytes(index, channel, length);
    }

    public int getBytes(int index, FileChannel channel, long pos, int length) throws IOException {
        return this.parent.getBytes(index, channel, pos, length);
    }

    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.parent.getCharSequence(index, length, charset);
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setBoolean(int i, boolean bl) {
        this.parent.setBoolean(i, bl);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setByte(int i, int j) {
        this.parent.setByte(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setShort(int i, int j) {
        this.parent.setShort(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setShortLE(int i, int j) {
        this.parent.setShortLE(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setMedium(int i, int j) {
        this.parent.setMedium(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setMediumLE(int i, int j) {
        this.parent.setMediumLE(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setInt(int i, int j) {
        this.parent.setInt(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setIntLE(int i, int j) {
        this.parent.setIntLE(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setLong(int i, long l) {
        this.parent.setLong(i, l);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setLongLE(int i, long l) {
        this.parent.setLongLE(i, l);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setChar(int i, int j) {
        this.parent.setChar(i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setFloat(int i, float f) {
        this.parent.setFloat(i, f);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setDouble(int i, double d) {
        this.parent.setDouble(i, d);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setBytes(int i, ByteBuf byteBuf) {
        this.parent.setBytes(i, byteBuf);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setBytes(int i, ByteBuf byteBuf, int j) {
        this.parent.setBytes(i, byteBuf, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setBytes(int i, ByteBuf byteBuf, int j, int k) {
        this.parent.setBytes(i, byteBuf, j, k);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setBytes(int i, byte[] bs) {
        this.parent.setBytes(i, bs);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setBytes(int i, byte[] bs, int j, int k) {
        this.parent.setBytes(i, bs, j, k);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setBytes(int i, ByteBuffer byteBuffer) {
        this.parent.setBytes(i, byteBuffer);
        return this;
    }

    public int setBytes(int index, InputStream stream, int length) throws IOException {
        return this.parent.setBytes(index, stream, length);
    }

    public int setBytes(int index, ScatteringByteChannel channel, int length) throws IOException {
        return this.parent.setBytes(index, channel, length);
    }

    public int setBytes(int index, FileChannel channel, long pos, int length) throws IOException {
        return this.parent.setBytes(index, channel, pos, length);
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_setZero(int i, int j) {
        this.parent.setZero(i, j);
        return this;
    }

    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.parent.setCharSequence(index, sequence, charset);
    }

    public boolean readBoolean() {
        return this.parent.readBoolean();
    }

    public byte readByte() {
        return this.parent.readByte();
    }

    public short readUnsignedByte() {
        return this.parent.readUnsignedByte();
    }

    public short readShort() {
        return this.parent.readShort();
    }

    public short readShortLE() {
        return this.parent.readShortLE();
    }

    public int readUnsignedShort() {
        return this.parent.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.parent.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.parent.readMedium();
    }

    public int readMediumLE() {
        return this.parent.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.parent.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.parent.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.parent.readInt();
    }

    public int readIntLE() {
        return this.parent.readIntLE();
    }

    public long readUnsignedInt() {
        return this.parent.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.parent.readUnsignedIntLE();
    }

    public long readLong() {
        return this.parent.readLong();
    }

    public long readLongLE() {
        return this.parent.readLongLE();
    }

    public char readChar() {
        return this.parent.readChar();
    }

    public float readFloat() {
        return this.parent.readFloat();
    }

    public double readDouble() {
        return this.parent.readDouble();
    }

    public ByteBuf readBytes(int length) {
        return this.parent.readBytes(length);
    }

    public ByteBuf readSlice(int length) {
        return this.parent.readSlice(length);
    }

    public ByteBuf readRetainedSlice(int length) {
        return this.parent.readRetainedSlice(length);
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readBytes(ByteBuf byteBuf) {
        this.parent.readBytes(byteBuf);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readBytes(ByteBuf byteBuf, int i) {
        this.parent.readBytes(byteBuf, i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readBytes(ByteBuf byteBuf, int i, int j) {
        this.parent.readBytes(byteBuf, i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readBytes(byte[] bs) {
        this.parent.readBytes(bs);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readBytes(byte[] bs, int i, int j) {
        this.parent.readBytes(bs, i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readBytes(ByteBuffer byteBuffer) {
        this.parent.readBytes(byteBuffer);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_readBytes(OutputStream outputStream, int i) throws IOException {
        this.parent.readBytes(outputStream, i);
        return this;
    }

    public int readBytes(GatheringByteChannel channel, int length) throws IOException {
        return this.parent.readBytes(channel, length);
    }

    public CharSequence readCharSequence(int length, Charset charset) {
        return this.parent.readCharSequence(length, charset);
    }

    public int readBytes(FileChannel channel, long pos, int length) throws IOException {
        return this.parent.readBytes(channel, pos, length);
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_skipBytes(int i) {
        this.parent.skipBytes(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeBoolean(boolean bl) {
        this.parent.writeBoolean(bl);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeByte(int i) {
        this.parent.writeByte(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeShort(int i) {
        this.parent.writeShort(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeShortLE(int i) {
        this.parent.writeShortLE(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeMedium(int i) {
        this.parent.writeMedium(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeMediumLE(int i) {
        this.parent.writeMediumLE(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeInt(int i) {
        this.parent.writeInt(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeIntLE(int i) {
        this.parent.writeIntLE(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeLong(long l) {
        this.parent.writeLong(l);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeLongLE(long l) {
        this.parent.writeLongLE(l);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeChar(int i) {
        this.parent.writeChar(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeFloat(float f) {
        this.parent.writeFloat(f);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeDouble(double d) {
        this.parent.writeDouble(d);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeBytes(ByteBuf byteBuf) {
        this.parent.writeBytes(byteBuf);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeBytes(ByteBuf byteBuf, int i) {
        this.parent.writeBytes(byteBuf, i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeBytes(ByteBuf byteBuf, int i, int j) {
        this.parent.writeBytes(byteBuf, i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeBytes(byte[] bs) {
        this.parent.writeBytes(bs);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeBytes(byte[] bs, int i, int j) {
        this.parent.writeBytes(bs, i, j);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeBytes(ByteBuffer byteBuffer) {
        this.parent.writeBytes(byteBuffer);
        return this;
    }

    public int writeBytes(InputStream stream, int length) throws IOException {
        return this.parent.writeBytes(stream, length);
    }

    public int writeBytes(ScatteringByteChannel channel, int length) throws IOException {
        return this.parent.writeBytes(channel, length);
    }

    public int writeBytes(FileChannel channel, long pos, int length) throws IOException {
        return this.parent.writeBytes(channel, pos, length);
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_writeZero(int i) {
        this.parent.writeZero(i);
        return this;
    }

    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.parent.writeCharSequence(sequence, charset);
    }

    public int indexOf(int from, int to, byte value) {
        return this.parent.indexOf(from, to, value);
    }

    public int bytesBefore(byte value) {
        return this.parent.bytesBefore(value);
    }

    public int bytesBefore(int length, byte value) {
        return this.parent.bytesBefore(length, value);
    }

    public int bytesBefore(int index, int length, byte value) {
        return this.parent.bytesBefore(index, length, value);
    }

    public int forEachByte(ByteProcessor byteProcessor) {
        return this.parent.forEachByte(byteProcessor);
    }

    public int forEachByte(int index, int length, ByteProcessor byteProcessor) {
        return this.parent.forEachByte(index, length, byteProcessor);
    }

    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(byteProcessor);
    }

    public int forEachByteDesc(int index, int length, ByteProcessor byteProcessor) {
        return this.parent.forEachByteDesc(index, length, byteProcessor);
    }

    public ByteBuf copy() {
        return this.parent.copy();
    }

    public ByteBuf copy(int index, int length) {
        return this.parent.copy(index, length);
    }

    public ByteBuf slice() {
        return this.parent.slice();
    }

    public ByteBuf retainedSlice() {
        return this.parent.retainedSlice();
    }

    public ByteBuf slice(int index, int length) {
        return this.parent.slice(index, length);
    }

    public ByteBuf retainedSlice(int index, int length) {
        return this.parent.retainedSlice(index, length);
    }

    public ByteBuf duplicate() {
        return this.parent.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.parent.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.parent.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.parent.nioBuffer();
    }

    public ByteBuffer nioBuffer(int index, int length) {
        return this.parent.nioBuffer(index, length);
    }

    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.parent.internalNioBuffer(index, length);
    }

    public ByteBuffer[] nioBuffers() {
        return this.parent.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.parent.nioBuffers(index, length);
    }

    public boolean hasArray() {
        return this.parent.hasArray();
    }

    public byte[] array() {
        return this.parent.array();
    }

    public int arrayOffset() {
        return this.parent.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.parent.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.parent.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.parent.toString(charset);
    }

    public String toString(int index, int length, Charset charset) {
        return this.parent.toString(index, length, charset);
    }

    public int hashCode() {
        return this.parent.hashCode();
    }

    public boolean equals(Object o) {
        return this.parent.equals(o);
    }

    public int compareTo(ByteBuf byteBuf) {
        return this.parent.compareTo(byteBuf);
    }

    public String toString() {
        return this.parent.toString();
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_retain(int i) {
        this.parent.retain(i);
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_retain() {
        this.parent.retain();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_touch() {
        this.parent.touch();
        return this;
    }

    public PacketByteBuf net_minecraft_network_PacketByteBuf_touch(Object object) {
        this.parent.touch(object);
        return this;
    }

    public int refCnt() {
        return this.parent.refCnt();
    }

    public boolean release() {
        return this.parent.release();
    }

    public boolean release(int decrement) {
        return this.parent.release(decrement);
    }

    public ByteBuf io_netty_buffer_ByteBuf_touch(Object object) {
        return this.net_minecraft_network_PacketByteBuf_touch(object);
    }

    public ByteBuf io_netty_buffer_ByteBuf_touch() {
        return this.net_minecraft_network_PacketByteBuf_touch();
    }

    public ByteBuf io_netty_buffer_ByteBuf_retain() {
        return this.net_minecraft_network_PacketByteBuf_retain();
    }

    public ByteBuf io_netty_buffer_ByteBuf_retain(int increment) {
        return this.net_minecraft_network_PacketByteBuf_retain(increment);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeZero(int length) {
        return this.net_minecraft_network_PacketByteBuf_writeZero(length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeBytes(ByteBuffer buf) {
        return this.net_minecraft_network_PacketByteBuf_writeBytes(buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeBytes(byte[] bytes, int sourceIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_writeBytes(bytes, sourceIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeBytes(byte[] bytes) {
        return this.net_minecraft_network_PacketByteBuf_writeBytes(bytes);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeBytes(ByteBuf buf, int sourceIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_writeBytes(buf, sourceIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeBytes(ByteBuf buf, int length) {
        return this.net_minecraft_network_PacketByteBuf_writeBytes(buf, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeBytes(ByteBuf buf) {
        return this.net_minecraft_network_PacketByteBuf_writeBytes(buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeDouble(double value) {
        return this.net_minecraft_network_PacketByteBuf_writeDouble(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeFloat(float value) {
        return this.net_minecraft_network_PacketByteBuf_writeFloat(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeChar(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeChar(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeLongLE(long value) {
        return this.net_minecraft_network_PacketByteBuf_writeLongLE(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeLong(long value) {
        return this.net_minecraft_network_PacketByteBuf_writeLong(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeIntLE(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeIntLE(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeInt(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeInt(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeMediumLE(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeMediumLE(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeMedium(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeMedium(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeShortLE(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeShortLE(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeShort(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeShort(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeByte(int value) {
        return this.net_minecraft_network_PacketByteBuf_writeByte(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writeBoolean(boolean value) {
        return this.net_minecraft_network_PacketByteBuf_writeBoolean(value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_skipBytes(int length) {
        return this.net_minecraft_network_PacketByteBuf_skipBytes(length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readBytes(OutputStream stream, int length) throws IOException {
        return this.net_minecraft_network_PacketByteBuf_readBytes(stream, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readBytes(ByteBuffer buf) {
        return this.net_minecraft_network_PacketByteBuf_readBytes(buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readBytes(byte[] bytes, int outputIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_readBytes(bytes, outputIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readBytes(byte[] bytes) {
        return this.net_minecraft_network_PacketByteBuf_readBytes(bytes);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readBytes(ByteBuf buf, int outputIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_readBytes(buf, outputIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readBytes(ByteBuf buf, int length) {
        return this.net_minecraft_network_PacketByteBuf_readBytes(buf, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readBytes(ByteBuf buf) {
        return this.net_minecraft_network_PacketByteBuf_readBytes(buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setZero(int index, int length) {
        return this.net_minecraft_network_PacketByteBuf_setZero(index, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setBytes(int index, ByteBuffer buf) {
        return this.net_minecraft_network_PacketByteBuf_setBytes(index, buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setBytes(int index, byte[] bytes, int sourceIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_setBytes(index, bytes, sourceIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setBytes(int index, byte[] bytes) {
        return this.net_minecraft_network_PacketByteBuf_setBytes(index, bytes);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setBytes(int index, ByteBuf buf, int sourceIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_setBytes(index, buf, sourceIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setBytes(int index, ByteBuf buf, int length) {
        return this.net_minecraft_network_PacketByteBuf_setBytes(index, buf, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setBytes(int index, ByteBuf buf) {
        return this.net_minecraft_network_PacketByteBuf_setBytes(index, buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setDouble(int index, double value) {
        return this.net_minecraft_network_PacketByteBuf_setDouble(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setFloat(int index, float value) {
        return this.net_minecraft_network_PacketByteBuf_setFloat(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setChar(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setChar(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setLongLE(int index, long value) {
        return this.net_minecraft_network_PacketByteBuf_setLongLE(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setLong(int index, long value) {
        return this.net_minecraft_network_PacketByteBuf_setLong(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setIntLE(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setIntLE(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setInt(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setInt(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setMediumLE(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setMediumLE(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setMedium(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setMedium(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setShortLE(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setShortLE(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setShort(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setShort(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setByte(int index, int value) {
        return this.net_minecraft_network_PacketByteBuf_setByte(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_setBoolean(int index, boolean value) {
        return this.net_minecraft_network_PacketByteBuf_setBoolean(index, value);
    }

    public ByteBuf io_netty_buffer_ByteBuf_getBytes(int index, OutputStream stream, int length) throws IOException {
        return this.net_minecraft_network_PacketByteBuf_getBytes(index, stream, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_getBytes(int index, ByteBuffer buf) {
        return this.net_minecraft_network_PacketByteBuf_getBytes(index, buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_getBytes(int index, byte[] bytes, int outputIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_getBytes(index, bytes, outputIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_getBytes(int index, byte[] bytes) {
        return this.net_minecraft_network_PacketByteBuf_getBytes(index, bytes);
    }

    public ByteBuf io_netty_buffer_ByteBuf_getBytes(int index, ByteBuf buf, int outputIndex, int length) {
        return this.net_minecraft_network_PacketByteBuf_getBytes(index, buf, outputIndex, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_getBytes(int index, ByteBuf buf, int length) {
        return this.net_minecraft_network_PacketByteBuf_getBytes(index, buf, length);
    }

    public ByteBuf io_netty_buffer_ByteBuf_getBytes(int index, ByteBuf buf) {
        return this.net_minecraft_network_PacketByteBuf_getBytes(index, buf);
    }

    public ByteBuf io_netty_buffer_ByteBuf_ensureWritable(int minBytes) {
        return this.net_minecraft_network_PacketByteBuf_ensureWritable(minBytes);
    }

    public ByteBuf io_netty_buffer_ByteBuf_discardSomeReadBytes() {
        return this.net_minecraft_network_PacketByteBuf_discardSomeReadBytes();
    }

    public ByteBuf io_netty_buffer_ByteBuf_discardReadBytes() {
        return this.net_minecraft_network_PacketByteBuf_discardReadBytes();
    }

    public ByteBuf io_netty_buffer_ByteBuf_resetWriterIndex() {
        return this.net_minecraft_network_PacketByteBuf_resetWriterIndex();
    }

    public ByteBuf io_netty_buffer_ByteBuf_markWriterIndex() {
        return this.net_minecraft_network_PacketByteBuf_markWriterIndex();
    }

    public ByteBuf io_netty_buffer_ByteBuf_resetReaderIndex() {
        return this.net_minecraft_network_PacketByteBuf_resetReaderIndex();
    }

    public ByteBuf io_netty_buffer_ByteBuf_markReaderIndex() {
        return this.net_minecraft_network_PacketByteBuf_markReaderIndex();
    }

    public ByteBuf io_netty_buffer_ByteBuf_clear() {
        return this.net_minecraft_network_PacketByteBuf_clear();
    }

    public ByteBuf io_netty_buffer_ByteBuf_setIndex(int readerIndex, int writerIndex) {
        return this.net_minecraft_network_PacketByteBuf_setIndex(readerIndex, writerIndex);
    }

    public ByteBuf io_netty_buffer_ByteBuf_writerIndex(int index) {
        return this.net_minecraft_network_PacketByteBuf_writerIndex(index);
    }

    public ByteBuf io_netty_buffer_ByteBuf_readerIndex(int index) {
        return this.net_minecraft_network_PacketByteBuf_readerIndex(index);
    }

    public ByteBuf io_netty_buffer_ByteBuf_capacity(int capacity) {
        return this.net_minecraft_network_PacketByteBuf_capacity(capacity);
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_touch(Object object) {
        return this.net_minecraft_network_PacketByteBuf_touch(object);
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_touch() {
        return this.net_minecraft_network_PacketByteBuf_touch();
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_retain(int increment) {
        return this.net_minecraft_network_PacketByteBuf_retain(increment);
    }

    public ReferenceCounted io_netty_util_ReferenceCounted_retain() {
        return this.net_minecraft_network_PacketByteBuf_retain();
    }
}

