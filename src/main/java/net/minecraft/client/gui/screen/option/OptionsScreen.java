/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OnlineOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.screen.option.TelemetryInfoScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OptionsScreen
extends Screen {
    final static private Text TITLE_TEXT = Text.translatable("options.title");
    final static private Text SKIN_CUSTOMIZATION_TEXT = Text.translatable("options.skinCustomisation");
    final static private Text SOUNDS_TEXT = Text.translatable("options.sounds");
    final static private Text VIDEO_TEXT = Text.translatable("options.video");
    final static private Text CONTROL_TEXT = Text.translatable("options.controls");
    final static private Text LANGUAGE_TEXT = Text.translatable("options.language");
    final static private Text CHAT_TEXT = Text.translatable("options.chat");
    final static private Text RESOURCE_PACK_TEXT = Text.translatable("options.resourcepack");
    final static private Text ACCESSIBILITY_TEXT = Text.translatable("options.accessibility");
    final static private Text TELEMETRY_TEXT = Text.translatable("options.telemetry");
    final static private Tooltip TELEMETRY_DISABLED_TOOLTIP = Tooltip.of(Text.translatable("options.telemetry.disabled"));
    final static private Text CREDITS_AND_ATTRIBUTION_TEXT = Text.translatable("options.credits_and_attribution");
    final static private int COLUMNS = 2;
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 61, 33);
    final private Screen parent;
    final private GameOptions settings;
    @Nullable
    private CyclingButtonWidget<Difficulty> difficultyButton;
    @Nullable
    private LockButtonWidget lockDifficultyButton;

    public OptionsScreen(Screen parent, GameOptions gameOptions) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.settings = gameOptions;
    }

    @Override
    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(8));
        directionalLayoutWidget.add(new TextWidget(TITLE_TEXT, this.textRenderer), Positioner::alignHorizontalCenter);
        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal()).spacing(8);
        directionalLayoutWidget2.add(this.settings.getFov().createWidget(this.client.options));
        directionalLayoutWidget2.add(this.createTopRightButton());
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().net_minecraft_client_gui_widget_Positioner_marginX(4).net_minecraft_client_gui_widget_Positioner_marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);
        adder.add(this.createButton(SKIN_CUSTOMIZATION_TEXT, () -> new SkinOptionsScreen(this, this.settings)));
        adder.add(this.createButton(SOUNDS_TEXT, () -> new SoundOptionsScreen(this, this.settings)));
        adder.add(this.createButton(VIDEO_TEXT, () -> new VideoOptionsScreen((Screen)this, this.client, this.settings)));
        adder.add(this.createButton(CONTROL_TEXT, () -> new ControlsOptionsScreen(this, this.settings)));
        adder.add(this.createButton(LANGUAGE_TEXT, () -> new LanguageOptionsScreen((Screen)this, this.settings, this.client.getLanguageManager())));
        adder.add(this.createButton(CHAT_TEXT, () -> new ChatOptionsScreen(this, this.settings)));
        adder.add(this.createButton(RESOURCE_PACK_TEXT, () -> new PackScreen(this.client.getResourcePackManager(), this::refreshResourcePacks, this.client.getResourcePackDir(), Text.translatable("resourcePack.title"))));
        adder.add(this.createButton(ACCESSIBILITY_TEXT, () -> new AccessibilityOptionsScreen(this, this.settings)));
        ButtonWidget buttonWidget = adder.add(this.createButton(TELEMETRY_TEXT, () -> new TelemetryInfoScreen(this, this.settings)));
        if (!this.client.isTelemetryEnabledByApi()) {
            buttonWidget.active = false;
            buttonWidget.setTooltip(TELEMETRY_DISABLED_TOOLTIP);
        }
        adder.add(this.createButton(CREDITS_AND_ATTRIBUTION_TEXT, () -> new CreditsAndAttributionScreen(this)));
        this.layout.addBody(gridWidget);
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private void refreshResourcePacks(ResourcePackManager resourcePackManager) {
        this.settings.refreshResourcePacks(resourcePackManager);
        this.client.setScreen(this);
    }

    private Widget createTopRightButton() {
        if (this.client.world != null && this.client.isIntegratedServerRunning()) {
            this.difficultyButton = OptionsScreen.createDifficultyButtonWidget(0, 0, "options.difficulty", this.client);
            if (!this.client.world.net_minecraft_client_world_ClientWorld$Properties_getLevelProperties().isHardcore()) {
                this.lockDifficultyButton = new LockButtonWidget(0, 0, button -> this.client.setScreen(new ConfirmScreen(this::lockDifficulty, Text.translatable("difficulty.lock.title"), Text.translatable("difficulty.lock.question", this.client.world.net_minecraft_client_world_ClientWorld$Properties_getLevelProperties().getDifficulty().getTranslatableName()))));
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockDifficultyButton.getWidth());
                this.lockDifficultyButton.setLocked(this.client.world.net_minecraft_client_world_ClientWorld$Properties_getLevelProperties().isDifficultyLocked());
                this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
                this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
                AxisGridWidget axisGridWidget = new AxisGridWidget(150, 0, AxisGridWidget.DisplayAxis.HORIZONTAL);
                axisGridWidget.add(this.difficultyButton);
                axisGridWidget.add(this.lockDifficultyButton);
                return axisGridWidget;
            }
            this.difficultyButton.active = false;
            return this.difficultyButton;
        }
        return ButtonWidget.builder(Text.translatable("options.online"), button -> this.client.setScreen(new OnlineOptionsScreen(this, this.settings))).dimensions(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20).build();
    }

    public static CyclingButtonWidget<Difficulty> createDifficultyButtonWidget(int x, int y, String translationKey, MinecraftClient client) {
        return CyclingButtonWidget.builder(Difficulty::getTranslatableName).values((Difficulty[])Difficulty.values()).initially(client.world.getDifficulty()).build(x, y, 150, 20, Text.translatable(translationKey), (button, difficulty) -> client.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket((Difficulty)difficulty)));
    }

    private void lockDifficulty(boolean difficultyLocked) {
        this.client.setScreen(this);
        if (difficultyLocked && this.client.world != null && this.lockDifficultyButton != null && this.difficultyButton != null) {
            this.client.getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
            this.lockDifficultyButton.setLocked(true);
            this.lockDifficultyButton.active = false;
            this.difficultyButton.active = false;
        }
    }

    @Override
    public void removed() {
        this.settings.write();
    }

    private ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier) {
        return ButtonWidget.builder(message, button -> this.client.setScreen((Screen)screenSupplier.get())).build();
    }
}

