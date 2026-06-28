/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public class DimensionTypes {
    final static public RegistryKey<DimensionType> OVERWORLD = DimensionTypes.of("overworld");
    final static public RegistryKey<DimensionType> THE_NETHER = DimensionTypes.of("the_nether");
    final static public RegistryKey<DimensionType> THE_END = DimensionTypes.of("the_end");
    final static public RegistryKey<DimensionType> OVERWORLD_CAVES = DimensionTypes.of("overworld_caves");
    final static public Identifier OVERWORLD_ID = Identifier.ofVanilla("overworld");
    final static public Identifier THE_NETHER_ID = Identifier.ofVanilla("the_nether");
    final static public Identifier THE_END_ID = Identifier.ofVanilla("the_end");

    private static RegistryKey<DimensionType> of(String id) {
        return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, Identifier.ofVanilla(id));
    }
}

