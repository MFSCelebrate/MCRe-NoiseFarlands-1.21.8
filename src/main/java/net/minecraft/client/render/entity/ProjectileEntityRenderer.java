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
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.ArrowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends PersistentProjectileEntity, S extends ProjectileEntityRenderState>
extends EntityRenderer<T, S> {
    final private ArrowEntityModel model;

    public ProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ArrowEntityModel(context.getPart(EntityModelLayers.ARROW));
    }

    @Override
    public void render(S projectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(((ProjectileEntityRenderState)projectileEntityRenderState).yaw - 90.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(((ProjectileEntityRenderState)projectileEntityRenderState).pitch));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(projectileEntityRenderState)));
        this.model.setAngles((ProjectileEntityRenderState)projectileEntityRenderState);
        this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(projectileEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    protected abstract Identifier getTexture(S var1);

    @Override
    public void updateRenderState(T persistentProjectileEntity, S projectileEntityRenderState, float f) {
        super.updateRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        ((ProjectileEntityRenderState)projectileEntityRenderState).pitch = ((Entity)persistentProjectileEntity).getLerpedPitch(f);
        ((ProjectileEntityRenderState)projectileEntityRenderState).yaw = ((Entity)persistentProjectileEntity).getLerpedYaw(f);
        ((ProjectileEntityRenderState)projectileEntityRenderState).shake = (float)((PersistentProjectileEntity)persistentProjectileEntity).shake - f;
    }
}

