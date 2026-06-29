/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicLike
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerWaypointHandler;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class GameRules {
    final static public int DEFAULT_RANDOM_TICK_SPEED = 3;
    final static Logger LOGGER = LogUtils.getLogger();
    final static private Map<Key<?>, Type<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> key.name));
    final static public Key<BooleanRule> DO_FIRE_TICK = GameRules.register("doFireTick", Category.UPDATES, BooleanRule.create(true));
    final static public Key<BooleanRule> ALLOW_FIRE_TICKS_AWAY_FROM_PLAYER = GameRules.register("allowFireTicksAwayFromPlayer", Category.UPDATES, BooleanRule.create(false));
    final static public Key<BooleanRule> DO_MOB_GRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanRule.create(true));
    final static public Key<BooleanRule> KEEP_INVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanRule.create(false));
    final static public Key<BooleanRule> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_MOB_LOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanRule.create(true));
    final static public Key<BooleanRule> PROJECTILES_CAN_BREAK_BLOCKS = GameRules.register("projectilesCanBreakBlocks", Category.DROPS, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_TILE_DROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanRule.create(true));
    final static public Key<BooleanRule> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanRule.create(true));
    final static public Key<BooleanRule> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanRule.create(true));
    final static public Key<BooleanRule> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanRule.create(true));
    final static public Key<BooleanRule> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanRule.create(true));
    final static public Key<IntRule> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", Category.UPDATES, IntRule.create(3));
    final static public Key<BooleanRule> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanRule.create(true));
    final static public Key<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanRule.create(false, (server, rule) -> {
        byte b = rule.get() ? EntityStatuses.USE_REDUCED_DEBUG_INFO : EntityStatuses.USE_FULL_DEBUG_INFO;
        for (ServerPlayerEntity serverPlayerEntity : server.net_minecraft_server_PlayerManager_getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
        }
    }));
    final static public Key<BooleanRule> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanRule.create(true));
    final static public Key<IntRule> SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntRule.create(10));
    final static public Key<BooleanRule> DISABLE_PLAYER_MOVEMENT_CHECK = GameRules.register("disablePlayerMovementCheck", Category.PLAYER, BooleanRule.create(false));
    final static public Key<BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanRule.create(false));
    final static public Key<IntRule> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntRule.create(24));
    final static public Key<BooleanRule> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanRule.create(false, (server, rule) -> {
        for (ServerPlayerEntity serverPlayerEntity : server.net_minecraft_server_PlayerManager_getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED, rule.get() ? 1.0f : (float)GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
        }
    }));
    final static public Key<IntRule> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntRule.create(65536));
    final static public Key<IntRule> MAX_COMMAND_FORK_COUNT = GameRules.register("maxCommandForkCount", Category.MISC, IntRule.create(65536));
    final static public Key<IntRule> COMMAND_MODIFICATION_BLOCK_LIMIT = GameRules.register("commandModificationBlockLimit", Category.MISC, IntRule.create(32768));
    final static public Key<BooleanRule> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanRule.create(true));
    final static public Key<BooleanRule> DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanRule.create(false));
    final static public Key<BooleanRule> DO_INSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanRule.create(false, (server, rule) -> {
        for (ServerPlayerEntity serverPlayerEntity : server.net_minecraft_server_PlayerManager_getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.IMMEDIATE_RESPAWN, rule.get() ? 1.0f : (float)GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
        }
    }));
    final static public Key<IntRule> PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = GameRules.register("playersNetherPortalDefaultDelay", Category.PLAYER, IntRule.create(80));
    final static public Key<IntRule> PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = GameRules.register("playersNetherPortalCreativeDelay", Category.PLAYER, IntRule.create(0));
    final static public Key<BooleanRule> DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanRule.create(true));
    final static public Key<BooleanRule> FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanRule.create(true));
    final static public Key<BooleanRule> FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanRule.create(true));
    final static public Key<BooleanRule> FREEZE_DAMAGE = GameRules.register("freezeDamage", Category.PLAYER, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_WARDEN_SPAWNING = GameRules.register("doWardenSpawning", Category.SPAWNING, BooleanRule.create(true));
    final static public Key<BooleanRule> FORGIVE_DEAD_PLAYERS = GameRules.register("forgiveDeadPlayers", Category.MOBS, BooleanRule.create(true));
    final static public Key<BooleanRule> UNIVERSAL_ANGER = GameRules.register("universalAnger", Category.MOBS, BooleanRule.create(false));
    final static public Key<IntRule> PLAYERS_SLEEPING_PERCENTAGE = GameRules.register("playersSleepingPercentage", Category.PLAYER, IntRule.create(100));
    final static public Key<BooleanRule> BLOCK_EXPLOSION_DROP_DECAY = GameRules.register("blockExplosionDropDecay", Category.DROPS, BooleanRule.create(true));
    final static public Key<BooleanRule> MOB_EXPLOSION_DROP_DECAY = GameRules.register("mobExplosionDropDecay", Category.DROPS, BooleanRule.create(true));
    final static public Key<BooleanRule> TNT_EXPLOSION_DROP_DECAY = GameRules.register("tntExplosionDropDecay", Category.DROPS, BooleanRule.create(false));
    final static public Key<IntRule> SNOW_ACCUMULATION_HEIGHT = GameRules.register("snowAccumulationHeight", Category.UPDATES, IntRule.create(1));
    final static public Key<BooleanRule> WATER_SOURCE_CONVERSION = GameRules.register("waterSourceConversion", Category.UPDATES, BooleanRule.create(true));
    final static public Key<BooleanRule> LAVA_SOURCE_CONVERSION = GameRules.register("lavaSourceConversion", Category.UPDATES, BooleanRule.create(false));
    final static public Key<BooleanRule> GLOBAL_SOUND_EVENTS = GameRules.register("globalSoundEvents", Category.MISC, BooleanRule.create(true));
    final static public Key<BooleanRule> DO_VINES_SPREAD = GameRules.register("doVinesSpread", Category.UPDATES, BooleanRule.create(true));
    final static public Key<BooleanRule> ENDER_PEARLS_VANISH_ON_DEATH = GameRules.register("enderPearlsVanishOnDeath", Category.PLAYER, BooleanRule.create(true));
    final static public Key<IntRule> MINECART_MAX_SPEED = GameRules.register("minecartMaxSpeed", Category.MISC, IntRule.create(8, 1, 1000, FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS), (server, value) -> {}));
    final static public Key<IntRule> SPAWN_CHUNK_RADIUS = GameRules.register("spawnChunkRadius", Category.MISC, IntRule.create(2, 0, 32, FeatureSet.empty(), (server, value) -> {
        ServerWorld serverWorld = server.getOverworld();
        serverWorld.setSpawnPos(serverWorld.getSpawnPos(), serverWorld.getSpawnAngle());
    }));
    final static public Key<BooleanRule> TNT_EXPLODES = GameRules.register("tntExplodes", Category.MISC, BooleanRule.create(true));
    final static public Key<BooleanRule> LOCATOR_BAR = GameRules.register("locatorBar", Category.PLAYER, BooleanRule.create(true, (server, rule) -> server.getWorlds().forEach(world -> {
        ServerWaypointHandler serverWaypointHandler = world.getWaypointHandler();
        if (rule.get()) {
            world.getPlayers().forEach(serverWaypointHandler::updatePlayerPos);
        } else {
            serverWaypointHandler.clear();
        }
    })));
    final private Map<Key<?>, Rule<?>> rules;
    final private FeatureSet enabledFeatures;

    public static <T extends Rule<T>> Type<T> getRuleType(Key<T> key) {
        return RULE_TYPES.get(key);
    }

    public static <T extends Rule<T>> Codec<Key<T>> createKeyCodec(Class<T> ruleClass) {
        return Codec.STRING.comapFlatMap(string -> RULE_TYPES.entrySet().stream().filter(ruleType -> ((Type)ruleType.getValue()).ruleClass == ruleClass).map(Map.Entry::getKey).filter(key -> key.getName().equals(string)).map(key -> key).findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Invalid game rule ID for type: " + string)), Key::getName);
    }

    private static <T extends Rule<T>> Key<T> register(String name, Category category, Type<T> type) {
        Key key = new Key(name, category);
        Type<T> type2 = RULE_TYPES.put(key, type);
        if (type2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        return key;
    }

    public GameRules(FeatureSet enabledFeatures, DynamicLike<?> values) {
        this(enabledFeatures);
        this.load(values);
    }

    public GameRules(FeatureSet enabledFeatures) {
        this((Map)GameRules.streamAllRules(enabledFeatures).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Type)entry.getValue()).createRule())), enabledFeatures);
    }

    private static Stream<Map.Entry<Key<?>, Type<?>>> streamAllRules(FeatureSet enabledFeatures) {
        return RULE_TYPES.entrySet().stream().filter(entry -> ((Type)entry.getValue()).requiredFeatures.isSubsetOf(enabledFeatures));
    }

    private GameRules(Map<Key<?>, Rule<?>> rules, FeatureSet enabledFeatures) {
        this.rules = rules;
        this.enabledFeatures = enabledFeatures;
    }

    public <T extends Rule<T>> T get(Key<T> key) {
        Rule<?> rule = this.rules.get(key);
        if (rule == null) {
            throw new IllegalArgumentException("Tried to access invalid game rule");
        }
        return (T)rule;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.rules.forEach((key, rule) -> nbtCompound.putString(key.name, rule.serialize()));
        return nbtCompound;
    }

    private void load(DynamicLike<?> values) {
        this.rules.forEach((key, rule) -> values.get(key.name).asString().ifSuccess(rule::deserialize));
    }

    public GameRules copy(FeatureSet enabledFeatures) {
        return new GameRules((Map)GameRules.streamAllRules(enabledFeatures).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> this.rules.containsKey(entry.getKey()) ? this.rules.get(entry.getKey()).copy() : ((Type)entry.getValue()).createRule())), enabledFeatures);
    }

    public void accept(Visitor visitor) {
        RULE_TYPES.forEach((key, type) -> this.accept(visitor, (Key<?>)key, (Type<?>)type));
    }

    private <T extends Rule<T>> void accept(Visitor visitor, Key<?> key, Type<?> type) {
        Key<?> key2 = key;
        Type<?> type2 = type;
        if (type2.requiredFeatures.isSubsetOf(this.enabledFeatures)) {
            visitor.visit(key2, type2);
            type2.accept(visitor, key2);
        }
    }

    public void setAllValues(GameRules rules, @Nullable MinecraftServer server) {
        rules.rules.keySet().forEach(key -> this.setValue((Key)key, rules, server));
    }

    private <T extends Rule<T>> void setValue(Key<T> key, GameRules rules, @Nullable MinecraftServer server) {
        T rule = rules.get(key);
        ((Rule)this.get(key)).setValue(rule, server);
    }

    public boolean getBoolean(Key<BooleanRule> rule) {
        return this.get(rule).get();
    }

    public int getInt(Key<IntRule> rule) {
        return this.get(rule).get();
    }

    public static class Type<T extends Rule<T>> {
        final Supplier<ArgumentType<?>> argumentType;
        final private Function<Type<T>, T> ruleFactory;
        final BiConsumer<MinecraftServer, T> changeCallback;
        final private Acceptor<T> ruleAcceptor;
        final Class<T> ruleClass;
        final FeatureSet requiredFeatures;

        Type(Supplier<ArgumentType<?>> argumentType, Function<Type<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback, Acceptor<T> ruleAcceptor, Class<T> ruleClass, FeatureSet requiredFeatures) {
            this.argumentType = argumentType;
            this.ruleFactory = ruleFactory;
            this.changeCallback = changeCallback;
            this.ruleAcceptor = ruleAcceptor;
            this.ruleClass = ruleClass;
            this.requiredFeatures = requiredFeatures;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
            return CommandManager.argument(name, this.argumentType.get());
        }

        public T createRule() {
            return (T)((Rule)this.ruleFactory.apply(this));
        }

        public void accept(Visitor consumer, Key<T> key) {
            this.ruleAcceptor.call(consumer, key, this);
        }

        public FeatureSet getRequiredFeatures() {
            return this.requiredFeatures;
        }
    }

    public static final class Key<T extends Rule<T>> {
        final String name;
        final private Category category;

        public Key(String name, Category category) {
            this.name = name;
            this.category = category;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            return o instanceof Key && ((Key)o).name.equals(this.name);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String getName() {
            return this.name;
        }

        public String getTranslationKey() {
            return "gamerule." + this.name;
        }

        public Category getCategory() {
            return this.category;
        }
    }

    public static final class Category
    extends Enum<Category> {
        final static public Category PLAYER = new Category("gamerule.category.player");
        final static public Category MOBS = new Category("gamerule.category.mobs");
        final static public Category SPAWNING = new Category("gamerule.category.spawning");
        final static public Category DROPS = new Category("gamerule.category.drops");
        final static public Category UPDATES = new Category("gamerule.category.updates");
        final static public Category CHAT = new Category("gamerule.category.chat");
        final static public Category MISC = new Category("gamerule.category.misc");
        final private String category;
        final static private Category[] field_24102;

        public static Category[] values() {
            return (Category[])field_24102.clone();
        }

        public static Category valueOf(String string) {
            return Enum.valueOf(Category.class, string);
        }

        private Category(String category) {
            this.category = category;
        }

        public String getCategory() {
            return this.category;
        }

        private static Category[] method_36694() {
            return new Category[]{PLAYER, MOBS, SPAWNING, DROPS, UPDATES, CHAT, MISC};
        }

        static {
            field_24102 = Category.method_36694();
        }
    }

    public static abstract class Rule<T extends Rule<T>> {
        final protected Type<T> type;

        public Rule(Type<T> type) {
            this.type = type;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> context, String name) {
            this.setFromArgument(context, name);
            this.changed(((ServerCommandSource)context.getSource()).getServer());
        }

        protected void changed(@Nullable MinecraftServer server) {
            if (server != null) {
                this.type.changeCallback.accept(server, (MinecraftServer)this.getThis());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getThis();

        protected abstract T copy();

        public abstract void setValue(T var1, @Nullable MinecraftServer var2);
    }

    public static interface Visitor {
        default public <T extends Rule<T>> void visit(Key<T> key, Type<T> type) {
        }

        default public void visitBoolean(Key<BooleanRule> key, Type<BooleanRule> type) {
        }

        default public void visitInt(Key<IntRule> key, Type<IntRule> type) {
        }
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        private static Type<BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, BooleanRule> changeCallback, FeatureSet requiredFeatures) {
            return new Type<BooleanRule>(BoolArgumentType::bool, type -> new BooleanRule((Type<BooleanRule>)type, initialValue), changeCallback, Visitor::visitBoolean, BooleanRule.class, requiredFeatures);
        }

        static Type<BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, BooleanRule> changeCallback) {
            return new Type<BooleanRule>(BoolArgumentType::bool, type -> new BooleanRule((Type<BooleanRule>)type, initialValue), changeCallback, Visitor::visitBoolean, BooleanRule.class, FeatureSet.empty());
        }

        static Type<BooleanRule> create(boolean initialValue) {
            return BooleanRule.create(initialValue, (server, rule) -> {});
        }

        public BooleanRule(Type<BooleanRule> type, boolean initialValue) {
            super(type);
            this.value = initialValue;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
            this.value = BoolArgumentType.getBool(context, (String)name);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean value, @Nullable MinecraftServer server) {
            this.value = value;
            this.changed(server);
        }

        @Override
        public String serialize() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void deserialize(String value) {
            this.value = Boolean.parseBoolean(value);
        }

        @Override
        public int getCommandResult() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanRule net_minecraft_world_GameRules$BooleanRule_getThis() {
            return this;
        }

        @Override
        protected BooleanRule net_minecraft_world_GameRules$BooleanRule_copy() {
            return new BooleanRule(this.type, this.value);
        }

        @Override
        public void setValue(BooleanRule booleanRule, @Nullable MinecraftServer minecraftServer) {
            this.value = booleanRule.value;
            this.changed(minecraftServer);
        }

        @Override
        protected Rule net_minecraft_world_GameRules$Rule_copy() {
            return this.net_minecraft_world_GameRules$BooleanRule_copy();
        }

        @Override
        protected Rule net_minecraft_world_GameRules$Rule_getThis() {
            return this.net_minecraft_world_GameRules$BooleanRule_getThis();
        }
    }

    public static class IntRule
    extends Rule<IntRule> {
        private int value;

        private static Type<IntRule> create(int initialValue, BiConsumer<MinecraftServer, IntRule> changeCallback) {
            return new Type<IntRule>(IntegerArgumentType::integer, type -> new IntRule((Type<IntRule>)type, initialValue), changeCallback, Visitor::visitInt, IntRule.class, FeatureSet.empty());
        }

        static Type<IntRule> create(int initialValue, int min, int max, FeatureSet requiredFeatures, BiConsumer<MinecraftServer, IntRule> changeCallback) {
            return new Type<IntRule>(() -> IntegerArgumentType.integer((int)min, (int)max), type -> new IntRule((Type<IntRule>)type, initialValue), changeCallback, Visitor::visitInt, IntRule.class, requiredFeatures);
        }

        static Type<IntRule> create(int initialValue) {
            return IntRule.create(initialValue, (server, rule) -> {});
        }

        public IntRule(Type<IntRule> rule, int initialValue) {
            super(rule);
            this.value = initialValue;
        }

        @Override
        protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
            this.value = IntegerArgumentType.getInteger(context, (String)name);
        }

        public int get() {
            return this.value;
        }

        public void set(int value, @Nullable MinecraftServer server) {
            this.value = value;
            this.changed(server);
        }

        @Override
        public String serialize() {
            return Integer.toString(this.value);
        }

        @Override
        protected void deserialize(String value) {
            this.value = IntRule.parseInt(value);
        }

        public boolean validateAndSet(String input) {
            try {
                StringReader stringReader = new StringReader(input);
                this.value = (Integer)this.type.argumentType.get().parse(stringReader);
                return !stringReader.canRead();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return false;
            }
        }

        private static int parseInt(String input) {
            if (!input.isEmpty()) {
                try {
                    return Integer.parseInt(input);
                }
                catch (NumberFormatException numberFormatException) {
                    LOGGER.warn("Failed to parse integer {}", (Object)input);
                }
            }
            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected IntRule net_minecraft_world_GameRules$IntRule_getThis() {
            return this;
        }

        @Override
        protected IntRule net_minecraft_world_GameRules$IntRule_copy() {
            return new IntRule(this.type, this.value);
        }

        @Override
        public void setValue(IntRule intRule, @Nullable MinecraftServer minecraftServer) {
            this.value = intRule.value;
            this.changed(minecraftServer);
        }

        @Override
        protected Rule net_minecraft_world_GameRules$Rule_copy() {
            return this.net_minecraft_world_GameRules$IntRule_copy();
        }

        @Override
        protected Rule net_minecraft_world_GameRules$Rule_getThis() {
            return this.net_minecraft_world_GameRules$IntRule_getThis();
        }
    }

    static interface Acceptor<T extends Rule<T>> {
        public void call(Visitor var1, Key<T> var2, Type<T> var3);
    }
}

