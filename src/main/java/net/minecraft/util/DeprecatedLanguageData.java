/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Language;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

public record DeprecatedLanguageData(List<String> removed, Map<String, String> renamed) {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public DeprecatedLanguageData NONE = new DeprecatedLanguageData(List.of(), Map.of());
    final static public Codec<DeprecatedLanguageData> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.listOf().fieldOf("removed").forGetter(DeprecatedLanguageData::removed), (App)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.STRING).fieldOf("renamed").forGetter(DeprecatedLanguageData::renamed)).apply((Applicative)instance, DeprecatedLanguageData::new));

    public static DeprecatedLanguageData fromInputStream(InputStream stream) {
        JsonElement jsonElement = StrictJsonParser.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return (DeprecatedLanguageData)CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement).getOrThrow(error -> new IllegalStateException("Failed to parse deprecated language data: " + error));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    public static DeprecatedLanguageData fromPath(String path) {
        try {
            InputStream inputStream;
            block6: {
                DeprecatedLanguageData deprecatedLanguageData;
                inputStream = Language.class.getResourceAsStream(path);
                try {
                    if (inputStream == null) break block6;
                    deprecatedLanguageData = DeprecatedLanguageData.fromInputStream(inputStream);
                    if (inputStream == null) return deprecatedLanguageData;
                }
                catch (Throwable throwable) {
                    if (inputStream == null) throw throwable;
                    try {
                        inputStream.close();
                        throw throwable;
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                inputStream.close();
                return deprecatedLanguageData;
            }
            if (inputStream == null) return NONE;
            inputStream.close();
            return NONE;
        }
        catch (Exception exception) {
            LOGGER.error("Failed to read {}", (Object)path, (Object)exception);
        }
        return NONE;
    }

    public static DeprecatedLanguageData create() {
        return DeprecatedLanguageData.fromPath("/assets/minecraft/lang/deprecated.json");
    }

    public void apply(Map<String, String> map) {
        for (String string : this.removed) {
            map.remove(string);
        }
        this.renamed.forEach((oldKey, newKey) -> {
            String string = (String)map.remove(oldKey);
            if (string == null) {
                LOGGER.warn("Missing translation key for rename: {}", oldKey);
                map.remove(newKey);
            } else {
                map.put((String)newKey, string);
            }
        });
    }
}

