/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;

public class ParticleTypes {
    final static public SimpleParticleType ANGRY_VILLAGER = ParticleTypes.register("angry_villager", false);
    final static public ParticleType<BlockStateParticleEffect> BLOCK = ParticleTypes.register("block", false, BlockStateParticleEffect::createCodec, BlockStateParticleEffect::createPacketCodec);
    final static public ParticleType<BlockStateParticleEffect> BLOCK_MARKER = ParticleTypes.register("block_marker", true, BlockStateParticleEffect::createCodec, BlockStateParticleEffect::createPacketCodec);
    final static public SimpleParticleType BUBBLE = ParticleTypes.register("bubble", false);
    final static public SimpleParticleType CLOUD = ParticleTypes.register("cloud", false);
    final static public SimpleParticleType CRIT = ParticleTypes.register("crit", false);
    final static public SimpleParticleType DAMAGE_INDICATOR = ParticleTypes.register("damage_indicator", true);
    final static public SimpleParticleType DRAGON_BREATH = ParticleTypes.register("dragon_breath", false);
    final static public SimpleParticleType DRIPPING_LAVA = ParticleTypes.register("dripping_lava", false);
    final static public SimpleParticleType FALLING_LAVA = ParticleTypes.register("falling_lava", false);
    final static public SimpleParticleType LANDING_LAVA = ParticleTypes.register("landing_lava", false);
    final static public SimpleParticleType DRIPPING_WATER = ParticleTypes.register("dripping_water", false);
    final static public SimpleParticleType FALLING_WATER = ParticleTypes.register("falling_water", false);
    final static public ParticleType<DustParticleEffect> DUST = ParticleTypes.register("dust", false, type -> DustParticleEffect.CODEC, type -> DustParticleEffect.PACKET_CODEC);
    final static public ParticleType<DustColorTransitionParticleEffect> DUST_COLOR_TRANSITION = ParticleTypes.register("dust_color_transition", false, type -> DustColorTransitionParticleEffect.CODEC, type -> DustColorTransitionParticleEffect.PACKET_CODEC);
    final static public SimpleParticleType EFFECT = ParticleTypes.register("effect", false);
    final static public SimpleParticleType ELDER_GUARDIAN = ParticleTypes.register("elder_guardian", true);
    final static public SimpleParticleType ENCHANTED_HIT = ParticleTypes.register("enchanted_hit", false);
    final static public SimpleParticleType ENCHANT = ParticleTypes.register("enchant", false);
    final static public SimpleParticleType END_ROD = ParticleTypes.register("end_rod", false);
    final static public ParticleType<TintedParticleEffect> ENTITY_EFFECT = ParticleTypes.register("entity_effect", false, TintedParticleEffect::createCodec, TintedParticleEffect::createPacketCodec);
    final static public SimpleParticleType EXPLOSION_EMITTER = ParticleTypes.register("explosion_emitter", true);
    final static public SimpleParticleType EXPLOSION = ParticleTypes.register("explosion", true);
    final static public SimpleParticleType GUST = ParticleTypes.register("gust", true);
    final static public SimpleParticleType SMALL_GUST = ParticleTypes.register("small_gust", false);
    final static public SimpleParticleType GUST_EMITTER_LARGE = ParticleTypes.register("gust_emitter_large", true);
    final static public SimpleParticleType GUST_EMITTER_SMALL = ParticleTypes.register("gust_emitter_small", true);
    final static public SimpleParticleType SONIC_BOOM = ParticleTypes.register("sonic_boom", true);
    final static public ParticleType<BlockStateParticleEffect> FALLING_DUST = ParticleTypes.register("falling_dust", false, BlockStateParticleEffect::createCodec, BlockStateParticleEffect::createPacketCodec);
    final static public SimpleParticleType FIREWORK = ParticleTypes.register("firework", false);
    final static public SimpleParticleType FISHING = ParticleTypes.register("fishing", false);
    final static public SimpleParticleType FLAME = ParticleTypes.register("flame", false);
    final static public SimpleParticleType INFESTED = ParticleTypes.register("infested", false);
    final static public SimpleParticleType CHERRY_LEAVES = ParticleTypes.register("cherry_leaves", false);
    final static public SimpleParticleType PALE_OAK_LEAVES = ParticleTypes.register("pale_oak_leaves", false);
    final static public ParticleType<TintedParticleEffect> TINTED_LEAVES = ParticleTypes.register("tinted_leaves", false, TintedParticleEffect::createCodec, TintedParticleEffect::createPacketCodec);
    final static public SimpleParticleType SCULK_SOUL = ParticleTypes.register("sculk_soul", false);
    final static public ParticleType<SculkChargeParticleEffect> SCULK_CHARGE = ParticleTypes.register("sculk_charge", true, type -> SculkChargeParticleEffect.CODEC, type -> SculkChargeParticleEffect.PACKET_CODEC);
    final static public SimpleParticleType SCULK_CHARGE_POP = ParticleTypes.register("sculk_charge_pop", true);
    final static public SimpleParticleType SOUL_FIRE_FLAME = ParticleTypes.register("soul_fire_flame", false);
    final static public SimpleParticleType SOUL = ParticleTypes.register("soul", false);
    final static public SimpleParticleType FLASH = ParticleTypes.register("flash", false);
    final static public SimpleParticleType HAPPY_VILLAGER = ParticleTypes.register("happy_villager", false);
    final static public SimpleParticleType COMPOSTER = ParticleTypes.register("composter", false);
    final static public SimpleParticleType HEART = ParticleTypes.register("heart", false);
    final static public SimpleParticleType INSTANT_EFFECT = ParticleTypes.register("instant_effect", false);
    final static public ParticleType<ItemStackParticleEffect> ITEM = ParticleTypes.register("item", false, ItemStackParticleEffect::createCodec, ItemStackParticleEffect::createPacketCodec);
    final static public ParticleType<VibrationParticleEffect> VIBRATION = ParticleTypes.register("vibration", true, type -> VibrationParticleEffect.CODEC, type -> VibrationParticleEffect.PACKET_CODEC);
    final static public ParticleType<TrailParticleEffect> TRAIL = ParticleTypes.register("trail", false, type -> TrailParticleEffect.CODEC, type -> TrailParticleEffect.PACKET_CODEC);
    final static public SimpleParticleType ITEM_SLIME = ParticleTypes.register("item_slime", false);
    final static public SimpleParticleType ITEM_COBWEB = ParticleTypes.register("item_cobweb", false);
    final static public SimpleParticleType ITEM_SNOWBALL = ParticleTypes.register("item_snowball", false);
    final static public SimpleParticleType LARGE_SMOKE = ParticleTypes.register("large_smoke", false);
    final static public SimpleParticleType LAVA = ParticleTypes.register("lava", false);
    final static public SimpleParticleType MYCELIUM = ParticleTypes.register("mycelium", false);
    final static public SimpleParticleType NOTE = ParticleTypes.register("note", false);
    final static public SimpleParticleType POOF = ParticleTypes.register("poof", true);
    final static public SimpleParticleType PORTAL = ParticleTypes.register("portal", false);
    final static public SimpleParticleType RAIN = ParticleTypes.register("rain", false);
    final static public SimpleParticleType SMOKE = ParticleTypes.register("smoke", false);
    final static public SimpleParticleType WHITE_SMOKE = ParticleTypes.register("white_smoke", false);
    final static public SimpleParticleType SNEEZE = ParticleTypes.register("sneeze", false);
    final static public SimpleParticleType SPIT = ParticleTypes.register("spit", true);
    final static public SimpleParticleType SQUID_INK = ParticleTypes.register("squid_ink", true);
    final static public SimpleParticleType SWEEP_ATTACK = ParticleTypes.register("sweep_attack", true);
    final static public SimpleParticleType TOTEM_OF_UNDYING = ParticleTypes.register("totem_of_undying", false);
    final static public SimpleParticleType UNDERWATER = ParticleTypes.register("underwater", false);
    final static public SimpleParticleType SPLASH = ParticleTypes.register("splash", false);
    final static public SimpleParticleType WITCH = ParticleTypes.register("witch", false);
    final static public SimpleParticleType BUBBLE_POP = ParticleTypes.register("bubble_pop", false);
    final static public SimpleParticleType CURRENT_DOWN = ParticleTypes.register("current_down", false);
    final static public SimpleParticleType BUBBLE_COLUMN_UP = ParticleTypes.register("bubble_column_up", false);
    final static public SimpleParticleType NAUTILUS = ParticleTypes.register("nautilus", false);
    final static public SimpleParticleType DOLPHIN = ParticleTypes.register("dolphin", false);
    final static public SimpleParticleType CAMPFIRE_COSY_SMOKE = ParticleTypes.register("campfire_cosy_smoke", true);
    final static public SimpleParticleType CAMPFIRE_SIGNAL_SMOKE = ParticleTypes.register("campfire_signal_smoke", true);
    final static public SimpleParticleType DRIPPING_HONEY = ParticleTypes.register("dripping_honey", false);
    final static public SimpleParticleType FALLING_HONEY = ParticleTypes.register("falling_honey", false);
    final static public SimpleParticleType LANDING_HONEY = ParticleTypes.register("landing_honey", false);
    final static public SimpleParticleType FALLING_NECTAR = ParticleTypes.register("falling_nectar", false);
    final static public SimpleParticleType FALLING_SPORE_BLOSSOM = ParticleTypes.register("falling_spore_blossom", false);
    final static public SimpleParticleType ASH = ParticleTypes.register("ash", false);
    final static public SimpleParticleType CRIMSON_SPORE = ParticleTypes.register("crimson_spore", false);
    final static public SimpleParticleType WARPED_SPORE = ParticleTypes.register("warped_spore", false);
    final static public SimpleParticleType SPORE_BLOSSOM_AIR = ParticleTypes.register("spore_blossom_air", false);
    final static public SimpleParticleType DRIPPING_OBSIDIAN_TEAR = ParticleTypes.register("dripping_obsidian_tear", false);
    final static public SimpleParticleType FALLING_OBSIDIAN_TEAR = ParticleTypes.register("falling_obsidian_tear", false);
    final static public SimpleParticleType LANDING_OBSIDIAN_TEAR = ParticleTypes.register("landing_obsidian_tear", false);
    final static public SimpleParticleType REVERSE_PORTAL = ParticleTypes.register("reverse_portal", false);
    final static public SimpleParticleType WHITE_ASH = ParticleTypes.register("white_ash", false);
    final static public SimpleParticleType SMALL_FLAME = ParticleTypes.register("small_flame", false);
    final static public SimpleParticleType SNOWFLAKE = ParticleTypes.register("snowflake", false);
    final static public SimpleParticleType DRIPPING_DRIPSTONE_LAVA = ParticleTypes.register("dripping_dripstone_lava", false);
    final static public SimpleParticleType FALLING_DRIPSTONE_LAVA = ParticleTypes.register("falling_dripstone_lava", false);
    final static public SimpleParticleType DRIPPING_DRIPSTONE_WATER = ParticleTypes.register("dripping_dripstone_water", false);
    final static public SimpleParticleType FALLING_DRIPSTONE_WATER = ParticleTypes.register("falling_dripstone_water", false);
    final static public SimpleParticleType GLOW_SQUID_INK = ParticleTypes.register("glow_squid_ink", true);
    final static public SimpleParticleType GLOW = ParticleTypes.register("glow", true);
    final static public SimpleParticleType WAX_ON = ParticleTypes.register("wax_on", true);
    final static public SimpleParticleType WAX_OFF = ParticleTypes.register("wax_off", true);
    final static public SimpleParticleType ELECTRIC_SPARK = ParticleTypes.register("electric_spark", true);
    final static public SimpleParticleType SCRAPE = ParticleTypes.register("scrape", true);
    final static public ParticleType<ShriekParticleEffect> SHRIEK = ParticleTypes.register("shriek", false, type -> ShriekParticleEffect.CODEC, type -> ShriekParticleEffect.PACKET_CODEC);
    final static public SimpleParticleType EGG_CRACK = ParticleTypes.register("egg_crack", false);
    final static public SimpleParticleType DUST_PLUME = ParticleTypes.register("dust_plume", false);
    final static public SimpleParticleType TRIAL_SPAWNER_DETECTION = ParticleTypes.register("trial_spawner_detection", true);
    final static public SimpleParticleType TRIAL_SPAWNER_DETECTION_OMINOUS = ParticleTypes.register("trial_spawner_detection_ominous", true);
    final static public SimpleParticleType VAULT_CONNECTION = ParticleTypes.register("vault_connection", true);
    final static public ParticleType<BlockStateParticleEffect> DUST_PILLAR = ParticleTypes.register("dust_pillar", false, BlockStateParticleEffect::createCodec, BlockStateParticleEffect::createPacketCodec);
    final static public SimpleParticleType OMINOUS_SPAWNING = ParticleTypes.register("ominous_spawning", true);
    final static public SimpleParticleType RAID_OMEN = ParticleTypes.register("raid_omen", false);
    final static public SimpleParticleType TRIAL_OMEN = ParticleTypes.register("trial_omen", false);
    final static public ParticleType<BlockStateParticleEffect> BLOCK_CRUMBLE = ParticleTypes.register("block_crumble", false, BlockStateParticleEffect::createCodec, BlockStateParticleEffect::createPacketCodec);
    final static public SimpleParticleType FIREFLY = ParticleTypes.register("firefly", false);
    final static public Codec<ParticleEffect> TYPE_CODEC = Registries.PARTICLE_TYPE.getCodec().dispatch("type", ParticleEffect::getType, ParticleType::getCodec);
    final static public PacketCodec<RegistryByteBuf, ParticleEffect> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.PARTICLE_TYPE).dispatch(ParticleEffect::getType, ParticleType::getPacketCodec);

    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, name, new SimpleParticleType(alwaysShow));
    }

    private static <T extends ParticleEffect> ParticleType<T> register(String name, boolean alwaysShow, final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, PacketCodec<? super RegistryByteBuf, T>> packetCodecGetter) {
        return Registry.register(Registries.PARTICLE_TYPE, name, new ParticleType<T>(alwaysShow){

            @Override
            public MapCodec<T> getCodec() {
                return (MapCodec)codecGetter.apply(this);
            }

            @Override
            public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
                return (PacketCodec)packetCodecGetter.apply(this);
            }
        });
    }
}

