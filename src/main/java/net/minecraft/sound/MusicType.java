/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.sound;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class MusicType {
    final static private int MENU_MIN_DELAY = 20;
    final static private int MENU_MAX_DELAY = 600;
    final static private int GAME_MIN_DELAY = 12000;
    final static private int GAME_MAX_DELAY = 24000;
    final static private int END_MIN_DELAY = 6000;
    final static public MusicSound MENU = new MusicSound(SoundEvents.MUSIC_MENU, 20, 600, true);
    final static public MusicSound CREATIVE = new MusicSound(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
    final static public MusicSound CREDITS = new MusicSound(SoundEvents.MUSIC_CREDITS, 0, 0, true);
    final static public MusicSound DRAGON = new MusicSound(SoundEvents.MUSIC_DRAGON, 0, 0, true);
    final static public MusicSound END = new MusicSound(SoundEvents.MUSIC_END, 6000, 24000, true);
    final static public MusicSound UNDERWATER = MusicType.createIngameMusic(SoundEvents.MUSIC_UNDER_WATER);
    final static public MusicSound GAME = MusicType.createIngameMusic(SoundEvents.MUSIC_GAME);

    public static MusicSound createIngameMusic(RegistryEntry<SoundEvent> sound) {
        return new MusicSound(sound, 12000, 24000, false);
    }
}

