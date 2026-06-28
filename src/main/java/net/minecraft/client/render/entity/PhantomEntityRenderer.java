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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PhantomEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class PhantomEntityRenderer
extends MobEntityRenderer<PhantomEntity, PhantomEntityRenderState, PhantomEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/phantom.png");

    public PhantomEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PhantomEntityModel(context.getPart(EntityModelLayers.PHANTOM)), 0.75f);
        this.addFeature(new PhantomEyesFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(PhantomEntityRenderState phantomEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public PhantomEntityRenderState net_minecraft_client_render_entity_state_PhantomEntityRenderState_createRenderState() {
        return new PhantomEntityRenderState();
    }

    @Override
    public void updateRenderState(PhantomEntity phantomEntity, PhantomEntityRenderState phantomEntityRenderState, float f) {
        super.updateRenderState(phantomEntity, phantomEntityRenderState, f);
        phantomEntityRenderState.wingFlapProgress = (float)phantomEntity.getWingFlapTickOffset() + phantomEntityRenderState.age;
        phantomEntityRenderState.size = phantomEntity.getPhantomSize();
    }

    @Override
    protected void scale(PhantomEntityRenderState phantomEntityRenderState, MatrixStack matrixStack) {
        float f = 1.0f + 0.15f * (float)phantomEntityRenderState.size;
        matrixStack.scale(f, f, f);
        matrixStack.translate(0.0f, 1.3125f, 0.1875f);
    }

    @Override
    protected void setupTransforms(PhantomEntityRenderState phantomEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(phantomEntityRenderState, matrixStack, f, g);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(phantomEntityRenderState.pitch));
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PhantomEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_PhantomEntityRenderState_createRenderState();
    }
}

