/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ElytraFeatureRenderer<S extends BipedEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    final private ElytraEntityModel model;
    final private ElytraEntityModel babyModel;
    final private EquipmentRenderer equipmentRenderer;

    public ElytraFeatureRenderer(FeatureRendererContext<S, M> context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.model = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA));
        this.babyModel = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA_BABY));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S bipedEntityRenderState, float f, float g) {
        ItemStack itemStack = ((BipedEntityRenderState)bipedEntityRenderState).equippedChestStack;
        EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.assetId().isEmpty()) {
            return;
        }
        Identifier identifier = ElytraFeatureRenderer.getTexture(bipedEntityRenderState);
        ElytraEntityModel elytraEntityModel = ((BipedEntityRenderState)bipedEntityRenderState).baby ? this.babyModel : this.model;
        matrixStack.push();
        matrixStack.translate(0.0f, 0.0f, 0.125f);
        elytraEntityModel.setAngles((BipedEntityRenderState)bipedEntityRenderState);
        this.equipmentRenderer.render(EquipmentModel.LayerType.WINGS, equippableComponent.assetId().get(), elytraEntityModel, itemStack, matrixStack, vertexConsumerProvider, 1, identifier);
        matrixStack.pop();
    }

    @Nullable
    private static Identifier getTexture(BipedEntityRenderState state) {
        if (state instanceof PlayerEntityRenderState) {
            PlayerEntityRenderState playerEntityRenderState = (PlayerEntityRenderState)state;
            SkinTextures skinTextures = playerEntityRenderState.skinTextures;
            if (skinTextures.elytraTexture() != null) {
                return skinTextures.elytraTexture();
            }
            if (skinTextures.capeTexture() != null && playerEntityRenderState.capeVisible) {
                return skinTextures.capeTexture();
            }
        }
        return null;
    }
}

