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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class FishingBobberEntityRenderer
extends EntityRenderer<FishingBobberEntity, FishingBobberEntityState> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fishing_hook.png");
    final static private RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);
    final static private double field_33632 = 960.0;

    public FishingBobberEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(FishingBobberEntity fishingBobberEntity, Frustum frustum, double d, double e, double f) {
        return super.shouldRender(fishingBobberEntity, frustum, d, e, f) && fishingBobberEntity.getPlayerOwner() != null;
    }

    @Override
    public void render(FishingBobberEntityState fishingBobberEntityState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)this.dispatcher.getRotation());
        MatrixStack.Entry entry = matrixStack.peek();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        FishingBobberEntityRenderer.vertex(vertexConsumer, entry, 1, 0.0f, 0, 0, 1);
        FishingBobberEntityRenderer.vertex(vertexConsumer, entry, 1, 1.0f, 0, 1, 1);
        FishingBobberEntityRenderer.vertex(vertexConsumer, entry, 1, 1.0f, 1, 1, 0);
        FishingBobberEntityRenderer.vertex(vertexConsumer, entry, 1, 0.0f, 1, 0, 0);
        matrixStack.pop();
        float f = (float)fishingBobberEntityState.pos.x;
        float g = (float)fishingBobberEntityState.pos.y;
        float h = (float)fishingBobberEntityState.pos.z;
        VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
        MatrixStack.Entry entry2 = matrixStack.peek();
        int j = 16;
        for (int k = 0; k <= 16; ++k) {
            FishingBobberEntityRenderer.renderFishingLine(f, g, h, vertexConsumer2, entry2, FishingBobberEntityRenderer.percentage(k, 16), FishingBobberEntityRenderer.percentage(k + 1, 16));
        }
        matrixStack.pop();
        super.render(fishingBobberEntityState, matrixStack, vertexConsumerProvider, 1);
    }

    public static Arm getArmHoldingRod(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof FishingRodItem ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    private Vec3d getHandPos(PlayerEntity player, float f, float tickProgress) {
        int i;
        int n = i = FishingBobberEntityRenderer.getArmHoldingRod(player) == Arm.RIGHT ? 1 : -1;
        if (!this.dispatcher.gameOptions.getPerspective().isFirstPerson() || player != MinecraftClient.getInstance().player) {
            float g = MathHelper.lerp(tickProgress, player.lastBodyYaw, player.bodyYaw) * ((float)Math.PI / 180);
            double d = MathHelper.sin(g);
            double e = MathHelper.cos(g);
            float h = player.getScale();
            double j = 0.35 * (double)h;
            double k = 0.8 * (double)h;
            float l = player.isInSneakingPose() ? -0.1875f : 0.0f;
            return player.getCameraPosVec(tickProgress).add(-e * j - d * k, (double)l - 0.45 * (double)h, -d * j + e * k);
        }
        double m = 960.0 / (double)this.dispatcher.gameOptions.getFov().getValue().intValue();
        Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition(0.525f, -0.1f).multiply(m).rotateY(f * 0.5f).rotateX(-f * 0.7f);
        return player.getCameraPosVec(tickProgress).add(vec3d);
    }

    private static float percentage(int value, int denominator) {
        return (float)value / (float)denominator;
    }

    private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(Colors.WHITE).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5f + 0.25f;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5f + 0.25f - g;
        float k = z * segmentEnd - h;
        float l = MathHelper.sqrt(i * i + j * j + k * k);
        buffer.vertex(matrices, f, g, h).color(Colors.BLACK).normal(matrices, i /= l, j /= l, k /= l);
    }

    @Override
    public FishingBobberEntityState net_minecraft_client_render_entity_state_FishingBobberEntityState_createRenderState() {
        return new FishingBobberEntityState();
    }

    @Override
    public void updateRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityState fishingBobberEntityState, float f) {
        super.updateRenderState(fishingBobberEntity, fishingBobberEntityState, f);
        PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
        if (playerEntity == null) {
            fishingBobberEntityState.pos = Vec3d.ZERO;
            return;
        }
        float g = playerEntity.getHandSwingProgress(f);
        float h = MathHelper.sin(MathHelper.sqrt(g) * (float)Math.PI);
        Vec3d vec3d = this.getHandPos(playerEntity, h, f);
        Vec3d vec3d2 = fishingBobberEntity.getLerpedPos(f).add(0.0, 0.25, 0.0);
        fishingBobberEntityState.pos = vec3d.subtract(vec3d2);
    }

    @Override
    protected boolean canBeCulled(FishingBobberEntity fishingBobberEntity) {
        return false;
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_FishingBobberEntityState_createRenderState();
    }

    @Override
    protected boolean canBeCulled(Entity entity) {
        return this.canBeCulled((FishingBobberEntity)entity);
    }
}

