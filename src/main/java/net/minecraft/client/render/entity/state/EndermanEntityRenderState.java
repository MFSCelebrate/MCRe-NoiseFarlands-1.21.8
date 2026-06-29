/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityRenderState
extends BipedEntityRenderState {
    public boolean angry;
    @Nullable
    public BlockState carriedBlock;
}

