/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ItemBlockStateProperty(String property) implements SelectProperty<String>
{
    final static public PrimitiveCodec<String> VALUE_CODEC = Codec.STRING;
    final static public SelectProperty.Type<ItemBlockStateProperty, String> TYPE = SelectProperty.Type.create(RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("block_state_property").forGetter(ItemBlockStateProperty::property)).apply((Applicative)instance, ItemBlockStateProperty::new)), VALUE_CODEC);

    @Override
    @Nullable
    public String java_lang_String_getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        BlockStateComponent blockStateComponent = itemStack.get(DataComponentTypes.BLOCK_STATE);
        if (blockStateComponent == null) {
            return null;
        }
        return blockStateComponent.properties().get(this.property);
    }

    @Override
    public SelectProperty.Type<ItemBlockStateProperty, String> getType() {
        return TYPE;
    }

    @Override
    public Codec<String> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    @Nullable
    public Object java_lang_Object_getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.java_lang_String_getValue(stack, world, user, seed, displayContext);
    }
}

