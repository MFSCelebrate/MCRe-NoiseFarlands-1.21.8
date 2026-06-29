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
import net.minecraft.sound.SoundEvents;

public class BiomeMoodSound {
    final static public Codec<BiomeMoodSound> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(sound -> sound.sound), (App)Codec.INT.fieldOf("tick_delay").forGetter(sound -> sound.cultivationTicks), (App)Codec.INT.fieldOf("block_search_extent").forGetter(sound -> sound.spawnRange), (App)Codec.DOUBLE.fieldOf("offset").forGetter(sound -> sound.extraDistance)).apply((Applicative)instance, BiomeMoodSound::new));
    final static public BiomeMoodSound CAVE = new BiomeMoodSound(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
    final private RegistryEntry<SoundEvent> sound;
    final private int cultivationTicks;
    final private int spawnRange;
    final private double extraDistance;

    public BiomeMoodSound(RegistryEntry<SoundEvent> sound, int cultivationTicks, int spawnRange, double extraDistance) {
        this.sound = sound;
        this.cultivationTicks = cultivationTicks;
        this.spawnRange = spawnRange;
        this.extraDistance = extraDistance;
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    public int getCultivationTicks() {
        return this.cultivationTicks;
    }

    public int getSpawnRange() {
        return this.spawnRange;
    }

    public double getExtraDistance() {
        return this.extraDistance;
    }
}

