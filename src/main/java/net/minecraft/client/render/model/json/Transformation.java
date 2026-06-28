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
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record Transformation(Vector3fc rotation, Vector3fc translation, Vector3fc scale) {
    final static public Transformation IDENTITY = new Transformation((Vector3fc)new Vector3f(), (Vector3fc)new Vector3f(), (Vector3fc)new Vector3f(1.0f, 1.0f, 1.0f));

    public void apply(boolean leftHanded, MatrixStack.Entry entry) {
        float h;
        float g;
        float f;
        if (this == IDENTITY) {
            entry.translate(-0.5f, -0.5f, -0.5f);
            return;
        }
        if (leftHanded) {
            f = -this.translation.x();
            g = -this.rotation.y();
            h = -this.rotation.z();
        } else {
            f = this.translation.x();
            g = this.rotation.y();
            h = this.rotation.z();
        }
        entry.translate(f, this.translation.y(), this.translation.z());
        entry.rotate((Quaternionfc)new Quaternionf().rotationXYZ(this.rotation.x() * ((float)Math.PI / 180), g * ((float)Math.PI / 180), h * ((float)Math.PI / 180)));
        entry.scale(this.scale.x(), this.scale.y(), this.scale.z());
        entry.translate(-0.5f, -0.5f, -0.5f);
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<Transformation> {
        final static private Vector3f DEFAULT_ROTATION = new Vector3f(0.0f, 0.0f, 0.0f);
        final static private Vector3f DEFAULT_TRANSLATION = new Vector3f(0.0f, 0.0f, 0.0f);
        final static private Vector3f DEFAULT_SCALE = new Vector3f(1.0f, 1.0f, 1.0f);
        final static public float field_32808 = 5.0f;
        final static public float field_32809 = 4.0f;

        protected Deserializer() {
        }

        public Transformation net_minecraft_client_render_model_json_Transformation_deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Vector3f vector3f = this.parseVector3f(jsonObject, "rotation", DEFAULT_ROTATION);
            Vector3f vector3f2 = this.parseVector3f(jsonObject, "translation", DEFAULT_TRANSLATION);
            vector3f2.mul(0.0625f);
            vector3f2.set(MathHelper.clamp(vector3f2.x, -5.0f, 5.0f), MathHelper.clamp(vector3f2.y, -5.0f, 5.0f), MathHelper.clamp(vector3f2.z, -5.0f, 5.0f));
            Vector3f vector3f3 = this.parseVector3f(jsonObject, "scale", DEFAULT_SCALE);
            vector3f3.set(MathHelper.clamp(vector3f3.x, -4.0f, 4.0f), MathHelper.clamp(vector3f3.y, -4.0f, 4.0f), MathHelper.clamp(vector3f3.z, -4.0f, 4.0f));
            return new Transformation((Vector3fc)vector3f, (Vector3fc)vector3f2, (Vector3fc)vector3f3);
        }

        private Vector3f parseVector3f(JsonObject json, String key, Vector3f fallback) {
            if (!json.has(key)) {
                return fallback;
            }
            JsonArray jsonArray = JsonHelper.getArray(json, key);
            if (jsonArray.size() != 3) {
                throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
            }
            float[] fs = new float[3];
            for (int i = 0; i < fs.length; ++i) {
                fs[i] = JsonHelper.asFloat(jsonArray.get(i), key + "[" + i + "]");
            }
            return new Vector3f(fs[0], fs[1], fs[2]);
        }

        public Object java_lang_Object_deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.net_minecraft_client_render_model_json_Transformation_deserialize(functionJson, unused, context);
        }
    }
}

