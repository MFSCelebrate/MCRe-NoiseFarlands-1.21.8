/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.hash.HashCode
 *  com.google.common.net.InetAddresses
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.network;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.Component;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.sync.TrackedSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.server.network.ServerItemCooldownManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.server.network.ServerWaypointHandler;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.HashCodeOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerPlayerEntity
extends PlayerEntity {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int field_29769 = 32;
    final static private int field_29770 = 10;
    final static private int field_46928 = 25;
    final static public double field_54046 = 1.0;
    final static public double field_54047 = 3.0;
    final static public int field_54207 = 2;
    final static public String ENDER_PEARLS_KEY = "ender_pearls";
    final static public String ENDER_PEARLS_DIMENSION_KEY = "ender_pearl_dimension";
    final static public String DIMENSION_KEY = "Dimension";
    final static private EntityAttributeModifier CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER = new EntityAttributeModifier(Identifier.ofVanilla("creative_mode_block_range"), 0.5, EntityAttributeModifier.Operation.ADD_VALUE);
    final static private EntityAttributeModifier CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER = new EntityAttributeModifier(Identifier.ofVanilla("creative_mode_entity_range"), 2.0, EntityAttributeModifier.Operation.ADD_VALUE);
    final static private Text SET_SPAWN_TEXT = Text.translatable("block.minecraft.set_spawn");
    final static private EntityAttributeModifier WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER = new EntityAttributeModifier(Identifier.ofVanilla("waypoint_transmit_range_crouch"), -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    final static private boolean DEFAULT_SEEN_CREDITS = false;
    final static private boolean DEFAULT_SPAWN_EXTRA_PARTICLES_ON_FALL = false;
    public ServerPlayNetworkHandler networkHandler;
    final private MinecraftServer server;
    final public ServerPlayerInteractionManager interactionManager;
    final private PlayerAdvancementTracker advancementTracker;
    final private ServerStatHandler statHandler;
    private float lastHealthScore = Float.MIN_VALUE;
    private int lastFoodScore = Integer.MIN_VALUE;
    private int lastAirScore = Integer.MIN_VALUE;
    private int lastArmorScore = Integer.MIN_VALUE;
    private int lastLevelScore = Integer.MIN_VALUE;
    private int lastExperienceScore = Integer.MIN_VALUE;
    private float syncedHealth = -1.0E8f;
    private int syncedFoodLevel = -99999999;
    private boolean syncedSaturationIsZero = true;
    private int syncedExperience = -99999999;
    private ChatVisibility clientChatVisibility = ChatVisibility.FULL;
    private ParticlesMode particlesMode = ParticlesMode.ALL;
    private boolean clientChatColorsEnabled = true;
    private long lastActionTime = Util.getMeasuringTimeMs();
    @Nullable
    private Entity cameraEntity;
    private boolean inTeleportationState;
    public boolean seenCredits = false;
    final private ServerRecipeBook recipeBook;
    @Nullable
    private Vec3d levitationStartPos;
    private int levitationStartTick;
    private boolean disconnected;
    private int viewDistance = 2;
    private String language = "en_us";
    @Nullable
    private Vec3d fallStartPos;
    @Nullable
    private Vec3d enteredNetherPos;
    @Nullable
    private Vec3d vehicleInLavaRidingPos;
    private ChunkSectionPos watchedSection = ChunkSectionPos.from(0, 0, 0);
    private ChunkFilter chunkFilter = ChunkFilter.IGNORE_ALL;
    @Nullable
    private Respawn respawn;
    final private TextStream textStream;
    private boolean filterText;
    private boolean allowServerListing;
    private boolean spawnExtraParticlesOnFall = false;
    private SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager();
    @Nullable
    private BlockPos startRaidPos;
    private Vec3d movement = Vec3d.ZERO;
    private PlayerInput playerInput = PlayerInput.DEFAULT;
    final private Set<EnderPearlEntity> enderPearls = new HashSet<EnderPearlEntity>();
    final private ScreenHandlerSyncHandler screenHandlerSyncHandler = new ScreenHandlerSyncHandler(){
        final private LoadingCache<Component<?>, Integer> componentHashCache = CacheBuilder.newBuilder().maximumSize(256L).build(new CacheLoader<Component<?>, Integer>(){
            final private DynamicOps<HashCode> hashOps;
            {
                this.hashOps = ServerPlayerEntity.this.getRegistryManager().getOps(HashCodeOps.INSTANCE);
            }

            public Integer load(Component<?> component) {
                return ((HashCode)component.encode(this.hashOps).getOrThrow(error -> new IllegalArgumentException("Failed to hash " + String.valueOf(component) + ": " + error))).asInt();
            }

            public Object load(Object component) throws Exception {
                return this.load((Component)component);
            }
        });

        @Override
        public void updateState(ScreenHandler handler, List<ItemStack> stacks, ItemStack cursorStack, int[] properties) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new InventoryS2CPacket(handler.syncId, handler.nextRevision(), stacks, cursorStack));
            for (int i = 0; i < properties.length; ++i) {
                this.sendPropertyUpdate(handler, i, properties[i]);
            }
        }

        @Override
        public void updateSlot(ScreenHandler handler, int slot, ItemStack stack) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), slot, stack));
        }

        @Override
        public void updateCursorStack(ScreenHandler handler, ItemStack stack) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new SetCursorItemS2CPacket(stack));
        }

        @Override
        public void updateProperty(ScreenHandler handler, int property, int value) {
            this.sendPropertyUpdate(handler, property, value);
        }

        private void sendPropertyUpdate(ScreenHandler handler, int property, int value) {
            ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, property, value));
        }

        @Override
        public TrackedSlot createTrackedSlot() {
            return new TrackedSlot.Impl(arg_0 -> this.componentHashCache.getUnchecked(arg_0));
        }
    };
    final private ScreenHandlerListener screenHandlerListener = new ScreenHandlerListener(){

        @Override
        public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
            Slot slot = handler.getSlot(slotId);
            if (slot instanceof CraftingResultSlot) {
                return;
            }
            if (slot.inventory == ServerPlayerEntity.this.getInventory()) {
                Criteria.INVENTORY_CHANGED.trigger(ServerPlayerEntity.this, ServerPlayerEntity.this.getInventory(), stack);
            }
        }

        @Override
        public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
        }
    };
    @Nullable
    private PublicPlayerSession session;
    @Nullable
    final public Object field_49777;
    final private CommandOutput commandOutput = new CommandOutput(){

        @Override
        public boolean shouldReceiveFeedback() {
            return ServerPlayerEntity.this.net_minecraft_server_world_ServerWorld_getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
        }

        @Override
        public boolean shouldTrackOutput() {
            return true;
        }

        @Override
        public boolean shouldBroadcastConsoleToOps() {
            return true;
        }

        @Override
        public void sendMessage(Text message) {
            ServerPlayerEntity.this.sendMessage(message);
        }
    };
    private int screenHandlerSyncId;
    public boolean notInAnyWorld;

    public ServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions) {
        super(world, profile);
        this.textStream = server.createFilterer(this);
        this.interactionManager = server.getPlayerInteractionManager(this);
        this.recipeBook = new ServerRecipeBook((key, adder) -> server.getRecipeManager().forEachRecipeDisplay(key, adder));
        this.server = server;
        this.statHandler = server.net_minecraft_server_PlayerManager_getPlayerManager().createStatHandler(this);
        this.advancementTracker = server.net_minecraft_server_PlayerManager_getPlayerManager().getAdvancementTracker(this);
        this.setClientOptions(clientOptions);
        this.field_49777 = null;
    }

    @Override
    public BlockPos getWorldSpawnPos(ServerWorld world, BlockPos basePos) {
        Box box = this.getDimensions(EntityPose.STANDING).getBoxAt(Vec3d.ZERO);
        BlockPos blockPos = basePos;
        if (world.getDimension().hasSkyLight() && world.getServer().getSaveProperties().getGameMode() != GameMode.ADVENTURE) {
            long l;
            long m;
            int i = Math.max(0, this.server.getSpawnRadius(world));
            int j = MathHelper.floor(world.getWorldBorder().getDistanceInsideBorder(basePos.getX(), basePos.getZ()));
            if (j < i) {
                i = j;
            }
            if (j <= 1) {
                i = 1;
            }
            int k = (m = (l = (long)(i * 2 + 1)) * l) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)m;
            int n = this.calculateSpawnOffsetMultiplier(k);
            int o = Random.create().nextInt(k);
            for (int p = 0; p < k; ++p) {
                int q = (o + n * p) % k;
                int r = q % (i * 2 + 1);
                int s = q / (i * 2 + 1);
                int t = basePos.getX() + r - i;
                int u = basePos.getZ() + s - i;
                try {
                    blockPos = SpawnLocating.findOverworldSpawn(world, t, u);
                    if (blockPos == null || !this.canSpawnIn(world, box.offset(blockPos.toBottomCenterPos()))) continue;
                    return blockPos;
                }
                catch (Exception exception) {
                    int v = p;
                    int w = i;
                    CrashReport crashReport = CrashReport.create(exception, "Searching for spawn");
                    CrashReportSection crashReportSection = crashReport.addElement("Spawn Lookup");
                    crashReportSection.add("Origin", basePos::toString);
                    crashReportSection.add("Radius", () -> Integer.toString(w));
                    crashReportSection.add("Candidate", () -> "[" + t + "," + u + "]");
                    crashReportSection.add("Progress", () -> v + " out of " + k);
                    throw new CrashException(crashReport);
                }
            }
            blockPos = basePos;
        }
        while (!this.canSpawnIn(world, box.offset(blockPos.toBottomCenterPos())) && blockPos.getY() < world.getTopYInclusive()) {
            blockPos = blockPos.net_minecraft_util_math_BlockPos_up();
        }
        while (this.canSpawnIn(world, box.offset(blockPos.net_minecraft_util_math_BlockPos_down().toBottomCenterPos())) && blockPos.getY() > world.getBottomY() + 1) {
            blockPos = blockPos.net_minecraft_util_math_BlockPos_down();
        }
        return blockPos;
    }

    private boolean canSpawnIn(ServerWorld world, Box box) {
        return world.isSpaceEmpty(this, box, true);
    }

    private int calculateSpawnOffsetMultiplier(int horizontalSpawnArea) {
        return horizontalSpawnArea <= 16 ? horizontalSpawnArea - 1 : 17;
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.sculkShriekerWarningManager = view.read("warden_spawn_tracker", SculkShriekerWarningManager.CODEC).orElseGet(SculkShriekerWarningManager::new);
        this.enteredNetherPos = view.read("entered_nether_pos", Vec3d.CODEC).orElse(null);
        this.seenCredits = view.getBoolean("seenCredits", false);
        view.read("recipeBook", ServerRecipeBook.Packed.CODEC).ifPresent(packed -> this.recipeBook.unpack((ServerRecipeBook.Packed)packed, recipeKey -> this.server.getRecipeManager().get((RegistryKey<Recipe<?>>)recipeKey).isPresent()));
        if (this.isSleeping()) {
            this.wakeUp();
        }
        this.respawn = view.read("respawn", Respawn.CODEC).orElse(null);
        this.spawnExtraParticlesOnFall = view.getBoolean("spawn_extra_particles_on_fall", false);
        this.startRaidPos = view.read("raid_omen_position", BlockPos.CODEC).orElse(null);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.put("warden_spawn_tracker", SculkShriekerWarningManager.CODEC, this.sculkShriekerWarningManager);
        this.writeGameModeData(view);
        view.putBoolean("seenCredits", this.seenCredits);
        view.putNullable("entered_nether_pos", Vec3d.CODEC, this.enteredNetherPos);
        this.writeRootVehicle(view);
        view.put("recipeBook", ServerRecipeBook.Packed.CODEC, this.recipeBook.pack());
        view.putString(DIMENSION_KEY, this.net_minecraft_server_world_ServerWorld_getWorld().getRegistryKey().getValue().toString());
        view.putNullable("respawn", Respawn.CODEC, this.respawn);
        view.putBoolean("spawn_extra_particles_on_fall", this.spawnExtraParticlesOnFall);
        view.putNullable("raid_omen_position", BlockPos.CODEC, this.startRaidPos);
        this.writeEnderPearls(view);
    }

    private void writeRootVehicle(WriteView view) {
        Entity entity = this.getRootVehicle();
        Entity entity2 = this.getVehicle();
        if (entity2 != null && entity != this && entity.hasPlayerRider()) {
            WriteView writeView = view.get("RootVehicle");
            writeView.put("Attach", Uuids.INT_STREAM_CODEC, entity2.getUuid());
            entity.saveData(writeView.get("Entity"));
        }
    }

    public void readRootVehicle(ReadView view) {
        Optional<ReadView> optional = view.getOptionalReadView("RootVehicle");
        if (optional.isEmpty()) {
            return;
        }
        ServerWorld serverWorld = this.net_minecraft_server_world_ServerWorld_getWorld();
        Entity entity2 = EntityType.loadEntityWithPassengers(optional.get().getReadView("Entity"), (World)serverWorld, SpawnReason.LOAD, entity -> {
            if (!serverWorld.tryLoadEntity((Entity)entity)) {
                return null;
            }
            return entity;
        });
        if (entity2 == null) {
            return;
        }
        UUID uUID = optional.get().read("Attach", Uuids.INT_STREAM_CODEC).orElse(null);
        if (entity2.getUuid().equals(uUID)) {
            this.startRiding(entity2, true);
        } else {
            for (Entity entity22 : entity2.getPassengersDeep()) {
                if (!entity22.getUuid().equals(uUID)) continue;
                this.startRiding(entity22, true);
                break;
            }
        }
        if (!this.hasVehicle()) {
            LOGGER.warn("Couldn't reattach entity to player");
            entity2.discard();
            for (Entity entity22 : entity2.getPassengersDeep()) {
                entity22.discard();
            }
        }
    }

    private void writeEnderPearls(WriteView view) {
        if (!this.enderPearls.isEmpty()) {
            WriteView.ListView listView = view.getList(ENDER_PEARLS_KEY);
            for (EnderPearlEntity enderPearlEntity : this.enderPearls) {
                if (enderPearlEntity.isRemoved()) {
                    LOGGER.warn("Trying to save removed ender pearl, skipping");
                    continue;
                }
                WriteView writeView = listView.add();
                enderPearlEntity.saveData(writeView);
                writeView.put(ENDER_PEARLS_DIMENSION_KEY, World.CODEC, enderPearlEntity.net_minecraft_world_World_getWorld().getRegistryKey());
            }
        }
    }

    public void readEnderPearls(ReadView view) {
        view.getListReadView(ENDER_PEARLS_KEY).forEach(this::readEnderPearl);
    }

    private void readEnderPearl(ReadView view) {
        Optional<RegistryKey<World>> optional = view.read(ENDER_PEARLS_DIMENSION_KEY, World.CODEC);
        if (optional.isEmpty()) {
            return;
        }
        ServerWorld serverWorld = this.net_minecraft_server_world_ServerWorld_getWorld().getServer().getWorld(optional.get());
        if (serverWorld != null) {
            Entity entity = EntityType.loadEntityWithPassengers(view, (World)serverWorld, SpawnReason.LOAD, enderPearl -> {
                if (!serverWorld.tryLoadEntity((Entity)enderPearl)) {
                    return null;
                }
                return enderPearl;
            });
            if (entity != null) {
                ServerPlayerEntity.addEnderPearlTicket(serverWorld, entity.getChunkPos());
            } else {
                LOGGER.warn("Failed to spawn player ender pearl in level ({}), skipping", optional.get());
            }
        } else {
            LOGGER.warn("Trying to load ender pearl without level ({}) being loaded, skipping", optional.get());
        }
    }

    public void setExperiencePoints(int points) {
        float f = this.getNextLevelExperience();
        float g = (f - 1.0f) / f;
        this.experienceProgress = MathHelper.clamp((float)points / f, 0.0f, g);
        this.syncedExperience = -1;
    }

    public void setExperienceLevel(int level) {
        this.experienceLevel = level;
        this.syncedExperience = -1;
    }

    @Override
    public void addExperienceLevels(int levels) {
        super.addExperienceLevels(levels);
        this.syncedExperience = -1;
    }

    @Override
    public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
        super.applyEnchantmentCosts(enchantedItem, experienceLevels);
        this.syncedExperience = -1;
    }

    private void onScreenHandlerOpened(ScreenHandler screenHandler) {
        screenHandler.addListener(this.screenHandlerListener);
        screenHandler.updateSyncHandler(this.screenHandlerSyncHandler);
    }

    public void onSpawn() {
        this.onScreenHandlerOpened(this.playerScreenHandler);
    }

    @Override
    public void enterCombat() {
        super.enterCombat();
        this.networkHandler.sendPacket(EnterCombatS2CPacket.INSTANCE);
    }

    @Override
    public void endCombat() {
        super.endCombat();
        this.networkHandler.sendPacket(new EndCombatS2CPacket(this.getDamageTracker()));
    }

    @Override
    public void onBlockCollision(BlockState state) {
        Criteria.ENTER_BLOCK.trigger(this, state);
    }

    @Override
    protected ItemCooldownManager createCooldownManager() {
        return new ServerItemCooldownManager(this);
    }

    @Override
    public void tick() {
        Entity entity;
        this.tickLoaded();
        this.interactionManager.update();
        this.sculkShriekerWarningManager.tick();
        if (this.timeUntilRegen > 0) {
            --this.timeUntilRegen;
        }
        this.currentScreenHandler.sendContentUpdates();
        if (!this.currentScreenHandler.canUse(this)) {
            this.closeHandledScreen();
            this.currentScreenHandler = this.playerScreenHandler;
        }
        if ((entity = this.getCameraEntity()) != this) {
            if (entity.isAlive()) {
                this.updatePositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                this.net_minecraft_server_world_ServerWorld_getWorld().net_minecraft_server_world_ServerChunkManager_getChunkManager().updatePosition(this);
                if (this.shouldDismount()) {
                    this.setCameraEntity(this);
                }
            } else {
                this.setCameraEntity(this);
            }
        }
        Criteria.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            Criteria.LEVITATION.trigger(this, this.levitationStartPos, this.age - this.levitationStartTick);
        }
        this.tickFallStartPos();
        this.tickVehicleInLavaRiding();
        this.updateCreativeInteractionRangeModifiers();
        this.advancementTracker.sendUpdate(this, true);
    }

    private void updateCreativeInteractionRangeModifiers() {
        EntityAttributeInstance entityAttributeInstance3;
        EntityAttributeInstance entityAttributeInstance2;
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        if (entityAttributeInstance != null) {
            if (this.isCreative()) {
                entityAttributeInstance.updateModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER);
            } else {
                entityAttributeInstance.removeModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER);
            }
        }
        if ((entityAttributeInstance2 = this.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE)) != null) {
            if (this.isCreative()) {
                entityAttributeInstance2.updateModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER);
            } else {
                entityAttributeInstance2.removeModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER);
            }
        }
        if ((entityAttributeInstance3 = this.getAttributeInstance(EntityAttributes.WAYPOINT_TRANSMIT_RANGE)) != null) {
            if (this.isInSneakingPose()) {
                entityAttributeInstance3.updateModifier(WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER);
            } else {
                entityAttributeInstance3.removeModifier(WAYPOINT_TRANSMIT_RANGE_CROUCH_MODIFIER);
            }
        }
    }

    public void playerTick() {
        try {
            if (!this.isSpectator() || !this.isRegionUnloaded()) {
                super.tick();
            }
            for (int i = 0; i < this.getInventory().size(); ++i) {
                ItemStack itemStack = this.getInventory().getStack(i);
                if (itemStack.isEmpty()) continue;
                this.sendMapPacket(itemStack);
            }
            if (this.getHealth() != this.syncedHealth || this.syncedFoodLevel != this.hungerManager.getFoodLevel() || this.hungerManager.getSaturationLevel() == 0.0f != this.syncedSaturationIsZero) {
                this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
                this.syncedHealth = this.getHealth();
                this.syncedFoodLevel = this.hungerManager.getFoodLevel();
                boolean bl = this.syncedSaturationIsZero = this.hungerManager.getSaturationLevel() == 0.0f;
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
                this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
                this.updateScores(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.lastHealthScore));
            }
            if (this.hungerManager.getFoodLevel() != this.lastFoodScore) {
                this.lastFoodScore = this.hungerManager.getFoodLevel();
                this.updateScores(ScoreboardCriterion.FOOD, MathHelper.ceil(this.lastFoodScore));
            }
            if (this.getAir() != this.lastAirScore) {
                this.lastAirScore = this.getAir();
                this.updateScores(ScoreboardCriterion.AIR, MathHelper.ceil(this.lastAirScore));
            }
            if (this.getArmor() != this.lastArmorScore) {
                this.lastArmorScore = this.getArmor();
                this.updateScores(ScoreboardCriterion.ARMOR, MathHelper.ceil(this.lastArmorScore));
            }
            if (this.totalExperience != this.lastExperienceScore) {
                this.lastExperienceScore = this.totalExperience;
                this.updateScores(ScoreboardCriterion.XP, MathHelper.ceil(this.lastExperienceScore));
            }
            if (this.experienceLevel != this.lastLevelScore) {
                this.lastLevelScore = this.experienceLevel;
                this.updateScores(ScoreboardCriterion.LEVEL, MathHelper.ceil(this.lastLevelScore));
            }
            if (this.totalExperience != this.syncedExperience) {
                this.syncedExperience = this.totalExperience;
                this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }
            if (this.age % 20 == 0) {
                Criteria.LOCATION.trigger(this);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Ticking player");
            CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    private void sendMapPacket(ItemStack stack) {
        Packet<?> packet;
        MapIdComponent mapIdComponent = stack.get(DataComponentTypes.MAP_ID);
        MapState mapState = FilledMapItem.getMapState(mapIdComponent, (World)this.net_minecraft_server_world_ServerWorld_getWorld());
        if (mapState != null && (packet = mapState.getPlayerMarkerPacket(mapIdComponent, this)) != null) {
            this.networkHandler.sendPacket(packet);
        }
    }

    @Override
    protected void tickHunger() {
        if (this.net_minecraft_server_world_ServerWorld_getWorld().getDifficulty() == Difficulty.PEACEFUL && this.net_minecraft_server_world_ServerWorld_getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.age % 20 == 0) {
                float f;
                if (this.getHealth() < this.getMaxHealth()) {
                    this.heal(1.0f);
                }
                if ((f = this.hungerManager.getSaturationLevel()) < 20.0f) {
                    this.hungerManager.setSaturationLevel(f + 1.0f);
                }
            }
            if (this.age % 10 == 0 && this.hungerManager.isNotFull()) {
                this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
            }
        }
    }

    @Override
    public void onLanding() {
        if (this.getHealth() > 0.0f && this.fallStartPos != null) {
            Criteria.FALL_FROM_HEIGHT.trigger(this, this.fallStartPos);
        }
        this.fallStartPos = null;
        super.onLanding();
    }

    public void tickFallStartPos() {
        if (this.fallDistance > 0.0 && this.fallStartPos == null) {
            this.fallStartPos = this.getPos();
            if (this.currentExplosionImpactPos != null && this.currentExplosionImpactPos.y <= this.fallStartPos.y) {
                Criteria.FALL_AFTER_EXPLOSION.trigger(this, this.currentExplosionImpactPos, this.explodedBy);
            }
        }
    }

    public void tickVehicleInLavaRiding() {
        if (this.getVehicle() != null && this.getVehicle().isInLava()) {
            if (this.vehicleInLavaRidingPos == null) {
                this.vehicleInLavaRidingPos = this.getPos();
            } else {
                Criteria.RIDE_ENTITY_IN_LAVA.trigger(this, this.vehicleInLavaRidingPos);
            }
        }
        if (!(this.vehicleInLavaRidingPos == null || this.getVehicle() != null && this.getVehicle().isInLava())) {
            this.vehicleInLavaRidingPos = null;
        }
    }

    private void updateScores(ScoreboardCriterion criterion, int score) {
        this.getScoreboard().forEachScore(criterion, this, innerScore -> innerScore.setScore(score));
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.emitGameEvent(GameEvent.ENTITY_DIE);
        boolean bl = this.net_minecraft_server_world_ServerWorld_getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
        if (bl) {
            Text text = this.getDamageTracker().getDeathMessage();
            this.networkHandler.send(new DeathMessageS2CPacket(this.getId(), text), PacketCallbacks.of(() -> {
                int i = 256;
                String string = text.asTruncatedString(256);
                MutableText text2 = Text.translatable("death.attack.message_too_long", Text.literal(string).formatted(Formatting.YELLOW));
                MutableText text3 = Text.translatable("death.attack.even_more_magic", this.getDisplayName()).styled(style -> style.withHoverEvent(new HoverEvent.ShowText(text2)));
                return new DeathMessageS2CPacket(this.getId(), text3);
            }));
            Team abstractTeam = this.getScoreboardTeam();
            if (abstractTeam == null || ((AbstractTeam)abstractTeam).getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
                this.server.net_minecraft_server_PlayerManager_getPlayerManager().broadcast(text, false);
            } else if (((AbstractTeam)abstractTeam).getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
                this.server.net_minecraft_server_PlayerManager_getPlayerManager().sendToTeam(this, text);
            } else if (((AbstractTeam)abstractTeam).getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
                this.server.net_minecraft_server_PlayerManager_getPlayerManager().sendToOtherTeams(this, text);
            }
        } else {
            this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getId(), ScreenTexts.EMPTY));
        }
        this.dropShoulderEntities();
        if (this.net_minecraft_server_world_ServerWorld_getWorld().getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
            this.forgiveMobAnger();
        }
        if (!this.isSpectator()) {
            this.drop(this.net_minecraft_server_world_ServerWorld_getWorld(), damageSource);
        }
        this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this, ScoreAccess::incrementScore);
        LivingEntity livingEntity = this.getPrimeAdversary();
        if (livingEntity != null) {
            this.incrementStat(Stats.KILLED_BY.getOrCreateStat(livingEntity.getType()));
            livingEntity.updateKilledAdvancementCriterion(this, damageSource);
            this.onKilledBy(livingEntity);
        }
        this.net_minecraft_server_world_ServerWorld_getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
        this.incrementStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        this.extinguish();
        this.setFrozenTicks(0);
        this.setOnFire(false);
        this.getDamageTracker().update();
        this.setLastDeathPos(Optional.of(GlobalPos.create(this.net_minecraft_server_world_ServerWorld_getWorld().getRegistryKey(), this.getBlockPos())));
        this.setLoaded(false);
    }

    private void forgiveMobAnger() {
        Box box = new Box(this.getBlockPos()).expand(32.0, 10.0, 32.0);
        this.net_minecraft_server_world_ServerWorld_getWorld().getEntitiesByClass(MobEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR).stream().filter(entity -> entity instanceof Angerable).forEach(entity -> ((Angerable)((Object)entity)).forgive(this.net_minecraft_server_world_ServerWorld_getWorld(), this));
    }

    @Override
    public void updateKilledAdvancementCriterion(Entity entityKilled, DamageSource damageSource) {
        if (entityKilled == this) {
            return;
        }
        super.updateKilledAdvancementCriterion(entityKilled, damageSource);
        this.getScoreboard().forEachScore(ScoreboardCriterion.TOTAL_KILL_COUNT, this, ScoreAccess::incrementScore);
        if (entityKilled instanceof PlayerEntity) {
            this.incrementStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forEachScore(ScoreboardCriterion.PLAYER_KILL_COUNT, this, ScoreAccess::incrementScore);
        } else {
            this.incrementStat(Stats.MOB_KILLS);
        }
        this.updateScoreboardScore(this, entityKilled, ScoreboardCriterion.TEAM_KILLS);
        this.updateScoreboardScore(entityKilled, this, ScoreboardCriterion.KILLED_BY_TEAMS);
        Criteria.PLAYER_KILLED_ENTITY.trigger(this, entityKilled, damageSource);
    }

    private void updateScoreboardScore(ScoreHolder targetScoreHolder, ScoreHolder aboutScoreHolder, ScoreboardCriterion[] criterions) {
        int i;
        Team team = this.getScoreboard().getScoreHolderTeam(aboutScoreHolder.getNameForScoreboard());
        if (team != null && (i = team.getColor().getColorIndex()) >= 0 && i < criterions.length) {
            this.getScoreboard().forEachScore(criterions[i], targetScoreHolder, ScoreAccess::incrementScore);
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        PlayerEntity playerEntity2;
        PersistentProjectileEntity persistentProjectileEntity;
        Entity entity2;
        PlayerEntity playerEntity;
        if (this.isInvulnerableTo(world, source)) {
            return false;
        }
        Entity entity = source.getAttacker();
        if (entity instanceof PlayerEntity && !this.shouldDamagePlayer(playerEntity = (PlayerEntity)entity)) {
            return false;
        }
        if (entity instanceof PersistentProjectileEntity && (entity2 = (persistentProjectileEntity = (PersistentProjectileEntity)entity).net_minecraft_entity_Entity_getOwner()) instanceof PlayerEntity && !this.shouldDamagePlayer(playerEntity2 = (PlayerEntity)entity2)) {
            return false;
        }
        return super.damage(world, source, amount);
    }

    @Override
    public boolean shouldDamagePlayer(PlayerEntity player) {
        if (!this.isPvpEnabled()) {
            return false;
        }
        return super.shouldDamagePlayer(player);
    }

    private boolean isPvpEnabled() {
        return this.server.isPvpEnabled();
    }

    public TeleportTarget getRespawnTarget(boolean alive, TeleportTarget.PostDimensionTransition postDimensionTransition) {
        Respawn respawn = this.getRespawn();
        ServerWorld serverWorld = this.server.getWorld(Respawn.getDimension(respawn));
        if (serverWorld != null && respawn != null) {
            Optional<RespawnPos> optional = ServerPlayerEntity.findRespawnPosition(serverWorld, respawn, alive);
            if (optional.isPresent()) {
                RespawnPos respawnPos = optional.get();
                return new TeleportTarget(serverWorld, respawnPos.pos(), Vec3d.ZERO, respawnPos.yaw(), 0.0f, postDimensionTransition);
            }
            return TeleportTarget.missingSpawnBlock(this.server.getOverworld(), this, postDimensionTransition);
        }
        return new TeleportTarget(this.server.getOverworld(), this, postDimensionTransition);
    }

    public boolean canReceiveWaypoints() {
        return this.getAttributeValue(EntityAttributes.WAYPOINT_RECEIVE_RANGE) > 0.0;
    }

    @Override
    protected void updateAttribute(RegistryEntry<EntityAttribute> attribute) {
        if (attribute.matches(EntityAttributes.WAYPOINT_RECEIVE_RANGE)) {
            ServerWaypointHandler serverWaypointHandler = this.net_minecraft_server_world_ServerWorld_getWorld().getWaypointHandler();
            if (this.getAttributes().getValue(attribute) > 0.0) {
                serverWaypointHandler.addPlayer(this);
            } else {
                serverWaypointHandler.removePlayer(this);
            }
        }
        super.updateAttribute(attribute);
    }

    private static Optional<RespawnPos> findRespawnPosition(ServerWorld world, Respawn respawn, boolean bl) {
        BlockPos blockPos = respawn.pos;
        float f = respawn.angle;
        boolean bl2 = respawn.forced;
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof RespawnAnchorBlock && (bl2 || blockState.get(RespawnAnchorBlock.CHARGES) > 0) && RespawnAnchorBlock.isNether(world)) {
            Optional<Vec3d> optional = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, blockPos);
            if (!bl2 && bl && optional.isPresent()) {
                world.setBlockState(blockPos, (BlockState)blockState.with(RespawnAnchorBlock.CHARGES, blockState.get(RespawnAnchorBlock.CHARGES) - 1), Block.NOTIFY_ALL);
            }
            return optional.map(respawnPos -> RespawnPos.fromCurrentPos(respawnPos, blockPos));
        }
        if (block instanceof BedBlock && BedBlock.isBedWorking(world)) {
            return BedBlock.findWakeUpPosition(EntityType.PLAYER, (CollisionView)world, blockPos, (Direction)blockState.get(BedBlock.FACING), f).map(respawnPos -> RespawnPos.fromCurrentPos(respawnPos, blockPos));
        }
        if (!bl2) {
            return Optional.empty();
        }
        boolean bl3 = block.canMobSpawnInside(blockState);
        BlockState blockState2 = world.getBlockState(blockPos.net_minecraft_util_math_BlockPos_up());
        boolean bl4 = blockState2.getBlock().canMobSpawnInside(blockState2);
        if (bl3 && bl4) {
            return Optional.of(new RespawnPos(new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.1, (double)blockPos.getZ() + 0.5), f));
        }
        return Optional.empty();
    }

    public void detachForDimensionChange() {
        this.detach();
        this.net_minecraft_server_world_ServerWorld_getWorld().removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
        if (!this.notInAnyWorld) {
            this.notInAnyWorld = true;
            this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_WON, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
            this.seenCredits = true;
        }
    }

    @Override
    @Nullable
    public ServerPlayerEntity net_minecraft_server_network_ServerPlayerEntity_teleportTo(TeleportTarget teleportTarget) {
        if (this.isRemoved()) {
            return null;
        }
        if (teleportTarget.missingRespawnBlock()) {
            this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
        }
        ServerWorld serverWorld = teleportTarget.world();
        ServerWorld serverWorld2 = this.net_minecraft_server_world_ServerWorld_getWorld();
        RegistryKey<World> registryKey = serverWorld2.getRegistryKey();
        if (!teleportTarget.boolean_asPassenger()) {
            this.dismountVehicle();
        }
        if (serverWorld.getRegistryKey() == registryKey) {
            this.networkHandler.requestTeleport(PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives());
            this.networkHandler.syncWithPlayerPosition();
            teleportTarget.postTeleportTransition().onTransition(this);
            return this;
        }
        this.inTeleportationState = true;
        WorldProperties worldProperties = serverWorld.net_minecraft_world_WorldProperties_getLevelProperties();
        this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(this.createCommonPlayerSpawnInfo(serverWorld), PlayerRespawnS2CPacket.KEEP_ALL));
        this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
        PlayerManager playerManager = this.server.net_minecraft_server_PlayerManager_getPlayerManager();
        playerManager.sendCommandTree(this);
        serverWorld2.removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
        this.unsetRemoved();
        Profiler profiler = Profilers.get();
        profiler.push("moving");
        if (registryKey == World.OVERWORLD && serverWorld.getRegistryKey() == World.NETHER) {
            this.enteredNetherPos = this.getPos();
        }
        profiler.pop();
        profiler.push("placing");
        this.setServerWorld(serverWorld);
        this.networkHandler.requestTeleport(PlayerPosition.fromTeleportTarget(teleportTarget), teleportTarget.relatives());
        this.networkHandler.syncWithPlayerPosition();
        serverWorld.onDimensionChanged(this);
        profiler.pop();
        this.worldChanged(serverWorld2);
        this.clearActiveItem();
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
        playerManager.sendWorldInfo(this, serverWorld);
        playerManager.sendPlayerStatus(this);
        playerManager.sendStatusEffects(this);
        teleportTarget.postTeleportTransition().onTransition(this);
        this.syncedExperience = -1;
        this.syncedHealth = -1.0f;
        this.syncedFoodLevel = -1;
        this.teleportSpectatingPlayers(teleportTarget, serverWorld2);
        return this;
    }

    @Override
    public void rotate(float yaw, float pitch) {
        this.networkHandler.sendPacket(new PlayerRotationS2CPacket(yaw, pitch));
    }

    private void worldChanged(ServerWorld origin) {
        RegistryKey<World> registryKey = origin.getRegistryKey();
        RegistryKey<World> registryKey2 = this.net_minecraft_server_world_ServerWorld_getWorld().getRegistryKey();
        Criteria.CHANGED_DIMENSION.trigger(this, registryKey, registryKey2);
        if (registryKey == World.NETHER && registryKey2 == World.OVERWORLD && this.enteredNetherPos != null) {
            Criteria.NETHER_TRAVEL.trigger(this, this.enteredNetherPos);
        }
        if (registryKey2 != World.NETHER) {
            this.enteredNetherPos = null;
        }
    }

    @Override
    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        if (spectator.isSpectator()) {
            return this.getCameraEntity() == this;
        }
        if (this.isSpectator()) {
            return false;
        }
        return super.canBeSpectated(spectator);
    }

    @Override
    public void sendPickup(Entity item, int count) {
        super.sendPickup(item, count);
        this.currentScreenHandler.sendContentUpdates();
    }

    @Override
    public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
        Direction direction = this.net_minecraft_server_world_ServerWorld_getWorld().getBlockState(pos).get(HorizontalFacingBlock.FACING);
        if (this.isSleeping() || !this.isAlive()) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.OTHER_PROBLEM));
        }
        if (!this.net_minecraft_server_world_ServerWorld_getWorld().getDimension().natural()) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE));
        }
        if (!this.isBedWithinRange(pos, direction)) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.TOO_FAR_AWAY));
        }
        if (this.isBedObstructed(pos, direction)) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.OBSTRUCTED));
        }
        this.setSpawnPoint(new Respawn(this.net_minecraft_server_world_ServerWorld_getWorld().getRegistryKey(), pos, this.getYaw(), false), true);
        if (this.net_minecraft_server_world_ServerWorld_getWorld().isDay()) {
            return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW));
        }
        if (!this.isCreative()) {
            double d = 8.0;
            double e = 5.0;
            Vec3d vec3d = Vec3d.ofBottomCenter(pos);
            List<HostileEntity> list = this.net_minecraft_server_world_ServerWorld_getWorld().getEntitiesByClass(HostileEntity.class, new Box(vec3d.getX() - 8.0, vec3d.getY() - 5.0, vec3d.getZ() - 8.0, vec3d.getX() + 8.0, vec3d.getY() + 5.0, vec3d.getZ() + 8.0), entity -> entity.isAngryAt(this.net_minecraft_server_world_ServerWorld_getWorld(), this));
            if (!list.isEmpty()) {
                return Either.left((Object)((Object)PlayerEntity.SleepFailureReason.NOT_SAFE));
            }
        }
        Either either = super.trySleep(pos).ifRight(unit -> {
            this.incrementStat(Stats.SLEEP_IN_BED);
            Criteria.SLEPT_IN_BED.trigger(this);
        });
        if (!this.net_minecraft_server_world_ServerWorld_getWorld().isSleepingEnabled()) {
            this.sendMessage(Text.translatable("sleep.not_possible"), true);
        }
        this.net_minecraft_server_world_ServerWorld_getWorld().updateSleepingPlayers();
        return either;
    }

    @Override
    public void sleep(BlockPos pos) {
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        super.sleep(pos);
    }

    private boolean isBedWithinRange(BlockPos pos, Direction direction) {
        return this.isBedWithinRange(pos) || this.isBedWithinRange(pos.net_minecraft_util_math_BlockPos_offset(direction.getOpposite()));
    }

    private boolean isBedWithinRange(BlockPos pos) {
        Vec3d vec3d = Vec3d.ofBottomCenter(pos);
        return Math.abs(this.getX() - vec3d.getX()) <= 3.0 && Math.abs(this.getY() - vec3d.getY()) <= 2.0 && Math.abs(this.getZ() - vec3d.getZ()) <= 3.0;
    }

    private boolean isBedObstructed(BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.net_minecraft_util_math_BlockPos_up();
        return !this.doesNotSuffocate(blockPos) || !this.doesNotSuffocate(blockPos.net_minecraft_util_math_BlockPos_offset(direction.getOpposite()));
    }

    @Override
    public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers) {
        if (this.isSleeping()) {
            this.net_minecraft_server_world_ServerWorld_getWorld().net_minecraft_server_world_ServerChunkManager_getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, EntityAnimationS2CPacket.WAKE_UP));
        }
        super.wakeUp(skipSleepTimer, updateSleepingPlayers);
        if (this.networkHandler != null) {
            this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        }
    }

    @Override
    public boolean isInvulnerableTo(ServerWorld world, DamageSource source) {
        return super.isInvulnerableTo(world, source) || this.isInTeleportationState() && !source.isOf(DamageTypes.ENDER_PEARL) || !this.isLoaded();
    }

    @Override
    protected void applyMovementEffects(ServerWorld world, BlockPos pos) {
        if (!this.isSpectator()) {
            super.applyMovementEffects(world, pos);
        }
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        if (this.spawnExtraParticlesOnFall && onGround && this.fallDistance > 0.0) {
            Vec3d vec3d = landedPosition.toCenterPos().add(0.0, 0.5, 0.0);
            int i = (int)MathHelper.clamp(50.0 * this.fallDistance, 0.0, 200.0);
            this.net_minecraft_server_world_ServerWorld_getWorld().spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), vec3d.x, vec3d.y, vec3d.z, i, 0.3f, 0.3f, 0.3f, 0.15f);
            this.spawnExtraParticlesOnFall = false;
        }
        super.fall(heightDifference, onGround, state, landedPosition);
    }

    @Override
    public void onExplodedBy(@Nullable Entity entity) {
        super.onExplodedBy(entity);
        this.currentExplosionImpactPos = this.getPos();
        this.explodedBy = entity;
        this.setIgnoreFallDamageFromCurrentExplosion(entity != null && entity.getType() == EntityType.WIND_CHARGE);
    }

    @Override
    protected void tickCramming() {
        if (this.net_minecraft_server_world_ServerWorld_getWorld().getTickManager().shouldTick()) {
            super.tickCramming();
        }
    }

    @Override
    public void openEditSignScreen(SignBlockEntity sign, boolean front) {
        this.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.net_minecraft_server_world_ServerWorld_getWorld(), sign.getPos()));
        this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(sign.getPos(), front));
    }

    @Override
    public void openDialog(RegistryEntry<Dialog> dialog) {
        this.networkHandler.sendPacket(new ShowDialogS2CPacket(dialog));
    }

    private void incrementScreenHandlerSyncId() {
        this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
    }

    @Override
    public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
        if (factory == null) {
            return OptionalInt.empty();
        }
        if (this.currentScreenHandler != this.playerScreenHandler) {
            this.closeHandledScreen();
        }
        this.incrementScreenHandlerSyncId();
        ScreenHandler screenHandler = factory.createMenu(this.screenHandlerSyncId, this.getInventory(), this);
        if (screenHandler == null) {
            if (this.isSpectator()) {
                this.sendMessage(Text.translatable("container.spectatorCantOpen").formatted(Formatting.RED), true);
            }
            return OptionalInt.empty();
        }
        this.networkHandler.sendPacket(new OpenScreenS2CPacket(screenHandler.syncId, screenHandler.getType(), factory.getDisplayName()));
        this.onScreenHandlerOpened(screenHandler);
        this.currentScreenHandler = screenHandler;
        return OptionalInt.of(this.screenHandlerSyncId);
    }

    @Override
    public void sendTradeOffers(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
        this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(syncId, offers, levelProgress, experience, leveled, refreshable));
    }

    @Override
    public void openHorseInventory(AbstractHorseEntity horse, Inventory inventory) {
        if (this.currentScreenHandler != this.playerScreenHandler) {
            this.closeHandledScreen();
        }
        this.incrementScreenHandlerSyncId();
        int i = horse.getInventoryColumns();
        this.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(this.screenHandlerSyncId, 1, horse.getId()));
        this.currentScreenHandler = new HorseScreenHandler(this.screenHandlerSyncId, this.getInventory(), inventory, horse, 1);
        this.onScreenHandlerOpened(this.currentScreenHandler);
    }

    @Override
    public void useBook(ItemStack book, Hand hand) {
        if (book.contains(DataComponentTypes.WRITTEN_BOOK_CONTENT)) {
            if (WrittenBookContentComponent.resolveInStack(book, this.getCommandSource(), this)) {
                this.currentScreenHandler.sendContentUpdates();
            }
            this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
        }
    }

    @Override
    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
        this.networkHandler.sendPacket(BlockEntityUpdateS2CPacket.create(commandBlock, BlockEntity::createComponentlessNbt));
    }

    @Override
    public void closeHandledScreen() {
        this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.currentScreenHandler.syncId));
        this.onHandledScreenClosed();
    }

    @Override
    public void onHandledScreenClosed() {
        this.currentScreenHandler.onClosed(this);
        this.playerScreenHandler.copySharedSlots(this.currentScreenHandler);
        this.currentScreenHandler = this.playerScreenHandler;
    }

    @Override
    public void tickRiding() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.tickRiding();
        this.increaseRidingMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
    }

    public void increaseTravelMotionStats(double deltaX, double deltaY, double deltaZ) {
        if (this.hasVehicle() || ServerPlayerEntity.isZero(deltaX, deltaY, deltaZ)) {
            return;
        }
        if (this.isSwimming()) {
            int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0f);
            if (i > 0) {
                this.increaseStat(Stats.SWIM_ONE_CM, i);
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isSubmergedIn(FluidTags.WATER)) {
            int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0f);
            if (i > 0) {
                this.increaseStat(Stats.WALK_UNDER_WATER_ONE_CM, i);
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isTouchingWater()) {
            int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0f);
            if (i > 0) {
                this.increaseStat(Stats.WALK_ON_WATER_ONE_CM, i);
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isClimbing()) {
            if (deltaY > 0.0) {
                this.increaseStat(Stats.CLIMB_ONE_CM, (int)Math.round(deltaY * 100.0));
            }
        } else if (this.isOnGround()) {
            int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0f);
            if (i > 0) {
                if (this.isSprinting()) {
                    this.increaseStat(Stats.SPRINT_ONE_CM, i);
                    this.addExhaustion(0.1f * (float)i * 0.01f);
                } else if (this.isInSneakingPose()) {
                    this.increaseStat(Stats.CROUCH_ONE_CM, i);
                    this.addExhaustion(0.0f * (float)i * 0.01f);
                } else {
                    this.increaseStat(Stats.WALK_ONE_CM, i);
                    this.addExhaustion(0.0f * (float)i * 0.01f);
                }
            }
        } else if (this.isGliding()) {
            int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0f);
            this.increaseStat(Stats.AVIATE_ONE_CM, i);
        } else {
            int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0f);
            if (i > 25) {
                this.increaseStat(Stats.FLY_ONE_CM, i);
            }
        }
    }

    private void increaseRidingMotionStats(double deltaX, double deltaY, double deltaZ) {
        if (!this.hasVehicle() || ServerPlayerEntity.isZero(deltaX, deltaY, deltaZ)) {
            return;
        }
        int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0f);
        Entity entity = this.getVehicle();
        if (entity instanceof AbstractMinecartEntity) {
            this.increaseStat(Stats.MINECART_ONE_CM, i);
        } else if (entity instanceof AbstractBoatEntity) {
            this.increaseStat(Stats.BOAT_ONE_CM, i);
        } else if (entity instanceof PigEntity) {
            this.increaseStat(Stats.PIG_ONE_CM, i);
        } else if (entity instanceof AbstractHorseEntity) {
            this.increaseStat(Stats.HORSE_ONE_CM, i);
        } else if (entity instanceof StriderEntity) {
            this.increaseStat(Stats.STRIDER_ONE_CM, i);
        } else if (entity instanceof HappyGhastEntity) {
            this.increaseStat(Stats.HAPPY_GHAST_ONE_CM, i);
        }
    }

    private static boolean isZero(double deltaX, double deltaY, double deltaZ) {
        return deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0;
    }

    @Override
    public void increaseStat(Stat<?> stat, int amount) {
        this.statHandler.increaseStat(this, stat, amount);
        this.getScoreboard().forEachScore(stat, this, score -> score.incrementScore(amount));
    }

    @Override
    public void resetStat(Stat<?> stat) {
        this.statHandler.setStat(this, stat, 0);
        this.getScoreboard().forEachScore(stat, this, ScoreAccess::resetScore);
    }

    @Override
    public int unlockRecipes(Collection<RecipeEntry<?>> recipes) {
        return this.recipeBook.unlockRecipes(recipes, this);
    }

    @Override
    public void onRecipeCrafted(RecipeEntry<?> recipe, List<ItemStack> ingredients) {
        Criteria.RECIPE_CRAFTED.trigger(this, recipe.id(), ingredients);
    }

    @Override
    public void unlockRecipes(List<RegistryKey<Recipe<?>>> recipes) {
        List<RecipeEntry<?>> list = recipes.stream().flatMap(recipeKey -> this.server.getRecipeManager().get((RegistryKey<Recipe<?>>)recipeKey).stream()).collect(Collectors.toList());
        this.unlockRecipes((Collection<RecipeEntry<?>>)list);
    }

    @Override
    public int lockRecipes(Collection<RecipeEntry<?>> recipes) {
        return this.recipeBook.lockRecipes(recipes, this);
    }

    @Override
    public void jump() {
        super.jump();
        this.incrementStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.addExhaustion(0.2f);
        } else {
            this.addExhaustion(0.05f);
        }
    }

    @Override
    public void addExperience(int experience) {
        super.addExperience(experience);
        this.syncedExperience = -1;
    }

    public void onDisconnect() {
        this.disconnected = true;
        this.removeAllPassengers();
        if (this.isSleeping()) {
            this.wakeUp(true, false);
        }
    }

    public boolean isDisconnected() {
        return this.disconnected;
    }

    public void markHealthDirty() {
        this.syncedHealth = -1.0E8f;
    }

    @Override
    public void sendMessage(Text message, boolean overlay) {
        this.sendMessageToClient(message, overlay);
    }

    @Override
    protected void consumeItem() {
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, EntityStatuses.CONSUME_ITEM));
            super.consumeItem();
        }
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        super.lookAt(anchorPoint, target);
        this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, target.x, target.y, target.z));
    }

    public void lookAtEntity(EntityAnchorArgumentType.EntityAnchor anchorPoint, Entity targetEntity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
        Vec3d vec3d = targetAnchor.positionAt(targetEntity);
        super.lookAt(anchorPoint, vec3d);
        this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, targetEntity, targetAnchor));
    }

    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
        this.sculkShriekerWarningManager = oldPlayer.sculkShriekerWarningManager;
        this.session = oldPlayer.session;
        this.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode(), oldPlayer.interactionManager.getPreviousGameMode());
        this.sendAbilitiesUpdate();
        if (alive) {
            this.getAttributes().setBaseFrom(oldPlayer.getAttributes());
            this.getAttributes().addPersistentModifiersFrom(oldPlayer.getAttributes());
            this.setHealth(oldPlayer.getHealth());
            this.hungerManager = oldPlayer.hungerManager;
            for (StatusEffectInstance statusEffectInstance : oldPlayer.getStatusEffects()) {
                this.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
            }
            this.getInventory().clone(oldPlayer.getInventory());
            this.experienceLevel = oldPlayer.experienceLevel;
            this.totalExperience = oldPlayer.totalExperience;
            this.experienceProgress = oldPlayer.experienceProgress;
            this.setScore(oldPlayer.getScore());
            this.portalManager = oldPlayer.portalManager;
        } else {
            this.getAttributes().setBaseFrom(oldPlayer.getAttributes());
            this.setHealth(this.getMaxHealth());
            if (this.net_minecraft_server_world_ServerWorld_getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) {
                this.getInventory().clone(oldPlayer.getInventory());
                this.experienceLevel = oldPlayer.experienceLevel;
                this.totalExperience = oldPlayer.totalExperience;
                this.experienceProgress = oldPlayer.experienceProgress;
                this.setScore(oldPlayer.getScore());
            }
        }
        this.enchantingTableSeed = oldPlayer.enchantingTableSeed;
        this.enderChestInventory = oldPlayer.enderChestInventory;
        this.getDataTracker().set(PLAYER_MODEL_PARTS, (Byte)oldPlayer.getDataTracker().get(PLAYER_MODEL_PARTS));
        this.syncedExperience = -1;
        this.syncedHealth = -1.0f;
        this.syncedFoodLevel = -1;
        this.recipeBook.copyFrom(oldPlayer.recipeBook);
        this.seenCredits = oldPlayer.seenCredits;
        this.enteredNetherPos = oldPlayer.enteredNetherPos;
        this.chunkFilter = oldPlayer.chunkFilter;
        this.setShoulderEntityLeft(oldPlayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(oldPlayer.getShoulderEntityRight());
        this.setLastDeathPos(oldPlayer.getLastDeathPos());
    }

    @Override
    protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {
        super.onStatusEffectApplied(effect, source);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect, true));
        if (effect.equals(StatusEffects.LEVITATION)) {
            this.levitationStartTick = this.age;
            this.levitationStartPos = this.getPos();
        }
        Criteria.EFFECTS_CHANGED.trigger(this, source);
    }

    @Override
    protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
        super.onStatusEffectUpgraded(effect, reapplyEffect, source);
        this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect, false));
        Criteria.EFFECTS_CHANGED.trigger(this, source);
    }

    @Override
    protected void onStatusEffectsRemoved(Collection<StatusEffectInstance> effects) {
        super.onStatusEffectsRemoved(effects);
        for (StatusEffectInstance statusEffectInstance : effects) {
            this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getId(), statusEffectInstance.getEffectType()));
            if (!statusEffectInstance.equals(StatusEffects.LEVITATION)) continue;
            this.levitationStartPos = null;
        }
        Criteria.EFFECTS_CHANGED.trigger(this, (Entity)null);
    }

    @Override
    public void requestTeleport(double destX, double destY, double destZ) {
        this.networkHandler.requestTeleport(new PlayerPosition(new Vec3d(destX, destY, destZ), Vec3d.ZERO, 0.0f, 0.0f), PositionFlag.combine(PositionFlag.DELTA, PositionFlag.ROT));
    }

    @Override
    public void requestTeleportOffset(double offsetX, double offsetY, double offsetZ) {
        this.networkHandler.requestTeleport(new PlayerPosition(new Vec3d(offsetX, offsetY, offsetZ), Vec3d.ZERO, 0.0f, 0.0f), PositionFlag.VALUES);
    }

    @Override
    public boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, boolean resetCamera) {
        boolean bl;
        if (this.isSleeping()) {
            this.wakeUp(true, true);
        }
        if (resetCamera) {
            this.setCameraEntity(this);
        }
        if (bl = super.teleport(world, destX, destY, destZ, flags, yaw, pitch, resetCamera)) {
            this.setHeadYaw(flags.contains((Object)PositionFlag.Y_ROT) ? this.getHeadYaw() + yaw : yaw);
        }
        return bl;
    }

    @Override
    public void refreshPositionAfterTeleport(double x, double y, double z) {
        super.refreshPositionAfterTeleport(x, y, z);
        this.networkHandler.syncWithPlayerPosition();
    }

    @Override
    public void addCritParticles(Entity target) {
        this.net_minecraft_server_world_ServerWorld_getWorld().net_minecraft_server_world_ServerChunkManager_getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, EntityAnimationS2CPacket.CRIT));
    }

    @Override
    public void addEnchantedHitParticles(Entity target) {
        this.net_minecraft_server_world_ServerWorld_getWorld().net_minecraft_server_world_ServerChunkManager_getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, EntityAnimationS2CPacket.ENCHANTED_HIT));
    }

    @Override
    public void sendAbilitiesUpdate() {
        if (this.networkHandler == null) {
            return;
        }
        this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
        this.updatePotionVisibility();
    }

    @Override
    public ServerWorld net_minecraft_server_world_ServerWorld_getWorld() {
        return (ServerWorld)super.net_minecraft_world_World_getWorld();
    }

    public boolean changeGameMode(GameMode gameMode) {
        boolean bl = this.isSpectator();
        if (!this.interactionManager.changeGameMode(gameMode)) {
            return false;
        }
        this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, gameMode.getIndex()));
        if (gameMode == GameMode.SPECTATOR) {
            this.dropShoulderEntities();
            this.stopRiding();
            EnchantmentHelper.removeLocationBasedEffects(this);
        } else {
            this.setCameraEntity(this);
            if (bl) {
                EnchantmentHelper.applyLocationBasedEffects(this.net_minecraft_server_world_ServerWorld_getWorld(), this);
            }
        }
        this.sendAbilitiesUpdate();
        this.markEffectsDirty();
        return true;
    }

    @Override
    @NotNull
    public GameMode getGameMode() {
        return this.interactionManager.getGameMode();
    }

    public CommandOutput getCommandOutput() {
        return this.commandOutput;
    }

    public ServerCommandSource getCommandSource() {
        return new ServerCommandSource(this.getCommandOutput(), this.getPos(), this.getRotationClient(), this.net_minecraft_server_world_ServerWorld_getWorld(), this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.server, this);
    }

    public void sendMessage(Text message) {
        this.sendMessageToClient(message, false);
    }

    public void sendMessageToClient(Text message, boolean overlay) {
        if (!this.acceptsMessage(overlay)) {
            return;
        }
        this.networkHandler.send(new GameMessageS2CPacket(message, overlay), PacketCallbacks.of(() -> {
            if (this.acceptsMessage(false)) {
                int i = 256;
                String string = message.asTruncatedString(256);
                MutableText text2 = Text.literal(string).formatted(Formatting.YELLOW);
                return new GameMessageS2CPacket(Text.translatable("multiplayer.message_not_delivered", text2).formatted(Formatting.RED), false);
            }
            return null;
        }));
    }

    public void sendChatMessage(SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params) {
        if (this.acceptsChatMessage()) {
            message.send(this, filterMaskEnabled, params);
        }
    }

    public String getIp() {
        SocketAddress socketAddress = this.networkHandler.getConnectionAddress();
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
            return InetAddresses.toAddrString((InetAddress)inetSocketAddress.getAddress());
        }
        return "<unknown>";
    }

    public void setClientOptions(SyncedClientOptions clientOptions) {
        this.language = clientOptions.language();
        this.viewDistance = clientOptions.viewDistance();
        this.clientChatVisibility = clientOptions.chatVisibility();
        this.clientChatColorsEnabled = clientOptions.chatColorsEnabled();
        this.filterText = clientOptions.filtersText();
        this.allowServerListing = clientOptions.allowsServerListing();
        this.particlesMode = clientOptions.particleStatus();
        this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)clientOptions.playerModelParts());
        this.getDataTracker().set(MAIN_ARM, (byte)clientOptions.mainArm().getId());
    }

    public SyncedClientOptions getClientOptions() {
        byte i = (Byte)this.getDataTracker().get(PLAYER_MODEL_PARTS);
        Arm arm = Arm.BY_ID.apply(((Byte)this.getDataTracker().get(MAIN_ARM)).byteValue());
        return new SyncedClientOptions(this.language, this.viewDistance, this.clientChatVisibility, this.clientChatColorsEnabled, i, arm, this.filterText, this.allowServerListing, this.particlesMode);
    }

    public boolean areClientChatColorsEnabled() {
        return this.clientChatColorsEnabled;
    }

    public ChatVisibility getClientChatVisibility() {
        return this.clientChatVisibility;
    }

    private boolean acceptsMessage(boolean overlay) {
        if (this.clientChatVisibility == ChatVisibility.HIDDEN) {
            return overlay;
        }
        return true;
    }

    private boolean acceptsChatMessage() {
        return this.clientChatVisibility == ChatVisibility.FULL;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public void sendServerMetadata(ServerMetadata metadata) {
        this.networkHandler.sendPacket(new ServerMetadataS2CPacket(metadata.description(), metadata.favicon().map(ServerMetadata.Favicon::iconBytes)));
    }

    @Override
    public int getPermissionLevel() {
        return this.server.getPermissionLevel(this.getGameProfile());
    }

    public void updateLastActionTime() {
        this.lastActionTime = Util.getMeasuringTimeMs();
    }

    public ServerStatHandler getStatHandler() {
        return this.statHandler;
    }

    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    @Override
    protected void updatePotionVisibility() {
        if (this.isSpectator()) {
            this.clearPotionSwirls();
            this.setInvisible(true);
        } else {
            super.updatePotionVisibility();
        }
    }

    public Entity getCameraEntity() {
        return this.cameraEntity == null ? this : this.cameraEntity;
    }

    public void setCameraEntity(@Nullable Entity entity) {
        Entity entity2 = this.getCameraEntity();
        Entity entity3 = this.cameraEntity = entity == null ? this : entity;
        if (entity2 != this.cameraEntity) {
            World world = this.cameraEntity.net_minecraft_world_World_getWorld();
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                this.teleport(serverWorld, this.cameraEntity.getX(), this.cameraEntity.getY(), this.cameraEntity.getZ(), Set.of(), this.getYaw(), this.getPitch(), false);
            }
            if (entity != null) {
                this.net_minecraft_server_world_ServerWorld_getWorld().net_minecraft_server_world_ServerChunkManager_getChunkManager().updatePosition(this);
            }
            this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
            this.networkHandler.syncWithPlayerPosition();
        }
    }

    @Override
    protected void tickPortalCooldown() {
        if (!this.inTeleportationState) {
            super.tickPortalCooldown();
        }
    }

    @Override
    public void attack(Entity target) {
        if (this.isSpectator()) {
            this.setCameraEntity(target);
        } else {
            super.attack(target);
        }
    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public Text getPlayerListName() {
        return null;
    }

    public int getPlayerListOrder() {
        return 0;
    }

    @Override
    public void swingHand(Hand hand) {
        super.swingHand(hand);
        this.resetLastAttackedTicks();
    }

    public boolean isInTeleportationState() {
        return this.inTeleportationState;
    }

    public void onTeleportationDone() {
        this.inTeleportationState = false;
    }

    public PlayerAdvancementTracker getAdvancementTracker() {
        return this.advancementTracker;
    }

    @Nullable
    public Respawn getRespawn() {
        return this.respawn;
    }

    public void setSpawnPointFrom(ServerPlayerEntity player) {
        this.setSpawnPoint(player.respawn, false);
    }

    public void setSpawnPoint(@Nullable Respawn respawn, boolean sendMessage) {
        if (sendMessage && respawn != null && !respawn.posEquals(this.respawn)) {
            this.sendMessage(SET_SPAWN_TEXT);
        }
        this.respawn = respawn;
    }

    public ChunkSectionPos getWatchedSection() {
        return this.watchedSection;
    }

    public void setWatchedSection(ChunkSectionPos section) {
        this.watchedSection = section;
    }

    public ChunkFilter getChunkFilter() {
        return this.chunkFilter;
    }

    public void setChunkFilter(ChunkFilter chunkFilter) {
        this.chunkFilter = chunkFilter;
    }

    @Override
    public void playSoundToPlayer(SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(sound), category, this.getX(), this.getY(), this.getZ(), volume, pitch, this.random.nextLong()));
    }

    @Override
    public ItemEntity dropItem(ItemStack stack, boolean dropAtSelf, boolean retainOwnership) {
        ItemEntity itemEntity = super.dropItem(stack, dropAtSelf, retainOwnership);
        if (retainOwnership) {
            ItemStack itemStack;
            ItemStack itemStack2 = itemStack = itemEntity != null ? itemEntity.getStack() : ItemStack.EMPTY;
            if (!itemStack.isEmpty()) {
                this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
                this.incrementStat(Stats.DROP);
            }
        }
        return itemEntity;
    }

    public TextStream getTextStream() {
        return this.textStream;
    }

    public void setServerWorld(ServerWorld world) {
        this.setWorld(world);
        this.interactionManager.setWorld(world);
    }

    @Nullable
    private static GameMode gameModeFromData(@Nullable ReadView view, String key) {
        return view != null ? (GameMode)view.read(key, GameMode.INDEX_CODEC).orElse(null) : null;
    }

    private GameMode getServerGameMode(@Nullable GameMode backupGameMode) {
        GameMode gameMode = this.server.getForcedGameMode();
        if (gameMode != null) {
            return gameMode;
        }
        return backupGameMode != null ? backupGameMode : this.server.getDefaultGameMode();
    }

    public void readGameModeData(@Nullable ReadView view) {
        this.interactionManager.setGameMode(this.getServerGameMode(ServerPlayerEntity.gameModeFromData(view, "playerGameType")), ServerPlayerEntity.gameModeFromData(view, "previousPlayerGameType"));
    }

    private void writeGameModeData(WriteView view) {
        view.put("playerGameType", GameMode.INDEX_CODEC, this.interactionManager.getGameMode());
        GameMode gameMode = this.interactionManager.getPreviousGameMode();
        view.putNullable("previousPlayerGameType", GameMode.INDEX_CODEC, gameMode);
    }

    @Override
    public boolean shouldFilterText() {
        return this.filterText;
    }

    public boolean shouldFilterMessagesSentTo(ServerPlayerEntity player) {
        if (player == this) {
            return false;
        }
        return this.filterText || player.filterText;
    }

    @Override
    public boolean canModifyAt(ServerWorld world, BlockPos pos) {
        return super.canModifyAt(world, pos) && world.canEntityModifyAt(this, pos);
    }

    @Override
    protected void tickItemStackUsage(ItemStack stack) {
        Criteria.USING_ITEM.trigger(this, stack);
        super.tickItemStackUsage(stack);
    }

    public boolean dropSelectedItem(boolean entireStack) {
        PlayerInventory playerInventory = this.getInventory();
        ItemStack itemStack = playerInventory.dropSelectedItem(entireStack);
        this.currentScreenHandler.getSlotIndex(playerInventory, playerInventory.getSelectedSlot()).ifPresent(index -> this.currentScreenHandler.setReceivedStack(index, playerInventory.getSelectedStack()));
        return this.dropItem(itemStack, false, true) != null;
    }

    @Override
    public void giveOrDropStack(ItemStack stack) {
        if (!this.getInventory().insertStack(stack)) {
            this.dropItem(stack, false);
        }
    }

    public boolean allowsServerListing() {
        return this.allowServerListing;
    }

    @Override
    public Optional<SculkShriekerWarningManager> getSculkShriekerWarningManager() {
        return Optional.of(this.sculkShriekerWarningManager);
    }

    public void setSpawnExtraParticlesOnFall(boolean spawnExtraParticlesOnFall) {
        this.spawnExtraParticlesOnFall = spawnExtraParticlesOnFall;
    }

    @Override
    public void triggerItemPickedUpByEntityCriteria(ItemEntity item) {
        super.triggerItemPickedUpByEntityCriteria(item);
        Entity entity = item.net_minecraft_entity_Entity_getOwner();
        if (entity != null) {
            Criteria.THROWN_ITEM_PICKED_UP_BY_PLAYER.trigger(this, item.getStack(), entity);
        }
    }

    public void setSession(PublicPlayerSession session) {
        this.session = session;
    }

    @Nullable
    public PublicPlayerSession getSession() {
        if (this.session != null && this.session.isKeyExpired()) {
            return null;
        }
        return this.session;
    }

    @Override
    public void tiltScreen(double deltaX, double deltaZ) {
        this.damageTiltYaw = (float)(MathHelper.atan2(deltaZ, deltaX) * 57.2957763671875 - (double)this.getYaw());
        this.networkHandler.sendPacket(new DamageTiltS2CPacket(this));
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        if (super.startRiding(entity, force)) {
            entity.updatePassengerPosition(this);
            this.networkHandler.requestTeleport(new PlayerPosition(this.getPos(), Vec3d.ZERO, 0.0f, 0.0f), PositionFlag.ROT);
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                this.server.net_minecraft_server_PlayerManager_getPlayerManager().sendStatusEffects(livingEntity, this.networkHandler);
            }
            this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
            return true;
        }
        return false;
    }

    @Override
    public void dismountVehicle() {
        Entity entity = this.getVehicle();
        super.dismountVehicle();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            for (StatusEffectInstance statusEffectInstance : livingEntity.getStatusEffects()) {
                this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(entity.getId(), statusEffectInstance.getEffectType()));
            }
        }
        if (entity != null) {
            this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
        }
    }

    public CommonPlayerSpawnInfo createCommonPlayerSpawnInfo(ServerWorld world) {
        return new CommonPlayerSpawnInfo(world.getDimensionEntry(), world.getRegistryKey(), BiomeAccess.hashSeed(world.getSeed()), this.interactionManager.getGameMode(), this.interactionManager.getPreviousGameMode(), world.isDebugWorld(), world.isFlat(), this.getLastDeathPos(), this.getPortalCooldown(), world.getSeaLevel());
    }

    public void setStartRaidPos(BlockPos startRaidPos) {
        this.startRaidPos = startRaidPos;
    }

    public void clearStartRaidPos() {
        this.startRaidPos = null;
    }

    @Nullable
    public BlockPos getStartRaidPos() {
        return this.startRaidPos;
    }

    @Override
    public Vec3d getMovement() {
        Entity entity = this.getVehicle();
        if (entity != null && entity.getControllingPassenger() != this) {
            return entity.getMovement();
        }
        return this.movement;
    }

    public void setMovement(Vec3d movement) {
        this.movement = movement;
    }

    @Override
    protected float getDamageAgainst(Entity target, float baseDamage, DamageSource damageSource) {
        return EnchantmentHelper.getDamage(this.net_minecraft_server_world_ServerWorld_getWorld(), this.getWeaponStack(), target, damageSource, baseDamage);
    }

    @Override
    public void sendEquipmentBreakStatus(Item item, EquipmentSlot slot) {
        super.sendEquipmentBreakStatus(item, slot);
        this.incrementStat(Stats.BROKEN.getOrCreateStat(item));
    }

    public PlayerInput getPlayerInput() {
        return this.playerInput;
    }

    public void setPlayerInput(PlayerInput playerInput) {
        this.playerInput = playerInput;
    }

    public Vec3d getInputVelocityForMinecart() {
        float f;
        float f2 = this.playerInput.left() == this.playerInput.right() ? 0.0f : (f = this.playerInput.left() ? 1.0f : -1.0f);
        float g = this.playerInput.forward() == this.playerInput.backward() ? 0.0f : (this.playerInput.forward() ? 1.0f : -1.0f);
        return ServerPlayerEntity.movementInputToVelocity(new Vec3d(f, 0.0, g), 1.0f, this.getYaw());
    }

    public void addEnderPearl(EnderPearlEntity enderPearl) {
        this.enderPearls.add(enderPearl);
    }

    public void removeEnderPearl(EnderPearlEntity enderPearl) {
        this.enderPearls.remove(enderPearl);
    }

    public Set<EnderPearlEntity> getEnderPearls() {
        return this.enderPearls;
    }

    public long handleThrownEnderPearl(EnderPearlEntity enderPearl) {
        World world = enderPearl.net_minecraft_world_World_getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            ChunkPos chunkPos = enderPearl.getChunkPos();
            this.addEnderPearl(enderPearl);
            serverWorld.resetIdleTimeout();
            return ServerPlayerEntity.addEnderPearlTicket(serverWorld, chunkPos) - 1L;
        }
        return 0L;
    }

    public static long addEnderPearlTicket(ServerWorld world, ChunkPos chunkPos) {
        world.net_minecraft_server_world_ServerChunkManager_getChunkManager().addTicket(ChunkTicketType.ENDER_PEARL, chunkPos, 2);
        return ChunkTicketType.ENDER_PEARL.expiryTicks();
    }

    @Override
    public World net_minecraft_world_World_getWorld() {
        return this.net_minecraft_server_world_ServerWorld_getWorld();
    }

    @Override
    @Nullable
    public Entity net_minecraft_entity_Entity_teleportTo(TeleportTarget teleportTarget) {
        return this.net_minecraft_server_network_ServerPlayerEntity_teleportTo(teleportTarget);
    }

    public static final class Respawn
    extends Record {
        final private RegistryKey<World> dimension;
        final BlockPos pos;
        final float angle;
        final boolean forced;
        final static public Codec<Respawn> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)World.CODEC.optionalFieldOf("dimension", World.OVERWORLD).forGetter(Respawn::dimension), (App)BlockPos.CODEC.fieldOf("pos").forGetter(Respawn::pos), (App)Codec.FLOAT.optionalFieldOf("angle", (Object)Float.valueOf(0.0f)).forGetter(Respawn::angle), (App)Codec.BOOL.optionalFieldOf("forced", (Object)false).forGetter(Respawn::forced)).apply((Applicative)instance, Respawn::new));

        public Respawn(RegistryKey<World> registryKey, BlockPos blockPos, float f, boolean bl) {
            this.dimension = registryKey;
            this.pos = blockPos;
            this.angle = f;
            this.forced = bl;
        }

        static RegistryKey<World> getDimension(@Nullable Respawn respawn) {
            return respawn != null ? respawn.dimension() : World.OVERWORLD;
        }

        public boolean posEquals(@Nullable Respawn respawn) {
            return respawn != null && this.dimension == respawn.dimension && this.pos.equals(respawn.pos);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Respawn.class, "dimension;pos;angle;forced", "dimension", "pos", "angle", "forced"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Respawn.class, "dimension;pos;angle;forced", "dimension", "pos", "angle", "forced"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Respawn.class, "dimension;pos;angle;forced", "dimension", "pos", "angle", "forced"}, this, object);
        }

        public RegistryKey<World> dimension() {
            return this.dimension;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public float angle() {
            return this.angle;
        }

        public boolean forced() {
            return this.forced;
        }
    }

    record RespawnPos(Vec3d pos, float yaw) {
        public static RespawnPos fromCurrentPos(Vec3d respawnPos, BlockPos currentPos) {
            return new RespawnPos(respawnPos, RespawnPos.getYaw(respawnPos, currentPos));
        }

        private static float getYaw(Vec3d respawnPos, BlockPos currentPos) {
            Vec3d vec3d = Vec3d.ofBottomCenter(currentPos).subtract(respawnPos).normalize();
            return (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d.z, vec3d.x) * 57.2957763671875 - 90.0);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{RespawnPos.class, "position;yaw", "pos", "yaw"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RespawnPos.class, "position;yaw", "pos", "yaw"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RespawnPos.class, "position;yaw", "pos", "yaw"}, this, object);
        }
    }
}

