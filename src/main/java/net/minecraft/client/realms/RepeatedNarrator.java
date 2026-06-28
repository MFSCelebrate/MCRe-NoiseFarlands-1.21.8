/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.RateLimiter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RepeatedNarrator {
    final private float permitsPerSecond;
    final private AtomicReference<Parameters> params = new AtomicReference();

    public RepeatedNarrator(Duration duration) {
        this.permitsPerSecond = 1000.0f / (float)duration.toMillis();
    }

    public void narrate(NarratorManager narratorManager, Text text) {
        Parameters parameters2 = this.params.updateAndGet(parameters -> {
            if (parameters == null || !text.equals(parameters.message)) {
                return new Parameters(text, RateLimiter.create((double)this.permitsPerSecond));
            }
            return parameters;
        });
        if (parameters2.rateLimiter.tryAcquire(1)) {
            narratorManager.narrateSystemImmediately(text);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Parameters {
        final Text message;
        final RateLimiter rateLimiter;

        Parameters(Text text, RateLimiter rateLimiter) {
            this.message = text;
            this.rateLimiter = rateLimiter;
        }
    }
}

