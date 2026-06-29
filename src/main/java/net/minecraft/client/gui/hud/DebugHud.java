/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixUtils
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.longs.LongSets$EmptySet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
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
 */
package net.minecraft.client.gui.hud;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.PacketSizeChart;
import net.minecraft.client.gui.hud.debug.PieChart;
import net.minecraft.client.gui.hud.debug.PingChart;
import net.minecraft.client.gui.hud.debug.RenderingChart;
import net.minecraft.client.gui.hud.debug.TickChart;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.profiler.ServerTickType;
import net.minecraft.util.profiler.log.DebugSampleType;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class DebugHud {
    final static private float DEBUG_CROSSHAIR_SCALE = 0.01f;
    final static private int field_57920 = 18;
    final static private int TEXT_COLOR = -2039584;
    final static private int field_32188 = 2;
    final static private int field_32189 = 2;
    final static private int field_32190 = 2;
    final static private Map<Heightmap.Type, String> HEIGHT_MAP_TYPES = Maps.newEnumMap(Map.of(Heightmap.Type.WORLD_SURFACE_WG, "SW", Heightmap.Type.WORLD_SURFACE, "S", Heightmap.Type.OCEAN_FLOOR_WG, "OW", Heightmap.Type.OCEAN_FLOOR, "O", Heightmap.Type.MOTION_BLOCKING, "M", Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML"));
    final private MinecraftClient client;
    final private AllocationRateCalculator allocationRateCalculator;
    final private TextRenderer textRenderer;
    final private GpuBuffer debugCrosshairBuffer;
    final private RenderSystem.ShapeIndexBuffer debugCrosshairIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.LINES);
    private HitResult blockHit;
    private HitResult fluidHit;
    @Nullable
    private ChunkPos pos;
    @Nullable
    private WorldChunk chunk;
    @Nullable
    private CompletableFuture<WorldChunk> chunkFuture;
    private boolean showDebugHud;
    private boolean renderingChartVisible;
    private boolean renderingAndTickChartsVisible;
    private boolean packetSizeAndPingChartsVisible;
    final private MultiValueDebugSampleLogImpl frameNanosLog = new MultiValueDebugSampleLogImpl(1);
    final private MultiValueDebugSampleLogImpl tickNanosLog = new MultiValueDebugSampleLogImpl(ServerTickType.values().length);
    final private MultiValueDebugSampleLogImpl pingLog = new MultiValueDebugSampleLogImpl(1);
    final private MultiValueDebugSampleLogImpl packetSizeLog = new MultiValueDebugSampleLogImpl(1);
    final private Map<DebugSampleType, MultiValueDebugSampleLogImpl> receivedDebugSamples = Map.of(DebugSampleType.TICK_TIME, this.tickNanosLog);
    final private RenderingChart renderingChart;
    final private TickChart tickChart;
    final private PingChart pingChart;
    final private PacketSizeChart packetSizeChart;
    final private PieChart pieChart;

    public DebugHud(MinecraftClient client) {
        this.client = client;
        this.allocationRateCalculator = new AllocationRateCalculator();
        this.textRenderer = client.textRenderer;
        this.renderingChart = new RenderingChart(this.textRenderer, this.frameNanosLog);
        this.tickChart = new TickChart(this.textRenderer, this.tickNanosLog, () -> Float.valueOf(this.client.world.getTickManager().getMillisPerTick()));
        this.pingChart = new PingChart(this.textRenderer, this.pingLog);
        this.packetSizeChart = new PacketSizeChart(this.textRenderer, this.packetSizeLog);
        this.pieChart = new PieChart(this.textRenderer);
        BufferAllocator bufferAllocator = BufferAllocator.method_72201(VertexFormats.POSITION_COLOR_NORMAL.getVertexSize() * 12);
        try {
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);
            bufferBuilder.vertex(0.0f, 0.0f, 0.0f).color(Colors.RED).normal(1.0f, 0.0f, 0.0f);
            bufferBuilder.vertex(1.0f, 0.0f, 0.0f).color(Colors.RED).normal(1.0f, 0.0f, 0.0f);
            bufferBuilder.vertex(0.0f, 0.0f, 0.0f).color(Colors.GREEN).normal(0.0f, 1.0f, 0.0f);
            bufferBuilder.vertex(0.0f, 1.0f, 0.0f).color(Colors.GREEN).normal(0.0f, 1.0f, 0.0f);
            bufferBuilder.vertex(0.0f, 0.0f, 0.0f).color(-8421377).normal(0.0f, 0.0f, 1.0f);
            bufferBuilder.vertex(0.0f, 0.0f, 1.0f).color(-8421377).normal(0.0f, 0.0f, 1.0f);
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                this.debugCrosshairBuffer = RenderSystem.getDevice().createBuffer(() -> "Crosshair vertex buffer", 32, builtBuffer.getBuffer());
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

    public void resetChunk() {
        this.chunkFuture = null;
        this.chunk = null;
    }

    public void render(DrawContext context) {
        int k;
        int j;
        int i;
        Profiler profiler = Profilers.get();
        profiler.push("debug");
        Entity entity = this.client.getCameraEntity();
        this.blockHit = entity.raycast(20.0, 0.0f, false);
        this.fluidHit = entity.raycast(20.0, 0.0f, true);
        this.drawLeftText(context);
        this.drawRightText(context);
        context.createNewRootLayer();
        this.pieChart.setBottomMargin(10);
        if (this.renderingAndTickChartsVisible) {
            i = context.getScaledWindowWidth();
            j = i / 2;
            this.renderingChart.render(context, 0, this.renderingChart.getWidth(j));
            if (this.tickNanosLog.getLength() > 0) {
                k = this.tickChart.getWidth(j);
                this.tickChart.render(context, i - k, k);
            }
            this.pieChart.setBottomMargin(this.tickChart.getHeight());
        }
        if (this.packetSizeAndPingChartsVisible) {
            i = context.getScaledWindowWidth();
            j = i / 2;
            if (!this.client.isInSingleplayer()) {
                this.packetSizeChart.render(context, 0, this.packetSizeChart.getWidth(j));
            }
            k = this.pingChart.getWidth(j);
            this.pingChart.render(context, i - k, k);
            this.pieChart.setBottomMargin(this.pingChart.getHeight());
        }
        ScopedProfiler scopedProfiler = profiler.scoped("profilerPie");
        try {
            this.pieChart.render(context);
            if (scopedProfiler != null) {
                scopedProfiler.close();
            }
        }
        catch (Throwable throwable) {
            if (scopedProfiler != null) {
                try {
                    scopedProfiler.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
        profiler.pop();
    }

    protected void drawLeftText(DrawContext context) {
        List<String> list = this.getLeftText();
        list.add("");
        boolean bl = this.client.getServer() != null;
        list.add("Debug charts: [F3+1] Profiler " + (this.renderingChartVisible ? "visible" : "hidden") + "; [F3+2] " + (bl ? "FPS + TPS " : "FPS ") + (this.renderingAndTickChartsVisible ? "visible" : "hidden") + "; [F3+3] " + (!this.client.isInSingleplayer() ? "Bandwidth + Ping" : "Ping") + (this.packetSizeAndPingChartsVisible ? " visible" : " hidden"));
        list.add("For help: press F3 + Q");
        this.drawText(context, list, true);
    }

    protected void drawRightText(DrawContext context) {
        List<String> list = this.getRightText();
        this.drawText(context, list, false);
    }

    private void drawText(DrawContext context, List<String> text, boolean left) {
        int m;
        int l;
        int k;
        String string;
        int j;
        int i = this.textRenderer.fontHeight;
        for (j = 0; j < text.size(); ++j) {
            string = text.get(j);
            if (Strings.isNullOrEmpty((String)string)) continue;
            k = this.textRenderer.getWidth(string);
            l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
            m = 2 + i * j;
            context.fill(l - 1, m - 1, l + k + 1, m + i - 1, -1873784752);
        }
        for (j = 0; j < text.size(); ++j) {
            string = text.get(j);
            if (Strings.isNullOrEmpty((String)string)) continue;
            k = this.textRenderer.getWidth(string);
            l = left ? 2 : context.getScaledWindowWidth() - 2 - k;
            m = 2 + i * j;
            context.drawText(this.textRenderer, string, l, m, -2039584, false);
        }
    }

    protected List<String> getLeftText() {
        Identifier identifier;
        World world;
        String string3;
        IntegratedServer integratedServer = this.client.getServer();
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        ClientConnection clientConnection = clientPlayNetworkHandler.getConnection();
        float f = clientConnection.getAveragePacketsSent();
        float g = clientConnection.getAveragePacketsReceived();
        TickManager tickManager = this.getWorld().getTickManager();
        String string = tickManager.isStepping() ? " (frozen - stepping)" : (tickManager.isFrozen() ? " (frozen)" : "");
        if (integratedServer != null) {
            ServerTickManager serverTickManager = integratedServer.getTickManager();
            boolean bl = serverTickManager.isSprinting();
            if (bl) {
                string = " (sprinting)";
            }
            String string2 = bl ? "-" : String.format(Locale.ROOT, "%.1f", Float.valueOf(tickManager.getMillisPerTick()));
            string3 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", Float.valueOf(integratedServer.getAverageTickTime()), string2, string, Float.valueOf(f), Float.valueOf(g));
        } else {
            string3 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", clientPlayNetworkHandler.getBrand(), string, Float.valueOf(f), Float.valueOf(g));
        }
        BlockPos blockPos = this.client.getCameraEntity().getBlockPos();
        if (this.client.hasReducedDebugInfo()) {
            return List.of("Minecraft - MCRe NoiseFarlandsJava " + SharedConstants.getGameVersion().name() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.client.fpsDebugString, string3, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.asString(), "", String.format(Locale.ROOT, "Chunk-relative: %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF));
        }
        Entity entity = this.client.getCameraEntity();
        Direction direction = entity.getHorizontalFacing();
        String string4 = switch (direction) {
            case Direction.NORTH -> "Towards negative Z";
            case Direction.SOUTH -> "Towards positive Z";
            case Direction.WEST -> "Towards negative X";
            case Direction.EAST -> "Towards positive X";
            default -> "Invalid";
        };
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!Objects.equals(this.pos, chunkPos)) {
            this.pos = chunkPos;
            this.resetChunk();
        }
        LongSet longSet = (world = this.getWorld()) instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET;
        ArrayList list = Lists.newArrayList((Object[])new String[]{"Minecraft - MCRe NoiseFarlandsJava " + SharedConstants.getGameVersion().name() + " (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + (String)("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType()) + ")", this.client.fpsDebugString, string3, this.client.worldRenderer.getChunksDebugString(), this.client.worldRenderer.getEntitiesDebugString(), "P: " + this.client.particleManager.getDebugString() + ". T: " + this.client.world.getRegularEntityCount(), this.client.world.asString()});
        String string5 = this.getServerWorldDebugString();
        if (string5 != null) {
            list.add(string5);
        }
        list.add(String.valueOf(this.client.world.getRegistryKey().getValue()) + " FC: " + longSet.size());
        list.add("");
        list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.client.getCameraEntity().getX(), this.client.getCameraEntity().getY(), this.client.getCameraEntity().getZ()));
        list.add(String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF));
        list.add(String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", chunkPos.x, ChunkSectionPos.getSectionCoord(blockPos.getY()), chunkPos.z, chunkPos.getRegionRelativeX(), chunkPos.getRegionRelativeZ(), chunkPos.getRegionX(), chunkPos.getRegionZ()));
        list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string4, Float.valueOf(MathHelper.wrapDegrees(entity.getYaw())), Float.valueOf(MathHelper.wrapDegrees(entity.getPitch()))));
        WorldChunk worldChunk = this.getClientChunk();
        if (worldChunk.isEmpty()) {
            list.add("Server is Waiting for chunk...");
        } else {
            int i = this.client.world.getChunkManager().getLightingProvider().getLight(blockPos, 0);
            int j = this.client.world.getLightLevel(LightType.SKY, blockPos);
            int k = this.client.world.getLightLevel(LightType.BLOCK, blockPos);
            list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
            WorldChunk worldChunk2 = this.getChunk();
            StringBuilder stringBuilder = new StringBuilder("CH");
            for (Heightmap.Type type : Heightmap.Type.values()) {
                if (!type.shouldSendToClient()) continue;
                stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get(type)).append(": ").append(worldChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
            }
            list.add(stringBuilder.toString());
            stringBuilder.setLength(0);
            stringBuilder.append("SH");
            for (Heightmap.Type type : Heightmap.Type.values()) {
                if (!type.isStoredServerSide()) continue;
                stringBuilder.append(" ").append(HEIGHT_MAP_TYPES.get(type)).append(": ");
                if (worldChunk2 != null) {
                    stringBuilder.append(worldChunk2.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                    continue;
                }
                stringBuilder.append("??");
            }
            list.add(stringBuilder.toString());
            if (this.client.world.isInHeightLimit(blockPos.getY())) {
                list.add("Biome: " + DebugHud.getBiomeString(this.client.world.getBiome(blockPos)));
                if (worldChunk2 != null) {
                    float h = world.getMoonSize();
                    long l = worldChunk2.getInhabitedTime();
                    LocalDifficulty localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), l, h);
                    list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", Float.valueOf(localDifficulty.getLocalDifficulty()), Float.valueOf(localDifficulty.getClampedLocalDifficulty()), this.client.world.getTimeOfDay() / 24000L));
                } else {
                    list.add("Local Difficulty: ??");
                }
            }
            if (worldChunk2 != null && worldChunk2.usesOldNoise()) {
                list.add("Blending: Old");
            }
        }
        ServerWorld serverWorld = this.getServerWorld();
        if (serverWorld != null) {
            ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
            ChunkGenerator chunkGenerator = serverChunkManager.getChunkGenerator();
            NoiseConfig noiseConfig = serverChunkManager.getNoiseConfig();
            chunkGenerator.appendDebugHudText(list, noiseConfig, blockPos);
            MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler = noiseConfig.getMultiNoiseSampler();
            BiomeSource biomeSource = chunkGenerator.getBiomeSource();
            biomeSource.addDebugInfo(list, blockPos, multiNoiseSampler);
            SpawnHelper.Info info = serverChunkManager.getSpawnInfo();
            if (info != null) {
                Object2IntMap<SpawnGroup> object2IntMap = info.getGroupToCount();
                int m = info.getSpawningChunkCount();
                list.add("SC: " + m + ", " + Stream.of(SpawnGroup.values()).map(group -> Character.toUpperCase(group.getName().charAt(0)) + ": " + object2IntMap.getInt(group)).collect(Collectors.joining(", ")));
            } else {
                list.add("SC: N/A");
            }
        }
        if ((identifier = this.client.gameRenderer.getPostProcessorId()) != null) {
            list.add("Post: " + String.valueOf(identifier));
        }
        list.add(this.client.getSoundManager().getDebugString() + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.client.player.getMoodPercentage() * 100.0f)));
        return list;
    }

    private static String getBiomeString(RegistryEntry<Biome> biome) {
        return (String)biome.getKeyOrValue().map(biomeKey -> biomeKey.getValue().toString(), biome_ -> "[unregistered " + String.valueOf(biome_) + "]");
    }

    @Nullable
    private ServerWorld getServerWorld() {
        IntegratedServer integratedServer = this.client.getServer();
        if (integratedServer != null) {
            return integratedServer.getWorld(this.client.world.getRegistryKey());
        }
        return null;
    }

    @Nullable
    private String getServerWorldDebugString() {
        ServerWorld serverWorld = this.getServerWorld();
        if (serverWorld != null) {
            return serverWorld.asString();
        }
        return null;
    }

    private World getWorld() {
        return (World)DataFixUtils.orElse(Optional.ofNullable(this.client.getServer()).flatMap(server -> Optional.ofNullable(server.getWorld(this.client.world.getRegistryKey()))), (Object)this.client.world);
    }

    @Nullable
    private WorldChunk getChunk() {
        if (this.chunkFuture == null) {
            ServerWorld serverWorld = this.getServerWorld();
            if (serverWorld == null) {
                return null;
            }
            this.chunkFuture = serverWorld.getChunkManager()
    .getChunkFutureSyncOnMainThread(this.pos.x, this.pos.z, ChunkStatus.FULL, false)
    .thenApply(chunk -> (WorldChunk) chunk.orElse(null));
        }
        return this.chunkFuture.getNow(null);
    }

    private WorldChunk getClientChunk() {
        if (this.chunk == null) {
            this.chunk = this.client.world.getChunk(this.pos.x, this.pos.z);
        }
        return this.chunk;
    }

    protected List<String> getRightText() {
        Entity entity;
        BlockPos blockPos;
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        GpuDevice gpuDevice = RenderSystem.getDevice();
        ArrayList list = Lists.newArrayList((Object[])new String[]{String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")), String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", o * 100L / l, DebugHud.toMiB(o), DebugHud.toMiB(l)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", DebugHud.toMiB(this.allocationRateCalculator.get(o))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", m * 100L / l, DebugHud.toMiB(m)), "", String.format(Locale.ROOT, "CPU: %s", GLX._getCpuInfo()), "", String.format(Locale.ROOT, "Display: %dx%d (%s)", MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), gpuDevice.getVendor()), gpuDevice.getRenderer(), String.format(Locale.ROOT, "%s %s", gpuDevice.getBackendName(), gpuDevice.getVersion())});
        if (this.client.hasReducedDebugInfo()) {
            return list;
        }
        if (this.blockHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.blockHit).getBlockPos();
            BlockState blockState = this.client.world.getBlockState(blockPos);
            list.add("");
            list.add(String.valueOf(Formatting.UNDERLINE) + "Targeted Block: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            list.add(String.valueOf(Registries.BLOCK.getId(blockState.getBlock())));
            for (Map.Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            blockState.streamTags().map(tag -> "#" + String.valueOf(tag.id())).forEach(list::add);
        }
        if (this.fluidHit.getType() == HitResult.Type.BLOCK) {
            blockPos = ((BlockHitResult)this.fluidHit).getBlockPos();
            FluidState fluidState = this.client.world.getFluidState(blockPos);
            list.add("");
            list.add(String.valueOf(Formatting.UNDERLINE) + "Targeted Fluid: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ());
            list.add(String.valueOf(Registries.FLUID.getId(fluidState.getFluid())));
            for (Map.Entry<Property<?>, Comparable<?>> entry : fluidState.getEntries().entrySet()) {
                list.add(this.propertyToString(entry));
            }
            fluidState.streamTags().map(tag -> "#" + String.valueOf(tag.id())).forEach(list::add);
        }
        if ((entity = this.client.targetedEntity) != null) {
            list.add("");
            list.add(String.valueOf(Formatting.UNDERLINE) + "Targeted Entity");
            list.add(String.valueOf(Registries.ENTITY_TYPE.getId(entity.getType())));
        }
        return list;
    }

    private String propertyToString(Map.Entry<Property<?>, Comparable<?>> propEntry) {
        Property<?> property = propEntry.getKey();
        Comparable<?> comparable = propEntry.getValue();
        Object string = Util.getValueAsString(property, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            string = String.valueOf(Formatting.GREEN) + (String)string;
        } else if (Boolean.FALSE.equals(comparable)) {
            string = String.valueOf(Formatting.RED) + (String)string;
        }
        return property.getName() + ": " + (String)string;
    }

    private static long toMiB(long bytes) {
        return bytes / 1024L / 1024L;
    }

    public boolean shouldShowDebugHud() {
        return this.showDebugHud && !this.client.options.hudHidden;
    }

    public boolean shouldShowRenderingChart() {
        return this.shouldShowDebugHud() && this.renderingChartVisible;
    }

    public boolean shouldShowPacketSizeAndPingCharts() {
        return this.shouldShowDebugHud() && this.packetSizeAndPingChartsVisible;
    }

    public boolean shouldRenderTickCharts() {
        return this.shouldShowDebugHud() && this.renderingAndTickChartsVisible;
    }

    public void toggleDebugHud() {
        this.showDebugHud = !this.showDebugHud;
    }

    public void togglePacketSizeAndPingCharts() {
        boolean bl = this.packetSizeAndPingChartsVisible = !this.showDebugHud || !this.packetSizeAndPingChartsVisible;
        if (this.packetSizeAndPingChartsVisible) {
            this.showDebugHud = true;
            this.renderingAndTickChartsVisible = false;
        }
    }

    public void toggleRenderingAndTickCharts() {
        boolean bl = this.renderingAndTickChartsVisible = !this.showDebugHud || !this.renderingAndTickChartsVisible;
        if (this.renderingAndTickChartsVisible) {
            this.showDebugHud = true;
            this.packetSizeAndPingChartsVisible = false;
        }
    }

    public void toggleRenderingChart() {
        boolean bl = this.renderingChartVisible = !this.showDebugHud || !this.renderingChartVisible;
        if (this.renderingChartVisible) {
            this.showDebugHud = true;
        }
    }

    public void pushToFrameLog(long value) {
        this.frameNanosLog.push(value);
    }

    public MultiValueDebugSampleLogImpl getTickNanosLog() {
        return this.tickNanosLog;
    }

    public MultiValueDebugSampleLogImpl getPingLog() {
        return this.pingLog;
    }

    public MultiValueDebugSampleLogImpl getPacketSizeLog() {
        return this.packetSizeLog;
    }

    public PieChart getPieChart() {
        return this.pieChart;
    }

    public void set(long[] values, DebugSampleType type) {
        MultiValueDebugSampleLogImpl multiValueDebugSampleLogImpl = this.receivedDebugSamples.get((Object)type);
        if (multiValueDebugSampleLogImpl != null) {
            multiValueDebugSampleLogImpl.set(values);
        }
    }

    public void clear() {
        this.showDebugHud = false;
        this.tickNanosLog.clear();
        this.pingLog.clear();
        this.packetSizeLog.clear();
    }

    public void renderDebugCrosshair(Camera camera) {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.translate(0.0f, 0.0f, -1.0f);
        matrix4fStack.rotateX(camera.getPitch() * ((float)Math.PI / 180));
        matrix4fStack.rotateY(camera.getYaw() * ((float)Math.PI / 180));
        float f = 0.01f * (float)this.client.getWindow().getScaleFactor();
        matrix4fStack.scale(-f, f, -f);
        RenderPipeline renderPipeline = RenderPipelines.LINES;
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        GpuTextureView gpuTextureView = framebuffer.getColorAttachmentView();
        GpuTextureView gpuTextureView2 = framebuffer.getDepthAttachmentView();
        GpuBuffer gpuBuffer = this.debugCrosshairIndexBuffer.getIndexBuffer(18);
        GpuBufferSlice[] gpuBufferSlices = RenderSystem.getDynamicUniforms().writeAll(new DynamicUniforms.UniformValue((Matrix4fc)new Matrix4f((Matrix4fc)matrix4fStack), (Vector4fc)new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 4.0f), new DynamicUniforms.UniformValue((Matrix4fc)new Matrix4f((Matrix4fc)matrix4fStack), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f(), 2.0f));
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "3d crosshair", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());
        try {
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setVertexBuffer(0, this.debugCrosshairBuffer);
            renderPass.setIndexBuffer(gpuBuffer, this.debugCrosshairIndexBuffer.getIndexType());
            renderPass.setUniform("DynamicTransforms", gpuBufferSlices[0]);
            renderPass.drawIndexed(0, 0, 18, 1);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlices[1]);
            renderPass.drawIndexed(0, 0, 18, 1);
            if (renderPass != null) {
                renderPass.close();
            }
        }
        catch (Throwable throwable) {
            if (renderPass != null) {
                try {
                    renderPass.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
            }
            throw throwable;
        }
        matrix4fStack.popMatrix();
    }

    @Environment(value=EnvType.CLIENT)
    static class AllocationRateCalculator {
        final static private int INTERVAL = 500;
        final static private List<GarbageCollectorMXBean> GARBAGE_COLLECTORS = ManagementFactory.getGarbageCollectorMXBeans();
        private long lastCalculated = 0L;
        private long allocatedBytes = -1L;
        private long collectionCount = -1L;
        private long allocationRate = 0L;

        AllocationRateCalculator() {
        }

        long get(long allocatedBytes) {
            long l = System.currentTimeMillis();
            if (l - this.lastCalculated < 500L) {
                return this.allocationRate;
            }
            long m = AllocationRateCalculator.getCollectionCount();
            if (this.lastCalculated != 0L && m == this.collectionCount) {
                double d = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(l - this.lastCalculated);
                long n = allocatedBytes - this.allocatedBytes;
                this.allocationRate = Math.round((double)n * d);
            }
            this.lastCalculated = l;
            this.allocatedBytes = allocatedBytes;
            this.collectionCount = m;
            return this.allocationRate;
        }

        private static long getCollectionCount() {
            long l = 0L;
            for (GarbageCollectorMXBean garbageCollectorMXBean : GARBAGE_COLLECTORS) {
                l += garbageCollectorMXBean.getCollectionCount();
            }
            return l;
        }
    }
}

