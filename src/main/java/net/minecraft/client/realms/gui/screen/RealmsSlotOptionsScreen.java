/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class RealmsSlotOptionsScreen
extends RealmsScreen {
    final static private int field_32125 = 2;
    final static public List<Difficulty> DIFFICULTIES = ImmutableList.of((Object)Difficulty.PEACEFUL, (Object)Difficulty.EASY, (Object)Difficulty.NORMAL, (Object)Difficulty.HARD);
    final static private int field_32126 = 0;
    final static public List<GameMode> GAME_MODES = ImmutableList.of((Object)GameMode.SURVIVAL, (Object)GameMode.CREATIVE, (Object)GameMode.ADVENTURE);
    final static private Text EDIT_SLOT_NAME = Text.translatable("mco.configure.world.edit.slot.name");
    final static Text SPAWN_PROTECTION = Text.translatable("mco.configure.world.spawnProtection");
    private TextFieldWidget nameEdit;
    final protected RealmsConfigureWorldScreen parent;
    private int column1_x;
    private int column2_x;
    final private RealmsSlot slot;
    final private RealmsServer.WorldType worldType;
    private Difficulty difficulty;
    private GameMode gameMode;
    final private String defaultSlotName;
    private String slotName;
    private boolean pvp;
    private boolean spawnMonsters;
    int spawnProtection;
    private boolean commandBlocks;
    private boolean forceGameMode;
    SettingsSlider spawnProtectionButton;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen parent, RealmsSlot slot, RealmsServer.WorldType worldType, int activeSlot) {
        super(Text.translatable("mco.configure.world.buttons.options"));
        this.parent = parent;
        this.slot = slot;
        this.worldType = worldType;
        this.difficulty = RealmsSlotOptionsScreen.get(DIFFICULTIES, slot.options.difficulty, 2);
        this.gameMode = RealmsSlotOptionsScreen.get(GAME_MODES, slot.options.gameMode, 0);
        this.defaultSlotName = slot.options.getDefaultSlotName(activeSlot);
        this.setSlotName(slot.options.getSlotName(activeSlot));
        if (worldType == RealmsServer.WorldType.NORMAL) {
            this.pvp = slot.options.pvp;
            this.spawnProtection = slot.options.spawnProtection;
            this.forceGameMode = slot.options.forceGameMode;
            this.spawnMonsters = slot.options.spawnMonsters;
            this.commandBlocks = slot.options.commandBlocks;
        } else {
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnMonsters = true;
            this.commandBlocks = true;
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private static <T> T get(List<T> list, int index, int fallbackIndex) {
        try {
            return list.get(index);
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return list.get(fallbackIndex);
        }
    }

    private static <T> int indexOf(List<T> list, T value, int fallbackIndex) {
        int i = list.indexOf(value);
        return 1 == -1 ? fallbackIndex : 1;
    }

    @Override
    public void init() {
        this.column2_x = 170;
        this.column1_x = this.width / 2 - this.column2_x;
        int i = this.width / 2 + 10;
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            MutableText text = this.worldType == RealmsServer.WorldType.ADVENTUREMAP ? Text.translatable("mco.configure.world.edit.subscreen.adventuremap") : (this.worldType == RealmsServer.WorldType.INSPIRATION ? Text.translatable("mco.configure.world.edit.subscreen.inspiration") : Text.translatable("mco.configure.world.edit.subscreen.experience"));
            this.addLabel(new RealmsLabel(text, this.width / 2, 26, -65536));
        }
        this.nameEdit = this.addSelectableChild(new TextFieldWidget(this.client.textRenderer, this.column1_x, RealmsSlotOptionsScreen.row(1), this.column2_x, 20, null, Text.translatable("mco.configure.world.edit.slot.name")));
        this.nameEdit.setText(this.slotName);
        this.nameEdit.setChangedListener(this::setSlotName);
        CyclingButtonWidget<Boolean> cyclingButtonWidget = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.pvp).build(i, RealmsSlotOptionsScreen.row(1), this.column2_x, 20, Text.translatable("mco.configure.world.pvp"), (button, pvp) -> {
            this.pvp = pvp;
        }));
        CyclingButtonWidget<GameMode> cyclingButtonWidget2 = this.addDrawableChild(CyclingButtonWidget.builder(GameMode::getSimpleTranslatableName).values((Collection<GameMode>)GAME_MODES).initially(this.gameMode).build(this.column1_x, RealmsSlotOptionsScreen.row(3), this.column2_x, 20, Text.translatable("selectWorld.gameMode"), (button, gameModeIndex) -> {
            this.gameMode = gameModeIndex;
        }));
        this.spawnProtectionButton = this.addDrawableChild(new SettingsSlider(i, RealmsSlotOptionsScreen.row(3), this.column2_x, this.spawnProtection, 0.0f, 16.0f));
        MutableText text2 = Text.translatable("mco.configure.world.spawn_toggle.message");
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = CyclingButtonWidget.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).build(i, RealmsSlotOptionsScreen.row(5), this.column2_x, 20, Text.translatable("mco.configure.world.spawnMonsters"), this.getSpawnToggleButtonCallback(text2, spawnMonsters -> {
            this.spawnMonsters = spawnMonsters;
        }));
        CyclingButtonWidget<Difficulty> cyclingButtonWidget4 = this.addDrawableChild(CyclingButtonWidget.builder(Difficulty::getTranslatableName).values((Collection<Difficulty>)DIFFICULTIES).initially(this.difficulty).build(this.column1_x, RealmsSlotOptionsScreen.row(5), this.column2_x, 20, Text.translatable("options.difficulty"), (button, difficulty) -> {
            this.difficulty = difficulty;
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                boolean bl;
                cyclingButtonWidget.active = bl = this.difficulty != Difficulty.PEACEFUL;
                cyclingButtonWidget3.setValue(bl && this.spawnMonsters);
            }
        }));
        this.addDrawableChild(cyclingButtonWidget3);
        CyclingButtonWidget<Boolean> cyclingButtonWidget5 = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.forceGameMode).build(this.column1_x, RealmsSlotOptionsScreen.row(7), this.column2_x, 20, Text.translatable("mco.configure.world.forceGameMode"), (button, forceGameMode) -> {
            this.forceGameMode = forceGameMode;
        }));
        CyclingButtonWidget<Boolean> cyclingButtonWidget6 = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.commandBlocks).build(i, RealmsSlotOptionsScreen.row(7), this.column2_x, 20, Text.translatable("mco.configure.world.commandBlocks"), (button, commandBlocks) -> {
            this.commandBlocks = commandBlocks;
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            cyclingButtonWidget.active = false;
            cyclingButtonWidget3.active = false;
            this.spawnProtectionButton.active = false;
            cyclingButtonWidget5.active = false;
        }
        if (this.difficulty == Difficulty.PEACEFUL) {
            cyclingButtonWidget3.active = false;
        }
        if (this.slot.isHardcore()) {
            cyclingButtonWidget6.active = false;
            cyclingButtonWidget4.active = false;
            cyclingButtonWidget2.active = false;
            cyclingButtonWidget3.active = false;
            cyclingButtonWidget5.active = false;
        }
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.done"), button -> this.saveSettings()).dimensions(this.column1_x, RealmsSlotOptionsScreen.row(13), this.column2_x, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions(i, RealmsSlotOptionsScreen.row(13), this.column2_x, 20).build());
    }

    private CyclingButtonWidget.UpdateCallback<Boolean> getSpawnToggleButtonCallback(Text text, Consumer<Boolean> valueSetter) {
        return (button, value) -> {
            if (value.booleanValue()) {
                valueSetter.accept(true);
            } else {
                this.client.setScreen(RealmsPopups.createContinuableWarningPopup(this, text, popup -> {
                    valueSetter.accept(false);
                    popup.close();
                }));
            }
        };
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, Colors.WHITE);
        context.drawTextWithShadow(this.textRenderer, EDIT_SLOT_NAME, this.column1_x + this.column2_x / 2 - this.textRenderer.getWidth(EDIT_SLOT_NAME) / 2, RealmsSlotOptionsScreen.row(0) - 5, Colors.WHITE);
        this.nameEdit.render(context, mouseX, mouseY, deltaTicks);
    }

    private void setSlotName(String slotName) {
        this.slotName = slotName.equals(this.defaultSlotName) ? "" : slotName;
    }

    private void saveSettings() {
        int i = RealmsSlotOptionsScreen.indexOf(DIFFICULTIES, this.difficulty, 2);
        int j = RealmsSlotOptionsScreen.indexOf(GAME_MODES, this.gameMode, 0);
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.slot.options.pvp, this.slot.options.spawnMonsters, this.slot.options.spawnProtection, this.slot.options.commandBlocks, i, j, this.slot.options.forceGameMode, this.slotName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        } else {
            boolean bl = this.worldType == RealmsServer.WorldType.NORMAL && this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters;
            this.parent.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.pvp, bl, this.spawnProtection, this.commandBlocks, i, j, this.forceGameMode, this.slotName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SettingsSlider
    extends SliderWidget {
        final private double min;
        final private double max;

        public SettingsSlider(int x, int y, int width, int value, float min, float max) {
            super(x, y, width, 20, ScreenTexts.EMPTY, 0.0);
            this.min = min;
            this.max = max;
            this.value = (MathHelper.clamp((float)value, min, max) - min) / (max - min);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(ScreenTexts.composeGenericOptionText(SPAWN_PROTECTION, RealmsSlotOptionsScreen.this.spawnProtection == 0 ? ScreenTexts.OFF : Text.literal(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection))));
        }
    }
}

