/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package net.minecraft.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record BlockSetType(String name, boolean canOpenByHand, boolean canOpenByWindCharge, boolean canButtonBeActivatedByArrows, ActivationRule pressurePlateSensitivity, BlockSoundGroup soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
    final static private Map<String, BlockSetType> VALUES = new Object2ObjectArrayMap();
    final static public Codec<BlockSetType> CODEC = Codec.stringResolver(BlockSetType::name, VALUES::get);
    final static public BlockSetType IRON = BlockSetType.register(new BlockSetType("iron", false, false, false, ActivationRule.EVERYTHING, BlockSoundGroup.IRON, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    final static public BlockSetType COPPER = BlockSetType.register(new BlockSetType("copper", true, true, false, ActivationRule.EVERYTHING, BlockSoundGroup.COPPER, SoundEvents.BLOCK_COPPER_DOOR_CLOSE, SoundEvents.BLOCK_COPPER_DOOR_OPEN, SoundEvents.BLOCK_COPPER_TRAPDOOR_CLOSE, SoundEvents.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    final static public BlockSetType GOLD = BlockSetType.register(new BlockSetType("gold", false, true, false, ActivationRule.EVERYTHING, BlockSoundGroup.METAL, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    final static public BlockSetType STONE = BlockSetType.register(new BlockSetType("stone", true, true, false, ActivationRule.MOBS, BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    final static public BlockSetType POLISHED_BLACKSTONE = BlockSetType.register(new BlockSetType("polished_blackstone", true, true, false, ActivationRule.MOBS, BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    final static public BlockSetType OAK = BlockSetType.register(new BlockSetType("oak"));
    final static public BlockSetType SPRUCE = BlockSetType.register(new BlockSetType("spruce"));
    final static public BlockSetType BIRCH = BlockSetType.register(new BlockSetType("birch"));
    final static public BlockSetType ACACIA = BlockSetType.register(new BlockSetType("acacia"));
    final static public BlockSetType CHERRY = BlockSetType.register(new BlockSetType("cherry", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.CHERRY_WOOD, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON));
    final static public BlockSetType JUNGLE = BlockSetType.register(new BlockSetType("jungle"));
    final static public BlockSetType DARK_OAK = BlockSetType.register(new BlockSetType("dark_oak"));
    final static public BlockSetType PALE_OAK = BlockSetType.register(new BlockSetType("pale_oak"));
    final static public BlockSetType CRIMSON = BlockSetType.register(new BlockSetType("crimson", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
    final static public BlockSetType WARPED = BlockSetType.register(new BlockSetType("warped", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
    final static public BlockSetType MANGROVE = BlockSetType.register(new BlockSetType("mangrove"));
    final static public BlockSetType BAMBOO = BlockSetType.register(new BlockSetType("bamboo", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.BAMBOO_WOOD, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON));

    public BlockSetType(String name) {
        this(name, true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.WOOD, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);
    }

    private static BlockSetType register(BlockSetType blockSetType) {
        VALUES.put(blockSetType.name, blockSetType);
        return blockSetType;
    }

    public static Stream<BlockSetType> stream() {
        return VALUES.values().stream();
    }

    public static final class ActivationRule
    extends Enum<ActivationRule> {
        final static public ActivationRule EVERYTHING = new ActivationRule();
        final static public ActivationRule MOBS = new ActivationRule();
        final static private ActivationRule[] field_11363;

        public static ActivationRule[] values() {
            return (ActivationRule[])field_11363.clone();
        }

        public static ActivationRule valueOf(String string) {
            return Enum.valueOf(ActivationRule.class, string);
        }

        private static ActivationRule[] method_36707() {
            return new ActivationRule[]{EVERYTHING, MOBS};
        }

        static {
            field_11363 = ActivationRule.method_36707();
        }
    }
}

