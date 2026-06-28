/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.client.sound.RepeatingAudioStream;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class SoundLoader {
    final private ResourceFactory resourceFactory;
    final private Map<Identifier, CompletableFuture<StaticSound>> loadedSounds = Maps.newHashMap();

    public SoundLoader(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public CompletableFuture<StaticSound> loadStatic(Identifier id) {
        return this.loadedSounds.computeIfAbsent(id, id2 -> CompletableFuture.supplyAsync(() -> {
            StaticSound staticSound;
            OggAudioStream nonRepeatingAudioStream;
            InputStream inputStream;
            block13: {
                inputStream = this.resourceFactory.open((Identifier)id2);
                nonRepeatingAudioStream = new OggAudioStream(inputStream);
                ByteBuffer byteBuffer = nonRepeatingAudioStream.readAll();
                staticSound = new StaticSound(byteBuffer, nonRepeatingAudioStream.getFormat());
                nonRepeatingAudioStream.close();
                if (inputStream == null) break block13;
                inputStream.close();
            }
            return staticSound;
            {
                catch (Throwable throwable) {
                    try {
                        try {
                            try {
                                nonRepeatingAudioStream.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                            throw throwable;
                        }
                        catch (Throwable throwable3) {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                }
                                catch (Throwable throwable4) {
                                    throwable3.addSuppressed(throwable4);
                                }
                            }
                            throw throwable3;
                        }
                    }
                    catch (IOException iOException) {
                        throw new CompletionException(iOException);
                    }
                }
            }
        }, Util.getDownloadWorkerExecutor()));
    }

    public CompletableFuture<AudioStream> loadStreamed(Identifier id, boolean repeatInstantly) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream = this.resourceFactory.open(id);
                return repeatInstantly ? new RepeatingAudioStream(OggAudioStream::new, inputStream) : new OggAudioStream(inputStream);
            }
            catch (IOException iOException) {
                throw new CompletionException(iOException);
            }
        }, Util.getDownloadWorkerExecutor());
    }

    public void close() {
        this.loadedSounds.values().forEach(soundFuture -> soundFuture.thenAccept(StaticSound::close));
        this.loadedSounds.clear();
    }

    public CompletableFuture<?> loadStatic(Collection<Sound> sounds) {
        return CompletableFuture.allOf((CompletableFuture[])sounds.stream().map(sound -> this.loadStatic(sound.getLocation())).toArray(CompletableFuture[]::new));
    }
}

