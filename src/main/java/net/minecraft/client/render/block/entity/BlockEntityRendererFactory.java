/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface BlockEntityRendererFactory<T extends BlockEntity> {
    public BlockEntityRenderer<T> create(Context var1);

    @Environment(value=EnvType.CLIENT)
    public static class Context {
        final private BlockEntityRenderDispatcher renderDispatcher;
        final private BlockRenderManager renderManager;
        final private ItemModelManager itemModelManager;
        final private ItemRenderer itemRenderer;
        final private EntityRenderDispatcher entityRenderDispatcher;
        final private LoadedEntityModels loadedEntityModels;
        final private TextRenderer textRenderer;

        public Context(BlockEntityRenderDispatcher renderDispatcher, BlockRenderManager renderManager, ItemModelManager itemModelManager, ItemRenderer itemRenderer, EntityRenderDispatcher entityRenderDispatcher, LoadedEntityModels layerRenderDispatcher, TextRenderer textRenderer) {
            this.renderDispatcher = renderDispatcher;
            this.renderManager = renderManager;
            this.itemModelManager = itemModelManager;
            this.itemRenderer = itemRenderer;
            this.entityRenderDispatcher = entityRenderDispatcher;
            this.loadedEntityModels = layerRenderDispatcher;
            this.textRenderer = textRenderer;
        }

        public BlockEntityRenderDispatcher getRenderDispatcher() {
            return this.renderDispatcher;
        }

        public BlockRenderManager getRenderManager() {
            return this.renderManager;
        }

        public EntityRenderDispatcher getEntityRenderDispatcher() {
            return this.entityRenderDispatcher;
        }

        public ItemModelManager getItemModelManager() {
            return this.itemModelManager;
        }

        public ItemRenderer getItemRenderer() {
            return this.itemRenderer;
        }

        public LoadedEntityModels getLoadedEntityModels() {
            return this.loadedEntityModels;
        }

        public ModelPart getLayerModelPart(EntityModelLayer modelLayer) {
            return this.loadedEntityModels.getModelPart(modelLayer);
        }

        public TextRenderer getTextRenderer() {
            return this.textRenderer;
        }
    }
}

