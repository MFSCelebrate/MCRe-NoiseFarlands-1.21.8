/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinDrawer {
    final static public int FACE_WIDTH = 8;
    final static public int FACE_HEIGHT = 8;
    final static public int FACE_X = 8;
    final static public int FACE_Y = 8;
    final static public int FACE_OVERLAY_X = 40;
    final static public int FACE_OVERLAY_Y = 8;
    final static public int field_39531 = 8;
    final static public int field_39532 = 8;
    final static public int SKIN_TEXTURE_WIDTH = 64;
    final static public int SKIN_TEXTURE_HEIGHT = 64;

    public static void draw(DrawContext context, SkinTextures textures, int x, int y, int size) {
        PlayerSkinDrawer.draw(context, textures, x, y, size, -1);
    }

    public static void draw(DrawContext context, SkinTextures textures, int x, int y, int size, int color) {
        PlayerSkinDrawer.draw(context, textures.texture(), x, y, size, true, false, color);
    }

    public static void draw(DrawContext context, Identifier texture, int x, int y, int size, boolean hatVisible, boolean upsideDown, int color) {
        int i = 8 + (upsideDown ? 8 : 0);
        int j = 8 * (upsideDown ? -1 : 1);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 8.0f, i, size, size, 8, j, 64, 64, color);
        if (hatVisible) {
            PlayerSkinDrawer.drawHat(context, texture, x, y, size, upsideDown, color);
        }
    }

    private static void drawHat(DrawContext context, Identifier texture, int x, int y, int size, boolean upsideDown, int color) {
        int i = 8 + (upsideDown ? 8 : 0);
        int j = 8 * (upsideDown ? -1 : 1);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 40.0f, i, size, size, 8, j, 64, 64, color);
    }
}

