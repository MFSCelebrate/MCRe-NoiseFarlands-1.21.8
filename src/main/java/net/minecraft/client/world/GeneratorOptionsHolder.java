/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.InitialWorldOptions;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.DataPackContents;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.WorldGenSettings;

@Environment(value=EnvType.CLIENT)
public record GeneratorOptionsHolder(GeneratorOptions generatorOptions, Registry<DimensionOptions> dimensionOptionsRegistry, DimensionOptionsRegistryHolder selectedDimensions, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration, InitialWorldOptions initialWorldCreationOptions) {
    public GeneratorOptionsHolder(WorldGenSettings worldGenSettings, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration) {
        this(worldGenSettings.generatorOptions(), worldGenSettings.dimensionOptionsRegistryHolder(), combinedDynamicRegistries, dataPackContents, dataConfiguration, new InitialWorldOptions(WorldCreator.Mode.SURVIVAL, Set.of(), null));
    }

    public GeneratorOptionsHolder(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder selectedDimensions, CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration, InitialWorldOptions initialWorldOptions) {
        this(generatorOptions, (Registry<DimensionOptions>)combinedDynamicRegistries.get(ServerDynamicRegistryType.DIMENSIONS).net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.DIMENSION), selectedDimensions, combinedDynamicRegistries.with(ServerDynamicRegistryType.DIMENSIONS, new DynamicRegistryManager.Immutable[0]), dataPackContents, dataConfiguration, initialWorldOptions);
    }

    public GeneratorOptionsHolder with(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder selectedDimensions) {
        return new GeneratorOptionsHolder(generatorOptions, this.dimensionOptionsRegistry, selectedDimensions, this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public GeneratorOptionsHolder apply(Modifier modifier) {
        return new GeneratorOptionsHolder((GeneratorOptions)modifier.apply(this.generatorOptions), this.dimensionOptionsRegistry, this.selectedDimensions, this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public GeneratorOptionsHolder apply(RegistryAwareModifier modifier) {
        return new GeneratorOptionsHolder(this.generatorOptions, this.dimensionOptionsRegistry, (DimensionOptionsRegistryHolder)modifier.apply(this.getCombinedRegistryManager(), this.selectedDimensions), this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
    }

    public DynamicRegistryManager.Immutable getCombinedRegistryManager() {
        return this.combinedDynamicRegistries.getCombinedRegistryManager();
    }

    public void initializeIndexedFeaturesLists() {
        for (DimensionOptions dimensionOptions : this.dimensionOptionsRegistry()) {
            dimensionOptions.chunkGenerator().initializeIndexedFeaturesList();
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GeneratorOptionsHolder.class, "options;datapackDimensions;selectedDimensions;worldgenRegistries;dataPackResources;dataConfiguration;initialWorldCreationOptions", "generatorOptions", "dimensionOptionsRegistry", "selectedDimensions", "combinedDynamicRegistries", "dataPackContents", "dataConfiguration", "initialWorldCreationOptions"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GeneratorOptionsHolder.class, "options;datapackDimensions;selectedDimensions;worldgenRegistries;dataPackResources;dataConfiguration;initialWorldCreationOptions", "generatorOptions", "dimensionOptionsRegistry", "selectedDimensions", "combinedDynamicRegistries", "dataPackContents", "dataConfiguration", "initialWorldCreationOptions"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GeneratorOptionsHolder.class, "options;datapackDimensions;selectedDimensions;worldgenRegistries;dataPackResources;dataConfiguration;initialWorldCreationOptions", "generatorOptions", "dimensionOptionsRegistry", "selectedDimensions", "combinedDynamicRegistries", "dataPackContents", "dataConfiguration", "initialWorldCreationOptions"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Modifier
    extends UnaryOperator<GeneratorOptions> {
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface RegistryAwareModifier
    extends BiFunction<DynamicRegistryManager.Immutable, DimensionOptionsRegistryHolder, DimensionOptionsRegistryHolder> {
    }
}

