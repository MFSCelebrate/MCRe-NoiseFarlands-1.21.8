/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandEntityRenderState, ArmorStandArmorEntityModel> {
    final static public Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armorstand/wood.png");
    final private ArmorStandArmorEntityModel mainModel = (ArmorStandArmorEntityModel)this.getModel();
    final private ArmorStandArmorEntityModel smallModel;

    public ArmorStandEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND)), 0.0f);
        this.smallModel = new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL));
        this.addFeature(new ArmorFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel>(this, new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_INNER_ARMOR)), new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_OUTER_ARMOR)), new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL_INNER_ARMOR)), new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL_OUTER_ARMOR)), context.getEquipmentRenderer()));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this, context.getEntityModels()));
    }

    @Override
    public Identifier getTexture(ArmorStandEntityRenderState armorStandEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public ArmorStandEntityRenderState net_minecraft_client_render_entity_state_ArmorStandEntityRenderState_createRenderState() {
        return new ArmorStandEntityRenderState();
    }

    @Override
    public void updateRenderState(ArmorStandEntity armorStandEntity, ArmorStandEntityRenderState armorStandEntityRenderState, float f) {
        super.updateRenderState(armorStandEntity, armorStandEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState(armorStandEntity, armorStandEntityRenderState, f, this.itemModelResolver);
        armorStandEntityRenderState.yaw = MathHelper.lerpAngleDegrees(f, armorStandEntity.lastYaw, armorStandEntity.getYaw());
        armorStandEntityRenderState.marker = armorStandEntity.isMarker();
        armorStandEntityRenderState.small = armorStandEntity.isSmall();
        armorStandEntityRenderState.showArms = armorStandEntity.shouldShowArms();
        armorStandEntityRenderState.showBasePlate = armorStandEntity.shouldShowBasePlate();
        armorStandEntityRenderState.bodyRotation = armorStandEntity.getBodyRotation();
        armorStandEntityRenderState.headRotation = armorStandEntity.getHeadRotation();
        armorStandEntityRenderState.leftArmRotation = armorStandEntity.getLeftArmRotation();
        armorStandEntityRenderState.rightArmRotation = armorStandEntity.getRightArmRotation();
        armorStandEntityRenderState.leftLegRotation = armorStandEntity.getLeftLegRotation();
        armorStandEntityRenderState.rightLegRotation = armorStandEntity.getRightLegRotation();
        armorStandEntityRenderState.timeSinceLastHit = (float)(armorStandEntity.net_minecraft_world_World_getWorld().getTime() - armorStandEntity.lastHitTime) + f;
    }

    @Override
    public void render(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.model = armorStandEntityRenderState.small ? this.smallModel : this.mainModel;
        super.render(armorStandEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    protected void setupTransforms(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, float f, float g) {
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        if (armorStandEntityRenderState.timeSinceLastHit < 5.0f) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(armorStandEntityRenderState.timeSinceLastHit / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    @Override
    protected boolean hasLabel(ArmorStandEntity armorStandEntity, double d) {
        return armorStandEntity.isCustomNameVisible();
    }

    @Override
    @Nullable
    protected RenderLayer getRenderLayer(ArmorStandEntityRenderState armorStandEntityRenderState, boolean bl, boolean bl2, boolean bl3) {
        if (!armorStandEntityRenderState.marker) {
            return super.getRenderLayer(armorStandEntityRenderState, bl, bl2, bl3);
        }
        Identifier identifier = this.getTexture(armorStandEntityRenderState);
        if (bl2) {
            return RenderLayer.getEntityTranslucent(identifier, false);
        }
        if (bl) {
            return RenderLayer.getEntityCutoutNoCull(identifier, false);
        }
        return null;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ArmorStandEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ArmorStandEntityRenderState_createRenderState();
    }
}

