/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public final class RegionSelectionMethod
extends Enum<RegionSelectionMethod> {
    final static public RegionSelectionMethod AUTOMATIC_PLAYER = new RegionSelectionMethod(0, "realms.configuration.region_preference.automatic_player");
    final static public RegionSelectionMethod AUTOMATIC_OWNER = new RegionSelectionMethod(1, "realms.configuration.region_preference.automatic_owner");
    final static public RegionSelectionMethod MANUAL = new RegionSelectionMethod(2, "");
    final static public RegionSelectionMethod DEFAULT;
    final public int index;
    final public String translationKey;
    final static private RegionSelectionMethod[] field_60232;

    public static RegionSelectionMethod[] values() {
        return (RegionSelectionMethod[])field_60232.clone();
    }

    public static RegionSelectionMethod valueOf(String string) {
        return Enum.valueOf(RegionSelectionMethod.class, string);
    }

    private RegionSelectionMethod(int index, String translationKey) {
        this.index = index;
        this.translationKey = translationKey;
    }

    private static RegionSelectionMethod[] method_71190() {
        return new RegionSelectionMethod[]{AUTOMATIC_PLAYER, AUTOMATIC_OWNER, MANUAL};
    }

    static {
        field_60232 = RegionSelectionMethod.method_71190();
        DEFAULT = AUTOMATIC_PLAYER;
    }

    @Environment(value=EnvType.CLIENT)
    public static class SelectionMethodTypeAdapter
    extends TypeAdapter<RegionSelectionMethod> {
        final static private Logger LOGGER = LogUtils.getLogger();

        public void write(JsonWriter jsonWriter, RegionSelectionMethod regionSelectionMethod) throws IOException {
            jsonWriter.value((long)regionSelectionMethod.index);
        }

        public RegionSelectionMethod net_minecraft_client_realms_dto_RegionSelectionMethod_read(JsonReader jsonReader) throws IOException {
            int i = jsonReader.nextInt();
            for (RegionSelectionMethod regionSelectionMethod : RegionSelectionMethod.values()) {
                if (regionSelectionMethod.index != i) continue;
                return regionSelectionMethod;
            }
            LOGGER.warn("Unsupported RegionSelectionPreference {}", (Object)i);
            return DEFAULT;
        }

        public Object java_lang_Object_read(JsonReader reader) throws IOException {
            return this.net_minecraft_client_realms_dto_RegionSelectionMethod_read(reader);
        }

        public void write(JsonWriter writer, Object selectionMethod) throws IOException {
            this.write(writer, (RegionSelectionMethod)((Object)selectionMethod));
        }
    }
}

