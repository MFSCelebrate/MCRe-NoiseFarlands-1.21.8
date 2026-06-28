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

public class GeodeLayerThicknessConfig {
    final static private Codec<Double> RANGE = Codec.doubleRange(0.01, 50.0);
    final static public Codec<GeodeLayerThicknessConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RANGE.fieldOf("filling").orElse((Object)1.7).forGetter(config -> config.filling), (App)RANGE.fieldOf("inner_layer").orElse((Object)2.2).forGetter(config -> config.innerLayer), (App)RANGE.fieldOf("middle_layer").orElse((Object)3.2).forGetter(config -> config.middleLayer), (App)RANGE.fieldOf("outer_layer").orElse((Object)4.2).forGetter(config -> config.outerLayer)).apply((Applicative)instance, GeodeLayerThicknessConfig::new));
    final public double filling;
    final public double innerLayer;
    final public double middleLayer;
    final public double outerLayer;

    public GeodeLayerThicknessConfig(double filling, double innerLayer, double middleLayer, double outerLayer) {
        this.filling = filling;
        this.innerLayer = innerLayer;
        this.middleLayer = middleLayer;
        this.outerLayer = outerLayer;
    }
}

