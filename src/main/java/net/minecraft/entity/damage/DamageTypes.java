/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface DamageTypes {
    final static public RegistryKey<DamageType> IN_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("in_fire"));
    final static public RegistryKey<DamageType> CAMPFIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("campfire"));
    final static public RegistryKey<DamageType> LIGHTNING_BOLT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("lightning_bolt"));
    final static public RegistryKey<DamageType> ON_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("on_fire"));
    final static public RegistryKey<DamageType> LAVA = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("lava"));
    final static public RegistryKey<DamageType> HOT_FLOOR = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("hot_floor"));
    final static public RegistryKey<DamageType> IN_WALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("in_wall"));
    final static public RegistryKey<DamageType> CRAMMING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("cramming"));
    final static public RegistryKey<DamageType> DROWN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("drown"));
    final static public RegistryKey<DamageType> STARVE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("starve"));
    final static public RegistryKey<DamageType> CACTUS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("cactus"));
    final static public RegistryKey<DamageType> FALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("fall"));
    final static public RegistryKey<DamageType> ENDER_PEARL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("ender_pearl"));
    final static public RegistryKey<DamageType> FLY_INTO_WALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("fly_into_wall"));
    final static public RegistryKey<DamageType> OUT_OF_WORLD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("out_of_world"));
    final static public RegistryKey<DamageType> GENERIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("generic"));
    final static public RegistryKey<DamageType> MAGIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("magic"));
    final static public RegistryKey<DamageType> WITHER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("wither"));
    final static public RegistryKey<DamageType> DRAGON_BREATH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("dragon_breath"));
    final static public RegistryKey<DamageType> DRY_OUT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("dry_out"));
    final static public RegistryKey<DamageType> SWEET_BERRY_BUSH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("sweet_berry_bush"));
    final static public RegistryKey<DamageType> FREEZE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("freeze"));
    final static public RegistryKey<DamageType> STALAGMITE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("stalagmite"));
    final static public RegistryKey<DamageType> FALLING_BLOCK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("falling_block"));
    final static public RegistryKey<DamageType> FALLING_ANVIL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("falling_anvil"));
    final static public RegistryKey<DamageType> FALLING_STALACTITE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("falling_stalactite"));
    final static public RegistryKey<DamageType> STING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("sting"));
    final static public RegistryKey<DamageType> MOB_ATTACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("mob_attack"));
    final static public RegistryKey<DamageType> MOB_ATTACK_NO_AGGRO = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("mob_attack_no_aggro"));
    final static public RegistryKey<DamageType> PLAYER_ATTACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("player_attack"));
    final static public RegistryKey<DamageType> ARROW = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("arrow"));
    final static public RegistryKey<DamageType> TRIDENT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("trident"));
    final static public RegistryKey<DamageType> MOB_PROJECTILE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("mob_projectile"));
    final static public RegistryKey<DamageType> SPIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("spit"));
    final static public RegistryKey<DamageType> WIND_CHARGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("wind_charge"));
    final static public RegistryKey<DamageType> FIREWORKS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("fireworks"));
    final static public RegistryKey<DamageType> FIREBALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("fireball"));
    final static public RegistryKey<DamageType> UNATTRIBUTED_FIREBALL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("unattributed_fireball"));
    final static public RegistryKey<DamageType> WITHER_SKULL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("wither_skull"));
    final static public RegistryKey<DamageType> THROWN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("thrown"));
    final static public RegistryKey<DamageType> INDIRECT_MAGIC = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("indirect_magic"));
    final static public RegistryKey<DamageType> THORNS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("thorns"));
    final static public RegistryKey<DamageType> EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("explosion"));
    final static public RegistryKey<DamageType> PLAYER_EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("player_explosion"));
    final static public RegistryKey<DamageType> SONIC_BOOM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("sonic_boom"));
    final static public RegistryKey<DamageType> BAD_RESPAWN_POINT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("bad_respawn_point"));
    final static public RegistryKey<DamageType> OUTSIDE_BORDER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("outside_border"));
    final static public RegistryKey<DamageType> GENERIC_KILL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("generic_kill"));
    final static public RegistryKey<DamageType> MACE_SMASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.ofVanilla("mace_smash"));

    public static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        damageTypeRegisterable.register(IN_FIRE, new DamageType("inFire", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(CAMPFIRE, new DamageType("inFire", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(LIGHTNING_BOLT, new DamageType("lightningBolt", 0.1f));
        damageTypeRegisterable.register(ON_FIRE, new DamageType("onFire", 0.0f, DamageEffects.BURNING));
        damageTypeRegisterable.register(LAVA, new DamageType("lava", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(HOT_FLOOR, new DamageType("hotFloor", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(IN_WALL, new DamageType("inWall", 0.0f));
        damageTypeRegisterable.register(CRAMMING, new DamageType("cramming", 0.0f));
        damageTypeRegisterable.register(DROWN, new DamageType("drown", 0.0f, DamageEffects.DROWNING));
        damageTypeRegisterable.register(STARVE, new DamageType("starve", 0.0f));
        damageTypeRegisterable.register(CACTUS, new DamageType("cactus", 0.1f));
        damageTypeRegisterable.register(FALL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0f, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS));
        damageTypeRegisterable.register(ENDER_PEARL, new DamageType("fall", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0f, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS));
        damageTypeRegisterable.register(FLY_INTO_WALL, new DamageType("flyIntoWall", 0.0f));
        damageTypeRegisterable.register(OUT_OF_WORLD, new DamageType("outOfWorld", 0.0f));
        damageTypeRegisterable.register(GENERIC, new DamageType("generic", 0.0f));
        damageTypeRegisterable.register(MAGIC, new DamageType("magic", 0.0f));
        damageTypeRegisterable.register(WITHER, new DamageType("wither", 0.0f));
        damageTypeRegisterable.register(DRAGON_BREATH, new DamageType("dragonBreath", 0.0f));
        damageTypeRegisterable.register(DRY_OUT, new DamageType("dryout", 0.1f));
        damageTypeRegisterable.register(SWEET_BERRY_BUSH, new DamageType("sweetBerryBush", 0.1f, DamageEffects.POKING));
        damageTypeRegisterable.register(FREEZE, new DamageType("freeze", 0.0f, DamageEffects.FREEZING));
        damageTypeRegisterable.register(STALAGMITE, new DamageType("stalagmite", 0.0f));
        damageTypeRegisterable.register(FALLING_BLOCK, new DamageType("fallingBlock", 0.1f));
        damageTypeRegisterable.register(FALLING_ANVIL, new DamageType("anvil", 0.1f));
        damageTypeRegisterable.register(FALLING_STALACTITE, new DamageType("fallingStalactite", 0.1f));
        damageTypeRegisterable.register(STING, new DamageType("sting", 0.1f));
        damageTypeRegisterable.register(MOB_ATTACK, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(MOB_ATTACK_NO_AGGRO, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(PLAYER_ATTACK, new DamageType("player", 0.1f));
        damageTypeRegisterable.register(ARROW, new DamageType("arrow", 0.1f));
        damageTypeRegisterable.register(TRIDENT, new DamageType("trident", 0.1f));
        damageTypeRegisterable.register(MOB_PROJECTILE, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(SPIT, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(FIREWORKS, new DamageType("fireworks", 0.1f));
        damageTypeRegisterable.register(UNATTRIBUTED_FIREBALL, new DamageType("onFire", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(FIREBALL, new DamageType("fireball", 0.1f, DamageEffects.BURNING));
        damageTypeRegisterable.register(WITHER_SKULL, new DamageType("witherSkull", 0.1f));
        damageTypeRegisterable.register(THROWN, new DamageType("thrown", 0.1f));
        damageTypeRegisterable.register(INDIRECT_MAGIC, new DamageType("indirectMagic", 0.0f));
        damageTypeRegisterable.register(THORNS, new DamageType("thorns", 0.1f, DamageEffects.THORNS));
        damageTypeRegisterable.register(EXPLOSION, new DamageType("explosion", DamageScaling.ALWAYS, 0.1f));
        damageTypeRegisterable.register(PLAYER_EXPLOSION, new DamageType("explosion.player", DamageScaling.ALWAYS, 0.1f));
        damageTypeRegisterable.register(SONIC_BOOM, new DamageType("sonic_boom", DamageScaling.ALWAYS, 0.0f));
        damageTypeRegisterable.register(BAD_RESPAWN_POINT, new DamageType("badRespawnPoint", DamageScaling.ALWAYS, 0.1f, DamageEffects.HURT, DeathMessageType.INTENTIONAL_GAME_DESIGN));
        damageTypeRegisterable.register(OUTSIDE_BORDER, new DamageType("outsideBorder", 0.0f));
        damageTypeRegisterable.register(GENERIC_KILL, new DamageType("genericKill", 0.0f));
        damageTypeRegisterable.register(WIND_CHARGE, new DamageType("mob", 0.1f));
        damageTypeRegisterable.register(MACE_SMASH, new DamageType("mace_smash", 0.1f));
    }
}

