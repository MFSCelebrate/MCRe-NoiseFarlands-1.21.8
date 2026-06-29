/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BrewingStandScreen
extends HandledScreen<BrewingStandScreenHandler> {
    final static private Identifier FUEL_LENGTH_TEXTURE = Identifier.ofVanilla("container/brewing_stand/fuel_length");
    final static private Identifier BREW_PROGRESS_TEXTURE = Identifier.ofVanilla("container/brewing_stand/brew_progress");
    final static private Identifier BUBBLES_TEXTURE = Identifier.ofVanilla("container/brewing_stand/bubbles");
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/brewing_stand.png");
    final static private int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public BrewingStandScreen(BrewingStandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int m;
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        int k = ((BrewingStandScreenHandler)this.handler).getFuel();
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FUEL_LENGTH_TEXTURE, 18, 4, 0, 0, i + 60, j + 44, l, 4);
        }
        if ((m = ((BrewingStandScreenHandler)this.handler).getBrewTime()) > 0) {
            int n = (int)(28.0f * (1.0f - (float)m / 400.0f));
            if (n > 0) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BREW_PROGRESS_TEXTURE, 9, 28, 0, 0, i + 97, j + 16, 9, n);
            }
            if ((n = BUBBLE_PROGRESS[m / 2 % 7]) > 0) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BUBBLES_TEXTURE, 12, 29, 0, 29 - n, i + 63, j + 14 + 29 - n, 12, n);
            }
        }
    }
}

