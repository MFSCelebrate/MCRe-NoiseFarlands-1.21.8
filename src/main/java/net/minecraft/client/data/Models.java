/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.Optional;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class Models {
    final static public Model CUBE = Models.block("cube", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN);
    final static public Model CUBE_DIRECTIONAL = Models.block("cube_directional", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN);
    final static public Model CUBE_ALL = Models.block("cube_all", TextureKey.ALL);
    final static public Model CUBE_ALL_INNER_FACES = Models.block("cube_all_inner_faces", TextureKey.ALL);
    final static public Model CUBE_MIRRORED_ALL = Models.block("cube_mirrored_all", "_mirrored", TextureKey.ALL);
    final static public Model CUBE_NORTH_WEST_MIRRORED_ALL = Models.block("cube_north_west_mirrored_all", "_north_west_mirrored", TextureKey.ALL);
    final static public Model CUBE_COLUMN_UV_LOCKED_X = Models.block("cube_column_uv_locked_x", "_x", TextureKey.END, TextureKey.SIDE);
    final static public Model CUBE_COLUMN_UV_LOCKED_Y = Models.block("cube_column_uv_locked_y", "_y", TextureKey.END, TextureKey.SIDE);
    final static public Model CUBE_COLUMN_UV_LOCKED_Z = Models.block("cube_column_uv_locked_z", "_z", TextureKey.END, TextureKey.SIDE);
    final static public Model CUBE_COLUMN = Models.block("cube_column", TextureKey.END, TextureKey.SIDE);
    final static public Model CUBE_COLUMN_HORIZONTAL = Models.block("cube_column_horizontal", "_horizontal", TextureKey.END, TextureKey.SIDE);
    final static public Model CUBE_COLUMN_MIRRORED = Models.block("cube_column_mirrored", "_mirrored", TextureKey.END, TextureKey.SIDE);
    final static public Model CUBE_TOP = Models.block("cube_top", TextureKey.TOP, TextureKey.SIDE);
    final static public Model CUBE_BOTTOM_TOP = Models.block("cube_bottom_top", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    final static public Model CUBE_BOTTOM_TOP_INNER_FACES = Models.block("cube_bottom_top_inner_faces", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    final static public Model ORIENTABLE = Models.block("orientable", TextureKey.TOP, TextureKey.FRONT, TextureKey.SIDE);
    final static public Model ORIENTABLE_WITH_BOTTOM = Models.block("orientable_with_bottom", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.FRONT);
    final static public Model ORIENTABLE_VERTICAL = Models.block("orientable_vertical", "_vertical", TextureKey.FRONT, TextureKey.SIDE);
    final static public Model BUTTON = Models.block("button", TextureKey.TEXTURE);
    final static public Model BUTTON_PRESSED = Models.block("button_pressed", "_pressed", TextureKey.TEXTURE);
    final static public Model BUTTON_INVENTORY = Models.block("button_inventory", "_inventory", TextureKey.TEXTURE);
    final static public Model DOOR_BOTTOM_LEFT = Models.block("door_bottom_left", "_bottom_left", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model DOOR_BOTTOM_LEFT_OPEN = Models.block("door_bottom_left_open", "_bottom_left_open", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model DOOR_BOTTOM_RIGHT = Models.block("door_bottom_right", "_bottom_right", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model DOOR_BOTTOM_RIGHT_OPEN = Models.block("door_bottom_right_open", "_bottom_right_open", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model DOOR_TOP_LEFT = Models.block("door_top_left", "_top_left", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model DOOR_TOP_LEFT_OPEN = Models.block("door_top_left_open", "_top_left_open", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model DOOR_TOP_RIGHT = Models.block("door_top_right", "_top_right", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model DOOR_TOP_RIGHT_OPEN = Models.block("door_top_right_open", "_top_right_open", TextureKey.TOP, TextureKey.BOTTOM);
    final static public Model CUSTOM_FENCE_POST = Models.block("custom_fence_post", "_post", TextureKey.TEXTURE, TextureKey.PARTICLE);
    final static public Model CUSTOM_FENCE_SIDE_NORTH = Models.block("custom_fence_side_north", "_side_north", TextureKey.TEXTURE);
    final static public Model CUSTOM_FENCE_SIDE_EAST = Models.block("custom_fence_side_east", "_side_east", TextureKey.TEXTURE);
    final static public Model CUSTOM_FENCE_SIDE_SOUTH = Models.block("custom_fence_side_south", "_side_south", TextureKey.TEXTURE);
    final static public Model CUSTOM_FENCE_SIDE_WEST = Models.block("custom_fence_side_west", "_side_west", TextureKey.TEXTURE);
    final static public Model CUSTOM_FENCE_INVENTORY = Models.block("custom_fence_inventory", "_inventory", TextureKey.TEXTURE);
    final static public Model FENCE_POST = Models.block("fence_post", "_post", TextureKey.TEXTURE);
    final static public Model FENCE_SIDE = Models.block("fence_side", "_side", TextureKey.TEXTURE);
    final static public Model FENCE_INVENTORY = Models.block("fence_inventory", "_inventory", TextureKey.TEXTURE);
    final static public Model TEMPLATE_WALL_POST = Models.block("template_wall_post", "_post", TextureKey.WALL);
    final static public Model TEMPLATE_WALL_SIDE = Models.block("template_wall_side", "_side", TextureKey.WALL);
    final static public Model TEMPLATE_WALL_SIDE_TALL = Models.block("template_wall_side_tall", "_side_tall", TextureKey.WALL);
    final static public Model WALL_INVENTORY = Models.block("wall_inventory", "_inventory", TextureKey.WALL);
    final static public Model TEMPLATE_CUSTOM_FENCE_GATE = Models.block("template_custom_fence_gate", TextureKey.TEXTURE, TextureKey.PARTICLE);
    final static public Model TEMPLATE_CUSTOM_FENCE_GATE_OPEN = Models.block("template_custom_fence_gate_open", "_open", TextureKey.TEXTURE, TextureKey.PARTICLE);
    final static public Model TEMPLATE_CUSTOM_FENCE_GATE_WALL = Models.block("template_custom_fence_gate_wall", "_wall", TextureKey.TEXTURE, TextureKey.PARTICLE);
    final static public Model TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN = Models.block("template_custom_fence_gate_wall_open", "_wall_open", TextureKey.TEXTURE, TextureKey.PARTICLE);
    final static public Model TEMPLATE_FENCE_GATE = Models.block("template_fence_gate", TextureKey.TEXTURE);
    final static public Model TEMPLATE_FENCE_GATE_OPEN = Models.block("template_fence_gate_open", "_open", TextureKey.TEXTURE);
    final static public Model TEMPLATE_FENCE_GATE_WALL = Models.block("template_fence_gate_wall", "_wall", TextureKey.TEXTURE);
    final static public Model TEMPLATE_FENCE_GATE_WALL_OPEN = Models.block("template_fence_gate_wall_open", "_wall_open", TextureKey.TEXTURE);
    final static public Model PRESSURE_PLATE_UP = Models.block("pressure_plate_up", TextureKey.TEXTURE);
    final static public Model PRESSURE_PLATE_DOWN = Models.block("pressure_plate_down", "_down", TextureKey.TEXTURE);
    final static public Model PARTICLE = Models.make(TextureKey.PARTICLE);
    final static public Model SLAB = Models.block("slab", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    final static public Model SLAB_TOP = Models.block("slab_top", "_top", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    final static public Model LEAVES = Models.block("leaves", TextureKey.ALL);
    final static public Model STAIRS = Models.block("stairs", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    final static public Model INNER_STAIRS = Models.block("inner_stairs", "_inner", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    final static public Model OUTER_STAIRS = Models.block("outer_stairs", "_outer", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    final static public Model TEMPLATE_TRAPDOOR_TOP = Models.block("template_trapdoor_top", "_top", TextureKey.TEXTURE);
    final static public Model TEMPLATE_TRAPDOOR_BOTTOM = Models.block("template_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
    final static public Model TEMPLATE_TRAPDOOR_OPEN = Models.block("template_trapdoor_open", "_open", TextureKey.TEXTURE);
    final static public Model TEMPLATE_ORIENTABLE_TRAPDOOR_TOP = Models.block("template_orientable_trapdoor_top", "_top", TextureKey.TEXTURE);
    final static public Model TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM = Models.block("template_orientable_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
    final static public Model TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN = Models.block("template_orientable_trapdoor_open", "_open", TextureKey.TEXTURE);
    final static public Model POINTED_DRIPSTONE = Models.block("pointed_dripstone", TextureKey.CROSS);
    final static public Model CROSS = Models.block("cross", TextureKey.CROSS);
    final static public Model TINTED_CROSS = Models.block("tinted_cross", TextureKey.CROSS);
    final static public Model CROSS_EMISSIVE = Models.block("cross_emissive", TextureKey.CROSS, TextureKey.CROSS_EMISSIVE);
    final static public Model FLOWER_POT_CROSS = Models.block("flower_pot_cross", TextureKey.PLANT);
    final static public Model TINTED_FLOWER_POT_CROSS = Models.block("tinted_flower_pot_cross", TextureKey.PLANT);
    final static public Model FLOWER_POT_CROSS_EMISSIVE = Models.block("flower_pot_cross_emissive", TextureKey.PLANT, TextureKey.CROSS_EMISSIVE);
    final static public Model RAIL_FLAT = Models.block("rail_flat", TextureKey.RAIL);
    final static public Model RAIL_CURVED = Models.block("rail_curved", "_corner", TextureKey.RAIL);
    final static public Model TEMPLATE_RAIL_RAISED_NE = Models.block("template_rail_raised_ne", "_raised_ne", TextureKey.RAIL);
    final static public Model TEMPLATE_RAIL_RAISED_SW = Models.block("template_rail_raised_sw", "_raised_sw", TextureKey.RAIL);
    final static public Model CARPET = Models.block("carpet", TextureKey.WOOL);
    final static public Model MOSSY_CARPET_SIDE = Models.block("mossy_carpet_side", TextureKey.SIDE);
    final static public Model FLOWERBED_1 = Models.block("flowerbed_1", "_1", TextureKey.FLOWERBED, TextureKey.STEM);
    final static public Model FLOWERBED_2 = Models.block("flowerbed_2", "_2", TextureKey.FLOWERBED, TextureKey.STEM);
    final static public Model FLOWERBED_3 = Models.block("flowerbed_3", "_3", TextureKey.FLOWERBED, TextureKey.STEM);
    final static public Model FLOWERBED_4 = Models.block("flowerbed_4", "_4", TextureKey.FLOWERBED, TextureKey.STEM);
    final static public Model TEMPLATE_LEAF_LITTER_1 = Models.block("template_leaf_litter_1", "_1", TextureKey.TEXTURE);
    final static public Model TEMPLATE_LEAF_LITTER_2 = Models.block("template_leaf_litter_2", "_2", TextureKey.TEXTURE);
    final static public Model TEMPLATE_LEAF_LITTER_3 = Models.block("template_leaf_litter_3", "_3", TextureKey.TEXTURE);
    final static public Model TEMPLATE_LEAF_LITTER_4 = Models.block("template_leaf_litter_4", "_4", TextureKey.TEXTURE);
    final static public Model CORAL_FAN = Models.block("coral_fan", TextureKey.FAN);
    final static public Model CORAL_WALL_FAN = Models.block("coral_wall_fan", TextureKey.FAN);
    final static public Model TEMPLATE_GLAZED_TERRACOTTA = Models.block("template_glazed_terracotta", TextureKey.PATTERN);
    final static public Model TEMPLATE_CHORUS_FLOWER = Models.block("template_chorus_flower", TextureKey.TEXTURE);
    final static public Model TEMPLATE_DAYLIGHT_DETECTOR = Models.block("template_daylight_detector", TextureKey.TOP, TextureKey.SIDE);
    final static public Model TEMPLATE_GLASS_PANE_NOSIDE = Models.block("template_glass_pane_noside", "_noside", TextureKey.PANE);
    final static public Model TEMPLATE_GLASS_PANE_NOSIDE_ALT = Models.block("template_glass_pane_noside_alt", "_noside_alt", TextureKey.PANE);
    final static public Model TEMPLATE_GLASS_PANE_POST = Models.block("template_glass_pane_post", "_post", TextureKey.PANE, TextureKey.EDGE);
    final static public Model TEMPLATE_GLASS_PANE_SIDE = Models.block("template_glass_pane_side", "_side", TextureKey.PANE, TextureKey.EDGE);
    final static public Model TEMPLATE_GLASS_PANE_SIDE_ALT = Models.block("template_glass_pane_side_alt", "_side_alt", TextureKey.PANE, TextureKey.EDGE);
    final static public Model TEMPLATE_COMMAND_BLOCK = Models.block("template_command_block", TextureKey.FRONT, TextureKey.BACK, TextureKey.SIDE);
    final static public Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_LEFT = Models.block("template_chiseled_bookshelf_slot_top_left", "_slot_top_left", TextureKey.TEXTURE);
    final static public Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_MID = Models.block("template_chiseled_bookshelf_slot_top_mid", "_slot_top_mid", TextureKey.TEXTURE);
    final static public Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_RIGHT = Models.block("template_chiseled_bookshelf_slot_top_right", "_slot_top_right", TextureKey.TEXTURE);
    final static public Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT = Models.block("template_chiseled_bookshelf_slot_bottom_left", "_slot_bottom_left", TextureKey.TEXTURE);
    final static public Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_MID = Models.block("template_chiseled_bookshelf_slot_bottom_mid", "_slot_bottom_mid", TextureKey.TEXTURE);
    final static public Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT = Models.block("template_chiseled_bookshelf_slot_bottom_right", "_slot_bottom_right", TextureKey.TEXTURE);
    final static public Model TEMPLATE_ANVIL = Models.block("template_anvil", TextureKey.TOP);
    final static public Model[] STEM_GROWTH_STAGES = (Model[])IntStream.range(0, 8).mapToObj(stage -> Models.block("stem_growth" + stage, "_stage" + stage, TextureKey.STEM)).toArray(Model[]::new);
    final static public Model STEM_FRUIT = Models.block("stem_fruit", TextureKey.STEM, TextureKey.UPPERSTEM);
    final static public Model CROP = Models.block("crop", TextureKey.CROP);
    final static public Model TEMPLATE_FARMLAND = Models.block("template_farmland", TextureKey.DIRT, TextureKey.TOP);
    final static public Model TEMPLATE_FIRE_FLOOR = Models.block("template_fire_floor", TextureKey.FIRE);
    final static public Model TEMPLATE_FIRE_SIDE = Models.block("template_fire_side", TextureKey.FIRE);
    final static public Model TEMPLATE_FIRE_SIDE_ALT = Models.block("template_fire_side_alt", TextureKey.FIRE);
    final static public Model TEMPLATE_FIRE_UP = Models.block("template_fire_up", TextureKey.FIRE);
    final static public Model TEMPLATE_FIRE_UP_ALT = Models.block("template_fire_up_alt", TextureKey.FIRE);
    final static public Model TEMPLATE_CAMPFIRE = Models.block("template_campfire", TextureKey.FIRE, TextureKey.LIT_LOG);
    final static public Model TEMPLATE_LANTERN = Models.block("template_lantern", TextureKey.LANTERN);
    final static public Model TEMPLATE_HANGING_LANTERN = Models.block("template_hanging_lantern", "_hanging", TextureKey.LANTERN);
    final static public Model TEMPLATE_TORCH = Models.block("template_torch", TextureKey.TORCH);
    final static public Model TEMPLATE_TORCH_UNLIT = Models.block("template_torch_unlit", TextureKey.TORCH);
    final static public Model TEMPLATE_TORCH_WALL = Models.block("template_torch_wall", TextureKey.TORCH);
    final static public Model TEMPLATE_TORCH_WALL_UNLIT = Models.block("template_torch_wall_unlit", TextureKey.TORCH);
    final static public Model TEMPLATE_REDSTONE_TORCH = Models.block("template_redstone_torch", TextureKey.TORCH);
    final static public Model TEMPLATE_REDSTONE_TORCH_WALL = Models.block("template_redstone_torch_wall", TextureKey.TORCH);
    final static public Model TEMPLATE_PISTON = Models.block("template_piston", TextureKey.PLATFORM, TextureKey.BOTTOM, TextureKey.SIDE);
    final static public Model TEMPLATE_PISTON_HEAD = Models.block("template_piston_head", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
    final static public Model TEMPLATE_PISTON_HEAD_SHORT = Models.block("template_piston_head_short", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
    final static public Model TEMPLATE_SEAGRASS = Models.block("template_seagrass", TextureKey.TEXTURE);
    final static public Model TEMPLATE_TURTLE_EGG = Models.block("template_turtle_egg", TextureKey.ALL);
    final static public Model DRIED_GHAST = Models.block("dried_ghast", TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.TENTACLES);
    final static public Model TEMPLATE_TWO_TURTLE_EGGS = Models.block("template_two_turtle_eggs", TextureKey.ALL);
    final static public Model TEMPLATE_THREE_TURTLE_EGGS = Models.block("template_three_turtle_eggs", TextureKey.ALL);
    final static public Model TEMPLATE_FOUR_TURTLE_EGGS = Models.block("template_four_turtle_eggs", TextureKey.ALL);
    final static public Model TEMPLATE_SINGLE_FACE = Models.block("template_single_face", TextureKey.TEXTURE);
    final static public Model TEMPLATE_CAULDRON_LEVEL1 = Models.block("template_cauldron_level1", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    final static public Model TEMPLATE_CAULDRON_LEVEL2 = Models.block("template_cauldron_level2", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    final static public Model TEMPLATE_CAULDRON_FULL = Models.block("template_cauldron_full", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    final static public Model TEMPLATE_AZALEA = Models.block("template_azalea", TextureKey.TOP, TextureKey.SIDE);
    final static public Model TEMPLATE_POTTED_AZALEA_BUSH = Models.block("template_potted_azalea_bush", TextureKey.PLANT, TextureKey.TOP, TextureKey.SIDE);
    final static public Model TEMPLATE_POTTED_FLOWERING_AZALEA_BUSH = Models.block("template_potted_azalea_bush", TextureKey.PLANT, TextureKey.TOP, TextureKey.SIDE);
    final static public Model SNIFFER_EGG = Models.block("sniffer_egg", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST);
    final static public Model GENERATED = Models.item("generated", TextureKey.LAYER0);
    final static public Model TEMPLATE_MUSIC_DISC = Models.item("template_music_disc", TextureKey.LAYER0);
    final static public Model HANDHELD = Models.item("handheld", TextureKey.LAYER0);
    final static public Model HANDHELD_ROD = Models.item("handheld_rod", TextureKey.LAYER0);
    final static public Model GENERATED_TWO_LAYERS = Models.item("generated", TextureKey.LAYER0, TextureKey.LAYER1);
    final static public Model GENERATED_THREE_LAYERS = Models.item("generated", TextureKey.LAYER0, TextureKey.LAYER1, TextureKey.LAYER2);
    final static public Model TEMPLATE_SHULKER_BOX = Models.item("template_shulker_box", TextureKey.PARTICLE);
    final static public Model TEMPLATE_BED = Models.item("template_bed", TextureKey.PARTICLE);
    final static public Model TEMPLATE_CHEST = Models.item("template_chest", TextureKey.PARTICLE);
    final static public Model TEMPLATE_BUNDLE_OPEN_FRONT = Models.openBundle("template_bundle_open_front", "_open_front", TextureKey.LAYER0);
    final static public Model TEMPLATE_BUNDLE_OPEN_BACK = Models.openBundle("template_bundle_open_back", "_open_back", TextureKey.LAYER0);
    final static public Model BOW = Models.item("bow", TextureKey.LAYER0);
    final static public Model CROSSBOW = Models.item("crossbow", TextureKey.LAYER0);
    final static public Model TEMPLATE_CANDLE = Models.block("template_candle", TextureKey.ALL, TextureKey.PARTICLE);
    final static public Model TEMPLATE_TWO_CANDLES = Models.block("template_two_candles", TextureKey.ALL, TextureKey.PARTICLE);
    final static public Model TEMPLATE_THREE_CANDLES = Models.block("template_three_candles", TextureKey.ALL, TextureKey.PARTICLE);
    final static public Model TEMPLATE_FOUR_CANDLES = Models.block("template_four_candles", TextureKey.ALL, TextureKey.PARTICLE);
    final static public Model TEMPLATE_CAKE_WITH_CANDLE = Models.block("template_cake_with_candle", TextureKey.CANDLE, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE);
    final static public Model TEMPLATE_SCULK_SHRIEKER = Models.block("template_sculk_shrieker", TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE, TextureKey.INNER_TOP);
    final static public Model TEMPLATE_VAULT = Models.block("template_vault", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.FRONT);
    final static public Model HANDHELD_MACE = Models.item("handheld_mace", TextureKey.LAYER0);

    private static Model make(TextureKey ... requiredTextureKeys) {
        return new Model(Optional.empty(), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla("block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model item(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model openBundle(String parent, String variant, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla("item/" + parent)), Optional.of(variant), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla("block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }
}

