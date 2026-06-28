/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public abstract class ScrollableTextFieldWidget
extends ScrollableWidget {
    final static private ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/text_field"), Identifier.ofVanilla("widget/text_field_highlighted"));
    final static private int field_55261 = 4;
    final static public int field_60867 = 8;
    private boolean hasBackground = true;
    private boolean hasOverlay = true;

    public ScrollableTextFieldWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public ScrollableTextFieldWidget(int x, int y, int width, int height, Text message, boolean hasBackground, boolean hasOverlay) {
        this(x, y, width, height, message);
        this.hasBackground = hasBackground;
        this.hasOverlay = hasOverlay;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = this.checkScrollbarDragged(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button) || bl;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl2;
        boolean bl = keyCode == GLFW.GLFW_KEY_UP;
        boolean bl3 = bl2 = keyCode == GLFW.GLFW_KEY_DOWN;
        if (bl || bl2) {
            double d = this.getScrollY();
            this.setScrollY(this.getScrollY() + (double)(bl ? -1 : 1) * this.getDeltaYPerScroll());
            if (d != this.getScrollY()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!this.visible) {
            return;
        }
        if (this.hasBackground) {
            this.drawBox(context);
        }
        context.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1);
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(0.0f, (float)(-this.getScrollY()));
        this.renderContents(context, mouseX, mouseY, deltaTicks);
        context.getMatrices().popMatrix();
        context.disableScissor();
        this.drawScrollbar(context);
        if (this.hasOverlay) {
            this.renderOverlay(context);
        }
    }

    protected void renderOverlay(DrawContext context) {
    }

    protected int getTextMargin() {
        return 4;
    }

    protected int getPadding() {
        return this.getTextMargin() * 2;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getRight() + 6) && mouseY < (double)this.getBottom();
    }

    @Override
    protected int getScrollbarX() {
        return this.getRight();
    }

    @Override
    protected int getContentsHeightWithPadding() {
        return this.getContentsHeight() + this.getPadding();
    }

    protected void drawBox(DrawContext context) {
        this.draw(context, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    protected void draw(DrawContext context, int x, int y, int width, int height) {
        Identifier identifier = TEXTURES.get(this.isNarratable(), this.isFocused());
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x, y, width, height);
    }

    protected boolean isVisible(int textTop, int textBottom) {
        return (double)textBottom - this.getScrollY() >= (double)this.getY() && (double)textTop - this.getScrollY() <= (double)(this.getY() + this.height);
    }

    protected abstract int getContentsHeight();

    protected abstract void renderContents(DrawContext var1, int var2, int var3, float var4);

    protected int getTextX() {
        return this.getX() + this.getTextMargin();
    }

    protected int getTextY() {
        return this.getY() + this.getTextMargin();
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }
}

