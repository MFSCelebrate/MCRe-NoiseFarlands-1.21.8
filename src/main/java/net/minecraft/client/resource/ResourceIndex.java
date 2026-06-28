/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.fs.ResourceFileSystem;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ResourceIndex {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public Splitter SEPARATOR_SPLITTER = Splitter.on((char)'/');

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Path buildFileSystem(Path assetsDir, String indexName) {
        Path path = assetsDir.resolve("objects");
        ResourceFileSystem.Builder builder = ResourceFileSystem.builder();
        Path path2 = assetsDir.resolve("indexes/" + indexName + ".json");
        try {
            try (BufferedReader bufferedReader = Files.newBufferedReader(path2, StandardCharsets.UTF_8);){
                JsonObject jsonObject = JsonHelper.deserialize(bufferedReader);
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "objects", null);
                if (jsonObject2 != null) {
                    for (Map.Entry entry : jsonObject2.entrySet()) {
                        JsonObject jsonObject3 = (JsonObject)entry.getValue();
                        String string = (String)entry.getKey();
                        List list = SEPARATOR_SPLITTER.splitToList((CharSequence)string);
                        String string2 = JsonHelper.getString(jsonObject3, "hash");
                        Path path3 = path.resolve(string2.substring(0, 2) + "/" + string2);
                        builder.withFile(list, path3);
                    }
                }
                if (bufferedReader == null) return builder.build("index-" + indexName).getPath("/", new String[0]);
            }
            return builder.build("index-" + indexName).getPath("/", new String[0]);
        }
        catch (JsonParseException jsonParseException) {
            LOGGER.error("Unable to parse resource index file: {}", (Object)path2);
            return builder.build("index-" + indexName).getPath("/", new String[0]);
        }
        catch (IOException iOException) {
            LOGGER.error("Can't open the resource index file: {}", (Object)path2);
        }
        return builder.build("index-" + indexName).getPath("/", new String[0]);
    }
}

