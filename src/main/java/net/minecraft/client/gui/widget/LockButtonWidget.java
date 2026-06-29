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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LockButtonWidget
extends ButtonWidget {
    private boolean locked;

    public LockButtonWidget(int x, int y, ButtonWidget.PressAction action) {
        super(x, y, 20, 20, Text.translatable("narrator.button.difficulty_lock"), action, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return ScreenTexts.joinSentences(super.getNarrationMessage(), this.isLocked() ? Text.translatable("narrator.button.difficulty_lock.locked") : Text.translatable("narrator.button.difficulty_lock.unlocked"));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Icon icon = !this.active ? (this.locked ? Icon.LOCKED_DISABLED : Icon.UNLOCKED_DISABLED) : (this.isSelected() ? (this.locked ? Icon.LOCKED_HOVER : Icon.UNLOCKED_HOVER) : (this.locked ? Icon.LOCKED : Icon.UNLOCKED));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, icon.texture, this.getX(), this.getY(), this.width, this.height);
    }

    @Environment(value=EnvType.CLIENT)
    static final class Icon
    extends Enum<Icon> {
        final static public Icon LOCKED = new Icon(Identifier.ofVanilla("widget/locked_button"));
        final static public Icon LOCKED_HOVER = new Icon(Identifier.ofVanilla("widget/locked_button_highlighted"));
        final static public Icon LOCKED_DISABLED = new Icon(Identifier.ofVanilla("widget/locked_button_disabled"));
        final static public Icon UNLOCKED = new Icon(Identifier.ofVanilla("widget/unlocked_button"));
        final static public Icon UNLOCKED_HOVER = new Icon(Identifier.ofVanilla("widget/unlocked_button_highlighted"));
        final static public Icon UNLOCKED_DISABLED = new Icon(Identifier.ofVanilla("widget/unlocked_button_disabled"));
        final Identifier texture;
        final static private Icon[] field_2136;

        public static Icon[] values() {
            return (Icon[])field_2136.clone();
        }

        public static Icon valueOf(String string) {
            return Enum.valueOf(Icon.class, string);
        }

        private Icon(Identifier texture) {
            this.texture = texture;
        }

        private static Icon[] method_36870() {
            return new Icon[]{LOCKED, LOCKED_HOVER, LOCKED_DISABLED, UNLOCKED, UNLOCKED_HOVER, UNLOCKED_DISABLED};
        }

        static {
            field_2136 = Icon.method_36870();
        }
    }
}

