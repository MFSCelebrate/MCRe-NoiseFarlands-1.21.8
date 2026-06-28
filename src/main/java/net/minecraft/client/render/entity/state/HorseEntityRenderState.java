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
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class HorseEntityRenderState
extends LivingHorseEntityRenderState {
    public HorseColor color = HorseColor.WHITE;
    public HorseMarking marking = HorseMarking.NONE;
    public ItemStack armor = ItemStack.EMPTY;
}

