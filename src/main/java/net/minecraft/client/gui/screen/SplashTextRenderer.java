/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SplashTextRenderer {
    final static public SplashTextRenderer MERRY_X_MAS_ = new SplashTextRenderer("Merry X-mas!");
    final static public SplashTextRenderer HAPPY_NEW_YEAR_ = new SplashTextRenderer("Happy new year!");
    final static public SplashTextRenderer OOOOO_O_O_OOOOO__SPOOKY_ = new SplashTextRenderer("OOoooOOOoooo! Spooky!");
    final static private int TEXT_X = 123;
    final static private int TEXT_Y = 69;
    final private String text;

    public SplashTextRenderer(String text) {
        this.text = text;
    }

    public void render(DrawContext context, int screenWidth, TextRenderer textRenderer, float alpha) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)screenWidth / 2.0f + 123.0f, 69.0f);
        context.getMatrices().rotate(-0.34906584f);
        float f = 1.8f - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
        f = f * 100.0f / (float)(textRenderer.getWidth(this.text) + 32);
        context.getMatrices().scale(f, f);
        context.drawCenteredTextWithShadow(textRenderer, this.text, 0, -8, ColorHelper.withAlpha(alpha, Colors.YELLOW));
        context.getMatrices().popMatrix();
    }
}

