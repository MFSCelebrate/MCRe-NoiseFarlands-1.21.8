/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.biome;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeParticleConfig;
import org.jetbrains.annotations.Nullable;

public class BiomeEffects {
    final static public Codec<BiomeEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("fog_color").forGetter(effects -> effects.fogColor), (App)Codec.INT.fieldOf("water_color").forGetter(effects -> effects.waterColor), (App)Codec.INT.fieldOf("water_fog_color").forGetter(effects -> effects.waterFogColor), (App)Codec.INT.fieldOf("sky_color").forGetter(effects -> effects.skyColor), (App)Codec.INT.optionalFieldOf("foliage_color").forGetter(effects -> effects.foliageColor), (App)Codec.INT.optionalFieldOf("dry_foliage_color").forGetter(effects -> effects.dryFoliageColor), (App)Codec.INT.optionalFieldOf("grass_color").forGetter(effects -> effects.grassColor), (App)GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", (Object)GrassColorModifier.NONE).forGetter(effects -> effects.grassColorModifier), (App)BiomeParticleConfig.CODEC.optionalFieldOf("particle").forGetter(effects -> effects.particleConfig), (App)SoundEvent.ENTRY_CODEC.optionalFieldOf("ambient_sound").forGetter(effects -> effects.loopSound), (App)BiomeMoodSound.CODEC.optionalFieldOf("mood_sound").forGetter(effects -> effects.moodSound), (App)BiomeAdditionsSound.CODEC.optionalFieldOf("additions_sound").forGetter(effects -> effects.additionsSound), (App)Pool.createCodec(MusicSound.CODEC).optionalFieldOf("music").forGetter(effects -> effects.music), (App)Codec.FLOAT.fieldOf("music_volume").orElse((Object)Float.valueOf(1.0f)).forGetter(effects -> Float.valueOf(effects.musicVolume))).apply((Applicative)instance, BiomeEffects::new));
    final private int fogColor;
    final private int waterColor;
    final private int waterFogColor;
    final private int skyColor;
    final private Optional<Integer> foliageColor;
    final private Optional<Integer> dryFoliageColor;
    final private Optional<Integer> grassColor;
    final private GrassColorModifier grassColorModifier;
    final private Optional<BiomeParticleConfig> particleConfig;
    final private Optional<RegistryEntry<SoundEvent>> loopSound;
    final private Optional<BiomeMoodSound> moodSound;
    final private Optional<BiomeAdditionsSound> additionsSound;
    final private Optional<Pool<MusicSound>> music;
    final private float musicVolume;

    BiomeEffects(int fogColor, int waterColor, int waterFogColor, int skyColor, Optional<Integer> foliageColor, Optional<Integer> dryFoliageColor, Optional<Integer> grassColor, GrassColorModifier grassColorModifier, Optional<BiomeParticleConfig> particleConfig, Optional<RegistryEntry<SoundEvent>> loopSound, Optional<BiomeMoodSound> moodSound, Optional<BiomeAdditionsSound> additionsSound, Optional<Pool<MusicSound>> music, float musicVolume) {
        this.fogColor = fogColor;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
        this.skyColor = skyColor;
        this.foliageColor = foliageColor;
        this.dryFoliageColor = dryFoliageColor;
        this.grassColor = grassColor;
        this.grassColorModifier = grassColorModifier;
        this.particleConfig = particleConfig;
        this.loopSound = loopSound;
        this.moodSound = moodSound;
        this.additionsSound = additionsSound;
        this.music = music;
        this.musicVolume = musicVolume;
    }

    public int getFogColor() {
        return this.fogColor;
    }

    public int getWaterColor() {
        return this.waterColor;
    }

    public int getWaterFogColor() {
        return this.waterFogColor;
    }

    public int getSkyColor() {
        return this.skyColor;
    }

    public Optional<Integer> getFoliageColor() {
        return this.foliageColor;
    }

    public Optional<Integer> getDryFoliageColor() {
        return this.dryFoliageColor;
    }

    public Optional<Integer> getGrassColor() {
        return this.grassColor;
    }

