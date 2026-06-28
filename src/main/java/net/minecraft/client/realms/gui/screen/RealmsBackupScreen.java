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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsBackupInfoScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.DownloadTask;
import net.minecraft.client.realms.task.RestoreTask;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsBackupScreen
extends RealmsScreen {
    final static Logger LOGGER = LogUtils.getLogger();
    final static private Text BACKUPS_TEXT = Text.translatable("mco.configure.world.backup");
    final static Text RESTORE_TEXT = Text.translatable("mco.backup.button.restore");
    final static Text CHANGES_TOOLTIP = Text.translatable("mco.backup.changes.tooltip");
    final static private Text NO_BACKUPS_TEXT = Text.translatable("mco.backup.nobackups");
    final static private Text DOWNLOAD_TEXT = Text.translatable("mco.backup.button.download");
    final static private String UPLOADED = "uploaded";
    final static private int field_49447 = 8;
    final RealmsConfigureWorldScreen parent;
    List<Backup> backups = Collections.emptyList();
    @Nullable
    BackupObjectSelectionList selectionList;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    final private int slotId;
    @Nullable
    ButtonWidget downloadButton;
    final RealmsServer serverData;
    boolean noBackups = false;

    public RealmsBackupScreen(RealmsConfigureWorldScreen parent, RealmsServer serverData, int slotId) {
        super(BACKUPS_TEXT);
        this.parent = parent;
        this.serverData = serverData;
        this.slotId = slotId;
    }

    @Override
    public void init() {
        this.layout.addHeader(BACKUPS_TEXT, this.textRenderer);
        this.selectionList = this.layout.addBody(new BackupObjectSelectionList(this));
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        this.downloadButton = directionalLayoutWidget.add(ButtonWidget.builder(DOWNLOAD_TEXT, button -> this.downloadClicked()).build());
        this.downloadButton.active = false;
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(element -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(element);
        });
        this.refreshWidgetPositions();
        this.startBackupFetcher();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (this.noBackups && this.selectionList != null) {
            context.drawTextWithShadow(this.textRenderer, NO_BACKUPS_TEXT, this.width / 2 - this.textRenderer.getWidth(NO_BACKUPS_TEXT) / 2, this.selectionList.getY() + this.selectionList.getHeight() / 2 - this.textRenderer.fontHeight / 2, Colors.WHITE);
        }
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.selectionList != null) {
            this.selectionList.position(this.width, this.layout);
        }
    }

    private void startBackupFetcher() {
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient realmsClient = RealmsClient.create();
                try {
                    List<Backup> list = realmsClient.backupsFor((long)RealmsBackupScreen.this.serverData.id).backups;
                    RealmsBackupScreen.this.client.execute(() -> {
                        RealmsBackupScreen.this.backups = list;
                        RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                        if (!RealmsBackupScreen.this.noBackups && RealmsBackupScreen.this.downloadButton != null) {
                            RealmsBackupScreen.this.downloadButton.active = true;
                        }
                        if (RealmsBackupScreen.this.selectionList != null) {
                            RealmsBackupScreen.this.selectionList.replaceEntries(RealmsBackupScreen.this.backups.stream().map(backup -> new BackupObjectSelectionListEntry((Backup)backup)).toList());
                        }
                    });
                }
                catch (RealmsServiceException realmsServiceException) {
                    LOGGER.error("Couldn't request backups", (Throwable)realmsServiceException);
                }
            }
        }.start();
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    private void downloadClicked() {
        this.client.setScreen(RealmsPopups.createInfoPopup(this, Text.translatable("mco.configure.world.restore.download.question.line1"), button -> this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, Objects.requireNonNullElse(this.serverData.name, "") + " (" + this.serverData.slots.get((Object)Integer.valueOf((int)this.serverData.activeSlot)).options.getSlotName(this.serverData.activeSlot) + ")", this)))));
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionList
    extends ElementListWidget<BackupObjectSelectionListEntry> {
        final static private int field_49450 = 36;

        public BackupObjectSelectionList(RealmsBackupScreen realmsBackupScreen) {
            super(MinecraftClient.getInstance(), realmsBackupScreen.width, realmsBackupScreen.layout.getContentHeight(), realmsBackupScreen.layout.getHeaderHeight(), 36);
        }

        @Override
        public int getRowWidth() {
            return 300;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BackupObjectSelectionListEntry
    extends ElementListWidget.Entry<BackupObjectSelectionListEntry> {
        final static private int field_44525 = 2;
        final private Backup mBackup;
        @Nullable
        private ButtonWidget restoreButton;
        @Nullable
        private ButtonWidget changesButton;
        final private List<ClickableWidget> buttons = new ArrayList<ClickableWidget>();

        public BackupObjectSelectionListEntry(Backup backup) {
            this.mBackup = backup;
            this.updateChangeList(backup);
            if (!backup.changeList.isEmpty()) {
                this.changesButton = ButtonWidget.builder(CHANGES_TOOLTIP, button -> RealmsBackupScreen.this.client.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.mBackup))).width(8 + RealmsBackupScreen.this.textRenderer.getWidth(CHANGES_TOOLTIP)).narrationSupplier(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.backup.narration", this.getLastModifiedDate()), (Text)textSupplier.get())).build();
                this.buttons.add(this.changesButton);
            }
            if (!RealmsBackupScreen.this.serverData.expired) {
                this.restoreButton = ButtonWidget.builder(RESTORE_TEXT, button -> this.restore()).width(8 + RealmsBackupScreen.this.textRenderer.getWidth(CHANGES_TOOLTIP)).narrationSupplier(textSupplier -> ScreenTexts.joinSentences(Text.translatable("mco.backup.narration", this.getLastModifiedDate()), (Text)textSupplier.get())).build();
                this.buttons.add(this.restoreButton);
            }
        }

        private void updateChangeList(Backup backup) {
            int i = RealmsBackupScreen.this.backups.indexOf(backup);
            if (i == RealmsBackupScreen.this.backups.size() - 1) {
                return;
            }
            Backup backup2 = RealmsBackupScreen.this.backups.get(i + 1);
            for (String string : backup.metadata.keySet()) {
                if (!string.contains(RealmsBackupScreen.UPLOADED) && backup2.metadata.containsKey(string)) {
                    if (backup.metadata.get(string).equals(backup2.metadata.get(string))) continue;
                    this.addChange(string);
                    continue;
                }
                this.addChange(string);
            }
        }

        private void addChange(String metadataKey) {
            if (metadataKey.contains(RealmsBackupScreen.UPLOADED)) {
                String string = DateFormat.getDateTimeInstance(3, 3).format(this.mBackup.lastModifiedDate);
                this.mBackup.changeList.put(metadataKey, string);
                this.mBackup.setUploadedVersion(true);
            } else {
                this.mBackup.changeList.put(metadataKey, this.mBackup.metadata.get(metadataKey));
            }
        }

        private String getLastModifiedDate() {
            return DateFormat.getDateTimeInstance(3, 3).format(this.mBackup.lastModifiedDate);
        }

        private void restore() {
            Text text = RealmsUtil.convertToAgePresentation(this.mBackup.lastModifiedDate);
            MutableText text2 = Text.translatable("mco.configure.world.restore.question.line1", this.getLastModifiedDate(), text);
            RealmsBackupScreen.this.client.setScreen(RealmsPopups.createContinuableWarningPopup(RealmsBackupScreen.this, text2, popup -> {
                RealmsConfigureWorldScreen realmsConfigureWorldScreen = RealmsBackupScreen.this.parent.getNewScreen();
                RealmsBackupScreen.this.client.setScreen(new RealmsLongRunningMcoTaskScreen(realmsConfigureWorldScreen, new RestoreTask(this.mBackup, RealmsBackupScreen.this.serverData.id, realmsConfigureWorldScreen)));
            }));
        }

        @Override
        public List<? extends Element> children() {
            return this.buttons;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.buttons;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            int i = y + entryHeight / 2;
            int j = i - ((RealmsBackupScreen)RealmsBackupScreen.this).textRenderer.fontHeight - 2;
            int k = i + 2;
            int l = this.mBackup.isUploadedVersion() ? RealmsScreen.GREEN : Colors.WHITE;
            context.drawTextWithShadow(RealmsBackupScreen.this.textRenderer, Text.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentation(this.mBackup.lastModifiedDate)), x, j, l);
            context.drawTextWithShadow(RealmsBackupScreen.this.textRenderer, this.getMediumDatePresentation(this.mBackup.lastModifiedDate), x, k, RealmsScreen.DARK_GRAY);
            int m = 0;
            int n = y + entryHeight / 2 - 10;
            if (this.restoreButton != null) {
                this.restoreButton.setX(x + entryWidth - (m += this.restoreButton.getWidth() + 8));
                this.restoreButton.setY(n);
                this.restoreButton.render(context, mouseX, mouseY, tickProgress);
            }
            if (this.changesButton != null) {
                this.changesButton.setX(x + entryWidth - (m += this.changesButton.getWidth() + 8));
                this.changesButton.setY(n);
                this.changesButton.render(context, mouseX, mouseY, tickProgress);
            }
        }

        private String getMediumDatePresentation(Date lastModifiedDate) {
            return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
        }
    }
}

