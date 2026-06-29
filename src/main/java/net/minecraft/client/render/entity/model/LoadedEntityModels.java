/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;

@Environment(value=EnvType.CLIENT)
public class LoadedEntityModels {
    final static public LoadedEntityModels EMPTY = new LoadedEntityModels(Map.of());
    final private Map<EntityModelLayer, TexturedModelData> modelParts;

    public LoadedEntityModels(Map<EntityModelLayer, TexturedModelData> modelParts) {
        this.modelParts = modelParts;
    }

    public ModelPart getModelPart(EntityModelLayer layer) {
        TexturedModelData texturedModelData = this.modelParts.get(layer);
        if (texturedModelData == null) {
            throw new IllegalArgumentException("No model for layer " + String.valueOf(layer));
        }
        return texturedModelData.createModel();
    }

    public static LoadedEntityModels copy() {
        return new LoadedEntityModels((Map<EntityModelLayer, TexturedModelData>)ImmutableMap.copyOf(EntityModels.getModels()));
    }
}

