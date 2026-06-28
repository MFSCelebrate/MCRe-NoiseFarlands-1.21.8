/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MusicInstance(@Nullable MusicSound music, float volume) {
    public MusicInstance(MusicSound music) {
        this(music, 1.0f);
    }

    public boolean shouldReplace(SoundInstance sound) {
        if (this.music == null) {
            return false;
        }
        return this.music.replaceCurrentMusic() && !this.music.sound().value().id().equals(sound.getId());
    }
}

