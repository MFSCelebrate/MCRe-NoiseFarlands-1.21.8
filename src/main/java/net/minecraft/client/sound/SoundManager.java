/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundEntryDeserializer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundListenerTransform;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.MultipliedFloatSupplier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ScopedProfiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SoundManager
extends SinglePreparationResourceReloader<SoundList> {
    final static public Identifier EMPTY_ID = Identifier.ofVanilla("empty");
    final static public Sound MISSING_SOUND = new Sound(EMPTY_ID, ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), 1, Sound.RegistrationType.FILE, false, false, 16);
    final static public Identifier INTENTIONALLY_EMPTY_ID = Identifier.ofVanilla("intentionally_empty");
    final static public WeightedSoundSet INTENTIONALLY_EMPTY_SOUND_SET = new WeightedSoundSet(INTENTIONALLY_EMPTY_ID, null);
    final static public Sound INTENTIONALLY_EMPTY_SOUND = new Sound(INTENTIONALLY_EMPTY_ID, ConstantFloatProvider.create(1.0f), ConstantFloatProvider.create(1.0f), 1, Sound.RegistrationType.FILE, false, false, 16);
    final static Logger LOGGER = LogUtils.getLogger();
    final static private String SOUNDS_JSON = "sounds.json";
    final static private Gson GSON = new GsonBuilder().registerTypeAdapter(SoundEntry.class, (Object)new SoundEntryDeserializer()).create();
    final static private TypeToken<Map<String, SoundEntry>> TYPE = new TypeToken<Map<String, SoundEntry>>(){};
    final private Map<Identifier, WeightedSoundSet> sounds = Maps.newHashMap();
    final private SoundSystem soundSystem;
    final private Map<Identifier, Resource> soundResources = new HashMap<Identifier, Resource>();

    public SoundManager(GameOptions gameOptions, MusicTracker musicTracker) {
        this.soundSystem = new SoundSystem(musicTracker, this, gameOptions, ResourceFactory.fromMap(this.soundResources));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    @Override
    protected SoundList net_minecraft_client_sound_SoundManager$SoundList_prepare(ResourceManager resourceManager, Profiler profiler) {
        SoundList soundList = new SoundList();
        ScopedProfiler scopedProfiler = profiler.scoped("list");
        try {
            soundList.findSounds(resourceManager);
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
        }
        catch (Throwable throwable) {
            if (scopedProfiler == null) throw throwable;
            try {
                scopedProfiler.close();
                throw throwable;
            }
            catch (Throwable throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        Iterator<String> iterator = resourceManager.getAllNamespaces().iterator();
        while (iterator.hasNext()) {
            String string = iterator.next();
            try (ScopedProfiler scopedProfiler2 = profiler.scoped(string);){
                List<Resource> list = resourceManager.getAllResources(Identifier.of(string, SOUNDS_JSON));
                for (Resource resource : list) {
                    block20: {
                        profiler.push(resource.getPackId());
                        try (BufferedReader reader = resource.getReader();){
                            profiler.push("parse");
                            Map<String, SoundEntry> map = JsonHelper.deserialize(GSON, (Reader)reader, TYPE);
                            profiler.swap("register");
                            for (Map.Entry<String, SoundEntry> entry : map.entrySet()) {
                                soundList.register(Identifier.of(string, entry.getKey()), entry.getValue());
                            }
                            profiler.pop();
                            if (reader == null) break block20;
                        }
                        catch (RuntimeException runtimeException) {
                            LOGGER.warn("Invalid {} in resourcepack: '{}'", new Object[]{SOUNDS_JSON, resource.getPackId(), runtimeException});
                        }
                    }
                    profiler.pop();
                }
                if (scopedProfiler2 == null) continue;
            }
            catch (IOException iOException) {
            }
        }
        return soundList;
    }

    @Override
    protected void apply(SoundList soundList, ResourceManager resourceManager, Profiler profiler) {
        soundList.reload(this.sounds, this.soundResources, this.soundSystem);
        if (SharedConstants.isDevelopment) {
            for (Identifier identifier : this.sounds.keySet()) {
                WeightedSoundSet weightedSoundSet = this.sounds.get(identifier);
                if (Texts.hasTranslation(weightedSoundSet.getSubtitle()) || !Registries.SOUND_EVENT.containsId(identifier)) continue;
                LOGGER.error("Missing subtitle {} for sound event: {}", (Object)weightedSoundSet.getSubtitle(), (Object)identifier);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            for (Identifier identifier : this.sounds.keySet()) {
                if (Registries.SOUND_EVENT.containsId(identifier)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)identifier);
            }
        }
        this.soundSystem.reloadSounds();
    }

    public List<String> getSoundDevices() {
        return this.soundSystem.getSoundDevices();
    }

    public SoundListenerTransform getListenerTransform() {
        return this.soundSystem.getListenerTransform();
    }

    static boolean isSoundResourcePresent(Sound sound, Identifier id, ResourceFactory resourceFactory) {
        Identifier identifier = sound.getLocation();
        if (resourceFactory.getResource(identifier).isEmpty()) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)identifier, (Object)id);
            return false;
        }
        return true;
    }

    @Nullable
    public WeightedSoundSet get(Identifier id) {
        return this.sounds.get(id);
    }

    public Collection<Identifier> getKeys() {
        return this.sounds.keySet();
    }

    public void playNextTick(TickableSoundInstance sound) {
        this.soundSystem.playNextTick(sound);
    }

    public SoundSystem.PlayResult play(SoundInstance sound) {
        return this.soundSystem.play(sound);
    }

    public void play(SoundInstance sound, int delay) {
        this.soundSystem.play(sound, delay);
    }

    public void updateListenerPosition(Camera camera) {
        this.soundSystem.updateListenerPosition(camera);
    }

    public void pauseAllExcept(SoundCategory ... categories) {
        this.soundSystem.pauseAllExcept(categories);
    }

    public void stopAll() {
        this.soundSystem.stopAll();
    }

    public void close() {
        this.soundSystem.stop();
    }

    public void stopAbruptly() {
        this.soundSystem.stopAbruptly();
    }

    public void tick(boolean paused) {
        this.soundSystem.tick(paused);
    }

    public void resumeAll() {
        this.soundSystem.resumeAll();
    }

    public void updateSoundVolume(SoundCategory soundCategory, float f) {
        this.soundSystem.updateSoundVolume(soundCategory, f);
    }

    public void stop(SoundInstance sound) {
        this.soundSystem.stop(sound);
    }

    public void setVolume(SoundInstance sound, float volume) {
        this.soundSystem.setVolume(sound, volume);
    }

    public boolean isPlaying(SoundInstance sound) {
        return this.soundSystem.isPlaying(sound);
    }

    public void registerListener(SoundInstanceListener listener) {
        this.soundSystem.registerListener(listener);
    }

    public void unregisterListener(SoundInstanceListener listener) {
        this.soundSystem.unregisterListener(listener);
    }

    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory soundCategory) {
        this.soundSystem.stopSounds(id, soundCategory);
    }

    public String getDebugString() {
        return this.soundSystem.getDebugString();
    }

    public void reloadSounds() {
        this.soundSystem.reloadSounds();
    }

    @Override
    protected Object java_lang_Object_prepare(ResourceManager manager, Profiler profiler) {
        return this.net_minecraft_client_sound_SoundManager$SoundList_prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    protected static class SoundList {
        final Map<Identifier, WeightedSoundSet> loadedSounds = Maps.newHashMap();
        private Map<Identifier, Resource> foundSounds = Map.of();

        protected SoundList() {
        }

        void findSounds(ResourceManager resourceManager) {
            this.foundSounds = Sound.FINDER.findResources(resourceManager);
        }

        void register(Identifier id, SoundEntry entry) {
            boolean bl;
            WeightedSoundSet weightedSoundSet = this.loadedSounds.get(id);
            boolean bl2 = bl = weightedSoundSet == null;
            if (bl || entry.canReplace()) {
                if (!bl) {
                    LOGGER.debug("Replaced sound event location {}", (Object)id);
                }
                weightedSoundSet = new WeightedSoundSet(id, entry.getSubtitle());
                this.loadedSounds.put(id, weightedSoundSet);
            }
            ResourceFactory resourceFactory = ResourceFactory.fromMap(this.foundSounds);
            block4: for (final Sound sound : entry.getSounds()) {
                final Identifier identifier = sound.getIdentifier();
                weightedSoundSet.add(switch (sound.getRegistrationType()) {
                    case Sound.RegistrationType.FILE -> {
                        if (!SoundManager.isSoundResourcePresent(sound, id, resourceFactory)) continue block4;
                        yield sound;
                    }
                    case Sound.RegistrationType.SOUND_EVENT -> new SoundContainer<Sound>(){

                        @Override
                        public int getWeight() {
                            WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                            return weightedSoundSet == null ? 0 : weightedSoundSet.getWeight();
                        }

                        @Override
                        public Sound net_minecraft_client_sound_Sound_getSound(Random random) {
                            WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                            if (weightedSoundSet == null) {
                                return MISSING_SOUND;
                            }
                            Sound sound2 = weightedSoundSet.net_minecraft_client_sound_Sound_getSound(random);
                            return new Sound(sound2.getIdentifier(), new MultipliedFloatSupplier(sound2.getVolume(), sound.getVolume()), new MultipliedFloatSupplier(sound2.getPitch(), sound.getPitch()), sound.getWeight(), Sound.RegistrationType.FILE, sound2.isStreamed() || sound.isStreamed(), sound2.isPreloaded(), sound2.getAttenuation());
                        }

                        @Override
                        public void preload(SoundSystem soundSystem) {
                            WeightedSoundSet weightedSoundSet = loadedSounds.get(identifier);
                            if (weightedSoundSet == null) {
                                return;
                            }
                            weightedSoundSet.preload(soundSystem);
                        }

                        @Override
                        public Object java_lang_Object_getSound(Random random) {
                            return this.net_minecraft_client_sound_Sound_getSound(random);
                        }
                    };
                    default -> throw new IllegalStateException("Unknown SoundEventRegistration type: " + String.valueOf((Object)sound.getRegistrationType()));
                });
            }
        }

        public void reload(Map<Identifier, WeightedSoundSet> sounds, Map<Identifier, Resource> soundResources, SoundSystem system) {
            sounds.clear();
            soundResources.clear();
            soundResources.putAll(this.foundSounds);
            for (Map.Entry<Identifier, WeightedSoundSet> entry : this.loadedSounds.entrySet()) {
                sounds.put(entry.getKey(), entry.getValue());
                entry.getValue().preload(system);
            }
        }
    }
}

