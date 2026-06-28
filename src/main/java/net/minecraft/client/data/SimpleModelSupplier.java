/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SimpleModelSupplier
implements ModelSupplier {
    final private Identifier parent;

    public SimpleModelSupplier(Identifier parent) {
        this.parent = parent;
    }

    @Override
    public JsonElement com_google_gson_JsonElement_get() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parent", this.parent.toString());
        return jsonObject;
    }

    @Override
    public Object java_lang_Object_get() {
        return this.com_google_gson_JsonElement_get();
    }
}

