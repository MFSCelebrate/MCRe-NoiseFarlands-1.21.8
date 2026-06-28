/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.render.entity.model.ModelTransformer;

@Environment(value=EnvType.CLIENT)
public record BabyModelTransformer(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset, Set<String> headParts) implements ModelTransformer
{
    public BabyModelTransformer(Set<String> headParts) {
        this(false, 5.0f, 2.0f, headParts);
    }

    public BabyModelTransformer(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, Set<String> headParts) {
        this(scaleHead, babyYHeadOffset, babyZHeadOffset, 2.0f, 2.0f, 24.0f, headParts);
    }

    @Override
    public ModelData apply(ModelData modelData) {
        float f = this.scaleHead ? 1.5f / this.babyHeadScale : 1.0f;
        float g = 1.0f / this.babyBodyScale;
        UnaryOperator unaryOperator = modelTransform -> modelTransform.moveOrigin(0.0f, this.babyYHeadOffset, this.babyZHeadOffset).scaled(f);
        UnaryOperator unaryOperator2 = modelTransform -> modelTransform.moveOrigin(0.0f, this.bodyYOffset, 0.0f).scaled(g);
        ModelData modelData2 = new ModelData();
        for (Map.Entry<String, ModelPartData> entry : modelData.getRoot().getChildren()) {
            String string = entry.getKey();
            ModelPartData modelPartData = entry.getValue();
            modelData2.getRoot().addChild(string, modelPartData.applyTransformer(this.headParts.contains(string) ? unaryOperator : unaryOperator2));
        }
        return modelData2;
    }
}

