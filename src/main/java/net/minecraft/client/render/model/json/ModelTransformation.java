/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.item.ItemDisplayContext;

@Environment(value=EnvType.CLIENT)
public record ModelTransformation(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed) {
    final static public ModelTransformation NONE = new ModelTransformation(Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY);

    public Transformation getTransformation(ItemDisplayContext renderMode) {
        return switch (renderMode) {
            case ItemDisplayContext.THIRD_PERSON_LEFT_HAND -> this.thirdPersonLeftHand;
            case ItemDisplayContext.THIRD_PERSON_RIGHT_HAND -> this.thirdPersonRightHand;
            case ItemDisplayContext.FIRST_PERSON_LEFT_HAND -> this.firstPersonLeftHand;
            case ItemDisplayContext.FIRST_PERSON_RIGHT_HAND -> this.firstPersonRightHand;
            case ItemDisplayContext.HEAD -> this.head;
            case ItemDisplayContext.GUI -> this.gui;
            case ItemDisplayContext.GROUND -> this.ground;
            case ItemDisplayContext.FIXED -> this.fixed;
            default -> Transformation.IDENTITY;
        };
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<ModelTransformation> {
        protected Deserializer() {
        }

        public ModelTransformation net_minecraft_client_render_model_json_ModelTransformation_deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Transformation transformation = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
            Transformation transformation2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
            if (transformation2 == Transformation.IDENTITY) {
                transformation2 = transformation;
            }
            Transformation transformation3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
            Transformation transformation4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
            if (transformation4 == Transformation.IDENTITY) {
                transformation4 = transformation3;
            }
            Transformation transformation5 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.HEAD);
            Transformation transformation6 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.GUI);
            Transformation transformation7 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.GROUND);
            Transformation transformation8 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.FIXED);
            return new ModelTransformation(transformation2, transformation, transformation4, transformation3, transformation5, transformation6, transformation7, transformation8);
        }

        private Transformation parseModelTransformation(JsonDeserializationContext ctx, JsonObject json, ItemDisplayContext displayContext) {
            String string = displayContext.asString();
            if (json.has(string)) {
                return (Transformation)ctx.deserialize(json.get(string), Transformation.class);
            }
            return Transformation.IDENTITY;
        }

        public Object java_lang_Object_deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.net_minecraft_client_render_model_json_ModelTransformation_deserialize(functionJson, unused, context);
        }
    }
}

