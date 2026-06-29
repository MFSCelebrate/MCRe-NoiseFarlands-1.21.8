/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelRotation;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record ModelElement(Vector3fc from, Vector3fc to, Map<Direction, ModelElementFace> faces, @Nullable ModelRotation rotation, boolean shade, int lightEmission) {
    final static private boolean field_32785 = false;
    final static private float field_32786 = -16.0f;
    final static private float field_32787 = 32.0f;

    public ModelElement(Vector3fc vector3fc, Vector3fc vector3fc2, Map<Direction, ModelElementFace> faces) {
        this(vector3fc, vector3fc2, faces, null, true, 0);
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<ModelElement> {
        final static private boolean DEFAULT_SHADE = true;
        final static private int field_53160 = 0;

        protected Deserializer() {
        }

        public ModelElement net_minecraft_client_render_model_json_ModelElement_deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f vector3f = this.deserializeFrom(jsonObject);
            Vector3f vector3f2 = this.deserializeTo(jsonObject);
            ModelRotation modelRotation = this.deserializeRotation(jsonObject);
            Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
            if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
            int i = 0;
            if (jsonObject.has("light_emission")) {
                boolean bl2 = JsonHelper.hasNumber(jsonObject, "light_emission");
                if (bl2) {
                    i = JsonHelper.getInt(jsonObject, "light_emission");
                }
                if (!bl2 || i < 0 || i > 15) {
                    throw new JsonParseException("Expected light_emission to be an Integer between (inclusive) 0 and 15");
                }
            }
            return new ModelElement((Vector3fc)vector3f, (Vector3fc)vector3f2, map, modelRotation, bl, i);
        }

        @Nullable
        private ModelRotation deserializeRotation(JsonObject object) {
            ModelRotation modelRotation = null;
            if (object.has("rotation")) {
                JsonObject jsonObject = JsonHelper.getObject(object, "rotation");
                Vector3f vector3f = this.deserializeVec3f(jsonObject, "origin");
                vector3f.mul(0.0625f);
                Direction.Axis axis = this.deserializeAxis(jsonObject);
                float f = this.deserializeRotationAngle(jsonObject);
                boolean bl = JsonHelper.getBoolean(jsonObject, "rescale", false);
                modelRotation = new ModelRotation(vector3f, axis, f, bl);
            }
            return modelRotation;
        }

        private float deserializeRotationAngle(JsonObject object) {
            float f = JsonHelper.getFloat(object, "angle");
            if (MathHelper.abs(f) > 45.0f) {
                throw new JsonParseException("Invalid rotation " + f + " found, only values in [-45,45] range allowed");
            }
            return f;
        }

        private Direction.Axis deserializeAxis(JsonObject object) {
            String string = JsonHelper.getString(object, "axis");
            Direction.Axis axis = Direction.Axis.fromId(string.toLowerCase(Locale.ROOT));
            if (axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + string);
            }
            return axis;
        }

        private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext context, JsonObject object) {
            Map<Direction, ModelElementFace> map = this.deserializeFaces(context, object);
            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            return map;
        }

        private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext context, JsonObject object) {
            EnumMap map = Maps.newEnumMap(Direction.class);
            JsonObject jsonObject = JsonHelper.getObject(object, "faces");
            for (Map.Entry entry : jsonObject.entrySet()) {
                Direction direction = this.getDirection((String)entry.getKey());
                map.put(direction, (ModelElementFace)context.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
            }
            return map;
        }

        private Direction getDirection(String name) {
            Direction direction = Direction.byId(name);
            if (direction == null) {
                throw new JsonParseException("Unknown facing: " + name);
            }
            return direction;
        }

        private Vector3f deserializeTo(JsonObject object) {
            Vector3f vector3f = this.deserializeVec3f(object, "to");
            if (vector3f.x() < -16.0f || vector3f.y() < -16.0f || vector3f.z() < -16.0f || vector3f.x() > 32.0f || vector3f.y() > 32.0f || vector3f.z() > 32.0f) {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + String.valueOf(vector3f));
            }
            return vector3f;
        }

        private Vector3f deserializeFrom(JsonObject object) {
            Vector3f vector3f = this.deserializeVec3f(object, "from");
            if (vector3f.x() < -16.0f || vector3f.y() < -16.0f || vector3f.z() < -16.0f || vector3f.x() > 32.0f || vector3f.y() > 32.0f || vector3f.z() > 32.0f) {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + String.valueOf(vector3f));
            }
            return vector3f;
        }

        private Vector3f deserializeVec3f(JsonObject object, String name) {
            JsonArray jsonArray = JsonHelper.getArray(object, name);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + name + " values, found: " + jsonArray.size());
            }
            float[] fs = new float[3];
            for (int i = 0; i < fs.length; ++i) {
                fs[i] = JsonHelper.asFloat(jsonArray.get(i), name + "[" + i + "]");
            }
            return new Vector3f(fs[0], fs[1], fs[2]);
        }

        public Object java_lang_Object_deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return this.net_minecraft_client_render_model_json_ModelElement_deserialize(json, type, context);
        }
    }
}

