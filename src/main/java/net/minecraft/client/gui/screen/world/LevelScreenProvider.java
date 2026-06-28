/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Environment(value=EnvType.CLIENT)
public interface LevelScreenProvider {
    final static public Map<Optional<RegistryKey<WorldPreset>>, LevelScreenProvider> WORLD_PRESET_TO_SCREEN_PROVIDER = Map.of(Optional.of(WorldPresets.FLAT), (parent, generatorOptionsHolder) -> {
        ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
        DynamicRegistryManager.Immutable dynamicRegistryManager = generatorOptionsHolder.getCombinedRegistryManager();
        RegistryWrapper.Impl registryEntryLookup = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.BIOME);
        RegistryWrapper.Impl registryEntryLookup2 = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.STRUCTURE_SET);
        RegistryWrapper.Impl registryEntryLookup3 = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.PLACED_FEATURE);
        return new CustomizeFlatLevelScreen(parent, config -> parent.getWorldCreator().applyModifier(LevelScreenProvider.createModifier(config)), chunkGenerator instanceof FlatChunkGenerator ? ((FlatChunkGenerator)chunkGenerator).getConfig() : FlatChunkGeneratorConfig.getDefaultConfig(registryEntryLookup, registryEntryLookup2, registryEntryLookup3));
    }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (parent, generatorOptionsHolder) -> new CustomizeBuffetLevelScreen(parent, generatorOptionsHolder, biomeEntry -> parent.getWorldCreator().applyModifier(LevelScreenProvider.createModifier(biomeEntry))));

    public Screen createEditScreen(CreateWorldScreen var1, GeneratorOptionsHolder var2);

    public static GeneratorOptionsHolder.RegistryAwareModifier createModifier(FlatChunkGeneratorConfig config) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
            FlatChunkGenerator chunkGenerator = new FlatChunkGenerator(config);
            return dimensionsRegistryHolder.with((RegistryWrapper.WrapperLookup)dynamicRegistryManager, chunkGenerator);
        };
    }

    private static GeneratorOptionsHolder.RegistryAwareModifier createModifier(RegistryEntry<Biome> biomeEntry) {
        return (dynamicRegistryManager, dimensionsRegistryHolder) -> {
            RegistryWrapper.Impl registry = dynamicRegistryManager.net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.CHUNK_GENERATOR_SETTINGS);
            RegistryEntry.Reference registryEntry2 = registry.getOrThrow(ChunkGeneratorSettings.OVERWORLD);
            FixedBiomeSource biomeSource = new FixedBiomeSource(biomeEntry);
            NoiseChunkGenerator chunkGenerator = new NoiseChunkGenerator((BiomeSource)biomeSource, registryEntry2);
            return dimensionsRegistryHolder.with((RegistryWrapper.WrapperLookup)dynamicRegistryManager, chunkGenerator);
        };
    }
}

