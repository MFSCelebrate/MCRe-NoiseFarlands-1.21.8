/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.root;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record MangroveRootPlacement(RegistryEntryList<Block> canGrowThrough, RegistryEntryList<Block> muddyRootsIn, BlockStateProvider muddyRootsProvider, int maxRootWidth, int maxRootLength, float randomSkewChance) {
    final static public Codec<MangroveRootPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("can_grow_through").forGetter(rootPlacement -> rootPlacement.canGrowThrough), (App)RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("muddy_roots_in").forGetter(rootPlacement -> rootPlacement.muddyRootsIn), (App)BlockStateProvider.TYPE_CODEC.fieldOf("muddy_roots_provider").forGetter(rootPlacement -> rootPlacement.muddyRootsProvider), (App)Codec.intRange(1, 12).fieldOf("max_root_width").forGetter(rootPlacement -> rootPlacement.maxRootWidth), (App)Codec.intRange(1, 64).fieldOf("max_root_length").forGetter(rootPlacement -> rootPlacement.maxRootLength), (App)Codec.floatRange(0.0f, 1.0f).fieldOf("random_skew_chance").forGetter(rootPlacement -> Float.valueOf(rootPlacement.randomSkewChance))).apply((Applicative)instance, MangroveRootPlacement::new));

    public MangroveRootPlacement(RegistryEntryList<Block> registryEntryList, RegistryEntryList<Block> registryEntryList2, BlockStateProvider blockStateProvider, int i, int j, float f) {
        this.canGrowThrough = registryEntryList;
        this.muddyRootsIn = registryEntryList2;
        this.muddyRootsProvider = blockStateProvider;
        this.maxRootWidth = 1;
        this.maxRootLength = j;
        this.randomSkewChance = f;
    }
}

