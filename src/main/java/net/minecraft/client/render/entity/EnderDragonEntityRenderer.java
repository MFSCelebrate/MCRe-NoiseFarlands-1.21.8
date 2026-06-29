/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.DragonEntityModel;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderer
extends EntityRenderer<EnderDragonEntity, EnderDragonEntityRenderState> {
    final static public Identifier CRYSTAL_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal_beam.png");
    final static private Identifier EXPLOSION_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_exploding.png");
    final static private Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon.png");
    final static private Identifier EYE_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_eyes.png");
    final static private RenderLayer DRAGON_CUTOUT = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    final static private RenderLayer DRAGON_DECAL = RenderLayer.getEntityDecal(TEXTURE);
    final static private RenderLayer DRAGON_EYES = RenderLayer.getEyes(EYE_TEXTURE);
    final static private RenderLayer CRYSTAL_BEAM_LAYER = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEXTURE);
    final static private float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
    final private DragonEntityModel model;

    public EnderDragonEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.model = new DragonEntityModel(context.getPart(EntityModelLayers.ENDER_DRAGON));
    }

    @Override
    public void render(EnderDragonEntityRenderState enderDragonEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float f = enderDragonEntityRenderState.getLerpedFrame(7).yRot();
        float g = (float)(enderDragonEntityRenderState.getLerpedFrame(5).y() - enderDragonEntityRenderState.getLerpedFrame(10).y());
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(g * 10.0f));
        matrixStack.translate(0.0f, 0.0f, 1.0f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        this.model.setAngles(enderDragonEntityRenderState);
        if (enderDragonEntityRenderState.ticksSinceDeath > 0.0f) {
            float h = enderDragonEntityRenderState.ticksSinceDeath / 200.0f;
            int j = ColorHelper.withAlpha(MathHelper.floor(h * 255.0f), Colors.WHITE);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEXTURE));
            this.model.render(matrixStack, vertexConsumer, 1, OverlayTexture.DEFAULT_UV, j);
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(DRAGON_DECAL);
            this.model.render(matrixStack, vertexConsumer2, 1, OverlayTexture.getUv(0.0f, enderDragonEntityRenderState.hurt));
        } else {
            VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_CUTOUT);
            this.model.render(matrixStack, vertexConsumer3, 1, OverlayTexture.getUv(0.0f, enderDragonEntityRenderState.hurt));
        }
        VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_EYES);
        this.model.render(matrixStack, vertexConsumer3, 1, OverlayTexture.DEFAULT_UV);
        if (enderDragonEntityRenderState.ticksSinceDeath > 0.0f) {
            float k = enderDragonEntityRenderState.ticksSinceDeath / 200.0f;
            matrixStack.push();
            matrixStack.translate(0.0f, -1.0f, -2.0f);
            EnderDragonEntityRenderer.renderDeathAnimation(matrixStack, k, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRays()));
            EnderDragonEntityRenderer.renderDeathAnimation(matrixStack, k, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRaysDepth()));
            matrixStack.pop();
        }
        matrixStack.pop();
        if (enderDragonEntityRenderState.crystalBeamPos != null) {
            EnderDragonEntityRenderer.renderCrystalBeam((float)enderDragonEntityRenderState.crystalBeamPos.x, (float)enderDragonEntityRenderState.crystalBeamPos.y, (float)enderDragonEntityRenderState.crystalBeamPos.z, enderDragonEntityRenderState.age, matrixStack, vertexConsumerProvider, 1);
        }
        super.render(enderDragonEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    private static void renderDeathAnimation(MatrixStack matrices, float animationProgress, VertexConsumer vertexCOnsumer) {
        matrices.push();
        float f = Math.min(animationProgress > 0.8f ? (animationProgress - 0.8f) / 0.2f : 0.0f, 1.0f);
        int i = ColorHelper.fromFloats(1.0f - f, 1.0f, 1.0f, 1.0f);
        int j = 0xFF00FF;
        Random random = Random.create(432L);
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f2 = new Vector3f();
        Vector3f vector3f3 = new Vector3f();
        Vector3f vector3f4 = new Vector3f();
        Quaternionf quaternionf = new Quaternionf();
        int k = MathHelper.floor((animationProgress + animationProgress * animationProgress) / 2.0f * 60.0f);
        for (int l = 0; l < k; ++l) {
            quaternionf.rotationXYZ(random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2)).rotateXYZ(random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2) + animationProgress * 1.5707964f);
            matrices.multiply((Quaternionfc)quaternionf);
            float g = random.nextFloat() * 20.0f + 5.0f + f * 10.0f;
            float h = random.nextFloat() * 2.0f + 1.0f + f * 2.0f;
            vector3f2.set(-HALF_SQRT_3 * h, g, -0.5f * h);
            vector3f3.set(HALF_SQRT_3 * h, g, -0.5f * h);
            vector3f4.set(0.0f, g, h);
            MatrixStack.Entry entry = matrices.peek();
            vertexCOnsumer.vertex(entry, vector3f).color(1);
            vertexCOnsumer.vertex(entry, vector3f2).color(0xFF00FF);
            vertexCOnsumer.vertex(entry, vector3f3).color(0xFF00FF);
            vertexCOnsumer.vertex(entry, vector3f).color(1);
            vertexCOnsumer.vertex(entry, vector3f3).color(0xFF00FF);
            vertexCOnsumer.vertex(entry, vector3f4).color(0xFF00FF);
            vertexCOnsumer.vertex(entry, vector3f).color(1);
            vertexCOnsumer.vertex(entry, vector3f4).color(0xFF00FF);
            vertexCOnsumer.vertex(entry, vector3f2).color(0xFF00FF);
        }
        matrices.pop();
    }

    public static void renderCrystalBeam(float dx, float dy, float dz, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float f = MathHelper.sqrt(dx * dx + dz * dz);
        float g = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        matrices.push();
        matrices.translate(0.0f, 2.0f, 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation((float)(-Math.atan2(dz, dx)) - 1.5707964f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation((float)(-Math.atan2(f, dy)) - 1.5707964f));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(CRYSTAL_BEAM_LAYER);
        float h = 0.0f - tickProgress * 0.01f;
        float i = g / 32.0f - tickProgress * 0.01f;
        int j = 8;
        float k = 0.0f;
        float l = 0.75f;
        float m = 0.0f;
        MatrixStack.Entry entry = matrices.peek();
        for (int n = 1; n <= 8; ++n) {
            float o = MathHelper.sin((float)n * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float p = MathHelper.cos((float)n * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float q = (float)n / 8.0f;
            vertexConsumer.vertex(entry, k * 0.2f, l * 0.2f, 0.0f).color(Colors.BLACK).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0f, -1.0f, 0.0f);
            vertexConsumer.vertex(entry, k, l, g).color(Colors.WHITE).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0f, -1.0f, 0.0f);
            vertexConsumer.vertex(entry, o, p, g).color(Colors.WHITE).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0f, -1.0f, 0.0f);
            vertexConsumer.vertex(entry, o * 0.2f, p * 0.2f, 0.0f).color(Colors.BLACK).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0f, -1.0f, 0.0f);
            k = o;
            l = p;
            m = q;
        }
        matrices.pop();
    }

    @Override
    public EnderDragonEntityRenderState net_minecraft_client_render_entity_state_EnderDragonEntityRenderState_createRenderState() {
        return new EnderDragonEntityRenderState();
    }

    @Override
    public void updateRenderState(EnderDragonEntity enderDragonEntity, EnderDragonEntityRenderState enderDragonEntityRenderState, float f) {
        super.updateRenderState(enderDragonEntity, enderDragonEntityRenderState, f);
        enderDragonEntityRenderState.wingPosition = MathHelper.lerp(f, enderDragonEntity.lastWingPosition, enderDragonEntity.wingPosition);
        enderDragonEntityRenderState.ticksSinceDeath = enderDragonEntity.ticksSinceDeath > 0 ? (float)enderDragonEntity.ticksSinceDeath + f : 0.0f;
        enderDragonEntityRenderState.hurt = enderDragonEntity.hurtTime > 0;
        EndCrystalEntity endCrystalEntity = enderDragonEntity.connectedCrystal;
        if (endCrystalEntity != null) {
            Vec3d vec3d = endCrystalEntity.getLerpedPos(f).add(0.0, EndCrystalEntityRenderer.getYOffset((float)endCrystalEntity.endCrystalAge + f), 0.0);
            enderDragonEntityRenderState.crystalBeamPos = vec3d.subtract(enderDragonEntity.getLerpedPos(f));
        } else {
            enderDragonEntityRenderState.crystalBeamPos = null;
        }
        Phase phase = enderDragonEntity.getPhaseManager().getCurrent();
        enderDragonEntityRenderState.inLandingOrTakeoffPhase = phase == PhaseType.LANDING || phase == PhaseType.TAKEOFF;
        enderDragonEntityRenderState.sittingOrHovering = phase.isSittingOrHovering();
        BlockPos blockPos = enderDragonEntity.net_minecraft_world_World_getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin(enderDragonEntity.getFightOrigin()));
        enderDragonEntityRenderState.squaredDistanceFromOrigin = blockPos.getSquaredDistance(enderDragonEntity.getPos());
        enderDragonEntityRenderState.tickProgress = enderDragonEntity.isDead() ? 0.0f : f;
        enderDragonEntityRenderState.frameTracker.copyFrom(enderDragonEntity.frameTracker);
    }

    @Override
    protected void appendHitboxes(EnderDragonEntity enderDragonEntity, ImmutableList.Builder<EntityHitbox> builder, float f) {
        super.appendHitboxes(enderDragonEntity, builder, f);
        double d = -MathHelper.lerp((double)f, enderDragonEntity.lastRenderX, enderDragonEntity.getX());
        double e = -MathHelper.lerp((double)f, enderDragonEntity.lastRenderY, enderDragonEntity.getY());
        double g = -MathHelper.lerp((double)f, enderDragonEntity.lastRenderZ, enderDragonEntity.getZ());
        for (EnderDragonPart enderDragonPart : enderDragonEntity.getBodyParts()) {
            Box box = enderDragonPart.getBoundingBox();
            EntityHitbox entityHitbox = new EntityHitbox(box.minX - enderDragonPart.getX(), box.minY - enderDragonPart.getY(), box.minZ - enderDragonPart.getZ(), box.maxX - enderDragonPart.getX(), box.maxY - enderDragonPart.getY(), box.maxZ - enderDragonPart.getZ(), (float)(d + MathHelper.lerp((double)f, enderDragonPart.lastRenderX, enderDragonPart.getX())), (float)(e + MathHelper.lerp((double)f, enderDragonPart.lastRenderY, enderDragonPart.getY())), (float)(g + MathHelper.lerp((double)f, enderDragonPart.lastRenderZ, enderDragonPart.getZ())), 0.25f, 1.0f, 0.0f);
            builder.add((Object)entityHitbox);
        }
    }

    @Override
    protected boolean canBeCulled(EnderDragonEntity enderDragonEntity) {
        return false;
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_EnderDragonEntityRenderState_createRenderState();
    }

    @Override
    protected boolean canBeCulled(Entity entity) {
        return this.canBeCulled((EnderDragonEntity)entity);
    }
}

