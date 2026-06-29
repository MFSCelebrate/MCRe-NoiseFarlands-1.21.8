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
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PageTurnWidget
extends ButtonWidget {
    final static private Identifier PAGE_FORWARD_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/page_forward_highlighted");
    final static private Identifier PAGE_FORWARD_TEXTURE = Identifier.ofVanilla("widget/page_forward");
    final static private Identifier PAGE_BACKWARD_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/page_backward_highlighted");
    final static private Identifier PAGE_BACKWARD_TEXTURE = Identifier.ofVanilla("widget/page_backward");
    final static private Text NEXT_TEXT = Text.translatable("book.page_button.next");
    final static private Text PREVIOUS_TEXT = Text.translatable("book.page_button.previous");
    final private boolean isNextPageButton;
    final private boolean playPageTurnSound;

    public PageTurnWidget(int x, int y, boolean isNextPageButton, ButtonWidget.PressAction action, boolean playPageTurnSound) {
        super(x, y, 23, 13, isNextPageButton ? NEXT_TEXT : PREVIOUS_TEXT, action, DEFAULT_NARRATION_SUPPLIER);
        this.isNextPageButton = isNextPageButton;
        this.playPageTurnSound = playPageTurnSound;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Identifier identifier = this.isNextPageButton ? (this.isSelected() ? PAGE_FORWARD_HIGHLIGHTED_TEXTURE : PAGE_FORWARD_TEXTURE) : (this.isSelected() ? PAGE_BACKWARD_HIGHLIGHTED_TEXTURE : PAGE_BACKWARD_TEXTURE);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), 23, 13);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (this.playPageTurnSound) {
            soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0f));
        }
    }
}

