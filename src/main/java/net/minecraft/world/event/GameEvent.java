/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public record GameEvent(int notificationRadius) {
    final static public RegistryEntry.Reference<GameEvent> BLOCK_ACTIVATE = GameEvent.register("block_activate");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_ATTACH = GameEvent.register("block_attach");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_CHANGE = GameEvent.register("block_change");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_CLOSE = GameEvent.register("block_close");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_DEACTIVATE = GameEvent.register("block_deactivate");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_DESTROY = GameEvent.register("block_destroy");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_DETACH = GameEvent.register("block_detach");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_OPEN = GameEvent.register("block_open");
    final static public RegistryEntry.Reference<GameEvent> BLOCK_PLACE = GameEvent.register("block_place");
    final static public RegistryEntry.Reference<GameEvent> CONTAINER_CLOSE = GameEvent.register("container_close");
    final static public RegistryEntry.Reference<GameEvent> CONTAINER_OPEN = GameEvent.register("container_open");
    final static public RegistryEntry.Reference<GameEvent> DRINK = GameEvent.register("drink");
    final static public RegistryEntry.Reference<GameEvent> EAT = GameEvent.register("eat");
    final static public RegistryEntry.Reference<GameEvent> ELYTRA_GLIDE = GameEvent.register("elytra_glide");
    final static public RegistryEntry.Reference<GameEvent> ENTITY_DAMAGE = GameEvent.register("entity_damage");
    final static public RegistryEntry.Reference<GameEvent> ENTITY_DIE = GameEvent.register("entity_die");
    final static public RegistryEntry.Reference<GameEvent> ENTITY_DISMOUNT = GameEvent.register("entity_dismount");
    final static public RegistryEntry.Reference<GameEvent> ENTITY_INTERACT = GameEvent.register("entity_interact");
    final static public RegistryEntry.Reference<GameEvent> ENTITY_MOUNT = GameEvent.register("entity_mount");
    final static public RegistryEntry.Reference<GameEvent> ENTITY_PLACE = GameEvent.register("entity_place");
    final static public RegistryEntry.Reference<GameEvent> ENTITY_ACTION = GameEvent.register("entity_action");
    final static public RegistryEntry.Reference<GameEvent> EQUIP = GameEvent.register("equip");
    final static public RegistryEntry.Reference<GameEvent> EXPLODE = GameEvent.register("explode");
    final static public RegistryEntry.Reference<GameEvent> FLAP = GameEvent.register("flap");
    final static public RegistryEntry.Reference<GameEvent> FLUID_PICKUP = GameEvent.register("fluid_pickup");
    final static public RegistryEntry.Reference<GameEvent> FLUID_PLACE = GameEvent.register("fluid_place");
    final static public RegistryEntry.Reference<GameEvent> HIT_GROUND = GameEvent.register("hit_ground");
    final static public RegistryEntry.Reference<GameEvent> INSTRUMENT_PLAY = GameEvent.register("instrument_play");
    final static public RegistryEntry.Reference<GameEvent> ITEM_INTERACT_FINISH = GameEvent.register("item_interact_finish");
    final static public RegistryEntry.Reference<GameEvent> ITEM_INTERACT_START = GameEvent.register("item_interact_start");
    final static public RegistryEntry.Reference<GameEvent> JUKEBOX_PLAY = GameEvent.register("jukebox_play", 10);
    final static public RegistryEntry.Reference<GameEvent> JUKEBOX_STOP_PLAY = GameEvent.register("jukebox_stop_play", 10);
    final static public RegistryEntry.Reference<GameEvent> LIGHTNING_STRIKE = GameEvent.register("lightning_strike");
    final static public RegistryEntry.Reference<GameEvent> NOTE_BLOCK_PLAY = GameEvent.register("note_block_play");
    final static public RegistryEntry.Reference<GameEvent> PRIME_FUSE = GameEvent.register("prime_fuse");
    final static public RegistryEntry.Reference<GameEvent> PROJECTILE_LAND = GameEvent.register("projectile_land");
    final static public RegistryEntry.Reference<GameEvent> PROJECTILE_SHOOT = GameEvent.register("projectile_shoot");
    final static public RegistryEntry.Reference<GameEvent> SCULK_SENSOR_TENDRILS_CLICKING = GameEvent.register("sculk_sensor_tendrils_clicking");
    final static public RegistryEntry.Reference<GameEvent> SHEAR = GameEvent.register("shear");
    final static public RegistryEntry.Reference<GameEvent> SHRIEK = GameEvent.register("shriek", 32);
    final static public RegistryEntry.Reference<GameEvent> SPLASH = GameEvent.register("splash");
    final static public RegistryEntry.Reference<GameEvent> STEP = GameEvent.register("step");
    final static public RegistryEntry.Reference<GameEvent> SWIM = GameEvent.register("swim");
    final static public RegistryEntry.Reference<GameEvent> TELEPORT = GameEvent.register("teleport");
    final static public RegistryEntry.Reference<GameEvent> UNEQUIP = GameEvent.register("unequip");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_1 = GameEvent.register("resonate_1");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_2 = GameEvent.register("resonate_2");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_3 = GameEvent.register("resonate_3");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_4 = GameEvent.register("resonate_4");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_5 = GameEvent.register("resonate_5");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_6 = GameEvent.register("resonate_6");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_7 = GameEvent.register("resonate_7");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_8 = GameEvent.register("resonate_8");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_9 = GameEvent.register("resonate_9");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_10 = GameEvent.register("resonate_10");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_11 = GameEvent.register("resonate_11");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_12 = GameEvent.register("resonate_12");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_13 = GameEvent.register("resonate_13");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_14 = GameEvent.register("resonate_14");
    final static public RegistryEntry.Reference<GameEvent> RESONATE_15 = GameEvent.register("resonate_15");
    final static public int DEFAULT_RANGE = 16;
    final static public Codec<RegistryEntry<GameEvent>> CODEC = RegistryFixedCodec.of(RegistryKeys.GAME_EVENT);

    public static RegistryEntry<GameEvent> registerAndGetDefault(Registry<GameEvent> registry) {
        return BLOCK_ACTIVATE;
    }

    private static RegistryEntry.Reference<GameEvent> register(String id) {
        return GameEvent.register(id, 16);
    }

    private static RegistryEntry.Reference<GameEvent> register(String id, int range) {
        return Registry.registerReference(Registries.GAME_EVENT, Identifier.ofVanilla(id), new GameEvent(range));
    }

    public static final class Message
    implements Comparable<Message> {
        final private RegistryEntry<GameEvent> event;
        final private Vec3d emitterPos;
        final private Emitter emitter;
        final private GameEventListener listener;
        final private double distanceTraveled;

        public Message(RegistryEntry<GameEvent> event, Vec3d emitterPos, Emitter emitter, GameEventListener listener, Vec3d listenerPos) {
            this.event = event;
            this.emitterPos = emitterPos;
            this.emitter = emitter;
            this.listener = listener;
            this.distanceTraveled = emitterPos.squaredDistanceTo(listenerPos);
        }

        @Override
        public int compareTo(Message message) {
            return Double.compare(this.distanceTraveled, message.distanceTraveled);
        }

        public RegistryEntry<GameEvent> getEvent() {
            return this.event;
        }

        public Vec3d getEmitterPos() {
            return this.emitterPos;
        }

        public Emitter getEmitter() {
            return this.emitter;
        }

        public GameEventListener getListener() {
            return this.listener;
        }

        @Override
        public int compareTo(Object other) {
            return this.compareTo((Message)other);
        }
    }

    public record Emitter(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
        public static Emitter of(@Nullable Entity sourceEntity) {
            return new Emitter(sourceEntity, null);
        }

        public static Emitter of(@Nullable BlockState affectedState) {
            return new Emitter(null, affectedState);
        }

        public static Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
            return new Emitter(sourceEntity, affectedState);
        }
    }
}

