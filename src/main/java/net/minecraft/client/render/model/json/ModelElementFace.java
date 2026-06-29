/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, @Nullable UV uvs, AxisRotation rotation) {
    final static public int field_32789 = -1;

    public static float getUValue(UV uV, AxisRotation axisRotation, int i) {
        return uV.getUVertices(axisRotation.rotate(i)) / 16.0f;
    }

    public static float getVValue(UV uV, AxisRotation axisRotation, int i) {
        return uV.getVVertices(axisRotation.rotate(i)) / 16.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public record UV(float minU, float minV, float maxU, float maxV) {
        public UV(float f, float g, float h, float i) {
            this.minU = f;
            this.minV = g;
            this.maxU = h;
            this.maxV = 1;
        }

        public float getUVertices(int i) {
            return i == 0 || i == 1 ? this.minU : this.maxU;
        }

        public float getVVertices(int i) {
            return i == 0 || i == 3 ? this.minV : this.maxV;
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<ModelElementFace> {
        final static private int DEFAULT_TINT_INDEX = -1;
        final static private int field_56927 = 0;

        protected Deserializer() {
        }

        public ModelElementFace net_minecraft_client_render_model_json_ModelElementFace_deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Direction direction = Deserializer.deserializeCullFace(jsonObject);
            int i = Deserializer.deserializeTintIndex(jsonObject);
            String string = Deserializer.deserializeTexture(jsonObject);
            UV uV = Deserializer.getUV(jsonObject);
            AxisRotation axisRotation = Deserializer.getRotation(jsonObject);
            return new ModelElementFace(direction, i, string, uV, axisRotation);
        }

        private static int deserializeTintIndex(JsonObject jsonObject) {
            return JsonHelper.getInt(jsonObject, "tintindex", -1);
        }

        private static String deserializeTexture(JsonObject jsonObject) {
            return JsonHelper.getString(jsonObject, "texture");
        }

        @Nullable
        private static Direction deserializeCullFace(JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "cullface", "");
            return Direction.byId(string);
        }

        private static AxisRotation getRotation(JsonObject jsonObject) {
            int i = JsonHelper.getInt(jsonObject, "rotation", 0);
            return AxisRotation.fromDegrees(i);
        }

        @Nullable
        private static UV getUV(JsonObject jsonObject) {
            if (!jsonObject.has("uv")) {
                return null;
            }
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "uv");
            if (jsonArray.size() != 4) {
                throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
            }
            float f = JsonHelper.asFloat(jsonArray.get(0), "minU");
            float g = JsonHelper.asFloat(jsonArray.get(1), "minV");
            float h = JsonHelper.asFloat(jsonArray.get(2), "maxU");
            float i = JsonHelper.asFloat(jsonArray.get(3), "maxV");
            return new UV(f, g, h, i);
        }

        public Object java_lang_Object_deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.net_minecraft_client_render_model_json_ModelElementFace_deserialize(functionJson, unused, context);
        }
    }
}

