/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.FileDownload;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsDownloadLatestWorldScreen
extends RealmsScreen {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
    final static private int field_41772 = 200;
    final static private int field_41769 = 80;
    final static private int field_41770 = 95;
    final static private int field_41771 = 1;
    final private Screen parent;
    final private WorldDownload worldDownload;
    final private Text downloadTitle;
    final private RateLimiter narrationRateLimiter;
    private ButtonWidget cancelButton;
    final private String worldName;
    final private DownloadStatus downloadStatus;
    @Nullable
    private volatile Text downloadError;
    private volatile Text status = Text.translatable("mco.download.preparing");
    @Nullable
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots = true;
    private volatile boolean finished;
    private volatile boolean extracting;
    @Nullable
    private Long previousWrittenBytes;
    @Nullable
    private Long previousTimeSnapshot;
    private long bytesPerSecond;
    private int animTick;
    final static private String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private boolean checked;
    final private BooleanConsumer onBack;

    public RealmsDownloadLatestWorldScreen(Screen parent, WorldDownload worldDownload, String worldName, BooleanConsumer onBack) {
        super(NarratorManager.EMPTY);
        this.onBack = onBack;
        this.parent = parent;
        this.worldName = worldName;
        this.worldDownload = worldDownload;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = Text.translatable("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
    }

    @Override
    public void init() {
        this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).dimensions((this.width - 200) / 2, this.height - 42, 200, 20).build());
        this.checkDownloadSize();
    }

    private void checkDownloadSize() {
        if (this.finished || this.checked) {
            return;
        }
        this.checked = true;
        if (this.getContentLength(this.worldDownload.downloadLink) >= 0x140000000L) {
            MutableText text = Text.translatable("mco.download.confirmation.oversized", SizeUnit.getUserFriendlyString(0x140000000L));
            this.client.setScreen(RealmsPopups.createNonContinuableWarningPopup(this, text, popupScreen -> {
                this.client.setScreen(this);
                this.downloadSave();
            }));
        } else {
            this.downloadSave();
        }
    }

    private long getContentLength(String downloadLink) {
        FileDownload fileDownload = new FileDownload();
        return fileDownload.contentLength(downloadLink);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            Text text = this.getNarration();
            this.client.getNarratorManager().narrateSystemImmediately(text);
        }
    }

    private Text getNarration() {
        ArrayList list = Lists.newArrayList();
        list.add(this.downloadTitle);
        list.add(this.status);
        if (this.progress != null) {
            list.add(Text.translatable("mco.download.percent", this.progress));
            list.add(Text.translatable("mco.download.speed.narration", SizeUnit.getUserFriendlyString(this.bytesPerSecond)));
        }
        if (this.downloadError != null) {
            list.add(this.downloadError);
        }
        return ScreenTexts.joinLines(list);
    }

    @Override
    public void close() {
        this.cancelled = true;
        if (this.finished && this.onBack != null && this.downloadError == null) {
            this.onBack.accept(true);
        }
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, this.downloadTitle, this.width / 2, 20, Colors.WHITE);
        context.drawCenteredTextWithShadow(this.textRenderer, this.status, this.width / 2, 50, Colors.WHITE);
        if (this.showDots) {
            this.drawDots(context);
        }
        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar(context);
            this.drawDownloadSpeed(context);
        }
        if (this.downloadError != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, this.downloadError, this.width / 2, 110, Colors.RED);
        }
    }

    private void drawDots(DrawContext context) {
        int i = this.textRenderer.getWidth(this.status);
        if (this.animTick != 0 && this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        context.drawTextWithShadow(this.textRenderer, DOTS[this.dotIndex % DOTS.length], this.width / 2 + i / 2 + 5, 50, Colors.WHITE);
    }

    private void drawProgressBar(DrawContext context) {
        double d = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0);
        this.progress = String.format(Locale.ROOT, "%.1f", d * 100.0);
        int i = (this.width - 200) / 2;
        int j = i + (int)Math.round(200.0 * d);
        context.fill(i - 1, 79, j + 1, 96, Colors.WHITE);
        context.fill(i, 80, j, 95, Colors.GRAY);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("mco.download.percent", this.progress), this.width / 2, 84, Colors.WHITE);
    }

    private void drawDownloadSpeed(DrawContext context) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = Util.getMeasuringTimeMs() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPerSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawDownloadSpeed0(context, this.bytesPerSecond);
            }
            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMeasuringTimeMs();
        } else {
            this.drawDownloadSpeed0(context, this.bytesPerSecond);
        }
    }

    private void drawDownloadSpeed0(DrawContext context, long bytesPerSecond) {
        if (bytesPerSecond > 0L) {
            int i = this.textRenderer.getWidth(this.progress);
            context.drawTextWithShadow(this.textRenderer, Text.translatable("mco.download.speed", SizeUnit.getUserFriendlyString(bytesPerSecond)), this.width / 2 + i / 2 + 15, 84, Colors.WHITE);
        }
    }

    private void downloadSave() {
        new Thread(() -> {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 12[WHILELOOP]
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }).start();
    }

    private void downloadCancelled() {
        this.status = Text.translatable("mco.download.cancelled");
    }

    @Environment(value=EnvType.CLIENT)
    public static class DownloadStatus {
        public volatile long bytesWritten;
        public volatile long totalBytes;
    }
}

