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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class LogoDrawer {
    final static public Identifier LOGO_TEXTURE = Identifier.ofVanilla("textures/gui/title/minecraft.png");
    final static public Identifier MINCERAFT_TEXTURE = Identifier.ofVanilla("textures/gui/title/minceraft.png");
    final static public Identifier EDITION_TEXTURE = Identifier.ofVanilla("textures/gui/title/edition.png");
    final static public int LOGO_REGION_WIDTH = 256;
    final static public int LOGO_REGION_HEIGHT = 44;
    final static private int LOGO_TEXTURE_WIDTH = 256;
    final static private int LOGO_TEXTURE_HEIGHT = 64;
    final static private int EDITION_REGION_WIDTH = 128;
    final static private int EDITION_REGION_HEIGHT = 14;
    final static private int EDITION_TEXTURE_WIDTH = 128;
    final static private int EDITION_TEXTURE_HEIGHT = 16;
    final static public int LOGO_BASE_Y = 30;
    final static private int LOGO_AND_EDITION_OVERLAP = 7;
    final private boolean minceraft = (double)Random.create().nextFloat() < 1.0E-4;
    final private boolean ignoreAlpha;

    public LogoDrawer(boolean ignoreAlpha) {
        this.ignoreAlpha = ignoreAlpha;
    }

    public void draw(DrawContext context, int screenWidth, float alpha) {
        this.draw(context, screenWidth, alpha, 30);
    }

    public void draw(DrawContext context, int screenWidth, float alpha, int y) {
        int i = screenWidth / 2 - 128;
        float f = this.ignoreAlpha ? 1.0f : alpha;
        int j = ColorHelper.getWhite(f);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.minceraft ? MINCERAFT_TEXTURE : LOGO_TEXTURE, i, y, 0.0f, 0.0f, 256, 44, 256, 64, j);
        int k = screenWidth / 2 - 64;
        int l = y + 44 - 7;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, EDITION_TEXTURE, k, l, 0.0f, 0.0f, 128, 14, 128, 16, j);
    }

    public boolean shouldIgnoreAlpha() {
        return this.ignoreAlpha;
    }
}

