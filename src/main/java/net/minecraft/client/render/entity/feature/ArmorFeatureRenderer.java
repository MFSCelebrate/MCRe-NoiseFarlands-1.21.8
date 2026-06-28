/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class ArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>>
extends FeatureRenderer<S, M> {
    final private A innerModel;
    final private A outerModel;
    final private A babyInnerModel;
    final private A babyOuterModel;
    final private EquipmentRenderer equipmentRenderer;

    public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, A innerModel, A outerModel, EquipmentRenderer equipmentRenderer) {
        this(context, innerModel, outerModel, innerModel, outerModel, equipmentRenderer);
    }

    public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, A innerModel, A outerModel, A babyInnerModel, A babyOuterModel, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
        this.babyInnerModel = babyInnerModel;
        this.babyOuterModel = babyOuterModel;
        this.equipmentRenderer = equipmentRenderer;
    }

    public static boolean hasModel(ItemStack stack, EquipmentSlot slot) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        return equippableComponent != null && ArmorFeatureRenderer.hasModel(equippableComponent, slot);
    }

    private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
        return component.assetId().isPresent() && component.slot() == slot;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S bipedEntityRenderState, float f, float g) {
        this.renderArmor(matrixStack, vertexConsumerProvider, ((BipedEntityRenderState)bipedEntityRenderState).equippedChestStack, EquipmentSlot.CHEST, 1, this.getModel(bipedEntityRenderState, EquipmentSlot.CHEST));
        this.renderArmor(matrixStack, vertexConsumerProvider, ((BipedEntityRenderState)bipedEntityRenderState).equippedLegsStack, EquipmentSlot.LEGS, 1, this.getModel(bipedEntityRenderState, EquipmentSlot.LEGS));
        this.renderArmor(matrixStack, vertexConsumerProvider, ((BipedEntityRenderState)bipedEntityRenderState).equippedFeetStack, EquipmentSlot.FEET, 1, this.getModel(bipedEntityRenderState, EquipmentSlot.FEET));
        this.renderArmor(matrixStack, vertexConsumerProvider, ((BipedEntityRenderState)bipedEntityRenderState).equippedHeadStack, EquipmentSlot.HEAD, 1, this.getModel(bipedEntityRenderState, EquipmentSlot.HEAD));
    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel) {
        EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || !ArmorFeatureRenderer.hasModel(equippableComponent, slot)) {
            return;
        }
        ((BipedEntityModel)this.getContextModel()).copyTransforms(armorModel);
        this.setVisible(armorModel, slot);
        EquipmentModel.LayerType layerType = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
        this.equipmentRenderer.render(layerType, equippableComponent.assetId().orElseThrow(), (Model)armorModel, stack, matrices, vertexConsumers, light);
    }

    protected void setVisible(A bipedModel, EquipmentSlot slot) {
        ((BipedEntityModel)bipedModel).setVisible(false);
        switch (slot) {
            case HEAD: {
                ((BipedEntityModel)bipedModel).head.visible = true;
                ((BipedEntityModel)bipedModel).hat.visible = true;
                break;
            }
            case CHEST: {
                ((BipedEntityModel)bipedModel).body.visible = true;
                ((BipedEntityModel)bipedModel).rightArm.visible = true;
                ((BipedEntityModel)bipedModel).leftArm.visible = true;
                break;
            }
            case LEGS: {
                ((BipedEntityModel)bipedModel).body.visible = true;
                ((BipedEntityModel)bipedModel).rightLeg.visible = true;
                ((BipedEntityModel)bipedModel).leftLeg.visible = true;
                break;
            }
            case FEET: {
                ((BipedEntityModel)bipedModel).rightLeg.visible = true;
                ((BipedEntityModel)bipedModel).leftLeg.visible = true;
            }
        }
    }

    private A getModel(S state, EquipmentSlot slot) {
        if (this.usesInnerModel(slot)) {
            return ((BipedEntityRenderState)state).baby ? this.babyInnerModel : this.innerModel;
        }
        return ((BipedEntityRenderState)state).baby ? this.babyOuterModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }
}

