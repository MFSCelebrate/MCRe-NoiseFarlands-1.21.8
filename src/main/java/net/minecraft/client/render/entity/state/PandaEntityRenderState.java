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
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.entity.passive.PandaEntity;

@Environment(value=EnvType.CLIENT)
public class PandaEntityRenderState
extends ItemHolderEntityRenderState {
    public PandaEntity.Gene gene = PandaEntity.Gene.NORMAL;
    public boolean askingForBamboo;
    public boolean sneezing;
    public int sneezeProgress;
    public boolean eating;
    public boolean scaredByThunderstorm;
    public boolean sitting;
    public float sittingAnimationProgress;
    public float lieOnBackAnimationProgress;
    public float rollOverAnimationProgress;
    public float playingTicks;
}

