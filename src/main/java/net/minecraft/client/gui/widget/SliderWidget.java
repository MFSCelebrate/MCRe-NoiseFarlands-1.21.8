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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public abstract class SliderWidget
extends ClickableWidget {
    final static private Identifier TEXTURE = Identifier.ofVanilla("widget/slider");
    final static private Identifier HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_highlighted");
    final static private Identifier HANDLE_TEXTURE = Identifier.ofVanilla("widget/slider_handle");
    final static private Identifier HANDLE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_handle_highlighted");
    final static protected int field_43054 = 2;
    final static public int field_60708 = 20;
    final static private int field_41790 = 8;
    final static private int field_41789 = 4;
    protected double value;
    private boolean sliderFocused;

    public SliderWidget(int x, int y, int width, int height, Text text, double value) {
        super(x, y, width, height, text);
        this.value = value;
    }

    private Identifier getTexture() {
        if (this.isNarratable() && this.isFocused() && !this.sliderFocused) {
            return HIGHLIGHTED_TEXTURE;
        }
        return TEXTURE;
    }

    private Identifier getHandleTexture() {
        if (this.isNarratable() && (this.hovered || this.sliderFocused)) {
            return HANDLE_HIGHLIGHTED_TEXTURE;
        }
        return HANDLE_TEXTURE;
    }

    @Override
    protected MutableText getNarrationMessage() {
        return Text.translatable("gui.narrate.slider", this.getMessage());
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.slider.usage.focused"));
            } else {
                builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.slider.usage.hovered"));
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ColorHelper.getWhite(this.alpha));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getHandleTexture(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ColorHelper.getWhite(this.alpha));
        int i = ColorHelper.withAlpha(this.alpha, this.active ? Colors.WHITE : Colors.LIGHT_GRAY);
        this.drawScrollableText(context, minecraftClient.textRenderer, 2, i);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.setValueFromMouse(mouseX);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.sliderFocused = false;
            return;
        }
        GuiNavigationType guiNavigationType = MinecraftClient.getInstance().getNavigationType();
        if (guiNavigationType == GuiNavigationType.MOUSE || guiNavigationType == GuiNavigationType.KEYBOARD_TAB) {
            this.sliderFocused = true;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyCodes.isToggle(keyCode)) {
            this.sliderFocused = !this.sliderFocused;
            return true;
        }
        if (this.sliderFocused) {
            boolean bl;
            boolean bl2 = bl = keyCode == GLFW.GLFW_KEY_LEFT;
            if (bl || keyCode == GLFW.GLFW_KEY_RIGHT) {
                float f = bl ? -1.0f : 1.0f;
                this.setValue(this.value + (double)(f / (float)(this.width - 8)));
                return true;
            }
        }
        return false;
    }

    private void setValueFromMouse(double mouseX) {
        this.setValue((mouseX - (double)(this.getX() + 4)) / (double)(this.width - 8));
    }

    private void setValue(double value) {
        double d = this.value;
        this.value = MathHelper.clamp(value, 0.0, 1.0);
        if (d != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        this.setValueFromMouse(mouseX);
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}

