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
import net.minecraft.block.BlockSetType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record WoodType(String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
    final static private Map<String, WoodType> VALUES = new Object2ObjectArrayMap();
    final static public Codec<WoodType> CODEC = Codec.stringResolver(WoodType::name, VALUES::get);
    final static public WoodType OAK = WoodType.register(new WoodType("oak", BlockSetType.OAK));
    final static public WoodType SPRUCE = WoodType.register(new WoodType("spruce", BlockSetType.SPRUCE));
    final static public WoodType BIRCH = WoodType.register(new WoodType("birch", BlockSetType.BIRCH));
    final static public WoodType ACACIA = WoodType.register(new WoodType("acacia", BlockSetType.ACACIA));
    final static public WoodType CHERRY = WoodType.register(new WoodType("cherry", BlockSetType.CHERRY, BlockSoundGroup.CHERRY_WOOD, BlockSoundGroup.CHERRY_WOOD_HANGING_SIGN, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN));
    final static public WoodType JUNGLE = WoodType.register(new WoodType("jungle", BlockSetType.JUNGLE));
    final static public WoodType DARK_OAK = WoodType.register(new WoodType("dark_oak", BlockSetType.DARK_OAK));
    final static public WoodType PALE_OAK = WoodType.register(new WoodType("pale_oak", BlockSetType.PALE_OAK));
    final static public WoodType CRIMSON = WoodType.register(new WoodType("crimson", BlockSetType.CRIMSON, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
    final static public WoodType WARPED = WoodType.register(new WoodType("warped", BlockSetType.WARPED, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
    final static public WoodType MANGROVE = WoodType.register(new WoodType("mangrove", BlockSetType.MANGROVE));
    final static public WoodType BAMBOO = WoodType.register(new WoodType("bamboo", BlockSetType.BAMBOO, BlockSoundGroup.BAMBOO_WOOD, BlockSoundGroup.BAMBOO_WOOD_HANGING_SIGN, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN));

    public WoodType(String name, BlockSetType setType) {
        this(name, setType, BlockSoundGroup.WOOD, BlockSoundGroup.HANGING_SIGN, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN);
    }

    private static WoodType register(WoodType type) {
        VALUES.put(type.name(), type);
        return type;
    }

    public static Stream<WoodType> stream() {
        return VALUES.values().stream();
    }
}

