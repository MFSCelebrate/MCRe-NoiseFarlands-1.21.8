/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
    final static protected float field_32921 = 0.025f;
    final static public int field_52257 = 24;
    final static public float field_60152 = 0.05f;
    final protected EntityRenderDispatcher dispatcher;
    final private TextRenderer textRenderer;
    protected float shadowRadius;
    protected float shadowOpacity = 1.0f;
    final private S state = this.createRenderState();

    protected EntityRenderer(EntityRendererFactory.Context context) {
        this.dispatcher = context.getRenderDispatcher();
        this.textRenderer = context.getTextRenderer();
    }

    public final int getLight(T entity, float tickProgress) {
        BlockPos blockPos = BlockPos.ofFloored(((Entity)entity).getClientCameraPosVec(tickProgress));
        return LightmapTextureManager.pack(this.getBlockLight(entity, blockPos), this.getSkyLight(entity, blockPos));
    }

    protected int getSkyLight(T entity, BlockPos pos) {
        return ((Entity)entity).net_minecraft_world_World_getWorld().getLightLevel(LightType.SKY, pos);
    }

    protected int getBlockLight(T entity, BlockPos pos) {
        if (((Entity)entity).isOnFire()) {
            return 15;
        }
        return ((Entity)entity).net_minecraft_world_World_getWorld().getLightLevel(LightType.BLOCK, pos);
    }

    public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
        Leashable leashable;
        Entity entity2;
        if (!((Entity)entity).shouldRender(x, y, z)) {
            return false;
        }
        if (!this.canBeCulled(entity)) {
            return true;
        }
        Box box = this.getBoundingBox(entity).expand(0.5);
        if (box.isNaN() || box.getAverageSideLength() == 0.0) {
            box = new Box(((Entity)entity).getX() - 2.0, ((Entity)entity).getY() - 2.0, ((Entity)entity).getZ() - 2.0, ((Entity)entity).getX() + 2.0, ((Entity)entity).getY() + 2.0, ((Entity)entity).getZ() + 2.0);
        }
        if (frustum.isVisible(box)) {
            return true;
        }
        if (entity instanceof Leashable && (entity2 = (leashable = (Leashable)entity).getLeashHolder()) != null) {
            Box box2 = this.dispatcher.getRenderer(entity2).getBoundingBox(entity2);
            return frustum.isVisible(box2) || frustum.isVisible(box.union(box2));
        }
        return false;
    }

    protected Box getBoundingBox(T entity) {
        return ((Entity)entity).getBoundingBox();
    }

    protected boolean canBeCulled(T entity) {
        return true;
    }

    public Vec3d getPositionOffset(S state) {
        if (((EntityRenderState)state).positionOffset != null) {
            return ((EntityRenderState)state).positionOffset;
        }
        return Vec3d.ZERO;
    }

    public void render(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (((EntityRenderState)state).leashDatas != null) {
            for (EntityRenderState.LeashData leashData : ((EntityRenderState)state).leashDatas) {
                EntityRenderer.renderLeash(matrices, vertexConsumers, leashData);
            }
        }
        if (((EntityRenderState)state).displayName != null) {
            this.renderLabelIfPresent(state, ((EntityRenderState)state).displayName, matrices, vertexConsumers, light);
        }
    }

    private static void renderLeash(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState.LeashData leashData) {
        int l;
        float f = (float)(leashData.endPos.x - leashData.startPos.x);
        float g = (float)(leashData.endPos.y - leashData.startPos.y);
        float h = (float)(leashData.endPos.z - leashData.startPos.z);
        float i = MathHelper.inverseSqrt(f * f + h * h) * 0.05f / 2.0f;
        float j = h * i;
        float k = f * i;
        matrices.push();
        matrices.translate(leashData.offset);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLeash());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        for (l = 0; l <= 24; ++l) {
            EntityRenderer.renderLeashSegment(vertexConsumer, matrix4f, f, g, h, 0.05f, 0.05f, j, k, l, false, leashData);
        }
        for (l = 24; l >= 0; --l) {
            EntityRenderer.renderLeashSegment(vertexConsumer, matrix4f, f, g, h, 0.05f, 0.0f, j, k, l, true, leashData);
        }
        matrices.pop();
    }

    private static void renderLeashSegment(VertexConsumer vertexConsumer, Matrix4f matrix, float leashedEntityX, float leashedEntityY, float leashedEntityZ, float f, float g, float h, float i, int segment, boolean bl, EntityRenderState.LeashData leashData) {
        float j = (float)segment / 24.0f;
        int k = (int)MathHelper.lerp(j, (float)leashData.leashedEntityBlockLight, (float)leashData.leashHolderBlockLight);
        int l = (int)MathHelper.lerp(j, (float)leashData.leashedEntitySkyLight, (float)leashData.leashHolderSkyLight);
        int m = LightmapTextureManager.pack(k, l);
        float n = segment % 2 == (bl ? 1 : 0) ? 0.7f : 1.0f;
        float o = 0.5f * n;
        float p = 0.4f * n;
        float q = 0.3f * n;
        float r = leashedEntityX * j;
        float s = leashData.field_60161 ? (leashedEntityY > 0.0f ? leashedEntityY * j * j : leashedEntityY - leashedEntityY * (1.0f - j) * (1.0f - j)) : leashedEntityY * j;
        float t = leashedEntityZ * j;
        vertexConsumer.vertex(matrix, r - h, s + g, t + i).color(o, p, q, 1.0f).light(m);
        vertexConsumer.vertex(matrix, r + h, s + f - g, t - i).color(o, p, q, 1.0f).light(m);
    }

    protected boolean hasLabel(T entity, double squaredDistanceToCamera) {
        return ((Entity)entity).shouldRenderName() || ((Entity)entity).hasCustomName() && entity == this.dispatcher.targetedEntity;
    }

    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }

    protected void renderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d vec3d = ((EntityRenderState)state).nameLabelPos;
        if (vec3d == null) {
            return;
        }
        boolean bl = !((EntityRenderState)state).sneaking;
        int i = "deadmau5".equals(text.getString()) ? -10 : 0;
        matrices.push();
        matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
        matrices.multiply((Quaternionfc)this.dispatcher.getRotation());
        matrices.scale(0.025f, -0.025f, 0.025f);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = this.getTextRenderer();
        float f = (float)(-textRenderer.getWidth(text)) / 2.0f;
        int j = (int)(MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f) * 255.0f) << 24;
        textRenderer.draw(text, f, (float)i, -2130706433, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
        if (bl) {
            textRenderer.draw(text, f, (float)i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.applyEmission(light, 2));
        }
        matrices.pop();
    }

    @Nullable
    protected Text getDisplayName(T entity) {
        return ((Entity)entity).getDisplayName();
    }

    protected float getShadowRadius(S state) {
        return this.shadowRadius;
    }

    protected float getShadowOpacity(S state) {
        return this.shadowOpacity;
    }

    public abstract S createRenderState();

    public final S getAndUpdateRenderState(T entity, float tickProgress) {
        S entityRenderState = this.state;
        this.updateRenderState(entity, entityRenderState, tickProgress);
        return entityRenderState;
    }

    public void updateRenderState(T entity, S state, float tickProgress) {
        Leashable leashable;
        Entity entity2;
        boolean bl;
        ExperimentalMinecartController experimentalMinecartController;
        AbstractMinecartEntity abstractMinecartEntity;
        Object object;
        ((EntityRenderState)state).entityType = ((Entity)entity).getType();
        ((EntityRenderState)state).x = MathHelper.lerp((double)tickProgress, ((Entity)entity).lastRenderX, ((Entity)entity).getX());
        ((EntityRenderState)state).y = MathHelper.lerp((double)tickProgress, ((Entity)entity).lastRenderY, ((Entity)entity).getY());
        ((EntityRenderState)state).z = MathHelper.lerp((double)tickProgress, ((Entity)entity).lastRenderZ, ((Entity)entity).getZ());
        ((EntityRenderState)state).invisible = ((Entity)entity).isInvisible();
        ((EntityRenderState)state).age = (float)((Entity)entity).age + tickProgress;
        ((EntityRenderState)state).width = ((Entity)entity).getWidth();
        ((EntityRenderState)state).height = ((Entity)entity).getHeight();
        ((EntityRenderState)state).standingEyeHeight = ((Entity)entity).getStandingEyeHeight();
        if (((Entity)entity).hasVehicle() && (object = ((Entity)entity).getVehicle()) instanceof AbstractMinecartEntity && (object = (abstractMinecartEntity = (AbstractMinecartEntity)object).getController()) instanceof ExperimentalMinecartController && (experimentalMinecartController = (ExperimentalMinecartController)object).hasCurrentLerpSteps()) {
            double d = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderX, abstractMinecartEntity.getX());
            double e = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderY, abstractMinecartEntity.getY());
            double f = MathHelper.lerp((double)tickProgress, abstractMinecartEntity.lastRenderZ, abstractMinecartEntity.getZ());
            ((EntityRenderState)state).positionOffset = experimentalMinecartController.getLerpedPosition(tickProgress).subtract(new Vec3d(d, e, f));
        } else {
            ((EntityRenderState)state).positionOffset = null;
        }
        ((EntityRenderState)state).squaredDistanceToCamera = this.dispatcher.getSquaredDistanceToCamera((Entity)entity);
        boolean bl2 = bl = ((EntityRenderState)state).squaredDistanceToCamera < 4096.0 && this.hasLabel(entity, ((EntityRenderState)state).squaredDistanceToCamera);
        if (bl) {
            ((EntityRenderState)state).displayName = this.getDisplayName(entity);
            ((EntityRenderState)state).nameLabelPos = ((Entity)entity).getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, ((Entity)entity).getLerpedYaw(tickProgress));
        } else {
            ((EntityRenderState)state).displayName = null;
        }
        ((EntityRenderState)state).sneaking = ((Entity)entity).isSneaky();
        if (entity instanceof Leashable && (entity2 = (leashable = (Leashable)entity).getLeashHolder()) instanceof Entity) {
            int m;
            Entity entity22 = entity2;
            float g = ((Entity)entity).lerpYaw(tickProgress) * ((float)Math.PI / 180);
            Vec3d vec3d = leashable.getLeashOffset(tickProgress);
            BlockPos blockPos = BlockPos.ofFloored(((Entity)entity).getCameraPosVec(tickProgress));
            BlockPos blockPos2 = BlockPos.ofFloored(entity22.getCameraPosVec(tickProgress));
            int i = this.getBlockLight(entity, blockPos);
            int j = this.dispatcher.getRenderer(entity22).getBlockLight(entity22, blockPos2);
            int k = ((Entity)entity).net_minecraft_world_World_getWorld().getLightLevel(LightType.SKY, blockPos);
            int l = ((Entity)entity).net_minecraft_world_World_getWorld().getLightLevel(LightType.SKY, blockPos2);
            boolean bl22 = entity22.hasQuadLeashAttachmentPoints() && leashable.canUseQuadLeashAttachmentPoint();
            int n = m = bl22 ? 4 : 1;
            if (((EntityRenderState)state).leashDatas == null || ((EntityRenderState)state).leashDatas.size() != m) {
                ((EntityRenderState)state).leashDatas = new ArrayList<EntityRenderState.LeashData>(m);
                for (int n2 = 0; n2 < m; ++n2) {
                    ((EntityRenderState)state).leashDatas.add(new EntityRenderState.LeashData());
                }
            }
            if (bl22) {
                float h = entity22.lerpYaw(tickProgress) * ((float)Math.PI / 180);
                Vec3d vec3d2 = entity22.getLerpedPos(tickProgress);
                Vec3d[] vec3ds = leashable.getQuadLeashOffsets();
                Vec3d[] vec3ds2 = entity22.getHeldQuadLeashOffsets();
                for (int o = 0; o < m; ++o) {
                    EntityRenderState.LeashData leashData = ((EntityRenderState)state).leashDatas.get(o);
                    leashData.offset = vec3ds[o].rotateY(-g);
                    leashData.startPos = ((Entity)entity).getLerpedPos(tickProgress).add(leashData.offset);
                    leashData.endPos = vec3d2.add(vec3ds2[o].rotateY(-h));
                    leashData.leashedEntityBlockLight = i;
                    leashData.leashHolderBlockLight = j;
                    leashData.leashedEntitySkyLight = k;
                    leashData.leashHolderSkyLight = l;
                    leashData.field_60161 = false;
                }
            } else {
                Vec3d vec3d3 = vec3d.rotateY(-g);
                EntityRenderState.LeashData leashData2 = ((EntityRenderState)state).leashDatas.getFirst();
                leashData2.offset = vec3d3;
                leashData2.startPos = ((Entity)entity).getLerpedPos(tickProgress).add(vec3d3);
                leashData2.endPos = entity22.getLeashPos(tickProgress);
                leashData2.leashedEntityBlockLight = i;
                leashData2.leashHolderBlockLight = j;
                leashData2.leashedEntitySkyLight = k;
                leashData2.leashHolderSkyLight = l;
            }
        } else {
            ((EntityRenderState)state).leashDatas = null;
        }
        ((EntityRenderState)state).onFire = ((Entity)entity).doesRenderOnFire();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getEntityRenderDispatcher().shouldRenderHitboxes() && !((EntityRenderState)state).invisible && !minecraftClient.hasReducedDebugInfo()) {
            this.updateDebugState(entity, state, tickProgress);
        } else {
            ((EntityRenderState)state).hitbox = null;
            ((EntityRenderState)state).debugInfo = null;
        }
    }

    private void updateDebugState(T entity, S state, float tickProgress) {
        ((EntityRenderState)state).hitbox = this.createHitbox(entity, tickProgress, false);
        ((EntityRenderState)state).debugInfo = null;
    }

    private EntityHitboxAndView createHitbox(T entity, float tickProgress, boolean green) {
        ImmutableList.Builder builder = new ImmutableList.Builder();
        Box box = ((Entity)entity).getBoundingBox();
        EntityHitbox entityHitbox = green ? new EntityHitbox(box.minX - ((Entity)entity).getX(), box.minY - ((Entity)entity).getY(), box.minZ - ((Entity)entity).getZ(), box.maxX - ((Entity)entity).getX(), box.maxY - ((Entity)entity).getY(), box.maxZ - ((Entity)entity).getZ(), 0.0f, 1.0f, 0.0f) : new EntityHitbox(box.minX - ((Entity)entity).getX(), box.minY - ((Entity)entity).getY(), box.minZ - ((Entity)entity).getZ(), box.maxX - ((Entity)entity).getX(), box.maxY - ((Entity)entity).getY(), box.maxZ - ((Entity)entity).getZ(), 1.0f, 1.0f, 1.0f);
        builder.add((Object)entityHitbox);
        Entity entity2 = ((Entity)entity).getVehicle();
        if (entity2 != null) {
            float f = Math.min(entity2.getWidth(), ((Entity)entity).getWidth()) / 2.0f;
            float g = 0.0625f;
            Vec3d vec3d = entity2.getPassengerRidingPos((Entity)entity).subtract(((Entity)entity).getPos());
            EntityHitbox entityHitbox2 = new EntityHitbox(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + 0.0625, vec3d.z + (double)f, 1.0f, 1.0f, 0.0f);
            builder.add((Object)entityHitbox2);
        }
        this.appendHitboxes(entity, (ImmutableList.Builder<EntityHitbox>)builder, tickProgress);
        Vec3d vec3d2 = ((Entity)entity).getRotationVec(tickProgress);
        return new EntityHitboxAndView(vec3d2.x, vec3d2.y, vec3d2.z, (ImmutableList<EntityHitbox>)builder.build());
    }

    protected void appendHitboxes(T entity, ImmutableList.Builder<EntityHitbox> builder, float tickProgress) {
    }

    @Nullable
    private static Entity getServerEntity(Entity clientEntity) {
        ServerWorld serverWorld;
        IntegratedServer integratedServer = MinecraftClient.getInstance().getServer();
        if (integratedServer != null && (serverWorld = integratedServer.getWorld(clientEntity.net_minecraft_world_World_getWorld().getRegistryKey())) != null) {
            return serverWorld.getEntityById(clientEntity.getId());
        }
        return null;
    }
}

