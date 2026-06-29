/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ContextDimensionProperty() implements SelectProperty<RegistryKey<World>>
{
    final static public Codec<RegistryKey<World>> VALUE_CODEC = RegistryKey.createCodec(RegistryKeys.WORLD);
    final static public SelectProperty.Type<ContextDimensionProperty, RegistryKey<World>> TYPE = SelectProperty.Type.create(MapCodec.unit((Object)new ContextDimensionProperty()), VALUE_CODEC);

    @Override
    @Nullable
    public RegistryKey<World> getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return clientWorld != null ? clientWorld.getRegistryKey() : null;
    }

    @Override
    public SelectProperty.Type<ContextDimensionProperty, RegistryKey<World>> getType() {
        return TYPE;
    }

    @Override
    public Codec<RegistryKey<World>> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

