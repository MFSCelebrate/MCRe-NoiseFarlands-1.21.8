/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TooltipBackgroundRenderer {
    final static private Identifier DEFAULT_BACKGROUND_TEXTURE = Identifier.ofVanilla("tooltip/background");
    final static private Identifier DEFAULT_FRAME_TEXTURE = Identifier.ofVanilla("tooltip/frame");
    final static public int field_41688 = 12;
    final static private int field_41693 = 3;
    final static public int field_41689 = 3;
    final static public int field_41690 = 3;
    final static public int field_41691 = 3;
    final static public int field_41692 = 3;
    final static private int field_54153 = 9;

    public static void render(DrawContext context, int x, int y, int width, int height, @Nullable Identifier texture) {
        int i = x - 3 - 9;
        int j = y - 3 - 9;
        int k = width + 3 + 3 + 18;
        int l = height + 3 + 3 + 18;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TooltipBackgroundRenderer.getBackgroundTexture(texture), i, j, k, l);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TooltipBackgroundRenderer.getFrameTexture(texture), i, j, k, l);
    }

    private static Identifier getBackgroundTexture(@Nullable Identifier texture) {
        if (texture == null) {
            return DEFAULT_BACKGROUND_TEXTURE;
        }
        return texture.withPath(name -> "tooltip/" + name + "_background");
    }

    private static Identifier getFrameTexture(@Nullable Identifier texture) {
        if (texture == null) {
            return DEFAULT_FRAME_TEXTURE;
        }
        return texture.withPath(name -> "tooltip/" + name + "_frame");
    }
}

