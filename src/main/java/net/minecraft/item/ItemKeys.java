/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ItemKeys {
    final static public RegistryKey<Item> PUMPKIN_SEEDS = ItemKeys.of("pumpkin_seeds");
    final static public RegistryKey<Item> MELON_SEEDS = ItemKeys.of("melon_seeds");

    private static RegistryKey<Item> of(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla(id));
    }
}

