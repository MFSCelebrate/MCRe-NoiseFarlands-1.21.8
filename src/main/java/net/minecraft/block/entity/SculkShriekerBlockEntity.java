/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.OptionalInt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class SculkShriekerBlockEntity
extends BlockEntity
implements GameEventListener.Holder<Vibrations.VibrationListener>,
Vibrations {
    final static private int field_38750 = 10;
    final static private int WARDEN_SPAWN_TRIES = 20;
    final static private int WARDEN_SPAWN_HORIZONTAL_RANGE = 5;
    final static private int WARDEN_SPAWN_VERTICAL_RANGE = 6;
    final static private int DARKNESS_RANGE = 40;
    final static private int SHRIEK_DELAY = 90;
    final static private Int2ObjectMap<SoundEvent> WARNING_SOUNDS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), warningSounds -> {
        warningSounds.put(1, (Object)SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE);
        warningSounds.put(2, (Object)SoundEvents.ENTITY_WARDEN_NEARBY_CLOSER);
        warningSounds.put(3, (Object)SoundEvents.ENTITY_WARDEN_NEARBY_CLOSEST);
        warningSounds.put(4, (Object)SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY);
    });
    final static private int DEFAULT_WARNING_LEVEL = 0;
    private int warningLevel = 0;
    final private Vibrations.Callback vibrationCallback = new VibrationCallback();
    private Vibrations.ListenerData vibrationListenerData = new Vibrations.ListenerData();
    final private Vibrations.VibrationListener vibrationListener = new Vibrations.VibrationListener(this);

    public SculkShriekerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SCULK_SHRIEKER, pos, state);
    }

    @Override
    public Vibrations.ListenerData getVibrationListenerData() {
        return this.vibrationListenerData;
    }

    @Override
    public Vibrations.Callback getVibrationCallback() {
        return this.vibrationCallback;
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.warningLevel = view.getInt("warning_level", 0);
        this.vibrationListenerData = view.read("listener", Vibrations.ListenerData.CODEC).orElseGet(Vibrations.ListenerData::new);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("warning_level", this.warningLevel);
        view.put("listener", Vibrations.ListenerData.CODEC, this.vibrationListenerData);
    }

    @Nullable
    public static ServerPlayerEntity findResponsiblePlayerFromEntity(@Nullable Entity entity) {
        ItemEntity itemEntity;
        ServerPlayerEntity serverPlayerEntity2;
        ProjectileEntity projectileEntity;
        Entity entity2;
        LivingEntity livingEntity;
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            return serverPlayerEntity;
        }
        if (entity != null && (livingEntity = entity.getControllingPassenger()) instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
            return serverPlayerEntity;
        }
        if (entity instanceof ProjectileEntity && (entity2 = (projectileEntity = (ProjectileEntity)entity).net_minecraft_entity_Entity_getOwner()) instanceof ServerPlayerEntity) {
            serverPlayerEntity2 = (ServerPlayerEntity)entity2;
            return serverPlayerEntity2;
        }
        if (entity instanceof ItemEntity && (entity2 = (itemEntity = (ItemEntity)entity).net_minecraft_entity_Entity_getOwner()) instanceof ServerPlayerEntity) {
            serverPlayerEntity2 = (ServerPlayerEntity)entity2;
            return serverPlayerEntity2;
        }
        return null;
    }

    public void shriek(ServerWorld world, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return;
        }
        BlockState blockState = this.getCachedState();
        if (blockState.get(SculkShriekerBlock.SHRIEKING).booleanValue()) {
            return;
        }
        this.warningLevel = 0;
        if (this.canWarn(world) && !this.trySyncWarningLevel(world, player)) {
            return;
        }
        this.shriek(world, (Entity)player);
    }

    private boolean trySyncWarningLevel(ServerWorld world, ServerPlayerEntity player) {
        OptionalInt optionalInt = SculkShriekerWarningManager.warnNearbyPlayers(world, this.getPos(), player);
        optionalInt.ifPresent(warningLevel -> {
            this.warningLevel = warningLevel;
        });
        return optionalInt.isPresent();
    }

    private void shriek(ServerWorld world, @Nullable Entity entity) {
        BlockPos blockPos = this.getPos();
        BlockState blockState = this.getCachedState();
        world.setBlockState(blockPos, (BlockState)blockState.with(SculkShriekerBlock.SHRIEKING, true), Block.NOTIFY_LISTENERS);
        world.scheduleBlockTick(blockPos, blockState.getBlock(), 90);
        world.syncWorldEvent(WorldEvents.SCULK_SHRIEKS, blockPos, 0);
        world.emitGameEvent(GameEvent.SHRIEK, blockPos, GameEvent.Emitter.of(entity));
    }

    private boolean canWarn(ServerWorld world) {
        return this.getCachedState().get(SculkShriekerBlock.CAN_SUMMON) != false && world.getDifficulty() != Difficulty.PEACEFUL && world.getGameRules().getBoolean(GameRules.DO_WARDEN_SPAWNING);
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        World world;
        if (oldState.get(SculkShriekerBlock.SHRIEKING).booleanValue() && (world = this.world) instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.warn(serverWorld);
        }
    }

    public void warn(ServerWorld world) {
        if (this.canWarn(world) && this.warningLevel > 0) {
            if (!this.trySpawnWarden(world)) {
                this.playWarningSound(world);
            }
            WardenEntity.addDarknessToClosePlayers(world, Vec3d.ofCenter(this.getPos()), null, 40);
        }
    }

    private void playWarningSound(World world) {
        SoundEvent soundEvent = (SoundEvent)WARNING_SOUNDS.get(this.warningLevel);
        if (soundEvent != null) {
            BlockPos blockPos = this.getPos();
            int i = blockPos.getX() + MathHelper.nextBetween(world.random, -10, 10);
            int j = blockPos.getY() + MathHelper.nextBetween(world.random, -10, 10);
            int k = blockPos.getZ() + MathHelper.nextBetween(world.random, -10, 10);
            world.playSound(null, (double)i, (double)j, (double)k, soundEvent, SoundCategory.HOSTILE, 5.0f, 1.0f);
        }
    }

    private boolean trySpawnWarden(ServerWorld world) {
        if (this.warningLevel < 4) {
            return false;
        }
        return LargeEntitySpawnHelper.trySpawnAt(EntityType.WARDEN, SpawnReason.TRIGGERED, world, this.getPos(), 20, 5, 6, LargeEntitySpawnHelper.Requirements.WARDEN, false).isPresent();
    }

    @Override
    public Vibrations.VibrationListener net_minecraft_world_event_Vibrations$VibrationListener_getEventListener() {
        return this.vibrationListener;
    }

    @Override
    public GameEventListener net_minecraft_world_event_listener_GameEventListener_getEventListener() {
        return this.net_minecraft_world_event_Vibrations$VibrationListener_getEventListener();
    }

    class VibrationCallback
    implements Vibrations.Callback {
        final static private int RANGE = 8;
        final private PositionSource positionSource;

        public VibrationCallback() {
            this.positionSource = new BlockPositionSource(SculkShriekerBlockEntity.this.pos);
        }

        @Override
        public int getRange() {
            return 8;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public TagKey<GameEvent> getTag() {
            return GameEventTags.SHRIEKER_CAN_LISTEN;
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter) {
            return SculkShriekerBlockEntity.this.getCachedState().get(SculkShriekerBlock.SHRIEKING) == false && SculkShriekerBlockEntity.findResponsiblePlayerFromEntity(emitter.sourceEntity()) != null;
        }

        @Override
        public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            SculkShriekerBlockEntity.this.shriek(world, SculkShriekerBlockEntity.findResponsiblePlayerFromEntity(entity != null ? entity : sourceEntity));
        }

        @Override
        public void onListen() {
            SculkShriekerBlockEntity.this.markDirty();
        }

        @Override
        public boolean requiresTickingChunksAround() {
            return true;
        }
    }
}

