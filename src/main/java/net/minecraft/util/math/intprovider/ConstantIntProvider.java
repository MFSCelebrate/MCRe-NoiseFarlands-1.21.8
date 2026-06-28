/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.random.Random;

public class ConstantIntProvider
extends IntProvider {
    final static public ConstantIntProvider ZERO = new ConstantIntProvider(0);
    final static public MapCodec<ConstantIntProvider> CODEC = Codec.INT.fieldOf("value").xmap(ConstantIntProvider::create, ConstantIntProvider::getValue);
    final private int value;

    public static ConstantIntProvider create(int value) {
        if (value == 0) {
            return ZERO;
        }
        return new ConstantIntProvider(value);
    }

    private ConstantIntProvider(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int get(Random random) {
        return this.value;
    }

    @Override
    public int getMin() {
        return this.value;
    }

    @Override
    public int getMax() {
        return this.value;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.CONSTANT;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}

