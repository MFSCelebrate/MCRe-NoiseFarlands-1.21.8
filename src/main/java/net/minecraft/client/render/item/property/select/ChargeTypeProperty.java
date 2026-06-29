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
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ChargeTypeProperty() implements SelectProperty<CrossbowItem.ChargeType>
{
    final static public Codec<CrossbowItem.ChargeType> VALUE_CODEC = CrossbowItem.ChargeType.CODEC;
    final static public SelectProperty.Type<ChargeTypeProperty, CrossbowItem.ChargeType> TYPE = SelectProperty.Type.create(MapCodec.unit((Object)new ChargeTypeProperty()), VALUE_CODEC);

    @Override
    public CrossbowItem.ChargeType net_minecraft_item_CrossbowItem$ChargeType_getValue(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        ChargedProjectilesComponent chargedProjectilesComponent = itemStack.get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
            return CrossbowItem.ChargeType.NONE;
        }
        if (chargedProjectilesComponent.contains(Items.FIREWORK_ROCKET)) {
            return CrossbowItem.ChargeType.ROCKET;
        }
        return CrossbowItem.ChargeType.ARROW;
    }

    @Override
    public SelectProperty.Type<ChargeTypeProperty, CrossbowItem.ChargeType> getType() {
        return TYPE;
    }

    @Override
    public Codec<CrossbowItem.ChargeType> valueCodec() {
        return VALUE_CODEC;
    }

    @Override
    public Object java_lang_Object_getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext) {
        return this.net_minecraft_item_CrossbowItem$ChargeType_getValue(stack, world, user, seed, displayContext);
    }
}

