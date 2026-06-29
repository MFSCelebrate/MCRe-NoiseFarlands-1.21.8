/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;

@Environment(value=EnvType.CLIENT)
public class ModelPartData {
    final private List<ModelCuboidData> cuboidData;
    final private ModelTransform rotationData;
    final private Map<String, ModelPartData> children = Maps.newHashMap();

    ModelPartData(List<ModelCuboidData> cuboidData, ModelTransform rotationData) {
        this.cuboidData = cuboidData;
        this.rotationData = rotationData;
    }

    public ModelPartData addChild(String name, ModelPartBuilder builder, ModelTransform rotationData) {
        ModelPartData modelPartData = new ModelPartData(builder.build(), rotationData);
        return this.addChild(name, modelPartData);
    }

    public ModelPartData addChild(String name, ModelPartData data) {
        ModelPartData modelPartData = this.children.put(name, data);
        if (modelPartData != null) {
            data.children.putAll(modelPartData.children);
        }
        return data;
    }

    public ModelPartData addChild(String name) {
        ModelPartData modelPartData = this.children.get(name);
        if (modelPartData == null) {
            throw new IllegalArgumentException("No child with name: " + name);
        }
        return this.addChild(name, ModelPartBuilder.create(), modelPartData.rotationData);
    }

    public ModelPart createPart(int textureWidth, int textureHeight) {
        Object2ObjectArrayMap object2ObjectArrayMap = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((ModelPartData)entry.getValue()).createPart(textureWidth, textureHeight), (modelPart, modelPart2) -> modelPart, Object2ObjectArrayMap::new));
        List<ModelPart.Cuboid> list = this.cuboidData.stream().map(modelCuboidData -> modelCuboidData.createCuboid(textureWidth, textureHeight)).toList();
        ModelPart modelPart3 = new ModelPart(list, (Map<String, ModelPart>)object2ObjectArrayMap);
        modelPart3.setDefaultTransform(this.rotationData);
        modelPart3.setTransform(this.rotationData);
        return modelPart3;
    }

    public ModelPartData getChild(String name) {
        return this.children.get(name);
    }

    public Set<Map.Entry<String, ModelPartData>> getChildren() {
        return this.children.entrySet();
    }

    public ModelPartData applyTransformer(UnaryOperator<ModelTransform> transformer) {
        ModelPartData modelPartData = new ModelPartData(this.cuboidData, (ModelTransform)transformer.apply(this.rotationData));
        modelPartData.children.putAll(this.children);
        return modelPartData;
    }
}

