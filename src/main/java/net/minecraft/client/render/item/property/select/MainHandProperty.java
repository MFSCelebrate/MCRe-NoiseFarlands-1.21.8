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
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MainHandProperty() implements SelectProperty<Arm>
{
    final static public Codec<Arm> VALUE_CODEC = Arm.CODEC;
    final static public SelectProperty.Type<MainHandProperty, Arm> TYPE = SelectProperty.Type.create(MapCodec.unit((Object)new MainHandProperty()), VALUE_CODEC);

    @Override
    @Nullable
    public Arm net_minecraft_util_Arm_getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return livingEntity == null ? null : livingEntity.getMainArm();
    }

    @Override
    public SelectProperty.Type<MainHandProperty, Arm> getType() {
        return TYPE;
    }

    @Override
    public Codec<Arm> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public Object java_lang_Object_getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.net_minecraft_util_Arm_getValue(stack, world, user, seed, displayContext);
    }
}

