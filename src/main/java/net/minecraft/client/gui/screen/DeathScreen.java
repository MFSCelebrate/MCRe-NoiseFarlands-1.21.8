/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DeathScreen
extends Screen {
    final static private Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla("icon/draft_report");
    private int ticksSinceDeath;
    final private Text message;
    final private boolean isHardcore;
    private Text scoreText;
    final private List<ButtonWidget> buttons = Lists.newArrayList();
    @Nullable
    private ButtonWidget titleScreenButton;

    public DeathScreen(@Nullable Text message, boolean isHardcore) {
        super(Text.translatable(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
        this.message = message;
        this.isHardcore = isHardcore;
    }

    @Override
    protected void init() {
        this.ticksSinceDeath = 0;
        this.buttons.clear();
        MutableText text = this.isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn");
        this.buttons.add(this.addDrawableChild(ButtonWidget.builder(text, button -> {
            this.client.player.requestRespawn();
            button.active = false;
        }).dimensions(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
        this.titleScreenButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("deathScreen.titleScreen"), button -> this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::onTitleScreenButtonClicked, true)).dimensions(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build());
        this.buttons.add(this.titleScreenButton);
        this.setButtonsActive(false);
        this.scoreText = Text.translatable("deathScreen.score.value", Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void onTitleScreenButtonClicked() {
        if (this.isHardcore) {
            this.quitLevel();
            return;
        }
        TitleScreenConfirmScreen confirmScreen = new TitleScreenConfirmScreen(confirmed -> {
            if (confirmed) {
                this.quitLevel();
            } else {
                this.client.player.requestRespawn();
                this.client.setScreen(null);
            }
        }, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
        this.client.setScreen(confirmScreen);
        confirmScreen.disableButtons(20);
    }

    private void quitLevel() {
        if (this.client.world != null) {
            this.client.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
        }
        this.client.disconnectWithSavingScreen();
        this.client.setScreen(new TitleScreen());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(2.0f, 2.0f);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2 / 2, 30, Colors.WHITE);
        context.getMatrices().popMatrix();
        if (this.message != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.message, this.width / 2, 85, Colors.WHITE);
        }
        context.drawCenteredTextWithShadow(this.textRenderer, this.scoreText, this.width / 2, 100, Colors.WHITE);
        if (this.message != null && mouseY > 85 && mouseY < 85 + this.textRenderer.fontHeight) {
            Style style = this.getTextComponentUnderMouse(mouseX);
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
        }
        if (this.titleScreenButton != null && this.client.getAbuseReportContext().hasDraft()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_ICON_TEXTURE, this.titleScreenButton.getX() + this.titleScreenButton.getWidth() - 17, this.titleScreenButton.getY() + 3, 15, 15);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        DeathScreen.fillBackgroundGradient(context, this.width, this.height);
    }

    static void fillBackgroundGradient(DrawContext context, int width, int height) {
        context.fillGradient(0, 0, width, height, 0x60500000, -1602211792);
    }

    @Nullable
    private Style getTextComponentUnderMouse(int mouseX) {
        if (this.message == null) {
            return null;
        }
        int i = this.client.textRenderer.getWidth(this.message);
        int j = this.width / 2 - i / 2;
        int k = this.width / 2 + i / 2;
        if (mouseX < j || mouseX > k) {
            return null;
        }
        return this.client.textRenderer.getTextHandler().getStyleAt(this.message, mouseX - j);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClickEvent clickEvent;
        Style style;
        if (this.message != null && mouseY > 85.0 && mouseY < (double)(85 + this.textRenderer.fontHeight) && (style = this.getTextComponentUnderMouse((int)mouseX)) != null && (clickEvent = style.getClickEvent()) instanceof ClickEvent.OpenUrl) {
            ClickEvent.OpenUrl openUrl = (ClickEvent.OpenUrl)clickEvent;
            return DeathScreen.handleOpenUri(this.client, this, openUrl.uri());
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ++this.ticksSinceDeath;
        if (this.ticksSinceDeath == 20) {
            this.setButtonsActive(true);
        }
    }

    private void setButtonsActive(boolean active) {
        for (ButtonWidget buttonWidget : this.buttons) {
            buttonWidget.active = active;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class TitleScreenConfirmScreen
    extends ConfirmScreen {
        public TitleScreenConfirmScreen(BooleanConsumer booleanConsumer, Text text, Text text2, Text text3, Text text4) {
            super(booleanConsumer, text, text2, text3, text4);
        }

        @Override
        public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            DeathScreen.fillBackgroundGradient(context, this.width, this.height);
        }
    }
}

