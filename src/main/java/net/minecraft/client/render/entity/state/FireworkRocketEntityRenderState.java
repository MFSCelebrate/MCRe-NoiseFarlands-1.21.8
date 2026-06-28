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
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

@Environment(value=EnvType.CLIENT)
public class FireworkRocketEntityRenderState
extends EntityRenderState {
    public boolean shotAtAngle;
    final public ItemRenderState stack = new ItemRenderState();
}

