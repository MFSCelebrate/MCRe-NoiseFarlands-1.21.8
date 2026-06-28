/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.sound;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

public record MusicSound(RegistryEntry<SoundEvent> sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
    final static public Codec<MusicSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(sound -> sound.sound), (App)Codec.INT.fieldOf("min_delay").forGetter(sound -> sound.minDelay), (App)Codec.INT.fieldOf("max_delay").forGetter(sound -> sound.maxDelay), (App)Codec.BOOL.fieldOf("replace_current_music").forGetter(sound -> sound.replaceCurrentMusic)).apply((Applicative)instance, MusicSound::new));

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MusicSound.class, "event;minDelay;maxDelay;replaceCurrentMusic", "sound", "minDelay", "maxDelay", "replaceCurrentMusic"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MusicSound.class, "event;minDelay;maxDelay;replaceCurrentMusic", "sound", "minDelay", "maxDelay", "replaceCurrentMusic"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MusicSound.class, "event;minDelay;maxDelay;replaceCurrentMusic", "sound", "minDelay", "maxDelay", "replaceCurrentMusic"}, this, object);
    }
}

