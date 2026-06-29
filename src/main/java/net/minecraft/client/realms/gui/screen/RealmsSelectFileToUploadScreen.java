/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsUploadScreen;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen
extends RealmsScreen {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public Text TITLE = Text.translatable("mco.upload.select.world.title");
    final static private Text LOADING_ERROR_TEXT = Text.translatable("selectWorld.unable_to_load");
    final static Text WORLD_LANG = Text.translatable("selectWorld.world");
    final static private DateFormat DATE_FORMAT = new SimpleDateFormat();
    @Nullable
    final private WorldCreationTask creationTask;
    final private RealmsCreateWorldScreen parent;
    final private long worldId;
    final private int slotId;
    ButtonWidget uploadButton;
    List<LevelSummary> levelList = Lists.newArrayList();
    int selectedWorld = -1;
    WorldSelectionList worldSelectionList;

    public RealmsSelectFileToUploadScreen(@Nullable WorldCreationTask creationTask, long worldId, int slotId, RealmsCreateWorldScreen parent) {
        super(TITLE);
        this.creationTask = creationTask;
        this.parent = parent;
        this.worldId = worldId;
        this.slotId = slotId;
    }

    private void loadLevelList() {
        LevelStorage.LevelList levelList = this.client.getLevelStorage().getLevelList();
        this.levelList = this.client.getLevelStorage().loadSummaries(levelList).join().stream().filter(LevelSummary::isImmediatelyLoadable).collect(Collectors.toList());
        for (LevelSummary levelSummary : this.levelList) {
            this.worldSelectionList.addEntry(levelSummary);
        }
    }

    @Override
    public void init() {
        this.worldSelectionList = this.addDrawableChild(new WorldSelectionList());
        try {
            this.loadLevelList();
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load level list", (Throwable)exception);
            this.client.setScreen(new RealmsGenericErrorScreen(LOADING_ERROR_TEXT, Text.of(exception.getMessage()), this.parent));
            return;
        }
        this.uploadButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.upload.button.name"), button -> this.upload()).dimensions(this.width / 2 - 154, this.height - 32, 153, 20).build());
        this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 6, this.height - 32, 153, 20).build());
        this.addLabel(new RealmsLabel(Text.translatable("mco.upload.select.world.subtitle"), this.width / 2, RealmsSelectFileToUploadScreen.row(-1), -6250336));
        if (this.levelList.isEmpty()) {
            this.addLabel(new RealmsLabel(Text.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, -1));
        }
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
    }

    private void upload() {
        if (this.selectedWorld != -1) {
            LevelSummary levelSummary = this.levelList.get(this.selectedWorld);
            this.client.setScreen(new RealmsUploadScreen(this.creationTask, this.worldId, this.slotId, this.parent, levelSummary));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 13, Colors.WHITE);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    static Text getGameModeName(LevelSummary summary) {
        return summary.getGameMode().getTranslatableName();
    }

    static String getLastPlayed(LevelSummary summary) {
        return DATE_FORMAT.format(new Date(summary.getLastPlayed()));
    }

    @Environment(value=EnvType.CLIENT)
    class WorldSelectionList
    extends AlwaysSelectedEntryListWidget<WorldListEntry> {
        public WorldSelectionList() {
            super(MinecraftClient.getInstance(), RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height - 40 - RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.row(0), 36);
        }

        public void addEntry(LevelSummary summary) {
            this.addEntry(new WorldListEntry(summary));
        }

        @Override
        public void setSelected(@Nullable WorldListEntry worldListEntry) {
            super.setSelected(worldListEntry);
            RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(worldListEntry);
            RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getEntryCount();
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 0.6);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldListEntry
    extends AlwaysSelectedEntryListWidget.Entry<WorldListEntry> {
        final private LevelSummary summary;
        final private String displayName;
        final private Text nameAndLastPlayed;
        final private Text details;

        public WorldListEntry(LevelSummary summary) {
            this.summary = summary;
            this.displayName = summary.getDisplayName();
            this.nameAndLastPlayed = Text.translatable("mco.upload.entry.id", summary.getName(), RealmsSelectFileToUploadScreen.getLastPlayed(summary));
            this.details = summary.getDetails();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            this.renderItem(context, index, x, y);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.summary));
            return super.mouseClicked(mouseX, mouseY, button);
        }

        protected void renderItem(DrawContext context, int index, int x, int y) {
            Object string = this.displayName.isEmpty() ? String.valueOf(WORLD_LANG) + " " + (index + 1) : this.displayName;
            context.drawTextWithShadow(RealmsSelectFileToUploadScreen.this.textRenderer, (String)string, x + 2, y + 1, Colors.WHITE);
            context.drawTextWithShadow(RealmsSelectFileToUploadScreen.this.textRenderer, this.nameAndLastPlayed, x + 2, y + 12, Colors.GRAY);
            context.drawTextWithShadow(RealmsSelectFileToUploadScreen.this.textRenderer, this.details, x + 2, y + 12 + 10, Colors.GRAY);
        }

        @Override
        public Text getNarration() {
            Text text = ScreenTexts.joinLines(Text.literal(this.summary.getDisplayName()), Text.literal(RealmsSelectFileToUploadScreen.getLastPlayed(this.summary)), RealmsSelectFileToUploadScreen.getGameModeName(this.summary));
            return Text.translatable("narrator.select", text);
        }
    }
}

