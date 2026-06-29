/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class AbuseReportType
extends Enum<AbuseReportType> {
    final static public AbuseReportType CHAT = new AbuseReportType("chat");
    final static public AbuseReportType SKIN = new AbuseReportType("skin");
    final static public AbuseReportType USERNAME = new AbuseReportType("username");
    final private String name;
    final static private AbuseReportType[] field_46068;

    public static AbuseReportType[] values() {
        return (AbuseReportType[])field_46068.clone();
    }

    public static AbuseReportType valueOf(String string) {
        return Enum.valueOf(AbuseReportType.class, string);
    }

    private AbuseReportType(String name) {
        this.name = name.toUpperCase(Locale.ROOT);
    }

    public String getName() {
        return this.name;
    }

    private static AbuseReportType[] method_53617() {
        return new AbuseReportType[]{CHAT, SKIN, USERNAME};
    }

    static {
        field_46068 = AbuseReportType.method_53617();
    }
}

