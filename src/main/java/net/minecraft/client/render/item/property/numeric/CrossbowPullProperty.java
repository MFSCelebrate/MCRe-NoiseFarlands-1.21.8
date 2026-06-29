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
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CrossbowPullProperty
implements NumericProperty {
    final static public MapCodec<CrossbowPullProperty> CODEC = MapCodec.unit((Object)new CrossbowPullProperty());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        if (holder == null) {
            return 0.0f;
        }
        if (CrossbowItem.isCharged(stack)) {
            return 0.0f;
        }
        int i = CrossbowItem.getPullTime(stack, holder);
        return (float)UseDurationProperty.getTicksUsedSoFar(stack, holder) / (float)i;
    }

    public MapCodec<CrossbowPullProperty> getCodec() {
        return CODEC;
    }
}

