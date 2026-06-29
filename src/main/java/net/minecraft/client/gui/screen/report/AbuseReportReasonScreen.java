/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Nullables;
import net.minecraft.util.Urls;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AbuseReportReasonScreen
extends Screen {
    final static private Text TITLE_TEXT = Text.translatable("gui.abuseReport.reason.title");
    final static private Text DESCRIPTION_TEXT = Text.translatable("gui.abuseReport.reason.description");
    final static private Text READ_INFO_TEXT = Text.translatable("gui.abuseReport.read_info");
    final static private int field_49546 = 320;
    final static private int field_49547 = 62;
    final static private int TOP_MARGIN = 4;
    @Nullable
    final private Screen parent;
    @Nullable
    private ReasonListWidget reasonList;
    @Nullable
    AbuseReportReason reason;
    final private Consumer<AbuseReportReason> reasonConsumer;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    final AbuseReportType reportType;

    public AbuseReportReasonScreen(@Nullable Screen parent, @Nullable AbuseReportReason reason, AbuseReportType reportType, Consumer<AbuseReportReason> reasonConsumer) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.reason = reason;
        this.reasonConsumer = reasonConsumer;
        this.reportType = reportType;
    }

    @Override
    protected void init() {
        this.layout.addHeader(TITLE_TEXT, this.textRenderer);
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(DirectionalLayoutWidget.vertical().spacing(4));
        this.reasonList = directionalLayoutWidget.add(new ReasonListWidget(this.client));
        ReasonListWidget.ReasonEntry reasonEntry = Nullables.map(this.reason, this.reasonList::getEntry);
        this.reasonList.setSelected(reasonEntry);
        directionalLayoutWidget.add(EmptyWidget.ofHeight(this.getHeight()));
        DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add(ButtonWidget.builder(READ_INFO_TEXT, ConfirmLinkScreen.opening((Screen)this, Urls.ABOUT_JAVA_REPORTING)).build());
        directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
            if (reasonEntry != null) {
                this.reasonConsumer.accept(reasonEntry.getReason());
            }
            this.client.setScreen(this.parent);
        }).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.reasonList != null) {
            this.reasonList.position(this.width, this.getReasonListHeight(), this.layout.getHeaderHeight());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.fill(this.getLeft(), this.getTop(), this.getRight(), this.getBottom(), Colors.BLACK);
        context.drawBorder(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight(), Colors.WHITE);
        context.drawTextWithShadow(this.textRenderer, DESCRIPTION_TEXT, this.getLeft() + 4, this.getTop() + 4, Colors.WHITE);
        ReasonListWidget.ReasonEntry reasonEntry = (ReasonListWidget.ReasonEntry)this.reasonList.getSelectedOrNull();
        if (reasonEntry != null) {
            int i = this.getLeft() + 4 + 16;
            int j = this.getRight() - 4;
            int k = this.getTop() + 4 + this.textRenderer.fontHeight + 2;
            int l = this.getBottom() - 4;
            int m = j - i;
            int n = l - k;
            int o = this.textRenderer.getWrappedLinesHeight(reasonEntry.reason.getDescription(), m);
            context.drawWrappedTextWithShadow(this.textRenderer, reasonEntry.reason.getDescription(), i, k + (n - o) / 2, m, -1);
        }
    }

    private int getLeft() {
        return (this.width - 320) / 2;
    }

    private int getRight() {
        return (this.width + 320) / 2;
    }

    private int getTop() {
        return this.getBottom() - this.getHeight();
    }

    private int getBottom() {
        return this.height - this.layout.getFooterHeight() - 4;
    }

    private int getWidth() {
        return 320;
    }

    private int getHeight() {
        return 62;
    }

    int getReasonListHeight() {
        return this.layout.getContentHeight() - this.getHeight() - 8;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Environment(value=EnvType.CLIENT)
    public class ReasonListWidget
    extends AlwaysSelectedEntryListWidget<ReasonEntry> {
        public ReasonListWidget(MinecraftClient client) {
            super(client, AbuseReportReasonScreen.this.width, AbuseReportReasonScreen.this.getReasonListHeight(), AbuseReportReasonScreen.this.layout.getHeaderHeight(), 18);
            for (AbuseReportReason abuseReportReason : AbuseReportReason.values()) {
                if (AbuseReportReason.getExcludedReasonsForType(AbuseReportReasonScreen.this.reportType).contains((Object)abuseReportReason)) continue;
                this.addEntry(new ReasonEntry(abuseReportReason));
            }
        }

        @Nullable
        public ReasonEntry getEntry(AbuseReportReason reason) {
            return this.children().stream().filter(entry -> entry.reason == reason).findFirst().orElse(null);
        }

        @Override
        public int getRowWidth() {
            return 320;
        }

        @Override
        public void setSelected(@Nullable ReasonEntry reasonEntry) {
            super.setSelected(reasonEntry);
            AbuseReportReasonScreen.this.reason = reasonEntry != null ? reasonEntry.getReason() : null;
        }

        @Environment(value=EnvType.CLIENT)
        public class ReasonEntry
        extends AlwaysSelectedEntryListWidget.Entry<ReasonEntry> {
            final AbuseReportReason reason;

            public ReasonEntry(AbuseReportReason reason) {
                this.reason = reason;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
                int i = x + 1;
                int j = y + (entryHeight - ((AbuseReportReasonScreen)AbuseReportReasonScreen.this).textRenderer.fontHeight) / 2 + 1;
                context.drawTextWithShadow(AbuseReportReasonScreen.this.textRenderer, this.reason.getText(), i, j, Colors.WHITE);
            }

            @Override
            public Text getNarration() {
                return Text.translatable("gui.abuseReport.reason.narration", this.reason.getText(), this.reason.getDescription());
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                ReasonListWidget.this.setSelected(this);
                return super.mouseClicked(mouseX, mouseY, button);
            }

            public AbuseReportReason getReason() {
                return this.reason;
            }
        }
    }
}

