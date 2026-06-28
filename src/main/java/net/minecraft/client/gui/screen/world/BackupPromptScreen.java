/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public class BackupPromptScreen
extends Screen {
    final static private Text SKIP_BUTTON_TEXT = Text.translatable("selectWorld.backupJoinSkipButton");
    final static public Text CONFIRM_BUTTON_TEXT = Text.translatable("selectWorld.backupJoinConfirmButton");
    final private Runnable onCancel;
    final protected Callback callback;
    final private Text subtitle;
    final private boolean showEraseCacheCheckbox;
    private MultilineText wrappedText = MultilineText.EMPTY;
    final Text firstButtonText;
    protected int field_32236;
    private CheckboxWidget eraseCacheCheckbox;

    public BackupPromptScreen(Runnable onCancel, Callback callback, Text title, Text subtitle, boolean showEraseCacheCheckbox) {
        this(onCancel, callback, title, subtitle, CONFIRM_BUTTON_TEXT, showEraseCacheCheckbox);
    }

    public BackupPromptScreen(Runnable onCancel, Callback callback, Text title, Text subtitle, Text firstButtonText, boolean showEraseCacheCheckbox) {
        super(title);
        this.onCancel = onCancel;
        this.callback = callback;
        this.subtitle = subtitle;
        this.showEraseCacheCheckbox = showEraseCacheCheckbox;
        this.firstButtonText = firstButtonText;
    }

    @Override
    protected void init() {
        super.init();
        this.wrappedText = MultilineText.create(this.textRenderer, this.subtitle, this.width - 50);
        int i = (this.wrappedText.count() + 1) * this.textRenderer.fontHeight;
        this.eraseCacheCheckbox = CheckboxWidget.builder(Text.translatable("selectWorld.backupEraseCache"), this.textRenderer).pos(this.width / 2 - 155 + 80, 76 + i).build();
        if (this.showEraseCacheCheckbox) {
            this.addDrawableChild(this.eraseCacheCheckbox);
        }
        this.addDrawableChild(ButtonWidget.builder(this.firstButtonText, button -> this.callback.proceed(true, this.eraseCacheCheckbox.isChecked())).dimensions(this.width / 2 - 155, 100 + i, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(SKIP_BUTTON_TEXT, button -> this.callback.proceed(false, this.eraseCacheCheckbox.isChecked())).dimensions(this.width / 2 - 155 + 160, 100 + i, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCancel.run()).dimensions(this.width / 2 - 155 + 80, 124 + i, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 50, Colors.WHITE);
        this.wrappedText.drawCenterWithShadow(context, this.width / 2, 70);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.onCancel.run();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Callback {
        public void proceed(boolean var1, boolean var2);
    }
}

