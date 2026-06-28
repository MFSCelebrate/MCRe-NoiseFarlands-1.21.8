/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

@Environment(value=EnvType.CLIENT)
public interface TooltipSubmenuHandler {
    public boolean isApplicableTo(Slot var1);

    public boolean onScroll(double var1, double var3, int var5, ItemStack var6);

    public void reset(Slot var1);

    public void onMouseClick(Slot var1, SlotActionType var2);
}

