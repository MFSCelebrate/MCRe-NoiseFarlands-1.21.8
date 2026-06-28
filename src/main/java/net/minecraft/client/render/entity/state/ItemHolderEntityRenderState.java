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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;

@Environment(value=EnvType.CLIENT)
public class ItemHolderEntityRenderState
extends LivingEntityRenderState {
    final public ItemRenderState itemRenderState = new ItemRenderState();

    public static void update(LivingEntity entity, ItemHolderEntityRenderState state, ItemModelManager itemModelManager) {
        itemModelManager.updateForLivingEntity(state.itemRenderState, entity.getMainHandStack(), ItemDisplayContext.GROUND, entity);
    }
}

