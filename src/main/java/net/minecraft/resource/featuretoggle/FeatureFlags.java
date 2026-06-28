/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.resource.featuretoggle;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public class FeatureFlags {
    final static public FeatureFlag VANILLA;
    final static public FeatureFlag TRADE_REBALANCE;
    final static public FeatureFlag REDSTONE_EXPERIMENTS;
    final static public FeatureFlag MINECART_IMPROVEMENTS;
    final static public FeatureManager FEATURE_MANAGER;
    final static public Codec<FeatureSet> CODEC;
    final static public FeatureSet VANILLA_FEATURES;
    final static public FeatureSet DEFAULT_ENABLED_FEATURES;

    public static String printMissingFlags(FeatureSet featuresToCheck, FeatureSet features) {
        return FeatureFlags.printMissingFlags(FEATURE_MANAGER, featuresToCheck, features);
    }

    public static String printMissingFlags(FeatureManager featureManager, FeatureSet featuresToCheck, FeatureSet features) {
        Set<Identifier> set = featureManager.toId(features);
        Set<Identifier> set2 = featureManager.toId(featuresToCheck);
        return set.stream().filter(id -> !set2.contains(id)).map(Identifier::toString).collect(Collectors.joining(", "));
    }

    public static boolean isNotVanilla(FeatureSet features) {
        return !features.isSubsetOf(VANILLA_FEATURES);
    }

    static {
        FeatureManager.Builder builder = new FeatureManager.Builder("main");
        VANILLA = builder.addVanillaFlag("vanilla");
        TRADE_REBALANCE = builder.addVanillaFlag("trade_rebalance");
        REDSTONE_EXPERIMENTS = builder.addVanillaFlag("redstone_experiments");
        MINECART_IMPROVEMENTS = builder.addVanillaFlag("minecart_improvements");
        FEATURE_MANAGER = builder.build();
        CODEC = FEATURE_MANAGER.getCodec();
        DEFAULT_ENABLED_FEATURES = VANILLA_FEATURES = FeatureSet.of(VANILLA);
    }
}

