/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.ARBTimerQuery
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL32C
 *  org.lwjgl.opengl.GL33
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBTimerQuery;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33;

@Environment(value=EnvType.CLIENT)
public class GlTimer {
    private int queryId;

    public static Optional<GlTimer> getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void beginProfile() {
        RenderSystem.assertOnRenderThread();
        if (this.queryId != 0) {
            throw new IllegalStateException("Current profile not ended");
        }
        this.queryId = GL32C.glGenQueries();
        GL32C.glBeginQuery((int)GL33.GL_TIME_ELAPSED, (int)this.queryId);
    }

    public Query endProfile() {
        RenderSystem.assertOnRenderThread();
        if (this.queryId == 0) {
            throw new IllegalStateException("endProfile called before beginProfile");
        }
        GL32C.glEndQuery((int)GL33.GL_TIME_ELAPSED);
        Query query = new Query(this.queryId);
        this.queryId = 0;
        return query;
    }

    @Environment(value=EnvType.CLIENT)
    static class InstanceHolder {
        final static Optional<GlTimer> INSTANCE = Optional.ofNullable(InstanceHolder.create());

        private InstanceHolder() {
        }

        @Nullable
        private static GlTimer create() {
            if (!GL.getCapabilities().GL_ARB_timer_query) {
                return null;
            }
            return new GlTimer();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Query {
        final static private long MISSING = 0L;
        final static private long CLOSED = -1L;
        final private int queryId;
        private long result;

        Query(int queryId) {
            this.queryId = queryId;
        }

        public void close() {
            RenderSystem.assertOnRenderThread();
            if (this.result != 0L) {
                return;
            }
            this.result = -1L;
            GL32C.glDeleteQueries((int)this.queryId);
        }

        public boolean isResultAvailable() {
            RenderSystem.assertOnRenderThread();
            if (this.result != 0L) {
                return true;
            }
            if (1 == GL32C.glGetQueryObjecti((int)this.queryId, (int)GL15.GL_QUERY_RESULT_AVAILABLE)) {
                this.result = ARBTimerQuery.glGetQueryObjecti64((int)this.queryId, (int)GL15.GL_QUERY_RESULT);
                GL32C.glDeleteQueries((int)this.queryId);
                return true;
            }
            return false;
        }

        public long queryResult() {
            RenderSystem.assertOnRenderThread();
            if (this.result == 0L) {
                this.result = ARBTimerQuery.glGetQueryObjecti64((int)this.queryId, (int)GL15.GL_QUERY_RESULT);
                GL32C.glDeleteQueries((int)this.queryId);
            }
            return this.result;
        }
    }
}

