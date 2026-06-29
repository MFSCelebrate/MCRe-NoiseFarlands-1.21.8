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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CooldownProperty() implements NumericProperty
{
    final static public MapCodec<CooldownProperty> CODEC = MapCodec.unit((Object)new CooldownProperty());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        float f;
        if (holder instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)holder;
            f = playerEntity.getItemCooldownManager().getCooldownProgress(stack, 0.0f);
        } else {
            f = 0.0f;
        }
        return f;
    }

    public MapCodec<CooldownProperty> getCodec() {
        return CODEC;
    }
}

