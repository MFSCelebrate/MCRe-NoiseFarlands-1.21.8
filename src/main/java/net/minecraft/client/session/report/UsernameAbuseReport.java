/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.minecraft.report.ReportedEntity
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.UsernameReportScreen;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class UsernameAbuseReport
extends AbuseReport {
    final private String username;

    UsernameAbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid, String username) {
        super(reportId, currentTime, reportedPlayerUuid);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public UsernameAbuseReport net_minecraft_client_session_report_UsernameAbuseReport_copy() {
        UsernameAbuseReport usernameAbuseReport = new UsernameAbuseReport(this.reportId, this.currentTime, this.reportedPlayerUuid, this.username);
        usernameAbuseReport.opinionComments = this.opinionComments;
        usernameAbuseReport.attested = this.attested;
        return usernameAbuseReport;
    }

    @Override
    public Screen createReportScreen(Screen parent, AbuseReportContext context) {
        return new UsernameReportScreen(parent, context, this);
    }

    @Override
    public AbuseReport net_minecraft_client_session_report_AbuseReport_copy() {
        return this.net_minecraft_client_session_report_UsernameAbuseReport_copy();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder
    extends AbuseReport.Builder<UsernameAbuseReport> {
        public Builder(UsernameAbuseReport report, AbuseReportLimits limits) {
            super(report, limits);
        }

        public Builder(UUID reportedPlayerUuid, String username, AbuseReportLimits limits) {
            super(new UsernameAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, username), limits);
        }

        @Override
        public boolean hasEnoughInfo() {
            return StringUtils.isNotEmpty((CharSequence)this.getOpinionComments());
        }

        @Override
        @Nullable
        public AbuseReport.ValidationError validate() {
            if (((UsernameAbuseReport)this.report).opinionComments.length() > this.limits.maxOpinionCommentsLength()) {
                return AbuseReport.ValidationError.COMMENTS_TOO_LONG;
            }
            return super.validate();
        }

        @Override
        public Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext context) {
            AbuseReport.ValidationError validationError = this.validate();
            if (validationError != null) {
                return Either.right((Object)validationError);
            }
            ReportedEntity reportedEntity = new ReportedEntity(((UsernameAbuseReport)this.report).reportedPlayerUuid);
            com.mojang.authlib.minecraft.report.AbuseReport abuseReport = com.mojang.authlib.minecraft.report.AbuseReport.name((String)((UsernameAbuseReport)this.report).opinionComments, (ReportedEntity)reportedEntity, (Instant)((UsernameAbuseReport)this.report).currentTime);
            return Either.left((Object)new AbuseReport.ReportWithId(((UsernameAbuseReport)this.report).reportId, AbuseReportType.USERNAME, abuseReport));
        }
    }
}

