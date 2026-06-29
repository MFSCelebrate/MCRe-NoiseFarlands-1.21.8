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
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record TrimMaterialProperty() implements SelectProperty<RegistryKey<ArmorTrimMaterial>>
{
    final static public Codec<RegistryKey<ArmorTrimMaterial>> VALUE_CODEC = RegistryKey.createCodec(RegistryKeys.TRIM_MATERIAL);
    final static public SelectProperty.Type<TrimMaterialProperty, RegistryKey<ArmorTrimMaterial>> TYPE = SelectProperty.Type.create(MapCodec.unit((Object)new TrimMaterialProperty()), VALUE_CODEC);

    @Override
    @Nullable
    public RegistryKey<ArmorTrimMaterial> getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        ArmorTrim armorTrim = itemStack.get(DataComponentTypes.TRIM);
        if (armorTrim == null) {
            return null;
        }
        return armorTrim.material().getKey().orElse(null);
    }

    @Override
    public SelectProperty.Type<TrimMaterialProperty, RegistryKey<ArmorTrimMaterial>> getType() {
        return TYPE;
    }

    @Override
    public Codec<RegistryKey<ArmorTrimMaterial>> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.getValue(stack, world, user, seed, displayContext);
    }
}

