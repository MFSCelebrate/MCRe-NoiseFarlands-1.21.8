/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.WeightedBlockStateModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

@Environment(value=EnvType.CLIENT)
public record WeightedVariant(Pool<ModelVariant> variants) {
    public WeightedVariant {
        if (pool.isEmpty()) {
            throw new IllegalArgumentException("Variant list must contain at least one element");
        }
    }

    public WeightedVariant apply(ModelVariantOperator operator) {
        return new WeightedVariant(this.variants.transform(operator));
    }

    public BlockStateModel.Unbaked toModel() {
        List<Weighted<ModelVariant>> list = this.variants.getEntries();
        return list.size() == 1 ? new SimpleBlockStateModel.Unbaked(list.getFirst().value()) : new WeightedBlockStateModel.Unbaked(this.variants.transform(SimpleBlockStateModel.Unbaked::new));
    }
}

