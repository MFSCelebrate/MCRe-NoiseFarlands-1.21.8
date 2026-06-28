/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.NetworkUtils;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OpenToLanScreen
extends Screen {
    final static private int MIN_PORT = 1024;
    final static private int MAX_PORT = 65535;
    final static private Text ALLOW_COMMANDS_TEXT = Text.translatable("selectWorld.allowCommands");
    final static private Text GAME_MODE_TEXT = Text.translatable("selectWorld.gameMode");
    final static private Text OTHER_PLAYERS_TEXT = Text.translatable("lanServer.otherPlayers");
    final static private Text PORT_TEXT = Text.translatable("lanServer.port");
    final static private Text UNAVAILABLE_PORT_TEXT = Text.translatable("lanServer.port.unavailable", 1024, 65535);
    final static private Text INVALID_PORT_TEXT = Text.translatable("lanServer.port.invalid", 1024, 65535);
    final static private int ERROR_TEXT_COLOR = -43691;
    final private Screen parent;
    private GameMode gameMode = GameMode.SURVIVAL;
    private boolean allowCommands;
    private int port = NetworkUtils.findLocalPort();
    @Nullable
    private TextFieldWidget portField;

    public OpenToLanScreen(Screen screen) {
        super(Text.translatable("lanServer.title"));
        this.parent = screen;
    }

    @Override
    protected void init() {
        IntegratedServer integratedServer = this.client.getServer();
        this.gameMode = integratedServer.getDefaultGameMode();
        this.allowCommands = integratedServer.getSaveProperties().areCommandsAllowed();
        this.addDrawableChild(CyclingButtonWidget.builder(GameMode::getSimpleTranslatableName).values((GameMode[])new GameMode[]{GameMode.SURVIVAL, GameMode.SPECTATOR, GameMode.CREATIVE, GameMode.ADVENTURE}).initially(this.gameMode).build(this.width / 2 - 155, 100, 150, 20, GAME_MODE_TEXT, (button, gameMode) -> {
            this.gameMode = gameMode;
        }));
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.allowCommands).build(this.width / 2 + 5, 100, 150, 20, ALLOW_COMMANDS_TEXT, (button, allowCommands) -> {
            this.allowCommands = allowCommands;
        }));
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("lanServer.start"), button -> {
            this.client.setScreen(null);
            MutableText text = integratedServer.openToLan(this.gameMode, this.allowCommands, this.port) ? PublishCommand.getStartedText(this.port) : Text.translatable("commands.publish.failed");
            this.client.inGameHud.getChatHud().addMessage(text);
            this.client.getNarratorManager().narrateSystemMessage(text);
            this.client.updateWindowTitle();
        }).dimensions(this.width / 2 - 155, this.height - 28, 150, 20).build();
        this.portField = new TextFieldWidget(this.textRenderer, this.width / 2 - 75, 160, 150, 20, Text.translatable("lanServer.port"));
        this.portField.setChangedListener(portText -> {
            Text text = this.updatePort((String)portText);
            this.portField.setPlaceholder(Text.literal("" + this.port).formatted(Formatting.DARK_GRAY));
            if (text == null) {
                this.portField.setEditableColor(-2039584);
                this.portField.setTooltip(null);
                buttonWidget.active = true;
            } else {
                this.portField.setEditableColor(-43691);
                this.portField.setTooltip(Tooltip.of(text));
                buttonWidget.active = false;
            }
        });
        this.portField.setPlaceholder(Text.literal("" + this.port).formatted(Formatting.DARK_GRAY));
        this.addDrawableChild(this.portField);
        this.addDrawableChild(buttonWidget);
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions(this.width / 2 + 5, this.height - 28, 150, 20).build());
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Nullable
    private Text updatePort(String portText) {
        if (portText.isBlank()) {
            this.port = NetworkUtils.findLocalPort();
            return null;
        }
        try {
            this.port = Integer.parseInt(portText);
            if (this.port < 1024 || this.port > 65535) {
                return INVALID_PORT_TEXT;
            }
            if (!NetworkUtils.isPortAvailable(this.port)) {
                return UNAVAILABLE_PORT_TEXT;
            }
            return null;
        }
        catch (NumberFormatException numberFormatException) {
            this.port = NetworkUtils.findLocalPort();
            return INVALID_PORT_TEXT;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 50, Colors.WHITE);
        context.drawCenteredTextWithShadow(this.textRenderer, OTHER_PLAYERS_TEXT, this.width / 2, 82, Colors.WHITE);
        context.drawCenteredTextWithShadow(this.textRenderer, PORT_TEXT, this.width / 2, 142, Colors.WHITE);
    }
}

