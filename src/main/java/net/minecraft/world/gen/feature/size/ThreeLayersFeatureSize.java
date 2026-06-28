/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature.size;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public class ThreeLayersFeatureSize
extends FeatureSize {
    final static public MapCodec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.intRange(0, 80).fieldOf("limit").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.limit), (App)Codec.intRange(0, 80).fieldOf("upper_limit").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperLimit), (App)Codec.intRange(0, 16).fieldOf("lower_size").orElse((Object)0).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.lowerSize), (App)Codec.intRange(0, 16).fieldOf("middle_size").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.middleSize), (App)Codec.intRange(0, 16).fieldOf("upper_size").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperSize), ThreeLayersFeatureSize.createCodec()).apply((Applicative)instance, ThreeLayersFeatureSize::new));
    final private int limit;
    final private int upperLimit;
    final private int lowerSize;
    final private int middleSize;
    final private int upperSize;

    public ThreeLayersFeatureSize(int limit, int upperLimit, int lowerSize, int middleSize, int upperSize, OptionalInt minClippedHeight) {
        super(minClippedHeight);
        this.limit = limit;
        this.upperLimit = upperLimit;
        this.lowerSize = lowerSize;
        this.middleSize = middleSize;
        this.upperSize = upperSize;
    }

    @Override
    protected FeatureSizeType<?> getType() {
        return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int getRadius(int height, int y) {
        if (y < this.limit) {
            return this.lowerSize;
        }
        if (y >= height - this.upperLimit) {
            return this.upperSize;
        }
        return this.middleSize;
    }
}

