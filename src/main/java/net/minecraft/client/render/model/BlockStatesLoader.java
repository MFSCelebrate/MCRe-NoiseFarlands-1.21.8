/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BlockStateManagers;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BlockStatesLoader {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private ResourceFinder FINDER = ResourceFinder.json("blockstates");

    public static CompletableFuture<LoadedModels> load(ResourceManager resourceManager, Executor prepareExecutor) {
        Function<Identifier, StateManager<Block, BlockState>> function = BlockStateManagers.createIdToManagerMapper();
        return CompletableFuture.supplyAsync(() -> FINDER.findAllResources(resourceManager), prepareExecutor).thenCompose(resourceMap -> {
            ArrayList<CompletableFuture<LoadedModels>> list = new ArrayList<CompletableFuture<LoadedModels>>(resourceMap.size());
            for (Map.Entry entry : resourceMap.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    Identifier identifier = FINDER.toResourceId((Identifier)entry.getKey());
                    StateManager stateManager = (StateManager)function.apply(identifier);
                    if (stateManager == null) {
                        LOGGER.debug("Discovered unknown block state definition {}, ignoring", (Object)identifier);
                        return null;
                    }
                    List list = (List)entry.getValue();
                    ArrayList<LoadedBlockStateDefinition> list2 = new ArrayList<LoadedBlockStateDefinition>(list.size());
                    for (Resource resource : list) {
                        try {
                            BufferedReader reader = resource.getReader();
                            try {
                                JsonElement jsonElement = StrictJsonParser.parse(reader);
                                BlockModelDefinition blockModelDefinition = (BlockModelDefinition)BlockModelDefinition.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(JsonParseException::new);
                                list2.add(new LoadedBlockStateDefinition(resource.getPackId(), blockModelDefinition));
                                if (reader == null) continue;
                            }
                            catch (Throwable throwable) {
                                if (reader != null) {
                                    try {
                                        ((Reader)reader).close();
                                    }
                                    catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            }
                            ((Reader)reader).close();
                        }
                        catch (Exception exception) {
                            LOGGER.error("Failed to load blockstate definition {} from pack {}", new Object[]{identifier, resource.getPackId(), exception});
                        }
                    }
                    try {
                        return BlockStatesLoader.combine(identifier, stateManager, list2);
                    }
                    catch (Exception exception2) {
                        LOGGER.error("Failed to load blockstate definition {}", (Object)identifier, (Object)exception2);
                        return null;
                    }
                }, prepareExecutor));
            }
            return Util.combineSafe(list).thenApply(definitions -> {
                IdentityHashMap<BlockState, BlockStateModel.UnbakedGrouped> map = new IdentityHashMap<BlockState, BlockStateModel.UnbakedGrouped>();
                for (LoadedModels loadedModels : definitions) {
                    if (loadedModels == null) continue;
                    map.putAll(loadedModels.models());
                }
                return new LoadedModels(map);
            });
        });
    }

    private static LoadedModels combine(Identifier id, StateManager<Block, BlockState> stateManager, List<LoadedBlockStateDefinition> definitions) {
        IdentityHashMap<BlockState, BlockStateModel.UnbakedGrouped> map = new IdentityHashMap<BlockState, BlockStateModel.UnbakedGrouped>();
        for (LoadedBlockStateDefinition loadedBlockStateDefinition : definitions) {
            map.putAll(loadedBlockStateDefinition.contents.load(stateManager, () -> String.valueOf(id) + "/" + loadedBlockStateDefinition.source));
        }
        return new LoadedModels(map);
    }

    @Environment(value=EnvType.CLIENT)
    static final class LoadedBlockStateDefinition
    extends Record {
        final String source;
        final BlockModelDefinition contents;

        LoadedBlockStateDefinition(String string, BlockModelDefinition blockModelDefinition) {
            this.source = string;
            this.contents = blockModelDefinition;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{LoadedBlockStateDefinition.class, "source;contents", "source", "contents"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LoadedBlockStateDefinition.class, "source;contents", "source", "contents"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LoadedBlockStateDefinition.class, "source;contents", "source", "contents"}, this, object);
        }

        public String source() {
            return this.source;
        }

        public BlockModelDefinition contents() {
            return this.contents;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record LoadedModels(Map<BlockState, BlockStateModel.UnbakedGrouped> models) {
    }
}

