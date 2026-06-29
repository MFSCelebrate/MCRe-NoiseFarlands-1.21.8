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
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CatEntityRenderState
extends FelineEntityRenderState {
    final static private Identifier DEFAULT_TEXTURE = Identifier.ofVanilla("textures/entity/cat/tabby.png");
    public Identifier texture = DEFAULT_TEXTURE;
    public boolean nearSleepingPlayer;
    @Nullable
    public DyeColor collarColor;
}

