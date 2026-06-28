/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkLoadingDebugRenderer
implements DebugRenderer.Renderer {
    final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    final private int LOADING_DATA_CHUNK_RANGE = 12;
    @Nullable
    private ChunkLoadingStatus loadingData;

    public ChunkLoadingDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        double d = Util.getMeasuringTimeNano();
        if (d - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = d;
            IntegratedServer integratedServer = this.client.getServer();
            this.loadingData = integratedServer != null ? new ChunkLoadingStatus(this, integratedServer, cameraX, cameraZ) : null;
        }
        if (this.loadingData != null) {
            Map map = this.loadingData.serverStates.getNow(null);
            double e = this.client.gameRenderer.getCamera().getPos().y * 0.85;
            for (Map.Entry<ChunkPos, String> entry : this.loadingData.clientStates.entrySet()) {
                ChunkPos chunkPos = entry.getKey();
                Object string = entry.getValue();
                if (map != null) {
                    string = (String)string + (String)map.get(chunkPos);
                }
                String[] strings = ((String)string).split("\n");
                int i = 0;
                for (String string2 : strings) {
                    DebugRenderer.drawString(matrices, vertexConsumers, string2, ChunkSectionPos.getOffsetPos(chunkPos.x, 8), e + (double)i, ChunkSectionPos.getOffsetPos(chunkPos.z, 8), Colors.WHITE, 0.15f, true, 0.0f, true);
                    i -= 2;
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    final class ChunkLoadingStatus {
        final Map<ChunkPos, String> clientStates;
        final CompletableFuture<Map<ChunkPos, String>> serverStates;

        ChunkLoadingStatus(ChunkLoadingDebugRenderer chunkLoadingDebugRenderer, IntegratedServer server, double x, double z) {
            ClientWorld clientWorld = chunkLoadingDebugRenderer.client.world;
            RegistryKey<World> registryKey = clientWorld.getRegistryKey();
            int i = ChunkSectionPos.getSectionCoord(x);
            int j = ChunkSectionPos.getSectionCoord(z);
            ImmutableMap.Builder builder = ImmutableMap.builder();
            ClientChunkManager clientChunkManager = clientWorld.net_minecraft_client_world_ClientChunkManager_getChunkManager();
            for (int k = i - 12; k <= i + 12; ++k) {
                for (int l = j - 12; l <= j + 12; ++l) {
                    ChunkPos chunkPos = new ChunkPos(k, l);
                    Object string = "";
                    WorldChunk worldChunk = clientChunkManager.getWorldChunk(k, l, false);
                    string = (String)string + "Client: ";
                    if (worldChunk == null) {
                        string = (String)string + "0n/a\n";
                    } else {
                        string = (String)string + (worldChunk.isEmpty() ? " E" : "");
                        string = (String)string + "\n";
                    }
                    builder.put((Object)chunkPos, string);
                }
            }
            this.clientStates = builder.build();
            this.serverStates = server.submit(() -> {
                ServerWorld serverWorld = server.getWorld(registryKey);
                if (serverWorld == null) {
                    return ImmutableMap.of();
                }
                ImmutableMap.Builder builder = ImmutableMap.builder();
                ServerChunkManager serverChunkManager = serverWorld.net_minecraft_server_world_ServerChunkManager_getChunkManager();
                for (int k = -11; k <= 13; ++k) {
                    for (int l = j - 12; l <= j + 12; ++l) {
                        ChunkPos chunkPos = new ChunkPos(k, l);
                        builder.put((Object)chunkPos, (Object)("Server: " + serverChunkManager.getChunkLoadingDebugInfo(chunkPos)));
                    }
                }
                return builder.build();
            });
        }
    }
}

