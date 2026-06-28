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
package net.minecraft.client.render.item.property.bool;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record HasComponentProperty(ComponentType<?> componentType, boolean ignoreDefault) implements BooleanProperty
{
    final static public MapCodec<HasComponentProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.DATA_COMPONENT_TYPE.getCodec().fieldOf("component").forGetter(HasComponentProperty::componentType), (App)Codec.BOOL.optionalFieldOf("ignore_default", (Object)false).forGetter(HasComponentProperty::ignoreDefault)).apply((Applicative)instance, HasComponentProperty::new));

    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return this.ignoreDefault ? stack.hasChangedComponent(this.componentType) : stack.contains(this.componentType);
    }

    public MapCodec<HasComponentProperty> getCodec() {
        return CODEC;
    }
}