    public GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }

    public Optional<BiomeParticleConfig> getParticleConfig() {
        return this.particleConfig;
    }

    public Optional<RegistryEntry<SoundEvent>> getLoopSound() {
        return this.loopSound;
    }

    public Optional<BiomeMoodSound> getMoodSound() {
        return this.moodSound;
    }

    public Optional<BiomeAdditionsSound> getAdditionsSound() {
        return this.additionsSound;
    }

    public Optional<Pool<MusicSound>> getMusic() {
        return this.music;
    }

    public float getMusicVolume() {
        return this.musicVolume;
    }

    public static abstract sealed class GrassColorModifier
    extends Enum<GrassColorModifier>
    implements StringIdentifiable {
        final static public GrassColorModifier NONE = new GrassColorModifier("none"){

            @Override
            public int getModifiedGrassColor(double x, double z, int color) {
                return color;
            }
        };
        final static public GrassColorModifier DARK_FOREST = new GrassColorModifier("dark_forest"){

            @Override
            public int getModifiedGrassColor(double x, double z, int color) {
                return (color & 0xFEFEFE) + 2634762 >> 1;
            }
        };
        final static public GrassColorModifier SWAMP = new GrassColorModifier("swamp"){

            @Override
            public int getModifiedGrassColor(double x, double z, int color) {
                double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
                if (d < -0.1) {
                    return 5011004;
                }
                return 6975545;
            }
        };
        final private String name;
        final static public Codec<GrassColorModifier> CODEC;
        final static private GrassColorModifier[] field_26432;

        public static GrassColorModifier[] values() {
            return (GrassColorModifier[])field_26432.clone();
        }

        public static GrassColorModifier valueOf(String string) {
            return Enum.valueOf(GrassColorModifier.class, string);
        }

        public abstract int getModifiedGrassColor(double var1, double var3, int var5);

        GrassColorModifier(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static GrassColorModifier[] method_36701() {
            return new GrassColorModifier[]{NONE, DARK_FOREST, SWAMP};
        }

        static {
            field_26432 = GrassColorModifier.method_36701();
            CODEC = StringIdentifiable.createCodec(GrassColorModifier::values);
        }
    }

    public static class Builder {
        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private OptionalInt skyColor = OptionalInt.empty();
        private Optional<Integer> foliageColor = Optional.empty();
        private Optional<Integer> dryFoliageColor = Optional.empty();
        private Optional<Integer> grassColor = Optional.empty();
        private GrassColorModifier grassColorModifier = GrassColorModifier.NONE;
        private Optional<BiomeParticleConfig> particleConfig = Optional.empty();
        private Optional<RegistryEntry<SoundEvent>> loopSound = Optional.empty();
        private Optional<BiomeMoodSound> moodSound = Optional.empty();
        private Optional<BiomeAdditionsSound> additionsSound = Optional.empty();
        private Optional<Pool<MusicSound>> music = Optional.empty();
        private float musicVolume = 1.0f;

        public Builder fogColor(int fogColor) {
            this.fogColor = OptionalInt.of(fogColor);
            return this;
        }

        public Builder waterColor(int waterColor) {
            this.waterColor = OptionalInt.of(waterColor);
            return this;
        }

        public Builder waterFogColor(int waterFogColor) {
            this.waterFogColor = OptionalInt.of(waterFogColor);
            return this;
        }

        public Builder skyColor(int skyColor) {
            this.skyColor = OptionalInt.of(skyColor);
            return this;
        }

        public Builder foliageColor(int foliageColor) {
            this.foliageColor = Optional.of(foliageColor);
            return this;
        }

        public Builder dryFoliageColor(int dryFoliageColor) {
            this.dryFoliageColor = Optional.of(dryFoliageColor);
            return this;
        }

        public Builder grassColor(int grassColor) {
            this.grassColor = Optional.of(grassColor);
            return this;
        }

        public Builder grassColorModifier(GrassColorModifier grassColorModifier) {
            this.grassColorModifier = grassColorModifier;
            return this;
        }

        public Builder particleConfig(BiomeParticleConfig particleConfig) {
            this.particleConfig = Optional.of(particleConfig);
            return this;
        }

        public Builder loopSound(RegistryEntry<SoundEvent> loopSound) {
            this.loopSound = Optional.of(loopSound);
            return this;
        }

        public Builder moodSound(BiomeMoodSound moodSound) {
            this.moodSound = Optional.of(moodSound);
            return this;
        }

        public Builder additionsSound(BiomeAdditionsSound additionsSound) {
            this.additionsSound = Optional.of(additionsSound);
            return this;
        }

        public Builder music(@Nullable MusicSound music) {
            if (music == null) {
                this.music = Optional.empty();
                return this;
            }
            this.music = Optional.of(Pool.of(music));
            return this;
        }

        public Builder noMusic() {
            return this.music(Pool.empty()).musicVolume(0.0f);
        }

        public Builder music(Pool<MusicSound> music) {
            this.music = Optional.of(music);
            return this;
        }

        public Builder musicVolume(float musicVolume) {
            this.musicVolume = musicVolume;
            return this;
        }

        public BiomeEffects build() {
            return new BiomeEffects(this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")), this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")), this.skyColor.orElseThrow(() -> new IllegalStateException("Missing 'sky' color.")), this.foliageColor, this.dryFoliageColor, this.grassColor, this.grassColorModifier, this.particleConfig, this.loopSound, this.moodSound, this.additionsSound, this.music, this.musicVolume);
        }
    }
}

