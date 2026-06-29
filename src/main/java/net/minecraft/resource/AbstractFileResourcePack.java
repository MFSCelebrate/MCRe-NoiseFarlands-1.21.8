/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class AbstractFileResourcePack
implements ResourcePack {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private ResourcePackInfo info;

    protected AbstractFileResourcePack(ResourcePackInfo info) {
        this.info = info;
    }

    @Override
    @Nullable
    public <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer) throws IOException {
        T t;
        block6: {
            InputSupplier<InputStream> inputSupplier = this.openRoot("pack.mcmeta");
            if (inputSupplier == null) {
                return null;
            }
            InputStream inputStream = inputSupplier.get();
            try {
                t = AbstractFileResourcePack.parseMetadata(metadataSerializer, inputStream);
                if (inputStream == null) break block6;
            }
            catch (Throwable throwable) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            inputStream.close();
        }
        return t;
    }

    @Nullable
    public static <T> T parseMetadata(ResourceMetadataSerializer<T> metadataSerializer, InputStream inputStream) {
        JsonObject jsonObject;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
            jsonObject = JsonHelper.deserialize(bufferedReader);
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load {} metadata", (Object)metadataSerializer.name(), (Object)exception);
            return null;
        }
        if (!jsonObject.has(metadataSerializer.name())) {
            return null;
        }
        return metadataSerializer.codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonObject.get(metadataSerializer.name())).ifError(error -> LOGGER.error("Couldn't load {} metadata: {}", (Object)metadataSerializer.name(), error)).result().orElse(null);
    }

    @Override
    public ResourcePackInfo getInfo() {
        return this.info;
    }
}

