/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TaskScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.util.TextifiedException;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class ReportScreen<B extends AbuseReport.Builder<?>>
extends Screen {
    final static private Text REPORT_SENT_MESSAGE_TEXT = Text.translatable("gui.abuseReport.report_sent_msg");
    final static private Text SENDING_TITLE_TEXT = Text.translatable("gui.abuseReport.sending.title").formatted(Formatting.BOLD);
    final static private Text SENT_TITLE_TEXT = Text.translatable("gui.abuseReport.sent.title").formatted(Formatting.BOLD);
    final static private Text ERROR_TITLE_TEXT = Text.translatable("gui.abuseReport.error.title").formatted(Formatting.BOLD);
    final static private Text GENERIC_ERROR_TEXT = Text.translatable("gui.abuseReport.send.generic_error");
    final static protected Text SEND_TEXT = Text.translatable("gui.abuseReport.send");
    final static protected Text OBSERVED_WHAT_TEXT = Text.translatable("gui.abuseReport.observed_what");
    final static protected Text SELECT_REASON_TEXT = Text.translatable("gui.abuseReport.select_reason");
    final static private Text DESCRIBE_TEXT = Text.translatable("gui.abuseReport.describe");
    final static protected Text MORE_COMMENTS_TEXT = Text.translatable("gui.abuseReport.more_comments");
    final static private Text COMMENTS_TEXT = Text.translatable("gui.abuseReport.comments");
    final static private Text ATTESTATION_TEXT = Text.translatable("gui.abuseReport.attestation");
    final static protected int field_52303 = 120;
    final static protected int field_46016 = 20;
    final static protected int field_46017 = 280;
    final static protected int field_46018 = 8;
    final static private Logger LOGGER = LogUtils.getLogger();
    final protected Screen parent;
    final protected AbuseReportContext context;
    final protected DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(8);
    protected B reportBuilder;
    private CheckboxWidget checkbox;
    protected ButtonWidget sendButton;

    protected ReportScreen(Text title, Screen parent, AbuseReportContext context, B reportBuilder) {
        super(title);
        this.parent = parent;
        this.context = context;
        this.reportBuilder = reportBuilder;
    }

    protected EditBoxWidget createCommentsBox(int width, int height, Consumer<String> changeListener) {
        AbuseReportLimits abuseReportLimits = this.context.getSender().getLimits();
        EditBoxWidget editBoxWidget = EditBoxWidget.builder().placeholder(DESCRIBE_TEXT).build(this.textRenderer, width, height, COMMENTS_TEXT);
        editBoxWidget.setText(((AbuseReport.Builder)this.reportBuilder).getOpinionComments());
        editBoxWidget.setMaxLength(abuseReportLimits.maxOpinionCommentsLength());
        editBoxWidget.setChangeListener(changeListener);
        return editBoxWidget;
    }

    @Override
    protected void init() {
        this.layout.getMainPositioner().alignHorizontalCenter();
        this.addTitle();
        this.addContent();
        this.addAttestationCheckboxAndSendButton();
        this.onChange();
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void addTitle() {
        this.layout.add(new TextWidget(this.title, this.textRenderer));
    }

    protected abstract void addContent();

    protected void addAttestationCheckboxAndSendButton() {
        this.checkbox = this.layout.add(CheckboxWidget.builder(ATTESTATION_TEXT, this.textRenderer).checked(((AbuseReport.Builder)this.reportBuilder).isAttested()).maxWidth(280).callback((checkbox, attested) -> {
            ((AbuseReport.Builder)this.reportBuilder).setAttested(attested);
            this.onChange();
        }).build());
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.add(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).width(120).build());
        this.sendButton = directionalLayoutWidget.add(ButtonWidget.builder(SEND_TEXT, button -> this.trySend()).width(120).build());
    }

    protected void onChange() {
        AbuseReport.ValidationError validationError = ((AbuseReport.Builder)this.reportBuilder).validate();
        this.sendButton.active = validationError == null && this.checkbox.isChecked();
        this.sendButton.setTooltip(Nullables.map(validationError, AbuseReport.ValidationError::createTooltip));
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
    }

    protected void trySend() {
        ((AbuseReport.Builder)this.reportBuilder).build(this.context).ifLeft(reportWithId -> {
            CompletableFuture<Unit> completableFuture = this.context.getSender().send(reportWithId.id(), reportWithId.reportType(), reportWithId.report());
            this.client.setScreen(TaskScreen.createRunningScreen(SENDING_TITLE_TEXT, ScreenTexts.CANCEL, () -> {
                this.client.setScreen(this);
                completableFuture.cancel(true);
            }));
            completableFuture.handleAsync((v, throwable) -> {
                if (throwable == null) {
                    this.onSent();
                } else {
                    if (throwable instanceof CancellationException) {
                        return null;
                    }
                    this.onSendError((Throwable)throwable);
                }
                return null;
            }, (Executor)this.client);
        }).ifRight(validationError -> this.showError(validationError.message()));
    }

    private void onSent() {
        this.resetDraft();
        this.client.setScreen(TaskScreen.createResultScreen(SENT_TITLE_TEXT, REPORT_SENT_MESSAGE_TEXT, ScreenTexts.DONE, () -> this.client.setScreen(null)));
    }

    private void onSendError(Throwable error) {
        Text text;
        LOGGER.error("Encountered error while sending abuse report", error);
        Throwable throwable = error.getCause();
        if (throwable instanceof TextifiedException) {
            TextifiedException textifiedException = (TextifiedException)throwable;
            text = textifiedException.getMessageText();
        } else {
            text = GENERIC_ERROR_TEXT;
        }
        this.showError(text);
    }

    private void showError(Text errorMessage) {
        MutableText text = errorMessage.copy().formatted(Formatting.RED);
        this.client.setScreen(TaskScreen.createResultScreen(ERROR_TITLE_TEXT, text, ScreenTexts.BACK, () -> this.client.setScreen(this)));
    }

    void saveDraft() {
        if (((AbuseReport.Builder)this.reportBuilder).hasEnoughInfo()) {
            this.context.setDraft(((AbuseReport)((AbuseReport.Builder)this.reportBuilder).getReport()).net_minecraft_client_session_report_AbuseReport_copy());
        }
    }

    void resetDraft() {
        this.context.setDraft(null);
    }

    @Override
    public void close() {
        if (((AbuseReport.Builder)this.reportBuilder).hasEnoughInfo()) {
            this.client.setScreen(new DiscardWarningScreen());
        } else {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public void removed() {
        this.saveDraft();
        super.removed();
    }

    @Environment(value=EnvType.CLIENT)
    class DiscardWarningScreen
    extends WarningScreen {
        final static private Text TITLE = Text.translatable("gui.abuseReport.discard.title").formatted(Formatting.BOLD);
        final static private Text MESSAGE = Text.translatable("gui.abuseReport.discard.content");
        final static private Text RETURN_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.return");
        final static private Text DRAFT_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.draft");
        final static private Text DISCARD_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.discard");

        protected DiscardWarningScreen() {
            super(TITLE, MESSAGE, MESSAGE);
        }

        @Override
        protected LayoutWidget getLayout() {
            DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(8);
            directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
            DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
            directionalLayoutWidget2.add(ButtonWidget.builder(RETURN_BUTTON_TEXT, button -> this.close()).build());
            directionalLayoutWidget2.add(ButtonWidget.builder(DRAFT_BUTTON_TEXT, button -> {
                ReportScreen.this.saveDraft();
                this.client.setScreen(ReportScreen.this.parent);
            }).build());
            directionalLayoutWidget.add(ButtonWidget.builder(DISCARD_BUTTON_TEXT, button -> {
                ReportScreen.this.resetDraft();
                this.client.setScreen(ReportScreen.this.parent);
            }).build());
            return directionalLayoutWidget;
        }

        @Override
        public void close() {
            this.client.setScreen(ReportScreen.this);
        }

        @Override
        public boolean shouldCloseOnEsc() {
            return false;
        }
    }
}

