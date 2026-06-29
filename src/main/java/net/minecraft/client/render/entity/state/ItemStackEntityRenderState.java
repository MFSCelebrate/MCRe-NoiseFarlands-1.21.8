/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class ItemStackEntityRenderState
extends EntityRenderState {
    final public ItemRenderState itemRenderState = new ItemRenderState();
    public int renderedAmount;
    public int seed;

    public void update(Entity entity, ItemStack stack, ItemModelManager itemModelManager) {
        itemModelManager.updateForNonLivingEntity(this.itemRenderState, stack, ItemDisplayContext.GROUND, entity);
        this.renderedAmount = ItemStackEntityRenderState.getRenderedAmount(stack.getCount());
        this.seed = ItemStackEntityRenderState.getSeed(stack);
    }

    public static int getSeed(ItemStack stack) {
        return stack.isEmpty() ? 187 : Item.getRawId(stack.getItem()) + stack.getDamage();
    }

    public static int getRenderedAmount(int count) {
        if (count <= 1) {
            return 1;
        }
        if (count <= 16) {
            return 2;
        }
        if (count <= 32) {
            return 3;
        }
        if (count <= 48) {
            return 4;
        }
        return 5;
    }
}

