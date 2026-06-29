/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.jukebox;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface JukeboxSongs {
    final static public RegistryKey<JukeboxSong> THIRTEEN = JukeboxSongs.of("13");
    final static public RegistryKey<JukeboxSong> CAT = JukeboxSongs.of("cat");
    final static public RegistryKey<JukeboxSong> BLOCKS = JukeboxSongs.of("blocks");
    final static public RegistryKey<JukeboxSong> CHIRP = JukeboxSongs.of("chirp");
    final static public RegistryKey<JukeboxSong> FAR = JukeboxSongs.of("far");
    final static public RegistryKey<JukeboxSong> MALL = JukeboxSongs.of("mall");
    final static public RegistryKey<JukeboxSong> MELLOHI = JukeboxSongs.of("mellohi");
    final static public RegistryKey<JukeboxSong> STAL = JukeboxSongs.of("stal");
    final static public RegistryKey<JukeboxSong> STRAD = JukeboxSongs.of("strad");
    final static public RegistryKey<JukeboxSong> WARD = JukeboxSongs.of("ward");
    final static public RegistryKey<JukeboxSong> ELEVEN = JukeboxSongs.of("11");
    final static public RegistryKey<JukeboxSong> WAIT = JukeboxSongs.of("wait");
    final static public RegistryKey<JukeboxSong> PIGSTEP = JukeboxSongs.of("pigstep");
    final static public RegistryKey<JukeboxSong> OTHERSIDE = JukeboxSongs.of("otherside");
    final static public RegistryKey<JukeboxSong> FIVE = JukeboxSongs.of("5");
    final static public RegistryKey<JukeboxSong> RELIC = JukeboxSongs.of("relic");
    final static public RegistryKey<JukeboxSong> PRECIPICE = JukeboxSongs.of("precipice");
    final static public RegistryKey<JukeboxSong> CREATOR = JukeboxSongs.of("creator");
    final static public RegistryKey<JukeboxSong> CREATOR_MUSIC_BOX = JukeboxSongs.of("creator_music_box");
    final static public RegistryKey<JukeboxSong> TEARS = JukeboxSongs.of("tears");
    final static public RegistryKey<JukeboxSong> LAVA_CHICKEN = JukeboxSongs.of("lava_chicken");

    private static RegistryKey<JukeboxSong> of(String id) {
        return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.ofVanilla(id));
    }

    private static void register(Registerable<JukeboxSong> registry, RegistryKey<JukeboxSong> key, RegistryEntry.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        registry.register(key, new JukeboxSong(soundEvent, Text.translatable(Util.createTranslationKey("jukebox_song", key.getValue())), lengthInSeconds, comparatorOutput));
    }

    public static void bootstrap(Registerable<JukeboxSong> registry) {
        JukeboxSongs.register(registry, THIRTEEN, SoundEvents.MUSIC_DISC_13, 178, 1);
        JukeboxSongs.register(registry, CAT, SoundEvents.MUSIC_DISC_CAT, 185, 2);
        JukeboxSongs.register(registry, BLOCKS, SoundEvents.MUSIC_DISC_BLOCKS, 345, 3);
        JukeboxSongs.register(registry, CHIRP, SoundEvents.MUSIC_DISC_CHIRP, 185, 4);
        JukeboxSongs.register(registry, FAR, SoundEvents.MUSIC_DISC_FAR, 174, 5);
        JukeboxSongs.register(registry, MALL, SoundEvents.MUSIC_DISC_MALL, 197, 6);
        JukeboxSongs.register(registry, MELLOHI, SoundEvents.MUSIC_DISC_MELLOHI, 96, 7);
        JukeboxSongs.register(registry, STAL, SoundEvents.MUSIC_DISC_STAL, 150, 8);
        JukeboxSongs.register(registry, STRAD, SoundEvents.MUSIC_DISC_STRAD, 188, 9);
        JukeboxSongs.register(registry, WARD, SoundEvents.MUSIC_DISC_WARD, 251, 10);
        JukeboxSongs.register(registry, ELEVEN, SoundEvents.MUSIC_DISC_11, 71, 11);
        JukeboxSongs.register(registry, WAIT, SoundEvents.MUSIC_DISC_WAIT, 238, 12);
        JukeboxSongs.register(registry, PIGSTEP, SoundEvents.MUSIC_DISC_PIGSTEP, 149, 13);
        JukeboxSongs.register(registry, OTHERSIDE, SoundEvents.MUSIC_DISC_OTHERSIDE, 195, 14);
        JukeboxSongs.register(registry, FIVE, SoundEvents.MUSIC_DISC_5, 178, 15);
        JukeboxSongs.register(registry, RELIC, SoundEvents.MUSIC_DISC_RELIC, 218, 14);
        JukeboxSongs.register(registry, PRECIPICE, SoundEvents.MUSIC_DISC_PRECIPICE, 299, 13);
        JukeboxSongs.register(registry, CREATOR, SoundEvents.MUSIC_DISC_CREATOR, 176, 12);
        JukeboxSongs.register(registry, CREATOR_MUSIC_BOX, SoundEvents.MUSIC_DISC_CREATOR_MUSIC_BOX, 73, 11);
        JukeboxSongs.register(registry, TEARS, SoundEvents.MUSIC_DISC_TEARS, 175, 10);
        JukeboxSongs.register(registry, LAVA_CHICKEN, SoundEvents.MUSIC_DISC_LAVA_CHICKEN, 134, 9);
    }
}

