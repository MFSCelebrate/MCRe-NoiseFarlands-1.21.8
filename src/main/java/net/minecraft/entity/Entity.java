/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleListIterator
 *  it.unimi.dsi.fastutil.floats.FloatArraySet
 *  it.unimi.dsi.fastutil.floats.FloatArrays
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2DoubleMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.fabricmc.fabric.api.attachment.v1.AttachmentTarget
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.dimension.PortalManager;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.waypoint.ServerWaypoint;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Entity
implements DataTracked,
Nameable,
EntityLike,
ScoreHolder,
ComponentsAccess,
AttachmentTarget {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public String ID_KEY = "id";
    final static public String UUID_KEY = "UUID";
    final static public String PASSENGERS_KEY = "Passengers";
    final static public String CUSTOM_DATA_KEY = "data";
    final static public String POS_KEY = "Pos";
    final static public String MOTION_KEY = "Motion";
    final static public String ROTATION_KEY = "Rotation";
    final static public String PORTAL_COOLDOWN_KEY = "PortalCooldown";
    final static public String NO_GRAVITY_KEY = "NoGravity";
    final static public String AIR_KEY = "Air";
    final static public String ON_GROUND_KEY = "OnGround";
    final static public String FALL_DISTANCE_KEY = "fall_distance";
    final static public String FIRE_KEY = "Fire";
    final static public String SILENT_KEY = "Silent";
    final static public String GLOWING_KEY = "Glowing";
    final static public String INVULNERABLE_KEY = "Invulnerable";
    final static private AtomicInteger CURRENT_ID = new AtomicInteger();
    final static public int field_49791 = 0;
    final static public int MAX_RIDING_COOLDOWN = 60;
    final static public int DEFAULT_PORTAL_COOLDOWN = 300;
    final static public int MAX_COMMAND_TAGS = 1024;
    final static private Codec<List<String>> TAG_LIST_CODEC = Codec.STRING.sizeLimitedListOf(1024);
    final static public float field_44870 = 0.2f;
    final static public double field_44871 = 0.500001;
    final static public double field_44872 = 0.999999;
    final static public int DEFAULT_MIN_FREEZE_DAMAGE_TICKS = 140;
    final static public int FREEZING_DAMAGE_INTERVAL = 40;
    final static public int field_49073 = 3;
    final static private ImmutableList<Direction.Axis> X_THEN_Z = ImmutableList.of((Object)Direction.Axis.Y, (Object)Direction.Axis.X, (Object)Direction.Axis.Z);
    final static private ImmutableList<Direction.Axis> Z_THEN_X = ImmutableList.of((Object)Direction.Axis.Y, (Object)Direction.Axis.Z, (Object)Direction.Axis.X);
    final static private Box NULL_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    final static private double SPEED_IN_WATER = 0.014;
    final static private double SPEED_IN_LAVA_IN_NETHER = 0.007;
    final static private double SPEED_IN_LAVA = 0.0023333333333333335;
    static private double renderDistanceMultiplier = 1.0;
    final private EntityType<?> type;
    private boolean alwaysSyncAbsolute;
    private int id = CURRENT_ID.incrementAndGet();
    public boolean intersectionChecked;
    private ImmutableList<Entity> passengerList = ImmutableList.of();
    protected int ridingCooldown;
    @Nullable
    private Entity vehicle;
    private World world;
    public double lastX;
    public double lastY;
    public double lastZ;
    private Vec3d pos;
    private BlockPos blockPos;
    private ChunkPos chunkPos;
    private Vec3d velocity = Vec3d.ZERO;
    private float yaw;
    private float pitch;
    public float lastYaw;
    public float lastPitch;
    private Box boundingBox = NULL_BOX;
    private boolean onGround;
    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean groundCollision;
    public boolean collidedSoftly;
    public boolean velocityModified;
    protected Vec3d movementMultiplier = Vec3d.ZERO;
    @Nullable
    private RemovalReason removalReason;
    final static public float DEFAULT_FRICTION = 0.6f;
    final static public float MIN_RISING_BUBBLE_COLUMN_SPEED = 1.8f;
    public float distanceTraveled;
    public float speed;
    public double fallDistance;
    private float nextStepSoundDistance = 1.0f;
    public double lastRenderX;
    public double lastRenderY;
    public double lastRenderZ;
    public boolean noClip;
    final protected Random random = Random.create();
    public int age;
    private int fireTicks;
    protected boolean touchingWater;
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight = new Object2DoubleArrayMap(2);
    protected boolean submergedInWater;
    final private Set<TagKey<Fluid>> submergedFluidTag = new HashSet<TagKey<Fluid>>();
    public int timeUntilRegen;
    protected boolean firstUpdate = true;
    final protected DataTracker dataTracker;
    final static protected TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
    final static protected int ON_FIRE_FLAG_INDEX = 0;
    final static private int SNEAKING_FLAG_INDEX = 1;
    final static private int SPRINTING_FLAG_INDEX = 3;
    final static private int SWIMMING_FLAG_INDEX = 4;
    final static private int INVISIBLE_FLAG_INDEX = 5;
    final static protected int GLOWING_FLAG_INDEX = 6;
    final static protected int GLIDING_FLAG_INDEX = 7;
    final static private TrackedData<Integer> AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
    final static private TrackedData<Optional<Text>> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
    final static private TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    final static private TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    final static private TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    final static protected TrackedData<EntityPose> POSE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
    final static private TrackedData<Integer> FROZEN_TICKS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
    private EntityChangeListener changeListener = EntityChangeListener.NONE;
    final private TrackedPosition trackedPosition = new TrackedPosition();
    public boolean velocityDirty;
    @Nullable
    public PortalManager portalManager;
    private int portalCooldown;
    private boolean invulnerable;
    protected UUID uuid = MathHelper.randomUuid(this.random);
    protected String uuidString = this.uuid.toString();
    private boolean glowing;
    final private Set<String> commandTags = Sets.newHashSet();
    final private double[] pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
    private long pistonMovementTick;
    private EntityDimensions dimensions;
    private float standingEyeHeight;
    public boolean inPowderSnow;
    public boolean wasInPowderSnow;
    public Optional<BlockPos> supportingBlockPos = Optional.empty();
    private boolean forceUpdateSupportingBlockPos = false;
    private float lastChimeIntensity;
    private int lastChimeAge;
    private boolean hasVisualFire;
    @Nullable
    private BlockState stateAtPos = null;
    final static public int MAX_QUEUED_COLLISION_CHECKS = 100;
    final private ArrayDeque<QueuedCollisionCheck> queuedCollisionChecks = new ArrayDeque(100);
    final private List<QueuedCollisionCheck> currentlyCheckedCollisions = new ObjectArrayList();
    final private LongSet collidedBlockPositions = new LongOpenHashSet();
    final private EntityCollisionHandler.Impl collisionHandler = new EntityCollisionHandler.Impl();
    private NbtComponent customData = NbtComponent.DEFAULT;

    public Entity(EntityType<?> type, World world) {
        this.type = type;
        this.world = world;
        this.dimensions = type.getDimensions();
        this.pos = Vec3d.ZERO;
        this.blockPos = BlockPos.ORIGIN;
        this.chunkPos = ChunkPos.ORIGIN;
        DataTracker.Builder builder = new DataTracker.Builder(this);
        builder.add(FLAGS, 0);
        builder.add(AIR, this.getMaxAir());
        builder.add(NAME_VISIBLE, false);
        builder.add(CUSTOM_NAME, Optional.empty());
        builder.add(SILENT, false);
        builder.add(NO_GRAVITY, false);
        builder.add(POSE, EntityPose.STANDING);
        builder.add(FROZEN_TICKS, 0);
        this.initDataTracker(builder);
        this.dataTracker = builder.build();
        this.setPosition(0.0, 0.0, 0.0);
        this.standingEyeHeight = this.dimensions.eyeHeight();
    }

    public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
        VoxelShape voxelShape = state.getCollisionShape(this.net_minecraft_world_World_getWorld(), pos, ShapeContext.of(this)).offset(pos);
        return VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(this.getBoundingBox()), BooleanBiFunction.AND);
    }

    public int getTeamColorValue() {
        Team abstractTeam = this.getScoreboardTeam();
        if (abstractTeam != null && ((AbstractTeam)abstractTeam).getColor().getColorValue() != null) {
            return ((AbstractTeam)abstractTeam).getColor().getColorValue();
        }
        return 0xFFFFFF;
    }

    public boolean isSpectator() {
        return false;
    }

    public final void detach() {
        if (this.hasPassengers()) {
            this.removeAllPassengers();
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }

    public void updateTrackedPosition(double x, double y, double z) {
        this.trackedPosition.setPos(new Vec3d(x, y, z));
    }

    public TrackedPosition getTrackedPosition() {
        return this.trackedPosition;
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public boolean shouldAlwaysSyncAbsolute() {
        return this.alwaysSyncAbsolute;
    }

    public void setAlwaysSyncAbsolute(boolean alwaysSyncAbsolute) {
        this.alwaysSyncAbsolute = alwaysSyncAbsolute;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<String> getCommandTags() {
        return this.commandTags;
    }

    public boolean addCommandTag(String tag) {
        if (this.commandTags.size() >= 1024) {
            return false;
        }
        return this.commandTags.add(tag);
    }

    public boolean removeCommandTag(String tag) {
        return this.commandTags.remove(tag);
    }

    public void kill(ServerWorld world) {
        this.remove(RemovalReason.KILLED);
        this.emitGameEvent(GameEvent.ENTITY_DIE);
    }

    public final void discard() {
        this.remove(RemovalReason.DISCARDED);
    }

    protected abstract void initDataTracker(DataTracker.Builder var1);

    public DataTracker getDataTracker() {
        return this.dataTracker;
    }

    public boolean equals(Object o) {
        if (o instanceof Entity) {
            return ((Entity)o).id == this.id;
        }
        return false;
    }

    public int hashCode() {
        return this.id;
    }

    public void remove(RemovalReason reason) {
        this.setRemoved(reason);
    }

    public void onRemoved() {
    }

    public void onRemove(RemovalReason reason) {
    }

    public void setPose(EntityPose pose) {
        this.dataTracker.set(POSE, pose);
    }

    public EntityPose getPose() {
        return this.dataTracker.get(POSE);
    }

    public boolean isInPose(EntityPose pose) {
        return this.getPose() == pose;
    }

    public boolean isInRange(Entity entity, double radius) {
        return this.getPos().isInRange(entity.getPos(), radius);
    }

    public boolean isInRange(Entity entity, double horizontalRadius, double verticalRadius) {
        double d = entity.getX() - this.getX();
        double e = entity.getY() - this.getY();
        double f = entity.getZ() - this.getZ();
        return MathHelper.squaredHypot(d, f) < MathHelper.square(horizontalRadius) && MathHelper.square(e) < MathHelper.square(verticalRadius);
    }

    protected void setRotation(float yaw, float pitch) {
        this.setYaw(yaw % 360.0f);
        this.setPitch(pitch % 360.0f);
    }

    public final void setPosition(Vec3d pos) {
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public void setPosition(double x, double y, double z) {
        this.setPos(x, y, z);
        this.setBoundingBox(this.calculateBoundingBox());
    }

    protected final Box calculateBoundingBox() {
        return this.calculateDefaultBoundingBox(this.pos);
    }

    protected Box calculateDefaultBoundingBox(Vec3d pos) {
        return this.dimensions.getBoxAt(pos);
    }

    protected void refreshPosition() {
        this.setPosition(this.pos.x, this.pos.y, this.pos.z);
    }

    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        float f = (float)cursorDeltaY * 0.15f;
        float g = (float)cursorDeltaX * 0.15f;
        this.setPitch(this.getPitch() + f);
        this.setYaw(this.getYaw() + g);
        this.setPitch(MathHelper.clamp(this.getPitch(), -90.0f, 90.0f));
        this.lastPitch += f;
        this.lastYaw += g;
        this.lastPitch = MathHelper.clamp(this.lastPitch, -90.0f, 90.0f);
        if (this.vehicle != null) {
            this.vehicle.onPassengerLookAround(this);
        }
    }

    public void tick() {
        this.baseTick();
    }

    public void baseTick() {
        ServerWorld serverWorld;
        Profiler profiler = Profilers.get();
        profiler.push("entityBaseTick");
        this.stateAtPos = null;
        if (this.hasVehicle() && this.getVehicle().isRemoved()) {
            this.stopRiding();
        }
        if (this.ridingCooldown > 0) {
            --this.ridingCooldown;
        }
        this.tickPortalTeleportation();
        if (this.shouldSpawnSprintingParticles()) {
            this.spawnSprintingParticles();
        }
        this.wasInPowderSnow = this.inPowderSnow;
        this.inPowderSnow = false;
        this.updateWaterState();
        this.updateSubmergedInWaterState();
        this.updateSwimming();
        World world = this.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld) {
            serverWorld = (ServerWorld)world;
            if (this.fireTicks > 0) {
                if (this.isFireImmune()) {
                    this.setFireTicks(this.fireTicks - 4);
                } else {
                    if (this.fireTicks % 20 == 0 && !this.isInLava()) {
                        this.damage(serverWorld, this.getDamageSources().onFire(), 1.0f);
                    }
                    this.setFireTicks(this.fireTicks - 1);
                }
            }
        } else {
            this.extinguish();
        }
        if (this.isInLava()) {
            this.fallDistance *= 0.5;
        }
        this.attemptTickInVoid();
        if (!this.net_minecraft_world_World_getWorld().isClient) {
            this.setOnFire(this.fireTicks > 0);
        }
        this.firstUpdate = false;
        world = this.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld) {
            serverWorld = (ServerWorld)world;
            if (this instanceof Leashable) {
                Leashable.tickLeash(serverWorld, (Entity)((Object)((Leashable)((Object)this))));
            }
        }
        profiler.pop();
    }

    public void setOnFire(boolean onFire) {
        this.setFlag(ON_FIRE_FLAG_INDEX, onFire || this.hasVisualFire);
    }

    public void attemptTickInVoid() {
        if (this.getY() < (double)(this.net_minecraft_world_World_getWorld().getBottomY() - 64)) {
            this.tickInVoid();
        }
    }

    public void resetPortalCooldown() {
        this.portalCooldown = this.getDefaultPortalCooldown();
    }

    public void setPortalCooldown(int portalCooldown) {
        this.portalCooldown = portalCooldown;
    }

    public int getPortalCooldown() {
        return this.portalCooldown;
    }

    public boolean hasPortalCooldown() {
        return this.portalCooldown > 0;
    }

    protected void tickPortalCooldown() {
        if (this.hasPortalCooldown()) {
            --this.portalCooldown;
        }
    }

    public void igniteByLava() {
        if (this.isFireImmune()) {
            return;
        }
        this.setOnFireFor(15.0f);
    }

    public void setOnFireFromLava() {
        ServerWorld serverWorld;
        if (this.isFireImmune()) {
            return;
        }
        World world = this.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld && this.damage(serverWorld = (ServerWorld)world, this.getDamageSources().lava(), 4.0f) && this.shouldPlayBurnSoundInLava() && !this.isSilent()) {
            serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_BURN, this.getSoundCategory(), 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
        }
    }

    protected boolean shouldPlayBurnSoundInLava() {
        return true;
    }

    public final void setOnFireFor(float seconds) {
        this.setOnFireForTicks(MathHelper.floor(seconds * 20.0f));
    }

    public void setOnFireForTicks(int ticks) {
        if (this.fireTicks < ticks) {
            this.setFireTicks(ticks);
        }
        this.defrost();
    }

    public void setFireTicks(int fireTicks) {
        this.fireTicks = fireTicks;
    }

    public int getFireTicks() {
        return this.fireTicks;
    }

    public void extinguish() {
        this.setFireTicks(0);
    }

    protected void tickInVoid() {
        this.discard();
    }

    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return this.doesNotCollide(this.getBoundingBox().offset(offsetX, offsetY, offsetZ));
    }

    private boolean doesNotCollide(Box box) {
        return this.net_minecraft_world_World_getWorld().isSpaceEmpty(this, box) && !this.net_minecraft_world_World_getWorld().containsFluid(box);
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        this.updateSupportingBlockPos(onGround, null);
    }

    public void setMovement(boolean onGround, Vec3d movement) {
        this.setMovement(onGround, this.horizontalCollision, movement);
    }

    public void setMovement(boolean onGround, boolean horizontalCollision, Vec3d movement) {
        this.onGround = onGround;
        this.horizontalCollision = horizontalCollision;
        this.updateSupportingBlockPos(onGround, movement);
    }

    public boolean isSupportedBy(BlockPos pos) {
        return this.supportingBlockPos.isPresent() && this.supportingBlockPos.get().equals(pos);
    }

    protected void updateSupportingBlockPos(boolean onGround, @Nullable Vec3d movement) {
        if (onGround) {
            Box box = this.getBoundingBox();
            Box box2 = new Box(box.minX, box.minY - 1.0E-6, box.minZ, box.maxX, box.minY, box.maxZ);
            Optional optional = this.world.findSupportingBlockPos(this, box2);
            if (optional.isPresent() || this.forceUpdateSupportingBlockPos) {
                this.supportingBlockPos = optional;
            } else if (movement != null) {
                Box box3 = box2.offset(-movement.x, 0.0, -movement.z);
                this.supportingBlockPos = optional = this.world.findSupportingBlockPos(this, box3);
            }
            this.forceUpdateSupportingBlockPos = optional.isEmpty();
        } else {
            this.forceUpdateSupportingBlockPos = false;
            if (this.supportingBlockPos.isPresent()) {
                this.supportingBlockPos = Optional.empty();
            }
        }
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void move(MovementType type, Vec3d movement) {
        MoveEffect moveEffect;
        Vec3d vec3d;
        double d;
        if (this.noClip) {
            this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
            return;
        }
        if (type == MovementType.PISTON && (movement = this.adjustMovementForPiston(movement)).equals(Vec3d.ZERO)) {
            return;
        }
        Profiler profiler = Profilers.get();
        profiler.push("move");
        if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
            movement = movement.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
            this.setVelocity(Vec3d.ZERO);
        }
        if ((d = (vec3d = this.adjustMovementForCollisions(movement = this.adjustMovementForSneaking(movement, type))).lengthSquared()) > 1.0E-7 || movement.lengthSquared() - d < 1.0E-7) {
            BlockHitResult blockHitResult;
            if (this.fallDistance != 0.0 && d >= 1.0 && (blockHitResult = this.net_minecraft_world_World_getWorld().raycast(new RaycastContext(this.getPos(), this.getPos().add(vec3d), RaycastContext.ShapeType.FALLDAMAGE_RESETTING, RaycastContext.FluidHandling.WATER, this))).getType() != HitResult.Type.MISS) {
                this.onLanding();
            }
            Vec3d vec3d2 = this.getPos();
            Vec3d vec3d3 = vec3d2.add(vec3d);
            this.addQueuedCollisionChecks(new QueuedCollisionCheck(vec3d2, vec3d3, true));
            this.setPosition(vec3d3);
        }
        profiler.pop();
        profiler.push("rest");
        boolean bl = !MathHelper.approximatelyEquals(movement.x, vec3d.x);
        boolean bl2 = !MathHelper.approximatelyEquals(movement.z, vec3d.z);
        boolean bl3 = this.horizontalCollision = bl || bl2;
        if (Math.abs(movement.y) > 0.0 || this.isLogicalSideForUpdatingMovement()) {
            this.verticalCollision = movement.y != vec3d.y;
            this.groundCollision = this.verticalCollision && movement.y < 0.0;
            this.setMovement(this.groundCollision, this.horizontalCollision, vec3d);
        }
        this.collidedSoftly = this.horizontalCollision ? this.hasCollidedSoftly(vec3d) : false;
        BlockPos blockPos = this.getLandingPos();
        BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(blockPos);
        if (this.isLogicalSideForUpdatingMovement()) {
            this.fall(vec3d.y, this.isOnGround(), blockState, blockPos);
        }
        if (this.isRemoved()) {
            profiler.pop();
            return;
        }
        if (this.horizontalCollision) {
            Vec3d vec3d4 = this.getVelocity();
            this.setVelocity(bl ? 0.0 : vec3d4.x, vec3d4.y, bl2 ? 0.0 : vec3d4.z);
        }
        if (this.canMoveVoluntarily()) {
            Block block = blockState.getBlock();
            if (movement.y != vec3d.y) {
                block.onEntityLand(this.net_minecraft_world_World_getWorld(), this);
            }
        }
        if ((!this.net_minecraft_world_World_getWorld().isClient() || this.isLogicalSideForUpdatingMovement()) && (moveEffect = this.getMoveEffect()).hasAny() && !this.hasVehicle()) {
            this.applyMoveEffect(moveEffect, vec3d, blockPos, blockState);
        }
        float f = this.getVelocityMultiplier();
        this.setVelocity(this.getVelocity().multiply(f, 1.0, f));
        profiler.pop();
    }

    private void applyMoveEffect(MoveEffect moveEffect, Vec3d movement, BlockPos landingPos, BlockState landingState) {
        float f = 0.6f;
        float g = (float)(movement.length() * (double)0.6f);
        float h = (float)(movement.horizontalLength() * (double)0.6f);
        BlockPos blockPos = this.getSteppingPos();
        BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(blockPos);
        boolean bl = this.canClimb(blockState);
        this.distanceTraveled += bl ? g : h;
        this.speed += g;
        if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
            boolean bl2 = blockPos.equals(landingPos);
            boolean bl3 = this.stepOnBlock(landingPos, landingState, moveEffect.playsSounds(), bl2, movement);
            if (!bl2) {
                bl3 |= this.stepOnBlock(blockPos, blockState, false, moveEffect.emitsGameEvents(), movement);
            }
            if (bl3) {
                this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
            } else if (this.isTouchingWater()) {
                this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                if (moveEffect.playsSounds()) {
                    this.playSwimSound();
                }
                if (moveEffect.emitsGameEvents()) {
                    this.emitGameEvent(GameEvent.SWIM);
                }
            }
        } else if (blockState.isAir()) {
            this.addAirTravelEffects();
        }
    }

    protected void tickBlockCollision() {
        this.currentlyCheckedCollisions.clear();
        this.currentlyCheckedCollisions.addAll(this.queuedCollisionChecks);
        this.queuedCollisionChecks.clear();
        if (this.currentlyCheckedCollisions.isEmpty()) {
            this.currentlyCheckedCollisions.add(new QueuedCollisionCheck(this.getLastRenderPos(), this.getPos(), false));
        } else if (this.currentlyCheckedCollisions.getLast().to.squaredDistanceTo(this.getPos()) > 9.999999439624929E-11) {
            this.currentlyCheckedCollisions.add(new QueuedCollisionCheck(this.currentlyCheckedCollisions.getLast().to, this.getPos(), false));
        }
        this.tickBlockCollisions(this.currentlyCheckedCollisions);
    }

    private void addQueuedCollisionChecks(QueuedCollisionCheck queuedCollisionCheck) {
        if (this.queuedCollisionChecks.size() >= 100) {
            QueuedCollisionCheck queuedCollisionCheck2 = this.queuedCollisionChecks.removeFirst();
            QueuedCollisionCheck queuedCollisionCheck3 = this.queuedCollisionChecks.removeFirst();
            QueuedCollisionCheck queuedCollisionCheck4 = new QueuedCollisionCheck(queuedCollisionCheck2.from(), queuedCollisionCheck3.to(), false);
            this.queuedCollisionChecks.addFirst(queuedCollisionCheck4);
        }
        this.queuedCollisionChecks.add(queuedCollisionCheck);
    }

    public void popQueuedCollisionCheck() {
        if (!this.queuedCollisionChecks.isEmpty()) {
            this.queuedCollisionChecks.removeLast();
        }
    }

    protected void clearQueuedCollisionChecks() {
        this.queuedCollisionChecks.clear();
    }

    public void tickBlockCollision(Vec3d lastRenderPos, Vec3d pos) {
        this.tickBlockCollisions(List.of(new QueuedCollisionCheck(lastRenderPos, pos, false)));
    }

    private void tickBlockCollisions(List<QueuedCollisionCheck> checks) {
        boolean bl3;
        if (!this.shouldTickBlockCollision()) {
            return;
        }
        if (this.isOnGround()) {
            BlockPos blockPos = this.getLandingPos();
            BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(blockPos);
            blockState.getBlock().onSteppedOn(this.net_minecraft_world_World_getWorld(), blockPos, blockState, this);
        }
        boolean bl = this.isOnFire();
        boolean bl2 = this.shouldEscapePowderSnow();
        int i = this.getFireTicks();
        this.checkBlockCollisions(checks, this.collisionHandler);
        this.collisionHandler.runCallbacks(this);
        if (this.isBeingRainedOn()) {
            this.extinguish();
        }
        if (bl && !this.isOnFire() || bl2 && !this.shouldEscapePowderSnow()) {
            this.playExtinguishSound();
        }
        boolean bl4 = bl3 = this.getFireTicks() > i;
        if (!(this.world.isClient || this.isOnFire() || bl3)) {
            this.setFireTicks(-this.getBurningDuration());
        }
    }

    protected boolean shouldTickBlockCollision() {
        return !this.isRemoved() && !this.noClip;
    }

    private boolean canClimb(BlockState state) {
        return state.isIn(BlockTags.CLIMBABLE) || state.isOf(Blocks.POWDER_SNOW);
    }

    private boolean stepOnBlock(BlockPos pos, BlockState state, boolean playSound, boolean emitEvent, Vec3d movement) {
        if (state.isAir()) {
            return false;
        }
        boolean bl = this.canClimb(state);
        if ((this.isOnGround() || bl || this.isInSneakingPose() && movement.y == 0.0 || this.isOnRail()) && !this.isSwimming()) {
            if (playSound) {
                this.playStepSounds(pos, state);
            }
            if (emitEvent) {
                this.net_minecraft_world_World_getWorld().emitGameEvent(GameEvent.STEP, this.getPos(), GameEvent.Emitter.of(this, state));
            }
            return true;
        }
        return false;
    }

    protected boolean hasCollidedSoftly(Vec3d adjustedMovement) {
        return false;
    }

    protected void playExtinguishSound() {
        if (!this.world.isClient()) {
            this.net_minecraft_world_World_getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, this.getSoundCategory(), 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
    }

    public void extinguishWithSound() {
        if (this.isOnFire()) {
            this.playExtinguishSound();
        }
        this.extinguish();
    }

    protected void addAirTravelEffects() {
        if (this.isFlappingWings()) {
            this.addFlapEffects();
            if (this.getMoveEffect().emitsGameEvents()) {
                this.emitGameEvent(GameEvent.FLAP);
            }
        }
    }

    @Deprecated
    public BlockPos getLandingPos() {
        return this.getPosWithYOffset(0.2f);
    }

    public BlockPos getVelocityAffectingPos() {
        return this.getPosWithYOffset(0.500001f);
    }

    public BlockPos getSteppingPos() {
        return this.getPosWithYOffset(1.0E-5f);
    }

    protected BlockPos getPosWithYOffset(float offset) {
        if (this.supportingBlockPos.isPresent()) {
            BlockPos blockPos = this.supportingBlockPos.get();
            if (offset > 1.0E-5f) {
                BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(blockPos);
                if ((double)offset <= 0.5 && blockState.isIn(BlockTags.FENCES) || blockState.isIn(BlockTags.WALLS) || blockState.getBlock() instanceof FenceGateBlock) {
                    return blockPos;
                }
                return blockPos.withY(MathHelper.floor(this.pos.y - (double)offset));
            }
            return blockPos;
        }
        int i = MathHelper.floor(this.pos.x);
        int j = MathHelper.floor(this.pos.y - (double)offset);
        int k = MathHelper.floor(this.pos.z);
        return new BlockPos(i, j, k);
    }

    protected float getJumpVelocityMultiplier() {
        float f = this.net_minecraft_world_World_getWorld().getBlockState(this.getBlockPos()).getBlock().getJumpVelocityMultiplier();
        float g = this.net_minecraft_world_World_getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
        return (double)f == 1.0 ? g : f;
    }

    protected float getVelocityMultiplier() {
        BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(this.getBlockPos());
        float f = blockState.getBlock().getVelocityMultiplier();
        if (blockState.isOf(Blocks.WATER) || blockState.isOf(Blocks.BUBBLE_COLUMN)) {
            return f;
        }
        return (double)f == 1.0 ? this.net_minecraft_world_World_getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getVelocityMultiplier() : f;
    }

    protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
        return movement;
    }

    protected Vec3d adjustMovementForPiston(Vec3d movement) {
        if (movement.lengthSquared() <= 1.0E-7) {
            return movement;
        }
        long l = this.net_minecraft_world_World_getWorld().getTime();
        if (l != this.pistonMovementTick) {
            Arrays.fill(this.pistonMovementDelta, 0.0);
            this.pistonMovementTick = l;
        }
        if (movement.x != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.X, movement.x);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
        }
        if (movement.y != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.Y, movement.y);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, d, 0.0);
        }
        if (movement.z != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.Z, movement.z);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, 0.0, d);
        }
        return Vec3d.ZERO;
    }

    private double calculatePistonMovementFactor(Direction.Axis axis, double offsetFactor) {
        int i = axis.ordinal();
        double d = MathHelper.clamp(offsetFactor + this.pistonMovementDelta[i], -0.51, 0.51);
        offsetFactor = d - this.pistonMovementDelta[i];
        this.pistonMovementDelta[i] = d;
        return offsetFactor;
    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        boolean bl4;
        Box box = this.getBoundingBox();
        List<VoxelShape> list = this.net_minecraft_world_World_getWorld().getEntityCollisions(this, box.stretch(movement));
        Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(this, movement, box, this.net_minecraft_world_World_getWorld(), list);
        boolean bl = movement.x != vec3d.x;
        boolean bl2 = movement.y != vec3d.y;
        boolean bl3 = movement.z != vec3d.z;
        boolean bl5 = bl4 = bl2 && movement.y < 0.0;
        if (this.getStepHeight() > 0.0f && (bl4 || this.isOnGround()) && (bl || bl3)) {
            float[] fs;
            Box box2 = bl4 ? box.offset(0.0, vec3d.y, 0.0) : box;
            Box box3 = box2.stretch(movement.x, this.getStepHeight(), movement.z);
            if (!bl4) {
                box3 = box3.stretch(0.0, -1.0E-5f, 0.0);
            }
            List<VoxelShape> list2 = Entity.findCollisionsForMovement(this, this.world, list, box3);
            float f = (float)vec3d.y;
            for (float g : fs = Entity.collectStepHeights(box2, list2, this.getStepHeight(), f)) {
                Vec3d vec3d2 = Entity.adjustMovementForCollisions(new Vec3d(movement.x, g, movement.z), box2, list2);
                if (!(vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared())) continue;
                double d = box.minY - box2.minY;
                return vec3d2.subtract(0.0, d, 0.0);
            }
        }
        return vec3d;
    }

    private static float[] collectStepHeights(Box collisionBox, List<VoxelShape> collisions, float f, float stepHeight) {
        FloatArraySet floatSet = new FloatArraySet(4);
        block0: for (VoxelShape voxelShape : collisions) {
            DoubleList doubleList = voxelShape.getPointPositions(Direction.Axis.Y);
            DoubleListIterator doubleListIterator = doubleList.iterator();
            while (doubleListIterator.hasNext()) {
                double d = (Double)doubleListIterator.next();
                float g = (float)(d - collisionBox.minY);
                if (g < 0.0f || g == stepHeight) continue;
                if (g > f) continue block0;
                floatSet.add(g);
            }
        }
        float[] fs = floatSet.toFloatArray();
        FloatArrays.unstableSort((float[])fs);
        return fs;
    }

    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        List<VoxelShape> list = Entity.findCollisionsForMovement(entity, world, collisions, entityBoundingBox.stretch(movement));
        return Entity.adjustMovementForCollisions(movement, entityBoundingBox, list);
    }

    private static List<VoxelShape> findCollisionsForMovement(@Nullable Entity entity, World world, List<VoxelShape> regularCollisions, Box movingEntityBoundingBox) {
        boolean bl;
        ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)(regularCollisions.size() + 1));
        if (!regularCollisions.isEmpty()) {
            builder.addAll(regularCollisions);
        }
        WorldBorder worldBorder = world.getWorldBorder();
        boolean bl2 = bl = entity != null && worldBorder.canCollide(entity, movingEntityBoundingBox);
        if (bl) {
            builder.add((Object)worldBorder.asVoxelShape());
        }
        builder.addAll(world.getBlockCollisions(entity, movingEntityBoundingBox));
        return builder.build();
    }

    private static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, List<VoxelShape> collisions) {
        if (collisions.isEmpty()) {
            return movement;
        }
        Vec3d vec3d = Vec3d.ZERO;
        for (Direction.Axis axis : Entity.getAxisCheckOrder(movement)) {
            double d = movement.getComponentAlongAxis(axis);
            if (d == 0.0) continue;
            double e = VoxelShapes.calculateMaxOffset(axis, entityBoundingBox.offset(vec3d), collisions, d);
            vec3d = vec3d.withAxis(axis, e);
        }
        return vec3d;
    }

    private static Iterable<Direction.Axis> getAxisCheckOrder(Vec3d movement) {
        if (Math.abs(movement.x) < Math.abs(movement.z)) {
            return Z_THEN_X;
        }
        return X_THEN_Z;
    }

    protected float calculateNextStepSoundDistance() {
        return (int)this.distanceTraveled + 1;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_GENERIC_SWIM;
    }

    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_GENERIC_SPLASH;
    }

    protected SoundEvent getHighSpeedSplashSound() {
        return SoundEvents.ENTITY_GENERIC_SPLASH;
    }

    private void checkBlockCollisions(List<QueuedCollisionCheck> queuedCollisionChecks, EntityCollisionHandler.Impl collisionHandler) {
        if (!this.shouldTickBlockCollision()) {
            return;
        }
        LongSet longSet = this.collidedBlockPositions;
        for (QueuedCollisionCheck queuedCollisionCheck : queuedCollisionChecks) {
            Vec3d vec3d = queuedCollisionCheck.from;
            Vec3d vec3d2 = queuedCollisionCheck.to().subtract(queuedCollisionCheck.from());
            if (queuedCollisionCheck.axisIndependant && vec3d2.lengthSquared() > 0.0) {
                for (Direction.Axis axis : Entity.getAxisCheckOrder(vec3d2)) {
                    double d = vec3d2.getComponentAlongAxis(axis);
                    if (d == 0.0) continue;
                    Vec3d vec3d3 = vec3d.offset(axis.getPositiveDirection(), d);
                    this.checkBlockCollision(vec3d, vec3d3, collisionHandler, longSet);
                    vec3d = vec3d3;
                }
                continue;
            }
            this.checkBlockCollision(queuedCollisionCheck.from(), queuedCollisionCheck.to(), collisionHandler, longSet);
        }
        longSet.clear();
    }

    private void checkBlockCollision(Vec3d from, Vec3d to, EntityCollisionHandler.Impl collisionHandler, LongSet collidedBlockPositions) {
        Box box = this.calculateDefaultBoundingBox(to).contract(1.0E-5f);
        BlockView.collectCollisionsBetween(from, to, box, (pos, version) -> {
            boolean bl2;
            boolean bl;
            if (!this.isAlive()) {
                return false;
            }
            BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(pos);
            if (blockState.isAir()) {
                this.afterCollisionCheck(pos, false, false);
                return true;
            }
            if (!collidedBlockPositions.add(pos.asLong())) {
                return true;
            }
            VoxelShape voxelShape = blockState.getInsideCollisionShape(this.net_minecraft_world_World_getWorld(), pos, this);
            boolean bl3 = bl = voxelShape == VoxelShapes.fullCube() || this.collides(from, to, voxelShape.offset(new Vec3d(pos)).getBoundingBoxes());
            if (bl) {
                try {
                    collisionHandler.updateIfNecessary(version);
                    blockState.onEntityCollision(this.net_minecraft_world_World_getWorld(), pos, this, collisionHandler);
                    this.onBlockCollision(blockState);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Colliding entity with block");
                    CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
                    CrashReportSection.addBlockInfo(crashReportSection, this.net_minecraft_world_World_getWorld(), pos, blockState);
                    CrashReportSection crashReportSection2 = crashReport.addElement("Entity being checked for collision");
                    this.populateCrashReport(crashReportSection2);
                    throw new CrashException(crashReport);
                }
            }
            if (bl2 = this.collidesWithFluid(blockState.getFluidState(), pos, from, to)) {
                collisionHandler.updateIfNecessary(version);
                blockState.getFluidState().onEntityCollision(this.net_minecraft_world_World_getWorld(), pos, this, collisionHandler);
            }
            this.afterCollisionCheck(pos, bl, bl2);
            return true;
        });
    }

    private void afterCollisionCheck(BlockPos pos, boolean blockCollision, boolean fluidCollision) {
    }

    public boolean collidesWithFluid(FluidState state, BlockPos fluidPos, Vec3d oldPos, Vec3d newPos) {
        Box box = state.getCollisionBox(this.net_minecraft_world_World_getWorld(), fluidPos);
        return box != null && this.collides(oldPos, newPos, List.of(box));
    }

    public boolean collides(Vec3d oldPos, Vec3d newPos, List<Box> boxes) {
        Box box = this.calculateDefaultBoundingBox(oldPos);
        Vec3d vec3d = newPos.subtract(oldPos);
        return box.collides(vec3d, boxes);
    }

    protected void onBlockCollision(BlockState state) {
    }

    public BlockPos getWorldSpawnPos(ServerWorld world, BlockPos basePos) {
        BlockPos blockPos = world.getSpawnPos();
        Vec3d vec3d = blockPos.toCenterPos();
        int i = world.getWorldChunk(blockPos).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos.getX(), blockPos.getZ()) + 1;
        return BlockPos.ofFloored(vec3d.x, i, vec3d.z);
    }

    public void emitGameEvent(RegistryEntry<GameEvent> event, @Nullable Entity entity) {
        this.net_minecraft_world_World_getWorld().emitGameEvent(entity, event, this.pos);
    }

    public void emitGameEvent(RegistryEntry<GameEvent> event) {
        this.emitGameEvent(event, this);
    }

    private void playStepSounds(BlockPos pos, BlockState state) {
        this.playStepSound(pos, state);
        if (this.shouldPlayAmethystChimeSound(state)) {
            this.playAmethystChimeSound();
        }
    }

    protected void playSwimSound() {
        Entity entity = Objects.requireNonNullElse(this.getControllingPassenger(), this);
        float f = entity == this ? 0.35f : 0.4f;
        Vec3d vec3d = entity.getVelocity();
        float g = Math.min(1.0f, (float)Math.sqrt(vec3d.x * vec3d.x * (double)0.2f + vec3d.y * vec3d.y + vec3d.z * vec3d.z * (double)0.2f) * f);
        this.playSwimSound(g);
    }

    protected BlockPos getStepSoundPos(BlockPos pos) {
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_up();
        BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(blockPos);
        if (blockState.isIn(BlockTags.INSIDE_STEP_SOUND_BLOCKS) || blockState.isIn(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)) {
            return blockPos;
        }
        return pos;
    }

    protected void playCombinationStepSounds(BlockState primaryState, BlockState secondaryState) {
        BlockSoundGroup blockSoundGroup = primaryState.getSoundGroup();
        this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
        this.playSecondaryStepSound(secondaryState);
    }

    protected void playSecondaryStepSound(BlockState state) {
        BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.05f, blockSoundGroup.getPitch() * 0.8f);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
    }

    private boolean shouldPlayAmethystChimeSound(BlockState state) {
        return state.isIn(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.age >= this.lastChimeAge + 20;
    }

    private void playAmethystChimeSound() {
        this.lastChimeIntensity *= (float)Math.pow(0.997, this.age - this.lastChimeAge);
        this.lastChimeIntensity = Math.min(1.0f, this.lastChimeIntensity + 0.07f);
        float f = 0.5f + this.lastChimeIntensity * this.random.nextFloat() * 1.2f;
        float g = 0.1f + this.lastChimeIntensity * 1.2f;
        this.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, g, f);
        this.lastChimeAge = this.age;
    }

    protected void playSwimSound(float volume) {
        this.playSound(this.getSwimSound(), volume, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    protected void addFlapEffects() {
    }

    protected boolean isFlappingWings() {
        return false;
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!this.isSilent()) {
            this.net_minecraft_world_World_getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
        }
    }

    public void playSoundIfNotSilent(SoundEvent event) {
        if (!this.isSilent()) {
            this.playSound(event, 1.0f, 1.0f);
        }
    }

    public boolean isSilent() {
        return this.dataTracker.get(SILENT);
    }

    public void setSilent(boolean silent) {
        this.dataTracker.set(SILENT, silent);
    }

    public boolean hasNoGravity() {
        return this.dataTracker.get(NO_GRAVITY);
    }

    public void setNoGravity(boolean noGravity) {
        this.dataTracker.set(NO_GRAVITY, noGravity);
    }

    protected double getGravity() {
        return 0.0;
    }

    public final double getFinalGravity() {
        return this.hasNoGravity() ? 0.0 : this.getGravity();
    }

    protected void applyGravity() {
        double d = this.getFinalGravity();
        if (d != 0.0) {
            this.setVelocity(this.getVelocity().add(0.0, -d, 0.0));
        }
    }

    protected MoveEffect getMoveEffect() {
        return MoveEffect.ALL;
    }

    public boolean occludeVibrationSignals() {
        return false;
    }

    public final void handleFall(double xDifference, double yDifference, double zDifference, boolean onGround) {
        if (this.isRegionUnloaded()) {
            return;
        }
        this.updateSupportingBlockPos(onGround, new Vec3d(xDifference, yDifference, zDifference));
        BlockPos blockPos = this.getLandingPos();
        BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(blockPos);
        this.fall(yDifference, onGround, blockState, blockPos);
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        if (!this.isTouchingWater() && heightDifference < 0.0) {
            this.fallDistance -= (double)((float)heightDifference);
        }
        if (onGround) {
            if (this.fallDistance > 0.0) {
                state.getBlock().onLandedUpon(this.net_minecraft_world_World_getWorld(), state, landedPosition, this, this.fallDistance);
                this.net_minecraft_world_World_getWorld().emitGameEvent(GameEvent.HIT_GROUND, this.pos, GameEvent.Emitter.of(this, this.supportingBlockPos.map(pos -> this.net_minecraft_world_World_getWorld().getBlockState((BlockPos)pos)).orElse(state)));
            }
            this.onLanding();
        }
    }

    public boolean isFireImmune() {
        return this.getType().isFireImmune();
    }

    public boolean handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource) {
        if (this.type.isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
            return false;
        }
        this.handleFallDamageForPassengers(fallDistance, damagePerDistance, damageSource);
        return false;
    }

    protected void handleFallDamageForPassengers(double fallDistance, float damagePerDistance, DamageSource damageSource) {
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengerList()) {
                entity.handleFallDamage(fallDistance, damagePerDistance, damageSource);
            }
        }
    }

    public boolean isTouchingWater() {
        return this.touchingWater;
    }

    boolean isBeingRainedOn() {
        BlockPos blockPos = this.getBlockPos();
        return this.net_minecraft_world_World_getWorld().hasRain(blockPos) || this.net_minecraft_world_World_getWorld().hasRain(BlockPos.ofFloored(blockPos.getX(), this.getBoundingBox().maxY, blockPos.getZ()));
    }

    public boolean isTouchingWaterOrRain() {
        return this.isTouchingWater() || this.isBeingRainedOn();
    }

    public boolean isInFluid() {
        return this.isTouchingWater() || this.isInLava();
    }

    public boolean isSubmergedInWater() {
        return this.submergedInWater && this.isTouchingWater();
    }

    public boolean isAtCloudHeight() {
        Optional<Integer> optional = this.world.getDimension().cloudHeight();
        if (optional.isEmpty()) {
            return false;
        }
        int i = optional.get();
        if (this.getY() + (double)this.getHeight() < (double)i) {
            return false;
        }
        int j = i + 4;
        return this.getY() <= (double)j;
    }

    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isTouchingWater() && !this.hasVehicle());
        } else {
            this.setSwimming(this.isSprinting() && this.isSubmergedInWater() && !this.hasVehicle() && this.net_minecraft_world_World_getWorld().getFluidState(this.blockPos).isIn(FluidTags.WATER));
        }
    }

    protected boolean updateWaterState() {
        this.fluidHeight.clear();
        this.checkWaterState();
        double d = this.net_minecraft_world_World_getWorld().getDimension().ultrawarm() ? 0.007 : 0.0023333333333333335;
        boolean bl = this.updateMovementInFluid(FluidTags.LAVA, d);
        return this.isTouchingWater() || bl;
    }

    void checkWaterState() {
        AbstractBoatEntity abstractBoatEntity;
        Entity entity = this.getVehicle();
        if (entity instanceof AbstractBoatEntity && !(abstractBoatEntity = (AbstractBoatEntity)entity).isSubmergedInWater()) {
            this.touchingWater = false;
        } else if (this.updateMovementInFluid(FluidTags.WATER, 0.014)) {
            if (!this.touchingWater && !this.firstUpdate) {
                this.onSwimmingStart();
            }
            this.onLanding();
            this.touchingWater = true;
        } else {
            this.touchingWater = false;
        }
    }

    private void updateSubmergedInWaterState() {
        AbstractBoatEntity abstractBoatEntity;
        this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
        this.submergedFluidTag.clear();
        double d = this.getEyeY();
        Entity entity = this.getVehicle();
        if (entity instanceof AbstractBoatEntity && !(abstractBoatEntity = (AbstractBoatEntity)entity).isSubmergedInWater() && abstractBoatEntity.getBoundingBox().maxY >= d && abstractBoatEntity.getBoundingBox().minY <= d) {
            return;
        }
        BlockPos blockPos = BlockPos.ofFloored(this.getX(), d, this.getZ());
        FluidState fluidState = this.net_minecraft_world_World_getWorld().getFluidState(blockPos);
        double e = (float)blockPos.getY() + fluidState.getHeight(this.net_minecraft_world_World_getWorld(), blockPos);
        if (e > d) {
            fluidState.streamTags().forEach(this.submergedFluidTag::add);
        }
    }

    protected void onSwimmingStart() {
        double e;
        double d;
        Entity entity = Objects.requireNonNullElse(this.getControllingPassenger(), this);
        float f = entity == this ? 0.2f : 0.9f;
        Vec3d vec3d = entity.getVelocity();
        float g = Math.min(1.0f, (float)Math.sqrt(vec3d.x * vec3d.x * (double)0.2f + vec3d.y * vec3d.y + vec3d.z * vec3d.z * (double)0.2f) * f);
        if (g < 0.25f) {
            this.playSound(this.getSplashSound(), g, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        } else {
            this.playSound(this.getHighSpeedSplashSound(), g, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        float h = MathHelper.floor(this.getY());
        int i = 0;
        while ((float)i < 1.0f + this.dimensions.width() * 20.0f) {
            d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
            e = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
            this.net_minecraft_world_World_getWorld().addParticleClient(ParticleTypes.BUBBLE, this.getX() + d, h + 1.0f, this.getZ() + e, vec3d.x, vec3d.y - this.random.nextDouble() * (double)0.2f, vec3d.z);
            ++i;
        }
        i = 0;
        while ((float)i < 1.0f + this.dimensions.width() * 20.0f) {
            d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
            e = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width();
            this.net_minecraft_world_World_getWorld().addParticleClient(ParticleTypes.SPLASH, this.getX() + d, h + 1.0f, this.getZ() + e, vec3d.x, vec3d.y, vec3d.z);
            ++i;
        }
        this.emitGameEvent(GameEvent.SPLASH);
    }

    @Deprecated
    protected BlockState getLandingBlockState() {
        return this.net_minecraft_world_World_getWorld().getBlockState(this.getLandingPos());
    }

    public BlockState getSteppingBlockState() {
        return this.net_minecraft_world_World_getWorld().getBlockState(this.getSteppingPos());
    }

    public boolean shouldSpawnSprintingParticles() {
        return this.isSprinting() && !this.isTouchingWater() && !this.isSpectator() && !this.isInSneakingPose() && !this.isInLava() && this.isAlive();
    }

    protected void spawnSprintingParticles() {
        BlockPos blockPos = this.getLandingPos();
        BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState(blockPos);
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3d vec3d = this.getVelocity();
            BlockPos blockPos2 = this.getBlockPos();
            double d = this.getX() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width();
            double e = this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width();
            if (blockPos2.getX() != blockPos.getX()) {
                d = MathHelper.clamp(d, (double)blockPos.getX(), (double)blockPos.getX() + 1.0);
            }
            if (blockPos2.getZ() != blockPos.getZ()) {
                e = MathHelper.clamp(e, (double)blockPos.getZ(), (double)blockPos.getZ() + 1.0);
            }
            this.net_minecraft_world_World_getWorld().addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, this.getY() + 0.1, e, vec3d.x * -4.0, 1.5, vec3d.z * -4.0);
        }
    }

    public boolean isSubmergedIn(TagKey<Fluid> fluidTag) {
        return this.submergedFluidTag.contains(fluidTag);
    }

    public boolean isInLava() {
        return !this.firstUpdate && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
    }

    public void updateVelocity(float speed, Vec3d movementInput) {
        Vec3d vec3d = Entity.movementInputToVelocity(movementInput, speed, this.getYaw());
        this.setVelocity(this.getVelocity().add(vec3d));
    }

    protected static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
        float f = MathHelper.sin(yaw * ((float)Math.PI / 180));
        float g = MathHelper.cos(yaw * ((float)Math.PI / 180));
        return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
    }

    @Deprecated
    public float getBrightnessAtEyes() {
        if (this.net_minecraft_world_World_getWorld().isPosLoaded(this.getBlockX(), this.getBlockZ())) {
            return this.net_minecraft_world_World_getWorld().getBrightness(BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ()));
        }
        return 0.0f;
    }

    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.updatePosition(x, y, z);
        this.setAngles(yaw, pitch);
    }

    public void setAngles(float yaw, float pitch) {
        this.setYaw(yaw % 360.0f);
        this.setPitch(MathHelper.clamp(pitch, -90.0f, 90.0f) % 360.0f);
        this.lastYaw = this.getYaw();
        this.lastPitch = this.getPitch();
    }

    public void updatePosition(double x, double y, double z) {
        double d = MathHelper.clamp(x, -3.0E7, 3.0E7);
        double e = MathHelper.clamp(z, -3.0E7, 3.0E7);
        this.lastX = d;
        this.lastY = y;
        this.lastZ = e;
        this.setPosition(d, y, e);
    }

    public void refreshPositionAfterTeleport(Vec3d pos) {
        this.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
    }

    public void refreshPositionAfterTeleport(double x, double y, double z) {
        this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
    }

    public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
        this.refreshPositionAndAngles(pos.toBottomCenterPos(), yaw, pitch);
    }

    public void refreshPositionAndAngles(Vec3d pos, float yaw, float pitch) {
        this.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch);
    }

    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.setPos(x, y, z);
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.resetPosition();
        this.refreshPosition();
    }

    public final void resetPosition() {
        this.updateLastPosition();
        this.updateLastAngles();
    }

    public final void setLastPositionAndAngles(Vec3d pos, float yaw, float pitch) {
        this.setLastPosition(pos);
        this.setLastAngles(yaw, pitch);
    }

    protected void updateLastPosition() {
        this.setLastPosition(this.pos);
    }

    public void updateLastAngles() {
        this.setLastAngles(this.getYaw(), this.getPitch());
    }

    private void setLastPosition(Vec3d pos) {
        this.lastX = this.lastRenderX = pos.x;
        this.lastY = this.lastRenderY = pos.y;
        this.lastZ = this.lastRenderZ = pos.z;
    }

    private void setLastAngles(float lastYaw, float lastPitch) {
        this.lastYaw = lastYaw;
        this.lastPitch = lastPitch;
    }

    public final Vec3d getLastRenderPos() {
        return new Vec3d(this.lastRenderX, this.lastRenderY, this.lastRenderZ);
    }

    public float distanceTo(Entity entity) {
        float f = (float)(this.getX() - entity.getX());
        float g = (float)(this.getY() - entity.getY());
        float h = (float)(this.getZ() - entity.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public double squaredDistanceTo(double x, double y, double z) {
        double d = this.getX() - x;
        double e = this.getY() - y;
        double f = this.getZ() - z;
        return d * d + e * e + f * f;
    }

    public double squaredDistanceTo(Entity entity) {
        return this.squaredDistanceTo(entity.getPos());
    }

    public double squaredDistanceTo(Vec3d vector) {
        double d = this.getX() - vector.x;
        double e = this.getY() - vector.y;
        double f = this.getZ() - vector.z;
        return d * d + e * e + f * f;
    }

    public void onPlayerCollision(PlayerEntity player) {
    }

    public void pushAwayFrom(Entity entity) {
        double e;
        if (this.isConnectedThroughVehicle(entity)) {
            return;
        }
        if (entity.noClip || this.noClip) {
            return;
        }
        double d = entity.getX() - this.getX();
        double f = MathHelper.absMax(d, e = entity.getZ() - this.getZ());
        if (f >= (double)0.01f) {
            f = Math.sqrt(f);
            d /= f;
            e /= f;
            double g = 1.0 / f;
            if (g > 1.0) {
                g = 1.0;
            }
            d *= g;
            e *= g;
            d *= (double)0.05f;
            e *= (double)0.05f;
            if (!this.hasPassengers() && this.isPushable()) {
                this.addVelocity(-d, 0.0, -e);
            }
            if (!entity.hasPassengers() && entity.isPushable()) {
                entity.addVelocity(d, 0.0, e);
            }
        }
    }

    public void addVelocity(Vec3d velocity) {
        this.addVelocity(velocity.x, velocity.y, velocity.z);
    }

    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        this.setVelocity(this.getVelocity().add(deltaX, deltaY, deltaZ));
        this.velocityDirty = true;
    }

    protected void scheduleVelocityUpdate() {
        this.velocityModified = true;
    }

    @Deprecated
    public final void serverDamage(DamageSource source, float amount) {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.damage(serverWorld, source, amount);
        }
    }

    @Deprecated
    public final boolean sidedDamage(DamageSource source, float amount) {
        World world = this.world;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            return this.damage(serverWorld, source, amount);
        }
        return this.clientDamage(source);
    }

    public abstract boolean damage(ServerWorld var1, DamageSource var2, float var3);

    public boolean clientDamage(DamageSource source) {
        return false;
    }

    public final Vec3d getRotationVec(float tickProgress) {
        return this.getRotationVector(this.getPitch(tickProgress), this.getYaw(tickProgress));
    }

    public Direction getFacing() {
        return Direction.getFacing(this.getRotationVec(1.0f));
    }

    public float getPitch(float tickProgress) {
        return this.getLerpedPitch(tickProgress);
    }

    public float getYaw(float tickProgress) {
        return this.getLerpedYaw(tickProgress);
    }

    public float getLerpedPitch(float tickProgress) {
        if (tickProgress == 1.0f) {
            return this.getPitch();
        }
        return MathHelper.lerp(tickProgress, this.lastPitch, this.getPitch());
    }

    public float getLerpedYaw(float tickProgress) {
        if (tickProgress == 1.0f) {
            return this.getYaw();
        }
        return MathHelper.lerpAngleDegrees(tickProgress, this.lastYaw, this.getYaw());
    }

    public final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    public final Vec3d getOppositeRotationVector(float tickProgress) {
        return this.getOppositeRotationVector(this.getPitch(tickProgress), this.getYaw(tickProgress));
    }

    protected final Vec3d getOppositeRotationVector(float pitch, float yaw) {
        return this.getRotationVector(pitch - 90.0f, yaw);
    }

    public final Vec3d getEyePos() {
        return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
    }

    public final Vec3d getCameraPosVec(float tickProgress) {
        double d = MathHelper.lerp((double)tickProgress, this.lastX, this.getX());
        double e = MathHelper.lerp((double)tickProgress, this.lastY, this.getY()) + (double)this.getStandingEyeHeight();
        double f = MathHelper.lerp((double)tickProgress, this.lastZ, this.getZ());
        return new Vec3d(d, e, f);
    }

    public Vec3d getClientCameraPosVec(float tickProgress) {
        return this.getCameraPosVec(tickProgress);
    }

    public final Vec3d getLerpedPos(float deltaTicks) {
        double d = MathHelper.lerp((double)deltaTicks, this.lastX, this.getX());
        double e = MathHelper.lerp((double)deltaTicks, this.lastY, this.getY());
        double f = MathHelper.lerp((double)deltaTicks, this.lastZ, this.getZ());
        return new Vec3d(d, e, f);
    }

    public HitResult raycast(double maxDistance, float tickProgress, boolean includeFluids) {
        Vec3d vec3d = this.getCameraPosVec(tickProgress);
        Vec3d vec3d2 = this.getRotationVec(tickProgress);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        return this.net_minecraft_world_World_getWorld().raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, this));
    }

    public boolean canBeHitByProjectile() {
        return this.isAlive() && this.canHit();
    }

    public boolean canHit() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void updateKilledAdvancementCriterion(Entity entityKilled, DamageSource damageSource) {
        if (entityKilled instanceof ServerPlayerEntity) {
            Criteria.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)entityKilled, this, damageSource);
        }
    }

    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        double d = this.getX() - cameraX;
        double e = this.getY() - cameraY;
        double f = this.getZ() - cameraZ;
        double g = d * d + e * e + f * f;
        return this.shouldRender(g);
    }

    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        return distance < (d *= 64.0 * renderDistanceMultiplier) * d;
    }

    public boolean saveSelfData(WriteView view) {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        }
        String string = this.getSavedEntityId();
        if (string == null) {
            return false;
        }
        view.putString(ID_KEY, string);
        this.writeData(view);
        return true;
    }

    public boolean saveData(WriteView view) {
        if (this.hasVehicle()) {
            return false;
        }
        return this.saveSelfData(view);
    }

    public void writeData(WriteView view) {
        try {
            int i;
            if (this.vehicle != null) {
                view.put(POS_KEY, Vec3d.CODEC, new Vec3d(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
            } else {
                view.put(POS_KEY, Vec3d.CODEC, this.getPos());
            }
            view.put(MOTION_KEY, Vec3d.CODEC, this.getVelocity());
            view.put(ROTATION_KEY, Vec2f.CODEC, new Vec2f(this.getYaw(), this.getPitch()));
            view.putDouble(FALL_DISTANCE_KEY, this.fallDistance);
            view.putShort(FIRE_KEY, (short)this.fireTicks);
            view.putShort(AIR_KEY, (short)this.getAir());
            view.putBoolean(ON_GROUND_KEY, this.isOnGround());
            view.putBoolean(INVULNERABLE_KEY, this.invulnerable);
            view.putInt(PORTAL_COOLDOWN_KEY, this.portalCooldown);
            view.put(UUID_KEY, Uuids.INT_STREAM_CODEC, this.getUuid());
            view.putNullable("CustomName", TextCodecs.CODEC, this.getCustomName());
            if (this.isCustomNameVisible()) {
                view.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                view.putBoolean(SILENT_KEY, this.isSilent());
            }
            if (this.hasNoGravity()) {
                view.putBoolean(NO_GRAVITY_KEY, this.hasNoGravity());
            }
            if (this.glowing) {
                view.putBoolean(GLOWING_KEY, true);
            }
            if ((i = this.getFrozenTicks()) > 0) {
                view.putInt("TicksFrozen", this.getFrozenTicks());
            }
            if (this.hasVisualFire) {
                view.putBoolean("HasVisualFire", this.hasVisualFire);
            }
            if (!this.commandTags.isEmpty()) {
                view.put("Tags", TAG_LIST_CODEC, List.copyOf(this.commandTags));
            }
            if (!this.customData.isEmpty()) {
                view.put(CUSTOM_DATA_KEY, NbtComponent.CODEC, this.customData);
            }
            this.writeCustomData(view);
            if (this.hasPassengers()) {
                WriteView.ListView listView = view.getList(PASSENGERS_KEY);
                for (Entity entity : this.getPassengerList()) {
                    WriteView writeView;
                    if (entity.saveSelfData(writeView = listView.add())) continue;
                    listView.removeLast();
                }
                if (listView.isEmpty()) {
                    view.remove(PASSENGERS_KEY);
                }
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Saving entity NBT");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    public void readData(ReadView view) {
        try {
            Vec3d vec3d = view.read(POS_KEY, Vec3d.CODEC).orElse(Vec3d.ZERO);
            Vec3d vec3d2 = view.read(MOTION_KEY, Vec3d.CODEC).orElse(Vec3d.ZERO);
            Vec2f vec2f = view.read(ROTATION_KEY, Vec2f.CODEC).orElse(Vec2f.ZERO);
            this.setVelocity(Math.abs(vec3d2.x) > 10.0 ? 0.0 : vec3d2.x, Math.abs(vec3d2.y) > 10.0 ? 0.0 : vec3d2.y, Math.abs(vec3d2.z) > 10.0 ? 0.0 : vec3d2.z);
            this.velocityDirty = true;
            double d = 3.0000512E7;
            this.setPos(MathHelper.clamp(vec3d.x, -3.0000512E7, 3.0000512E7), MathHelper.clamp(vec3d.y, -2.0E7, 2.0E7), MathHelper.clamp(vec3d.z, -3.0000512E7, 3.0000512E7));
            this.setYaw(vec2f.x);
            this.setPitch(vec2f.y);
            this.resetPosition();
            this.setHeadYaw(this.getYaw());
            this.setBodyYaw(this.getYaw());
            this.fallDistance = view.getDouble(FALL_DISTANCE_KEY, 0.0);
            this.fireTicks = view.getShort(FIRE_KEY, 0);
            this.setAir(view.getInt(AIR_KEY, this.getMaxAir()));
            this.onGround = view.getBoolean(ON_GROUND_KEY, false);
            this.invulnerable = view.getBoolean(INVULNERABLE_KEY, false);
            this.portalCooldown = view.getInt(PORTAL_COOLDOWN_KEY, 0);
            view.read(UUID_KEY, Uuids.INT_STREAM_CODEC).ifPresent(uuid -> {
                this.uuid = uuid;
                this.uuidString = this.uuid.toString();
            });
            if (!(Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ()))) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite(this.getYaw()) || !Double.isFinite(this.getPitch())) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.refreshPosition();
            this.setRotation(this.getYaw(), this.getPitch());
            this.setCustomName(view.read("CustomName", TextCodecs.CODEC).orElse(null));
            this.setCustomNameVisible(view.getBoolean("CustomNameVisible", false));
            this.setSilent(view.getBoolean(SILENT_KEY, false));
            this.setNoGravity(view.getBoolean(NO_GRAVITY_KEY, false));
            this.setGlowing(view.getBoolean(GLOWING_KEY, false));
            this.setFrozenTicks(view.getInt("TicksFrozen", 0));
            this.hasVisualFire = view.getBoolean("HasVisualFire", false);
            this.customData = view.read(CUSTOM_DATA_KEY, NbtComponent.CODEC).orElse(NbtComponent.DEFAULT);
            this.commandTags.clear();
            view.read("Tags", TAG_LIST_CODEC).ifPresent(this.commandTags::addAll);
            this.readCustomData(view);
            if (this.shouldSetPositionOnLoad()) {
                this.refreshPosition();
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Loading entity NBT");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    protected boolean shouldSetPositionOnLoad() {
        return true;
    }

    @Nullable
    protected final String getSavedEntityId() {
        EntityType<?> entityType = this.getType();
        Identifier identifier = EntityType.getId(entityType);
        return !entityType.isSaveable() || identifier == null ? null : identifier.toString();
    }

    protected abstract void readCustomData(ReadView var1);

    protected abstract void writeCustomData(WriteView var1);

    @Nullable
    public ItemEntity dropItem(ServerWorld world, ItemConvertible item) {
        return this.dropItem(world, item, 0);
    }

    @Nullable
    public ItemEntity dropItem(ServerWorld world, ItemConvertible item, int offsetY) {
        return this.dropStack(world, new ItemStack(item), offsetY);
    }

    @Nullable
    public ItemEntity dropStack(ServerWorld world, ItemStack stack) {
        return this.dropStack(world, stack, 0.0f);
    }

    @Nullable
    public ItemEntity dropStack(ServerWorld world, ItemStack stack, Vec3d offset) {
        if (stack.isEmpty()) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(world, this.getX() + offset.x, this.getY() + offset.y, this.getZ() + offset.z, stack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
        return itemEntity;
    }

    @Nullable
    public ItemEntity dropStack(ServerWorld world, ItemStack stack, float yOffset) {
        return this.dropStack(world, stack, new Vec3d(0.0, yOffset, 0.0));
    }

    public boolean isAlive() {
        return !this.isRemoved();
    }

    public boolean isInsideWall() {
        if (this.noClip) {
            return false;
        }
        float f = this.dimensions.width() * 0.8f;
        Box box = Box.of(this.getEyePos(), f, 1.0E-6, f);
        return BlockPos.stream(box).anyMatch(pos -> {
            BlockState blockState = this.net_minecraft_world_World_getWorld().getBlockState((BlockPos)pos);
            return !blockState.isAir() && blockState.shouldSuffocate(this.net_minecraft_world_World_getWorld(), (BlockPos)pos) && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(this.net_minecraft_world_World_getWorld(), (BlockPos)pos).offset((Vec3i)pos), VoxelShapes.cuboid(box), BooleanBiFunction.AND);
        });
    }

    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack itemStack;
        Object list;
        LivingEntity livingEntity;
        Entity entity;
        Leashable leashable2;
        Entity entity2;
        if (!this.net_minecraft_world_World_getWorld().isClient && player.shouldCancelInteraction() && (entity2 = this) instanceof Leashable && (leashable2 = (Leashable)((Object)entity2)).canBeLeashed() && this.isAlive() && (!((entity = this) instanceof LivingEntity) || !(livingEntity = (LivingEntity)entity).isBaby()) && !(list = Leashable.collectLeashablesAround(this, leashable -> leashable.getLeashHolder() == player)).isEmpty()) {
            boolean bl = false;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                Leashable leashable22 = (Leashable)iterator.next();
                if (!leashable22.canBeLeashedTo(this)) continue;
                leashable22.attachLeash(this, true);
                bl = true;
            }
            if (bl) {
                this.net_minecraft_world_World_getWorld().emitGameEvent(GameEvent.ENTITY_ACTION, this.getBlockPos(), GameEvent.Emitter.of(player));
                this.playSoundIfNotSilent(SoundEvents.ITEM_LEAD_TIED);
                return ActionResult.SUCCESS_SERVER.noIncrementStat();
            }
        }
        if ((itemStack = player.getStackInHand(hand)).isOf(Items.SHEARS) && this.snipAllHeldLeashes(player)) {
            itemStack.damage(1, (LivingEntity)player, hand);
            return ActionResult.SUCCESS;
        }
        list = this;
        if (list instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)list;
            if (itemStack.isOf(Items.SHEARS) && mobEntity.canRemoveSaddle(player) && !player.shouldCancelInteraction() && this.shearEquipment(player, hand, itemStack, mobEntity)) {
                return ActionResult.SUCCESS;
            }
        }
        if (this.isAlive() && (list = this) instanceof Leashable) {
            Leashable leashable3 = (Leashable)list;
            if (leashable3.getLeashHolder() == player) {
                if (!this.net_minecraft_world_World_getWorld().isClient()) {
                    if (player.isInCreativeMode()) {
                        leashable3.detachLeashWithoutDrop();
                    } else {
                        leashable3.detachLeash();
                    }
                    this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
                    this.playSoundIfNotSilent(SoundEvents.ITEM_LEAD_UNTIED);
                }
                return ActionResult.SUCCESS.noIncrementStat();
            }
            ItemStack itemStack2 = player.getStackInHand(hand);
            if (itemStack2.isOf(Items.LEAD) && !(leashable3.getLeashHolder() instanceof PlayerEntity)) {
                if (!this.net_minecraft_world_World_getWorld().isClient() && leashable3.canBeLeashedTo(player)) {
                    if (leashable3.isLeashed()) {
                        leashable3.detachLeash();
                    }
                    leashable3.attachLeash(player, true);
                    this.playSoundIfNotSilent(SoundEvents.ITEM_LEAD_TIED);
                    itemStack2.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public boolean snipAllHeldLeashes(@Nullable PlayerEntity player) {
        World world;
        boolean bl = this.detachAllHeldLeashes(player);
        if (bl && (world = this.net_minecraft_world_World_getWorld()) instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            serverWorld.playSound(null, this.getBlockPos(), SoundEvents.ITEM_SHEARS_SNIP, player != null ? player.getSoundCategory() : this.getSoundCategory());
        }
        return bl;
    }

    public boolean detachAllHeldLeashes(@Nullable PlayerEntity player) {
        Leashable leashable;
        List<Leashable> list = Leashable.collectLeashablesHeldBy(this);
        boolean bl = !list.isEmpty();
        Entity entity = this;
        if (entity instanceof Leashable && (leashable = (Leashable)((Object)entity)).isLeashed()) {
            leashable.detachLeash();
            bl = true;
        }
        for (Leashable leashable2 : list) {
            leashable2.detachLeash();
        }
        if (bl) {
            this.emitGameEvent(GameEvent.SHEAR, player);
            return true;
        }
        return false;
    }

    private boolean shearEquipment(PlayerEntity player, Hand hand, ItemStack shears, MobEntity entity) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
            ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
            EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
            if (equippableComponent == null || !equippableComponent.canBeSheared() || EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) && !player.isCreative()) continue;
            shears.damage(1, (LivingEntity)player, LivingEntity.getSlotForHand(hand));
            Vec3d vec3d = this.dimensions.attachments().getPointOrDefault(EntityAttachmentType.PASSENGER);
            entity.equipLootStack(equipmentSlot, ItemStack.EMPTY);
            this.emitGameEvent(GameEvent.SHEAR, player);
            this.playSoundIfNotSilent(equippableComponent.shearingSound().value());
            World world = this.net_minecraft_world_World_getWorld();
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                this.dropStack(serverWorld, itemStack, vec3d);
                Criteria.PLAYER_SHEARED_EQUIPMENT.trigger((ServerPlayerEntity)player, itemStack, entity);
            }
            return true;
        }
        return false;
    }

    public boolean collidesWith(Entity other) {
        return other.isCollidable(this) && !this.isConnectedThroughVehicle(other);
    }

    public boolean isCollidable(@Nullable Entity entity) {
        return false;
    }

    public void tickRiding() {
        this.setVelocity(Vec3d.ZERO);
        this.tick();
        if (!this.hasVehicle()) {
            return;
        }
        this.getVehicle().updatePassengerPosition(this);
    }

    public final void updatePassengerPosition(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        this.updatePassengerPosition(passenger, Entity::setPosition);
    }

    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        Vec3d vec3d = this.getPassengerRidingPos(passenger);
        Vec3d vec3d2 = passenger.getVehicleAttachmentPos(this);
        positionUpdater.accept(passenger, vec3d.x - vec3d2.x, vec3d.y - vec3d2.y, vec3d.z - vec3d2.z);
    }

    public void onPassengerLookAround(Entity passenger) {
    }

    public Vec3d getVehicleAttachmentPos(Entity vehicle) {
        return this.getAttachments().getPoint(EntityAttachmentType.VEHICLE, 0, this.yaw);
    }

    public Vec3d getPassengerRidingPos(Entity passenger) {
        return this.getPos().add(this.getPassengerAttachmentPos(passenger, this.dimensions, 1.0f));
    }

    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return Entity.getPassengerAttachmentPos(this, passenger, dimensions.attachments());
    }

    protected static Vec3d getPassengerAttachmentPos(Entity vehicle, Entity passenger, EntityAttachments attachments) {
        int i = vehicle.getPassengerList().indexOf(passenger);
        return attachments.getPointOrDefault(EntityAttachmentType.PASSENGER, 1, vehicle.yaw);
    }

    public boolean startRiding(Entity entity) {
        return this.startRiding(entity, false);
    }

    public boolean isLiving() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity entity, boolean force) {
        if (entity == this.vehicle) {
            return false;
        }
        if (!entity.couldAcceptPassenger()) {
            return false;
        }
        if (!this.net_minecraft_world_World_getWorld().isClient() && !entity.type.isSaveable()) {
            return false;
        }
        Entity entity2 = entity;
        while (entity2.vehicle != null) {
            if (entity2.vehicle == this) {
                return false;
            }
            entity2 = entity2.vehicle;
        }
        if (!(force || this.canStartRiding(entity) && entity.canAddPassenger(this))) {
            return false;
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        this.setPose(EntityPose.STANDING);
        this.vehicle = entity;
        this.vehicle.addPassenger(this);
        entity.streamIntoPassengers().filter(passenger -> passenger instanceof ServerPlayerEntity).forEach(player -> Criteria.STARTED_RIDING.trigger((ServerPlayerEntity)player));
        return true;
    }

    protected boolean canStartRiding(Entity entity) {
        return !this.isSneaking() && this.ridingCooldown <= 0;
    }

    public void removeAllPassengers() {
        for (int i = this.passengerList.size() - 1; i >= 0; --i) {
            ((Entity)this.passengerList.get(i)).stopRiding();
        }
    }

    public void dismountVehicle() {
        if (this.vehicle != null) {
            Entity entity = this.vehicle;
            this.vehicle = null;
            entity.removePassenger(this);
        }
    }

    public void stopRiding() {
        this.dismountVehicle();
    }

    protected void addPassenger(Entity passenger) {
        if (passenger.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (this.passengerList.isEmpty()) {
            this.passengerList = ImmutableList.of((Object)passenger);
        } else {
            ArrayList list = Lists.newArrayList(this.passengerList);
            if (!this.net_minecraft_world_World_getWorld().isClient && passenger instanceof PlayerEntity && !(this.getFirstPassenger() instanceof PlayerEntity)) {
                list.add(0, passenger);
            } else {
                list.add(passenger);
            }
            this.passengerList = ImmutableList.copyOf((Collection)list);
        }
        this.emitGameEvent(GameEvent.ENTITY_MOUNT, passenger);
    }

    protected void removePassenger(Entity passenger) {
        if (passenger.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengerList = this.passengerList.size() == 1 && this.passengerList.get(0) == passenger ? ImmutableList.of() : (ImmutableList)this.passengerList.stream().filter(entity -> entity != passenger).collect(ImmutableList.toImmutableList());
        passenger.ridingCooldown = 60;
        this.emitGameEvent(GameEvent.ENTITY_DISMOUNT, passenger);
    }

    protected boolean canAddPassenger(Entity passenger) {
        return this.passengerList.isEmpty();
    }

    protected boolean couldAcceptPassenger() {
        return true;
    }

    public final boolean isInterpolating() {
        return this.getInterpolator() != null && this.getInterpolator().isInterpolating();
    }

    public final void updateTrackedPositionAndAngles(Vec3d pos, float yaw, float pitch) {
        PositionInterpolator positionInterpolator = this.getInterpolator();
        if (positionInterpolator != null) {
            positionInterpolator.refreshPositionAndAngles(pos, yaw, pitch);
        } else {
            this.setPosition(pos);
            this.setRotation(yaw, pitch);
        }
    }

    @Nullable
    public PositionInterpolator getInterpolator() {
        return null;
    }

    public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
        this.setHeadYaw(yaw);
    }

    public float getTargetingMargin() {
        return 0.0f;
    }

    public Vec3d getRotationVector() {
        return this.getRotationVector(this.getPitch(), this.getYaw());
    }

    public Vec3d getHandPosOffset(Item item) {
        Entity entity = this;
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            boolean bl = playerEntity.getOffHandStack().isOf(item) && !playerEntity.getMainHandStack().isOf(item);
            Arm arm = bl ? playerEntity.getMainArm().getOpposite() : playerEntity.getMainArm();
            return this.getRotationVector(0.0f, this.getYaw() + (float)(arm == Arm.RIGHT ? 80 : -80)).multiply(0.5);
        }
        return Vec3d.ZERO;
    }

    public Vec2f getRotationClient() {
        return new Vec2f(this.getPitch(), this.getYaw());
    }

    public Vec3d getRotationVecClient() {
        return Vec3d.fromPolar(this.getRotationClient());
    }

    public void tryUsePortal(Portal portal, BlockPos pos) {
        if (this.hasPortalCooldown()) {
            this.resetPortalCooldown();
            return;
        }
        if (this.portalManager == null || !this.portalManager.portalMatches(portal)) {
            this.portalManager = new PortalManager(portal, pos.toImmutable());
        } else if (!this.portalManager.isInPortal()) {
            this.portalManager.setPortalPos(pos.toImmutable());
            this.portalManager.setInPortal(true);
        }
    }

    protected void tickPortalTeleportation() {
        World world = this.net_minecraft_world_World_getWorld();
        if (!(world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        this.tickPortalCooldown();
        if (this.portalManager == null) {
            return;
        }
        if (this.portalManager.tick(serverWorld, this, this.canUsePortals(false))) {
            Profiler profiler = Profilers.get();
            profiler.push("portal");
            this.resetPortalCooldown();
            TeleportTarget teleportTarget = this.portalManager.createTeleportTarget(serverWorld, this);
            if (teleportTarget != null) {
                ServerWorld serverWorld2 = teleportTarget.world();
                if (serverWorld.getServer().isWorldAllowed(serverWorld2) && (serverWorld2.getRegistryKey() == serverWorld.getRegistryKey() || this.canTeleportBetween(serverWorld, serverWorld2))) {
                    this.net_minecraft_entity_Entity_teleportTo(teleportTarget);
                }
            }
            profiler.pop();
        } else if (this.portalManager.hasExpired()) {
            this.portalManager = null;
        }
    }

    public int getDefaultPortalCooldown() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof ServerPlayerEntity ? entity.getDefaultPortalCooldown() : 300;
    }

    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
    }

    public void onDamaged(DamageSource damageSource) {
    }

    public void handleStatus(byte status) {
        switch (status) {
            case 53: {
                HoneyBlock.addRegularParticles(this);
            }
        }
    }

    public void animateDamage(float yaw) {
    }

    public boolean isOnFire() {
        boolean bl = this.net_minecraft_world_World_getWorld() != null && this.net_minecraft_world_World_getWorld().isClient;
        return !this.isFireImmune() && (this.fireTicks > 0 || bl && this.getFlag(ON_FIRE_FLAG_INDEX));
    }

    public boolean hasVehicle() {
        return this.getVehicle() != null;
    }

    public boolean hasPassengers() {
        return !this.passengerList.isEmpty();
    }

    public boolean shouldDismountUnderwater() {
        return this.getType().isIn(EntityTypeTags.DISMOUNTS_UNDERWATER);
    }

    public boolean shouldControlVehicles() {
        return !this.getType().isIn(EntityTypeTags.NON_CONTROLLING_RIDER);
    }

    public void setSneaking(boolean sneaking) {
        this.setFlag(SNEAKING_FLAG_INDEX, sneaking);
    }

    public boolean isSneaking() {
        return this.getFlag(SNEAKING_FLAG_INDEX);
    }

    public boolean bypassesSteppingEffects() {
        return this.isSneaking();
    }

    public boolean bypassesLandingEffects() {
        return this.isSneaking();
    }

    public boolean isSneaky() {
        return this.isSneaking();
    }

    public boolean isDescending() {
        return this.isSneaking();
    }

    public boolean isInSneakingPose() {
        return this.isInPose(EntityPose.CROUCHING);
    }

    public boolean isSprinting() {
        return this.getFlag(SPRINTING_FLAG_INDEX);
    }

    public void setSprinting(boolean sprinting) {
        this.setFlag(SPRINTING_FLAG_INDEX, sprinting);
    }

    public boolean isSwimming() {
        return this.getFlag(SWIMMING_FLAG_INDEX);
    }

    public boolean isInSwimmingPose() {
        return this.isInPose(EntityPose.SWIMMING);
    }

    public boolean isCrawling() {
        return this.isInSwimmingPose() && !this.isTouchingWater();
    }

    public void setSwimming(boolean swimming) {
        this.setFlag(SWIMMING_FLAG_INDEX, swimming);
    }

    public final boolean isGlowingLocal() {
        return this.glowing;
    }

    public final void setGlowing(boolean glowing) {
        this.glowing = glowing;
        this.setFlag(GLOWING_FLAG_INDEX, this.isGlowing());
    }

    public boolean isGlowing() {
        if (this.net_minecraft_world_World_getWorld().isClient()) {
            return this.getFlag(GLOWING_FLAG_INDEX);
        }
        return this.glowing;
    }

    public boolean isInvisible() {
        return this.getFlag(INVISIBLE_FLAG_INDEX);
    }

    public boolean isInvisibleTo(PlayerEntity player) {
        if (player.isSpectator()) {
            return false;
        }
        Team abstractTeam = this.getScoreboardTeam();
        if (abstractTeam != null && player != null && player.getScoreboardTeam() == abstractTeam && ((AbstractTeam)abstractTeam).shouldShowFriendlyInvisibles()) {
            return false;
        }
        return this.isInvisible();
    }

    public boolean isOnRail() {
        return false;
    }

    public void updateEventHandler(BiConsumer<EntityGameEventHandler<?>, ServerWorld> callback) {
    }

    @Nullable
    public Team getScoreboardTeam() {
        return this.net_minecraft_world_World_getWorld().net_minecraft_scoreboard_Scoreboard_getScoreboard().getScoreHolderTeam(this.getNameForScoreboard());
    }

    public final boolean isTeammate(@Nullable Entity other) {
        if (other == null) {
            return false;
        }
        return this == other || this.isInSameTeam(other) || other.isInSameTeam(this);
    }

    protected boolean isInSameTeam(Entity other) {
        return this.isTeamPlayer(other.getScoreboardTeam());
    }

    public boolean isTeamPlayer(@Nullable AbstractTeam team) {
        if (this.getScoreboardTeam() != null) {
            return this.getScoreboardTeam().isEqual(team);
        }
        return false;
    }

    public void setInvisible(boolean invisible) {
        this.setFlag(INVISIBLE_FLAG_INDEX, invisible);
    }

    protected boolean getFlag(int index) {
        return (this.dataTracker.get(FLAGS) & 1 << index) != 0;
    }

    protected void setFlag(int index, boolean value) {
        byte b = this.dataTracker.get(FLAGS);
        if (value) {
            this.dataTracker.set(FLAGS, (byte)(b | 1 << index));
        } else {
            this.dataTracker.set(FLAGS, (byte)(b & ~(1 << index)));
        }
    }

    public int getMaxAir() {
        return 300;
    }

    public int getAir() {
        return this.dataTracker.get(AIR);
    }

    public void setAir(int air) {
        this.dataTracker.set(AIR, air);
    }

    public void defrost() {
        this.setFrozenTicks(0);
    }

    public int getFrozenTicks() {
        return this.dataTracker.get(FROZEN_TICKS);
    }

    public void setFrozenTicks(int frozenTicks) {
        this.dataTracker.set(FROZEN_TICKS, frozenTicks);
    }

    public float getFreezingScale() {
        int i = this.getMinFreezeDamageTicks();
        return (float)Math.min(this.getFrozenTicks(), i) / (float)i;
    }

    public boolean isFrozen() {
        return this.getFrozenTicks() >= this.getMinFreezeDamageTicks();
    }

    public int getMinFreezeDamageTicks() {
        return 140;
    }

    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        this.setFireTicks(this.fireTicks + 1);
        if (this.fireTicks == 0) {
            this.setOnFireFor(8.0f);
        }
        this.damage(world, this.getDamageSources().lightningBolt(), 5.0f);
    }

    public void onBubbleColumnSurfaceCollision(boolean drag, BlockPos pos) {
        Entity.applyBubbleColumnSurfaceEffects(this, drag, pos);
    }

    protected static void applyBubbleColumnSurfaceEffects(Entity entity, boolean drag, BlockPos pos) {
        Vec3d vec3d = entity.getVelocity();
        double d = drag ? Math.max(-0.9, vec3d.y - 0.03) : Math.min(1.8, vec3d.y + 0.1);
        entity.setVelocity(vec3d.x, d, vec3d.z);
        Entity.spawnBubbleColumnParticles(entity.world, pos);
    }

    protected static void spawnBubbleColumnParticles(World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            for (int i = 0; 1 < 2; ++i) {
                serverWorld.spawnParticles(ParticleTypes.SPLASH, (double)pos.getX() + world.random.nextDouble(), pos.getY() + 1, (double)pos.getZ() + world.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                serverWorld.spawnParticles(ParticleTypes.BUBBLE, (double)pos.getX() + world.random.nextDouble(), pos.getY() + 1, (double)pos.getZ() + world.random.nextDouble(), 1, 0.0, 0.01, 0.0, 0.2);
            }
        }
    }

    public void onBubbleColumnCollision(boolean drag) {
        Entity.applyBubbleColumnEffects(this, drag);
    }

    protected static void applyBubbleColumnEffects(Entity entity, boolean drag) {
        Vec3d vec3d = entity.getVelocity();
        double d = drag ? Math.max(-0.3, vec3d.y - 0.03) : Math.min(0.7, vec3d.y + 0.06);
        entity.setVelocity(vec3d.x, d, vec3d.z);
        entity.onLanding();
    }

    public boolean onKilledOther(ServerWorld world, LivingEntity other) {
        return true;
    }

    public void limitFallDistance() {
        if (this.getVelocity().getY() > -0.5 && this.fallDistance > 1.0) {
            this.fallDistance = 1.0;
        }
    }

    public void onLanding() {
        this.fallDistance = 0.0;
    }

    protected void pushOutOfBlocks(double x, double y, double z) {
        BlockPos blockPos = BlockPos.ofFloored(x, y, z);
        Vec3d vec3d = new Vec3d(x - (double)blockPos.getX(), y - (double)blockPos.getY(), z - (double)blockPos.getZ());
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Direction direction = Direction.UP;
        double d = Double.MAX_VALUE;
        for (Direction direction2 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            double f;
            mutable.set((Vec3i)blockPos, direction2);
            if (this.net_minecraft_world_World_getWorld().getBlockState(mutable).isFullCube(this.net_minecraft_world_World_getWorld(), mutable)) continue;
            double e = vec3d.getComponentAlongAxis(direction2.getAxis());
            double d2 = f = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - e : e;
            if (!(f < d)) continue;
            d = f;
            direction = direction2;
        }
        float g = this.random.nextFloat() * 0.2f + 0.1f;
        float h = direction.getDirection().offset();
        Vec3d vec3d2 = this.getVelocity().multiply(0.75);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setVelocity(h * g, vec3d2.y, vec3d2.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setVelocity(vec3d2.x, h * g, vec3d2.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setVelocity(vec3d2.x, vec3d2.y, h * g);
        }
    }

    public void slowMovement(BlockState state, Vec3d multiplier) {
        this.onLanding();
        this.movementMultiplier = multiplier;
    }

    private static Text removeClickEvents(Text textComponent) {
        MutableText mutableText = textComponent.copyContentOnly().setStyle(textComponent.getStyle().withClickEvent(null));
        for (Text text : textComponent.getSiblings()) {
            mutableText.append(Entity.removeClickEvents(text));
        }
        return mutableText;
    }

    @Override
    public Text getName() {
        Text text = this.getCustomName();
        if (text != null) {
            return Entity.removeClickEvents(text);
        }
        return this.getDefaultName();
    }

    protected Text getDefaultName() {
        return this.type.getName();
    }

    public boolean isPartOf(Entity entity) {
        return this == entity;
    }

    public float getHeadYaw() {
        return 0.0f;
    }

    public void setHeadYaw(float headYaw) {
    }

    public void setBodyYaw(float bodyYaw) {
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean handleAttack(Entity attacker) {
        return false;
    }

    public String toString() {
        String string;
        String string2 = string = this.net_minecraft_world_World_getWorld() == null ? "~NULL~" : this.net_minecraft_world_World_getWorld().toString();
        if (this.removalReason != null) {
            return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, removed=%s]", new Object[]{this.getClass().getSimpleName(), this.getName().getString(), this.id, string, this.getX(), this.getY(), this.getZ(), this.removalReason});
        }
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.id, string, this.getX(), this.getY(), this.getZ());
    }

    protected final boolean isAlwaysInvulnerableTo(DamageSource damageSource) {
        return this.isRemoved() || this.invulnerable && !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.isSourceCreativePlayer() || damageSource.isIn(DamageTypeTags.IS_FIRE) && this.isFireImmune() || damageSource.isIn(DamageTypeTags.IS_FALL) && this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public void copyPositionAndRotation(Entity entity) {
        this.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
    }

    public void copyFrom(Entity original) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getErrorReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, original.getRegistryManager());
            original.writeData(nbtWriteView);
            this.readData(NbtReadView.create(logging, this.getRegistryManager(), nbtWriteView.getNbt()));
        }
        this.portalCooldown = original.portalCooldown;
        this.portalManager = original.portalManager;
    }

    @Nullable
    public Entity net_minecraft_entity_Entity_teleportTo(TeleportTarget teleportTarget) {
        boolean bl;
        ServerWorld serverWorld;
        block6: {
            block5: {
                World world = this.net_minecraft_world_World_getWorld();
                if (!(world instanceof ServerWorld)) break block5;
                serverWorld = (ServerWorld)world;
                if (!this.isRemoved()) break block6;
            }
            return null;
        }
        ServerWorld serverWorld2 = teleportTarget.world();
        boolean bl2 = bl = serverWorld2.getRegistryKey() != serverWorld.getRegistryKey();
        if (!teleportTarget.boolean_asPassenger()) {
            this.stopRiding();
        }
        if (bl) {
            return this.teleportCrossDimension(serverWorld, serverWorld2, teleportTarget);
        }
        return this.teleportSameDimension(serverWorld, teleportTarget);
    }

    private Entity teleportSameDimension(ServerWorld world, TeleportTarget teleportTarget) {
        for (Entity entity : this.getPassengerList()) {
            entity.net_minecraft_entity_Entity_teleportTo(this.getPassengerTeleportTarget(teleportTarget, entity));
        }
        Profiler profiler = Profilers.get();
        profiler.push("teleportSameDimension");
        this.setPosition(PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives());
        if (!teleportTarget.boolean_asPassenger()) {
            this.sendTeleportPacket(teleportTarget);
        }
        teleportTarget.postTeleportTransition().onTransition(this);
        profiler.pop();
        return this;
    }

    private Entity teleportCrossDimension(ServerWorld from, ServerWorld to, TeleportTarget teleportTarget) {
        Entity entity2;
        List<Entity> list = this.getPassengerList();
        ArrayList<Entity> list2 = new ArrayList<Entity>(list.size());
        this.removeAllPassengers();
        for (Entity entity2 : list) {
            Entity entity22 = entity2.net_minecraft_entity_Entity_teleportTo(this.getPassengerTeleportTarget(teleportTarget, entity2));
            if (entity22 == null) continue;
            list2.add(entity22);
        }
        Profiler profiler = Profilers.get();
        profiler.push("teleportCrossDimension");
        entity2 = this.getType().create(to, SpawnReason.DIMENSION_TRAVEL);
        if (entity2 == null) {
            profiler.pop();
            return null;
        }
        entity2.copyFrom(this);
        this.removeFromDimension();
        entity2.setPosition(PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives());
        to.onDimensionChanged(entity2);
        for (Entity entity3 : list2) {
            entity3.startRiding(entity2, true);
        }
        to.resetIdleTimeout();
        teleportTarget.postTeleportTransition().onTransition(entity2);
        this.teleportSpectatingPlayers(teleportTarget, from);
        profiler.pop();
        return entity2;
    }

    protected void teleportSpectatingPlayers(TeleportTarget teleportTarget, ServerWorld from) {
        List<ServerPlayerEntity> list = List.copyOf(from.getPlayers());
        for (ServerPlayerEntity serverPlayerEntity : list) {
            if (serverPlayerEntity.getCameraEntity() != this) continue;
            serverPlayerEntity.net_minecraft_server_network_ServerPlayerEntity_teleportTo(teleportTarget);
            serverPlayerEntity.setCameraEntity(null);
        }
    }

    private TeleportTarget getPassengerTeleportTarget(TeleportTarget teleportTarget, Entity passenger) {
        float f = teleportTarget.yaw() + (teleportTarget.relatives().contains((Object)PositionFlag.Y_ROT) ? 0.0f : passenger.getYaw() - this.getYaw());
        float g = teleportTarget.pitch() + (teleportTarget.relatives().contains((Object)PositionFlag.X_ROT) ? 0.0f : passenger.getPitch() - this.getPitch());
        Vec3d vec3d = passenger.getPos().subtract(this.getPos());
        Vec3d vec3d2 = teleportTarget.position().add(teleportTarget.relatives().contains((Object)PositionFlag.X) ? 0.0 : vec3d.getX(), teleportTarget.relatives().contains((Object)PositionFlag.Y) ? 0.0 : vec3d.getY(), teleportTarget.relatives().contains((Object)PositionFlag.Z) ? 0.0 : vec3d.getZ());
        return teleportTarget.withPosition(vec3d2).withRotation(f, g).net_minecraft_world_TeleportTarget_asPassenger();
    }

    private void sendTeleportPacket(TeleportTarget teleportTarget) {
        LivingEntity entity = this.getControllingPassenger();
        for (Entity entity2 : this.getPassengersDeep()) {
            if (!(entity2 instanceof ServerPlayerEntity)) continue;
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
            if (entity != null && serverPlayerEntity.getId() == entity.getId()) {
                serverPlayerEntity.networkHandler.sendPacket(EntityPositionS2CPacket.create(this.getId(), PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives(), this.onGround));
                continue;
            }
            serverPlayerEntity.networkHandler.sendPacket(EntityPositionS2CPacket.create(this.getId(), PlayerPosition.fromEntity(this), Set.of(), this.onGround));
        }
    }

    public void setPosition(PlayerPosition pos, Set<PositionFlag> flags) {
        PlayerPosition playerPosition = PlayerPosition.fromEntity(this);
        PlayerPosition playerPosition2 = PlayerPosition.apply(playerPosition, pos, flags);
        this.setPos(playerPosition2.position().x, playerPosition2.position().y, playerPosition2.position().z);
        this.setYaw(playerPosition2.yaw());
        this.setHeadYaw(playerPosition2.yaw());
        this.setPitch(playerPosition2.pitch());
        this.refreshPosition();
        this.resetPosition();
        this.setVelocity(playerPosition2.deltaMovement());
        this.clearQueuedCollisionChecks();
    }

    public void rotate(float yaw, float pitch) {
        this.setYaw(yaw);
        this.setHeadYaw(yaw);
        this.setPitch(pitch);
        this.updateLastAngles();
    }

    public void addPortalChunkTicketAt(BlockPos pos) {
        World world = this.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            serverWorld.net_minecraft_server_world_ServerChunkManager_getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(pos), 3);
        }
    }

    protected void removeFromDimension() {
        Object object;
        this.setRemoved(RemovalReason.CHANGED_DIMENSION);
        Entity entity = this;
        if (entity instanceof Leashable) {
            Leashable leashable = (Leashable)((Object)entity);
            leashable.detachLeashWithoutDrop();
        }
        if ((object = this) instanceof ServerWaypoint) {
            ServerWaypoint serverWaypoint = (ServerWaypoint)object;
            object = this.world;
            if (object instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)object;
                serverWorld.getWaypointHandler().onUntrack(serverWaypoint);
            }
        }
    }

    public Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
        return NetherPortal.entityPosInPortal(portalRect, portalAxis, this.getPos(), this.getDimensions(this.getPose()));
    }

    public boolean canUsePortals(boolean allowVehicles) {
        return (allowVehicles || !this.hasVehicle()) && this.isAlive();
    }

    public boolean canTeleportBetween(World from, World to) {
        if (from.getRegistryKey() == World.END && to.getRegistryKey() == World.OVERWORLD) {
            for (Entity entity : this.getPassengerList()) {
                if (!(entity instanceof ServerPlayerEntity)) continue;
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                if (serverPlayerEntity.seenCredits) continue;
                return false;
            }
        }
        return true;
    }

    public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
        return max;
    }

    public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
        return true;
    }

    public int getSafeFallDistance() {
        return 3;
    }

    public boolean canAvoidTraps() {
        return false;
    }

    public void populateCrashReport(CrashReportSection section) {
        section.add("Entity Type", () -> String.valueOf(EntityType.getId(this.getType())) + " (" + this.getClass().getCanonicalName() + ")");
        section.add("Entity ID", this.id);
        section.add("Entity Name", () -> this.getName().getString());
        section.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
        section.add("Entity's Block location", CrashReportSection.createPositionString((HeightLimitView)this.net_minecraft_world_World_getWorld(), MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
        Vec3d vec3d = this.getVelocity();
        section.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
        section.add("Entity's Passengers", () -> this.getPassengerList().toString());
        section.add("Entity's Vehicle", () -> String.valueOf(this.getVehicle()));
    }

    public boolean doesRenderOnFire() {
        return this.isOnFire() && !this.isSpectator();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        this.uuidString = this.uuid.toString();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    public String getUuidAsString() {
        return this.uuidString;
    }

    @Override
    public String getNameForScoreboard() {
        return this.uuidString;
    }

    public boolean isPushedByFluids() {
        return true;
    }

    public static double getRenderDistanceMultiplier() {
        return renderDistanceMultiplier;
    }

    public static void setRenderDistanceMultiplier(double value) {
        renderDistanceMultiplier = value;
    }

    @Override
    public Text getDisplayName() {
        return Team.decorateName(this.getScoreboardTeam(), this.getName()).styled(style -> style.withHoverEvent(this.getHoverEvent()).withInsertion(this.getUuidAsString()));
    }

    public void setCustomName(@Nullable Text name) {
        this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(name));
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.dataTracker.get(CUSTOM_NAME).orElse(null);
    }

    @Override
    public boolean hasCustomName() {
        return this.dataTracker.get(CUSTOM_NAME).isPresent();
    }

    public void setCustomNameVisible(boolean visible) {
        this.dataTracker.set(NAME_VISIBLE, visible);
    }

    public boolean isCustomNameVisible() {
        return this.dataTracker.get(NAME_VISIBLE);
    }

    public boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, boolean resetCamera) {
        Entity entity = this.net_minecraft_entity_Entity_teleportTo(new TeleportTarget(world, new Vec3d(destX, destY, destZ), Vec3d.ZERO, yaw, pitch, flags, TeleportTarget.NO_OP));
        return entity != null;
    }

    public void requestTeleportAndDismount(double destX, double destY, double destZ) {
        this.requestTeleport(destX, destY, destZ);
    }

    public void requestTeleport(double destX, double destY, double destZ) {
        if (!(this.net_minecraft_world_World_getWorld() instanceof ServerWorld)) {
            return;
        }
        this.refreshPositionAndAngles(destX, destY, destZ, this.getYaw(), this.getPitch());
        this.teleportPassengers();
    }

    private void teleportPassengers() {
        this.streamSelfAndPassengers().forEach(entity -> {
            for (Entity entity2 : entity.passengerList) {
                entity.updatePassengerPosition(entity2, Entity::refreshPositionAfterTeleport);
            }
        });
    }

    public void requestTeleportOffset(double offsetX, double offsetY, double offsetZ) {
        this.requestTeleport(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
    }

    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    @Override
    public void onDataTrackerUpdate(List<DataTracker.SerializedEntry<?>> entries) {
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            this.calculateDimensions();
        }
    }

    @Deprecated
    protected void reinitDimensions() {
        EntityDimensions entityDimensions;
        EntityPose entityPose = this.getPose();
        this.dimensions = entityDimensions = this.getDimensions(entityPose);
        this.standingEyeHeight = entityDimensions.eyeHeight();
    }

    public void calculateDimensions() {
        boolean bl;
        EntityDimensions entityDimensions2;
        EntityDimensions entityDimensions = this.dimensions;
        EntityPose entityPose = this.getPose();
        this.dimensions = entityDimensions2 = this.getDimensions(entityPose);
        this.standingEyeHeight = entityDimensions2.eyeHeight();
        this.refreshPosition();
        boolean bl2 = bl = entityDimensions2.width() <= 4.0f && entityDimensions2.height() <= 4.0f;
        if (!(this.world.isClient || this.firstUpdate || this.noClip || !bl || !(entityDimensions2.width() > entityDimensions.width()) && !(entityDimensions2.height() > entityDimensions.height()) || this instanceof PlayerEntity)) {
            this.recalculateDimensions(entityDimensions);
        }
    }

    public boolean recalculateDimensions(EntityDimensions previous) {
        VoxelShape voxelShape2;
        Optional optional2;
        double e;
        double d;
        EntityDimensions entityDimensions = this.getDimensions(this.getPose());
        Vec3d vec3d = this.getPos().add(0.0, (double)previous.height() / 2.0, 0.0);
        VoxelShape voxelShape = VoxelShapes.cuboid(Box.of(vec3d, d = (double)Math.max(0.0f, entityDimensions.width() - previous.width()) + 1.0E-6, e = (double)Math.max(0.0f, entityDimensions.height() - previous.height()) + 1.0E-6, d));
        Optional optional = this.world.findClosestCollision(this, voxelShape, vec3d, entityDimensions.width(), entityDimensions.height(), entityDimensions.width());
        if (optional.isPresent()) {
            this.setPosition(((Vec3d)optional.get()).add(0.0, (double)(-entityDimensions.height()) / 2.0, 0.0));
            return true;
        }
        if (entityDimensions.width() > previous.width() && entityDimensions.height() > previous.height() && (optional2 = this.world.findClosestCollision(this, voxelShape2 = VoxelShapes.cuboid(Box.of(vec3d, d, 1.0E-6, d)), vec3d, entityDimensions.width(), previous.height(), entityDimensions.width())).isPresent()) {
            this.setPosition(((Vec3d)optional2.get()).add(0.0, (double)(-previous.height()) / 2.0 + 1.0E-6, 0.0));
            return true;
        }
        return false;
    }

    public Direction getHorizontalFacing() {
        return Direction.fromHorizontalDegrees(this.getYaw());
    }

    public Direction getMovementDirection() {
        return this.getHorizontalFacing();
    }

    protected HoverEvent getHoverEvent() {
        return new HoverEvent.ShowEntity(new HoverEvent.EntityContent(this.getType(), this.getUuid(), this.getName()));
    }

    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        return true;
    }

    @Override
    public final Box getBoundingBox() {
        return this.boundingBox;
    }

    public final void setBoundingBox(Box boundingBox) {
        this.boundingBox = boundingBox;
    }

    public final float getEyeHeight(EntityPose pose) {
        return this.getDimensions(pose).eyeHeight();
    }

    public final float getStandingEyeHeight() {
        return this.standingEyeHeight;
    }

    public StackReference getStackReference(int mappedIndex) {
        return StackReference.EMPTY;
    }

    @Nullable
    public MinecraftServer getServer() {
        return this.net_minecraft_world_World_getWorld().getServer();
    }

    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        return ActionResult.PASS;
    }

    public boolean isImmuneToExplosion(Explosion explosion) {
        return false;
    }

    public void onStartedTrackingBy(ServerPlayerEntity player) {
    }

    public void onStoppedTrackingBy(ServerPlayerEntity player) {
    }

    public float applyRotation(BlockRotation rotation) {
        float f = MathHelper.wrapDegrees(this.getYaw());
        switch (rotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 270.0f;
            }
            case CLOCKWISE_90: {
                return f + 90.0f;
            }
        }
        return f;
    }

    public float applyMirror(BlockMirror mirror) {
        float f = MathHelper.wrapDegrees(this.getYaw());
        switch (mirror) {
            case FRONT_BACK: {
                return -f;
            }
            case LEFT_RIGHT: {
                return 180.0f - f;
            }
        }
        return f;
    }

    public ProjectileDeflection getProjectileDeflection(ProjectileEntity projectile) {
        return this.getType().isIn(EntityTypeTags.DEFLECTS_PROJECTILES) ? ProjectileDeflection.SIMPLE : ProjectileDeflection.NONE;
    }

    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public final boolean hasControllingPassenger() {
        return this.getControllingPassenger() != null;
    }

    public final List<Entity> getPassengerList() {
        return this.passengerList;
    }

    @Nullable
    public Entity getFirstPassenger() {
        return this.passengerList.isEmpty() ? null : (Entity)this.passengerList.get(0);
    }

    public boolean hasPassenger(Entity passenger) {
        return this.passengerList.contains((Object)passenger);
    }

    public boolean hasPassenger(Predicate<Entity> predicate) {
        for (Entity entity : this.passengerList) {
            if (!predicate.test(entity)) continue;
            return true;
        }
        return false;
    }

    private Stream<Entity> streamIntoPassengers() {
        return this.passengerList.stream().flatMap(Entity::streamSelfAndPassengers);
    }

    public Stream<Entity> streamSelfAndPassengers() {
        return Stream.concat(Stream.of(this), this.streamIntoPassengers());
    }

    public Stream<Entity> streamPassengersAndSelf() {
        return Stream.concat(this.passengerList.stream().flatMap(Entity::streamPassengersAndSelf), Stream.of(this));
    }

    public Iterable<Entity> getPassengersDeep() {
        return () -> this.streamIntoPassengers().iterator();
    }

    public int getPlayerPassengers() {
        return (int)this.streamIntoPassengers().filter(passenger -> passenger instanceof PlayerEntity).count();
    }

    public boolean hasPlayerRider() {
        return this.getPlayerPassengers() == 1;
    }

    public Entity getRootVehicle() {
        Entity entity = this;
        while (entity.hasVehicle()) {
            entity = entity.getVehicle();
        }
        return entity;
    }

    public boolean isConnectedThroughVehicle(Entity entity) {
        return this.getRootVehicle() == entity.getRootVehicle();
    }

    public boolean hasPassengerDeep(Entity passenger) {
        if (!passenger.hasVehicle()) {
            return false;
        }
        Entity entity = passenger.getVehicle();
        if (entity == this) {
            return true;
        }
        return this.hasPassengerDeep(entity);
    }

    public final boolean isLogicalSideForUpdatingMovement() {
        if (this.world.isClient()) {
            return this.isControlledByMainPlayer();
        }
        return !this.isControlledByPlayer();
    }

    protected boolean isControlledByMainPlayer() {
        LivingEntity livingEntity = this.getControllingPassenger();
        return livingEntity != null && livingEntity.isControlledByMainPlayer();
    }

    public boolean isControlledByPlayer() {
        LivingEntity livingEntity = this.getControllingPassenger();
        return livingEntity != null && livingEntity.isControlledByPlayer();
    }

    public boolean canMoveVoluntarily() {
        return this.isLogicalSideForUpdatingMovement();
    }

    public boolean canActVoluntarily() {
        return this.isLogicalSideForUpdatingMovement();
    }

    protected static Vec3d getPassengerDismountOffset(double vehicleWidth, double passengerWidth, float passengerYaw) {
        double d = (vehicleWidth + passengerWidth + (double)1.0E-5f) / 2.0;
        float f = -MathHelper.sin(passengerYaw * ((float)Math.PI / 180));
        float g = MathHelper.cos(passengerYaw * ((float)Math.PI / 180));
        float h = Math.max(Math.abs(f), Math.abs(g));
        return new Vec3d((double)f * d / (double)h, 0.0, (double)g * d / (double)h);
    }

    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }

    @Nullable
    public Entity getControllingVehicle() {
        return this.vehicle != null && this.vehicle.getControllingPassenger() == this ? this.vehicle : null;
    }

    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.NORMAL;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    protected int getBurningDuration() {
        return 0;
    }

    public ServerCommandSource getCommandSource(ServerWorld world) {
        return new ServerCommandSource(CommandOutput.DUMMY, this.getPos(), this.getRotationClient(), world, 0, this.getName().getString(), this.getDisplayName(), world.getServer(), this);
    }

    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        Vec3d vec3d = anchorPoint.positionAt(this);
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);
        this.setPitch(MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875))));
        this.setYaw(MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f));
        this.setHeadYaw(this.getYaw());
        this.lastPitch = this.getPitch();
        this.lastYaw = this.getYaw();
    }

    public float lerpYaw(float tickProgress) {
        return MathHelper.lerp(tickProgress, this.lastYaw, this.yaw);
    }

    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        if (this.isRegionUnloaded()) {
            return false;
        }
        Box box = this.getBoundingBox().contract(0.001);
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.maxY);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        double d = 0.0;
        boolean bl = this.isPushedByFluids();
        boolean bl2 = false;
        Vec3d vec3d = Vec3d.ZERO;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int p = i; p < j; ++p) {
            for (int q = k; q < l; ++q) {
                for (int r = m; r < n; ++r) {
                    double e;
                    mutable.set(p, q, r);
                    FluidState fluidState = this.net_minecraft_world_World_getWorld().getFluidState(mutable);
                    if (!fluidState.isIn(tag) || !((e = (double)((float)q + fluidState.getHeight(this.net_minecraft_world_World_getWorld(), mutable))) >= box.minY)) continue;
                    bl2 = true;
                    d = Math.max(e - box.minY, d);
                    if (!bl) continue;
                    Vec3d vec3d2 = fluidState.getVelocity(this.net_minecraft_world_World_getWorld(), mutable);
                    if (d < 0.4) {
                        vec3d2 = vec3d2.multiply(d);
                    }
                    vec3d = vec3d.add(vec3d2);
                    ++o;
                }
            }
        }
        if (vec3d.length() > 0.0) {
            if (o > 0) {
                vec3d = vec3d.multiply(1.0 / (double)o);
            }
            if (!(this instanceof PlayerEntity)) {
                vec3d = vec3d.normalize();
            }
            Vec3d vec3d3 = this.getVelocity();
            vec3d = vec3d.multiply(speed);
            double f = 0.003;
            if (Math.abs(vec3d3.x) < 0.003 && Math.abs(vec3d3.z) < 0.003 && vec3d.length() < 0.0045000000000000005) {
                vec3d = vec3d.normalize().multiply(0.0045000000000000005);
            }
            this.setVelocity(this.getVelocity().add(vec3d));
        }
        this.fluidHeight.put(tag, d);
        return bl2;
    }

    public boolean isRegionUnloaded() {
        Box box = this.getBoundingBox().expand(1.0);
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minZ);
        int l = MathHelper.ceil(box.maxZ);
        return !this.net_minecraft_world_World_getWorld().isRegionLoaded(i, k, j, l);
    }

    public double getFluidHeight(TagKey<Fluid> fluid) {
        return this.fluidHeight.getDouble(fluid);
    }

    public double getSwimHeight() {
        return (double)this.getStandingEyeHeight() < 0.4 ? 0.0 : 0.4;
    }

    public final float getWidth() {
        return this.dimensions.width();
    }

    public final float getHeight() {
        return this.dimensions.height();
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return new EntitySpawnS2CPacket(this, entityTrackerEntry);
    }

    public EntityDimensions getDimensions(EntityPose pose) {
        return this.type.getDimensions();
    }

    public final EntityAttachments getAttachments() {
        return this.dimensions.attachments();
    }

    public Vec3d getPos() {
        return this.pos;
    }

    public Vec3d getSyncedPos() {
        return this.getPos();
    }

    @Override
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public BlockState getBlockStateAtPos() {
        if (this.stateAtPos == null) {
            this.stateAtPos = this.net_minecraft_world_World_getWorld().getBlockState(this.getBlockPos());
        }
        return this.stateAtPos;
    }

    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    public Vec3d getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Vec3d velocity) {
        this.velocity = velocity;
    }

    public void addVelocityInternal(Vec3d velocity) {
        this.setVelocity(this.getVelocity().add(velocity));
    }

    public void setVelocity(double x, double y, double z) {
        this.setVelocity(new Vec3d(x, y, z));
    }

    public final int getBlockX() {
        return this.blockPos.getX();
    }

    public final double getX() {
        return this.pos.x;
    }

    public double getBodyX(double widthScale) {
        return this.pos.x + (double)this.getWidth() * widthScale;
    }

    public double getParticleX(double widthScale) {
        return this.getBodyX((2.0 * this.random.nextDouble() - 1.0) * widthScale);
    }

    public final int getBlockY() {
        return this.blockPos.getY();
    }

    public final double getY() {
        return this.pos.y;
    }

    public double getBodyY(double heightScale) {
        return this.pos.y + (double)this.getHeight() * heightScale;
    }

    public double getRandomBodyY() {
        return this.getBodyY(this.random.nextDouble());
    }

    public double getEyeY() {
        return this.pos.y + (double)this.standingEyeHeight;
    }

    public final int getBlockZ() {
        return this.blockPos.getZ();
    }

    public final double getZ() {
        return this.pos.z;
    }

    public double getBodyZ(double widthScale) {
        return this.pos.z + (double)this.getWidth() * widthScale;
    }

    public double getParticleZ(double widthScale) {
        return this.getBodyZ((2.0 * this.random.nextDouble() - 1.0) * widthScale);
    }

    public final void setPos(double x, double y, double z) {
        if (this.pos.x != x || this.pos.y != y || this.pos.z != z) {
            World world;
            this.pos = new Vec3d(x, y, z);
            int i = MathHelper.floor(x);
            int j = MathHelper.floor(y);
            int k = MathHelper.floor(z);
            if (i != this.blockPos.getX() || j != this.blockPos.getY() || k != this.blockPos.getZ()) {
                this.blockPos = new BlockPos(i, j, k);
                this.stateAtPos = null;
                if (ChunkSectionPos.getSectionCoord(i) != this.chunkPos.x || ChunkSectionPos.getSectionCoord(k) != this.chunkPos.z) {
                    this.chunkPos = new ChunkPos(this.blockPos);
                }
            }
            this.changeListener.updateEntityPosition();
            if (!this.firstUpdate && (world = this.world) instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                if (!this.isRemoved()) {
                    ServerPlayerEntity serverPlayerEntity;
                    ServerWaypoint serverWaypoint;
                    Entity entity = this;
                    if (entity instanceof ServerWaypoint && (serverWaypoint = (ServerWaypoint)((Object)entity)).hasWaypoint()) {
                        serverWorld.getWaypointHandler().onUpdate(serverWaypoint);
                    }
                    if ((entity = this) instanceof ServerPlayerEntity && (serverPlayerEntity = (ServerPlayerEntity)entity).canReceiveWaypoints() && serverPlayerEntity.networkHandler != null) {
                        serverWorld.getWaypointHandler().updatePlayerPos(serverPlayerEntity);
                    }
                }
            }
        }
    }

    public void checkDespawn() {
    }

    public Vec3d[] getHeldQuadLeashOffsets() {
        return Leashable.createQuadLeashOffsets(this, 0.0, 0.5, 0.5, 0.0);
    }

    public boolean hasQuadLeashAttachmentPoints() {
        return false;
    }

    public void tickHeldLeash(Leashable leashedEntity) {
    }

    public void onHeldLeashUpdate(Leashable heldLeashable) {
    }

    public Vec3d getLeashPos(float tickProgress) {
        return this.getLerpedPos(tickProgress).add(0.0, (double)this.standingEyeHeight * 0.7, 0.0);
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        int i = packet.getEntityId();
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.updateTrackedPosition(d, e, f);
        this.refreshPositionAndAngles(d, e, f, packet.getYaw(), packet.getPitch());
        this.setId(i);
        this.setUuid(packet.getUuid());
        Vec3d vec3d = new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
        this.setVelocity(vec3d);
    }

    @Nullable
    public ItemStack getPickBlockStack() {
        return null;
    }

    public void setInPowderSnow(boolean inPowderSnow) {
        this.inPowderSnow = inPowderSnow;
    }

    public boolean canFreeze() {
        return !this.getType().isIn(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES);
    }

    public boolean shouldEscapePowderSnow() {
        return this.getFrozenTicks() > 0;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getBodyYaw() {
        return this.getYaw();
    }

    public void setYaw(float yaw) {
        if (!Float.isFinite(yaw)) {
            Util.logErrorOrPause("Invalid entity rotation: " + yaw + ", discarding.");
            return;
        }
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        if (!Float.isFinite(pitch)) {
            Util.logErrorOrPause("Invalid entity rotation: " + pitch + ", discarding.");
            return;
        }
        this.pitch = Math.clamp(pitch % 360.0f, -90.0f, 90.0f);
    }

    public boolean canSprintAsVehicle() {
        return false;
    }

    public float getStepHeight() {
        return 0.0f;
    }

    public void onExplodedBy(@Nullable Entity entity) {
    }

    @Override
    public final boolean isRemoved() {
        return this.removalReason != null;
    }

    @Nullable
    public RemovalReason getRemovalReason() {
        return this.removalReason;
    }

    @Override
    public final void setRemoved(RemovalReason reason) {
        if (this.removalReason == null) {
            this.removalReason = reason;
        }
        if (this.removalReason.shouldDestroy()) {
            this.stopRiding();
        }
        this.getPassengerList().forEach(Entity::stopRiding);
        this.changeListener.remove(reason);
        this.onRemove(reason);
    }

    protected void unsetRemoved() {
        this.removalReason = null;
    }

    @Override
    public void setChangeListener(EntityChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public boolean shouldSave() {
        if (this.removalReason != null && !this.removalReason.shouldSave()) {
            return false;
        }
        if (this.hasVehicle()) {
            return false;
        }
        return !this.hasPassengers() || !this.hasPlayerRider();
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    public boolean canModifyAt(ServerWorld world, BlockPos pos) {
        return true;
    }

    public boolean isFlyingVehicle() {
        return false;
    }

    public World net_minecraft_world_World_getWorld() {
        return this.world;
    }

    protected void setWorld(World world) {
        this.world = world;
    }

    public DamageSources getDamageSources() {
        return this.net_minecraft_world_World_getWorld().getDamageSources();
    }

    public DynamicRegistryManager getRegistryManager() {
        return this.net_minecraft_world_World_getWorld().getRegistryManager();
    }

    protected void lerpPosAndRotation(int step, double x, double y, double z, double yaw, double pitch) {
        double d = 1.0 / (double)step;
        double e = MathHelper.lerp(d, this.getX(), x);
        double f = MathHelper.lerp(d, this.getY(), y);
        double g = MathHelper.lerp(d, this.getZ(), z);
        float h = (float)MathHelper.lerpAngleDegrees(d, (double)this.getYaw(), yaw);
        float i = (float)MathHelper.lerp(d, (double)this.getPitch(), pitch);
        this.setPosition(e, f, g);
        this.setRotation(h, i);
    }

    public Random getRandom() {
        return this.random;
    }

    public Vec3d getMovement() {
        LivingEntity livingEntity = this.getControllingPassenger();
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)livingEntity;
            if (this.isAlive()) {
                return playerEntity.getMovement();
            }
        }
        return this.getVelocity();
    }

    @Nullable
    public ItemStack getWeaponStack() {
        return null;
    }

    public Optional<RegistryKey<LootTable>> getLootTableKey() {
        return this.type.getLootTableKey();
    }

    protected void copyComponentsFrom(ComponentsAccess from) {
        this.copyComponentFrom(from, DataComponentTypes.CUSTOM_NAME);
        this.copyComponentFrom(from, DataComponentTypes.CUSTOM_DATA);
    }

    public final void copyComponentsFrom(ItemStack stack) {
        this.copyComponentsFrom(stack.getComponents());
    }

    @Override
    @Nullable
    public <T> T get(ComponentType<? extends T> type) {
        if (type == DataComponentTypes.CUSTOM_NAME) {
            return Entity.castComponentValue(type, this.getCustomName());
        }
        if (type == DataComponentTypes.CUSTOM_DATA) {
            return Entity.castComponentValue(type, this.customData);
        }
        return null;
    }

    @Nullable
    @Contract(value="_,!null->!null;_,_->_")
    protected static <T> T castComponentValue(ComponentType<T> type, @Nullable Object value) {
        return (T)value;
    }

    public <T> void setComponent(ComponentType<T> type, T value) {
        this.setApplicableComponent(type, value);
    }

    protected <T> boolean setApplicableComponent(ComponentType<T> type, T value) {
        if (type == DataComponentTypes.CUSTOM_NAME) {
            this.setCustomName(Entity.castComponentValue(DataComponentTypes.CUSTOM_NAME, value));
            return true;
        }
        if (type == DataComponentTypes.CUSTOM_DATA) {
            this.customData = Entity.castComponentValue(DataComponentTypes.CUSTOM_DATA, value);
            return true;
        }
        return false;
    }

    protected <T> boolean copyComponentFrom(ComponentsAccess from, ComponentType<T> type) {
        T object = from.get(type);
        if (object != null) {
            return this.setApplicableComponent(type, object);
        }
        return false;
    }

    public ErrorReporter.Context getErrorReporterContext() {
        return new ErrorReporterContext(this);
    }

    public static final class RemovalReason
    extends Enum<RemovalReason> {
        final static public RemovalReason KILLED = new RemovalReason(true, false);
        final static public RemovalReason DISCARDED = new RemovalReason(true, false);
        final static public RemovalReason UNLOADED_TO_CHUNK = new RemovalReason(false, true);
        final static public RemovalReason UNLOADED_WITH_PLAYER = new RemovalReason(false, false);
        final static public RemovalReason CHANGED_DIMENSION = new RemovalReason(false, false);
        final private boolean destroy;
        final private boolean save;
        final static private RemovalReason[] field_27005;

        public static RemovalReason[] values() {
            return (RemovalReason[])field_27005.clone();
        }

        public static RemovalReason valueOf(String string) {
            return Enum.valueOf(RemovalReason.class, string);
        }

        private RemovalReason(boolean destroy, boolean save) {
            this.destroy = destroy;
            this.save = save;
        }

        public boolean shouldDestroy() {
            return this.destroy;
        }

        public boolean shouldSave() {
            return this.save;
        }

        private static RemovalReason[] method_36603() {
            return new RemovalReason[]{KILLED, DISCARDED, UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION};
        }

        static {
            field_27005 = RemovalReason.method_36603();
        }
    }

    static final class QueuedCollisionCheck
    extends Record {
        final Vec3d from;
        final Vec3d to;
        final boolean axisIndependant;

        QueuedCollisionCheck(Vec3d vec3d, Vec3d vec3d2, boolean bl) {
            this.from = vec3d;
            this.to = vec3d2;
            this.axisIndependant = bl;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{QueuedCollisionCheck.class, "from;to;axisIndependant", "from", "to", "axisIndependant"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{QueuedCollisionCheck.class, "from;to;axisIndependant", "from", "to", "axisIndependant"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{QueuedCollisionCheck.class, "from;to;axisIndependant", "from", "to", "axisIndependant"}, this, object);
        }

        public Vec3d from() {
            return this.from;
        }

        public Vec3d to() {
            return this.to;
        }

        public boolean axisIndependant() {
            return this.axisIndependant;
        }
    }

    public static final class MoveEffect
    extends Enum<MoveEffect> {
        final static public MoveEffect NONE = new MoveEffect(false, false);
        final static public MoveEffect SOUNDS = new MoveEffect(true, false);
        final static public MoveEffect EVENTS = new MoveEffect(false, true);
        final static public MoveEffect ALL = new MoveEffect(true, true);
        final boolean sounds;
        final boolean events;
        final static private MoveEffect[] field_28636;

        public static MoveEffect[] values() {
            return (MoveEffect[])field_28636.clone();
        }

        public static MoveEffect valueOf(String string) {
            return Enum.valueOf(MoveEffect.class, string);
        }

        private MoveEffect(boolean sounds, boolean events) {
            this.sounds = sounds;
            this.events = events;
        }

        public boolean hasAny() {
            return this.events || this.sounds;
        }

        public boolean emitsGameEvents() {
            return this.events;
        }

        public boolean playsSounds() {
            return this.sounds;
        }

        private static MoveEffect[] method_36602() {
            return new MoveEffect[]{NONE, SOUNDS, EVENTS, ALL};
        }

        static {
            field_28636 = MoveEffect.method_36602();
        }
    }

    @FunctionalInterface
    public static interface PositionUpdater {
        public void accept(Entity var1, double var2, double var4, double var6);
    }

    record ErrorReporterContext(Entity entity) implements ErrorReporter.Context
    {
        @Override
        public String getName() {
            return this.entity.toString();
        }
    }
}

