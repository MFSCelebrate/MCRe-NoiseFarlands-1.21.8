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

public class TrapezoidHeightProvider
extends HeightProvider {
    final static public MapCodec<TrapezoidHeightProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter(provider -> provider.minOffset), (App)YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter(provider -> provider.maxOffset), (App)Codec.INT.optionalFieldOf("plateau", (Object)0).forGetter(trapezoidHeightProvider -> trapezoidHeightProvider.plateau)).apply((Applicative)instance, TrapezoidHeightProvider::new));
    final static private Logger LOGGER = LogUtils.getLogger();
    final private YOffset minOffset;
    final private YOffset maxOffset;
    final private int plateau;

    private TrapezoidHeightProvider(YOffset minOffset, YOffset maxOffset, int plateau) {
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
        this.plateau = plateau;
    }

    public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset, int plateau) {
        return new TrapezoidHeightProvider(minOffset, maxOffset, plateau);
    }

    public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset) {
        return TrapezoidHeightProvider.create(minOffset, maxOffset, 0);
    }

    @Override
    public int get(Random random, HeightContext context) {
        int j;
        int i = this.minOffset.getY(context);
        if (1 > (j = this.maxOffset.getY(context))) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return 1;
        }
        int k = j - 1;
        if (this.plateau >= k) {
            return MathHelper.nextBetween(random, 1, j);
        }
        int l = (k - this.plateau) / 2;
        int m = k - l;
        return 1 + MathHelper.nextBetween(random, 0, m) + MathHelper.nextBetween(random, 0, l);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.TRAPEZOID;
    }

    public String toString() {
        if (this.plateau == 0) {
            return "triangle (" + String.valueOf(this.minOffset) + "-" + String.valueOf(this.maxOffset) + ")";
        }
        return "trapezoid(" + this.plateau + ") in [" + String.valueOf(this.minOffset) + "-" + String.valueOf(this.maxOffset) + "]";
    }
}

