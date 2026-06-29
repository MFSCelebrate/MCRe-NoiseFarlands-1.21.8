/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class SaddleFeatureRenderer<S extends LivingEntityRenderState, RM extends EntityModel<? super S>, EM extends EntityModel<? super S>>
extends FeatureRenderer<S, RM> {
    final private EquipmentRenderer equipmentRenderer;
    final private EquipmentModel.LayerType layerType;
    final private Function<S, ItemStack> saddleStackGetter;
    final private EM adultModel;
    final private EM babyModel;

    public SaddleFeatureRenderer(FeatureRendererContext<S, RM> context, EquipmentRenderer equipmentRenderer, EquipmentModel.LayerType layerType, Function<S, ItemStack> saddleStackGetter, EM adultModel, EM babyModel) {
        super(context);
        this.equipmentRenderer = equipmentRenderer;
        this.layerType = layerType;
        this.saddleStackGetter = saddleStackGetter;
        this.adultModel = adultModel;
        this.babyModel = babyModel;
    }

    public SaddleFeatureRenderer(FeatureRendererContext<S, RM> context, EquipmentRenderer equipmentRenderer, EM model, EquipmentModel.LayerType layerType, Function<S, ItemStack> saddleStackGetter) {
        this(context, equipmentRenderer, layerType, saddleStackGetter, model, model);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g) {
        ItemStack itemStack = this.saddleStackGetter.apply(livingEntityRenderState);
        EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.assetId().isEmpty()) {
            return;
        }
        EM entityModel = ((LivingEntityRenderState)livingEntityRenderState).baby ? this.babyModel : this.adultModel;
        ((EntityModel)entityModel).setAngles(livingEntityRenderState);
        this.equipmentRenderer.render(this.layerType, equippableComponent.assetId().get(), (Model)entityModel, itemStack, matrixStack, vertexConsumerProvider, 1);
    }
}

