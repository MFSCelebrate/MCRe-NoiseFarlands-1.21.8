/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.tooltip;

import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.FocusedTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.tooltip.WidgetTooltipPositioner;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TooltipState {
    @Nullable
    private Tooltip tooltip;
    private Duration delay = Duration.ZERO;
    private long renderCheckTime;
    private boolean lastShouldRender;

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    public void setTooltip(@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Nullable
    public Tooltip getTooltip() {
        return this.tooltip;
    }

    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, boolean focused, ScreenRect navigationFocus) {
        boolean bl;
        if (this.tooltip == null) {
            this.lastShouldRender = false;
            return;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl2 = bl = hovered || focused && minecraftClient.getNavigationType().isKeyboard();
        if (bl != this.lastShouldRender) {
            if (bl) {
                this.renderCheckTime = Util.getMeasuringTimeMs();
            }
            this.lastShouldRender = bl;
        }
        if (bl && Util.getMeasuringTimeMs() - this.renderCheckTime > this.delay.toMillis()) {
            context.drawTooltip(minecraftClient.textRenderer, this.tooltip.getLines(minecraftClient), this.createPositioner(navigationFocus, hovered, focused), mouseX, mouseY, focused);
        }
    }

    private TooltipPositioner createPositioner(ScreenRect focus, boolean hovered, boolean focused) {
        if (!hovered && focused && MinecraftClient.getInstance().getNavigationType().isKeyboard()) {
            return new FocusedTooltipPositioner(focus);
        }
        return new WidgetTooltipPositioner(focus);
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
        if (this.tooltip != null) {
            this.tooltip.appendNarrations(builder);
        }
    }
}

