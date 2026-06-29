/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.ByteBuf
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package net.minecraft.network.codec;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.network.encoding.VarLongs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface PacketCodecs {
    final static public int field_49674 = 65536;
    final static public PacketCodec<ByteBuf, Boolean> BOOLEAN = new PacketCodec<ByteBuf, Boolean>(){

        @Override
        public Boolean decode(ByteBuf byteBuf) {
            return byteBuf.readBoolean();
        }

        @Override
        public void encode(ByteBuf byteBuf, Boolean boolean_) {
            byteBuf.writeBoolean(boolean_.booleanValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Boolean)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Byte> BYTE = new PacketCodec<ByteBuf, Byte>(){

        @Override
        public Byte decode(ByteBuf byteBuf) {
            return byteBuf.readByte();
        }

        @Override
        public void encode(ByteBuf byteBuf, Byte byte_) {
            byteBuf.writeByte((int)byte_.byteValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Byte)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Float> DEGREES = BYTE.xmap(MathHelper::unpackDegrees, MathHelper::packDegrees);
    final static public PacketCodec<ByteBuf, Short> SHORT = new PacketCodec<ByteBuf, Short>(){

        @Override
        public Short decode(ByteBuf byteBuf) {
            return byteBuf.readShort();
        }

        @Override
        public void encode(ByteBuf byteBuf, Short short_) {
            byteBuf.writeShort((int)short_.shortValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Short)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Integer> UNSIGNED_SHORT = new PacketCodec<ByteBuf, Integer>(){

        @Override
        public Integer decode(ByteBuf byteBuf) {
            return byteBuf.readUnsignedShort();
        }

        @Override
        public void encode(ByteBuf byteBuf, Integer integer) {
            byteBuf.writeShort(integer.intValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Integer)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Integer> INTEGER = new PacketCodec<ByteBuf, Integer>(){

        @Override
        public Integer decode(ByteBuf byteBuf) {
            return byteBuf.readInt();
        }

        @Override
        public void encode(ByteBuf byteBuf, Integer integer) {
            byteBuf.writeInt(integer.intValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Integer)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Integer> VAR_INT = new PacketCodec<ByteBuf, Integer>(){

        @Override
        public Integer decode(ByteBuf byteBuf) {
            return VarInts.read(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, Integer integer) {
            VarInts.write(byteBuf, integer);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Integer)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, OptionalInt> OPTIONAL_INT = VAR_INT.xmap(value -> value == 0 ? OptionalInt.empty() : OptionalInt.of(value - 1), value -> value.isPresent() ? value.getAsInt() + 1 : 0);
    final static public PacketCodec<ByteBuf, Long> LONG = new PacketCodec<ByteBuf, Long>(){

        @Override
        public Long decode(ByteBuf byteBuf) {
            return byteBuf.readLong();
        }

        @Override
        public void encode(ByteBuf byteBuf, Long long_) {
            byteBuf.writeLong(long_.longValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Long)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Long> VAR_LONG = new PacketCodec<ByteBuf, Long>(){

        @Override
        public Long decode(ByteBuf byteBuf) {
            return VarLongs.read(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, Long long_) {
            VarLongs.write(byteBuf, long_);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Long)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Float> FLOAT = new PacketCodec<ByteBuf, Float>(){

        @Override
        public Float decode(ByteBuf byteBuf) {
            return Float.valueOf(byteBuf.readFloat());
        }

        @Override
        public void encode(ByteBuf byteBuf, Float float_) {
            byteBuf.writeFloat(float_.floatValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Float)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Double> DOUBLE = new PacketCodec<ByteBuf, Double>(){

        @Override
        public Double decode(ByteBuf byteBuf) {
            return byteBuf.readDouble();
        }

        @Override
        public void encode(ByteBuf byteBuf, Double double_) {
            byteBuf.writeDouble(double_.doubleValue());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Double)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, byte[]> BYTE_ARRAY = new PacketCodec<ByteBuf, byte[]>(){

        @Override
        public byte[] decode(ByteBuf buf) {
            return PacketByteBuf.readByteArray(buf);
        }

        @Override
        public void encode(ByteBuf buf, byte[] value) {
            PacketByteBuf.writeByteArray(buf, value);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (byte[])object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, long[]> LONG_ARRAY = new PacketCodec<ByteBuf, long[]>(){

        @Override
        public long[] decode(ByteBuf buf) {
            return PacketByteBuf.readLongArray(buf);
        }

        @Override
        public void encode(ByteBuf buf, long[] values) {
            PacketByteBuf.writeLongArray(buf, values);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (long[])object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, String> STRING = PacketCodecs.string(Short.MAX_VALUE);
    final static public PacketCodec<ByteBuf, NbtElement> NBT_ELEMENT = PacketCodecs.nbt(() -> NbtSizeTracker.of(0x200000L));
    final static public PacketCodec<ByteBuf, NbtElement> UNLIMITED_NBT_ELEMENT = PacketCodecs.nbt(NbtSizeTracker::ofUnlimitedBytes);
    final static public PacketCodec<ByteBuf, NbtCompound> NBT_COMPOUND = PacketCodecs.nbtCompound(() -> NbtSizeTracker.of(0x200000L));
    final static public PacketCodec<ByteBuf, NbtCompound> UNLIMITED_NBT_COMPOUND = PacketCodecs.nbtCompound(NbtSizeTracker::ofUnlimitedBytes);
    final static public PacketCodec<ByteBuf, Optional<NbtCompound>> OPTIONAL_NBT = new PacketCodec<ByteBuf, Optional<NbtCompound>>(){

        @Override
        public Optional<NbtCompound> decode(ByteBuf byteBuf) {
            return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf));
        }

        @Override
        public void encode(ByteBuf byteBuf, Optional<NbtCompound> optional) {
            PacketByteBuf.writeNbt(byteBuf, optional.orElse(null));
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Optional)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Vector3f> VECTOR_3F = new PacketCodec<ByteBuf, Vector3f>(){

        @Override
        public Vector3f decode(ByteBuf byteBuf) {
            return PacketByteBuf.readVector3f(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, Vector3f vector3f) {
            PacketByteBuf.writeVector3f(byteBuf, vector3f);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Vector3f)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Quaternionf> QUATERNION_F = new PacketCodec<ByteBuf, Quaternionf>(){

        @Override
        public Quaternionf decode(ByteBuf byteBuf) {
            return PacketByteBuf.readQuaternionf(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, Quaternionf quaternionf) {
            PacketByteBuf.writeQuaternionf(byteBuf, quaternionf);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Quaternionf)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Integer> SYNC_ID = new PacketCodec<ByteBuf, Integer>(){

        @Override
        public Integer decode(ByteBuf byteBuf) {
            return PacketByteBuf.readSyncId(byteBuf);
        }

        @Override
        public void encode(ByteBuf byteBuf, Integer integer) {
            PacketByteBuf.writeSyncId(byteBuf, integer);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Integer)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, PropertyMap> PROPERTY_MAP = new PacketCodec<ByteBuf, PropertyMap>(){
        final static private int NAME_MAX_LENGTH = 64;
        final static private int VALUE_MAX_LENGTH = Short.MAX_VALUE;
        final static private int SIGNATURE_MAX_LENGTH = 1024;
        final static private int MAP_MAX_SIZE = 16;

        @Override
        public PropertyMap decode(ByteBuf byteBuf) {
            int i = PacketCodecs.readCollectionSize(byteBuf, 16);
            PropertyMap propertyMap = new PropertyMap();
            for (int j = 0; j < i; ++j) {
                String string = StringEncoding.decode(byteBuf, 64);
                String string2 = StringEncoding.decode(byteBuf, Short.MAX_VALUE);
                String string3 = PacketByteBuf.readNullable(byteBuf, bufx -> StringEncoding.decode(bufx, 1024));
                Property property = new Property(string, string2, string3);
                propertyMap.put((Object)property.name(), (Object)property);
            }
            return propertyMap;
        }

        @Override
        public void encode(ByteBuf byteBuf, PropertyMap propertyMap) {
            PacketCodecs.writeCollectionSize(byteBuf, propertyMap.size(), 16);
            for (Property property : propertyMap.values()) {
                StringEncoding.encode(byteBuf, property.name(), 64);
                StringEncoding.encode(byteBuf, property.value(), Short.MAX_VALUE);
                PacketByteBuf.writeNullable(byteBuf, property.signature(), (bufx, signature) -> StringEncoding.encode(bufx, signature, 1024));
            }
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (PropertyMap)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, GameProfile> GAME_PROFILE = new PacketCodec<ByteBuf, GameProfile>(){

        @Override
        public GameProfile decode(ByteBuf byteBuf) {
            UUID uUID = (UUID)Uuids.PACKET_CODEC.decode(byteBuf);
            String string = StringEncoding.decode(byteBuf, 16);
            GameProfile gameProfile = new GameProfile(uUID, string);
            gameProfile.getProperties().putAll((Multimap)PROPERTY_MAP.decode(byteBuf));
            return gameProfile;
        }

        @Override
        public void encode(ByteBuf byteBuf, GameProfile gameProfile) {
            Uuids.PACKET_CODEC.encode(byteBuf, gameProfile.getId());
            StringEncoding.encode(byteBuf, gameProfile.getName(), 16);
            PROPERTY_MAP.encode(byteBuf, gameProfile.getProperties());
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (GameProfile)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public PacketCodec<ByteBuf, Integer> RGB = new PacketCodec<ByteBuf, Integer>(){

        @Override
        public Integer decode(ByteBuf byteBuf) {
            return ColorHelper.getArgb(byteBuf.readByte() & 0xFF, byteBuf.readByte() & 0xFF, byteBuf.readByte() & 0xFF);
        }

        @Override
        public void encode(ByteBuf byteBuf, Integer integer) {
            byteBuf.writeByte(ColorHelper.getRed(integer));
            byteBuf.writeByte(ColorHelper.getGreen(integer));
            byteBuf.writeByte(ColorHelper.getBlue(integer));
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (Integer)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };

    public static PacketCodec<ByteBuf, byte[]> byteArray(final int maxLength) {
        return new PacketCodec<ByteBuf, byte[]>(){

            @Override
            public byte[] decode(ByteBuf buf) {
                return PacketByteBuf.readByteArray(buf, maxLength);
            }

            @Override
            public void encode(ByteBuf buf, byte[] value) {
                if (value.length > maxLength) {
                    throw new EncoderException("ByteArray with size " + value.length + " is bigger than allowed " + maxLength);
                }
                PacketByteBuf.writeByteArray(buf, value);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((ByteBuf)object, (byte[])object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((ByteBuf)object);
            }
        };
    }

    public static PacketCodec<ByteBuf, String> string(final int maxLength) {
        return new PacketCodec<ByteBuf, String>(){

            @Override
            public String decode(ByteBuf byteBuf) {
                return StringEncoding.decode(byteBuf, maxLength);
            }

            @Override
            public void encode(ByteBuf byteBuf, String string) {
                StringEncoding.encode(byteBuf, string, maxLength);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((ByteBuf)object, (String)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((ByteBuf)object);
            }
        };
    }

    public static PacketCodec<ByteBuf, Optional<NbtElement>> nbtElement(final Supplier<NbtSizeTracker> sizeTrackerSupplier) {
        return new PacketCodec<ByteBuf, Optional<NbtElement>>(){

            @Override
            public Optional<NbtElement> decode(ByteBuf byteBuf) {
                return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf, (NbtSizeTracker)sizeTrackerSupplier.get()));
            }

            @Override
            public void encode(ByteBuf byteBuf, Optional<NbtElement> optional) {
                PacketByteBuf.writeNbt(byteBuf, optional.orElse(null));
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((ByteBuf)object, (Optional)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((ByteBuf)object);
            }
        };
    }

    public static PacketCodec<ByteBuf, NbtElement> nbt(final Supplier<NbtSizeTracker> sizeTracker) {
        return new PacketCodec<ByteBuf, NbtElement>(){

            @Override
            public NbtElement decode(ByteBuf byteBuf) {
                NbtElement nbtElement = PacketByteBuf.readNbt(byteBuf, (NbtSizeTracker)sizeTracker.get());
                if (nbtElement == null) {
                    throw new DecoderException("Expected non-null compound tag");
                }
                return nbtElement;
            }

            @Override
            public void encode(ByteBuf byteBuf, NbtElement nbtElement) {
                if (nbtElement == NbtEnd.INSTANCE) {
                    throw new EncoderException("Expected non-null compound tag");
                }
                PacketByteBuf.writeNbt(byteBuf, nbtElement);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((ByteBuf)object, (NbtElement)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((ByteBuf)object);
            }
        };
    }

    public static PacketCodec<ByteBuf, NbtCompound> nbtCompound(Supplier<NbtSizeTracker> sizeTracker) {
        return PacketCodecs.nbt(sizeTracker).xmap(nbt -> {
            if (nbt instanceof NbtCompound) {
                NbtCompound nbtCompound = (NbtCompound)nbt;
                return nbtCompound;
            }
            throw new DecoderException("Not a compound tag: " + String.valueOf(nbt));
        }, nbt -> nbt);
    }

    public static <T> PacketCodec<ByteBuf, T> unlimitedCodec(Codec<T> codec) {
        return PacketCodecs.codec(codec, NbtSizeTracker::ofUnlimitedBytes);
    }

    public static <T> PacketCodec<ByteBuf, T> codec(Codec<T> codec) {
        return PacketCodecs.codec(codec, () -> NbtSizeTracker.of(0x200000L));
    }

    public static <T, B extends ByteBuf, V> PacketCodec.ResultFunction<B, T, V> fromCodec(final DynamicOps<T> ops, final Codec<V> codec2) {
        return codec -> new PacketCodec<B, V>(){

            @Override
            public V decode(B byteBuf) {
                Object object = codec.decode(byteBuf);
                return codec2.parse(ops, object).getOrThrow(error -> new DecoderException("Failed to decode: " + error + " " + String.valueOf(object)));
            }

            @Override
            public void encode(B byteBuf, V object) {
                Object object2 = codec2.encodeStart(ops, object).getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + String.valueOf(object)));
                codec.encode(byteBuf, object2);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((B)((ByteBuf)object), (V)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <T> PacketCodec<ByteBuf, T> codec(Codec<T> codec, Supplier<NbtSizeTracker> sizeTracker) {
        return PacketCodecs.nbt(sizeTracker).collect(PacketCodecs.fromCodec(NbtOps.INSTANCE, codec));
    }

    public static <T> PacketCodec<RegistryByteBuf, T> unlimitedRegistryCodec(Codec<T> codec) {
        return PacketCodecs.registryCodec(codec, NbtSizeTracker::ofUnlimitedBytes);
    }

    public static <T> PacketCodec<RegistryByteBuf, T> registryCodec(Codec<T> codec) {
        return PacketCodecs.registryCodec(codec, () -> NbtSizeTracker.of(0x200000L));
    }

    public static <T> PacketCodec<RegistryByteBuf, T> registryCodec(final Codec<T> codec, Supplier<NbtSizeTracker> sizeTracker) {
        final PacketCodec<ByteBuf, NbtElement> packetCodec = PacketCodecs.nbt(sizeTracker);
        return new PacketCodec<RegistryByteBuf, T>(){

            @Override
            public T decode(RegistryByteBuf registryByteBuf) {
                NbtElement nbtElement = (NbtElement)packetCodec.decode(registryByteBuf);
                RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
                return codec.parse(registryOps, (Object)nbtElement).getOrThrow(error -> new DecoderException("Failed to decode: " + error + " " + String.valueOf(nbtElement)));
            }

            @Override
            public void encode(RegistryByteBuf registryByteBuf, T object) {
                RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
                NbtElement nbtElement = (NbtElement)codec.encodeStart(registryOps, object).getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + String.valueOf(object)));
                packetCodec.encode(registryByteBuf, nbtElement);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((RegistryByteBuf)((Object)object), object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((RegistryByteBuf)((Object)object));
            }
        };
    }

    public static <B extends ByteBuf, V> PacketCodec<B, Optional<V>> optional(final PacketCodec<B, V> codec) {
        return new PacketCodec<B, Optional<V>>(){

            @Override
            public Optional<V> decode(B byteBuf) {
                if (byteBuf.readBoolean()) {
                    return Optional.of(codec.decode(byteBuf));
                }
                return Optional.empty();
            }

            @Override
            public void encode(B byteBuf, Optional<V> optional) {
                if (optional.isPresent()) {
                    byteBuf.writeBoolean(true);
                    codec.encode(byteBuf, optional.get());
                } else {
                    byteBuf.writeBoolean(false);
                }
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((Object)((ByteBuf)object), (Optional)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static int readCollectionSize(ByteBuf buf, int maxSize) {
        int i = VarInts.read(buf);
        if (i > maxSize) {
            throw new DecoderException(i + " elements exceeded max size of: " + maxSize);
        }
        return i;
    }

    public static void writeCollectionSize(ByteBuf buf, int size, int maxSize) {
        if (size > maxSize) {
            throw new EncoderException(size + " elements exceeded max size of: " + maxSize);
        }
        VarInts.write(buf, size);
    }

    public static <B extends ByteBuf, V, C extends Collection<V>> PacketCodec<B, C> collection(IntFunction<C> factory, PacketCodec<? super B, V> elementCodec) {
        return PacketCodecs.collection(factory, elementCodec, Integer.MAX_VALUE);
    }

    public static <B extends ByteBuf, V, C extends Collection<V>> PacketCodec<B, C> collection(final IntFunction<C> factory, final PacketCodec<? super B, V> elementCodec, final int maxSize) {
        return new PacketCodec<B, C>(){

            @Override
            public C decode(B byteBuf) {
                int i = PacketCodecs.readCollectionSize(byteBuf, maxSize);
                Collection collection = (Collection)factory.apply(Math.min(i, 65536));
                for (int j = 0; j < i; ++j) {
                    collection.add(elementCodec.decode(byteBuf));
                }
                return collection;
            }

            @Override
            public void encode(B byteBuf, C collection) {
                PacketCodecs.writeCollectionSize(byteBuf, collection.size(), maxSize);
                for (Object object : collection) {
                    elementCodec.encode(byteBuf, object);
                }
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((B)((ByteBuf)object), (C)((Collection)object2));
            }

            @Override
            public Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <B extends ByteBuf, V, C extends Collection<V>> PacketCodec.ResultFunction<B, V, C> toCollection(IntFunction<C> collectionFactory) {
        return codec -> PacketCodecs.collection(collectionFactory, codec);
    }

    public static <B extends ByteBuf, V> PacketCodec.ResultFunction<B, V, List<V>> toList() {
        return codec -> PacketCodecs.collection(ArrayList::new, codec);
    }

    public static <B extends ByteBuf, V> PacketCodec.ResultFunction<B, V, List<V>> toList(int maxLength) {
        return codec -> PacketCodecs.collection(ArrayList::new, codec, maxLength);
    }

    public static <B extends ByteBuf, K, V, M extends Map<K, V>> PacketCodec<B, M> map(IntFunction<? extends M> factory, PacketCodec<? super B, K> keyCodec, PacketCodec<? super B, V> valueCodec) {
        return PacketCodecs.map(factory, keyCodec, valueCodec, Integer.MAX_VALUE);
    }

    public static <B extends ByteBuf, K, V, M extends Map<K, V>> PacketCodec<B, M> map(final IntFunction<? extends M> factory, final PacketCodec<? super B, K> keyCodec, final PacketCodec<? super B, V> valueCodec, final int maxSize) {
        return new PacketCodec<B, M>(){

            @Override
            public void encode(B byteBuf, M map) {
                PacketCodecs.writeCollectionSize(byteBuf, map.size(), maxSize);
                map.forEach((object, object2) -> {
                    keyCodec.encode(byteBuf, object);
                    valueCodec.encode(byteBuf, object2);
                });
            }

            @Override
            public M decode(B byteBuf) {
                int i = PacketCodecs.readCollectionSize(byteBuf, maxSize);
                Map map = (Map)factory.apply(Math.min(i, 65536));
                for (int j = 0; j < i; ++j) {
                    Object object = keyCodec.decode(byteBuf);
                    Object object2 = valueCodec.decode(byteBuf);
                    map.put(object, object2);
                }
                return map;
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((B)((ByteBuf)object), (M)((Map)object2));
            }

            @Override
            public Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <B extends ByteBuf, L, R> PacketCodec<B, Either<L, R>> either(final PacketCodec<? super B, L> left, final PacketCodec<? super B, R> right) {
        return new PacketCodec<B, Either<L, R>>(){

            @Override
            public Either<L, R> decode(B byteBuf) {
                if (byteBuf.readBoolean()) {
                    return Either.left(left.decode(byteBuf));
                }
                return Either.right(right.decode(byteBuf));
            }

            @Override
            public void encode(B byteBuf, Either<L, R> either) {
                either.ifLeft(value -> {
                    byteBuf.writeBoolean(true);
                    left.encode(byteBuf, value);
                }).ifRight(value -> {
                    byteBuf.writeBoolean(false);
                    right.encode(byteBuf, value);
                });
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((Object)((ByteBuf)object), (Either)((Either)object2));
            }

            @Override
            public Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <B extends ByteBuf, V> PacketCodec.ResultFunction<B, V, V> lengthPrepended(final int maxSize, final BiFunction<B, ByteBuf, B> bufWrapper) {
        return codec -> new PacketCodec<B, V>(){

            @Override
            public V decode(B byteBuf) {
                int i = VarInts.read(byteBuf);
                if (i > maxSize) {
                    throw new DecoderException("Buffer size " + i + " is larger than allowed limit of " + maxSize);
                }
                int j = byteBuf.readerIndex();
                ByteBuf byteBuf2 = (ByteBuf)bufWrapper.apply(byteBuf, byteBuf.slice(j, i));
                byteBuf.readerIndex(j + i);
                return codec.decode(byteBuf2);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void encode(B byteBuf, V object) {
                ByteBuf byteBuf2 = (ByteBuf)bufWrapper.apply(byteBuf, byteBuf.alloc().buffer());
                try {
                    codec.encode(byteBuf2, object);
                    int i = byteBuf2.readableBytes();
                    if (1 > maxSize) {
                        throw new EncoderException("Buffer size " + 1 + " is  larger than allowed limit of " + maxSize);
                    }
                    VarInts.write(byteBuf, 1);
                    byteBuf.writeBytes(byteBuf2);
                }
                catch (Throwable throwable) {
                    byteBuf2.release();
                    throw throwable;
                }
                byteBuf2.release();
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((B)((ByteBuf)object), (V)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((B)((ByteBuf)object));
            }
        };
    }

    public static <V> PacketCodec.ResultFunction<ByteBuf, V, V> lengthPrepended(int maxSize) {
        return PacketCodecs.lengthPrepended(maxSize, (byteBuf, bufToWrap) -> bufToWrap);
    }

    public static <V> PacketCodec.ResultFunction<RegistryByteBuf, V, V> lengthPrependedRegistry(int maxSize) {
        return PacketCodecs.lengthPrepended(maxSize, (registryByteBuf, byteBuf) -> new RegistryByteBuf((ByteBuf)byteBuf, registryByteBuf.getRegistryManager()));
    }

    public static <T> PacketCodec<ByteBuf, T> indexed(final IntFunction<T> indexToValue, final ToIntFunction<T> valueToIndex) {
        return new PacketCodec<ByteBuf, T>(){

            @Override
            public T decode(ByteBuf byteBuf) {
                int i = VarInts.read(byteBuf);
                return indexToValue.apply(i);
            }

            @Override
            public void encode(ByteBuf byteBuf, T object) {
                int i = valueToIndex.applyAsInt(object);
                VarInts.write(byteBuf, 1);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((ByteBuf)object, object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((ByteBuf)object);
            }
        };
    }

    public static <T> PacketCodec<ByteBuf, T> entryOf(IndexedIterable<T> iterable) {
        return PacketCodecs.indexed(iterable::getOrThrow, iterable::getRawIdOrThrow);
    }

    private static <T, R> PacketCodec<RegistryByteBuf, R> registry(final RegistryKey<? extends Registry<T>> registry, final Function<Registry<T>, IndexedIterable<R>> registryTransformer) {
        return new PacketCodec<RegistryByteBuf, R>(){

            private IndexedIterable<R> getRegistryOrThrow(RegistryByteBuf buf) {
                return (IndexedIterable)registryTransformer.apply(buf.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(registry));
            }

            @Override
            public R decode(RegistryByteBuf registryByteBuf) {
                int i = VarInts.read(registryByteBuf);
                return this.getRegistryOrThrow(registryByteBuf).getOrThrow(i);
            }

            @Override
            public void encode(RegistryByteBuf registryByteBuf, R object) {
                int i = this.getRegistryOrThrow(registryByteBuf).getRawIdOrThrow(object);
                VarInts.write(registryByteBuf, 1);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((RegistryByteBuf)((Object)object), object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((RegistryByteBuf)((Object)object));
            }
        };
    }

    public static <T> PacketCodec<RegistryByteBuf, T> registryValue(RegistryKey<? extends Registry<T>> registry2) {
        return PacketCodecs.registry(registry2, registry -> registry);
    }

    public static <T> PacketCodec<RegistryByteBuf, RegistryEntry<T>> registryEntry(RegistryKey<? extends Registry<T>> registry) {
        return PacketCodecs.registry(registry, Registry::getIndexedEntries);
    }

    public static <T> PacketCodec<RegistryByteBuf, RegistryEntry<T>> registryEntry(final RegistryKey<? extends Registry<T>> registry, final PacketCodec<? super RegistryByteBuf, T> directCodec) {
        return new PacketCodec<RegistryByteBuf, RegistryEntry<T>>(){
            final static private int field_61045 = 0;

            private IndexedIterable<RegistryEntry<T>> getIndexedEntries(RegistryByteBuf buf) {
                return buf.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(registry).getIndexedEntries();
            }

            @Override
            public RegistryEntry<T> decode(RegistryByteBuf registryByteBuf) {
                int i = VarInts.read(registryByteBuf);
                if (i == 0) {
                    return RegistryEntry.of(directCodec.decode(registryByteBuf));
                }
                return this.getIndexedEntries(registryByteBuf).getOrThrow(i - 1);
            }

            @Override
            public void encode(RegistryByteBuf registryByteBuf, RegistryEntry<T> registryEntry) {
                switch (registryEntry.getType()) {
                    case REFERENCE: {
                        int i = this.getIndexedEntries(registryByteBuf).getRawIdOrThrow(registryEntry);
                        VarInts.write(registryByteBuf, 2);
                        break;
                    }
                    case DIRECT: {
                        VarInts.write(registryByteBuf, 0);
                        directCodec.encode(registryByteBuf, registryEntry.value());
                    }
                }
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((RegistryByteBuf)((Object)object), (RegistryEntry)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((RegistryByteBuf)((Object)object));
            }
        };
    }

    public static <T> PacketCodec<RegistryByteBuf, RegistryEntryList<T>> registryEntryList(final RegistryKey<? extends Registry<T>> registryRef) {
        return new PacketCodec<RegistryByteBuf, RegistryEntryList<T>>(){
            final static private int field_61046 = -1;
            final private PacketCodec<RegistryByteBuf, RegistryEntry<T>> entryCodec;
            {
                this.entryCodec = PacketCodecs.registryEntry(registryRef);
            }

            @Override
            public RegistryEntryList<T> decode(RegistryByteBuf registryByteBuf) {
                int i = VarInts.read(registryByteBuf) - 1;
                if (i == -1) {
                    RegistryWrapper.Impl registry = registryByteBuf.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(registryRef);
                    return (RegistryEntryList)registry.getOptional(TagKey.of(registryRef, (Identifier)Identifier.PACKET_CODEC.decode(registryByteBuf))).orElseThrow();
                }
                ArrayList<RegistryEntry> list = new ArrayList<RegistryEntry>(Math.min(i, 65536));
                for (int j = 0; j < i; ++j) {
                    list.add((RegistryEntry)this.entryCodec.decode(registryByteBuf));
                }
                return RegistryEntryList.of(list);
            }

            @Override
            public void encode(RegistryByteBuf registryByteBuf, RegistryEntryList<T> registryEntryList) {
                Optional optional = registryEntryList.getTagKey();
                if (optional.isPresent()) {
                    VarInts.write(registryByteBuf, 0);
                    Identifier.PACKET_CODEC.encode(registryByteBuf, optional.get().id());
                } else {
                    VarInts.write(registryByteBuf, registryEntryList.size() + 1);
                    for (RegistryEntry registryEntry : registryEntryList) {
                        this.entryCodec.encode(registryByteBuf, registryEntry);
                    }
                }
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((RegistryByteBuf)((Object)object), (RegistryEntryList)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((RegistryByteBuf)((Object)object));
            }
        };
    }

    public static PacketCodec<ByteBuf, JsonElement> lenientJson(final int maxLength) {
        return new PacketCodec<ByteBuf, JsonElement>(){
            final static private Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

            @Override
            public JsonElement decode(ByteBuf byteBuf) {
                String string = StringEncoding.decode(byteBuf, maxLength);
                try {
                    return LenientJsonParser.parse(string);
                }
                catch (JsonSyntaxException jsonSyntaxException) {
                    throw new DecoderException("Failed to parse JSON", (Throwable)jsonSyntaxException);
                }
            }

            @Override
            public void encode(ByteBuf byteBuf, JsonElement jsonElement) {
                String string = GSON.toJson(jsonElement);
                StringEncoding.encode(byteBuf, string, maxLength);
            }

            @Override
            public void encode(Object object, Object object2) {
                this.encode((ByteBuf)object, (JsonElement)object2);
            }

            @Override
            public Object decode(Object object) {
                return this.decode((ByteBuf)object);
            }
        };
    }
}

