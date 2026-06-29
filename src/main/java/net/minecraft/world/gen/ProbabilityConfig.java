/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ProbabilityConfig
implements FeatureConfig {
    final static public Codec<ProbabilityConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.floatRange(0.0f, 1.0f).fieldOf("probability").forGetter(probabilityConfig -> Float.valueOf(probabilityConfig.probability))).apply((Applicative)instance, ProbabilityConfig::new));
    final public float probability;

    public ProbabilityConfig(float probability) {
        this.probability = probability;
    }
}

