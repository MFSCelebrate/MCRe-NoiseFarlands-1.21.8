/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.UnbakedGeometry;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record JsonUnbakedModel(@Nullable Geometry geometry, @Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ModelTransformation transformations, ModelTextures.Textures textures, @Nullable Identifier parent) implements UnbakedModel
{
    @VisibleForTesting
    final static Gson GSON = new GsonBuilder().registerTypeAdapter(JsonUnbakedModel.class, (Object)new Deserializer()).registerTypeAdapter(ModelElement.class, (Object)new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, (Object)new ModelElementFace.Deserializer()).registerTypeAdapter(Transformation.class, (Object)new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, (Object)new ModelTransformation.Deserializer()).create();

    public static JsonUnbakedModel deserialize(Reader input) {
        return JsonHelper.deserialize(GSON, input, JsonUnbakedModel.class);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{JsonUnbakedModel.class, "geometry;guiLight;ambientOcclusion;transforms;textureSlots;parent", "geometry", "guiLight", "ambientOcclusion", "transformations", "textures", "parent"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{JsonUnbakedModel.class, "geometry;guiLight;ambientOcclusion;transforms;textureSlots;parent", "geometry", "guiLight", "ambientOcclusion", "transformations", "textures", "parent"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{JsonUnbakedModel.class, "geometry;guiLight;ambientOcclusion;transforms;textureSlots;parent", "geometry", "guiLight", "ambientOcclusion", "transformations", "textures", "parent"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<JsonUnbakedModel> {
        public JsonUnbakedModel net_minecraft_client_render_model_json_JsonUnbakedModel_deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Geometry geometry = this.elementsFromJson(jsonDeserializationContext, jsonObject);
            String string = this.parentFromJson(jsonObject);
            ModelTextures.Textures textures = this.texturesFromJson(jsonObject);
            Boolean boolean_ = this.ambientOcclusionFromJson(jsonObject);
            ModelTransformation modelTransformation = null;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize((JsonElement)jsonObject2, ModelTransformation.class);
            }
            UnbakedModel.GuiLight guiLight = null;
            if (jsonObject.has("gui_light")) {
                guiLight = UnbakedModel.GuiLight.byName(JsonHelper.getString(jsonObject, "gui_light"));
            }
            Identifier identifier = string.isEmpty() ? null : Identifier.of(string);
            return new JsonUnbakedModel(geometry, guiLight, boolean_, modelTransformation, textures, identifier);
        }

        private ModelTextures.Textures texturesFromJson(JsonObject object) {
            if (object.has("textures")) {
                JsonObject jsonObject = JsonHelper.getObject(object, "textures");
                return ModelTextures.fromJson(jsonObject, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            }
            return ModelTextures.Textures.EMPTY;
        }

        private String parentFromJson(JsonObject json) {
            return JsonHelper.getString(json, "parent", "");
        }

        @Nullable
        protected Boolean ambientOcclusionFromJson(JsonObject json) {
            if (json.has("ambientocclusion")) {
                return JsonHelper.getBoolean(json, "ambientocclusion");
            }
            return null;
        }

        @Nullable
        protected Geometry elementsFromJson(JsonDeserializationContext context, JsonObject json) {
            if (json.has("elements")) {
                ArrayList<ModelElement> list = new ArrayList<ModelElement>();
                for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                    list.add((ModelElement)context.deserialize(jsonElement, ModelElement.class));
                }
                return new UnbakedGeometry(list);
            }
            return null;
        }

        public Object java_lang_Object_deserialize(JsonElement element, Type unused, JsonDeserializationContext ctx) throws JsonParseException {
            return this.net_minecraft_client_render_model_json_JsonUnbakedModel_deserialize(element, unused, ctx);
        }
    }
}

