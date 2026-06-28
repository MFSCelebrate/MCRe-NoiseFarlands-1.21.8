/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.sound;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MusicTracker {
    final static private int DEFAULT_TIME_UNTIL_NEXT_SONG = 100;
    final private Random random = Random.create();
    final private MinecraftClient client;
    @Nullable
    private SoundInstance current;
    private MusicFrequency musicFrequency;
    private float volume = 1.0f;
    private int timeUntilNextSong = 100;
    private boolean shownToast = false;

    public MusicTracker(MinecraftClient client) {
        this.client = client;
        this.musicFrequency = client.options.getMusicFrequency().getValue();
    }

    public void tick() {
        boolean bl;
        MusicInstance musicInstance = this.client.getMusicInstance();
        float f = musicInstance.volume();
        if (this.current != null && this.volume != f && !(bl = this.canFadeTowardsVolume(f))) {
            return;
        }
        MusicSound musicSound = musicInstance.music();
        if (musicSound == null) {
            this.timeUntilNextSong = Math.max(this.timeUntilNextSong, 100);
            return;
        }
        if (this.current != null) {
            if (musicInstance.shouldReplace(this.current)) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicSound.minDelay() / 2);
            }
            if (!this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(this.timeUntilNextSong, this.musicFrequency.getDelayBeforePlaying(musicSound, this.random));
            }
        }
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, this.musicFrequency.getDelayBeforePlaying(musicSound, this.random));
        if (this.current == null && this.timeUntilNextSong-- <= 0) {
            this.play(musicInstance);
        }
    }

    public void play(MusicInstance instance) {
        SoundEvent soundEvent = instance.music().sound().value();
        this.current = PositionedSoundInstance.music(soundEvent, instance.volume());
        switch (this.client.getSoundManager().play(this.current)) {
            case STARTED: {
                this.client.getToastManager().onMusicTrackStart();
                this.shownToast = true;
                break;
            }
            case STARTED_SILENTLY: {
                this.shownToast = false;
            }
        }
        this.timeUntilNextSong = Integer.MAX_VALUE;
        this.volume = instance.volume();
    }

    public void tryShowToast() {
        if (!this.shownToast) {
            this.client.getToastManager().onMusicTrackStart();
            this.shownToast = true;
        }
    }

    public void stop(MusicSound type) {
        if (this.isPlayingType(type)) {
            this.stop();
        }
    }

    public void stop() {
        if (this.current != null) {
            this.client.getSoundManager().stop(this.current);
            this.current = null;
            this.client.getToastManager().onMusicTrackStop();
        }
        this.timeUntilNextSong += 100;
    }

    private boolean canFadeTowardsVolume(float volume) {
        if (this.current == null) {
            return false;
        }
        if (this.volume == volume) {
            return true;
        }
        if (this.volume < volume) {
            this.volume += MathHelper.clamp(this.volume, 5.0E-4f, 0.005f);
            if (this.volume > volume) {
                this.volume = volume;
            }
        } else {
            this.volume = 0.03f * volume + 0.97f * this.volume;
            if (Math.abs(this.volume - volume) < 1.0E-4f || this.volume < volume) {
                this.volume = volume;
            }
        }
        this.volume = MathHelper.clamp(this.volume, 0.0f, 1.0f);
        if (this.volume <= 1.0E-4f) {
            this.stop();
            return false;
        }
        this.client.getSoundManager().setVolume(this.current, this.volume);
        return true;
    }

    public boolean isPlayingType(MusicSound type) {
        if (this.current == null) {
            return false;
        }
        return type.sound().value().id().equals(this.current.getId());
    }

    @Nullable
    public String getCurrentMusicTranslationKey() {
        Sound sound;
        if (this.current != null && (sound = this.current.getSound()) != null) {
            return sound.getIdentifier().toShortTranslationKey();
        }
        return null;
    }

    public void setMusicFrequency(MusicFrequency musicFrequency) {
        this.musicFrequency = musicFrequency;
        this.timeUntilNextSong = this.musicFrequency.getDelayBeforePlaying(this.client.getMusicInstance().music(), this.random);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class MusicFrequency
    extends Enum<MusicFrequency>
    implements TranslatableOption,
    StringIdentifiable {
        final static public MusicFrequency DEFAULT = new MusicFrequency(20);
        final static public MusicFrequency FREQUENT = new MusicFrequency(10);
        final static public MusicFrequency CONSTANT = new MusicFrequency(0);
        final static public Codec<MusicFrequency> CODEC;
        final static private String TRANSLATION_KEY_PREFIX = "options.music_frequency.";
        final private int index;
        final private int delayBetweenTracks;
        final private String translationKey;
        final static private MusicFrequency[] field_60805;

        public static MusicFrequency[] values() {
            return (MusicFrequency[])field_60805.clone();
        }

        public static MusicFrequency valueOf(String string) {
            return Enum.valueOf(MusicFrequency.class, string);
        }

        private MusicFrequency(int index) {
            this.index = index;
            this.delayBetweenTracks = index * 1200;
            this.translationKey = TRANSLATION_KEY_PREFIX + this.name().toLowerCase();
        }

        int getDelayBeforePlaying(@Nullable MusicSound music, Random random) {
            if (music == null) {
                return this.delayBetweenTracks;
            }
            if (this == CONSTANT) {
                return 100;
            }
            int i = Math.min(music.minDelay(), this.delayBetweenTracks);
            int j = Math.min(music.maxDelay(), this.delayBetweenTracks);
            return MathHelper.nextInt(random, 1, j);
        }

        @Override
        public int getId() {
            return this.index;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }

        @Override
        public String asString() {
            return this.name();
        }

        private static MusicFrequency[] method_71936() {
            return new MusicFrequency[]{DEFAULT, FREQUENT, CONSTANT};
        }

        static {
            field_60805 = MusicFrequency.method_71936();
            CODEC = StringIdentifiable.createCodec(MusicFrequency::values);
        }
    }
}

