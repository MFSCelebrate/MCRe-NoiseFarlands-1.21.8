/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record UseDurationProperty(boolean remaining) implements NumericProperty
{
    final static public MapCodec<UseDurationProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("remaining", (Object)false).forGetter(UseDurationProperty::remaining)).apply((Applicative)instance, UseDurationProperty::new));

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        if (holder == null || holder.getActiveItem() != stack) {
            return 0.0f;
        }
        return this.remaining ? (float)holder.getItemUseTimeLeft() : (float)UseDurationProperty.getTicksUsedSoFar(stack, holder);
    }

    public MapCodec<UseDurationProperty> getCodec() {
        return CODEC;
    }

    public static int getTicksUsedSoFar(ItemStack stack, LivingEntity user) {
        return stack.getMaxUseTime(user) - user.getItemUseTimeLeft();
    }
}

