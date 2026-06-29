/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.openal.AL10
 *  org.slf4j.Logger
 */
package net.minecraft.client.sound;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AlUtil;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL10;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Source {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int field_31895 = 4;
    final static public int field_31894 = 1;
    final private int pointer;
    final private AtomicBoolean playing = new AtomicBoolean(true);
    private int bufferSize = 16384;
    @Nullable
    private AudioStream stream;

    @Nullable
    static Source create() {
        int[] is = new int[1];
        AL10.alGenSources((int[])is);
        if (AlUtil.checkErrors("Allocate new source")) {
            return null;
        }
        return new Source(is[0]);
    }

    private Source(int pointer) {
        this.pointer = pointer;
    }

    public void close() {
        if (this.playing.compareAndSet(true, false)) {
            AL10.alSourceStop((int)this.pointer);
            AlUtil.checkErrors("Stop");
            if (this.stream != null) {
                try {
                    this.stream.close();
                }
                catch (IOException iOException) {
                    LOGGER.error("Failed to close audio stream", (Throwable)iOException);
                }
                this.removeProcessedBuffers();
                this.stream = null;
            }
            AL10.alDeleteSources((int[])new int[]{this.pointer});
            AlUtil.checkErrors("Cleanup");
        }
    }

    public void play() {
        AL10.alSourcePlay((int)this.pointer);
    }

    private int getSourceState() {
        if (!this.playing.get()) {
            return 4116;
        }
        return AL10.alGetSourcei((int)this.pointer, 4112);
    }

    public void pause() {
        if (this.getSourceState() == 4114) {
            AL10.alSourcePause((int)this.pointer);
        }
    }

    public void resume() {
        if (this.getSourceState() == 4115) {
            AL10.alSourcePlay((int)this.pointer);
        }
    }

    public void stop() {
        if (this.playing.get()) {
            AL10.alSourceStop((int)this.pointer);
            AlUtil.checkErrors("Stop");
        }
    }

    public boolean isPlaying() {
        return this.getSourceState() == 4114;
    }

    public boolean isStopped() {
        return this.getSourceState() == 4116;
    }

    public void setPosition(Vec3d pos) {
        AL10.alSourcefv((int)this.pointer, 4100, (float[])new float[]{(float)pos.x, (float)pos.y, (float)pos.z});
    }

    public void setPitch(float pitch) {
        AL10.alSourcef((int)this.pointer, 4099, (float)pitch);
    }

    public void setLooping(boolean looping) {
        AL10.alSourcei((int)this.pointer, 4103, (int)(looping ? 1 : 0));
    }

    public void setVolume(float volume) {
        AL10.alSourcef((int)this.pointer, 4106, (float)volume);
    }

    public void disableAttenuation() {
        AL10.alSourcei((int)this.pointer, 53248, 0);
    }

    public void setAttenuation(float attenuation) {
        AL10.alSourcei((int)this.pointer, 53248, 53251);
        AL10.alSourcef((int)this.pointer, 4131, (float)attenuation);
        AL10.alSourcef((int)this.pointer, 4129, 1.0f);
        AL10.alSourcef((int)this.pointer, 4128, 0.0f);
    }

    public void setRelative(boolean relative) {
        AL10.alSourcei((int)this.pointer, 514, (int)(relative ? 1 : 0));
    }

    public void setBuffer(StaticSound sound) {
        sound.getStreamBufferPointer().ifPresent(pointer -> AL10.alSourcei((int)this.pointer, 4105, (int)pointer));
    }

    public void setStream(AudioStream stream) {
        this.stream = stream;
        AudioFormat audioFormat = stream.getFormat();
        this.bufferSize = Source.getBufferSize(audioFormat, 1);
        this.read(4);
    }

    private static int getBufferSize(AudioFormat format, int time) {
        return (int)((float)(time * format.getSampleSizeInBits()) / 8.0f * (float)format.getChannels() * format.getSampleRate());
    }

    private void read(int count) {
        if (this.stream != null) {
            try {
                for (int i = 0; i < count; ++i) {
                    ByteBuffer byteBuffer = this.stream.read(this.bufferSize);
                    if (byteBuffer == null) continue;
                    new StaticSound(byteBuffer, this.stream.getFormat()).takeStreamBufferPointer().ifPresent(pointer -> AL10.alSourceQueueBuffers((int)this.pointer, (int[])new int[]{pointer}));
                }
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to read from audio stream", (Throwable)iOException);
            }
        }
    }

    public void tick() {
        if (this.stream != null) {
            int i = this.removeProcessedBuffers();
            this.read(i);
        }
    }

    private int removeProcessedBuffers() {
        int i = AL10.alGetSourcei((int)this.pointer, 4118);
        if (i > 0) {
            int[] is = new int[i];
            AL10.alSourceUnqueueBuffers((int)this.pointer, (int[])is);
            AlUtil.checkErrors("Unqueue buffers");
            AL10.alDeleteBuffers((int[])is);
            AlUtil.checkErrors("Remove processed buffers");
        }
        return i;
    }
}

