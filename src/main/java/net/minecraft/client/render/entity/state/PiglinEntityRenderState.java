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
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.mob.PiglinActivity;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityRenderState
extends BipedEntityRenderState {
    public boolean brute;
    public boolean shouldZombify;
    public float piglinCrossbowPullTime;
    public PiglinActivity activity = PiglinActivity.DEFAULT;
}

