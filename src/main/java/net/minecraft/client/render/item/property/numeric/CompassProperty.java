/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.CompassState;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompassProperty
implements NumericProperty {
    final static public MapCodec<CompassProperty> CODEC = CompassState.CODEC.xmap(CompassProperty::new, property -> property.state);
    final private CompassState state;

    public CompassProperty(boolean wobble, CompassState.Target target) {
        this(new CompassState(wobble, target));
    }

    private CompassProperty(CompassState state) {
        this.state = state;
    }

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        return this.state.getValue(stack, world, holder, seed);
    }

    public MapCodec<CompassProperty> getCodec() {
        return CODEC;
    }
}

