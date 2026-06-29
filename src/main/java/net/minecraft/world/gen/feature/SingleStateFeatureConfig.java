/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class SingleStateFeatureConfig
implements FeatureConfig {
    final static public Codec<SingleStateFeatureConfig> CODEC = BlockState.CODEC.fieldOf("state").xmap(SingleStateFeatureConfig::new, config -> config.state).codec();
    final public BlockState state;

    public SingleStateFeatureConfig(BlockState state) {
        this.state = state;
    }
}

