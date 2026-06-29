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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

public class BiomeAdditionsSound {
    final static public Codec<BiomeAdditionsSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(sound -> sound.sound), (App)Codec.DOUBLE.fieldOf("tick_chance").forGetter(sound -> sound.chance)).apply((Applicative)instance, BiomeAdditionsSound::new));
    final private RegistryEntry<SoundEvent> sound;
    final private double chance;

    public BiomeAdditionsSound(RegistryEntry<SoundEvent> sound, double chance) {
        this.sound = sound;
        this.chance = chance;
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    public double getChance() {
        return this.chance;
    }
}

