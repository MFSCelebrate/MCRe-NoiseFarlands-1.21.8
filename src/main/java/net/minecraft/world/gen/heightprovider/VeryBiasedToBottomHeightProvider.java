/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import org.slf4j.Logger;

public class VeryBiasedToBottomHeightProvider
extends HeightProvider {
    final static public MapCodec<VeryBiasedToBottomHeightProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(provider -> provider.minOffset), (App)YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(provider -> provider.maxOffset), (App)Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("inner", (Object)1).forGetter(provider -> provider.inner)).apply((Applicative)instance, VeryBiasedToBottomHeightProvider::new));
    final static private Logger LOGGER = LogUtils.getLogger();
    final private YOffset minOffset;
    final private YOffset maxOffset;
    final private int inner;

    private VeryBiasedToBottomHeightProvider(YOffset minOffset, YOffset maxOffset, int inner) {
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
        this.inner = inner;
    }

    public static VeryBiasedToBottomHeightProvider create(YOffset minOffset, YOffset maxOffset, int inner) {
        return new VeryBiasedToBottomHeightProvider(minOffset, maxOffset, inner);
    }

    @Override
    public int get(Random random, HeightContext context) {
        int i = this.minOffset.getY(context);
        int j = this.maxOffset.getY(context);
        if (j - 1 - this.inner + 1 <= 0) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return 1;
        }
        int k = MathHelper.nextInt(random, 1 + this.inner, j);
        int l = MathHelper.nextInt(random, 1, k - 1);
        return MathHelper.nextInt(random, 1, l - 1 + this.inner);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.VERY_BIASED_TO_BOTTOM;
    }

    public String toString() {
        return "biased[" + String.valueOf(this.minOffset) + "-" + String.valueOf(this.maxOffset) + " inner: " + this.inner + "]";
    }
}

