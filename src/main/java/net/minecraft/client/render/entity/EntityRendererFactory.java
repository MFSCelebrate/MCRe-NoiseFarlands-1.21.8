/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface EntityRendererFactory<T extends Entity> {
    public EntityRenderer<T, ?> create(Context var1);

    @Environment(value=EnvType.CLIENT)
    public static class Context {
        final private EntityRenderDispatcher renderDispatcher;
        final private ItemModelManager itemModelManager;
        final private MapRenderer mapRenderer;
        final private BlockRenderManager blockRenderManager;
        final private ResourceManager resourceManager;
        final private LoadedEntityModels entityModels;
        final private EquipmentModelLoader equipmentModelLoader;
        final private TextRenderer textRenderer;
        final private EquipmentRenderer equipmentRenderer;

        public Context(EntityRenderDispatcher renderDispatcher, ItemModelManager itemRenderer, MapRenderer mapRenderer, BlockRenderManager blockRenderManager, ResourceManager resourceManager, LoadedEntityModels entityModels, EquipmentModelLoader equipmentModelLoader, TextRenderer textRenderer) {
            this.renderDispatcher = renderDispatcher;
            this.itemModelManager = itemRenderer;
            this.mapRenderer = mapRenderer;
            this.blockRenderManager = blockRenderManager;
            this.resourceManager = resourceManager;
            this.entityModels = entityModels;
            this.equipmentModelLoader = equipmentModelLoader;
            this.textRenderer = textRenderer;
            this.equipmentRenderer = new EquipmentRenderer(equipmentModelLoader, this.getModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE));
        }

        public EntityRenderDispatcher getRenderDispatcher() {
            return this.renderDispatcher;
        }

        public ItemModelManager getItemModelManager() {
            return this.itemModelManager;
        }

        public MapRenderer getMapRenderer() {
            return this.mapRenderer;
        }

        public BlockRenderManager getBlockRenderManager() {
            return this.blockRenderManager;
        }

        public ResourceManager getResourceManager() {
            return this.resourceManager;
        }

        public LoadedEntityModels getEntityModels() {
            return this.entityModels;
        }

        public EquipmentModelLoader getEquipmentModelLoader() {
            return this.equipmentModelLoader;
        }

        public EquipmentRenderer getEquipmentRenderer() {
            return this.equipmentRenderer;
        }

        public BakedModelManager getModelManager() {
            return this.blockRenderManager.getModels().getModelManager();
        }

        public ModelPart getPart(EntityModelLayer layer) {
            return this.entityModels.getModelPart(layer);
        }

        public TextRenderer getTextRenderer() {
            return this.textRenderer;
        }
    }
}

