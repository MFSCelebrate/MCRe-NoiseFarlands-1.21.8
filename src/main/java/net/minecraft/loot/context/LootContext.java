/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.loot.context;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class LootContext {
    final private LootWorldContext worldContext;
    final private Random random;
    final private RegistryEntryLookup.RegistryLookup lookup;
    final private Set<Entry<?>> activeEntries = Sets.newLinkedHashSet();

    LootContext(LootWorldContext worldContext, Random random, RegistryEntryLookup.RegistryLookup lookup) {
        this.worldContext = worldContext;
        this.random = random;
        this.lookup = lookup;
    }

    public boolean hasParameter(ContextParameter<?> parameter) {
        return this.worldContext.getParameters().contains(parameter);
    }

    public <T> T getOrThrow(ContextParameter<T> parameter) {
        return this.worldContext.getParameters().getOrThrow(parameter);
    }

    @Nullable
    public <T> T get(ContextParameter<T> parameter) {
        return this.worldContext.getParameters().getNullable(parameter);
    }

    public void drop(Identifier id, Consumer<ItemStack> lootConsumer) {
        this.worldContext.addDynamicDrops(id, lootConsumer);
    }

    public boolean isActive(Entry<?> entry) {
        return this.activeEntries.contains(entry);
    }

    public boolean markActive(Entry<?> entry) {
        return this.activeEntries.add(entry);
    }

    public void markInactive(Entry<?> entry) {
        this.activeEntries.remove(entry);
    }

    public RegistryEntryLookup.RegistryLookup getLookup() {
        return this.lookup;
    }

    public Random getRandom() {
        return this.random;
    }

    public float getLuck() {
        return this.worldContext.getLuck();
    }

    public ServerWorld getWorld() {
        return this.worldContext.getWorld();
    }

    public static Entry<LootTable> table(LootTable table) {
        return new Entry<LootTable>(LootDataType.LOOT_TABLES, table);
    }

    public static Entry<LootCondition> predicate(LootCondition predicate) {
        return new Entry<LootCondition>(LootDataType.PREDICATES, predicate);
    }

    public static Entry<LootFunction> itemModifier(LootFunction itemModifier) {
        return new Entry<LootFunction>(LootDataType.ITEM_MODIFIERS, itemModifier);
    }

    public record Entry<T>(LootDataType<T> type, T value) {
    }

    public static final class EntityTarget
    extends Enum<EntityTarget>
    implements StringIdentifiable {
        final static public EntityTarget THIS = new EntityTarget("this", LootContextParameters.THIS_ENTITY);
        final static public EntityTarget ATTACKER = new EntityTarget("attacker", LootContextParameters.ATTACKING_ENTITY);
        final static public EntityTarget DIRECT_ATTACKER = new EntityTarget("direct_attacker", LootContextParameters.DIRECT_ATTACKING_ENTITY);
        final static public EntityTarget ATTACKING_PLAYER = new EntityTarget("attacking_player", LootContextParameters.LAST_DAMAGE_PLAYER);
        final static public StringIdentifiable.EnumCodec<EntityTarget> CODEC;
        final private String type;
        final private ContextParameter<? extends Entity> parameter;
        final static private EntityTarget[] field_940;

        public static EntityTarget[] values() {
            return (EntityTarget[])field_940.clone();
        }

        public static EntityTarget valueOf(String string) {
            return Enum.valueOf(EntityTarget.class, string);
        }

        private EntityTarget(String type, ContextParameter<? extends Entity> parameter) {
            this.type = type;
            this.parameter = parameter;
        }

        public ContextParameter<? extends Entity> getParameter() {
            return this.parameter;
        }

        public static EntityTarget fromString(String type) {
            EntityTarget entityTarget = CODEC.byId(type);
            if (entityTarget != null) {
                return entityTarget;
            }
            throw new IllegalArgumentException("Invalid entity target " + type);
        }

        @Override
        public String asString() {
            return this.type;
        }

        private static EntityTarget[] method_36793() {
            return new EntityTarget[]{THIS, ATTACKER, DIRECT_ATTACKER, ATTACKING_PLAYER};
        }

        static {
            field_940 = EntityTarget.method_36793();
            CODEC = StringIdentifiable.createCodec(EntityTarget::values);
        }
    }

    public static class Builder {
        final private LootWorldContext worldContext;
        @Nullable
        private Random random;

        public Builder(LootWorldContext worldContext) {
            this.worldContext = worldContext;
        }

        public Builder random(long seed) {
            if (seed != 0L) {
                this.random = Random.create(seed);
            }
            return this;
        }

        public Builder random(Random random) {
            this.random = random;
            return this;
        }

        public ServerWorld getWorld() {
            return this.worldContext.getWorld();
        }

        public LootContext build(Optional<Identifier> randomId) {
            ServerWorld serverWorld = this.getWorld();
            MinecraftServer minecraftServer = serverWorld.getServer();
            Random random = Optional.ofNullable(this.random).or(() -> randomId.map(serverWorld::getOrCreateRandom)).orElseGet(serverWorld::getRandom);
            return new LootContext(this.worldContext, random, minecraftServer.getReloadableRegistries().createRegistryLookup());
        }
    }
}

