/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public final class AbuseReportReason
extends Enum<AbuseReportReason> {
    final static public AbuseReportReason I_WANT_TO_REPORT_THEM = new AbuseReportReason("i_want_to_report_them");
    final static public AbuseReportReason HATE_SPEECH = new AbuseReportReason("hate_speech");
    final static public AbuseReportReason HARASSMENT_OR_BULLYING = new AbuseReportReason("harassment_or_bullying");
    final static public AbuseReportReason SELF_HARM_OR_SUICIDE = new AbuseReportReason("self_harm_or_suicide");
    final static public AbuseReportReason IMMINENT_HARM = new AbuseReportReason("imminent_harm");
    final static public AbuseReportReason DEFAMATION_IMPERSONATION_FALSE_INFORMATION = new AbuseReportReason("defamation_impersonation_false_information");
    final static public AbuseReportReason ALCOHOL_TOBACCO_DRUGS = new AbuseReportReason("alcohol_tobacco_drugs");
    final static public AbuseReportReason CHILD_SEXUAL_EXPLOITATION_OR_ABUSE = new AbuseReportReason("child_sexual_exploitation_or_abuse");
    final static public AbuseReportReason TERRORISM_OR_VIOLENT_EXTREMISM = new AbuseReportReason("terrorism_or_violent_extremism");
    final static public AbuseReportReason NON_CONSENSUAL_INTIMATE_IMAGERY = new AbuseReportReason("non_consensual_intimate_imagery");
    final static public AbuseReportReason SEXUALLY_INAPPROPRIATE = new AbuseReportReason("sexually_inappropriate");
    final private String id;
    final private Text text;
    final private Text description;
    final static private AbuseReportReason[] field_39674;

    public static AbuseReportReason[] values() {
        return (AbuseReportReason[])field_39674.clone();
    }

    public static AbuseReportReason valueOf(String string) {
        return Enum.valueOf(AbuseReportReason.class, string);
    }

    private AbuseReportReason(String id) {
        this.id = id.toUpperCase(Locale.ROOT);
        String string2 = "gui.abuseReport.reason." + id;
        this.text = Text.translatable(string2);
        this.description = Text.translatable(string2 + ".description");
    }

    public String getId() {
        return this.id;
    }

    public Text getText() {
        return this.text;
    }

    public Text getDescription() {
        return this.description;
    }

    public static List<AbuseReportReason> getExcludedReasonsForType(AbuseReportType reportType) {
        return switch (reportType) {
            case AbuseReportType.CHAT -> List.of(SEXUALLY_INAPPROPRIATE);
            case AbuseReportType.SKIN -> List.of(IMMINENT_HARM, DEFAMATION_IMPERSONATION_FALSE_INFORMATION);
            default -> List.of();
        };
    }

    private static AbuseReportReason[] method_44597() {
        return new AbuseReportReason[]{I_WANT_TO_REPORT_THEM, HATE_SPEECH, HARASSMENT_OR_BULLYING, SELF_HARM_OR_SUICIDE, IMMINENT_HARM, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, ALCOHOL_TOBACCO_DRUGS, CHILD_SEXUAL_EXPLOITATION_OR_ABUSE, TERRORISM_OR_VIOLENT_EXTREMISM, NON_CONSENSUAL_INTIMATE_IMAGERY, SEXUALLY_INAPPROPRIATE};
    }

    static {
        field_39674 = AbuseReportReason.method_44597();
    }
}

