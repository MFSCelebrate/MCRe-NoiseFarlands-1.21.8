/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.session;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class BanReason
extends Enum<BanReason> {
    final static public BanReason GENERIC_VIOLATION = new BanReason("generic_violation");
    final static public BanReason FALSE_REPORTING = new BanReason("false_reporting");
    final static public BanReason HATE_SPEECH = new BanReason("hate_speech");
    final static public BanReason HATE_TERRORISM_NOTORIOUS_FIGURE = new BanReason("hate_terrorism_notorious_figure");
    final static public BanReason HARASSMENT_OR_BULLYING = new BanReason("harassment_or_bullying");
    final static public BanReason DEFAMATION_IMPERSONATION_FALSE_INFORMATION = new BanReason("defamation_impersonation_false_information");
    final static public BanReason DRUGS = new BanReason("drugs");
    final static public BanReason FRAUD = new BanReason("fraud");
    final static public BanReason SPAM_OR_ADVERTISING = new BanReason("spam_or_advertising");
    final static public BanReason NUDITY_OR_PORNOGRAPHY = new BanReason("nudity_or_pornography");
    final static public BanReason SEXUALLY_INAPPROPRIATE = new BanReason("sexually_inappropriate");
    final static public BanReason EXTREME_VIOLENCE_OR_GORE = new BanReason("extreme_violence_or_gore");
    final static public BanReason IMMINENT_HARM_TO_PERSON_OR_PROPERTY = new BanReason("imminent_harm_to_person_or_property");
    final private Text description;
    final static private BanReason[] field_42905;

    public static BanReason[] values() {
        return (BanReason[])field_42905.clone();
    }

    public static BanReason valueOf(String string) {
        return Enum.valueOf(BanReason.class, string);
    }

    private BanReason(String id) {
        this.description = Text.translatable("gui.banned.reason." + id);
    }

    public Text getDescription() {
        return this.description;
    }

    @Nullable
    public static BanReason byId(int id) {
        return switch (id) {
            case 17, 19, 23, 31 -> GENERIC_VIOLATION;
            case 2 -> FALSE_REPORTING;
            case 5 -> HATE_SPEECH;
            case 16, 25 -> HATE_TERRORISM_NOTORIOUS_FIGURE;
            case 21 -> HARASSMENT_OR_BULLYING;
            case 27 -> DEFAMATION_IMPERSONATION_FALSE_INFORMATION;
            case 28 -> DRUGS;
            case 29 -> FRAUD;
            case 30 -> SPAM_OR_ADVERTISING;
            case 32 -> NUDITY_OR_PORNOGRAPHY;
            case 33, 35, 36 -> SEXUALLY_INAPPROPRIATE;
            case 34 -> EXTREME_VIOLENCE_OR_GORE;
            case 53 -> IMMINENT_HARM_TO_PERSON_OR_PROPERTY;
            default -> null;
        };
    }

    private static BanReason[] method_49314() {
        return new BanReason[]{GENERIC_VIOLATION, FALSE_REPORTING, HATE_SPEECH, HATE_TERRORISM_NOTORIOUS_FIGURE, HARASSMENT_OR_BULLYING, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, DRUGS, FRAUD, SPAM_OR_ADVERTISING, NUDITY_OR_PORNOGRAPHY, SEXUALLY_INAPPROPRIATE, EXTREME_VIOLENCE_OR_GORE, IMMINENT_HARM_TO_PERSON_OR_PROPERTY};
    }

    static {
        field_42905 = BanReason.method_49314();
    }
}

