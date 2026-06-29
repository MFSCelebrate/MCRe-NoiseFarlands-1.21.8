/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModelPart
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockModelPart;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BlockModelPart
extends FabricBlockModelPart {
    public List<BakedQuad> getQuads(@Nullable Direction var1);

    public boolean useAmbientOcclusion();

    public Sprite particleSprite();

    @Environment(value=EnvType.CLIENT)
    public static interface Unbaked
    extends ResolvableModel {
        public BlockModelPart bake(Baker var1);
    }
}

