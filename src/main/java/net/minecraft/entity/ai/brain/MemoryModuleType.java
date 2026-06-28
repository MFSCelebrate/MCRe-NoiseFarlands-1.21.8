/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.ai.brain;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class MemoryModuleType<U> {
    final static public MemoryModuleType<Void> DUMMY = MemoryModuleType.register("dummy");
    final static public MemoryModuleType<GlobalPos> HOME = MemoryModuleType.register("home", GlobalPos.CODEC);
    final static public MemoryModuleType<GlobalPos> JOB_SITE = MemoryModuleType.register("job_site", GlobalPos.CODEC);
    final static public MemoryModuleType<GlobalPos> POTENTIAL_JOB_SITE = MemoryModuleType.register("potential_job_site", GlobalPos.CODEC);
    final static public MemoryModuleType<GlobalPos> MEETING_POINT = MemoryModuleType.register("meeting_point", GlobalPos.CODEC);
    final static public MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = MemoryModuleType.register("secondary_job_site");
    final static public MemoryModuleType<List<LivingEntity>> MOBS = MemoryModuleType.register("mobs");
    final static public MemoryModuleType<LivingTargetCache> VISIBLE_MOBS = MemoryModuleType.register("visible_mobs");
    final static public MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = MemoryModuleType.register("visible_villager_babies");
    final static public MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = MemoryModuleType.register("nearest_players");
    final static public MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = MemoryModuleType.register("nearest_visible_player");
    final static public MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_TARGETABLE_PLAYER = MemoryModuleType.register("nearest_visible_targetable_player");
    final static public MemoryModuleType<List<PlayerEntity>> NEAREST_VISIBLE_TARGETABLE_PLAYERS = MemoryModuleType.register("nearest_visible_targetable_players");
    final static public MemoryModuleType<WalkTarget> WALK_TARGET = MemoryModuleType.register("walk_target");
    final static public MemoryModuleType<LookTarget> LOOK_TARGET = MemoryModuleType.register("look_target");
    final static public MemoryModuleType<LivingEntity> ATTACK_TARGET = MemoryModuleType.register("attack_target");
    final static public MemoryModuleType<Boolean> ATTACK_COOLING_DOWN = MemoryModuleType.register("attack_cooling_down");
    final static public MemoryModuleType<LivingEntity> INTERACTION_TARGET = MemoryModuleType.register("interaction_target");
    final static public MemoryModuleType<PassiveEntity> BREED_TARGET = MemoryModuleType.register("breed_target");
    final static public MemoryModuleType<Entity> RIDE_TARGET = MemoryModuleType.register("ride_target");
    final static public MemoryModuleType<Path> PATH = MemoryModuleType.register("path");
    final static public MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = MemoryModuleType.register("interactable_doors");
    final static public MemoryModuleType<Set<GlobalPos>> DOORS_TO_CLOSE = MemoryModuleType.register("doors_to_close");
    final static public MemoryModuleType<BlockPos> NEAREST_BED = MemoryModuleType.register("nearest_bed");
    final static public MemoryModuleType<DamageSource> HURT_BY = MemoryModuleType.register("hurt_by");
    final static public MemoryModuleType<LivingEntity> HURT_BY_ENTITY = MemoryModuleType.register("hurt_by_entity");
    final static public MemoryModuleType<LivingEntity> AVOID_TARGET = MemoryModuleType.register("avoid_target");
    final static public MemoryModuleType<LivingEntity> NEAREST_HOSTILE = MemoryModuleType.register("nearest_hostile");
    final static public MemoryModuleType<LivingEntity> NEAREST_ATTACKABLE = MemoryModuleType.register("nearest_attackable");
    final static public MemoryModuleType<GlobalPos> HIDING_PLACE = MemoryModuleType.register("hiding_place");
    final static public MemoryModuleType<Long> HEARD_BELL_TIME = MemoryModuleType.register("heard_bell_time");
    final static public MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = MemoryModuleType.register("cant_reach_walk_target_since");
    final static public MemoryModuleType<Boolean> GOLEM_DETECTED_RECENTLY = MemoryModuleType.register("golem_detected_recently", Codec.BOOL);
    final static public MemoryModuleType<Boolean> DANGER_DETECTED_RECENTLY = MemoryModuleType.register("danger_detected_recently", Codec.BOOL);
    final static public MemoryModuleType<Long> LAST_SLEPT = MemoryModuleType.register("last_slept", Codec.LONG);
    final static public MemoryModuleType<Long> LAST_WOKEN = MemoryModuleType.register("last_woken", Codec.LONG);
    final static public MemoryModuleType<Long> LAST_WORKED_AT_POI = MemoryModuleType.register("last_worked_at_poi", Codec.LONG);
    final static public MemoryModuleType<LivingEntity> NEAREST_VISIBLE_ADULT = MemoryModuleType.register("nearest_visible_adult");
    final static public MemoryModuleType<ItemEntity> NEAREST_VISIBLE_WANTED_ITEM = MemoryModuleType.register("nearest_visible_wanted_item");
    final static public MemoryModuleType<MobEntity> NEAREST_VISIBLE_NEMESIS = MemoryModuleType.register("nearest_visible_nemesis");
    final static public MemoryModuleType<Integer> PLAY_DEAD_TICKS = MemoryModuleType.register("play_dead_ticks", Codec.INT);
    final static public MemoryModuleType<PlayerEntity> TEMPTING_PLAYER = MemoryModuleType.register("tempting_player");
    final static public MemoryModuleType<Integer> TEMPTATION_COOLDOWN_TICKS = MemoryModuleType.register("temptation_cooldown_ticks", Codec.INT);
    final static public MemoryModuleType<Integer> GAZE_COOLDOWN_TICKS = MemoryModuleType.register("gaze_cooldown_ticks", Codec.INT);
    final static public MemoryModuleType<Boolean> IS_TEMPTED = MemoryModuleType.register("is_tempted", Codec.BOOL);
    final static public MemoryModuleType<Integer> LONG_JUMP_COOLING_DOWN = MemoryModuleType.register("long_jump_cooling_down", Codec.INT);
    final static public MemoryModuleType<Boolean> LONG_JUMP_MID_JUMP = MemoryModuleType.register("long_jump_mid_jump");
    final static public MemoryModuleType<Boolean> HAS_HUNTING_COOLDOWN = MemoryModuleType.register("has_hunting_cooldown", Codec.BOOL);
    final static public MemoryModuleType<Integer> RAM_COOLDOWN_TICKS = MemoryModuleType.register("ram_cooldown_ticks", Codec.INT);
    final static public MemoryModuleType<Vec3d> RAM_TARGET = MemoryModuleType.register("ram_target");
    final static public MemoryModuleType<Unit> IS_IN_WATER = MemoryModuleType.register("is_in_water", Unit.CODEC);
    final static public MemoryModuleType<Unit> IS_PREGNANT = MemoryModuleType.register("is_pregnant", Unit.CODEC);
    final static public MemoryModuleType<Boolean> IS_PANICKING = MemoryModuleType.register("is_panicking", Codec.BOOL);
    final static public MemoryModuleType<List<UUID>> UNREACHABLE_TONGUE_TARGETS = MemoryModuleType.register("unreachable_tongue_targets");
    final static public MemoryModuleType<UUID> ANGRY_AT = MemoryModuleType.register("angry_at", Uuids.INT_STREAM_CODEC);
    final static public MemoryModuleType<Boolean> UNIVERSAL_ANGER = MemoryModuleType.register("universal_anger", Codec.BOOL);
    final static public MemoryModuleType<Boolean> ADMIRING_ITEM = MemoryModuleType.register("admiring_item", Codec.BOOL);
    final static public MemoryModuleType<Integer> TIME_TRYING_TO_REACH_ADMIRE_ITEM = MemoryModuleType.register("time_trying_to_reach_admire_item");
    final static public MemoryModuleType<Boolean> DISABLE_WALK_TO_ADMIRE_ITEM = MemoryModuleType.register("disable_walk_to_admire_item");
    final static public MemoryModuleType<Boolean> ADMIRING_DISABLED = MemoryModuleType.register("admiring_disabled", Codec.BOOL);
    final static public MemoryModuleType<Boolean> HUNTED_RECENTLY = MemoryModuleType.register("hunted_recently", Codec.BOOL);
    final static public MemoryModuleType<BlockPos> CELEBRATE_LOCATION = MemoryModuleType.register("celebrate_location");
    final static public MemoryModuleType<Boolean> DANCING = MemoryModuleType.register("dancing");
    final static public MemoryModuleType<HoglinEntity> NEAREST_VISIBLE_HUNTABLE_HOGLIN = MemoryModuleType.register("nearest_visible_huntable_hoglin");
    final static public MemoryModuleType<HoglinEntity> NEAREST_VISIBLE_BABY_HOGLIN = MemoryModuleType.register("nearest_visible_baby_hoglin");
    final static public MemoryModuleType<PlayerEntity> NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD = MemoryModuleType.register("nearest_targetable_player_not_wearing_gold");
    final static public MemoryModuleType<List<AbstractPiglinEntity>> NEARBY_ADULT_PIGLINS = MemoryModuleType.register("nearby_adult_piglins");
    final static public MemoryModuleType<List<AbstractPiglinEntity>> NEAREST_VISIBLE_ADULT_PIGLINS = MemoryModuleType.register("nearest_visible_adult_piglins");
    final static public MemoryModuleType<List<HoglinEntity>> NEAREST_VISIBLE_ADULT_HOGLINS = MemoryModuleType.register("nearest_visible_adult_hoglins");
    final static public MemoryModuleType<AbstractPiglinEntity> NEAREST_VISIBLE_ADULT_PIGLIN = MemoryModuleType.register("nearest_visible_adult_piglin");
    final static public MemoryModuleType<LivingEntity> NEAREST_VISIBLE_ZOMBIFIED = MemoryModuleType.register("nearest_visible_zombified");
    final static public MemoryModuleType<Integer> VISIBLE_ADULT_PIGLIN_COUNT = MemoryModuleType.register("visible_adult_piglin_count");
    final static public MemoryModuleType<Integer> VISIBLE_ADULT_HOGLIN_COUNT = MemoryModuleType.register("visible_adult_hoglin_count");
    final static public MemoryModuleType<PlayerEntity> NEAREST_PLAYER_HOLDING_WANTED_ITEM = MemoryModuleType.register("nearest_player_holding_wanted_item");
    final static public MemoryModuleType<Boolean> ATE_RECENTLY = MemoryModuleType.register("ate_recently");
    final static public MemoryModuleType<BlockPos> NEAREST_REPELLENT = MemoryModuleType.register("nearest_repellent");
    final static public MemoryModuleType<Boolean> PACIFIED = MemoryModuleType.register("pacified");
    final static public MemoryModuleType<LivingEntity> ROAR_TARGET = MemoryModuleType.register("roar_target");
    final static public MemoryModuleType<BlockPos> DISTURBANCE_LOCATION = MemoryModuleType.register("disturbance_location");
    final static public MemoryModuleType<Unit> RECENT_PROJECTILE = MemoryModuleType.register("recent_projectile", Unit.CODEC);
    final static public MemoryModuleType<Unit> IS_SNIFFING = MemoryModuleType.register("is_sniffing", Unit.CODEC);
    final static public MemoryModuleType<Unit> IS_EMERGING = MemoryModuleType.register("is_emerging", Unit.CODEC);
    final static public MemoryModuleType<Unit> ROAR_SOUND_DELAY = MemoryModuleType.register("roar_sound_delay", Unit.CODEC);
    final static public MemoryModuleType<Unit> DIG_COOLDOWN = MemoryModuleType.register("dig_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> ROAR_SOUND_COOLDOWN = MemoryModuleType.register("roar_sound_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> SNIFF_COOLDOWN = MemoryModuleType.register("sniff_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> TOUCH_COOLDOWN = MemoryModuleType.register("touch_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> VIBRATION_COOLDOWN = MemoryModuleType.register("vibration_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> SONIC_BOOM_COOLDOWN = MemoryModuleType.register("sonic_boom_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> SONIC_BOOM_SOUND_COOLDOWN = MemoryModuleType.register("sonic_boom_sound_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> SONIC_BOOM_SOUND_DELAY = MemoryModuleType.register("sonic_boom_sound_delay", Unit.CODEC);
    final static public MemoryModuleType<UUID> LIKED_PLAYER = MemoryModuleType.register("liked_player", Uuids.INT_STREAM_CODEC);
    final static public MemoryModuleType<GlobalPos> LIKED_NOTEBLOCK = MemoryModuleType.register("liked_noteblock", GlobalPos.CODEC);
    final static public MemoryModuleType<Integer> LIKED_NOTEBLOCK_COOLDOWN_TICKS = MemoryModuleType.register("liked_noteblock_cooldown_ticks", Codec.INT);
    final static public MemoryModuleType<Integer> ITEM_PICKUP_COOLDOWN_TICKS = MemoryModuleType.register("item_pickup_cooldown_ticks", Codec.INT);
    final static public MemoryModuleType<List<GlobalPos>> SNIFFER_EXPLORED_POSITIONS = MemoryModuleType.register("sniffer_explored_positions", Codec.list(GlobalPos.CODEC));
    final static public MemoryModuleType<BlockPos> SNIFFER_SNIFFING_TARGET = MemoryModuleType.register("sniffer_sniffing_target");
    final static public MemoryModuleType<Boolean> SNIFFER_DIGGING = MemoryModuleType.register("sniffer_digging");
    final static public MemoryModuleType<Boolean> SNIFFER_HAPPY = MemoryModuleType.register("sniffer_happy");
    final static public MemoryModuleType<Unit> BREEZE_JUMP_COOLDOWN = MemoryModuleType.register("breeze_jump_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> BREEZE_SHOOT = MemoryModuleType.register("breeze_shoot", Unit.CODEC);
    final static public MemoryModuleType<Unit> BREEZE_SHOOT_CHARGING = MemoryModuleType.register("breeze_shoot_charging", Unit.CODEC);
    final static public MemoryModuleType<Unit> BREEZE_SHOOT_RECOVER = MemoryModuleType.register("breeze_shoot_recover", Unit.CODEC);
    final static public MemoryModuleType<Unit> BREEZE_SHOOT_COOLDOWN = MemoryModuleType.register("breeze_shoot_cooldown", Unit.CODEC);
    final static public MemoryModuleType<Unit> BREEZE_JUMP_INHALING = MemoryModuleType.register("breeze_jump_inhaling", Unit.CODEC);
    final static public MemoryModuleType<BlockPos> BREEZE_JUMP_TARGET = MemoryModuleType.register("breeze_jump_target", BlockPos.CODEC);
    final static public MemoryModuleType<Unit> BREEZE_LEAVING_WATER = MemoryModuleType.register("breeze_leaving_water", Unit.CODEC);
    final private Optional<Codec<Memory<U>>> codec;

    @VisibleForTesting
    public MemoryModuleType(Optional<Codec<U>> codec) {
        this.codec = codec.map(Memory::createCodec);
    }

    public String toString() {
        return Registries.MEMORY_MODULE_TYPE.getId(this).toString();
    }

    public Optional<Codec<Memory<U>>> getCodec() {
        return this.codec;
    }

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, Identifier.ofVanilla(id), new MemoryModuleType<U>(Optional.of(codec)));
    }

    private static <U> MemoryModuleType<U> register(String id) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, Identifier.ofVanilla(id), new MemoryModuleType<U>(Optional.empty()));
    }
}

