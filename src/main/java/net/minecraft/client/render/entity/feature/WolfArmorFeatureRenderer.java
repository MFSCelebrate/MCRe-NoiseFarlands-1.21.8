/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WolfArmorFeatureRenderer
extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    final private WolfEntityModel model;
    final private WolfEntityModel babyModel;
    final private EquipmentRenderer equipmentRenderer;
    final static private Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES = Map.of(Cracks.CrackLevel.LOW, Identifier.ofVanilla("textures/entity/wolf/wolf_armor_crackiness_low.png"), Cracks.CrackLevel.MEDIUM, Identifier.ofVanilla("textures/entity/wolf/wolf_armor_crackiness_medium.png"), Cracks.CrackLevel.HIGH, Identifier.ofVanilla("textures/entity/wolf/wolf_armor_crackiness_high.png"));

    public WolfArmorFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.model = new WolfEntityModel(loader.getModelPart(EntityModelLayers.WOLF_ARMOR));
        this.babyModel = new WolfEntityModel(loader.getModelPart(EntityModelLayers.WOLF_BABY_ARMOR));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WolfEntityRenderState wolfEntityRenderState, float f, float g) {
        ItemStack itemStack = wolfEntityRenderState.bodyArmor;
        EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.assetId().isEmpty()) {
            return;
        }
        WolfEntityModel wolfEntityModel = wolfEntityRenderState.baby ? this.babyModel : this.model;
        wolfEntityModel.setAngles(wolfEntityRenderState);
        this.equipmentRenderer.render(EquipmentModel.LayerType.WOLF_BODY, equippableComponent.assetId().get(), wolfEntityModel, itemStack, matrixStack, vertexConsumerProvider, 1);
        this.renderCracks(matrixStack, vertexConsumerProvider, 1, itemStack, wolfEntityModel);
    }

    private void renderCracks(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Model model) {
        Cracks.CrackLevel crackLevel = Cracks.WOLF_ARMOR.getCrackLevel(stack);
        if (crackLevel == Cracks.CrackLevel.NONE) {
            return;
        }
        Identifier identifier = CRACK_TEXTURES.get((Object)crackLevel);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.createArmorTranslucent(identifier));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
    }
}

