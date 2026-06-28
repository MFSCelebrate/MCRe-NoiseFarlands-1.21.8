/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.ModelTransformer;

@Environment(value=EnvType.CLIENT)
public class ModelData {
    final private ModelPartData data;

    public ModelData() {
        this(new ModelPartData((List<ModelCuboidData>)ImmutableList.of(), ModelTransform.NONE));
    }

    private ModelData(ModelPartData data) {
        this.data = data;
    }

    public ModelPartData getRoot() {
        return this.data;
    }

    public ModelData transform(UnaryOperator<ModelTransform> transformer) {
        return new ModelData(this.data.applyTransformer(transformer));
    }

    public ModelData transform(ModelTransformer transformer) {
        return transformer.apply(this);
    }
}

