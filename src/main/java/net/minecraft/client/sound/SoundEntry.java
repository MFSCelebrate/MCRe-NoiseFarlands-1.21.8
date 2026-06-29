/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.sound;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundEntry {
    final private List<Sound> sounds;
    final private boolean replace;
    @Nullable
    final private String subtitle;

    public SoundEntry(List<Sound> sounds, boolean replace, @Nullable String subtitle) {
        this.sounds = sounds;
        this.replace = replace;
        this.subtitle = subtitle;
    }

    public List<Sound> getSounds() {
        return this.sounds;
    }

    public boolean canReplace() {
        return this.replace;
    }

    @Nullable
    public String getSubtitle() {
        return this.subtitle;
    }
}

