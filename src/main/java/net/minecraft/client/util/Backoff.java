/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public interface Backoff {
    final static public Backoff ONE_CYCLE = new Backoff(){

        @Override
        public long success() {
            return 1L;
        }

        @Override
        public long fail() {
            return 1L;
        }
    };

    public long success();

    public long fail();

    public static Backoff exponential(final int maxSkippableCycles) {
        return new Backoff(){
            final static private Logger LOGGER = LogUtils.getLogger();
            private int failureCount;

            @Override
            public long success() {
                this.failureCount = 0;
                return 1L;
            }

            @Override
            public long fail() {
                ++this.failureCount;
                long l = Math.min(1L << this.failureCount, (long)maxSkippableCycles);
                LOGGER.debug("Skipping for {} extra cycles", (Object)l);
                return l;
            }
        };
    }
}

