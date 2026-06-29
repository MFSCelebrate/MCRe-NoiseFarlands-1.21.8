/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.item.consume;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.world.World;

public record ClearAllEffectsConsumeEffect() implements ConsumeEffect
{
    final static public ClearAllEffectsConsumeEffect INSTANCE = new ClearAllEffectsConsumeEffect();
    final static public MapCodec<ClearAllEffectsConsumeEffect> CODEC = MapCodec.unit((Object)INSTANCE);
    final static public PacketCodec<RegistryByteBuf, ClearAllEffectsConsumeEffect> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    public ConsumeEffect.Type<ClearAllEffectsConsumeEffect> getType() {
        return ConsumeEffect.Type.CLEAR_ALL_EFFECTS;
    }

    @Override
    public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
        return user.clearStatusEffects();
    }
}

