/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class SizeUnit
extends Enum<SizeUnit> {
    final static public SizeUnit B = new SizeUnit();
    final static public SizeUnit KB = new SizeUnit();
    final static public SizeUnit MB = new SizeUnit();
    final static public SizeUnit GB = new SizeUnit();
    final static private int BASE = 1024;
    final static private SizeUnit[] field_20204;

    public static SizeUnit[] values() {
        return (SizeUnit[])field_20204.clone();
    }

    public static SizeUnit valueOf(String name) {
        return Enum.valueOf(SizeUnit.class, name);
    }

    public static SizeUnit getLargestUnit(long bytes) {
        if (bytes < 1024L) {
            return B;
        }
        try {
            int i = (int)(Math.log(bytes) / Math.log(1024.0));
            String string = String.valueOf("KMGTPE".charAt(i - 1));
            return SizeUnit.valueOf(string + "B");
        }
        catch (Exception exception) {
            return GB;
        }
    }

    public static double convertToUnit(long bytes, SizeUnit unit) {
        if (unit == B) {
            return bytes;
        }
        return (double)bytes / Math.pow(1024.0, unit.ordinal());
    }

    public static String getUserFriendlyString(long bytes) {
        int i = 1024;
        if (bytes < 1024L) {
            return bytes + " B";
        }
        int j = (int)(Math.log(bytes) / Math.log(1024.0));
        String string = "" + "KMGTPE".charAt(j - 1);
        return String.format(Locale.ROOT, "%.1f %sB", (double)bytes / Math.pow(1024.0, j), string);
    }

    public static String humanReadableSize(long bytes, SizeUnit unit) {
        return String.format(Locale.ROOT, "%." + (unit == GB ? "1" : "0") + "f %s", SizeUnit.convertToUnit(bytes, unit), unit.name());
    }

    private static SizeUnit[] method_36844() {
        return new SizeUnit[]{B, KB, MB, GB};
    }

    static {
        field_20204 = SizeUnit.method_36844();
    }
}

