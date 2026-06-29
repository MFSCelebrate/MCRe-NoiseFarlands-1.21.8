/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeSupplier;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.DeobfuscateClass;
import net.minecraft.util.collection.ArrayListDeque;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class RenderSystem {
    final static Logger LOGGER = LogUtils.getLogger();
    final static public int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
    final static public int PROJECTION_MATRIX_UBO_SIZE = new Std140SizeCalculator().putMat4f().get();
    @Nullable
    static private Thread renderThread;
    @Nullable
    static private GpuDevice DEVICE;
    static private double lastDrawTime;
    final static private ShapeIndexBuffer sharedSequential;
    final static private ShapeIndexBuffer sharedSequentialQuad;
    final static private ShapeIndexBuffer sharedSequentialLines;
    static private ProjectionType projectionType;
    static private ProjectionType savedProjectionType;
    final static private Matrix4fStack modelViewStack;
    static private Matrix4f textureMatrix;
    final static public int TEXTURE_COUNT = 12;
    final static private GpuTextureView[] shaderTextures;
    @Nullable
    static private GpuBufferSlice shaderFog;
    @Nullable
    static private GpuBufferSlice shaderLightDirections;
    @Nullable
    static private GpuBufferSlice projectionMatrixBuffer;
    @Nullable
    static private GpuBufferSlice savedProjectionMatrixBuffer;
    final static private Vector3f modelOffset;
    static private float shaderLineWidth;
    static private String apiDescription;
    final static private AtomicLong pollEventsWaitStart;
    final static private AtomicBoolean pollingEvents;
    @Nullable
    static private GpuBuffer QUAD_VERTEX_BUFFER;
    final static private ArrayListDeque<Task> PENDING_FENCES;
    @Nullable
    static public GpuTextureView outputColorTextureOverride;
    @Nullable
    static public GpuTextureView outputDepthTextureOverride;
    @Nullable
    static private GpuBuffer globalSettingsUniform;
    @Nullable
    static private DynamicUniforms dynamicUniforms;
    static private ScissorState scissorStateForRenderTypeDraws;

    public static void initRenderThread() {
        if (renderThread != null) {
            throw new IllegalStateException("Could not initialize render thread");
        }
        renderThread = Thread.currentThread();
    }

    public static boolean isOnRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    public static void assertOnRenderThread() {
        if (!RenderSystem.isOnRenderThread()) {
            throw RenderSystem.constructThreadException();
        }
    }

    private static IllegalStateException constructThreadException() {
        return new IllegalStateException("Rendersystem called from wrong thread");
    }

    private static void pollEvents() {
        pollEventsWaitStart.set(Util.getMeasuringTimeMs());
        pollingEvents.set(true);
        GLFW.glfwPollEvents();
        pollingEvents.set(false);
    }

    public static boolean isFrozenAtPollEvents() {
        return pollingEvents.get() && Util.getMeasuringTimeMs() - pollEventsWaitStart.get() > 200L;
    }

    public static void flipFrame(long window, @Nullable TracyFrameCapturer capturer) {
        RenderSystem.pollEvents();
        Tessellator.getInstance().clear();
        GLFW.glfwSwapBuffers((long)window);
        if (capturer != null) {
            capturer.markFrame();
        }
        dynamicUniforms.clear();
        MinecraftClient.getInstance().worldRenderer.rotate();
        RenderSystem.pollEvents();
    }

    public static void limitDisplayFPS(int fps) {
        double d = lastDrawTime + 1.0 / (double)fps;
        double e = GLFW.glfwGetTime();
        while (e < d) {
            GLFW.glfwWaitEventsTimeout((double)(d - e));
            e = GLFW.glfwGetTime();
        }
        lastDrawTime = e;
    }

    public static void setShaderFog(GpuBufferSlice shaderFog) {
        RenderSystem.shaderFog = shaderFog;
    }

    @Nullable
    public static GpuBufferSlice getShaderFog() {
        return shaderFog;
    }

    public static void setShaderLights(GpuBufferSlice shaderLightDirections) {
        RenderSystem.shaderLightDirections = shaderLightDirections;
    }

    @Nullable
    public static GpuBufferSlice getShaderLights() {
        return shaderLightDirections;
    }

    public static void lineWidth(float width) {
        RenderSystem.assertOnRenderThread();
        shaderLineWidth = width;
    }

    public static float getShaderLineWidth() {
        RenderSystem.assertOnRenderThread();
        return shaderLineWidth;
    }

    public static void enableScissorForRenderTypeDraws(int i, int j, int k, int l) {
        scissorStateForRenderTypeDraws.enable(i, j, k, l);
    }

    public static void disableScissorForRenderTypeDraws() {
        scissorStateForRenderTypeDraws.disable();
    }

    public static ScissorState getScissorStateForRenderTypeDraws() {
        return scissorStateForRenderTypeDraws;
    }

    public static String getBackendDescription() {
        return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
    }

    public static String getApiDescription() {
        return apiDescription;
    }

    public static TimeSupplier.Nanoseconds initBackendSystem() {
        return GLX._initGlfw()::getAsLong;
    }

    public static void initRenderer(long windowHandle, int debugVerbosity, boolean sync, BiFunction<Identifier, ShaderType, String> shaderSourceGetter, boolean renderDebugLabels) {
        DEVICE = new GlBackend(windowHandle, debugVerbosity, sync, shaderSourceGetter, renderDebugLabels);
        apiDescription = RenderSystem.getDevice().getImplementationInformation();
        dynamicUniforms = new DynamicUniforms();
        BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION.getVertexSize() * 4);
        try {
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            bufferBuilder.vertex(0.0f, 0.0f, 0.0f);
            bufferBuilder.vertex(1.0f, 0.0f, 0.0f);
            bufferBuilder.vertex(1.0f, 1.0f, 0.0f);
            bufferBuilder.vertex(0.0f, 1.0f, 0.0f);
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                QUAD_VERTEX_BUFFER = RenderSystem.getDevice().createBuffer(() -> "Quad", 32, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            if (bufferAllocator != null) {
                bufferAllocator.close();
            }
        }
        catch (Throwable throwable) {
            if (bufferAllocator != null) {
                try {
                    bufferAllocator.close();
                }
                catch (Throwable throwable3) {
                    throwable.addSuppressed(throwable3);
                }
            }
            throw throwable;
        }
    }

    public static void setErrorCallback(GLFWErrorCallbackI callback) {
        GLX._setGlfwErrorCallback(callback);
    }

    public static void setupDefaultState() {
        modelViewStack.clear();
        textureMatrix.identity();
    }

    public static void setupOverlayColor(@Nullable GpuTextureView texture) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(1, texture);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertOnRenderThread();
        RenderSystem.setShaderTexture(1, null);
    }

    public static void setShaderTexture(int index, @Nullable GpuTextureView texture) {
        RenderSystem.assertOnRenderThread();
        if (index >= 0 && index < shaderTextures.length) {
            RenderSystem.shaderTextures[index] = texture;
        }
    }

    @Nullable
    public static GpuTextureView getShaderTexture(int index) {
        RenderSystem.assertOnRenderThread();
        if (index >= 0 && index < shaderTextures.length) {
            return shaderTextures[index];
        }
        return null;
    }

    public static void setProjectionMatrix(GpuBufferSlice projectionMatrixBuffer, ProjectionType projectionType) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.projectionMatrixBuffer = projectionMatrixBuffer;
        RenderSystem.projectionType = projectionType;
    }

    public static void setTextureMatrix(Matrix4f textureMatrix) {
        RenderSystem.assertOnRenderThread();
        RenderSystem.textureMatrix = new Matrix4f((Matrix4fc)textureMatrix);
    }

    public static void resetTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        textureMatrix.identity();
    }

    public static void backupProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        savedProjectionMatrixBuffer = projectionMatrixBuffer;
        savedProjectionType = projectionType;
    }

    public static void restoreProjectionMatrix() {
        RenderSystem.assertOnRenderThread();
        projectionMatrixBuffer = savedProjectionMatrixBuffer;
        projectionType = savedProjectionType;
    }

    @Nullable
    public static GpuBufferSlice getProjectionMatrixBuffer() {
        RenderSystem.assertOnRenderThread();
        return projectionMatrixBuffer;
    }

    public static Matrix4f getModelViewMatrix() {
        RenderSystem.assertOnRenderThread();
        return modelViewStack;
    }

    public static Matrix4fStack getModelViewStack() {
        RenderSystem.assertOnRenderThread();
        return modelViewStack;
    }

    public static Matrix4f getTextureMatrix() {
        RenderSystem.assertOnRenderThread();
        return textureMatrix;
    }

    public static ShapeIndexBuffer getSequentialBuffer(VertexFormat.DrawMode drawMode) {
        RenderSystem.assertOnRenderThread();
        return switch (drawMode) {
            case VertexFormat.DrawMode.QUADS -> sharedSequentialQuad;
            case VertexFormat.DrawMode.LINES -> sharedSequentialLines;
            default -> sharedSequential;
        };
    }

    public static void setGlobalSettingsUniform(GpuBuffer globalSettingsUniform) {
        RenderSystem.globalSettingsUniform = globalSettingsUniform;
    }

    @Nullable
    public static GpuBuffer getGlobalSettingsUniform() {
        return globalSettingsUniform;
    }

    public static ProjectionType getProjectionType() {
        RenderSystem.assertOnRenderThread();
        return projectionType;
    }

    public static GpuBuffer getQuadVertexBuffer() {
        if (QUAD_VERTEX_BUFFER == null) {
            throw new IllegalStateException("Can't getQuadVertexBuffer() before renderer was initialized");
        }
        return QUAD_VERTEX_BUFFER;
    }

    public static void setModelOffset(float offsetX, float offsetY, float offsetZ) {
        RenderSystem.assertOnRenderThread();
        modelOffset.set(offsetX, offsetY, offsetZ);
    }

    public static void resetModelOffset() {
        RenderSystem.assertOnRenderThread();
        modelOffset.set(0.0f, 0.0f, 0.0f);
    }

    public static Vector3f getModelOffset() {
        RenderSystem.assertOnRenderThread();
        return modelOffset;
    }

    public static void queueFencedTask(Runnable task) {
        PENDING_FENCES.addLast(new Task(task, RenderSystem.getDevice().createCommandEncoder().createFence()));
    }

    public static void executePendingTasks() {
        Task task = PENDING_FENCES.peekFirst();
        while (task != null) {
            if (task.fence.awaitCompletion(0L)) {
                try {
                    task.callback.run();
                    task.fence.close();
                }
                catch (Throwable throwable) {
                    task.fence.close();
                    throw throwable;
                }
                PENDING_FENCES.removeFirst();
                task = PENDING_FENCES.peekFirst();
                continue;
            }
            return;
        }
    }

    public static GpuDevice getDevice() {
        if (DEVICE == null) {
            throw new IllegalStateException("Can't getDevice() before it was initialized");
        }
        return DEVICE;
    }

    @Nullable
    public static GpuDevice tryGetDevice() {
        return DEVICE;
    }

    public static DynamicUniforms getDynamicUniforms() {
        if (dynamicUniforms == null) {
            throw new IllegalStateException("Can't getDynamicUniforms() before device was initialized");
        }
        return dynamicUniforms;
    }

    public static void bindDefaultUniforms(RenderPass pass) {
        GpuBufferSlice gpuBufferSlice3;
        GpuBuffer gpuBuffer;
        GpuBufferSlice gpuBufferSlice2;
        GpuBufferSlice gpuBufferSlice = RenderSystem.getProjectionMatrixBuffer();
        if (gpuBufferSlice != null) {
            pass.setUniform("Projection", gpuBufferSlice);
        }
        if ((gpuBufferSlice2 = RenderSystem.getShaderFog()) != null) {
            pass.setUniform("Fog", gpuBufferSlice2);
        }
        if ((gpuBuffer = RenderSystem.getGlobalSettingsUniform()) != null) {
            pass.setUniform("Globals", gpuBuffer);
        }
        if ((gpuBufferSlice3 = RenderSystem.getShaderLights()) != null) {
            pass.setUniform("Lighting", gpuBufferSlice3);
        }
    }

    static {
        lastDrawTime = Double.MIN_VALUE;
        sharedSequential = new ShapeIndexBuffer(1, 1, java.util.function.IntConsumer::accept);
        sharedSequentialQuad = new ShapeIndexBuffer(4, 6, (indexConsumer, firstVertexIndex) -> {
            indexConsumer.accept(firstVertexIndex);
            indexConsumer.accept(firstVertexIndex + 1);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 3);
            indexConsumer.accept(firstVertexIndex);
        });
        sharedSequentialLines = new ShapeIndexBuffer(4, 6, (indexConsumer, firstVertexIndex) -> {
            indexConsumer.accept(firstVertexIndex);
            indexConsumer.accept(firstVertexIndex + 1);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 3);
            indexConsumer.accept(firstVertexIndex + 2);
            indexConsumer.accept(firstVertexIndex + 1);
        });
        projectionType = ProjectionType.PERSPECTIVE;
        savedProjectionType = ProjectionType.PERSPECTIVE;
        modelViewStack = new Matrix4fStack(16);
        textureMatrix = new Matrix4f();
        shaderTextures = new GpuTextureView[12];
        shaderFog = null;
        modelOffset = new Vector3f();
        shaderLineWidth = 1.0f;
        apiDescription = "Unknown";
        pollEventsWaitStart = new AtomicLong();
        pollingEvents = new AtomicBoolean(false);
        PENDING_FENCES = new ArrayListDeque();
        scissorStateForRenderTypeDraws = new ScissorState();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ShapeIndexBuffer {
        final private int vertexCountInShape;
        final private int vertexCountInTriangulated;
        final private Triangulator triangulator;
        @Nullable
        private GpuBuffer indexBuffer;
        private VertexFormat.IndexType indexType = VertexFormat.IndexType.SHORT;
        private int size;

        ShapeIndexBuffer(int vertexCountInShape, int vertexCountInTriangulated, Triangulator triangulator) {
            this.vertexCountInShape = vertexCountInShape;
            this.vertexCountInTriangulated = vertexCountInTriangulated;
            this.triangulator = triangulator;
        }

        public boolean isLargeEnough(int requiredSize) {
            return requiredSize <= this.size;
        }

        public GpuBuffer getIndexBuffer(int requiredSize) {
            this.grow(requiredSize);
            return this.indexBuffer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void grow(int requiredSize) {
            if (this.isLargeEnough(requiredSize)) {
                return;
            }
            requiredSize = MathHelper.roundUpToMultiple(requiredSize * 2, this.vertexCountInTriangulated);
            LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", (Object)this.size, (Object)requiredSize);
            int i = requiredSize / this.vertexCountInTriangulated;
            int j = i * this.vertexCountInShape;
            VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(j);
            int k = MathHelper.roundUpToMultiple(requiredSize * indexType.size, 4);
            ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)k);
            try {
                this.indexType = indexType;
                IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);
                for (int l = 0; l < requiredSize; l += this.vertexCountInTriangulated) {
                    this.triangulator.accept(intConsumer, l * this.vertexCountInShape / this.vertexCountInTriangulated);
                }
                byteBuffer.flip();
                if (this.indexBuffer != null) {
                    this.indexBuffer.close();
                }
                this.indexBuffer = RenderSystem.getDevice().createBuffer(() -> "Auto Storage index buffer", 64, byteBuffer);
            }
            catch (Throwable throwable) {
                MemoryUtil.memFree((Buffer)byteBuffer);
                throw throwable;
            }
            MemoryUtil.memFree((Buffer)byteBuffer);
            this.size = requiredSize;
        }

        private IntConsumer getIndexConsumer(ByteBuffer indexBuffer) {
            switch (this.indexType) {
                case SHORT: {
                    return index -> indexBuffer.putShort((short)index);
                }
            }
            return indexBuffer::putInt;
        }

        public VertexFormat.IndexType getIndexType() {
            return this.indexType;
        }

        @Environment(value=EnvType.CLIENT)
        static interface Triangulator {
            public void accept(IntConsumer var1, int var2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Task
    extends Record {
        final Runnable callback;
        final GpuFence fence;

        Task(Runnable runnable, GpuFence gpuFence) {
            this.callback = runnable;
            this.fence = gpuFence;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Task.class, "callback;fence", "callback", "fence"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Task.class, "callback;fence", "callback", "fence"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Task.class, "callback;fence", "callback", "fence"}, this, object);
        }

        public Runnable callback() {
            return this.callback;
        }

        public GpuFence fence() {
            return this.fence;
        }
    }
}

