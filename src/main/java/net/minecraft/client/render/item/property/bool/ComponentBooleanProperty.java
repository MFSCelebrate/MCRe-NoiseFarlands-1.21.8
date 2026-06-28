/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.bool;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentPredicate;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ComponentBooleanProperty(ComponentPredicate.Typed<?> predicate) implements BooleanProperty
{
    final static public MapCodec<ComponentBooleanProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ComponentPredicate.createCodec("predicate").forGetter(ComponentBooleanProperty::predicate)).apply((Applicative)instance, ComponentBooleanProperty::new));

    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return this.predicate.predicate().test(stack);
    }

    public MapCodec<ComponentBooleanProperty> getCodec() {
        return CODEC;
    }
}

