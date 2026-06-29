/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SalmonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityRenderer
extends MobEntityRenderer<SalmonEntity, SalmonEntityRenderState, SalmonEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/salmon.png");
    final private SalmonEntityModel smallModel;
    final private SalmonEntityModel mediumModel;
    final private SalmonEntityModel largeModel;

    public SalmonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON)), 0.4f);
        this.smallModel = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON_SMALL));
        this.mediumModel = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON));
        this.largeModel = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON_LARGE));
    }

    @Override
    public void updateRenderState(SalmonEntity salmonEntity, SalmonEntityRenderState salmonEntityRenderState, float f) {
        super.updateRenderState(salmonEntity, salmonEntityRenderState, f);
        salmonEntityRenderState.variant = salmonEntity.getVariant();
    }

    @Override
    public Identifier getTexture(SalmonEntityRenderState salmonEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SalmonEntityRenderState net_minecraft_client_render_entity_state_SalmonEntityRenderState_createRenderState() {
        return new SalmonEntityRenderState();
    }

    @Override
    protected void setupTransforms(SalmonEntityRenderState salmonEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(salmonEntityRenderState, matrixStack, f, g);
        float h = 1.0f;
        float i = 1.0f;
        if (!salmonEntityRenderState.touchingWater) {
            h = 1.3f;
            i = 1.7f;
        }
        float j = h * 4.3f * MathHelper.sin(i * 0.6f * salmonEntityRenderState.age);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(j));
        if (!salmonEntityRenderState.touchingWater) {
            matrixStack.translate(0.2f, 0.1f, 0.0f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }

    @Override
    public void render(SalmonEntityRenderState salmonEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.model = salmonEntityRenderState.variant == SalmonEntity.Variant.SMALL ? this.smallModel : (salmonEntityRenderState.variant == SalmonEntity.Variant.LARGE ? this.largeModel : this.mediumModel);
        super.render(salmonEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SalmonEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_SalmonEntityRenderState_createRenderState();
    }
}

