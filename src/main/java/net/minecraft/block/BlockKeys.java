/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BlockKeys {
    final static public RegistryKey<Block> PUMPKIN = BlockKeys.of("pumpkin");
    final static public RegistryKey<Block> PUMPKIN_STEM = BlockKeys.of("pumpkin_stem");
    final static public RegistryKey<Block> ATTACHED_PUMPKIN_STEM = BlockKeys.of("attached_pumpkin_stem");
    final static public RegistryKey<Block> MELON = BlockKeys.of("melon");
    final static public RegistryKey<Block> MELON_STEM = BlockKeys.of("melon_stem");
    final static public RegistryKey<Block> ATTACHED_MELON_STEM = BlockKeys.of("attached_melon_stem");

    private static RegistryKey<Block> of(String id) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla(id));
    }
}

