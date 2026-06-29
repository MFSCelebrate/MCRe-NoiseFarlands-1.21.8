/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.exception.RealmsUploadException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.SwitchSlotTask;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.client.realms.util.RealmsUploader;
import net.minecraft.client.realms.util.UploadProgress;
import net.minecraft.client.realms.util.UploadProgressTracker;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public class RealmsUploadScreen
extends RealmsScreen
implements UploadProgressTracker {
    final static private int field_41776 = 200;
    final static private int field_41773 = 80;
    final static private int field_41774 = 95;
    final static private int field_41775 = 1;
    final static private String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    final static private Text VERIFYING_TEXT = Text.translatable("mco.upload.verifying");
    final private RealmsCreateWorldScreen parent;
    final private LevelSummary selectedLevel;
    @Nullable
    final private WorldCreationTask creationTask;
    final private long worldId;
    final private int slotId;
    final AtomicReference<RealmsUploader> uploader = new AtomicReference();
    final private UploadProgress uploadProgress;
    final private RateLimiter narrationRateLimiter;
    @Nullable
    private volatile Text[] statusTexts;
    private volatile Text status = Text.translatable("mco.upload.preparing");
    @Nullable
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    @Nullable
    private ButtonWidget backButton;
    @Nullable
    private ButtonWidget cancelButton;
    private int animTick;
    final private ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

    public RealmsUploadScreen(@Nullable WorldCreationTask creationTask, long worldId, int slotId, RealmsCreateWorldScreen parent, LevelSummary selectedLevel) {
        super(NarratorManager.EMPTY);
        this.creationTask = creationTask;
        this.worldId = worldId;
        this.slotId = slotId;
        this.parent = parent;
        this.selectedLevel = selectedLevel;
        this.uploadProgress = new UploadProgress();
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
    }

    @Override
    public void init() {
        this.backButton = this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.onBack()).build());
        this.backButton.visible = false;
        this.cancelButton = this.layout.addFooter(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.onCancel()).build());
        if (!this.uploadStarted) {
            if (this.parent.slot == -1) {
                this.uploadStarted = true;
                this.upload();
            } else {
                ArrayList<LongRunningTask> list = new ArrayList<LongRunningTask>();
                if (this.creationTask != null) {
                    list.add(this.creationTask);
                }
                list.add(new SwitchSlotTask(this.worldId, this.parent.slot, () -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.client.execute(() -> {
                            this.client.setScreen(this);
                            this.upload();
                        });
                    }
                }));
                this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, list.toArray(new LongRunningTask[0])));
            }
        }
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
    }

    private void onBack() {
        this.client.setScreen(new RealmsConfigureWorldScreen(new RealmsMainScreen(new TitleScreen()), this.worldId));
    }

    private void onCancel() {
        this.cancelled = true;
        RealmsUploader realmsUploader = this.uploader.get();
        if (realmsUploader != null) {
            realmsUploader.cancel();
        } else {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Text[] texts;
        super.render(context, mouseX, mouseY, deltaTicks);
        if (!this.uploadFinished && this.uploadProgress.hasWrittenBytes() && this.uploadProgress.hasWrittenAllBytes() && this.cancelButton != null) {
            this.status = VERIFYING_TEXT;
            this.cancelButton.active = false;
        }
        context.drawCenteredTextWithShadow(this.textRenderer, this.status, this.width / 2, 50, Colors.WHITE);
        if (this.showDots) {
            context.drawTextWithShadow(this.textRenderer, DOTS[this.animTick / 10 % DOTS.length], this.width / 2 + this.textRenderer.getWidth(this.status) / 2 + 5, 50, Colors.WHITE);
        }
        if (this.uploadProgress.hasWrittenBytes() && !this.cancelled) {
            this.drawProgressBar(context);
            this.drawUploadSpeed(context);
        }
        if ((texts = this.statusTexts) != null) {
            for (int i = 0; i < texts.length; ++i) {
                context.drawCenteredTextWithShadow(this.textRenderer, texts[i], this.width / 2, 110 + 12 * i, Colors.RED);
            }
        }
    }

    private void drawProgressBar(DrawContext context) {
        double d = this.uploadProgress.getFractionBytesWritten();
        this.progress = String.format(Locale.ROOT, "%.1f", d * 100.0);
        int i = (this.width - 200) / 2;
        int j = i + (int)Math.round(200.0 * d);
        context.fill(i - 1, 79, j + 1, 96, Colors.WHITE);
        context.fill(i, 80, j, 95, Colors.GRAY);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("mco.upload.percent", this.progress), this.width / 2, 84, Colors.WHITE);
    }

    private void drawUploadSpeed(DrawContext context) {
        this.drawUploadSpeed0(context, this.uploadProgress.getBytesPerSecond());
    }

    private void drawUploadSpeed0(DrawContext context, long bytesPerSecond) {
        String string = this.progress;
        if (bytesPerSecond > 0L && string != null) {
            int i = this.textRenderer.getWidth(string);
            String string2 = "(" + SizeUnit.getUserFriendlyString(bytesPerSecond) + "/s)";
            context.drawTextWithShadow(this.textRenderer, string2, this.width / 2 + i / 2 + 15, 84, Colors.WHITE);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        this.uploadProgress.tick();
        if (this.narrationRateLimiter.tryAcquire(1)) {
            Text text = this.getNarration();
            this.client.getNarratorManager().narrateSystemImmediately(text);
        }
    }

    private Text getNarration() {
        Text[] texts;
        ArrayList list = Lists.newArrayList();
        list.add(this.status);
        if (this.progress != null) {
            list.add(Text.translatable("mco.upload.percent", this.progress));
        }
        if ((texts = this.statusTexts) != null) {
            list.addAll(Arrays.asList(texts));
        }
        return ScreenTexts.joinLines(list);
    }

    private void upload() {
        RealmsWorldOptions realmsWorldOptions;
        RealmsSlot realmsSlot;
        Path path = this.client.runDirectory.toPath().resolve("saves").resolve(this.selectedLevel.getName());
        RealmsUploader realmsUploader = new RealmsUploader(path, realmsSlot = new RealmsSlot(this.slotId, realmsWorldOptions = RealmsWorldOptions.create(this.selectedLevel.getLevelInfo(), true, this.selectedLevel.getVersionInfo().getVersionName()), List.of(RealmsSettingDto.ofHardcore(this.selectedLevel.getLevelInfo().isHardcore()))), this.client.getSession(), this.worldId, this);
        if (!this.uploader.compareAndSet(null, realmsUploader)) {
            throw new IllegalStateException("Tried to start uploading but was already uploading");
        }
        realmsUploader.upload().handleAsync((v, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof CompletionException) {
                    CompletionException completionException = (CompletionException)throwable;
                    throwable = completionException.getCause();
                }
                if (throwable instanceof RealmsUploadException) {
                    RealmsUploadException realmsUploadException = (RealmsUploadException)throwable;
                    if (realmsUploadException.getStatus() != null) {
                        this.status = realmsUploadException.getStatus();
                    }
                    this.setStatusTexts(realmsUploadException.getStatusTexts());
                } else {
                    this.status = Text.translatable("mco.upload.failed", throwable.getMessage());
                }
            } else {
                this.status = Text.translatable("mco.upload.done");
                if (this.backButton != null) {
                    this.backButton.setMessage(ScreenTexts.DONE);
                }
            }
            this.uploadFinished = true;
            this.showDots = false;
            if (this.backButton != null) {
                this.backButton.visible = true;
            }
            if (this.cancelButton != null) {
                this.cancelButton.visible = false;
            }
            this.uploader.set(null);
            return null;
        }, (Executor)this.client);
    }

    private void setStatusTexts(Text ... statusTexts) {
        this.statusTexts = statusTexts;
    }

    @Override
    public UploadProgress getUploadProgress() {
        return this.uploadProgress;
    }

    @Override
    public void updateProgressDisplay() {
        this.status = Text.translatable("mco.upload.uploading", this.selectedLevel.getDisplayName());
    }
}

