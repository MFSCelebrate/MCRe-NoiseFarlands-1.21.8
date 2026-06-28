/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.enums.TestBlockMode;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.packet.c2s.play.SetTestBlockC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TestBlockScreen
extends Screen {
    final static private List<TestBlockMode> MODES = List.of(TestBlockMode.values());
    final static private Text TITLE_TEXT = Text.translatable(Blocks.TEST_BLOCK.getTranslationKey());
    final static private Text MESSAGE_TEXT = Text.translatable("test_block.message");
    final private BlockPos pos;
    private TestBlockMode mode;
    private String message;
    @Nullable
    private TextFieldWidget textField;

    public TestBlockScreen(TestBlockEntity blockEntity) {
        super(TITLE_TEXT);
        this.pos = blockEntity.getPos();
        this.mode = blockEntity.getMode();
        this.message = blockEntity.getMessage();
    }

    @Override
    public void init() {
        this.textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 240, 20, Text.translatable("test_block.message"));
        this.textField.setMaxLength(128);
        this.textField.setText(this.message);
        this.addDrawableChild(this.textField);
        this.setInitialFocus(this.textField);
        this.setMode(this.mode);
        this.addDrawableChild(CyclingButtonWidget.builder(TestBlockMode::getName).values((Collection<TestBlockMode>)MODES).omitKeyText().initially(this.mode).build(this.width / 2 - 4 - 150, 185, 50, 20, TITLE_TEXT, (button, mode) -> this.setMode((TestBlockMode)mode)));
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.onDone()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, Colors.WHITE);
        if (this.mode != TestBlockMode.START) {
            context.drawTextWithShadow(this.textRenderer, MESSAGE_TEXT, this.width / 2 - 153, 70, Colors.LIGHT_GRAY);
        }
        context.drawTextWithShadow(this.textRenderer, this.mode.getInfo(), this.width / 2 - 153, 174, Colors.LIGHT_GRAY);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void onDone() {
        this.message = this.textField.getText();
        this.client.getNetworkHandler().sendPacket(new SetTestBlockC2SPacket(this.pos, this.mode, this.message));
        this.close();
    }

    @Override
    public void close() {
        this.onCancel();
    }

    private void onCancel() {
        this.client.setScreen(null);
    }

    private void setMode(TestBlockMode mode) {
        this.mode = mode;
        this.textField.visible = mode != TestBlockMode.START;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderInGameBackground(context);
    }
}

