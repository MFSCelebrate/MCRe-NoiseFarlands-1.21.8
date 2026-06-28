/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityHitbox;

@Environment(value=EnvType.CLIENT)
public record EntityHitboxAndView(double viewX, double viewY, double viewZ, ImmutableList<EntityHitbox> hitboxes) {
}

