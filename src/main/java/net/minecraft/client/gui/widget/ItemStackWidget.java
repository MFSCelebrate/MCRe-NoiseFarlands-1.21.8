/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

@Environment(value=EnvType.CLIENT)
public class ItemStackWidget
extends ClickableWidget {
    final private MinecraftClient client;
    final private int xOffset;
    final private int yOffset;
    final private ItemStack stack;
    final private boolean drawOverlay;
    final private boolean hasTooltip;

    public ItemStackWidget(MinecraftClient client, int x, int y, int width, int height, Text message, ItemStack stack, boolean drawOverlay, boolean hasTooltip) {
        super(0, 0, width, height, message);
        this.client = client;
        this.xOffset = x;
        this.yOffset = y;
        this.stack = stack;
        this.drawOverlay = drawOverlay;
        this.hasTooltip = hasTooltip;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawItem(this.stack, this.getX() + this.xOffset, this.getY() + this.yOffset, 0);
        if (this.drawOverlay) {
            context.drawStackOverlay(this.client.textRenderer, this.stack, this.getX() + this.xOffset, this.getY() + this.yOffset, null);
        }
        if (this.isFocused()) {
            context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), Colors.WHITE);
        }
        if (this.hasTooltip && this.isSelected()) {
            context.drawItemTooltip(this.client.textRenderer, this.stack, mouseX, mouseY);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)Text.translatable("narration.item", this.stack.getName()));
    }
}

