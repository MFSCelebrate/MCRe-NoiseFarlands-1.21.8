/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record CarriedProperty() implements BooleanProperty
{
    final static public MapCodec<CarriedProperty> CODEC = MapCodec.unit((Object)new CarriedProperty());

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        if (!(entity instanceof ClientPlayerEntity)) return false;
        ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
        if (clientPlayerEntity.currentScreenHandler.getCursorStack() != stack) return false;
        return true;
    }

    public MapCodec<CarriedProperty> getCodec() {
        return CODEC;
    }
}

