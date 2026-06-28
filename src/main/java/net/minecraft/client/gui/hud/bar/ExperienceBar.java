/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.bar;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ExperienceBar
implements Bar {
    final static private Identifier BACKGROUND = Identifier.ofVanilla("hud/experience_bar_background");
    final static private Identifier PROGRESS = Identifier.ofVanilla("hud/experience_bar_progress");
    final private MinecraftClient client;

    public ExperienceBar(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void renderBar(DrawContext context, RenderTickCounter tickCounter) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        int i = this.getCenterX(this.client.getWindow());
        int j = this.getCenterY(this.client.getWindow());
        int k = clientPlayerEntity.getNextLevelExperience();
        if (k > 0) {
            int l = (int)(clientPlayerEntity.experienceProgress * 183.0f);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 182, 5);
            if (l > 0) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PROGRESS, 182, 5, 0, 0, i, j, l, 5);
            }
        }
    }

    @Override
    public void renderAddons(DrawContext context, RenderTickCounter tickCounter) {
    }
}

