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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ForgingScreen<T extends ForgingScreenHandler>
extends HandledScreen<T>
implements ScreenHandlerListener {
    final private Identifier texture;

    public ForgingScreen(T handler, PlayerInventory playerInventory, Text title, Identifier texture) {
        super(handler, playerInventory, title);
        this.texture = texture;
    }

    protected void setup() {
    }

    @Override
    protected void init() {
        super.init();
        this.setup();
        ((ForgingScreenHandler)this.handler).addListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        ((ForgingScreenHandler)this.handler).removeListener(this);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.x, this.y, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        this.drawInvalidRecipeArrow(context, this.x, this.y);
    }

    protected abstract void drawInvalidRecipeArrow(DrawContext var1, int var2, int var3);

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
    }
}

