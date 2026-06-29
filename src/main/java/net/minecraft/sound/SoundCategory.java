/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.sound;

public final class SoundCategory
extends Enum<SoundCategory> {
    final static public SoundCategory MASTER = new SoundCategory("master");
    final static public SoundCategory MUSIC = new SoundCategory("music");
    final static public SoundCategory RECORDS = new SoundCategory("record");
    final static public SoundCategory WEATHER = new SoundCategory("weather");
    final static public SoundCategory BLOCKS = new SoundCategory("block");
    final static public SoundCategory HOSTILE = new SoundCategory("hostile");
    final static public SoundCategory NEUTRAL = new SoundCategory("neutral");
    final static public SoundCategory PLAYERS = new SoundCategory("player");
    final static public SoundCategory AMBIENT = new SoundCategory("ambient");
    final static public SoundCategory VOICE = new SoundCategory("voice");
    final static public SoundCategory UI = new SoundCategory("ui");
    final private String name;
    final static private SoundCategory[] field_15255;

    public static SoundCategory[] values() {
        return (SoundCategory[])field_15255.clone();
    }

    public static SoundCategory valueOf(String string) {
        return Enum.valueOf(SoundCategory.class, string);
    }

    private SoundCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static SoundCategory[] method_36586() {
        return new SoundCategory[]{MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE, UI};
    }

    static {
        field_15255 = SoundCategory.method_36586();
    }
}

