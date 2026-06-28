/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package net.minecraft.entity.data;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.PigVariant;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.WolfSoundVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerData;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TrackedDataHandlerRegistry {
    final static private Int2ObjectBiMap<TrackedDataHandler<?>> DATA_HANDLERS = Int2ObjectBiMap.create(16);
    final static public TrackedDataHandler<Byte> BYTE = TrackedDataHandler.create(PacketCodecs.BYTE);
    final static public TrackedDataHandler<Integer> INTEGER = TrackedDataHandler.create(PacketCodecs.VAR_INT);
    final static public TrackedDataHandler<Long> LONG = TrackedDataHandler.create(PacketCodecs.VAR_LONG);
    final static public TrackedDataHandler<Float> FLOAT = TrackedDataHandler.create(PacketCodecs.FLOAT);
    final static public TrackedDataHandler<String> STRING = TrackedDataHandler.create(PacketCodecs.STRING);
    final static public TrackedDataHandler<Text> TEXT_COMPONENT = TrackedDataHandler.create(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
    final static public TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = TrackedDataHandler.create(TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC);
    final static public TrackedDataHandler<ItemStack> ITEM_STACK = new TrackedDataHandler<ItemStack>(){

        @Override
        public PacketCodec<? super RegistryByteBuf, ItemStack> codec() {
            return ItemStack.OPTIONAL_PACKET_CODEC;
        }

        @Override
        public ItemStack copy(ItemStack itemStack) {
            return itemStack.copy();
        }

        @Override
        public Object copy(Object object) {
            return this.copy((ItemStack)object);
        }
    };
    final static public TrackedDataHandler<BlockState> BLOCK_STATE = TrackedDataHandler.create(PacketCodecs.entryOf(Block.STATE_IDS));
    final static private PacketCodec<ByteBuf, Optional<BlockState>> OPTIONAL_BLOCK_STATE_CODEC = new PacketCodec<ByteBuf, Optional<BlockState>>(){

        @Override
        public void encode(ByteBuf byteBuf, Optional<BlockState> optional) {
            if (optional.isPresent()) {
                VarInts.write(byteBuf, Block.getRawIdFromState(optional.get()));
            } else {
                VarInts.write(byteBuf, 0);
            }
        }

        @Override
        public Optional<BlockState> decode(ByteBuf byteBuf) {
            int i = VarInts.read(byteBuf);
            if (i == 0) {
                return Optional.empty();
            }
            return Optional.of(Block.getStateFromRawId(i));
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
    final static public TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = TrackedDataHandler.create(OPTIONAL_BLOCK_STATE_CODEC);
    final static public TrackedDataHandler<Boolean> BOOLEAN = TrackedDataHandler.create(PacketCodecs.BOOLEAN);
    final static public TrackedDataHandler<ParticleEffect> PARTICLE = TrackedDataHandler.create(ParticleTypes.PACKET_CODEC);
    final static public TrackedDataHandler<List<ParticleEffect>> PARTICLE_LIST = TrackedDataHandler.create(ParticleTypes.PACKET_CODEC.collect(PacketCodecs.toList()));
    final static public TrackedDataHandler<EulerAngle> ROTATION = TrackedDataHandler.create(EulerAngle.PACKET_CODEC);
    final static public TrackedDataHandler<BlockPos> BLOCK_POS = TrackedDataHandler.create(BlockPos.PACKET_CODEC);
    final static public TrackedDataHandler<Optional<BlockPos>> OPTIONAL_BLOCK_POS = TrackedDataHandler.create(BlockPos.PACKET_CODEC.collect(PacketCodecs::optional));
    final static public TrackedDataHandler<Direction> FACING = TrackedDataHandler.create(Direction.PACKET_CODEC);
    final static public TrackedDataHandler<Optional<LazyEntityReference<LivingEntity>>> LAZY_ENTITY_REFERENCE = TrackedDataHandler.create(LazyEntityReference.createPacketCodec().collect(PacketCodecs::optional));
    final static public TrackedDataHandler<Optional<GlobalPos>> OPTIONAL_GLOBAL_POS = TrackedDataHandler.create(GlobalPos.PACKET_CODEC.collect(PacketCodecs::optional));
    final static public TrackedDataHandler<NbtCompound> NBT_COMPOUND = new TrackedDataHandler<NbtCompound>(){

        @Override
        public PacketCodec<? super RegistryByteBuf, NbtCompound> codec() {
            return PacketCodecs.UNLIMITED_NBT_COMPOUND;
        }

        @Override
        public NbtCompound copy(NbtCompound nbtCompound) {
            return nbtCompound.net_minecraft_nbt_NbtCompound_copy();
        }

        @Override
        public Object copy(Object object) {
            return this.copy((NbtCompound)object);
        }
    };
    final static public TrackedDataHandler<VillagerData> VILLAGER_DATA = TrackedDataHandler.create(VillagerData.PACKET_CODEC);
    final static private PacketCodec<ByteBuf, OptionalInt> OPTIONAL_INT_CODEC = new PacketCodec<ByteBuf, OptionalInt>(){

        @Override
        public OptionalInt decode(ByteBuf byteBuf) {
            int i = VarInts.read(byteBuf);
            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        @Override
        public void encode(ByteBuf byteBuf, OptionalInt optionalInt) {
            VarInts.write(byteBuf, optionalInt.orElse(-1) + 1);
        }

        @Override
        public void encode(Object object, Object object2) {
            this.encode((ByteBuf)object, (OptionalInt)object2);
        }

        @Override
        public Object decode(Object object) {
            return this.decode((ByteBuf)object);
        }
    };
    final static public TrackedDataHandler<OptionalInt> OPTIONAL_INT = TrackedDataHandler.create(OPTIONAL_INT_CODEC);
    final static public TrackedDataHandler<EntityPose> ENTITY_POSE = TrackedDataHandler.create(EntityPose.PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<CatVariant>> CAT_VARIANT = TrackedDataHandler.create(CatVariant.PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<ChickenVariant>> CHICKEN_VARIANT = TrackedDataHandler.create(ChickenVariant.ENTRY_PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<CowVariant>> COW_VARIANT = TrackedDataHandler.create(CowVariant.ENTRY_PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<WolfVariant>> WOLF_VARIANT = TrackedDataHandler.create(WolfVariant.ENTRY_PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<WolfSoundVariant>> WOLF_SOUND_VARIANT = TrackedDataHandler.create(WolfSoundVariant.PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<FrogVariant>> FROG_VARIANT = TrackedDataHandler.create(FrogVariant.PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<PigVariant>> PIG_VARIANT = TrackedDataHandler.create(PigVariant.ENTRY_PACKET_CODEC);
    final static public TrackedDataHandler<RegistryEntry<PaintingVariant>> PAINTING_VARIANT = TrackedDataHandler.create(PaintingVariant.ENTRY_PACKET_CODEC);
    final static public TrackedDataHandler<ArmadilloEntity.State> ARMADILLO_STATE = TrackedDataHandler.create(ArmadilloEntity.State.PACKET_CODEC);
    final static public TrackedDataHandler<SnifferEntity.State> SNIFFER_STATE = TrackedDataHandler.create(SnifferEntity.State.PACKET_CODEC);
    final static public TrackedDataHandler<Vector3f> VECTOR_3F = TrackedDataHandler.create(PacketCodecs.VECTOR_3F);
    final static public TrackedDataHandler<Quaternionf> QUATERNION_F = TrackedDataHandler.create(PacketCodecs.QUATERNION_F);

    public static void register(TrackedDataHandler<?> handler) {
        DATA_HANDLERS.add(handler);
    }

    @Nullable
    public static TrackedDataHandler<?> get(int id) {
        return DATA_HANDLERS.get(id);
    }

    public static int getId(TrackedDataHandler<?> handler) {
        return DATA_HANDLERS.getRawId(handler);
    }

    private TrackedDataHandlerRegistry() {
    }

    static {
        TrackedDataHandlerRegistry.register(BYTE);
        TrackedDataHandlerRegistry.register(INTEGER);
        TrackedDataHandlerRegistry.register(LONG);
        TrackedDataHandlerRegistry.register(FLOAT);
        TrackedDataHandlerRegistry.register(STRING);
        TrackedDataHandlerRegistry.register(TEXT_COMPONENT);
        TrackedDataHandlerRegistry.register(OPTIONAL_TEXT_COMPONENT);
        TrackedDataHandlerRegistry.register(ITEM_STACK);
        TrackedDataHandlerRegistry.register(BOOLEAN);
        TrackedDataHandlerRegistry.register(ROTATION);
        TrackedDataHandlerRegistry.register(BLOCK_POS);
        TrackedDataHandlerRegistry.register(OPTIONAL_BLOCK_POS);
        TrackedDataHandlerRegistry.register(FACING);
        TrackedDataHandlerRegistry.register(LAZY_ENTITY_REFERENCE);
        TrackedDataHandlerRegistry.register(BLOCK_STATE);
        TrackedDataHandlerRegistry.register(OPTIONAL_BLOCK_STATE);
        TrackedDataHandlerRegistry.register(NBT_COMPOUND);
        TrackedDataHandlerRegistry.register(PARTICLE);
        TrackedDataHandlerRegistry.register(PARTICLE_LIST);
        TrackedDataHandlerRegistry.register(VILLAGER_DATA);
        TrackedDataHandlerRegistry.register(OPTIONAL_INT);
        TrackedDataHandlerRegistry.register(ENTITY_POSE);
        TrackedDataHandlerRegistry.register(CAT_VARIANT);
        TrackedDataHandlerRegistry.register(COW_VARIANT);
        TrackedDataHandlerRegistry.register(WOLF_VARIANT);
        TrackedDataHandlerRegistry.register(WOLF_SOUND_VARIANT);
        TrackedDataHandlerRegistry.register(FROG_VARIANT);
        TrackedDataHandlerRegistry.register(PIG_VARIANT);
        TrackedDataHandlerRegistry.register(CHICKEN_VARIANT);
        TrackedDataHandlerRegistry.register(OPTIONAL_GLOBAL_POS);
        TrackedDataHandlerRegistry.register(PAINTING_VARIANT);
        TrackedDataHandlerRegistry.register(SNIFFER_STATE);
        TrackedDataHandlerRegistry.register(ARMADILLO_STATE);
        TrackedDataHandlerRegistry.register(VECTOR_3F);
        TrackedDataHandlerRegistry.register(QUATERNION_F);
    }
}

