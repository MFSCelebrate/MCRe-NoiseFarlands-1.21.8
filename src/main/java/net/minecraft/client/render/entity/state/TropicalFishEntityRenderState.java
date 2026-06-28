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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(value=EnvType.CLIENT)
public class TropicalFishEntityRenderState
extends LivingEntityRenderState {
    public TropicalFishEntity.Pattern variety = TropicalFishEntity.Pattern.FLOPPER;
    public int baseColor = -1;
    public int patternColor = -1;
}

