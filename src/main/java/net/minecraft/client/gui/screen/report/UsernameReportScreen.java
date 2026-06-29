/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ReportScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.UsernameAbuseReport;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class UsernameReportScreen
extends ReportScreen<UsernameAbuseReport.Builder> {
    final static private Text TITLE_TEXT = Text.translatable("gui.abuseReport.name.title");
    final static private Text field_52851 = Text.translatable("gui.abuseReport.name.comment_box_label");
    @Nullable
    private EditBoxWidget commentsBox;

    private UsernameReportScreen(Screen parent, AbuseReportContext context, UsernameAbuseReport.Builder reportBuilder) {
        super(TITLE_TEXT, parent, context, reportBuilder);
    }

    public UsernameReportScreen(Screen parent, AbuseReportContext context, UUID reportedPlayerUuid, String username) {
        this(parent, context, new UsernameAbuseReport.Builder(reportedPlayerUuid, username, context.getSender().getLimits()));
    }

    public UsernameReportScreen(Screen parent, AbuseReportContext context, UsernameAbuseReport report) {
        this(parent, context, new UsernameAbuseReport.Builder(report, context.getSender().getLimits()));
    }

    @Override
    protected void addContent() {
        MutableText text = Text.literal(((UsernameAbuseReport)((UsernameAbuseReport.Builder)this.reportBuilder).getReport()).getUsername()).formatted(Formatting.YELLOW);
        this.layout.add(new TextWidget(Text.translatable("gui.abuseReport.name.reporting", text), this.textRenderer), positioner -> positioner.alignHorizontalCenter().net_minecraft_client_gui_widget_Positioner_margin(0, 8));
        this.commentsBox = this.createCommentsBox(280, this.textRenderer.fontHeight * 8, comments -> {
            ((UsernameAbuseReport.Builder)this.reportBuilder).setOpinionComments((String)comments);
            this.onChange();
        });
        this.layout.add(LayoutWidgets.createLabeledWidget(this.textRenderer, this.commentsBox, field_52851, positioner -> positioner.net_minecraft_client_gui_widget_Positioner_marginBottom(12)));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (this.commentsBox != null) {
            return this.commentsBox.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }
}

