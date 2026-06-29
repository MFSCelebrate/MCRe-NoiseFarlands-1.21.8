/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SimpleGuiElementRenderState
extends GuiElementRenderState {
    public void setupVertices(VertexConsumer var1, float var2);

    public RenderPipeline pipeline();

    public TextureSetup textureSetup();

    @Nullable
    public ScreenRect scissorArea();
}

