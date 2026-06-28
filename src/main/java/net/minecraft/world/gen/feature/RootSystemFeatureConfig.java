/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class RootSystemFeatureConfig
implements FeatureConfig {
    final static public Codec<RootSystemFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(config -> config.feature), (App)Codec.intRange(1, 64).fieldOf("required_vertical_space_for_tree").forGetter(config -> config.requiredVerticalSpaceForTree), (App)Codec.intRange(1, 64).fieldOf("root_radius").forGetter(config -> config.rootRadius), (App)TagKey.codec(RegistryKeys.BLOCK).fieldOf("root_replaceable").forGetter(config -> config.rootReplaceable), (App)BlockStateProvider.TYPE_CODEC.fieldOf("root_state_provider").forGetter(config -> config.rootStateProvider), (App)Codec.intRange(1, 256).fieldOf("root_placement_attempts").forGetter(config -> config.rootPlacementAttempts), (App)Codec.intRange(1, 4096).fieldOf("root_column_max_height").forGetter(config -> config.maxRootColumnHeight), (App)Codec.intRange(1, 64).fieldOf("hanging_root_radius").forGetter(config -> config.hangingRootRadius), (App)Codec.intRange(0, 16).fieldOf("hanging_roots_vertical_span").forGetter(config -> config.hangingRootVerticalSpan), (App)BlockStateProvider.TYPE_CODEC.fieldOf("hanging_root_state_provider").forGetter(config -> config.hangingRootStateProvider), (App)Codec.intRange(1, 256).fieldOf("hanging_root_placement_attempts").forGetter(config -> config.hangingRootPlacementAttempts), (App)Codec.intRange(1, 64).fieldOf("allowed_vertical_water_for_tree").forGetter(config -> config.allowedVerticalWaterForTree), (App)BlockPredicate.BASE_CODEC.fieldOf("allowed_tree_position").forGetter(config -> config.predicate)).apply((Applicative)instance, RootSystemFeatureConfig::new));
    final public RegistryEntry<PlacedFeature> feature;
    final public int requiredVerticalSpaceForTree;
    final public int rootRadius;
    final public TagKey<Block> rootReplaceable;
    final public BlockStateProvider rootStateProvider;
    final public int rootPlacementAttempts;
    final public int maxRootColumnHeight;
    final public int hangingRootRadius;
    final public int hangingRootVerticalSpan;
    final public BlockStateProvider hangingRootStateProvider;
    final public int hangingRootPlacementAttempts;
    final public int allowedVerticalWaterForTree;
    final public BlockPredicate predicate;

    public RootSystemFeatureConfig(RegistryEntry<PlacedFeature> feature, int requiredVerticalSpaceForTree, int rootRadius, TagKey<Block> rootReplaceable, BlockStateProvider rootStateProvider, int rootPlacementAttempts, int maxRootColumnHeight, int hangingRootRadius, int hangingRootVerticalSpan, BlockStateProvider hangingRootStateProvider, int hangingRootPlacementAttempts, int allowedVerticalWaterForTree, BlockPredicate predicate) {
        this.feature = feature;
        this.requiredVerticalSpaceForTree = requiredVerticalSpaceForTree;
        this.rootRadius = rootRadius;
        this.rootReplaceable = rootReplaceable;
        this.rootStateProvider = rootStateProvider;
        this.rootPlacementAttempts = rootPlacementAttempts;
        this.maxRootColumnHeight = maxRootColumnHeight;
        this.hangingRootRadius = hangingRootRadius;
        this.hangingRootVerticalSpan = hangingRootVerticalSpan;
        this.hangingRootStateProvider = hangingRootStateProvider;
        this.hangingRootPlacementAttempts = hangingRootPlacementAttempts;
        this.allowedVerticalWaterForTree = allowedVerticalWaterForTree;
        this.predicate = predicate;
    }
}

