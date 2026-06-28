/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractMinecartEntityRenderer<T extends AbstractMinecartEntity, S extends MinecartEntityRenderState>
extends EntityRenderer<T, S> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/minecart.png");
    final static private float field_56953 = 0.75f;
    final protected MinecartEntityModel model;
    final private BlockRenderManager blockRenderManager;

    public AbstractMinecartEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx);
        this.shadowRadius = 0.7f;
        this.model = new MinecartEntityModel(ctx.getPart(layer));
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public void render(S minecartEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        BlockState blockState;
        super.render(minecartEntityRenderState, matrixStack, vertexConsumerProvider, 1);
        matrixStack.push();
        long l = ((MinecartEntityRenderState)minecartEntityRenderState).hash;
        float f = (((float)(l >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float g = (((float)(l >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float h = (((float)(l >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        matrixStack.translate(f, g, h);
        if (((MinecartEntityRenderState)minecartEntityRenderState).usesExperimentalController) {
            AbstractMinecartEntityRenderer.transformExperimentalControllerMinecart(minecartEntityRenderState, matrixStack);
        } else {
            AbstractMinecartEntityRenderer.transformDefaultControllerMinecart(minecartEntityRenderState, matrixStack);
        }
        float j = ((MinecartEntityRenderState)minecartEntityRenderState).damageWobbleTicks;
        if (j > 0.0f) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(j) * j * ((MinecartEntityRenderState)minecartEntityRenderState).damageWobbleStrength / 10.0f * (float)((MinecartEntityRenderState)minecartEntityRenderState).damageWobbleSide));
        }
        if ((blockState = ((MinecartEntityRenderState)minecartEntityRenderState).containedBlock).getRenderType() != BlockRenderType.INVISIBLE) {
            matrixStack.push();
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.translate(-0.5f, (float)(((MinecartEntityRenderState)minecartEntityRenderState).blockOffset - 8) / 16.0f, 0.5f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            this.renderBlock(minecartEntityRenderState, blockState, matrixStack, vertexConsumerProvider, 1);
            matrixStack.pop();
        }
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.model.setAngles(minecartEntityRenderState);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
    }

    private static <S extends MinecartEntityRenderState> void transformExperimentalControllerMinecart(S state, MatrixStack matrices) {
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(state.lerpedYaw));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(-state.lerpedPitch));
        matrices.translate(0.0f, 0.375f, 0.0f);
    }

    private static <S extends MinecartEntityRenderState> void transformDefaultControllerMinecart(S state, MatrixStack matrices) {
        double d = state.x;
        double e = state.y;
        double f = state.z;
        float g = state.lerpedPitch;
        float h = state.lerpedYaw;
        if (state.presentPos != null && state.futurePos != null && state.pastPos != null) {
            Vec3d vec3d = state.futurePos;
            Vec3d vec3d2 = state.pastPos;
            matrices.translate(state.presentPos.x - d, (vec3d.y + vec3d2.y) / 2.0 - e, state.presentPos.z - f);
            Vec3d vec3d3 = vec3d2.add(-vec3d.x, -vec3d.y, -vec3d.z);
            if (vec3d3.length() != 0.0) {
                vec3d3 = vec3d3.normalize();
                h = (float)(Math.atan2(vec3d3.z, vec3d3.x) * 180.0 / Math.PI);
                g = (float)(Math.atan(vec3d3.y) * 73.0);
            }
        }
        matrices.translate(0.0f, 0.375f, 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - h));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(-g));
    }

    @Override
    public void updateRenderState(T abstractMinecartEntity, S minecartEntityRenderState, float f) {
        super.updateRenderState(abstractMinecartEntity, minecartEntityRenderState, f);
        MinecartController minecartController = ((AbstractMinecartEntity)abstractMinecartEntity).getController();
        if (minecartController instanceof ExperimentalMinecartController) {
            ExperimentalMinecartController experimentalMinecartController = (ExperimentalMinecartController)minecartController;
            AbstractMinecartEntityRenderer.updateFromExperimentalController(abstractMinecartEntity, experimentalMinecartController, minecartEntityRenderState, f);
            ((MinecartEntityRenderState)minecartEntityRenderState).usesExperimentalController = true;
        } else {
            minecartController = ((AbstractMinecartEntity)abstractMinecartEntity).getController();
            if (minecartController instanceof DefaultMinecartController) {
                DefaultMinecartController defaultMinecartController = (DefaultMinecartController)minecartController;
                AbstractMinecartEntityRenderer.updateFromDefaultController(abstractMinecartEntity, defaultMinecartController, minecartEntityRenderState, f);
                ((MinecartEntityRenderState)minecartEntityRenderState).usesExperimentalController = false;
            }
        }
        long l = (long)((Entity)abstractMinecartEntity).getId() * 493286711L;
        ((MinecartEntityRenderState)minecartEntityRenderState).hash = l * l * 4392167121L + l * 98761L;
        ((MinecartEntityRenderState)minecartEntityRenderState).damageWobbleTicks = (float)((VehicleEntity)abstractMinecartEntity).getDamageWobbleTicks() - f;
        ((MinecartEntityRenderState)minecartEntityRenderState).damageWobbleSide = ((VehicleEntity)abstractMinecartEntity).getDamageWobbleSide();
        ((MinecartEntityRenderState)minecartEntityRenderState).damageWobbleStrength = Math.max(((VehicleEntity)abstractMinecartEntity).getDamageWobbleStrength() - f, 0.0f);
        ((MinecartEntityRenderState)minecartEntityRenderState).blockOffset = ((AbstractMinecartEntity)abstractMinecartEntity).getBlockOffset();
        ((MinecartEntityRenderState)minecartEntityRenderState).containedBlock = ((AbstractMinecartEntity)abstractMinecartEntity).getContainedBlock();
    }

    private static <T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> void updateFromExperimentalController(T minecart, ExperimentalMinecartController controller, S state, float tickProgress) {
        if (controller.hasCurrentLerpSteps()) {
            state.lerpedPos = controller.getLerpedPosition(tickProgress);
            state.lerpedPitch = controller.getLerpedPitch(tickProgress);
            state.lerpedYaw = controller.getLerpedYaw(tickProgress);
        } else {
            state.lerpedPos = null;
            state.lerpedPitch = minecart.getPitch();
            state.lerpedYaw = minecart.getYaw();
        }
    }

    private static <T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> void updateFromDefaultController(T minecart, DefaultMinecartController controller, S state, float tickProgress) {
        float f = 0.3f;
        state.lerpedPitch = minecart.getLerpedPitch(tickProgress);
        state.lerpedYaw = minecart.getLerpedYaw(tickProgress);
        double d = state.x;
        double e = state.y;
        double g = state.z;
        Vec3d vec3d = controller.snapPositionToRail(d, e, g);
        if (vec3d != null) {
            state.presentPos = vec3d;
            Vec3d vec3d2 = controller.simulateMovement(d, e, g, 0.3f);
            Vec3d vec3d3 = controller.simulateMovement(d, e, g, -0.3f);
            state.futurePos = Objects.requireNonNullElse(vec3d2, vec3d);
            state.pastPos = Objects.requireNonNullElse(vec3d3, vec3d);
        } else {
            state.presentPos = null;
            state.futurePos = null;
            state.pastPos = null;
        }
    }

    protected void renderBlock(S state, BlockState blockState, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        this.blockRenderManager.renderBlockAsEntity(blockState, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected Box getBoundingBox(T abstractMinecartEntity) {
        Box box = super.getBoundingBox(abstractMinecartEntity);
        if (!((AbstractMinecartEntity)abstractMinecartEntity).getContainedBlock().isAir()) {
            return box.stretch(0.0, (float)((AbstractMinecartEntity)abstractMinecartEntity).getBlockOffset() * 0.75f / 16.0f, 0.0);
        }
        return box;
    }

    @Override
    public Vec3d getPositionOffset(S minecartEntityRenderState) {
        Vec3d vec3d = super.getPositionOffset(minecartEntityRenderState);
        if (((MinecartEntityRenderState)minecartEntityRenderState).usesExperimentalController && ((MinecartEntityRenderState)minecartEntityRenderState).lerpedPos != null) {
            return vec3d.add(((MinecartEntityRenderState)minecartEntityRenderState).lerpedPos.x - ((MinecartEntityRenderState)minecartEntityRenderState).x, ((MinecartEntityRenderState)minecartEntityRenderState).lerpedPos.y - ((MinecartEntityRenderState)minecartEntityRenderState).y, ((MinecartEntityRenderState)minecartEntityRenderState).lerpedPos.z - ((MinecartEntityRenderState)minecartEntityRenderState).z);
        }
        return vec3d;
    }
}

