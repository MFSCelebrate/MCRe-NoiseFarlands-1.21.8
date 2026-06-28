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
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderState
extends EntityRenderState {
    public Direction facing = Direction.NORTH;
    final public ItemRenderState itemRenderState = new ItemRenderState();
    public int rotation;
    public boolean glow;
    @Nullable
    public MapIdComponent mapId;
    final public MapRenderState mapRenderState = new MapRenderState();
}

