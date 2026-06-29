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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ContextEntityTypeProperty() implements SelectProperty<RegistryKey<EntityType<?>>>
{
    final static public Codec<RegistryKey<EntityType<?>>> VALUE_CODEC = RegistryKey.createCodec(RegistryKeys.ENTITY_TYPE);
    final static public SelectProperty.Type<ContextEntityTypeProperty, RegistryKey<EntityType<?>>> TYPE = SelectProperty.Type.create(MapCodec.unit((Object)new ContextEntityTypeProperty()), VALUE_CODEC);

    @Override
    @Nullable
    public RegistryKey<EntityType<?>> getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return livingEntity == null ? null : livingEntity.getType().getRegistryEntry().registryKey();
    }

    @Override
    public SelectProperty.Type<ContextEntityTypeProperty, RegistryKey<EntityType<?>>> getType() {
        return TYPE;
    }

    @Override
    public Codec<RegistryKey<EntityType<?>>> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

