/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsWorldOptions;

@Environment(value=EnvType.CLIENT)
public final class RealmsSlot
implements RealmsSerializable {
    @SerializedName(value="slotId")
    public int slotId;
    @SerializedName(value="options")
    @JsonAdapter(value=OptionsTypeAdapter.class)
    public RealmsWorldOptions options;
    @SerializedName(value="settings")
    public List<RealmsSettingDto> settings;

    public RealmsSlot(int slotId, RealmsWorldOptions options, List<RealmsSettingDto> settings) {
        this.slotId = slotId;
        this.options = options;
        this.settings = settings;
    }

    public static RealmsSlot create(int slotId) {
        return new RealmsSlot(slotId, RealmsWorldOptions.getEmptyDefaults(), List.of(RealmsSettingDto.ofHardcore(false)));
    }

    public RealmsSlot net_minecraft_client_realms_dto_RealmsSlot_clone() {
        return new RealmsSlot(this.slotId, this.options.net_minecraft_client_realms_dto_RealmsWorldOptions_clone(), new ArrayList<RealmsSettingDto>(this.settings));
    }

    public boolean isHardcore() {
        return RealmsSettingDto.isHardcore(this.settings);
    }

    public Object java_lang_Object_clone() throws CloneNotSupportedException {
        return this.net_minecraft_client_realms_dto_RealmsSlot_clone();
    }

    @Environment(value=EnvType.CLIENT)
    static class OptionsTypeAdapter
    extends TypeAdapter<RealmsWorldOptions> {
        private OptionsTypeAdapter() {
        }

        public void write(JsonWriter jsonWriter, RealmsWorldOptions realmsWorldOptions) throws IOException {
            jsonWriter.jsonValue(new CheckedGson().toJson(realmsWorldOptions));
        }

        public RealmsWorldOptions net_minecraft_client_realms_dto_RealmsWorldOptions_read(JsonReader jsonReader) throws IOException {
            String string = jsonReader.nextString();
            return RealmsWorldOptions.fromJson(new CheckedGson(), string);
        }

        public Object java_lang_Object_read(JsonReader reader) throws IOException {
            return this.net_minecraft_client_realms_dto_RealmsWorldOptions_read(reader);
        }

        public void write(JsonWriter writer, Object options) throws IOException {
            this.write(writer, (RealmsWorldOptions)options);
        }
    }
}

