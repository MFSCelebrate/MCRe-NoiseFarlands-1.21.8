/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public class AnvilScreen
extends ForgingScreen<AnvilScreenHandler> {
    final static private Identifier TEXT_FIELD_TEXTURE = Identifier.ofVanilla("container/anvil/text_field");
    final static private Identifier TEXT_FIELD_DISABLED_TEXTURE = Identifier.ofVanilla("container/anvil/text_field_disabled");
    final static private Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/anvil.png");
    final static private Text TOO_EXPENSIVE_TEXT = Text.translatable("container.repair.expensive");
    private TextFieldWidget nameField;
    final private PlayerEntity player;

    public AnvilScreen(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, TEXTURE);
        this.player = inventory.player;
        this.titleX = 60;
    }

    @Override
    protected void setup() {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.nameField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, Text.translatable("container.repair"));
        this.nameField.setFocusUnlocked(false);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setDrawsBackground(false);
        this.nameField.setMaxLength(50);
        this.nameField.setChangedListener(this::onRenamed);
        this.nameField.setText("");
        this.addDrawableChild(this.nameField);
        this.nameField.setEditable(((AnvilScreenHandler)this.handler).getSlot(0).hasStack());
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.client.player.experienceBarDisplayStartTime = this.client.player.age;
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.nameField);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.nameField.getText();
        this.init(client, width, height);
        this.nameField.setText(string);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.player.closeHandledScreen();
        }
        if (this.nameField.keyPressed(keyCode, scanCode, modifiers) || this.nameField.isActive()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onRenamed(String name) {
        Slot slot = ((AnvilScreenHandler)this.handler).getSlot(0);
        if (!slot.hasStack()) {
            return;
        }
        String string = name;
        if (!slot.getStack().contains(DataComponentTypes.CUSTOM_NAME) && string.equals(slot.getStack().getName().getString())) {
            string = "";
        }
        if (((AnvilScreenHandler)this.handler).setNewItemName(string)) {
            this.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(string));
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        int i = ((AnvilScreenHandler)this.handler).getLevelCost();
        if (i > 0) {
            Text text;
            int j = -8323296;
            if (i >= 40 && !this.client.player.isInCreativeMode()) {
                text = TOO_EXPENSIVE_TEXT;
                j = -40864;
            } else if (!((AnvilScreenHandler)this.handler).getSlot(2).hasStack()) {
                text = null;
            } else {
                text = Text.translatable("container.repair.cost", i);
                if (!((AnvilScreenHandler)this.handler).getSlot(2).canTakeItems(this.player)) {
                    j = -40864;
                }
            }
            if (text != null) {
                int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
                int l = 69;
                context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 0x4F000000);
                context.drawTextWithShadow(this.textRenderer, text, k, 69, j);
            }
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        super.drawBackground(context, deltaTicks, mouseX, mouseY);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ((AnvilScreenHandler)this.handler).getSlot(0).hasStack() ? TEXT_FIELD_TEXTURE : TEXT_FIELD_DISABLED_TEXTURE, this.x + 59, this.y + 20, 110, 16);
    }

    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((((AnvilScreenHandler)this.handler).getSlot(0).hasStack() || ((AnvilScreenHandler)this.handler).getSlot(1).hasStack()) && !((AnvilScreenHandler)this.handler).getSlot(((AnvilScreenHandler)this.handler).getResultSlotIndex()).hasStack()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, x + 99, y + 45, 28, 21);
        }
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        if (slotId == 0) {
            this.nameField.setText(stack.isEmpty() ? "" : stack.getName().getString());
            this.nameField.setEditable(!stack.isEmpty());
            this.setFocused(this.nameField);
        }
    }
}

