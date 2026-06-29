/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.RootSpectatorCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class SpectatorMenu {
    final static Identifier CLOSE_TEXTURE = Identifier.ofVanilla("spectator/close");
    final static Identifier SCROLL_LEFT_TEXTURE = Identifier.ofVanilla("spectator/scroll_left");
    final static Identifier SCROLL_RIGHT_TEXTURE = Identifier.ofVanilla("spectator/scroll_right");
    final static private SpectatorMenuCommand CLOSE_COMMAND = new CloseSpectatorMenuCommand();
    final static private SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(-1, true);
    final static private SpectatorMenuCommand NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, true);
    final static private SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, false);
    final static private int CLOSE_SLOT = 8;
    final static Text CLOSE_TEXT = Text.translatable("spectatorMenu.close");
    final static Text PREVIOUS_PAGE_TEXT = Text.translatable("spectatorMenu.previous_page");
    final static Text NEXT_PAGE_TEXT = Text.translatable("spectatorMenu.next_page");
    final static public SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand(){

        @Override
        public void use(SpectatorMenu menu) {
        }

        @Override
        public Text getName() {
            return ScreenTexts.EMPTY;
        }

        @Override
        public void renderIcon(DrawContext context, float brightness, float alpha) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };
    final private SpectatorMenuCloseCallback closeCallback;
    private SpectatorMenuCommandGroup currentGroup = new RootSpectatorCommandGroup();
    private int selectedSlot = -1;
    int page;

    public SpectatorMenu(SpectatorMenuCloseCallback closeCallback) {
        this.closeCallback = closeCallback;
    }

    public SpectatorMenuCommand getCommand(int slot) {
        int i = slot + this.page * 6;
        if (this.page > 0 && slot == 0) {
            return PREVIOUS_PAGE_COMMAND;
        }
        if (slot == 7) {
            if (i < this.currentGroup.getCommands().size()) {
                return NEXT_PAGE_COMMAND;
            }
            return DISABLED_NEXT_PAGE_COMMAND;
        }
        if (slot == 8) {
            return CLOSE_COMMAND;
        }
        if (i < 0 || i >= this.currentGroup.getCommands().size()) {
            return BLANK_COMMAND;
        }
        return (SpectatorMenuCommand)MoreObjects.firstNonNull((Object)this.currentGroup.getCommands().get(i), (Object)BLANK_COMMAND);
    }

    public List<SpectatorMenuCommand> getCommands() {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(this.getCommand(i));
        }
        return list;
    }

    public SpectatorMenuCommand getSelectedCommand() {
        return this.getCommand(this.selectedSlot);
    }

    public SpectatorMenuCommandGroup getCurrentGroup() {
        return this.currentGroup;
    }

    public void useCommand(int slot) {
        SpectatorMenuCommand spectatorMenuCommand = this.getCommand(slot);
        if (spectatorMenuCommand != BLANK_COMMAND) {
            if (this.selectedSlot == slot && spectatorMenuCommand.isEnabled()) {
                spectatorMenuCommand.use(this);
            } else {
                this.selectedSlot = slot;
            }
        }
    }

    public void close() {
        this.closeCallback.close(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectElement(SpectatorMenuCommandGroup group) {
        this.currentGroup = group;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorMenuState getCurrentState() {
        return new SpectatorMenuState(this.getCommands(), this.selectedSlot);
    }

    @Environment(value=EnvType.CLIENT)
    static class CloseSpectatorMenuCommand
    implements SpectatorMenuCommand {
        CloseSpectatorMenuCommand() {
        }

        @Override
        public void use(SpectatorMenu menu) {
            menu.close();
        }

        @Override
        public Text getName() {
            return CLOSE_TEXT;
        }

        @Override
        public void renderIcon(DrawContext context, float brightness, float alpha) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CLOSE_TEXTURE, 0, 0, 16, 16, ColorHelper.fromFloats(alpha, brightness, brightness, brightness));
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ChangePageSpectatorMenuCommand
    implements SpectatorMenuCommand {
        final private int direction;
        final private boolean enabled;

        public ChangePageSpectatorMenuCommand(int direction, boolean enabled) {
            this.direction = direction;
            this.enabled = enabled;
        }

        @Override
        public void use(SpectatorMenu menu) {
            menu.page += this.direction;
        }

        @Override
        public Text getName() {
            return this.direction < 0 ? PREVIOUS_PAGE_TEXT : NEXT_PAGE_TEXT;
        }

        @Override
        public void renderIcon(DrawContext context, float brightness, float alpha) {
            int i = ColorHelper.fromFloats(alpha, brightness, brightness, brightness);
            if (this.direction < 0) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLL_LEFT_TEXTURE, 0, 0, 16, 16, i);
            } else {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLL_RIGHT_TEXTURE, 0, 0, 16, 16, i);
            }
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}

