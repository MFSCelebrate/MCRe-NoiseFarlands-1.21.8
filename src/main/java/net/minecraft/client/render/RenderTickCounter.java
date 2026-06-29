/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface RenderTickCounter {
    final static public RenderTickCounter ZERO = new Constant(0.0f);
    final static public RenderTickCounter ONE = new Constant(1.0f);

    public float getDynamicDeltaTicks();

    public float getTickProgress(boolean var1);

    public float getFixedDeltaTicks();

    @Environment(value=EnvType.CLIENT)
    public static class Constant
    implements RenderTickCounter {
        final private float value;

        Constant(float value) {
            this.value = value;
        }

        @Override
        public float getDynamicDeltaTicks() {
            return this.value;
        }

        @Override
        public float getTickProgress(boolean ignoreFreeze) {
            return this.value;
        }

        @Override
        public float getFixedDeltaTicks() {
            return this.value;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Dynamic
    implements RenderTickCounter {
        private float dynamicDeltaTicks;
        private float tickProgress;
        private float fixedDeltaTicks;
        private float tickProgressBeforePause;
        private long lastTimeMillis;
        private long timeMillis;
        final private float tickTime;
        final private FloatUnaryOperator targetMillisPerTick;
        private boolean paused;
        private boolean tickFrozen;

        public Dynamic(float tps, long timeMillis, FloatUnaryOperator targetMillisPerTick) {
            this.tickTime = 1000.0f / tps;
            this.timeMillis = this.lastTimeMillis = timeMillis;
            this.targetMillisPerTick = targetMillisPerTick;
        }

        public int beginRenderTick(long timeMillis, boolean tick) {
            this.setTimeMillis(timeMillis);
            if (tick) {
                return this.beginRenderTick(timeMillis);
            }
            return 0;
        }

        private int beginRenderTick(long timeMillis) {
            this.dynamicDeltaTicks = (float)(timeMillis - this.lastTimeMillis) / this.targetMillisPerTick.apply(this.tickTime);
            this.lastTimeMillis = timeMillis;
            this.tickProgress += this.dynamicDeltaTicks;
            int i = (int)this.tickProgress;
            this.tickProgress -= (float)i;
            return i;
        }

        private void setTimeMillis(long timeMillis) {
            this.fixedDeltaTicks = (float)(timeMillis - this.timeMillis) / this.tickTime;
            this.timeMillis = timeMillis;
        }

        public void tick(boolean paused) {
            if (paused) {
                this.tickPaused();
            } else {
                this.tickUnpaused();
            }
        }

        private void tickPaused() {
            if (!this.paused) {
                this.tickProgressBeforePause = this.tickProgress;
            }
            this.paused = true;
        }

        private void tickUnpaused() {
            if (this.paused) {
                this.tickProgress = this.tickProgressBeforePause;
            }
            this.paused = false;
        }

        public void setTickFrozen(boolean tickFrozen) {
            this.tickFrozen = tickFrozen;
        }

        @Override
        public float getDynamicDeltaTicks() {
            return this.dynamicDeltaTicks;
        }

        @Override
        public float getTickProgress(boolean ignoreFreeze) {
            if (!ignoreFreeze && this.tickFrozen) {
                return 1.0f;
            }
            return this.paused ? this.tickProgressBeforePause : this.tickProgress;
        }

        @Override
        public float getFixedDeltaTicks() {
            if (this.fixedDeltaTicks > 7.0f) {
                return 0.5f;
            }
            return this.fixedDeltaTicks;
        }
    }
}

