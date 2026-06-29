/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BeesComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BeehiveBlockEntity
extends BlockEntity {
    final static Logger LOGGER = LogUtils.getLogger();
    final static private String FLOWER_POS_KEY = "flower_pos";
    final static private String BEES_KEY = "bees";
    final static List<String> IRRELEVANT_BEE_NBT_KEYS = Arrays.asList("Air", "drop_chances", "equipment", "Brain", "CanPickUpLoot", "DeathTime", "fall_distance", "FallFlying", "Fire", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "sleeping_pos", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "hive_pos", "Passengers", "leash", "UUID");
    final static public int MAX_BEE_COUNT = 3;
    final static private int ANGERED_CANNOT_ENTER_HIVE_TICKS = 400;
    final static private int MIN_OCCUPATION_TICKS_WITH_NECTAR = 2400;
    final static public int MIN_OCCUPATION_TICKS_WITHOUT_NECTAR = 600;
    final private List<Bee> bees = Lists.newArrayList();
    @Nullable
    private BlockPos flowerPos;

    public BeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BEEHIVE, pos, state);
    }

    @Override
    public void markDirty() {
        if (this.isNearFire()) {
            this.angerBees(null, this.world.getBlockState(this.getPos()), BeeState.EMERGENCY);
        }
        super.markDirty();
    }

    public boolean isNearFire() {
        if (this.world == null) {
            return false;
        }
        for (BlockPos blockPos : BlockPos.iterate(this.pos.net_minecraft_util_math_BlockPos_add(-1, -1, -1), this.pos.net_minecraft_util_math_BlockPos_add(1, 1, 1))) {
            if (!(this.world.getBlockState(blockPos).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean hasNoBees() {
        return this.bees.isEmpty();
    }

    public boolean isFullOfBees() {
        return this.bees.size() == 3;
    }

    public void angerBees(@Nullable PlayerEntity player, BlockState state, BeeState beeState) {
        List<Entity> list = this.tryReleaseBee(state, beeState);
        if (player != null) {
            for (Entity entity : list) {
                if (!(entity instanceof BeeEntity)) continue;
                BeeEntity beeEntity = (BeeEntity)entity;
                if (!(player.getPos().squaredDistanceTo(entity.getPos()) <= 16.0)) continue;
                if (!this.isSmoked()) {
                    beeEntity.setTarget(player);
                    continue;
                }
                beeEntity.setCannotEnterHiveTicks(400);
            }
        }
    }

    private List<Entity> tryReleaseBee(BlockState state, BeeState beeState) {
        ArrayList list = Lists.newArrayList();
        this.bees.removeIf(bee -> BeehiveBlockEntity.releaseBee(this.world, this.pos, state, bee.createData(), list, beeState, this.flowerPos));
        if (!list.isEmpty()) {
            super.markDirty();
        }
        return list;
    }

    @Debug
    public int getBeeCount() {
        return this.bees.size();
    }

    public static int getHoneyLevel(BlockState state) {
        return state.get(BeehiveBlock.HONEY_LEVEL);
    }

    @Debug
    public boolean isSmoked() {
        return CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
    }

    public void tryEnterHive(BeeEntity entity) {
        if (this.bees.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.removeAllPassengers();
        entity.detachLeash();
        this.addBee(BeeData.of(entity));
        if (this.world != null) {
            if (entity.hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
                this.flowerPos = entity.getFlowerPos();
            }
            BlockPos blockPos = this.getPos();
            this.world.playSound(null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(entity, this.getCachedState()));
        }
        entity.discard();
        super.markDirty();
    }

    public void addBee(BeeData bee) {
        this.bees.add(new Bee(bee));
    }

    private static boolean releaseBee(World world, BlockPos pos, BlockState state, BeeData bee, @Nullable List<Entity> entities, BeeState beeState, @Nullable BlockPos flowerPos) {
        boolean bl;
        if (BeeEntity.isNightOrRaining(world) && beeState != BeeState.EMERGENCY) {
            return false;
        }
        Direction direction = state.get(BeehiveBlock.FACING);
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_offset(direction);
        boolean bl2 = bl = !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
        if (bl && beeState != BeeState.EMERGENCY) {
            return false;
        }
        Entity entity = bee.loadEntity(world, pos);
        if (entity != null) {
            if (entity instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity)entity;
                if (flowerPos != null && !beeEntity.hasFlower() && world.random.nextFloat() < 0.9f) {
                    beeEntity.setFlowerPos(flowerPos);
                }
                if (beeState == BeeState.HONEY_DELIVERED) {
                    int i;
                    beeEntity.onHoneyDelivered();
                    if (state.isIn(BlockTags.BEEHIVES, statex -> statex.contains(BeehiveBlock.HONEY_LEVEL)) && (i = BeehiveBlockEntity.getHoneyLevel(state)) < 5) {
                        int j;
                        int n = j = world.random.nextInt(100) == 0 ? 2 : 1;
                        if (i + j > 5) {
                            --j;
                        }
                        world.setBlockState(pos, (BlockState)state.with(BeehiveBlock.HONEY_LEVEL, i + j));
                    }
                }
                if (entities != null) {
                    entities.add(beeEntity);
                }
                float f = entity.getWidth();
                double d = bl ? 0.0 : 0.55 + (double)(f / 2.0f);
                double e = (double)pos.getX() + 0.5 + d * (double)direction.getOffsetX();
                double g = (double)pos.getY() + 0.5 - (double)(entity.getHeight() / 2.0f);
                double h = (double)pos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
                entity.refreshPositionAndAngles(e, g, h, entity.getYaw(), entity.getPitch());
            }
            world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, world.getBlockState(pos)));
            return world.spawnEntity(entity);
        }
        return false;
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    private static void tickBees(World world, BlockPos pos, BlockState state, List<Bee> bees, @Nullable BlockPos flowerPos) {
        boolean bl = false;
        Iterator<Bee> iterator = bees.iterator();
        while (iterator.hasNext()) {
            BeeState beeState;
            Bee bee = iterator.next();
            if (!bee.canExitHive()) continue;
            BeeState beeState2 = beeState = bee.hasNectar() ? BeeState.HONEY_DELIVERED : BeeState.BEE_RELEASED;
            if (!BeehiveBlockEntity.releaseBee(world, pos, state, bee.createData(), null, beeState, flowerPos)) continue;
            bl = true;
            iterator.remove();
        }
        if (bl) {
            BeehiveBlockEntity.markDirty(world, pos, state);
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BeehiveBlockEntity blockEntity) {
        BeehiveBlockEntity.tickBees(world, pos, state, blockEntity.bees, blockEntity.flowerPos);
        if (!blockEntity.bees.isEmpty() && world.getRandom().nextDouble() < 0.005) {
            double d = (double)pos.getX() + 0.5;
            double e = pos.getY();
            double f = (double)pos.getZ() + 0.5;
            world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        DebugInfoSender.sendBeehiveDebugData(world, pos, state, blockEntity);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.bees.clear();
        view.read(BEES_KEY, BeeData.LIST_CODEC).orElse(List.of()).forEach(this::addBee);
        this.flowerPos = view.read(FLOWER_POS_KEY, BlockPos.CODEC).orElse(null);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put(BEES_KEY, BeeData.LIST_CODEC, this.createBeesData());
        view.putNullable(FLOWER_POS_KEY, BlockPos.CODEC, this.flowerPos);
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.bees.clear();
        List<BeeData> list = components.getOrDefault(DataComponentTypes.BEES, BeesComponent.DEFAULT).bees();
        list.forEach(this::addBee);
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.BEES, new BeesComponent(this.createBeesData()));
    }

    @Override
    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove(BEES_KEY);
    }

    private List<BeeData> createBeesData() {
        return this.bees.stream().map(Bee::createData).toList();
    }

    public static final class BeeState
    extends Enum<BeeState> {
        final static public BeeState HONEY_DELIVERED = new BeeState();
        final static public BeeState BEE_RELEASED = new BeeState();
        final static public BeeState EMERGENCY = new BeeState();
        final static private BeeState[] field_20430;

        public static BeeState[] values() {
            return (BeeState[])field_20430.clone();
        }

        public static BeeState valueOf(String string) {
            return Enum.valueOf(BeeState.class, string);
        }

        private static BeeState[] method_36714() {
            return new BeeState[]{HONEY_DELIVERED, BEE_RELEASED, EMERGENCY};
        }

        static {
            field_20430 = BeeState.method_36714();
        }
    }

    public static final class BeeData
    extends Record {
        final NbtComponent entityData;
        final private int ticksInHive;
        final int minTicksInHive;
        final static public Codec<BeeData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NbtComponent.CODEC.optionalFieldOf("entity_data", (Object)NbtComponent.DEFAULT).forGetter(BeeData::entityData), (App)Codec.INT.fieldOf("ticks_in_hive").forGetter(BeeData::ticksInHive), (App)Codec.INT.fieldOf("min_ticks_in_hive").forGetter(BeeData::minTicksInHive)).apply((Applicative)instance, BeeData::new));
        final static public Codec<List<BeeData>> LIST_CODEC = CODEC.listOf();
        final static public PacketCodec<ByteBuf, BeeData> PACKET_CODEC = PacketCodec.tuple(NbtComponent.PACKET_CODEC, BeeData::entityData, PacketCodecs.VAR_INT, BeeData::ticksInHive, PacketCodecs.VAR_INT, BeeData::minTicksInHive, BeeData::new);

        public BeeData(NbtComponent nbtComponent, int i, int j) {
            this.entityData = nbtComponent;
            this.ticksInHive = i;
            this.minTicksInHive = j;
        }

        public static BeeData of(Entity entity) {
            ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);
            try {
                NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
                entity.saveData(nbtWriteView);
                IRRELEVANT_BEE_NBT_KEYS.forEach(nbtWriteView::remove);
                NbtCompound nbtCompound = nbtWriteView.getNbt();
                boolean bl = nbtCompound.getBoolean("HasNectar", false);
                BeeData beeData = new BeeData(NbtComponent.of(nbtCompound), 0, bl ? 2400 : 600);
                logging.close();
                return beeData;
            }
            catch (Throwable throwable) {
                try {
                    logging.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }

        public static BeeData create(int ticksInHive) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("id", Registries.ENTITY_TYPE.getId(EntityType.BEE).toString());
            return new BeeData(NbtComponent.of(nbtCompound), ticksInHive, 600);
        }

        @Nullable
        public Entity loadEntity(World world, BlockPos pos) {
            NbtCompound nbtCompound = this.entityData.copyNbt();
            IRRELEVANT_BEE_NBT_KEYS.forEach(nbtCompound::remove);
            Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, SpawnReason.LOAD, entity -> entity);
            if (entity2 == null || !entity2.getType().isIn(EntityTypeTags.BEEHIVE_INHABITORS)) {
                return null;
            }
            entity2.setNoGravity(true);
            if (entity2 instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity)entity2;
                beeEntity.setHivePos(pos);
                BeeData.tickEntity(this.ticksInHive, beeEntity);
            }
            return entity2;
        }

        private static void tickEntity(int ticksInHive, BeeEntity beeEntity) {
            int i = beeEntity.getBreedingAge();
            if (i < 0) {
                beeEntity.setBreedingAge(Math.min(0, i + ticksInHive));
            } else if (i > 0) {
                beeEntity.setBreedingAge(Math.max(0, i - ticksInHive));
            }
            beeEntity.setLoveTicks(Math.max(0, beeEntity.getLoveTicks() - ticksInHive));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BeeData.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BeeData.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BeeData.class, "entityData;ticksInHive;minTicksInHive", "entityData", "ticksInHive", "minTicksInHive"}, this, object);
        }

        public NbtComponent entityData() {
            return this.entityData;
        }

        public int ticksInHive() {
            return this.ticksInHive;
        }

        public int minTicksInHive() {
            return this.minTicksInHive;
        }
    }

    static class Bee {
        final private BeeData data;
        private int ticksInHive;

        Bee(BeeData data) {
            this.data = data;
            this.ticksInHive = data.ticksInHive();
        }

        public boolean canExitHive() {
            return this.ticksInHive++ > this.data.minTicksInHive;
        }

        public BeeData createData() {
            return new BeeData(this.data.entityData, this.ticksInHive, this.data.minTicksInHive);
        }

        public boolean hasNectar() {
            return this.data.entityData.getNbt().getBoolean("HasNectar", false);
        }
    }
}

