/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class UploadTokenCache {
    final static private Long2ObjectMap<String> TOKEN_CACHE = new Long2ObjectOpenHashMap();

    public static String get(long worldId) {
        return (String)TOKEN_CACHE.get(worldId);
    }

    public static void invalidate(long world) {
        TOKEN_CACHE.remove(world);
    }

    public static void put(long wid, @Nullable String token) {
        TOKEN_CACHE.put(wid, (Object)token);
    }
}

