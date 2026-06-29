/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.bar;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.JumpingMount;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class JumpBar
implements Bar {
    final static private Identifier BACKGROUND = Identifier.ofVanilla("hud/jump_bar_background");
    final static private Identifier COOLDOWN = Identifier.ofVanilla("hud/jump_bar_cooldown");
    final static private Identifier PROGRESS = Identifier.ofVanilla("hud/jump_bar_progress");
    final private MinecraftClient client;
    final private JumpingMount jumpingMount;

    public JumpBar(MinecraftClient client) {
        this.client = client;
        this.jumpingMount = Objects.requireNonNull(client.player).getJumpingMount();
    }

    @Override
    public void renderBar(DrawContext context, RenderTickCounter tickCounter) {
        int i = this.getCenterX(this.client.getWindow());
        int j = this.getCenterY(this.client.getWindow());
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 182, 5);
        if (this.jumpingMount.getJumpCooldown() > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, COOLDOWN, i, j, 182, 5);
            return;
        }
        int k = (int)(this.client.player.getMountJumpStrength() * 183.0f);
        if (k > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PROGRESS, 182, 5, 0, 0, i, j, k, 5);
        }
    }

    @Override
    public void renderAddons(DrawContext context, RenderTickCounter tickCounter) {
    }
}

