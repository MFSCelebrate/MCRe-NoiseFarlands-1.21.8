/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.item;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemModelManager {
    final private Function<Identifier, ItemModel> modelGetter = bakedModelManager::getItemModel;
    final private Function<Identifier, ItemAsset.Properties> propertiesGetter = bakedModelManager::getItemProperties;

    public ItemModelManager(BakedModelManager bakedModelManager) {
    }

    public void updateForLivingEntity(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, LivingEntity entity) {
        this.clearAndUpdate(renderState, stack, displayContext, entity.net_minecraft_world_World_getWorld(), entity, entity.getId() + displayContext.ordinal());
    }

    public void updateForNonLivingEntity(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Entity entity) {
        this.clearAndUpdate(renderState, stack, displayContext, entity.net_minecraft_world_World_getWorld(), null, entity.getId());
    }

    public void clearAndUpdate(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, @Nullable World world, @Nullable LivingEntity entity, int seed) {
        renderState.clear();
        if (!stack.isEmpty()) {
            renderState.displayContext = displayContext;
            this.update(renderState, stack, displayContext, world, entity, seed);
        }
    }

    public void update(ItemRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, @Nullable World world, @Nullable LivingEntity entity, int seed) {
        ClientWorld clientWorld;
        Identifier identifier = stack.get(DataComponentTypes.ITEM_MODEL);
        if (identifier == null) {
            return;
        }
        renderState.setOversizedInGui(this.propertiesGetter.apply(identifier).oversizedInGui());
        this.modelGetter.apply(identifier).update(renderState, stack, this, displayContext, world instanceof ClientWorld ? (clientWorld = (ClientWorld)world) : null, entity, seed);
    }

    public boolean hasHandAnimationOnSwap(ItemStack stack) {
        Identifier identifier = stack.get(DataComponentTypes.ITEM_MODEL);
        if (identifier == null) {
            return true;
        }
        return this.propertiesGetter.apply(identifier).handAnimationOnSwap();
    }
}

