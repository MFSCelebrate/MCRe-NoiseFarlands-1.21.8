/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EntityModelLayers {
    final static private String MAIN = "main";
    final static private Set<EntityModelLayer> LAYERS = Sets.newHashSet();
    final static public EntityModelLayer ACACIA_BOAT = EntityModelLayers.registerMain("boat/acacia");
    final static public EntityModelLayer ACACIA_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/acacia");
    final static public EntityModelLayer ALLAY = EntityModelLayers.registerMain("allay");
    final static public EntityModelLayer ARMADILLO = EntityModelLayers.registerMain("armadillo");
    final static public EntityModelLayer ARMADILLO_BABY = EntityModelLayers.registerMain("armadillo_baby");
    final static public EntityModelLayer ARMOR_STAND = EntityModelLayers.registerMain("armor_stand");
    final static public EntityModelLayer ARMOR_STAND_INNER_ARMOR = EntityModelLayers.createInnerArmor("armor_stand");
    final static public EntityModelLayer ARMOR_STAND_OUTER_ARMOR = EntityModelLayers.createOuterArmor("armor_stand");
    final static public EntityModelLayer ARMOR_STAND_SMALL = EntityModelLayers.registerMain("armor_stand_small");
    final static public EntityModelLayer ARMOR_STAND_SMALL_INNER_ARMOR = EntityModelLayers.createInnerArmor("armor_stand_small");
    final static public EntityModelLayer ARMOR_STAND_SMALL_OUTER_ARMOR = EntityModelLayers.createOuterArmor("armor_stand_small");
    final static public EntityModelLayer ARROW = EntityModelLayers.registerMain("arrow");
    final static public EntityModelLayer AXOLOTL = EntityModelLayers.registerMain("axolotl");
    final static public EntityModelLayer AXOLOTL_BABY = EntityModelLayers.registerMain("axolotl_baby");
    final static public EntityModelLayer BAMBOO_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/bamboo");
    final static public EntityModelLayer BAMBOO_BOAT = EntityModelLayers.registerMain("boat/bamboo");
    final static public EntityModelLayer STANDING_BANNER = EntityModelLayers.registerMain("standing_banner");
    final static public EntityModelLayer STANDING_BANNER_FLAG = EntityModelLayers.register("standing_banner", "flag");
    final static public EntityModelLayer WALL_BANNER = EntityModelLayers.registerMain("wall_banner");
    final static public EntityModelLayer WALL_BANNER_FLAG = EntityModelLayers.register("wall_banner", "flag");
    final static public EntityModelLayer BAT = EntityModelLayers.registerMain("bat");
    final static public EntityModelLayer BED_FOOT = EntityModelLayers.registerMain("bed_foot");
    final static public EntityModelLayer BED_HEAD = EntityModelLayers.registerMain("bed_head");
    final static public EntityModelLayer BEE = EntityModelLayers.registerMain("bee");
    final static public EntityModelLayer BEE_BABY = EntityModelLayers.registerMain("bee_baby");
    final static public EntityModelLayer BEE_STINGER = EntityModelLayers.registerMain("bee_stinger");
    final static public EntityModelLayer BELL = EntityModelLayers.registerMain("bell");
    final static public EntityModelLayer BIRCH_BOAT = EntityModelLayers.registerMain("boat/birch");
    final static public EntityModelLayer BIRCH_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/birch");
    final static public EntityModelLayer BLAZE = EntityModelLayers.registerMain("blaze");
    final static public EntityModelLayer BOAT = EntityModelLayers.register("boat", "water_patch");
    final static public EntityModelLayer BOGGED = EntityModelLayers.registerMain("bogged");
    final static public EntityModelLayer BOGGED_INNER_ARMOR = EntityModelLayers.createInnerArmor("bogged");
    final static public EntityModelLayer BOGGED_OUTER_ARMOR = EntityModelLayers.createOuterArmor("bogged");
    final static public EntityModelLayer BOGGED_OUTER = EntityModelLayers.register("bogged", "outer");
    final static public EntityModelLayer BOOK = EntityModelLayers.registerMain("book");
    final static public EntityModelLayer BREEZE = EntityModelLayers.registerMain("breeze");
    final static public EntityModelLayer BREEZE_WIND = EntityModelLayers.registerMain("breeze_wind");
    final static public EntityModelLayer CAMEL = EntityModelLayers.registerMain("camel");
    final static public EntityModelLayer CAMEL_BABY = EntityModelLayers.registerMain("camel_baby");
    final static public EntityModelLayer CAMEL_SADDLE = EntityModelLayers.register("camel", "saddle");
    final static public EntityModelLayer CAMEL_BABY_SADDLE = EntityModelLayers.register("camel_baby", "saddle");
    final static public EntityModelLayer CAT = EntityModelLayers.registerMain("cat");
    final static public EntityModelLayer CAT_BABY = EntityModelLayers.registerMain("cat_baby");
    final static public EntityModelLayer CAT_BABY_COLLAR = EntityModelLayers.register("cat_baby", "collar");
    final static public EntityModelLayer CAT_COLLAR = EntityModelLayers.register("cat", "collar");
    final static public EntityModelLayer CAVE_SPIDER = EntityModelLayers.registerMain("cave_spider");
    final static public EntityModelLayer CHERRY_BOAT = EntityModelLayers.registerMain("boat/cherry");
    final static public EntityModelLayer CHERRY_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/cherry");
    final static public EntityModelLayer CHEST = EntityModelLayers.registerMain("chest");
    final static public EntityModelLayer CHEST_MINECART = EntityModelLayers.registerMain("chest_minecart");
    final static public EntityModelLayer CHICKEN = EntityModelLayers.registerMain("chicken");
    final static public EntityModelLayer CHICKEN_BABY = EntityModelLayers.registerMain("chicken_baby");
    final static public EntityModelLayer COD = EntityModelLayers.registerMain("cod");
    final static public EntityModelLayer COLD_CHICKEN = EntityModelLayers.registerMain("cold_chicken");
    final static public EntityModelLayer COLD_CHICKEN_BABY = EntityModelLayers.registerMain("cold_chicken_baby");
    final static public EntityModelLayer COLD_COW = EntityModelLayers.registerMain("cold_cow");
    final static public EntityModelLayer COLD_COW_BABY = EntityModelLayers.registerMain("cold_cow_baby");
    final static public EntityModelLayer COLD_PIG = EntityModelLayers.registerMain("cold_pig");
    final static public EntityModelLayer COLD_PIG_BABY = EntityModelLayers.registerMain("cold_pig_baby");
    final static public EntityModelLayer COMMAND_BLOCK_MINECART = EntityModelLayers.registerMain("command_block_minecart");
    final static public EntityModelLayer CONDUIT = EntityModelLayers.register("conduit", "cage");
    final static public EntityModelLayer CONDUIT_EYE = EntityModelLayers.register("conduit", "eye");
    final static public EntityModelLayer CONDUIT_SHELL = EntityModelLayers.register("conduit", "shell");
    final static public EntityModelLayer CONDUIT_WIND = EntityModelLayers.register("conduit", "wind");
    final static public EntityModelLayer COW = EntityModelLayers.registerMain("cow");
    final static public EntityModelLayer COW_BABY = EntityModelLayers.registerMain("cow_baby");
    final static public EntityModelLayer CREAKING = EntityModelLayers.registerMain("creaking");
    final static public EntityModelLayer CREEPER = EntityModelLayers.registerMain("creeper");
    final static public EntityModelLayer CREEPER_ARMOR = EntityModelLayers.register("creeper", "armor");
    final static public EntityModelLayer CREEPER_HEAD = EntityModelLayers.registerMain("creeper_head");
    final static public EntityModelLayer DARK_OAK_BOAT = EntityModelLayers.registerMain("boat/dark_oak");
    final static public EntityModelLayer DARK_OAK_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/dark_oak");
    final static public EntityModelLayer DECORATED_POT_BASE = EntityModelLayers.registerMain("decorated_pot_base");
    final static public EntityModelLayer DECORATED_POT_SIDES = EntityModelLayers.registerMain("decorated_pot_sides");
    final static public EntityModelLayer DOLPHIN = EntityModelLayers.registerMain("dolphin");
    final static public EntityModelLayer DOLPHIN_BABY = EntityModelLayers.registerMain("dolphin_baby");
    final static public EntityModelLayer DONKEY = EntityModelLayers.registerMain("donkey");
    final static public EntityModelLayer DONKEY_BABY = EntityModelLayers.registerMain("donkey_baby");
    final static public EntityModelLayer DONKEY_SADDLE = EntityModelLayers.register("donkey", "saddle");
    final static public EntityModelLayer DONKEY_BABY_SADDLE = EntityModelLayers.register("donkey_baby", "saddle");
    final static public EntityModelLayer DOUBLE_CHEST_LEFT = EntityModelLayers.registerMain("double_chest_left");
    final static public EntityModelLayer DOUBLE_CHEST_RIGHT = EntityModelLayers.registerMain("double_chest_right");
    final static public EntityModelLayer DRAGON_SKULL = EntityModelLayers.registerMain("dragon_skull");
    final static public EntityModelLayer DROWNED = EntityModelLayers.registerMain("drowned");
    final static public EntityModelLayer DROWNED_BABY = EntityModelLayers.registerMain("drowned_baby");
    final static public EntityModelLayer DROWNED_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("drowned_baby");
    final static public EntityModelLayer DROWNED_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("drowned_baby");
    final static public EntityModelLayer DROWNED_BABY_OUTER = EntityModelLayers.register("drowned_baby", "outer");
    final static public EntityModelLayer DROWNED_INNER_ARMOR = EntityModelLayers.createInnerArmor("drowned");
    final static public EntityModelLayer DROWNED_OUTER_ARMOR = EntityModelLayers.createOuterArmor("drowned");
    final static public EntityModelLayer DROWNED_OUTER = EntityModelLayers.register("drowned", "outer");
    final static public EntityModelLayer ELDER_GUARDIAN = EntityModelLayers.registerMain("elder_guardian");
    final static public EntityModelLayer ELYTRA = EntityModelLayers.registerMain("elytra");
    final static public EntityModelLayer ELYTRA_BABY = EntityModelLayers.registerMain("elytra_baby");
    final static public EntityModelLayer ENDERMAN = EntityModelLayers.registerMain("enderman");
    final static public EntityModelLayer ENDERMITE = EntityModelLayers.registerMain("endermite");
    final static public EntityModelLayer ENDER_DRAGON = EntityModelLayers.registerMain("ender_dragon");
    final static public EntityModelLayer END_CRYSTAL = EntityModelLayers.registerMain("end_crystal");
    final static public EntityModelLayer EVOKER = EntityModelLayers.registerMain("evoker");
    final static public EntityModelLayer EVOKER_FANGS = EntityModelLayers.registerMain("evoker_fangs");
    final static public EntityModelLayer FOX = EntityModelLayers.registerMain("fox");
    final static public EntityModelLayer FOX_BABY = EntityModelLayers.registerMain("fox_baby");
    final static public EntityModelLayer FROG = EntityModelLayers.registerMain("frog");
    final static public EntityModelLayer FURNACE_MINECART = EntityModelLayers.registerMain("furnace_minecart");
    final static public EntityModelLayer GHAST = EntityModelLayers.registerMain("ghast");
    final static public EntityModelLayer GIANT = EntityModelLayers.registerMain("giant");
    final static public EntityModelLayer GIANT_INNER_ARMOR = EntityModelLayers.createInnerArmor("giant");
    final static public EntityModelLayer GIANT_OUTER_ARMOR = EntityModelLayers.createOuterArmor("giant");
    final static public EntityModelLayer GLOW_SQUID = EntityModelLayers.registerMain("glow_squid");
    final static public EntityModelLayer GLOW_SQUID_BABY = EntityModelLayers.registerMain("glow_squid_baby");
    final static public EntityModelLayer GOAT = EntityModelLayers.registerMain("goat");
    final static public EntityModelLayer GOAT_BABY = EntityModelLayers.registerMain("goat_baby");
    final static public EntityModelLayer GUARDIAN = EntityModelLayers.registerMain("guardian");
    final static public EntityModelLayer HAPPY_GHAST = EntityModelLayers.registerMain("happy_ghast");
    final static public EntityModelLayer HAPPY_GHAST_BABY = EntityModelLayers.registerMain("happy_ghast_baby");
    final static public EntityModelLayer HAPPY_GHAST_HARNESS = EntityModelLayers.registerMain("happy_ghast_harness");
    final static public EntityModelLayer HAPPY_GHAST_BABY_HARNESS = EntityModelLayers.registerMain("happy_ghast_baby_harness");
    final static public EntityModelLayer HAPPY_GHAST_ROPES = EntityModelLayers.registerMain("happy_ghast_ropes");
    final static public EntityModelLayer HAPPY_GHAST_BABY_ROPES = EntityModelLayers.registerMain("happy_ghast_baby_ropes");
    final static public EntityModelLayer HOGLIN = EntityModelLayers.registerMain("hoglin");
    final static public EntityModelLayer HOGLIN_BABY = EntityModelLayers.registerMain("hoglin_baby");
    final static public EntityModelLayer HOPPER_MINECART = EntityModelLayers.registerMain("hopper_minecart");
    final static public EntityModelLayer HORSE = EntityModelLayers.registerMain("horse");
    final static public EntityModelLayer HORSE_ARMOR = EntityModelLayers.registerMain("horse_armor");
    final static public EntityModelLayer HORSE_SADDLE = EntityModelLayers.register("horse", "saddle");
    final static public EntityModelLayer HORSE_BABY = EntityModelLayers.registerMain("horse_baby");
    final static public EntityModelLayer HORSE_ARMOR_BABY = EntityModelLayers.registerMain("horse_armor_baby");
    final static public EntityModelLayer HORSE_BABY_SADDLE = EntityModelLayers.register("horse_baby", "saddle");
    final static public EntityModelLayer HUSK = EntityModelLayers.registerMain("husk");
    final static public EntityModelLayer HUSK_BABY = EntityModelLayers.registerMain("husk_baby");
    final static public EntityModelLayer HUSK_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("husk_baby");
    final static public EntityModelLayer HUSK_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("husk_baby");
    final static public EntityModelLayer HUSK_INNER_ARMOR = EntityModelLayers.createInnerArmor("husk");
    final static public EntityModelLayer HUSK_OUTER_ARMOR = EntityModelLayers.createOuterArmor("husk");
    final static public EntityModelLayer ILLUSIONER = EntityModelLayers.registerMain("illusioner");
    final static public EntityModelLayer IRON_GOLEM = EntityModelLayers.registerMain("iron_golem");
    final static public EntityModelLayer JUNGLE_BOAT = EntityModelLayers.registerMain("boat/jungle");
    final static public EntityModelLayer JUNGLE_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/jungle");
    final static public EntityModelLayer LEASH_KNOT = EntityModelLayers.registerMain("leash_knot");
    final static public EntityModelLayer LLAMA = EntityModelLayers.registerMain("llama");
    final static public EntityModelLayer LLAMA_BABY = EntityModelLayers.registerMain("llama_baby");
    final static public EntityModelLayer LLAMA_BABY_DECOR = EntityModelLayers.register("llama_baby", "decor");
    final static public EntityModelLayer LLAMA_DECOR = EntityModelLayers.register("llama", "decor");
    final static public EntityModelLayer LLAMA_SPIT = EntityModelLayers.registerMain("llama_spit");
    final static public EntityModelLayer MAGMA_CUBE = EntityModelLayers.registerMain("magma_cube");
    final static public EntityModelLayer MANGROVE_BOAT = EntityModelLayers.registerMain("boat/mangrove");
    final static public EntityModelLayer MANGROVE_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/mangrove");
    final static public EntityModelLayer MINECART = EntityModelLayers.registerMain("minecart");
    final static public EntityModelLayer MOOSHROOM = EntityModelLayers.registerMain("mooshroom");
    final static public EntityModelLayer MOOSHROOM_BABY = EntityModelLayers.registerMain("mooshroom_baby");
    final static public EntityModelLayer MULE = EntityModelLayers.registerMain("mule");
    final static public EntityModelLayer MULE_BABY = EntityModelLayers.registerMain("mule_baby");
    final static public EntityModelLayer MULE_SADDLE = EntityModelLayers.register("mule", "saddle");
    final static public EntityModelLayer MULE_BABY_SADDLE = EntityModelLayers.register("mule_baby", "saddle");
    final static public EntityModelLayer OAK_BOAT = EntityModelLayers.registerMain("boat/oak");
    final static public EntityModelLayer OAK_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/oak");
    final static public EntityModelLayer OCELOT = EntityModelLayers.registerMain("ocelot");
    final static public EntityModelLayer OCELOT_BABY = EntityModelLayers.registerMain("ocelot_baby");
    final static public EntityModelLayer PALE_OAK_BOAT = EntityModelLayers.registerMain("boat/pale_oak");
    final static public EntityModelLayer PALE_OAK_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/pale_oak");
    final static public EntityModelLayer PANDA = EntityModelLayers.registerMain("panda");
    final static public EntityModelLayer PANDA_BABY = EntityModelLayers.registerMain("panda_baby");
    final static public EntityModelLayer PARROT = EntityModelLayers.registerMain("parrot");
    final static public EntityModelLayer PHANTOM = EntityModelLayers.registerMain("phantom");
    final static public EntityModelLayer PIG = EntityModelLayers.registerMain("pig");
    final static public EntityModelLayer PIGLIN = EntityModelLayers.registerMain("piglin");
    final static public EntityModelLayer PIGLIN_BABY = EntityModelLayers.registerMain("piglin_baby");
    final static public EntityModelLayer PIGLIN_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin_baby");
    final static public EntityModelLayer PIGLIN_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin_baby");
    final static public EntityModelLayer PIGLIN_BRUTE = EntityModelLayers.registerMain("piglin_brute");
    final static public EntityModelLayer PIGLIN_BRUTE_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin_brute");
    final static public EntityModelLayer PIGLIN_BRUTE_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin_brute");
    final static public EntityModelLayer PIGLIN_HEAD = EntityModelLayers.registerMain("piglin_head");
    final static public EntityModelLayer PIGLIN_INNER_ARMOR = EntityModelLayers.createInnerArmor("piglin");
    final static public EntityModelLayer PIGLIN_OUTER_ARMOR = EntityModelLayers.createOuterArmor("piglin");
    final static public EntityModelLayer PIG_BABY = EntityModelLayers.registerMain("pig_baby");
    final static public EntityModelLayer PIG_BABY_SADDLE = EntityModelLayers.register("pig_baby", "saddle");
    final static public EntityModelLayer PIG_SADDLE = EntityModelLayers.register("pig", "saddle");
    final static public EntityModelLayer PILLAGER = EntityModelLayers.registerMain("pillager");
    final static public EntityModelLayer PLAYER = EntityModelLayers.registerMain("player");
    final static public EntityModelLayer PLAYER_CAPE = EntityModelLayers.register("player", "cape");
    final static public EntityModelLayer PLAYER_EARS = EntityModelLayers.register("player", "ears");
    final static public EntityModelLayer PLAYER_HEAD = EntityModelLayers.registerMain("player_head");
    final static public EntityModelLayer PLAYER_INNER_ARMOR = EntityModelLayers.createInnerArmor("player");
    final static public EntityModelLayer PLAYER_OUTER_ARMOR = EntityModelLayers.createOuterArmor("player");
    final static public EntityModelLayer PLAYER_SLIM = EntityModelLayers.registerMain("player_slim");
    final static public EntityModelLayer PLAYER_SLIM_INNER_ARMOR = EntityModelLayers.createInnerArmor("player_slim");
    final static public EntityModelLayer PLAYER_SLIM_OUTER_ARMOR = EntityModelLayers.createOuterArmor("player_slim");
    final static public EntityModelLayer SPIN_ATTACK = EntityModelLayers.registerMain("spin_attack");
    final static public EntityModelLayer POLAR_BEAR = EntityModelLayers.registerMain("polar_bear");
    final static public EntityModelLayer POLAR_BEAR_BABY = EntityModelLayers.registerMain("polar_bear_baby");
    final static public EntityModelLayer PUFFERFISH_BIG = EntityModelLayers.registerMain("pufferfish_big");
    final static public EntityModelLayer PUFFERFISH_MEDIUM = EntityModelLayers.registerMain("pufferfish_medium");
    final static public EntityModelLayer PUFFERFISH_SMALL = EntityModelLayers.registerMain("pufferfish_small");
    final static public EntityModelLayer RABBIT = EntityModelLayers.registerMain("rabbit");
    final static public EntityModelLayer RABBIT_BABY = EntityModelLayers.registerMain("rabbit_baby");
    final static public EntityModelLayer RAVAGER = EntityModelLayers.registerMain("ravager");
    final static public EntityModelLayer SALMON = EntityModelLayers.registerMain("salmon");
    final static public EntityModelLayer SALMON_LARGE = EntityModelLayers.registerMain("salmon_large");
    final static public EntityModelLayer SALMON_SMALL = EntityModelLayers.registerMain("salmon_small");
    final static public EntityModelLayer SHEEP = EntityModelLayers.registerMain("sheep");
    final static public EntityModelLayer SHEEP_BABY = EntityModelLayers.registerMain("sheep_baby");
    final static public EntityModelLayer SHEEP_BABY_WOOL = EntityModelLayers.register("sheep_baby", "wool");
    final static public EntityModelLayer SHEEP_WOOL = EntityModelLayers.register("sheep", "wool");
    final static public EntityModelLayer SHEEP_WOOL_UNDERCOAT = EntityModelLayers.register("sheep", "wool_undercoat");
    final static public EntityModelLayer SHEEP_BABY_WOOL_UNDERCOAT = EntityModelLayers.register("sheep_baby", "wool_undercoat");
    final static public EntityModelLayer SHIELD = EntityModelLayers.registerMain("shield");
    final static public EntityModelLayer SHULKER = EntityModelLayers.registerMain("shulker");
    final static public EntityModelLayer SHULKER_BOX = EntityModelLayers.registerMain("shulker_box");
    final static public EntityModelLayer SHULKER_BULLET = EntityModelLayers.registerMain("shulker_bullet");
    final static public EntityModelLayer SILVERFISH = EntityModelLayers.registerMain("silverfish");
    final static public EntityModelLayer SKELETON = EntityModelLayers.registerMain("skeleton");
    final static public EntityModelLayer SKELETON_HORSE = EntityModelLayers.registerMain("skeleton_horse");
    final static public EntityModelLayer SKELETON_HORSE_BABY = EntityModelLayers.registerMain("skeleton_horse_baby");
    final static public EntityModelLayer SKELETON_HORSE_SADDLE = EntityModelLayers.register("skeleton_horse", "saddle");
    final static public EntityModelLayer SKELETON_HORSE_BABY_SADDLE = EntityModelLayers.register("skeleton_horse_baby", "saddle");
    final static public EntityModelLayer SKELETON_INNER_ARMOR = EntityModelLayers.createInnerArmor("skeleton");
    final static public EntityModelLayer SKELETON_OUTER_ARMOR = EntityModelLayers.createOuterArmor("skeleton");
    final static public EntityModelLayer SKELETON_SKULL = EntityModelLayers.registerMain("skeleton_skull");
    final static public EntityModelLayer SLIME = EntityModelLayers.registerMain("slime");
    final static public EntityModelLayer SLIME_OUTER = EntityModelLayers.register("slime", "outer");
    final static public EntityModelLayer SNIFFER = EntityModelLayers.registerMain("sniffer");
    final static public EntityModelLayer SNIFFER_BABY = EntityModelLayers.registerMain("sniffer_baby");
    final static public EntityModelLayer SNOW_GOLEM = EntityModelLayers.registerMain("snow_golem");
    final static public EntityModelLayer SPAWNER_MINECART = EntityModelLayers.registerMain("spawner_minecart");
    final static public EntityModelLayer SPIDER = EntityModelLayers.registerMain("spider");
    final static public EntityModelLayer SPRUCE_BOAT = EntityModelLayers.registerMain("boat/spruce");
    final static public EntityModelLayer SPRUCE_CHEST_BOAT = EntityModelLayers.registerMain("chest_boat/spruce");
    final static public EntityModelLayer SQUID = EntityModelLayers.registerMain("squid");
    final static public EntityModelLayer SQUID_BABY = EntityModelLayers.registerMain("squid_baby");
    final static public EntityModelLayer STRAY = EntityModelLayers.registerMain("stray");
    final static public EntityModelLayer STRAY_INNER_ARMOR = EntityModelLayers.createInnerArmor("stray");
    final static public EntityModelLayer STRAY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("stray");
    final static public EntityModelLayer STRAY_OUTER = EntityModelLayers.register("stray", "outer");
    final static public EntityModelLayer STRIDER = EntityModelLayers.registerMain("strider");
    final static public EntityModelLayer STRIDER_SADDLE = EntityModelLayers.register("strider", "saddle");
    final static public EntityModelLayer STRIDER_BABY = EntityModelLayers.registerMain("strider_baby");
    final static public EntityModelLayer STRIDER_BABY_SADDLE = EntityModelLayers.register("strider_baby", "saddle");
    final static public EntityModelLayer TADPOLE = EntityModelLayers.registerMain("tadpole");
    final static public EntityModelLayer TNT_MINECART = EntityModelLayers.registerMain("tnt_minecart");
    final static public EntityModelLayer TRADER_LLAMA = EntityModelLayers.registerMain("trader_llama");
    final static public EntityModelLayer TRADER_LLAMA_BABY = EntityModelLayers.registerMain("trader_llama_baby");
    final static public EntityModelLayer TRIDENT = EntityModelLayers.registerMain("trident");
    final static public EntityModelLayer TROPICAL_FISH_LARGE = EntityModelLayers.registerMain("tropical_fish_large");
    final static public EntityModelLayer TROPICAL_FISH_LARGE_PATTERN = EntityModelLayers.register("tropical_fish_large", "pattern");
    final static public EntityModelLayer TROPICAL_FISH_SMALL = EntityModelLayers.registerMain("tropical_fish_small");
    final static public EntityModelLayer TROPICAL_FISH_SMALL_PATTERN = EntityModelLayers.register("tropical_fish_small", "pattern");
    final static public EntityModelLayer TURTLE = EntityModelLayers.registerMain("turtle");
    final static public EntityModelLayer TURTLE_BABY = EntityModelLayers.registerMain("turtle_baby");
    final static public EntityModelLayer VEX = EntityModelLayers.registerMain("vex");
    final static public EntityModelLayer VILLAGER = EntityModelLayers.registerMain("villager");
    final static public EntityModelLayer VILLAGER_BABY = EntityModelLayers.registerMain("villager_baby");
    final static public EntityModelLayer VINDICATOR = EntityModelLayers.registerMain("vindicator");
    final static public EntityModelLayer WANDERING_TRADER = EntityModelLayers.registerMain("wandering_trader");
    final static public EntityModelLayer WARDEN = EntityModelLayers.registerMain("warden");
    final static public EntityModelLayer WARM_COW = EntityModelLayers.registerMain("warm_cow");
    final static public EntityModelLayer WARM_COW_BABY = EntityModelLayers.registerMain("warm_cow_baby");
    final static public EntityModelLayer WIND_CHARGE = EntityModelLayers.registerMain("wind_charge");
    final static public EntityModelLayer WITCH = EntityModelLayers.registerMain("witch");
    final static public EntityModelLayer WITHER = EntityModelLayers.registerMain("wither");
    final static public EntityModelLayer WITHER_ARMOR = EntityModelLayers.register("wither", "armor");
    final static public EntityModelLayer WITHER_SKELETON = EntityModelLayers.registerMain("wither_skeleton");
    final static public EntityModelLayer WITHER_SKELETON_INNER_ARMOR = EntityModelLayers.createInnerArmor("wither_skeleton");
    final static public EntityModelLayer WITHER_SKELETON_OUTER_ARMOR = EntityModelLayers.createOuterArmor("wither_skeleton");
    final static public EntityModelLayer WITHER_SKELETON_SKULL = EntityModelLayers.registerMain("wither_skeleton_skull");
    final static public EntityModelLayer WITHER_SKULL = EntityModelLayers.registerMain("wither_skull");
    final static public EntityModelLayer WOLF = EntityModelLayers.registerMain("wolf");
    final static public EntityModelLayer WOLF_ARMOR = EntityModelLayers.registerMain("wolf_armor");
    final static public EntityModelLayer WOLF_BABY = EntityModelLayers.registerMain("wolf_baby");
    final static public EntityModelLayer WOLF_BABY_ARMOR = EntityModelLayers.registerMain("wolf_baby_armor");
    final static public EntityModelLayer ZOGLIN = EntityModelLayers.registerMain("zoglin");
    final static public EntityModelLayer ZOGLIN_BABY = EntityModelLayers.registerMain("zoglin_baby");
    final static public EntityModelLayer ZOMBIE = EntityModelLayers.registerMain("zombie");
    final static public EntityModelLayer ZOMBIE_BABY = EntityModelLayers.registerMain("zombie_baby");
    final static public EntityModelLayer ZOMBIE_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie_baby");
    final static public EntityModelLayer ZOMBIE_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie_baby");
    final static public EntityModelLayer ZOMBIE_HEAD = EntityModelLayers.registerMain("zombie_head");
    final static public EntityModelLayer ZOMBIE_HORSE = EntityModelLayers.registerMain("zombie_horse");
    final static public EntityModelLayer ZOMBIE_HORSE_BABY = EntityModelLayers.registerMain("zombie_horse_baby");
    final static public EntityModelLayer ZOMBIE_HORSE_SADDLE = EntityModelLayers.register("zombie_horse", "saddle");
    final static public EntityModelLayer ZOMBIE_HORSE_BABY_SADDLE = EntityModelLayers.register("zombie_horse_baby", "saddle");
    final static public EntityModelLayer ZOMBIE_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie");
    final static public EntityModelLayer ZOMBIE_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie");
    final static public EntityModelLayer ZOMBIE_VILLAGER = EntityModelLayers.registerMain("zombie_villager");
    final static public EntityModelLayer ZOMBIE_VILLAGER_BABY = EntityModelLayers.registerMain("zombie_villager_baby");
    final static public EntityModelLayer ZOMBIE_VILLAGER_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie_villager_baby");
    final static public EntityModelLayer ZOMBIE_VILLAGER_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie_villager_baby");
    final static public EntityModelLayer ZOMBIE_VILLAGER_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombie_villager");
    final static public EntityModelLayer ZOMBIE_VILLAGER_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombie_villager");
    final static public EntityModelLayer ZOMBIFIED_PIGLIN = EntityModelLayers.registerMain("zombified_piglin");
    final static public EntityModelLayer ZOMBIFIED_PIGLIN_BABY = EntityModelLayers.registerMain("zombified_piglin_baby");
    final static public EntityModelLayer ZOMBIFIED_PIGLIN_BABY_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombified_piglin_baby");
    final static public EntityModelLayer ZOMBIFIED_PIGLIN_BABY_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombified_piglin_baby");
    final static public EntityModelLayer ZOMBIFIED_PIGLIN_INNER_ARMOR = EntityModelLayers.createInnerArmor("zombified_piglin");
    final static public EntityModelLayer ZOMBIFIED_PIGLIN_OUTER_ARMOR = EntityModelLayers.createOuterArmor("zombified_piglin");

    private static EntityModelLayer registerMain(String id) {
        return EntityModelLayers.register(id, MAIN);
    }

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = EntityModelLayers.create(id, layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + String.valueOf(entityModelLayer));
        }
        return entityModelLayer;
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(Identifier.ofVanilla(id), layer);
    }

    private static EntityModelLayer createInnerArmor(String id) {
        return EntityModelLayers.register(id, "inner_armor");
    }

    private static EntityModelLayer createOuterArmor(String id) {
        return EntityModelLayers.register(id, "outer_armor");
    }

    public static EntityModelLayer createStandingSign(WoodType type) {
        return EntityModelLayers.create("sign/standing/" + type.name(), MAIN);
    }

    public static EntityModelLayer createWallSign(WoodType type) {
        return EntityModelLayers.create("sign/wall/" + type.name(), MAIN);
    }

    public static EntityModelLayer createHangingSign(WoodType type, HangingSignBlockEntityRenderer.AttachmentType attachmentType) {
        return EntityModelLayers.create("hanging_sign/" + type.name() + "/" + attachmentType.asString(), MAIN);
    }

    public static Stream<EntityModelLayer> getLayers() {
        return LAYERS.stream();
    }
}

