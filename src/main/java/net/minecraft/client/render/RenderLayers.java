/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RenderLayers {
    final static private Map<Block, BlockRenderLayer> BLOCKS = Util.make(Maps.newHashMap(), map -> {
        BlockRenderLayer blockRenderLayer = BlockRenderLayer.TRIPWIRE;
        map.put(Blocks.TRIPWIRE, blockRenderLayer);
        BlockRenderLayer blockRenderLayer2 = BlockRenderLayer.CUTOUT_MIPPED;
        map.put(Blocks.GRASS_BLOCK, blockRenderLayer2);
        map.put(Blocks.IRON_BARS, blockRenderLayer2);
        map.put(Blocks.GLASS_PANE, blockRenderLayer2);
        map.put(Blocks.TRIPWIRE_HOOK, blockRenderLayer2);
        map.put(Blocks.HOPPER, blockRenderLayer2);
        map.put(Blocks.CHAIN, blockRenderLayer2);
        map.put(Blocks.JUNGLE_LEAVES, blockRenderLayer2);
        map.put(Blocks.OAK_LEAVES, blockRenderLayer2);
        map.put(Blocks.SPRUCE_LEAVES, blockRenderLayer2);
        map.put(Blocks.ACACIA_LEAVES, blockRenderLayer2);
        map.put(Blocks.CHERRY_LEAVES, blockRenderLayer2);
        map.put(Blocks.BIRCH_LEAVES, blockRenderLayer2);
        map.put(Blocks.DARK_OAK_LEAVES, blockRenderLayer2);
        map.put(Blocks.PALE_OAK_LEAVES, blockRenderLayer2);
        map.put(Blocks.AZALEA_LEAVES, blockRenderLayer2);
        map.put(Blocks.FLOWERING_AZALEA_LEAVES, blockRenderLayer2);
        map.put(Blocks.MANGROVE_ROOTS, blockRenderLayer2);
        map.put(Blocks.MANGROVE_LEAVES, blockRenderLayer2);
        BlockRenderLayer blockRenderLayer3 = BlockRenderLayer.CUTOUT;
        map.put(Blocks.OAK_SAPLING, blockRenderLayer3);
        map.put(Blocks.SPRUCE_SAPLING, blockRenderLayer3);
        map.put(Blocks.BIRCH_SAPLING, blockRenderLayer3);
        map.put(Blocks.JUNGLE_SAPLING, blockRenderLayer3);
        map.put(Blocks.ACACIA_SAPLING, blockRenderLayer3);
        map.put(Blocks.CHERRY_SAPLING, blockRenderLayer3);
        map.put(Blocks.DARK_OAK_SAPLING, blockRenderLayer3);
        map.put(Blocks.PALE_OAK_SAPLING, blockRenderLayer3);
        map.put(Blocks.GLASS, blockRenderLayer3);
        map.put(Blocks.WHITE_BED, blockRenderLayer3);
        map.put(Blocks.ORANGE_BED, blockRenderLayer3);
        map.put(Blocks.MAGENTA_BED, blockRenderLayer3);
        map.put(Blocks.LIGHT_BLUE_BED, blockRenderLayer3);
        map.put(Blocks.YELLOW_BED, blockRenderLayer3);
        map.put(Blocks.LIME_BED, blockRenderLayer3);
        map.put(Blocks.PINK_BED, blockRenderLayer3);
        map.put(Blocks.GRAY_BED, blockRenderLayer3);
        map.put(Blocks.LIGHT_GRAY_BED, blockRenderLayer3);
        map.put(Blocks.CYAN_BED, blockRenderLayer3);
        map.put(Blocks.PURPLE_BED, blockRenderLayer3);
        map.put(Blocks.BLUE_BED, blockRenderLayer3);
        map.put(Blocks.BROWN_BED, blockRenderLayer3);
        map.put(Blocks.GREEN_BED, blockRenderLayer3);
        map.put(Blocks.RED_BED, blockRenderLayer3);
        map.put(Blocks.BLACK_BED, blockRenderLayer3);
        map.put(Blocks.POWERED_RAIL, blockRenderLayer3);
        map.put(Blocks.DETECTOR_RAIL, blockRenderLayer3);
        map.put(Blocks.COBWEB, blockRenderLayer3);
        map.put(Blocks.SHORT_GRASS, blockRenderLayer3);
        map.put(Blocks.FERN, blockRenderLayer3);
        map.put(Blocks.BUSH, blockRenderLayer3);
        map.put(Blocks.DEAD_BUSH, blockRenderLayer3);
        map.put(Blocks.SHORT_DRY_GRASS, blockRenderLayer3);
        map.put(Blocks.TALL_DRY_GRASS, blockRenderLayer3);
        map.put(Blocks.SEAGRASS, blockRenderLayer3);
        map.put(Blocks.TALL_SEAGRASS, blockRenderLayer3);
        map.put(Blocks.DANDELION, blockRenderLayer3);
        map.put(Blocks.OPEN_EYEBLOSSOM, blockRenderLayer3);
        map.put(Blocks.CLOSED_EYEBLOSSOM, blockRenderLayer3);
        map.put(Blocks.POPPY, blockRenderLayer3);
        map.put(Blocks.BLUE_ORCHID, blockRenderLayer3);
        map.put(Blocks.ALLIUM, blockRenderLayer3);
        map.put(Blocks.AZURE_BLUET, blockRenderLayer3);
        map.put(Blocks.RED_TULIP, blockRenderLayer3);
        map.put(Blocks.ORANGE_TULIP, blockRenderLayer3);
        map.put(Blocks.WHITE_TULIP, blockRenderLayer3);
        map.put(Blocks.PINK_TULIP, blockRenderLayer3);
        map.put(Blocks.OXEYE_DAISY, blockRenderLayer3);
        map.put(Blocks.CORNFLOWER, blockRenderLayer3);
        map.put(Blocks.WITHER_ROSE, blockRenderLayer3);
        map.put(Blocks.LILY_OF_THE_VALLEY, blockRenderLayer3);
        map.put(Blocks.BROWN_MUSHROOM, blockRenderLayer3);
        map.put(Blocks.RED_MUSHROOM, blockRenderLayer3);
        map.put(Blocks.TORCH, blockRenderLayer3);
        map.put(Blocks.WALL_TORCH, blockRenderLayer3);
        map.put(Blocks.SOUL_TORCH, blockRenderLayer3);
        map.put(Blocks.SOUL_WALL_TORCH, blockRenderLayer3);
        map.put(Blocks.FIRE, blockRenderLayer3);
        map.put(Blocks.SOUL_FIRE, blockRenderLayer3);
        map.put(Blocks.SPAWNER, blockRenderLayer3);
        map.put(Blocks.TRIAL_SPAWNER, blockRenderLayer3);
        map.put(Blocks.VAULT, blockRenderLayer3);
        map.put(Blocks.REDSTONE_WIRE, blockRenderLayer3);
        map.put(Blocks.WHEAT, blockRenderLayer3);
        map.put(Blocks.OAK_DOOR, blockRenderLayer3);
        map.put(Blocks.LADDER, blockRenderLayer3);
        map.put(Blocks.RAIL, blockRenderLayer3);
        map.put(Blocks.IRON_DOOR, blockRenderLayer3);
        map.put(Blocks.REDSTONE_TORCH, blockRenderLayer3);
        map.put(Blocks.REDSTONE_WALL_TORCH, blockRenderLayer3);
        map.put(Blocks.CACTUS, blockRenderLayer3);
        map.put(Blocks.SUGAR_CANE, blockRenderLayer3);
        map.put(Blocks.REPEATER, blockRenderLayer3);
        map.put(Blocks.OAK_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.SPRUCE_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.BIRCH_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.JUNGLE_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.ACACIA_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.CHERRY_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.DARK_OAK_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.PALE_OAK_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.CRIMSON_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.WARPED_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.MANGROVE_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.BAMBOO_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.EXPOSED_COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.WEATHERED_COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.ATTACHED_PUMPKIN_STEM, blockRenderLayer3);
        map.put(Blocks.ATTACHED_MELON_STEM, blockRenderLayer3);
        map.put(Blocks.PUMPKIN_STEM, blockRenderLayer3);
        map.put(Blocks.MELON_STEM, blockRenderLayer3);
        map.put(Blocks.VINE, blockRenderLayer3);
        map.put(Blocks.PALE_MOSS_CARPET, blockRenderLayer3);
        map.put(Blocks.PALE_HANGING_MOSS, blockRenderLayer3);
        map.put(Blocks.GLOW_LICHEN, blockRenderLayer3);
        map.put(Blocks.RESIN_CLUMP, blockRenderLayer3);
        map.put(Blocks.LILY_PAD, blockRenderLayer3);
        map.put(Blocks.NETHER_WART, blockRenderLayer3);
        map.put(Blocks.BREWING_STAND, blockRenderLayer3);
        map.put(Blocks.COCOA, blockRenderLayer3);
        map.put(Blocks.BEACON, blockRenderLayer3);
        map.put(Blocks.FLOWER_POT, blockRenderLayer3);
        map.put(Blocks.POTTED_OAK_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_SPRUCE_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_BIRCH_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_JUNGLE_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_ACACIA_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_CHERRY_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_DARK_OAK_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_PALE_OAK_SAPLING, blockRenderLayer3);
        map.put(Blocks.POTTED_MANGROVE_PROPAGULE, blockRenderLayer3);
        map.put(Blocks.POTTED_FERN, blockRenderLayer3);
        map.put(Blocks.POTTED_DANDELION, blockRenderLayer3);
        map.put(Blocks.POTTED_POPPY, blockRenderLayer3);
        map.put(Blocks.POTTED_OPEN_EYEBLOSSOM, blockRenderLayer3);
        map.put(Blocks.POTTED_CLOSED_EYEBLOSSOM, blockRenderLayer3);
        map.put(Blocks.POTTED_BLUE_ORCHID, blockRenderLayer3);
        map.put(Blocks.POTTED_ALLIUM, blockRenderLayer3);
        map.put(Blocks.POTTED_AZURE_BLUET, blockRenderLayer3);
        map.put(Blocks.POTTED_RED_TULIP, blockRenderLayer3);
        map.put(Blocks.POTTED_ORANGE_TULIP, blockRenderLayer3);
        map.put(Blocks.POTTED_WHITE_TULIP, blockRenderLayer3);
        map.put(Blocks.POTTED_PINK_TULIP, blockRenderLayer3);
        map.put(Blocks.POTTED_OXEYE_DAISY, blockRenderLayer3);
        map.put(Blocks.POTTED_CORNFLOWER, blockRenderLayer3);
        map.put(Blocks.POTTED_LILY_OF_THE_VALLEY, blockRenderLayer3);
        map.put(Blocks.POTTED_WITHER_ROSE, blockRenderLayer3);
        map.put(Blocks.POTTED_RED_MUSHROOM, blockRenderLayer3);
        map.put(Blocks.POTTED_BROWN_MUSHROOM, blockRenderLayer3);
        map.put(Blocks.POTTED_DEAD_BUSH, blockRenderLayer3);
        map.put(Blocks.POTTED_CACTUS, blockRenderLayer3);
        map.put(Blocks.POTTED_AZALEA_BUSH, blockRenderLayer3);
        map.put(Blocks.POTTED_FLOWERING_AZALEA_BUSH, blockRenderLayer3);
        map.put(Blocks.POTTED_TORCHFLOWER, blockRenderLayer3);
        map.put(Blocks.CARROTS, blockRenderLayer3);
        map.put(Blocks.POTATOES, blockRenderLayer3);
        map.put(Blocks.COMPARATOR, blockRenderLayer3);
        map.put(Blocks.ACTIVATOR_RAIL, blockRenderLayer3);
        map.put(Blocks.IRON_TRAPDOOR, blockRenderLayer3);
        map.put(Blocks.SUNFLOWER, blockRenderLayer3);
        map.put(Blocks.LILAC, blockRenderLayer3);
        map.put(Blocks.ROSE_BUSH, blockRenderLayer3);
        map.put(Blocks.PEONY, blockRenderLayer3);
        map.put(Blocks.TALL_GRASS, blockRenderLayer3);
        map.put(Blocks.LARGE_FERN, blockRenderLayer3);
        map.put(Blocks.SPRUCE_DOOR, blockRenderLayer3);
        map.put(Blocks.BIRCH_DOOR, blockRenderLayer3);
        map.put(Blocks.JUNGLE_DOOR, blockRenderLayer3);
        map.put(Blocks.ACACIA_DOOR, blockRenderLayer3);
        map.put(Blocks.CHERRY_DOOR, blockRenderLayer3);
        map.put(Blocks.DARK_OAK_DOOR, blockRenderLayer3);
        map.put(Blocks.PALE_OAK_DOOR, blockRenderLayer3);
        map.put(Blocks.MANGROVE_DOOR, blockRenderLayer3);
        map.put(Blocks.BAMBOO_DOOR, blockRenderLayer3);
        map.put(Blocks.COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.EXPOSED_COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.WEATHERED_COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.OXIDIZED_COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, blockRenderLayer3);
        map.put(Blocks.END_ROD, blockRenderLayer3);
        map.put(Blocks.CHORUS_PLANT, blockRenderLayer3);
        map.put(Blocks.CHORUS_FLOWER, blockRenderLayer3);
        map.put(Blocks.TORCHFLOWER, blockRenderLayer3);
        map.put(Blocks.TORCHFLOWER_CROP, blockRenderLayer3);
        map.put(Blocks.PITCHER_PLANT, blockRenderLayer3);
        map.put(Blocks.PITCHER_CROP, blockRenderLayer3);
        map.put(Blocks.BEETROOTS, blockRenderLayer3);
        map.put(Blocks.KELP, blockRenderLayer3);
        map.put(Blocks.KELP_PLANT, blockRenderLayer3);
        map.put(Blocks.TURTLE_EGG, blockRenderLayer3);
        map.put(Blocks.DEAD_TUBE_CORAL, blockRenderLayer3);
        map.put(Blocks.DEAD_BRAIN_CORAL, blockRenderLayer3);
        map.put(Blocks.DEAD_BUBBLE_CORAL, blockRenderLayer3);
        map.put(Blocks.DEAD_FIRE_CORAL, blockRenderLayer3);
        map.put(Blocks.DEAD_HORN_CORAL, blockRenderLayer3);
        map.put(Blocks.TUBE_CORAL, blockRenderLayer3);
        map.put(Blocks.BRAIN_CORAL, blockRenderLayer3);
        map.put(Blocks.BUBBLE_CORAL, blockRenderLayer3);
        map.put(Blocks.FIRE_CORAL, blockRenderLayer3);
        map.put(Blocks.HORN_CORAL, blockRenderLayer3);
        map.put(Blocks.DEAD_TUBE_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_BRAIN_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_BUBBLE_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_FIRE_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_HORN_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.TUBE_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.BRAIN_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.BUBBLE_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.FIRE_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.HORN_CORAL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.TUBE_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.BRAIN_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.BUBBLE_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.FIRE_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.HORN_CORAL_WALL_FAN, blockRenderLayer3);
        map.put(Blocks.SEA_PICKLE, blockRenderLayer3);
        map.put(Blocks.CONDUIT, blockRenderLayer3);
        map.put(Blocks.BAMBOO_SAPLING, blockRenderLayer3);
        map.put(Blocks.BAMBOO, blockRenderLayer3);
        map.put(Blocks.POTTED_BAMBOO, blockRenderLayer3);
        map.put(Blocks.SCAFFOLDING, blockRenderLayer3);
        map.put(Blocks.STONECUTTER, blockRenderLayer3);
        map.put(Blocks.LANTERN, blockRenderLayer3);
        map.put(Blocks.SOUL_LANTERN, blockRenderLayer3);
        map.put(Blocks.CAMPFIRE, blockRenderLayer3);
        map.put(Blocks.SOUL_CAMPFIRE, blockRenderLayer3);
        map.put(Blocks.SWEET_BERRY_BUSH, blockRenderLayer3);
        map.put(Blocks.WEEPING_VINES, blockRenderLayer3);
        map.put(Blocks.WEEPING_VINES_PLANT, blockRenderLayer3);
        map.put(Blocks.TWISTING_VINES, blockRenderLayer3);
        map.put(Blocks.TWISTING_VINES_PLANT, blockRenderLayer3);
        map.put(Blocks.NETHER_SPROUTS, blockRenderLayer3);
        map.put(Blocks.CRIMSON_FUNGUS, blockRenderLayer3);
        map.put(Blocks.WARPED_FUNGUS, blockRenderLayer3);
        map.put(Blocks.CRIMSON_ROOTS, blockRenderLayer3);
        map.put(Blocks.WARPED_ROOTS, blockRenderLayer3);
        map.put(Blocks.POTTED_CRIMSON_FUNGUS, blockRenderLayer3);
        map.put(Blocks.POTTED_WARPED_FUNGUS, blockRenderLayer3);
        map.put(Blocks.POTTED_CRIMSON_ROOTS, blockRenderLayer3);
        map.put(Blocks.POTTED_WARPED_ROOTS, blockRenderLayer3);
        map.put(Blocks.CRIMSON_DOOR, blockRenderLayer3);
        map.put(Blocks.WARPED_DOOR, blockRenderLayer3);
        map.put(Blocks.POINTED_DRIPSTONE, blockRenderLayer3);
        map.put(Blocks.SMALL_AMETHYST_BUD, blockRenderLayer3);
        map.put(Blocks.MEDIUM_AMETHYST_BUD, blockRenderLayer3);
        map.put(Blocks.LARGE_AMETHYST_BUD, blockRenderLayer3);
        map.put(Blocks.AMETHYST_CLUSTER, blockRenderLayer3);
        map.put(Blocks.LIGHTNING_ROD, blockRenderLayer3);
        map.put(Blocks.CAVE_VINES, blockRenderLayer3);
        map.put(Blocks.CAVE_VINES_PLANT, blockRenderLayer3);
        map.put(Blocks.SPORE_BLOSSOM, blockRenderLayer3);
        map.put(Blocks.FLOWERING_AZALEA, blockRenderLayer3);
        map.put(Blocks.AZALEA, blockRenderLayer3);
        map.put(Blocks.PINK_PETALS, blockRenderLayer3);
        map.put(Blocks.WILDFLOWERS, blockRenderLayer3);
        map.put(Blocks.LEAF_LITTER, blockRenderLayer3);
        map.put(Blocks.BIG_DRIPLEAF, blockRenderLayer3);
        map.put(Blocks.BIG_DRIPLEAF_STEM, blockRenderLayer3);
        map.put(Blocks.SMALL_DRIPLEAF, blockRenderLayer3);
        map.put(Blocks.HANGING_ROOTS, blockRenderLayer3);
        map.put(Blocks.SCULK_SENSOR, blockRenderLayer3);
        map.put(Blocks.CALIBRATED_SCULK_SENSOR, blockRenderLayer3);
        map.put(Blocks.SCULK_VEIN, blockRenderLayer3);
        map.put(Blocks.SCULK_SHRIEKER, blockRenderLayer3);
        map.put(Blocks.MANGROVE_PROPAGULE, blockRenderLayer3);
        map.put(Blocks.FROGSPAWN, blockRenderLayer3);
        map.put(Blocks.COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.EXPOSED_COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.WEATHERED_COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.OXIDIZED_COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.WAXED_COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, blockRenderLayer3);
        map.put(Blocks.FIREFLY_BUSH, blockRenderLayer3);
        map.put(Blocks.CACTUS_FLOWER, blockRenderLayer3);
        BlockRenderLayer blockRenderLayer4 = BlockRenderLayer.TRANSLUCENT;
        map.put(Blocks.ICE, blockRenderLayer4);
        map.put(Blocks.NETHER_PORTAL, blockRenderLayer4);
        map.put(Blocks.WHITE_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.ORANGE_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.MAGENTA_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.LIGHT_BLUE_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.YELLOW_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.LIME_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.PINK_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.GRAY_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.LIGHT_GRAY_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.CYAN_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.PURPLE_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.BLUE_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.BROWN_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.GREEN_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.RED_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.BLACK_STAINED_GLASS, blockRenderLayer4);
        map.put(Blocks.WHITE_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.ORANGE_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.MAGENTA_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.YELLOW_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.LIME_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.PINK_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.GRAY_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.CYAN_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.PURPLE_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.BLUE_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.BROWN_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.GREEN_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.RED_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.BLACK_STAINED_GLASS_PANE, blockRenderLayer4);
        map.put(Blocks.SLIME_BLOCK, blockRenderLayer4);
        map.put(Blocks.HONEY_BLOCK, blockRenderLayer4);
        map.put(Blocks.FROSTED_ICE, blockRenderLayer4);
        map.put(Blocks.BUBBLE_COLUMN, blockRenderLayer4);
        map.put(Blocks.TINTED_GLASS, blockRenderLayer4);
    });
    final static private Map<Fluid, BlockRenderLayer> FLUIDS = Util.make(Maps.newHashMap(), map -> {
        map.put(Fluids.FLOWING_WATER, BlockRenderLayer.TRANSLUCENT);
        map.put(Fluids.WATER, BlockRenderLayer.TRANSLUCENT);
    });
    static private boolean fancyGraphicsOrBetter;

    public static BlockRenderLayer getBlockLayer(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return fancyGraphicsOrBetter ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
        }
        BlockRenderLayer blockRenderLayer = BLOCKS.get(block);
        if (blockRenderLayer != null) {
            return blockRenderLayer;
        }
        return BlockRenderLayer.SOLID;
    }

    public static RenderLayer getMovingBlockLayer(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
        }
        BlockRenderLayer blockRenderLayer = BLOCKS.get(block);
        if (blockRenderLayer != null) {
            return switch (blockRenderLayer) {
                default -> throw new MatchException(null, null);
                case BlockRenderLayer.SOLID -> RenderLayer.getSolid();
                case BlockRenderLayer.CUTOUT_MIPPED -> RenderLayer.getCutoutMipped();
                case BlockRenderLayer.CUTOUT -> RenderLayer.getCutout();
                case BlockRenderLayer.TRANSLUCENT -> RenderLayer.getTranslucentMovingBlock();
                case BlockRenderLayer.TRIPWIRE -> RenderLayer.getTripwire();
            };
        }
        return RenderLayer.getSolid();
    }

    public static RenderLayer getEntityBlockLayer(BlockState state) {
        BlockRenderLayer blockRenderLayer = RenderLayers.getBlockLayer(state);
        if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
            return TexturedRenderLayers.getItemEntityTranslucentCull();
        }
        return TexturedRenderLayers.getEntityCutout();
    }

    public static RenderLayer getItemLayer(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            Block block = blockItem.getBlock();
            return RenderLayers.getEntityBlockLayer(block.getDefaultState());
        }
        return TexturedRenderLayers.getItemEntityTranslucentCull();
    }

    public static BlockRenderLayer getFluidLayer(FluidState state) {
        BlockRenderLayer blockRenderLayer = FLUIDS.get(state.getFluid());
        if (blockRenderLayer != null) {
            return blockRenderLayer;
        }
        return BlockRenderLayer.SOLID;
    }

    public static void setFancyGraphicsOrBetter(boolean fancyGraphicsOrBetter) {
        RenderLayers.fancyGraphicsOrBetter = fancyGraphicsOrBetter;
    }
}

