/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;

@Environment(value=EnvType.CLIENT)
public class LoadedBlockEntityModels {
    final static public LoadedBlockEntityModels EMPTY = new LoadedBlockEntityModels(Map.of());
    final private Map<Block, SpecialModelRenderer<?>> renderers;

    public LoadedBlockEntityModels(Map<Block, SpecialModelRenderer<?>> renderers) {
        this.renderers = renderers;
    }

    public static LoadedBlockEntityModels fromModels(LoadedEntityModels models) {
        return new LoadedBlockEntityModels(SpecialModelTypes.buildBlockToModelTypeMap(models));
    }

    public void render(Block block, ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SpecialModelRenderer<?> specialModelRenderer = this.renderers.get(block);
        if (specialModelRenderer != null) {
            specialModelRenderer.render(null, displayContext, matrices, vertexConsumers, light, overlay, false);
        }
    }
}

