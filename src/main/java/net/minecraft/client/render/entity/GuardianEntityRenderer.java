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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityRenderer
extends MobEntityRenderer<GuardianEntity, GuardianEntityRenderState, GuardianEntityModel> {
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/guardian.png");
    final static private Identifier EXPLOSION_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/guardian_beam.png");
    final static private RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

    public GuardianEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 0.5f, EntityModelLayers.GUARDIAN);
    }

    protected GuardianEntityRenderer(EntityRendererFactory.Context ctx, float shadowRadius, EntityModelLayer layer) {
        super(ctx, new GuardianEntityModel(ctx.getPart(layer)), shadowRadius);
    }

    @Override
    public boolean shouldRender(GuardianEntity guardianEntity, Frustum frustum, double d, double e, double f) {
        LivingEntity livingEntity;
        if (super.shouldRender(guardianEntity, frustum, d, e, f)) {
            return true;
        }
        if (guardianEntity.hasBeamTarget() && (livingEntity = guardianEntity.getBeamTarget()) != null) {
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, 1.0f);
            Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, guardianEntity.getStandingEyeHeight(), 1.0f);
            return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
        }
        return false;
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = MathHelper.lerp((double)delta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)delta, entity.lastRenderY, entity.getY()) + yOffset;
        double f = MathHelper.lerp((double)delta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(d, e, f);
    }

    @Override
    public void render(GuardianEntityRenderState guardianEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(guardianEntityRenderState, matrixStack, vertexConsumerProvider, 1);
        Vec3d vec3d = guardianEntityRenderState.beamTargetPos;
        if (vec3d != null) {
            float f = guardianEntityRenderState.beamTicks * 0.5f % 1.0f;
            matrixStack.push();
            matrixStack.translate(0.0f, guardianEntityRenderState.standingEyeHeight, 0.0f);
            GuardianEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider.getBuffer(LAYER), vec3d.subtract(guardianEntityRenderState.cameraPosVec), guardianEntityRenderState.beamTicks, guardianEntityRenderState.beamProgress, f);
            matrixStack.pop();
        }
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumer vertexConsumer, Vec3d vec3d, float beamTicks, float f, float g) {
        float h = (float)(vec3d.length() + 1.0);
        vec3d = vec3d.normalize();
        float i = (float)Math.acos(vec3d.y);
        float j = 1.5707964f - (float)Math.atan2(vec3d.z, vec3d.x);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(j * 57.295776f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(i * 57.295776f));
        float k = beamTicks * 0.05f * -1.5f;
        float l = f * f;
        int m = 64 + (int)(l * 191.0f);
        int n = 32 + (int)(l * 191.0f);
        int o = 128 - (int)(l * 64.0f);
        float p = 0.2f;
        float q = 0.282f;
        float r = MathHelper.cos(k + 2.3561945f) * 0.282f;
        float s = MathHelper.sin(k + 2.3561945f) * 0.282f;
        float t = MathHelper.cos(k + 0.7853982f) * 0.282f;
        float u = MathHelper.sin(k + 0.7853982f) * 0.282f;
        float v = MathHelper.cos(k + 3.926991f) * 0.282f;
        float w = MathHelper.sin(k + 3.926991f) * 0.282f;
        float x = MathHelper.cos(k + 5.4977875f) * 0.282f;
        float y = MathHelper.sin(k + 5.4977875f) * 0.282f;
        float z = MathHelper.cos(k + (float)Math.PI) * 0.2f;
        float aa = MathHelper.sin(k + (float)Math.PI) * 0.2f;
        float ab = MathHelper.cos(k + 0.0f) * 0.2f;
        float ac = MathHelper.sin(k + 0.0f) * 0.2f;
        float ad = MathHelper.cos(k + 1.5707964f) * 0.2f;
        float ae = MathHelper.sin(k + 1.5707964f) * 0.2f;
        float af = MathHelper.cos(k + 4.712389f) * 0.2f;
        float ag = MathHelper.sin(k + 4.712389f) * 0.2f;
        float ah = h;
        float ai = 0.0f;
        float aj = 0.4999f;
        float ak = -1.0f + g;
        float al = ak + h * 2.5f;
        MatrixStack.Entry entry = matrices.peek();
        GuardianEntityRenderer.vertex(vertexConsumer, entry, z, ah, aa, m, n, o, 0.4999f, al);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, z, 0.0f, aa, m, n, o, 0.4999f, ak);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, ab, 0.0f, ac, m, n, o, 0.0f, ak);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, ab, ah, ac, m, n, o, 0.0f, al);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, ad, ah, ae, m, n, o, 0.4999f, al);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, ad, 0.0f, ae, m, n, o, 0.4999f, ak);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, af, 0.0f, ag, m, n, o, 0.0f, ak);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, af, ah, ag, m, n, o, 0.0f, al);
        float am = MathHelper.floor(beamTicks) % 2 == 0 ? 0.5f : 0.0f;
        GuardianEntityRenderer.vertex(vertexConsumer, entry, r, ah, s, m, n, o, 0.5f, am + 0.5f);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, t, ah, u, m, n, o, 1.0f, am + 0.5f);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, x, ah, y, m, n, o, 1.0f, am);
        GuardianEntityRenderer.vertex(vertexConsumer, entry, v, ah, w, m, n, o, 0.5f, am);
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, float z, int red, int green, int blue, float u, float v) {
        vertexConsumer.vertex(matrix, x, y, z).color(red, green, blue, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public GuardianEntityRenderState net_minecraft_client_render_entity_state_GuardianEntityRenderState_createRenderState() {
        return new GuardianEntityRenderState();
    }

    @Override
    public void updateRenderState(GuardianEntity guardianEntity, GuardianEntityRenderState guardianEntityRenderState, float f) {
        super.updateRenderState(guardianEntity, guardianEntityRenderState, f);
        guardianEntityRenderState.spikesExtension = guardianEntity.getSpikesExtension(f);
        guardianEntityRenderState.tailAngle = guardianEntity.getTailAngle(f);
        guardianEntityRenderState.cameraPosVec = guardianEntity.getCameraPosVec(f);
        Entity entity = GuardianEntityRenderer.getBeamTarget(guardianEntity);
        if (entity != null) {
            guardianEntityRenderState.rotationVec = guardianEntity.getRotationVec(f);
            guardianEntityRenderState.lookAtPos = entity.getCameraPosVec(f);
        } else {
            guardianEntityRenderState.rotationVec = null;
            guardianEntityRenderState.lookAtPos = null;
        }
        LivingEntity livingEntity = guardianEntity.getBeamTarget();
        if (livingEntity != null) {
            guardianEntityRenderState.beamProgress = guardianEntity.getBeamProgress(f);
            guardianEntityRenderState.beamTicks = guardianEntity.getBeamTicks() + f;
            guardianEntityRenderState.beamTargetPos = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, f);
        } else {
            guardianEntityRenderState.beamTargetPos = null;
        }
    }

    @Nullable
    private static Entity getBeamTarget(GuardianEntity guardian) {
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (guardian.hasBeamTarget()) {
            return guardian.getBeamTarget();
        }
        return entity;
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GuardianEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_GuardianEntityRenderState_createRenderState();
    }
}

