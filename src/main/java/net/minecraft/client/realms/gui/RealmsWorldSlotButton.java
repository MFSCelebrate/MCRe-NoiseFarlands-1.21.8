/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldSlotButton
extends ButtonWidget {
    final static private Identifier SLOT_FRAME = Identifier.ofVanilla("widget/slot_frame");
    final static public Identifier EMPTY_FRAME = Identifier.ofVanilla("textures/gui/realms/empty_frame.png");
    final static public Identifier PANORAMA_0 = Identifier.ofVanilla("textures/gui/title/background/panorama_0.png");
    final static public Identifier PANORAMA_2 = Identifier.ofVanilla("textures/gui/title/background/panorama_2.png");
    final static public Identifier PANORAMA_3 = Identifier.ofVanilla("textures/gui/title/background/panorama_3.png");
    final static private Text MINIGAME_TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip.minigame");
    final static private Text TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip");
    final static Text MINIGAME_SLOT_NAME = Text.translatable("mco.worldSlot.minigame");
    final static private int MAX_DISPLAYED_SLOT_NAME_LENGTH = 64;
    final static private String ELLIPSIS = "...";
    final private int slotIndex;
    private State state;

    public RealmsWorldSlotButton(int x, int y, int width, int height, int slotIndex, RealmsServer server, ButtonWidget.PressAction onPress) {
        super(x, y, width, height, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.slotIndex = slotIndex;
        this.state = this.setServer(server);
    }

    public State getState() {
        return this.state;
    }

    public State setServer(RealmsServer server) {
        this.state = new State(server, this.slotIndex);
        this.updateTooltip(this.state, server.minigameName);
        return this.state;
    }

    private void updateTooltip(State state, @Nullable String minigameName) {
        Text text;
        switch (state.action.ordinal()) {
            case 1: {
                Text text2;
                if (state.minigame) {
                    text2 = MINIGAME_TOOLTIP;
                    break;
                }
                text2 = TOOLTIP;
                break;
            }
            default: {
                Text text2 = text = null;
            }
        }
        if (text != null) {
            this.setTooltip(Tooltip.of(text));
        }
        MutableText mutableText = Text.literal(state.slotName);
        if (state.minigame && minigameName != null) {
            mutableText = mutableText.append(ScreenTexts.SPACE).append(minigameName);
        }
        this.setMessage(mutableText);
    }

    static Action getAction(RealmsServer server, boolean active, boolean minigame) {
        if (!(minigame || active && server.expired)) {
            return Action.SWITCH_SLOT;
        }
        return Action.NOTHING;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Object string;
        TextRenderer textRenderer;
        int i = this.getX();
        int j = this.getY();
        boolean bl = this.isSelected();
        Identifier identifier = this.state.minigame ? RealmsTextureManager.getTextureId(String.valueOf(this.state.imageId), this.state.image) : (this.state.empty ? EMPTY_FRAME : (this.state.image != null && this.state.imageId != -1L ? RealmsTextureManager.getTextureId(String.valueOf(this.state.imageId), this.state.image) : (this.slotIndex == 1 ? PANORAMA_0 : (this.slotIndex == 2 ? PANORAMA_2 : (this.slotIndex == 3 ? PANORAMA_3 : EMPTY_FRAME)))));
        int k = -1;
        if (!this.state.active) {
            k = ColorHelper.fromFloats(1.0f, 0.56f, 0.56f, 0.56f);
        }
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, i + 1, j + 1, 0.0f, 0.0f, this.width - 2, this.height - 2, 74, 74, 74, 74, k);
        if (bl && this.state.action != Action.NOTHING) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME, i, j, this.width, this.height);
        } else if (this.state.active) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME, i, j, this.width, this.height, ColorHelper.fromFloats(1.0f, 0.8f, 0.8f, 0.8f));
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_FRAME, i, j, this.width, this.height, ColorHelper.fromFloats(1.0f, 0.56f, 0.56f, 0.56f));
        }
        if (this.state.hardcore) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, RealmsMainScreen.HARDCORE_ICON_TEXTURE, i + 3, j + 4, 9, 8);
        }
        if ((textRenderer = MinecraftClient.getInstance().textRenderer).getWidth((String)(string = this.state.slotName)) > 64) {
            string = textRenderer.trimToWidth((String)string, 64 - textRenderer.getWidth(ELLIPSIS)) + ELLIPSIS;
        }
        context.drawCenteredTextWithShadow(textRenderer, (String)string, i + this.width / 2, j + this.height - 14, Colors.WHITE);
        if (this.state.active) {
            context.drawCenteredTextWithShadow(textRenderer, RealmsMainScreen.getVersionText(this.state.version, this.state.compatibility.isCompatible()), i + this.width / 2, j + this.height + 2, Colors.WHITE);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class State {
        final String slotName;
        final String version;
        final RealmsServer.Compatibility compatibility;
        final long imageId;
        @Nullable
        final String image;
        final public boolean empty;
        final public boolean minigame;
        final public Action action;
        final public boolean hardcore;
        final public boolean active;

        public State(RealmsServer server, int slot) {
            boolean bl = this.minigame = slot == 4;
            if (this.minigame) {
                this.slotName = MINIGAME_SLOT_NAME.getString();
                this.imageId = server.minigameId;
                this.image = server.minigameImage;
                this.empty = server.minigameId == -1;
                this.version = "";
                this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
                this.hardcore = false;
                this.active = server.isMinigame();
            } else {
                RealmsSlot realmsSlot = server.slots.get(slot);
                this.slotName = realmsSlot.options.getSlotName(slot);
                this.imageId = realmsSlot.options.templateId;
                this.image = realmsSlot.options.templateImage;
                this.empty = realmsSlot.options.empty;
                this.version = realmsSlot.options.version;
                this.compatibility = realmsSlot.options.compatibility;
                this.hardcore = realmsSlot.isHardcore();
                this.active = server.activeSlot == slot && !server.isMinigame();
            }
            this.action = RealmsWorldSlotButton.getAction(server, this.minigame, this.active);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Action
    extends Enum<Action> {
        final static public Action NOTHING = new Action();
        final static public Action SWITCH_SLOT = new Action();
        final static private Action[] field_19681;

        public static Action[] values() {
            return (Action[])field_19681.clone();
        }

        public static Action valueOf(String name) {
            return Enum.valueOf(Action.class, name);
        }

        private static Action[] method_36853() {
            return new Action[]{NOTHING, SWITCH_SLOT};
        }

        static {
            field_19681 = Action.method_36853();
        }
    }
}

