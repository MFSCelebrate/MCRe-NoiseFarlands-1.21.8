/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.registry.Registries;
import net.minecraft.world.gen.feature.size.FeatureSizeType;

public abstract class FeatureSize {
    final static public Codec<FeatureSize> TYPE_CODEC = Registries.FEATURE_SIZE_TYPE.getCodec().dispatch(FeatureSize::getType, FeatureSizeType::getCodec);
    final static protected int field_31522 = 16;
    final protected OptionalInt minClippedHeight;

    protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> createCodec() {
        return Codec.intRange(0, 80).optionalFieldOf("min_clipped_height").xmap(minClippedHeight -> minClippedHeight.map(OptionalInt::of).orElse(OptionalInt.empty()), minClippedHeight -> minClippedHeight.isPresent() ? Optional.of(minClippedHeight.getAsInt()) : Optional.empty()).forGetter(featureSize -> featureSize.minClippedHeight);
    }

    public FeatureSize(OptionalInt minClippedHeight) {
        this.minClippedHeight = minClippedHeight;
    }

    protected abstract FeatureSizeType<?> getType();

    public abstract int getRadius(int var1, int var2);

    public OptionalInt getMinClippedHeight() {
        return this.minClippedHeight;
    }
}

