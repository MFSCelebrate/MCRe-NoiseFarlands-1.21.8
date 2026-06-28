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
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Sound
implements SoundContainer<Sound> {
    final static public ResourceFinder FINDER = new ResourceFinder("sounds", ".ogg");
    final private Identifier id;
    final private FloatSupplier volume;
    final private FloatSupplier pitch;
    final private int weight;
    final private RegistrationType registrationType;
    final private boolean stream;
    final private boolean preload;
    final private int attenuation;

    public Sound(Identifier id, FloatSupplier volume, FloatSupplier pitch, int weight, RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        this.id = id;
        this.volume = volume;
        this.pitch = pitch;
        this.weight = weight;
        this.registrationType = registrationType;
        this.stream = stream;
        this.preload = preload;
        this.attenuation = attenuation;
    }

    public Identifier getIdentifier() {
        return this.id;
    }

    public Identifier getLocation() {
        return FINDER.toResourcePath(this.id);
    }

    public FloatSupplier getVolume() {
        return this.volume;
    }

    public FloatSupplier getPitch() {
        return this.pitch;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public Sound net_minecraft_client_sound_Sound_getSound(Random random) {
        return this;
    }

    @Override
    public void preload(SoundSystem soundSystem) {
        if (this.preload) {
            soundSystem.addPreloadedSound(this);
        }
    }

    public RegistrationType getRegistrationType() {
        return this.registrationType;
    }

    public boolean isStreamed() {
        return this.stream;
    }

    public boolean isPreloaded() {
        return this.preload;
    }

    public int getAttenuation() {
        return this.attenuation;
    }

    public String toString() {
        return "Sound[" + String.valueOf(this.id) + "]";
    }

    @Override
    public Object java_lang_Object_getSound(Random random) {
        return this.net_minecraft_client_sound_Sound_getSound(random);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class RegistrationType
    extends Enum<RegistrationType> {
        final static public RegistrationType FILE = new RegistrationType("file");
        final static public RegistrationType SOUND_EVENT = new RegistrationType("event");
        final private String name;
        final static private RegistrationType[] field_5471;

        public static RegistrationType[] values() {
            return (RegistrationType[])field_5471.clone();
        }

        public static RegistrationType valueOf(String string) {
            return Enum.valueOf(RegistrationType.class, string);
        }

        private RegistrationType(String name) {
            this.name = name;
        }

        @Nullable
        public static RegistrationType getByName(String name) {
            for (RegistrationType registrationType : RegistrationType.values()) {
                if (!registrationType.name.equals(name)) continue;
                return registrationType;
            }
            return null;
        }

        private static RegistrationType[] method_36926() {
            return new RegistrationType[]{FILE, SOUND_EVENT};
        }

        static {
            field_5471 = RegistrationType.method_36926();
        }
    }
}

