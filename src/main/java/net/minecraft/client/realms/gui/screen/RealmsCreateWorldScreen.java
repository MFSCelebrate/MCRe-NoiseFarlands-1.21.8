/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsSelectFileToUploadScreen;
import net.minecraft.client.realms.gui.screen.RealmsSelectWorldTemplateScreen;
import net.minecraft.client.realms.gui.screen.RealmsWorldCreating;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.ResettingWorldTemplateTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsCreateWorldScreen
extends RealmsScreen {
    final static Logger LOGGER = LogUtils.getLogger();
    final static private Text CREATE_REALM_TITLE = Text.translatable("mco.selectServer.create");
    final static private Text CREATE_REALM_SUBTITLE = Text.translatable("mco.selectServer.create.subtitle");
    final static private Text CREATE_WORLD_TITLE = Text.translatable("mco.configure.world.switch.slot");
    final static private Text CREATE_WORLD_SUBTITLE = Text.translatable("mco.configure.world.switch.slot.subtitle");
    final static private Text NEW_WORLD_BUTTON_TEXT = Text.translatable("mco.reset.world.generate");
    final static private Text RESET_WORLD_TITLE = Text.translatable("mco.reset.world.title");
    final static private Text RESET_WORLD_SUBTITLE = Text.translatable("mco.reset.world.warning");
    final static public Text CREATING_TEXT = Text.translatable("mco.create.world.reset.title");
    final static private Text RESETTING_TEXT = Text.translatable("mco.reset.world.resetting.screen.title");
    final static private Text TEMPLATE_TEXT = Text.translatable("mco.reset.world.template");
    final static private Text ADVENTURE_TEXT = Text.translatable("mco.reset.world.adventure");
    final static private Text EXPERIENCE_TEXT = Text.translatable("mco.reset.world.experience");
    final static private Text INSPIRATION_TEXT = Text.translatable("mco.reset.world.inspiration");
    final private Screen parent;
    final private RealmsServer serverData;
    final private Text subtitle;
    final private int subtitleColor;
    final private Text taskTitle;
    final static private Identifier UPLOAD_TEXTURE = Identifier.ofVanilla("textures/gui/realms/upload.png");
    final static private Identifier ADVENTURE_TEXTURE = Identifier.ofVanilla("textures/gui/realms/adventure.png");
    final static private Identifier SURVIVAL_SPAWN_TEXTURE = Identifier.ofVanilla("textures/gui/realms/survival_spawn.png");
    final static private Identifier NEW_WORLD_TEXTURE = Identifier.ofVanilla("textures/gui/realms/new_world.png");
    final static private Identifier EXPERIENCE_TEXTURE = Identifier.ofVanilla("textures/gui/realms/experience.png");
    final static private Identifier INSPIRATION_TEXTURE = Identifier.ofVanilla("textures/gui/realms/inspiration.png");
    WorldTemplatePaginatedList normalWorldTemplates;
    WorldTemplatePaginatedList adventureWorldTemplates;
    WorldTemplatePaginatedList experienceWorldTemplates;
    WorldTemplatePaginatedList inspirationWorldTemplates;
    final public int slot;
    @Nullable
    final private WorldCreationTask creationTask;
    final private Runnable callback;
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

    private RealmsCreateWorldScreen(Screen parent, RealmsServer serverData, int slot, Text title, Text subtitle, int subtitleColor, Text taskTitle, Runnable callback) {
        this(parent, serverData, slot, title, subtitle, subtitleColor, taskTitle, null, callback);
    }

    public RealmsCreateWorldScreen(Screen parent, RealmsServer serverData, int slot, Text title, Text subtitle, int subtitleColor, Text taskTitle, @Nullable WorldCreationTask creationTask, Runnable callback) {
        super(title);
        this.parent = parent;
        this.serverData = serverData;
        this.slot = slot;
        this.subtitle = subtitle;
        this.subtitleColor = subtitleColor;
        this.taskTitle = taskTitle;
        this.creationTask = creationTask;
        this.callback = callback;
    }

    public static RealmsCreateWorldScreen newRealm(Screen parent, RealmsServer serverData, WorldCreationTask creationTask, Runnable callback) {
        return new RealmsCreateWorldScreen(parent, serverData, serverData.activeSlot, CREATE_REALM_TITLE, CREATE_REALM_SUBTITLE, -6250336, CREATING_TEXT, creationTask, callback);
    }

    public static RealmsCreateWorldScreen newWorld(Screen parent, int slot, RealmsServer serverData, Runnable callback) {
        return new RealmsCreateWorldScreen(parent, serverData, slot, CREATE_WORLD_TITLE, CREATE_WORLD_SUBTITLE, -6250336, CREATING_TEXT, callback);
    }

    public static RealmsCreateWorldScreen resetWorld(Screen parent, RealmsServer serverData, Runnable callback) {
        return new RealmsCreateWorldScreen(parent, serverData, serverData.activeSlot, RESET_WORLD_TITLE, RESET_WORLD_SUBTITLE, -65536, RESETTING_TEXT, callback);
    }

    @Override
    public void init() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical());
        directionalLayoutWidget.getMainPositioner().net_minecraft_client_gui_widget_Positioner_margin(this.textRenderer.fontHeight / 3);
        directionalLayoutWidget.add(new TextWidget(this.title, this.textRenderer), Positioner::alignHorizontalCenter);
        directionalLayoutWidget.add(new TextWidget(this.subtitle, this.textRenderer).net_minecraft_client_gui_widget_TextWidget_setTextColor(this.subtitleColor), Positioner::alignHorizontalCenter);
        new Thread("Realms-reset-world-fetcher"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    WorldTemplatePaginatedList worldTemplatePaginatedList = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
                    WorldTemplatePaginatedList worldTemplatePaginatedList2 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
                    WorldTemplatePaginatedList worldTemplatePaginatedList3 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
                    WorldTemplatePaginatedList worldTemplatePaginatedList4 = realmsClient.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
                    RealmsCreateWorldScreen.this.client.execute(() -> {
                        RealmsCreateWorldScreen.this.normalWorldTemplates = worldTemplatePaginatedList;
                        RealmsCreateWorldScreen.this.adventureWorldTemplates = worldTemplatePaginatedList2;
                        RealmsCreateWorldScreen.this.experienceWorldTemplates = worldTemplatePaginatedList3;
                        RealmsCreateWorldScreen.this.inspirationWorldTemplates = worldTemplatePaginatedList4;
                    });
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't fetch templates in reset world", (Throwable)realmsServiceException);
                }
            }
        }.start();
        GridWidget gridWidget = this.layout.addBody(new GridWidget());
        GridWidget.Adder adder = gridWidget.createAdder(3);
        adder.getMainPositioner().net_minecraft_client_gui_widget_Positioner_marginX(16);
        adder.add(new FrameButton(this.client.textRenderer, NEW_WORLD_BUTTON_TEXT, NEW_WORLD_TEXTURE, button -> RealmsWorldCreating.showCreateWorldScreen(this.client, this.parent, this, this.slot, this.serverData, this.creationTask)));
        adder.add(new FrameButton(this.client.textRenderer, RealmsSelectFileToUploadScreen.TITLE, UPLOAD_TEXTURE, button -> this.client.setScreen(new RealmsSelectFileToUploadScreen(this.creationTask, this.serverData.id, this.slot, this))));
        adder.add(new FrameButton(this.client.textRenderer, TEMPLATE_TEXT, SURVIVAL_SPAWN_TEXTURE, button -> this.client.setScreen(new RealmsSelectWorldTemplateScreen(TEMPLATE_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.NORMAL, this.normalWorldTemplates))));
        adder.add(EmptyWidget.ofHeight(16), 3);
        adder.add(new FrameButton(this.client.textRenderer, ADVENTURE_TEXT, ADVENTURE_TEXTURE, button -> this.client.setScreen(new RealmsSelectWorldTemplateScreen(ADVENTURE_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.ADVENTUREMAP, this.adventureWorldTemplates))));
        adder.add(new FrameButton(this.client.textRenderer, EXPERIENCE_TEXT, EXPERIENCE_TEXTURE, button -> this.client.setScreen(new RealmsSelectWorldTemplateScreen(EXPERIENCE_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.EXPERIENCE, this.experienceWorldTemplates))));
        adder.add(new FrameButton(this.client.textRenderer, INSPIRATION_TEXT, INSPIRATION_TEXTURE, button -> this.client.setScreen(new RealmsSelectWorldTemplateScreen(INSPIRATION_TEXT, this::onSelectWorldTemplate, RealmsServer.WorldType.INSPIRATION, this.inspirationWorldTemplates))));
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
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
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.getTitle(), this.subtitle);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private void onSelectWorldTemplate(@Nullable WorldTemplate template) {
        this.client.setScreen(this);
        if (template != null) {
            this.runTasks(new ResettingWorldTemplateTask(template, this.serverData.id, this.taskTitle, this.callback));
        }
        RealmsMainScreen.resetServerList();
    }

    private void runTasks(LongRunningTask task) {
        ArrayList<LongRunningTask> list = new ArrayList<LongRunningTask>();
        if (this.creationTask != null) {
            list.add(this.creationTask);
        }
        if (this.slot != this.serverData.activeSlot) {
            list.add(new SwitchSlotTask(this.serverData.id, this.slot, () -> {}));
        }
        list.add(task);
        this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, list.toArray(new LongRunningTask[0])));
    }

    @Environment(value=EnvType.CLIENT)
    class FrameButton
    extends ButtonWidget {
        final static private Identifier TEXTURE = Identifier.ofVanilla("widget/slot_frame");
        final static private int SIZE = 60;
        final static private int TEXTURE_MARGIN = 2;
        final static private int TEXTURE_SIZE = 56;
        final private Identifier image;

        FrameButton(TextRenderer textRenderer, Text message, Identifier image, ButtonWidget.PressAction onPress) {
            super(0, 0, 60, 60 + textRenderer.fontHeight, message, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.image = image;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            boolean bl = this.isSelected();
            int i = -1;
            if (bl) {
                i = ColorHelper.fromFloats(1.0f, 0.56f, 0.56f, 0.56f);
            }
            int j = this.getX();
            int k = this.getY();
            context.drawTexture(RenderPipelines.GUI_TEXTURED, this.image, j + 2, k + 2, 0.0f, 0.0f, 56, 56, 56, 56, 56, 56, i);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, j, k, 60, 60, i);
            int l = bl ? Colors.LIGHT_GRAY : Colors.WHITE;
            context.drawCenteredTextWithShadow(RealmsCreateWorldScreen.this.textRenderer, this.getMessage(), j + 28, k - 14, l);
        }
    }
}

