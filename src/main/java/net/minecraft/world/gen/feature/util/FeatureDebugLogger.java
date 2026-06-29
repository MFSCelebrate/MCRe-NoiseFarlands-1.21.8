/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.slf4j.Logger
 */
package net.minecraft.world.gen.feature.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureDebugLogger {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private LoadingCache<ServerWorld, Features> FEATURES = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<ServerWorld, Features>(){

        public Features load(ServerWorld serverWorld) {
            return new Features((Object2IntMap<FeatureData>)Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap()), new MutableInt(0));
        }

        public Object load(Object world) throws Exception {
            return this.load((ServerWorld)world);
        }
    });

    public static void incrementTotalChunksCount(ServerWorld world) {
        try {
            ((Features)FEATURES.get((Object)world)).chunksWithFeatures().increment();
        }
        catch (Exception exception) {
            LOGGER.error("Failed to increment chunk count", (Throwable)exception);
        }
    }

    public static void incrementFeatureCount(ServerWorld world, ConfiguredFeature<?, ?> configuredFeature, Optional<PlacedFeature> placedFeature) {
        try {
            ((Features)FEATURES.get((Object)world)).featureData().computeInt((Object)new FeatureData(configuredFeature, placedFeature), (featureData, count) -> count == null ? 1 : count + 1);
        }
        catch (Exception exception) {
            LOGGER.error("Failed to increment feature count", (Throwable)exception);
        }
    }

    public static void clear() {
        FEATURES.invalidateAll();
        LOGGER.debug("Cleared feature counts");
    }

    public static void dump() {
        LOGGER.debug("Logging feature counts:");
        FEATURES.asMap().forEach((world, features) -> {
            String string = world.getRegistryKey().getValue().toString();
            boolean bl = world.getServer().isRunning();
            RegistryWrapper.Impl registry = world.getRegistryManager().net_minecraft_registry_RegistryWrapper$Impl_getOrThrow(RegistryKeys.PLACED_FEATURE);
            String string2 = (bl ? "running" : "dead") + " " + string;
            Integer integer = features.chunksWithFeatures().getValue();
            LOGGER.debug(string2 + " total_chunks: " + integer);
            features.featureData().forEach((arg_0, arg_1) -> FeatureDebugLogger.method_39602(string2, integer, (Registry)registry, arg_0, arg_1));
        });
    }

    private static void method_39602(String string, Integer integer, Registry registry, FeatureData featureData, Integer count) {
        LOGGER.debug(string + " " + String.format(Locale.ROOT, "%10d ", count) + String.format(Locale.ROOT, "%10f ", (double)count.intValue() / (double)integer.intValue()) + String.valueOf(featureData.topFeature().flatMap(registry::getKey).map(RegistryKey::getValue)) + " " + String.valueOf(featureData.feature().feature()) + " " + String.valueOf(featureData.feature()));
    }

    record Features(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
    }

    record FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
    }
}

