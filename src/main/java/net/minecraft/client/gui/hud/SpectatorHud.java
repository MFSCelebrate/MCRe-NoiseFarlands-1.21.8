/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpectatorHud
implements SpectatorMenuCloseCallback {
    final static private Identifier HOTBAR_TEXTURE = Identifier.ofVanilla("hud/hotbar");
    final static private Identifier HOTBAR_SELECTION_TEXTURE = Identifier.ofVanilla("hud/hotbar_selection");
    final static private long FADE_OUT_DELAY = 5000L;
    final static private long FADE_OUT_DURATION = 2000L;
    final private MinecraftClient client;
    private long lastInteractionTime;
    @Nullable
    private SpectatorMenu spectatorMenu;

    public SpectatorHud(MinecraftClient client) {
        this.client = client;
    }

    public void selectSlot(int slot) {
        this.lastInteractionTime = Util.getMeasuringTimeMs();
        if (this.spectatorMenu != null) {
            this.spectatorMenu.useCommand(slot);
        } else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }

    private float getSpectatorMenuHeight() {
        long l = this.lastInteractionTime - Util.getMeasuringTimeMs() + 5000L;
        return MathHelper.clamp((float)l / 2000.0f, 0.0f, 1.0f);
    }

    public void renderSpectatorMenu(DrawContext context) {
        if (this.spectatorMenu == null) {
            return;
        }
        float f = this.getSpectatorMenuHeight();
        if (f <= 0.0f) {
            this.spectatorMenu.close();
            return;
        }
        int i = context.getScaledWindowWidth() / 2;
        int j = MathHelper.floor((float)context.getScaledWindowHeight() - 22.0f * f);
        SpectatorMenuState spectatorMenuState = this.spectatorMenu.getCurrentState();
        this.renderSpectatorMenu(context, f, i, j, spectatorMenuState);
    }

    protected void renderSpectatorMenu(DrawContext context, float height, int x, int y, SpectatorMenuState state) {
        int i = ColorHelper.getWhite(height);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_TEXTURE, x - 91, y, 182, 22, i);
        if (state.getSelectedSlot() >= 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SELECTION_TEXTURE, x - 91 - 1 + state.getSelectedSlot() * 20, y - 1, 24, 23, i);
        }
        for (int j = 0; j < 9; ++j) {
            this.renderSpectatorCommand(context, j, context.getScaledWindowWidth() / 2 - 90 + j * 20 + 2, y + 3, height, state.getCommand(j));
        }
    }

    private void renderSpectatorCommand(DrawContext context, int slot, int x, float y, float height, SpectatorMenuCommand command) {
        if (command != SpectatorMenu.BLANK_COMMAND) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float)x, y);
            float f = command.isEnabled() ? 1.0f : 0.25f;
            command.renderIcon(context, f, height);
            context.getMatrices().popMatrix();
            if (height > 0.0f && command.isEnabled()) {
                Text text = this.client.options.hotbarKeys[slot].getBoundKeyLocalizedText();
                context.drawTextWithShadow(this.client.textRenderer, text, x + 19 - 2 - this.client.textRenderer.getWidth(text), (int)y + 6 + 3, ColorHelper.withAlpha(height, Colors.WHITE));
            }
        }
    }

    public void render(DrawContext context) {
        float f = this.getSpectatorMenuHeight();
        if (f > 0.0f && this.spectatorMenu != null) {
            SpectatorMenuCommand spectatorMenuCommand = this.spectatorMenu.getSelectedCommand();
            Text text = spectatorMenuCommand == SpectatorMenu.BLANK_COMMAND ? this.spectatorMenu.getCurrentGroup().getPrompt() : spectatorMenuCommand.getName();
            int i = this.client.textRenderer.getWidth(text);
            int j = (context.getScaledWindowWidth() - i) / 2;
            int k = context.getScaledWindowHeight() - 35;
            context.drawTextWithBackground(this.client.textRenderer, text, j, k, i, ColorHelper.withAlpha(f, Colors.WHITE));
        }
    }

    @Override
    public void close(SpectatorMenu menu) {
        this.spectatorMenu = null;
        this.lastInteractionTime = 0L;
    }

    public boolean isOpen() {
        return this.spectatorMenu != null;
    }

    public void cycleSlot(int offset) {
        int i;
        for (i = this.spectatorMenu.getSelectedSlot() + offset; !(i < 0 || i > 8 || this.spectatorMenu.getCommand(i) != SpectatorMenu.BLANK_COMMAND && this.spectatorMenu.getCommand(i).isEnabled()); i += offset) {
        }
        if (i >= 0 && i <= 8) {
            this.spectatorMenu.useCommand(i);
            this.lastInteractionTime = Util.getMeasuringTimeMs();
        }
    }

    public void useSelectedCommand() {
        this.lastInteractionTime = Util.getMeasuringTimeMs();
        if (this.isOpen()) {
            int i = this.spectatorMenu.getSelectedSlot();
            if (i != -1) {
                this.spectatorMenu.useCommand(i);
            }
        } else {
            this.spectatorMenu = new SpectatorMenu(this);
        }
    }
}

