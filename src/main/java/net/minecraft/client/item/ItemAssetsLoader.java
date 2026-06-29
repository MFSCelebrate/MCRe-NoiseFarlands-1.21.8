/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.ContextSwappableRegistryLookup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ItemAssetsLoader {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private ResourceFinder FINDER = ResourceFinder.json("items");

    public static CompletableFuture<Result> load(ResourceManager resourceManager, Executor executor) {
        DynamicRegistryManager.Immutable immutable = ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager();
        return CompletableFuture.supplyAsync(() -> FINDER.findResources(resourceManager), executor).thenCompose(itemAssets -> {
            ArrayList list = new ArrayList(itemAssets.size());
            itemAssets.forEach((itemId, itemResource) -> list.add(CompletableFuture.supplyAsync(() -> {
                Definition definition;
                block8: {
                    Identifier identifier2 = FINDER.toResourceId((Identifier)itemId);
                    BufferedReader reader = itemResource.getReader();
                    try {
                        ContextSwappableRegistryLookup contextSwappableRegistryLookup = new ContextSwappableRegistryLookup(immutable);
                        RegistryOps dynamicOps = contextSwappableRegistryLookup.createRegistryOps(JsonOps.INSTANCE);
                        ItemAsset itemAsset2 = ItemAsset.CODEC.parse(dynamicOps, (Object)StrictJsonParser.parse(reader)).ifError(error -> LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}", new Object[]{identifier2, itemResource.getPackId(), error.message()})).result().map(itemAsset -> {
                            if (contextSwappableRegistryLookup.hasEntries()) {
                                return itemAsset.withContextSwapper(contextSwappableRegistryLookup.createContextSwapper());
                            }
                            return itemAsset;
                        }).orElse(null);
                        definition = new Definition(identifier2, itemAsset2);
                        if (reader == null) break block8;
                    }
                    catch (Throwable throwable) {
                        try {
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
                        catch (Exception exception) {
                            LOGGER.error("Failed to open item model {} from pack '{}'", new Object[]{itemId, itemResource.getPackId(), exception});
                            return new Definition(identifier2, null);
                        }
                    }
                    ((Reader)reader).close();
                }
                return definition;
            }, executor)));
            return Util.combineSafe(list).thenApply(definitions -> {
                HashMap<Identifier, ItemAsset> map = new HashMap<Identifier, ItemAsset>();
                for (Definition definition : definitions) {
                    if (definition.clientItemInfo == null) continue;
                    map.put(definition.id, definition.clientItemInfo);
                }
                return new Result(map);
            });
        });
    }

    @Environment(value=EnvType.CLIENT)
    static final class Definition
    extends Record {
        final Identifier id;
        @Nullable
        final ItemAsset clientItemInfo;

        Definition(Identifier identifier, @Nullable ItemAsset itemAsset) {
            this.id = identifier;
            this.clientItemInfo = itemAsset;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Definition.class, "id;clientItemInfo", "id", "clientItemInfo"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Definition.class, "id;clientItemInfo", "id", "clientItemInfo"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Definition.class, "id;clientItemInfo", "id", "clientItemInfo"}, this, object);
        }

        public Identifier id() {
            return this.id;
        }

        @Nullable
        public ItemAsset clientItemInfo() {
            return this.clientItemInfo;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Result(Map<Identifier, ItemAsset> contents) {
    }
}

