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
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.decoration.DisplayEntity;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class DisplayEntityRenderState
extends EntityRenderState {
    @Nullable
    public DisplayEntity.RenderState displayRenderState;
    public float lerpProgress;
    public float yaw;
    public float pitch;

    public abstract boolean canRender();
}

