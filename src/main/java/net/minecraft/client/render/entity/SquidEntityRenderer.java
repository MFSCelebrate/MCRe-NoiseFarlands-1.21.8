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
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SquidEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class SquidEntityRenderer<T extends SquidEntity>
extends AgeableMobEntityRenderer<T, SquidEntityRenderState, SquidEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/squid/squid.png");

    public SquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel model, SquidEntityModel babyModel) {
        super(context, model, babyModel, 0.7f);
    }

    @Override
    public Identifier getTexture(SquidEntityRenderState squidEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SquidEntityRenderState net_minecraft_client_render_entity_state_SquidEntityRenderState_createRenderState() {
        return new SquidEntityRenderState();
    }

    @Override
    public void updateRenderState(T squidEntity, SquidEntityRenderState squidEntityRenderState, float f) {
        super.updateRenderState(squidEntity, squidEntityRenderState, f);
        squidEntityRenderState.tentacleAngle = MathHelper.lerp(f, ((SquidEntity)squidEntity).lastTentacleAngle, ((SquidEntity)squidEntity).tentacleAngle);
        squidEntityRenderState.tiltAngle = MathHelper.lerp(f, ((SquidEntity)squidEntity).lastTiltAngle, ((SquidEntity)squidEntity).tiltAngle);
        squidEntityRenderState.rollAngle = MathHelper.lerp(f, ((SquidEntity)squidEntity).lastRollAngle, ((SquidEntity)squidEntity).rollAngle);
    }

    @Override
    protected void setupTransforms(SquidEntityRenderState squidEntityRenderState, MatrixStack matrixStack, float f, float g) {
        matrixStack.translate(0.0f, squidEntityRenderState.baby ? 0.25f : 0.5f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(squidEntityRenderState.tiltAngle));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(squidEntityRenderState.rollAngle));
        matrixStack.translate(0.0f, squidEntityRenderState.baby ? -0.6f : -1.2f, 0.0f);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SquidEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_SquidEntityRenderState_createRenderState();
    }
}

