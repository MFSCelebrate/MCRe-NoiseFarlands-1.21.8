/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;

public class BiomeParticleConfig {
    final static public Codec<BiomeParticleConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ParticleTypes.TYPE_CODEC.fieldOf("options").forGetter(config -> config.particle), (App)Codec.FLOAT.fieldOf("probability").forGetter(config -> Float.valueOf(config.probability))).apply((Applicative)instance, BiomeParticleConfig::new));
    final private ParticleEffect particle;
    final private float probability;

    public BiomeParticleConfig(ParticleEffect particle, float probability) {
        this.particle = particle;
        this.probability = probability;
    }

    public ParticleEffect getParticle() {
        return this.particle;
    }

    public boolean shouldAddParticle(Random random) {
        return random.nextFloat() <= this.probability;
    }
}

