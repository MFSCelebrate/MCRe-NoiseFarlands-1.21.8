/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.slf4j.Logger
 */
package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayerGroup;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.ChunkBuilderMode;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.CloudRenderer;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.SectionRenderState;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.render.WorldBorderRendering;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.client.render.chunk.Buffers;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.chunk.NormalizedRelativePos;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldRenderer
implements SynchronousResourceReloader,
AutoCloseable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private Identifier TRANSPARENCY = Identifier.ofVanilla("transparency");
    final static private Identifier ENTITY_OUTLINE = Identifier.ofVanilla("entity_outline");
    final static public int field_32759 = 16;
    final static public int field_34812 = 8;
    final static public int field_54162 = 32;
    final static private int field_54163 = 15;
    final static private Comparator<Entity> ENTITY_COMPARATOR = Comparator.comparing(entity -> entity.getType().hashCode());
    final private MinecraftClient client;
    final private EntityRenderDispatcher entityRenderDispatcher;
    final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    final private BufferBuilderStorage bufferBuilders;
    final private SkyRendering skyRendering = new SkyRendering();
    final private CloudRenderer cloudRenderer = new CloudRenderer();
    final private WorldBorderRendering worldBorderRendering = new WorldBorderRendering();
    final private WeatherRendering weatherRendering = new WeatherRendering();
    @Nullable
    private ClientWorld world;
    final private ChunkRenderingDataPreparer chunkRenderingDataPreparer = new ChunkRenderingDataPreparer();
    final private ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks = new ObjectArrayList(10000);
    final private ObjectArrayList<ChunkBuilder.BuiltChunk> nearbyChunks = new ObjectArrayList(50);
    @Nullable
    private BuiltChunkStorage chunks;
    private int ticks;
    final private Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos = new Int2ObjectOpenHashMap();
    final private Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions = new Long2ObjectOpenHashMap();
    @Nullable
    private Framebuffer entityOutlineFramebuffer;
    final private DefaultFramebufferSet framebufferSet = new DefaultFramebufferSet();
    private int cameraChunkX = Integer.MIN_VALUE;
    private int cameraChunkY = Integer.MIN_VALUE;
    private int cameraChunkZ = Integer.MIN_VALUE;
    private double lastCameraX = Double.MIN_VALUE;
    private double lastCameraY = Double.MIN_VALUE;
    private double lastCameraZ = Double.MIN_VALUE;
    private double lastCameraPitch = Double.MIN_VALUE;
    private double lastCameraYaw = Double.MIN_VALUE;
    @Nullable
    private ChunkBuilder chunkBuilder;
    private int viewDistance = -1;
    final private List<Entity> renderedEntities = new ArrayList<Entity>();
    private int renderedEntitiesCount;
    private Frustum frustum;
    private boolean shouldCaptureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    @Nullable
    private BlockPos lastTranslucencySortCameraPos;
    private int chunkIndex;

    public WorldRenderer(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders) {
        this.client = client;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
        this.bufferBuilders = bufferBuilders;
    }

    public void addWeatherParticlesAndSound(Camera camera) {
        this.weatherRendering.addParticlesAndSound(this.client.world, camera, this.ticks, this.client.options.getParticles().getValue());
    }

    @Override
    public void close() {
        if (this.entityOutlineFramebuffer != null) {
            this.entityOutlineFramebuffer.delete();
        }
        this.skyRendering.close();
        this.cloudRenderer.close();
    }

    @Override
    public void reload(ResourceManager manager) {
        this.loadEntityOutlinePostProcessor();
    }

    public void loadEntityOutlinePostProcessor() {
        if (this.entityOutlineFramebuffer != null) {
            this.entityOutlineFramebuffer.delete();
        }
        this.entityOutlineFramebuffer = new SimpleFramebuffer("Entity Outline", this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), true);
    }

    @Nullable
    private PostEffectProcessor getTransparencyPostEffectProcessor() {
        if (!MinecraftClient.isFabulousGraphicsOrBetter()) {
            return null;
        }
        PostEffectProcessor postEffectProcessor = this.client.getShaderLoader().loadPostEffect(TRANSPARENCY, DefaultFramebufferSet.STAGES);
        if (postEffectProcessor == null) {
            this.client.options.getGraphicsMode().setValue(GraphicsMode.FANCY);
            this.client.options.write();
        }
        return postEffectProcessor;
    }

    public void drawEntityOutlinesFramebuffer() {
        if (this.canDrawEntityOutlines()) {
            this.entityOutlineFramebuffer.drawBlit(this.client.getFramebuffer().getColorAttachmentView());
        }
    }

    protected boolean canDrawEntityOutlines() {
        return !this.client.gameRenderer.isRenderingPanorama() && this.entityOutlineFramebuffer != null && this.client.player != null;
    }

    public void setWorld(@Nullable ClientWorld world) {
        this.cameraChunkX = Integer.MIN_VALUE;
        this.cameraChunkY = Integer.MIN_VALUE;
        this.cameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setWorld(world);
        this.world = world;
        if (world != null) {
            this.reload();
        } else {
            if (this.chunks != null) {
                this.chunks.clear();
                this.chunks = null;
            }
            if (this.chunkBuilder != null) {
                this.chunkBuilder.stop();
            }
            this.chunkBuilder = null;
            this.chunkRenderingDataPreparer.setStorage(null);
            this.clear();
        }
    }

    private void clear() {
        this.builtChunks.clear();
        this.nearbyChunks.clear();
    }

    public void reload() {
        if (this.world == null) {
            return;
        }
        this.world.reloadColor();
        if (this.chunkBuilder == null) {
            this.chunkBuilder = new ChunkBuilder(this.world, this, Util.getMainWorkerExecutor(), this.bufferBuilders, this.client.getBlockRenderManager(), this.client.getBlockEntityRenderDispatcher());
        } else {
            this.chunkBuilder.setWorld(this.world);
        }
        this.cloudRenderer.scheduleTerrainUpdate();
        RenderLayers.setFancyGraphicsOrBetter(MinecraftClient.isFancyGraphicsOrBetter());
        this.viewDistance = this.client.options.getClampedViewDistance();
        if (this.chunks != null) {
            this.chunks.clear();
        }
        this.chunkBuilder.cancelAllTasks();
        this.chunks = new BuiltChunkStorage(this.chunkBuilder, this.world, this.client.options.getClampedViewDistance(), this);
        this.chunkRenderingDataPreparer.setStorage(this.chunks);
        this.clear();
        Camera camera = this.client.gameRenderer.getCamera();
        this.chunks.updateCameraPosition(ChunkSectionPos.from(camera.getPos()));
    }

    public void onResized(int width, int height) {
        this.scheduleTerrainUpdate();
        if (this.entityOutlineFramebuffer != null) {
            this.entityOutlineFramebuffer.resize(width, height);
        }
    }

    public String getChunksDebugString() {
        int i = this.chunks.chunks.length;
        int j = this.getCompletedChunkCount();
        return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", j, i, this.client.chunkCullingEnabled ? "(s) " : "", this.viewDistance, this.chunkBuilder == null ? "null" : this.chunkBuilder.getDebugString());
    }

    public ChunkBuilder getChunkBuilder() {
        return this.chunkBuilder;
    }

    public double getChunkCount() {
        return this.chunks.chunks.length;
    }

    public double getViewDistance() {
        return this.viewDistance;
    }

    public int getCompletedChunkCount() {
        int i = 0;
        for (ChunkBuilder.BuiltChunk builtChunk : this.builtChunks) {
            if (!builtChunk.getCurrentRenderData().hasData()) continue;
            ++i;
        }
        return i;
    }

    public String getEntitiesDebugString() {
        return "E: " + this.renderedEntitiesCount + "/" + this.world.getRegularEntityCount() + ", SD: " + this.world.getSimulationDistance();
    }

    private void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator) {
        Vec3d vec3d = camera.getPos();
        if (this.client.options.getClampedViewDistance() != this.viewDistance) {
            this.reload();
        }
        Profiler profiler = Profilers.get();
        profiler.push("camera");
        int i = ChunkSectionPos.getSectionCoord(vec3d.getX());
        int j = ChunkSectionPos.getSectionCoord(vec3d.getY());
        int k = ChunkSectionPos.getSectionCoord(vec3d.getZ());
        if (this.cameraChunkX != i || this.cameraChunkY != j || this.cameraChunkZ != k) {
            this.cameraChunkX = i;
            this.cameraChunkY = j;
            this.cameraChunkZ = k;
            this.chunks.updateCameraPosition(ChunkSectionPos.from(vec3d));
            this.worldBorderRendering.markBuffersDirty();
        }
        this.chunkBuilder.setCameraPosition(vec3d);
        profiler.swap("cull");
        double d = Math.floor(vec3d.x / 8.0);
        double e = Math.floor(vec3d.y / 8.0);
        double f = Math.floor(vec3d.z / 8.0);
        if (d != this.lastCameraX || e != this.lastCameraY || f != this.lastCameraZ) {
            this.chunkRenderingDataPreparer.scheduleTerrainUpdate();
        }
        this.lastCameraX = d;
        this.lastCameraY = e;
        this.lastCameraZ = f;
        profiler.swap("update");
        if (!hasForcedFrustum) {
            boolean bl = this.client.chunkCullingEnabled;
            if (spectator && this.world.getBlockState(camera.getBlockPos()).isOpaqueFullCube()) {
                bl = false;
            }
            profiler.push("section_occlusion_graph");
            this.chunkRenderingDataPreparer.updateSectionOcclusionGraph(bl, camera, frustum, (List<ChunkBuilder.BuiltChunk>)this.builtChunks, this.world.net_minecraft_client_world_ClientChunkManager_getChunkManager().getActiveSections());
            profiler.pop();
            double g = Math.floor(camera.getPitch() / 2.0f);
            double h = Math.floor(camera.getYaw() / 2.0f);
            if (this.chunkRenderingDataPreparer.updateFrustum() || g != this.lastCameraPitch || h != this.lastCameraYaw) {
                this.applyFrustum(WorldRenderer.offsetFrustum(frustum));
                this.lastCameraPitch = g;
                this.lastCameraYaw = h;
            }
        }
        profiler.pop();
    }

    public static Frustum offsetFrustum(Frustum frustum) {
        return new Frustum(frustum).coverBoxAroundSetPosition(8);
    }

    private void applyFrustum(Frustum frustum) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
        }
        Profilers.get().push("apply_frustum");
        this.clear();
        this.chunkRenderingDataPreparer.collectChunks(frustum, (List<ChunkBuilder.BuiltChunk>)this.builtChunks, (List<ChunkBuilder.BuiltChunk>)this.nearbyChunks);
        Profilers.get().pop();
    }

    public void addBuiltChunk(ChunkBuilder.BuiltChunk chunk) {
        this.chunkRenderingDataPreparer.schedulePropagationFrom(chunk);
    }

    public void setupFrustum(Vec3d pos, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        this.frustum = new Frustum(positionMatrix, projectionMatrix);
        this.frustum.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public void render(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky) {
        Optional<Integer> optional;
        float f = tickCounter.getTickProgress(false);
        this.blockEntityRenderDispatcher.configure(this.world, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
        final Profiler profiler = Profilers.get();
        profiler.push("light_update_queue");
        this.world.runQueuedChunkUpdates();
        profiler.swap("light_updates");
        this.world.net_minecraft_client_world_ClientChunkManager_getChunkManager().net_minecraft_world_chunk_light_LightingProvider_getLightingProvider().doLightUpdates();
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double g = vec3d.getZ();
        profiler.swap("culling");
        boolean bl = this.capturedFrustum != null;
        Frustum frustum = bl ? this.capturedFrustum : this.frustum;
        profiler.swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.capturedFrustum = bl ? new Frustum(positionMatrix, projectionMatrix) : frustum;
            this.capturedFrustum.setPosition(d, e, g);
            this.shouldCaptureFrustum = false;
        }
        profiler.swap("cullEntities");
        boolean bl2 = this.getEntitiesToRender(camera, frustum, this.renderedEntities);
        this.renderedEntitiesCount = this.renderedEntities.size();
        profiler.swap("terrain_setup");
        this.setupTerrain(camera, frustum, bl, this.client.player.isSpectator());
        profiler.swap("compile_sections");
        this.updateChunks(camera);
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul((Matrix4fc)positionMatrix);
        FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
        this.framebufferSet.mainFramebuffer = frameGraphBuilder.createObjectNode("main", this.client.getFramebuffer());
        int i = this.client.getFramebuffer().textureWidth;
        int j = this.client.getFramebuffer().textureHeight;
        SimpleFramebufferFactory simpleFramebufferFactory = new SimpleFramebufferFactory(i, j, true, 0);
        PostEffectProcessor postEffectProcessor = this.getTransparencyPostEffectProcessor();
        if (postEffectProcessor != null) {
            this.framebufferSet.translucentFramebuffer = frameGraphBuilder.createResourceHandle("translucent", simpleFramebufferFactory);
            this.framebufferSet.itemEntityFramebuffer = frameGraphBuilder.createResourceHandle("item_entity", simpleFramebufferFactory);
            this.framebufferSet.particlesFramebuffer = frameGraphBuilder.createResourceHandle("particles", simpleFramebufferFactory);
            this.framebufferSet.weatherFramebuffer = frameGraphBuilder.createResourceHandle("weather", simpleFramebufferFactory);
            this.framebufferSet.cloudsFramebuffer = frameGraphBuilder.createResourceHandle("clouds", simpleFramebufferFactory);
        }
        if (this.entityOutlineFramebuffer != null) {
            this.framebufferSet.entityOutlineFramebuffer = frameGraphBuilder.createObjectNode("entity_outline", this.entityOutlineFramebuffer);
        }
        FramePass framePass = frameGraphBuilder.createPass("clear");
        this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        framePass.setRenderer(() -> {
            Framebuffer framebuffer = this.client.getFramebuffer();
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), ColorHelper.fromFloats(0.0f, vector4f.x, vector4f.y, vector4f.z), framebuffer.getDepthAttachment(), 1.0);
        });
        if (shouldRenderSky) {
            this.renderSky(frameGraphBuilder, camera, f, fog);
        }
        this.renderMain(frameGraphBuilder, frustum, camera, positionMatrix, fog, renderBlockOutline, bl2, tickCounter, profiler);
        PostEffectProcessor postEffectProcessor2 = this.client.getShaderLoader().loadPostEffect(ENTITY_OUTLINE, DefaultFramebufferSet.MAIN_AND_ENTITY_OUTLINE);
        if (bl2 && postEffectProcessor2 != null) {
            postEffectProcessor2.render(frameGraphBuilder, i, j, this.framebufferSet);
        }
        this.renderParticles(frameGraphBuilder, camera, f, fog);
        CloudRenderMode cloudRenderMode = this.client.options.getCloudRenderModeValue();
        if (cloudRenderMode != CloudRenderMode.OFF && (optional = this.world.getDimension().cloudHeight()).isPresent()) {
            float h = (float)this.ticks + f;
            int k = this.world.getCloudsColor(f);
            this.renderClouds(frameGraphBuilder, cloudRenderMode, camera.getPos(), h, k, (float)optional.get().intValue() + 0.33f);
        }
        this.renderWeather(frameGraphBuilder, camera.getPos(), f, fog);
        if (postEffectProcessor != null) {
            postEffectProcessor.render(frameGraphBuilder, i, j, this.framebufferSet);
        }
        this.renderLateDebug(frameGraphBuilder, vec3d, fog);
        profiler.swap("framegraph");
        frameGraphBuilder.run(allocator, new FrameGraphBuilder.Profiler(){

            @Override
            public void push(String location) {
                profiler.push(location);
            }

            @Override
            public void pop(String location) {
                profiler.pop();
            }
        });
        this.renderedEntities.clear();
        this.framebufferSet.clear();
        matrix4fStack.popMatrix();
        profiler.pop();
    }

    private void renderMain(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera, Matrix4f positionMatrix, GpuBufferSlice fog, boolean renderBlockOutline, boolean renderEntityOutline, RenderTickCounter tickCounter, Profiler profiler) {
        FramePass framePass = frameGraphBuilder.createPass("main");
        this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        if (this.framebufferSet.translucentFramebuffer != null) {
            this.framebufferSet.translucentFramebuffer = framePass.transfer(this.framebufferSet.translucentFramebuffer);
        }
        if (this.framebufferSet.itemEntityFramebuffer != null) {
            this.framebufferSet.itemEntityFramebuffer = framePass.transfer(this.framebufferSet.itemEntityFramebuffer);
        }
        if (this.framebufferSet.weatherFramebuffer != null) {
            this.framebufferSet.weatherFramebuffer = framePass.transfer(this.framebufferSet.weatherFramebuffer);
        }
        if (renderEntityOutline && this.framebufferSet.entityOutlineFramebuffer != null) {
            this.framebufferSet.entityOutlineFramebuffer = framePass.transfer(this.framebufferSet.entityOutlineFramebuffer);
        }
        Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
        Handle<Framebuffer> handle2 = this.framebufferSet.translucentFramebuffer;
        Handle<Framebuffer> handle3 = this.framebufferSet.itemEntityFramebuffer;
        Handle<Framebuffer> handle4 = this.framebufferSet.entityOutlineFramebuffer;
        framePass.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            float f = tickCounter.getTickProgress(false);
            Vec3d vec3d = camera.getPos();
            double d = vec3d.getX();
            double e = vec3d.getY();
            double g = vec3d.getZ();
            profiler.push("terrain");
            SectionRenderState sectionRenderState = this.renderBlockLayers((Matrix4fc)positionMatrix, d, e, g);
            sectionRenderState.renderSection(BlockRenderLayerGroup.OPAQUE);
            this.client.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.LEVEL);
            if (handle3 != null) {
                ((Framebuffer)handle3.get()).copyDepthFrom(this.client.getFramebuffer());
            }
            if (this.canDrawEntityOutlines() && handle4 != null) {
                Framebuffer framebuffer = (Framebuffer)handle4.get();
                RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.getColorAttachment(), 0, framebuffer.getDepthAttachment(), 1.0);
            }
            MatrixStack matrixStack = new MatrixStack();
            VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
            VertexConsumerProvider.Immediate immediate2 = this.bufferBuilders.getEffectVertexConsumers();
            profiler.swap("entities");
            this.renderedEntities.sort(ENTITY_COMPARATOR);
            this.renderEntities(matrixStack, immediate, camera, tickCounter, this.renderedEntities);
            immediate.drawCurrentLayer();
            this.checkEmpty(matrixStack);
            profiler.swap("blockentities");
            this.renderBlockEntities(matrixStack, immediate, immediate2, camera, f);
            immediate.drawCurrentLayer();
            this.checkEmpty(matrixStack);
            immediate.draw(RenderLayer.getSolid());
            immediate.draw(RenderLayer.getEndPortal());
            immediate.draw(RenderLayer.getEndGateway());
            immediate.draw(TexturedRenderLayers.getEntitySolid());
            immediate.draw(TexturedRenderLayers.getEntityCutout());
            immediate.draw(TexturedRenderLayers.getBeds());
            immediate.draw(TexturedRenderLayers.getShulkerBoxes());
            immediate.draw(TexturedRenderLayers.getSign());
            immediate.draw(TexturedRenderLayers.getHangingSign());
            immediate.draw(TexturedRenderLayers.getChest());
            this.bufferBuilders.getOutlineVertexConsumers().draw();
            if (renderBlockOutline) {
                this.renderTargetBlockOutline(camera, immediate, matrixStack, false);
            }
            profiler.swap("debug");
            this.client.debugRenderer.render(matrixStack, frustum, immediate, d, e, g);
            immediate.drawCurrentLayer();
            this.checkEmpty(matrixStack);
            immediate.draw(TexturedRenderLayers.getItemEntityTranslucentCull());
            immediate.draw(TexturedRenderLayers.getBannerPatterns());
            immediate.draw(TexturedRenderLayers.getShieldPatterns());
            immediate.draw(RenderLayer.getArmorEntityGlint());
            immediate.draw(RenderLayer.getGlint());
            immediate.draw(RenderLayer.getGlintTranslucent());
            immediate.draw(RenderLayer.getEntityGlint());
            profiler.swap("destroyProgress");
            this.renderBlockDamage(matrixStack, camera, immediate2);
            immediate2.draw();
            this.checkEmpty(matrixStack);
            immediate.draw(RenderLayer.getWaterMask());
            immediate.draw();
            if (handle2 != null) {
                ((Framebuffer)handle2.get()).copyDepthFrom((Framebuffer)handle.get());
            }
            profiler.swap("translucent");
            sectionRenderState.renderSection(BlockRenderLayerGroup.TRANSLUCENT);
            profiler.swap("string");
            sectionRenderState.renderSection(BlockRenderLayerGroup.TRIPWIRE);
            if (renderBlockOutline) {
                this.renderTargetBlockOutline(camera, immediate, matrixStack, true);
            }
            immediate.draw();
            profiler.pop();
        });
    }

    private void renderParticles(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickProgress, GpuBufferSlice fog) {
        FramePass framePass = frameGraphBuilder.createPass("particles");
        if (this.framebufferSet.particlesFramebuffer != null) {
            this.framebufferSet.particlesFramebuffer = framePass.transfer(this.framebufferSet.particlesFramebuffer);
            framePass.dependsOn(this.framebufferSet.mainFramebuffer);
        } else {
            this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        }
        Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
        Handle<Framebuffer> handle2 = this.framebufferSet.particlesFramebuffer;
        framePass.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            if (handle2 != null) {
                ((Framebuffer)handle2.get()).copyDepthFrom((Framebuffer)handle.get());
            }
            this.client.particleManager.renderParticles(camera, tickProgress, this.bufferBuilders.getEntityVertexConsumers());
        });
    }

    private void renderClouds(FrameGraphBuilder frameGraphBuilder, CloudRenderMode mode, Vec3d cameraPos, float cloudPhase, int color, float cloudHeight) {
        FramePass framePass = frameGraphBuilder.createPass("clouds");
        if (this.framebufferSet.cloudsFramebuffer != null) {
            this.framebufferSet.cloudsFramebuffer = framePass.transfer(this.framebufferSet.cloudsFramebuffer);
        } else {
            this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        }
        framePass.setRenderer(() -> this.cloudRenderer.renderClouds(color, mode, cloudHeight, cameraPos, cloudPhase));
    }

    private void renderWeather(FrameGraphBuilder frameGraphBuilder, Vec3d cameraPos, float tickProgress, GpuBufferSlice fog) {
        int i = this.client.options.getClampedViewDistance() * 16;
        float f = this.client.gameRenderer.getFarPlaneDistance();
        FramePass framePass = frameGraphBuilder.createPass("weather");
        if (this.framebufferSet.weatherFramebuffer != null) {
            this.framebufferSet.weatherFramebuffer = framePass.transfer(this.framebufferSet.weatherFramebuffer);
        } else {
            this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        }
        framePass.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
            this.weatherRendering.renderPrecipitation(this.client.world, immediate, this.ticks, tickProgress, cameraPos);
            this.worldBorderRendering.render(this.world.getWorldBorder(), cameraPos, 1, f);
            immediate.draw();
        });
    }

    private void renderLateDebug(FrameGraphBuilder frameGraphBuilder, Vec3d pos, GpuBufferSlice fog) {
        FramePass framePass = frameGraphBuilder.createPass("late_debug");
        this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        if (this.framebufferSet.itemEntityFramebuffer != null) {
            this.framebufferSet.itemEntityFramebuffer = framePass.transfer(this.framebufferSet.itemEntityFramebuffer);
        }
        Handle<Framebuffer> handle = this.framebufferSet.mainFramebuffer;
        framePass.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            MatrixStack matrixStack = new MatrixStack();
            VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
            this.client.debugRenderer.renderLate(matrixStack, immediate, vec3d.x, vec3d.y, vec3d.z);
            immediate.drawCurrentLayer();
            this.checkEmpty(matrixStack);
        });
    }

    private boolean getEntitiesToRender(Camera camera, Frustum frustum, List<Entity> output) {
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        boolean bl = false;
        boolean bl2 = this.canDrawEntityOutlines();
        Entity.setRenderDistanceMultiplier(MathHelper.clamp((double)this.client.options.getClampedViewDistance() / 8.0, 1.0, 2.5) * this.client.options.getEntityDistanceScaling().getValue());
        for (Entity entity : this.world.getEntities()) {
            BlockPos blockPos;
            if (!this.entityRenderDispatcher.shouldRender(entity, frustum, d, e, f) && !entity.hasPassengerDeep(this.client.player) || !this.world.isOutOfHeightLimit((blockPos = entity.getBlockPos()).getY()) && !this.isRenderingReady(blockPos) || entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()) || entity instanceof ClientPlayerEntity && camera.getFocusedEntity() != entity) continue;
            output.add(entity);
            if (!bl2 || !this.client.hasOutline(entity)) continue;
            bl = true;
        }
        return bl;
    }

    private void renderEntities(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, Camera camera, RenderTickCounter tickCounter, List<Entity> entities) {
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        TickManager tickManager = this.client.world.getTickManager();
        boolean bl = this.canDrawEntityOutlines();
        for (Entity entity : entities) {
            VertexConsumerProvider vertexConsumerProvider;
            if (entity.age == 0) {
                entity.lastRenderX = entity.getX();
                entity.lastRenderY = entity.getY();
                entity.lastRenderZ = entity.getZ();
            }
            if (bl && this.client.hasOutline(entity)) {
                OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                vertexConsumerProvider = outlineVertexConsumerProvider;
                int i = entity.getTeamColorValue();
                outlineVertexConsumerProvider.setColor(ColorHelper.getRed(i), ColorHelper.getGreen(i), ColorHelper.getBlue(i), 255);
            } else {
                vertexConsumerProvider = vertexConsumers;
            }
            float g = tickCounter.getTickProgress(!tickManager.shouldSkipTick(entity));
            this.renderEntity(entity, d, e, f, g, matrices, vertexConsumerProvider);
        }
    }

    private void renderBlockEntities(MatrixStack matrices, VertexConsumerProvider.Immediate entityVertexConsumers, VertexConsumerProvider.Immediate effectVertexConsumers, Camera camera, float tickProgress) {
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        for (ChunkBuilder.BuiltChunk builtChunk : this.builtChunks) {
            List<BlockEntity> list = builtChunk.getCurrentRenderData().getBlockEntities();
            if (list.isEmpty()) continue;
            for (BlockEntity blockEntity : list) {
                int i;
                BlockPos blockPos = blockEntity.getPos();
                VertexConsumerProvider vertexConsumerProvider = entityVertexConsumers;
                matrices.push();
                matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
                SortedSet sortedSet = (SortedSet)this.blockBreakingProgressions.get(blockPos.asLong());
                if (sortedSet != null && !sortedSet.isEmpty() && (i = ((BlockBreakingInfo)sortedSet.last()).getStage()) >= 0) {
                    MatrixStack.Entry entry = matrices.peek();
                    OverlayVertexConsumer vertexConsumer = new OverlayVertexConsumer(effectVertexConsumers.getBuffer(ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(i)), entry, 1.0f);
                    vertexConsumerProvider = renderLayer -> {
                        VertexConsumer vertexConsumer2 = entityVertexConsumers.getBuffer(renderLayer);
                        if (renderLayer.hasCrumbling()) {
                            return VertexConsumers.union(vertexConsumer, vertexConsumer2);
                        }
                        return vertexConsumer2;
                    };
                }
                this.blockEntityRenderDispatcher.render(blockEntity, tickProgress, matrices, vertexConsumerProvider);
                matrices.pop();
            }
        }
        Iterator<BlockEntity> iterator = this.world.getBlockEntities().iterator();
        while (iterator.hasNext()) {
            BlockEntity blockEntity2 = iterator.next();
            if (blockEntity2.isRemoved()) {
                iterator.remove();
                continue;
            }
            BlockPos blockPos2 = blockEntity2.getPos();
            matrices.push();
            matrices.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - f);
            this.blockEntityRenderDispatcher.render(blockEntity2, tickProgress, matrices, entityVertexConsumers);
            matrices.pop();
        }
    }

    private void renderBlockDamage(MatrixStack matrices, Camera camera, VertexConsumerProvider.Immediate vertexConsumers) {
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        for (Long2ObjectMap.Entry entry : this.blockBreakingProgressions.long2ObjectEntrySet()) {
            SortedSet sortedSet;
            BlockPos blockPos = BlockPos.fromLong(entry.getLongKey());
            if (blockPos.getSquaredDistanceFromCenter(d, e, f) > 1024.0 || (sortedSet = (SortedSet)entry.getValue()) == null || sortedSet.isEmpty()) continue;
            int i = ((BlockBreakingInfo)sortedSet.last()).getStage();
            matrices.push();
            matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
            MatrixStack.Entry entry2 = matrices.peek();
            OverlayVertexConsumer vertexConsumer = new OverlayVertexConsumer(vertexConsumers.getBuffer(ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.get(i)), entry2, 1.0f);
            this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos), blockPos, this.world, matrices, vertexConsumer);
            matrices.pop();
        }
    }

    private void renderTargetBlockOutline(Camera camera, VertexConsumerProvider.Immediate vertexConsumers, MatrixStack matrices, boolean translucent) {
        HitResult hitResult = this.client.crosshairTarget;
        if (!(hitResult instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult blockHitResult = (BlockHitResult)hitResult;
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState blockState = this.world.getBlockState(blockPos);
        if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos)) {
            VertexConsumer vertexConsumer;
            boolean bl = RenderLayers.getBlockLayer(blockState).isTranslucent();
            if (bl != translucent) {
                return;
            }
            Vec3d vec3d = camera.getPos();
            Boolean boolean_ = this.client.options.getHighContrastBlockOutline().getValue();
            if (boolean_.booleanValue()) {
                vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getSecondaryBlockOutline());
                this.drawBlockOutline(matrices, vertexConsumer, camera.getFocusedEntity(), vec3d.x, vec3d.y, vec3d.z, blockPos, blockState, -16777216);
            }
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
            int i = boolean_ != false ? Colors.CYAN : ColorHelper.withAlpha(102, Colors.BLACK);
            this.drawBlockOutline(matrices, vertexConsumer, camera.getFocusedEntity(), vec3d.x, vec3d.y, vec3d.z, blockPos, blockState, i);
            vertexConsumers.drawCurrentLayer();
        }
    }

    private void checkEmpty(MatrixStack matrices) {
        if (!matrices.isEmpty()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        double d = MathHelper.lerp((double)tickProgress, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)tickProgress, entity.lastRenderY, entity.getY());
        double f = MathHelper.lerp((double)tickProgress, entity.lastRenderZ, entity.getZ());
        this.entityRenderDispatcher.render(entity, d - cameraX, e - cameraY, f - cameraZ, tickProgress, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickProgress));
    }

    private void translucencySort(Vec3d cameraPos) {
        if (this.builtChunks.isEmpty()) {
            return;
        }
        BlockPos blockPos = BlockPos.ofFloored(cameraPos);
        boolean bl = !blockPos.equals(this.lastTranslucencySortCameraPos);
        Profilers.get().push("translucent_sort");
        NormalizedRelativePos normalizedRelativePos = new NormalizedRelativePos();
        for (ChunkBuilder.BuiltChunk builtChunk : this.nearbyChunks) {
            this.scheduleChunkTranslucencySort(builtChunk, normalizedRelativePos, cameraPos, bl, true);
        }
        this.chunkIndex %= this.builtChunks.size();
        int i = Math.max(this.builtChunks.size() / 8, 15);
        while (i-- > 0) {
            int j = this.chunkIndex++ % this.builtChunks.size();
            this.scheduleChunkTranslucencySort((ChunkBuilder.BuiltChunk)this.builtChunks.get(j), normalizedRelativePos, cameraPos, bl, false);
        }
        this.lastTranslucencySortCameraPos = blockPos;
        Profilers.get().pop();
    }

    private void scheduleChunkTranslucencySort(ChunkBuilder.BuiltChunk chunk, NormalizedRelativePos relativePos, Vec3d cameraPos, boolean needsUpdate, boolean ignoreCameraAlignment) {
        boolean bl2;
        relativePos.with(cameraPos, chunk.getSectionPos());
        boolean bl = chunk.getCurrentRenderData().hasPosition(relativePos);
        boolean bl3 = bl2 = needsUpdate && (relativePos.isOnCameraAxis() || ignoreCameraAlignment);
        if ((bl2 || bl) && !chunk.isCurrentlySorting() && chunk.hasTranslucentLayer()) {
            chunk.scheduleSort(this.chunkBuilder);
        }
    }

    private SectionRenderState renderBlockLayers(Matrix4fc matrix4fc, double d, double e, double f) {
        ObjectListIterator objectListIterator = this.builtChunks.listIterator(0);
        EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>> enumMap = new EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>>(BlockRenderLayer.class);
        int i = 0;
        for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
            enumMap.put(blockRenderLayer, new ArrayList());
        }
        ArrayList<DynamicUniforms.UniformValue> list = new ArrayList<DynamicUniforms.UniformValue>();
        Vector4f vector4f = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        Matrix4f matrix4f = new Matrix4f();
        while (objectListIterator.hasNext()) {
            ChunkBuilder.BuiltChunk builtChunk = (ChunkBuilder.BuiltChunk)objectListIterator.next();
            AbstractChunkRenderData abstractChunkRenderData = builtChunk.getCurrentRenderData();
            for (BlockRenderLayer blockRenderLayer2 : BlockRenderLayer.values()) {
                VertexFormat.IndexType indexType;
                GpuBuffer gpuBuffer;
                Buffers buffers = abstractChunkRenderData.getBuffersForLayer(blockRenderLayer2);
                if (buffers == null) continue;
                if (buffers.getIndexBuffer() == null) {
                    if (buffers.getIndexCount() > i) {
                        i = buffers.getIndexCount();
                    }
                    gpuBuffer = null;
                    indexType = null;
                } else {
                    gpuBuffer = buffers.getIndexBuffer();
                    indexType = buffers.getIndexType();
                }
                BlockPos blockPos = builtChunk.getOrigin();
                int j = list.size();
                list.add(new DynamicUniforms.UniformValue(matrix4fc, (Vector4fc)vector4f, (Vector3fc)new Vector3f((float)((double)blockPos.getX() - d), (float)((double)blockPos.getY() - e), (float)((double)blockPos.getZ() - f)), (Matrix4fc)matrix4f, 1.0f));
                enumMap.get((Object)blockRenderLayer2).add(new RenderPass.RenderObject<GpuBufferSlice[]>(0, buffers.getVertexBuffer(), gpuBuffer, indexType, 0, buffers.getIndexCount(), (gpuBufferSlices, uniformUploader) -> uniformUploader.upload("DynamicTransforms", gpuBufferSlices[j])));
            }
        }
        GpuBufferSlice[] gpuBufferSlices2 = RenderSystem.getDynamicUniforms().writeAll(list.toArray(new DynamicUniforms.UniformValue[0]));
        return new SectionRenderState(enumMap, i, gpuBufferSlices2);
    }

    public void rotate() {
        this.cloudRenderer.rotate();
    }

    public void captureFrustum() {
        this.shouldCaptureFrustum = true;
    }

    public void killFrustum() {
        this.capturedFrustum = null;
    }

    public void tick() {
        if (this.world.getTickManager().shouldTick()) {
            ++this.ticks;
        }
        if (this.ticks % 20 != 0) {
            return;
        }
        ObjectIterator iterator = this.blockBreakingInfos.values().iterator();
        while (iterator.hasNext()) {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)iterator.next();
            int i = blockBreakingInfo.getLastUpdateTick();
            if (this.ticks - 1 <= 400) continue;
            iterator.remove();
            this.removeBlockBreakingInfo(blockBreakingInfo);
        }
    }

    private void removeBlockBreakingInfo(BlockBreakingInfo info) {
        long l = info.getPos().asLong();
        Set set = (Set)this.blockBreakingProgressions.get(l);
        set.remove(info);
        if (set.isEmpty()) {
            this.blockBreakingProgressions.remove(l);
        }
    }

    private void renderSky(FrameGraphBuilder frameGraphBuilder, Camera camera, float tickProgress, GpuBufferSlice fog) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW || cameraSubmersionType == CameraSubmersionType.LAVA || this.hasBlindnessOrDarkness(camera)) {
            return;
        }
        DimensionEffects dimensionEffects = this.world.getDimensionEffects();
        DimensionEffects.SkyType skyType = dimensionEffects.getSkyType();
        if (skyType == DimensionEffects.SkyType.NONE) {
            return;
        }
        FramePass framePass = frameGraphBuilder.createPass("sky");
        this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
        framePass.setRenderer(() -> {
            RenderSystem.setShaderFog(fog);
            if (skyType == DimensionEffects.SkyType.END) {
                this.skyRendering.renderEndSky();
                return;
            }
            MatrixStack matrixStack = new MatrixStack();
            float g = this.world.getSkyAngleRadians(tickProgress);
            float h = this.world.getSkyAngle(tickProgress);
            float i = 1.0f - this.world.getRainGradient(tickProgress);
            float j = this.world.getStarBrightness(tickProgress) * i;
            int k = dimensionEffects.getSkyColor(h);
            int l = this.world.getMoonPhase();
            int m = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickProgress);
            float n = ColorHelper.getRedFloat(m);
            float o = ColorHelper.getGreenFloat(m);
            float p = ColorHelper.getBlueFloat(m);
            this.skyRendering.renderTopSky(n, o, p);
            VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
            if (dimensionEffects.isSunRisingOrSetting(h)) {
                this.skyRendering.renderGlowingSky(matrixStack, immediate, g, k);
            }
            this.skyRendering.renderCelestialBodies(matrixStack, immediate, h, l, i, j);
            immediate.draw();
            if (this.isSkyDark(tickProgress)) {
                this.skyRendering.renderSkyDark();
            }
        });
    }

    private boolean isSkyDark(float tickProgress) {
        return this.client.player.getCameraPosVec((float)tickProgress).y - this.world.net_minecraft_client_world_ClientWorld$Properties_getLevelProperties().getSkyDarknessHeight(this.world) < 0.0;
    }

    private boolean hasBlindnessOrDarkness(Camera camera) {
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity.hasStatusEffect(StatusEffects.BLINDNESS) || livingEntity.hasStatusEffect(StatusEffects.DARKNESS);
        }
        return false;
    }

    private void updateChunks(Camera camera) {
        Profiler profiler = Profilers.get();
        profiler.push("populate_sections_to_compile");
        ChunkRendererRegionBuilder chunkRendererRegionBuilder = new ChunkRendererRegionBuilder();
        BlockPos blockPos = camera.getBlockPos();
        ArrayList list = Lists.newArrayList();
        for (ChunkBuilder.BuiltChunk builtChunk : this.builtChunks) {
            if (!builtChunk.needsRebuild() || builtChunk.getCurrentRenderData() == ChunkRenderData.HIDDEN && !builtChunk.shouldBuild()) continue;
            boolean bl = false;
            if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.NEARBY) {
                BlockPos blockPos2 = ChunkSectionPos.from(builtChunk.getSectionPos()).getCenterPos();
                bl = blockPos2.getSquaredDistance(blockPos) < 768.0 || builtChunk.needsImportantRebuild();
            } else if (this.client.options.getChunkBuilderMode().getValue() == ChunkBuilderMode.PLAYER_AFFECTED) {
                bl = builtChunk.needsImportantRebuild();
            }
            if (bl) {
                profiler.push("build_near_sync");
                this.chunkBuilder.rebuild(builtChunk, chunkRendererRegionBuilder);
                builtChunk.cancelRebuild();
                profiler.pop();
                continue;
            }
            list.add(builtChunk);
        }
        profiler.swap("upload");
        this.chunkBuilder.upload();
        profiler.swap("schedule_async_compile");
        for (ChunkBuilder.BuiltChunk builtChunk : list) {
            builtChunk.scheduleRebuild(chunkRendererRegionBuilder);
            builtChunk.cancelRebuild();
        }
        profiler.pop();
        this.translucencySort(camera.getPos());
    }

    private void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, int color) {
        VertexRendering.drawOutline(matrices, vertexConsumer, state.getOutlineShape(this.world, pos, ShapeContext.of(entity)), (double)pos.getX() - cameraX, (double)pos.getY() - cameraY, (double)pos.getZ() - cameraZ, color);
    }

    public void updateBlock(BlockView world, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.scheduleSectionRender(pos, (flags & 8) != 0);
    }

    private void scheduleSectionRender(BlockPos pos, boolean important) {
        for (int i = pos.getZ() - 1; 1 <= pos.getZ() + 1; ++i) {
            for (int j = pos.getX() - 1; j <= pos.getX() + 1; ++j) {
                for (int k = pos.getY() - 1; k <= pos.getY() + 1; ++k) {
                    this.scheduleChunkRender(ChunkSectionPos.getSectionCoord(j), ChunkSectionPos.getSectionCoord(k), ChunkSectionPos.getSectionCoord(1), important);
                }
            }
        }
    }

    public void scheduleBlockRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i = minZ - 1; i <= maxZ + 1; ++i) {
            for (int j = minX - 1; j <= maxX + 1; ++j) {
                for (int k = minY - 1; k <= maxY + 1; ++k) {
                    this.scheduleChunkRender(ChunkSectionPos.getSectionCoord(j), ChunkSectionPos.getSectionCoord(k), ChunkSectionPos.getSectionCoord(i));
                }
            }
        }
    }

    public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
        if (this.client.getBakedModelManager().shouldRerender(old, updated)) {
            this.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void scheduleChunkRenders3x3x3(int x, int y, int z) {
        this.scheduleChunkRenders(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }

    public void scheduleChunkRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i = minZ; i <= maxZ; ++i) {
            for (int j = minX; j <= maxX; ++j) {
                for (int k = minY; k <= maxY; ++k) {
                    this.scheduleChunkRender(j, k, i);
                }
            }
        }
    }

    public void scheduleChunkRender(int chunkX, int chunkY, int chunkZ) {
        this.scheduleChunkRender(chunkX, chunkY, chunkZ, false);
    }

    private void scheduleChunkRender(int x, int y, int z, boolean important) {
        this.chunks.scheduleRebuild(x, y, z, important);
    }

    public void onChunkUnload(long sectionPos) {
        ChunkBuilder.BuiltChunk builtChunk = this.chunks.getRenderedChunk(sectionPos);
        if (builtChunk != null) {
            this.chunkRenderingDataPreparer.schedulePropagationFrom(builtChunk);
        }
    }

    public void addParticle(ParticleEffect parameters, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, force, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    public void addParticle(ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        try {
            this.spawnParticle(parameters, force, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Exception while adding particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being added");
            crashReportSection.add("ID", Registries.PARTICLE_TYPE.getId(parameters.getType()));
            crashReportSection.add("Parameters", () -> ParticleTypes.TYPE_CODEC.encodeStart(this.world.getRegistryManager().getOps(NbtOps.INSTANCE), (Object)parameters).toString());
            crashReportSection.add("Position", () -> CrashReportSection.createPositionString((HeightLimitView)this.world, x, y, z));
            throw new CrashException(crashReport);
        }
    }

    public <T extends ParticleEffect> void addParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    Particle spawnParticle(ParticleEffect parameters, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return this.spawnParticle(parameters, force, false, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Nullable
    private Particle spawnParticle(ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Camera camera = this.client.gameRenderer.getCamera();
        ParticlesMode particlesMode = this.getRandomParticleSpawnChance(canSpawnOnMinimal);
        if (force) {
            return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
        if (camera.getPos().squaredDistanceTo(x, y, z) > 1024.0) {
            return null;
        }
        if (particlesMode == ParticlesMode.MINIMAL) {
            return null;
        }
        return this.client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    private ParticlesMode getRandomParticleSpawnChance(boolean canSpawnOnMinimal) {
        ParticlesMode particlesMode = this.client.options.getParticles().getValue();
        if (canSpawnOnMinimal && particlesMode == ParticlesMode.MINIMAL && this.world.random.nextInt(10) == 0) {
            particlesMode = ParticlesMode.DECREASED;
        }
        if (particlesMode == ParticlesMode.DECREASED && this.world.random.nextInt(3) == 0) {
            particlesMode = ParticlesMode.MINIMAL;
        }
        return particlesMode;
    }

    public void setBlockBreakingInfo(int entityId, BlockPos pos, int stage) {
        if (stage < 0 || stage >= 10) {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.remove(entityId);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
        } else {
            BlockBreakingInfo blockBreakingInfo = (BlockBreakingInfo)this.blockBreakingInfos.get(entityId);
            if (blockBreakingInfo != null) {
                this.removeBlockBreakingInfo(blockBreakingInfo);
            }
            if (blockBreakingInfo == null || blockBreakingInfo.getPos().getX() != pos.getX() || blockBreakingInfo.getPos().getY() != pos.getY() || blockBreakingInfo.getPos().getZ() != pos.getZ()) {
                blockBreakingInfo = new BlockBreakingInfo(entityId, pos);
                this.blockBreakingInfos.put(entityId, (Object)blockBreakingInfo);
            }
            blockBreakingInfo.setStage(stage);
            blockBreakingInfo.setLastUpdateTick(this.ticks);
            ((SortedSet)this.blockBreakingProgressions.computeIfAbsent(blockBreakingInfo.getPos().asLong(), l -> Sets.newTreeSet())).add(blockBreakingInfo);
        }
    }

    public boolean isTerrainRenderComplete() {
        return this.chunkBuilder.isEmpty();
    }

    public void scheduleNeighborUpdates(ChunkPos chunkPos) {
        this.chunkRenderingDataPreparer.addNeighbors(chunkPos);
    }

    public void scheduleTerrainUpdate() {
        this.chunkRenderingDataPreparer.scheduleTerrainUpdate();
        this.cloudRenderer.scheduleTerrainUpdate();
    }

    public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return WorldRenderer.getLightmapCoordinates(BrightnessGetter.DEFAULT, world, world.getBlockState(pos), pos);
    }

    public static int getLightmapCoordinates(BrightnessGetter brightnessGetter, BlockRenderView world, BlockState state, BlockPos pos) {
        int k;
        if (state.hasEmissiveLighting(world, pos)) {
            return 0xF000F0;
        }
        int i = brightnessGetter.packedBrightness(world, pos);
        int j = LightmapTextureManager.getBlockLightCoordinates(1);
        if (j < (k = state.getLuminance())) {
            int l = LightmapTextureManager.getSkyLightCoordinates(1);
            return LightmapTextureManager.pack(k, l);
        }
        return 1;
    }

    public boolean isRenderingReady(BlockPos pos) {
        ChunkBuilder.BuiltChunk builtChunk = this.chunks.getRenderedChunk(pos);
        return builtChunk != null && builtChunk.currentRenderData.get() != ChunkRenderData.HIDDEN;
    }

    @Nullable
    public Framebuffer getEntityOutlinesFramebuffer() {
        return this.framebufferSet.entityOutlineFramebuffer != null ? this.framebufferSet.entityOutlineFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getTranslucentFramebuffer() {
        return this.framebufferSet.translucentFramebuffer != null ? this.framebufferSet.translucentFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getEntityFramebuffer() {
        return this.framebufferSet.itemEntityFramebuffer != null ? this.framebufferSet.itemEntityFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getParticlesFramebuffer() {
        return this.framebufferSet.particlesFramebuffer != null ? this.framebufferSet.particlesFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getWeatherFramebuffer() {
        return this.framebufferSet.weatherFramebuffer != null ? this.framebufferSet.weatherFramebuffer.get() : null;
    }

    @Nullable
    public Framebuffer getCloudsFramebuffer() {
        return this.framebufferSet.cloudsFramebuffer != null ? this.framebufferSet.cloudsFramebuffer.get() : null;
    }

    @Debug
    public ObjectArrayList<ChunkBuilder.BuiltChunk> getBuiltChunks() {
        return this.builtChunks;
    }

    @Debug
    public ChunkRenderingDataPreparer getChunkRenderingDataPreparer() {
        return this.chunkRenderingDataPreparer;
    }

    @Nullable
    public Frustum getCapturedFrustum() {
        return this.capturedFrustum;
    }

    public CloudRenderer getCloudRenderer() {
        return this.cloudRenderer;
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface BrightnessGetter {
        final static public BrightnessGetter DEFAULT = (world, pos) -> {
            int i = world.getLightLevel(LightType.SKY, pos);
            int j = world.getLightLevel(LightType.BLOCK, pos);
            return Brightness.pack(j, i);
        };

        public int packedBrightness(BlockRenderView var1, BlockPos var2);
    }
}

