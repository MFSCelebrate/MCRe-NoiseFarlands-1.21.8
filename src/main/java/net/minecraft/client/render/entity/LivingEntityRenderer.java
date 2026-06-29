/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends EntityRenderer<T, S>
implements FeatureRendererContext<S, M> {
    final static private float field_32939 = 0.1f;
    protected M model;
    final protected ItemModelManager itemModelResolver;
    final protected List<FeatureRenderer<S, M>> features = Lists.newArrayList();

    public LivingEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx);
        this.itemModelResolver = ctx.getItemModelManager();
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    protected final boolean addFeature(FeatureRenderer<S, M> feature) {
        return this.features.add(feature);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    protected Box getBoundingBox(T livingEntity) {
        Box box = super.getBoundingBox(livingEntity);
        if (((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
            float f = 0.5f;
            return box.expand(0.5, 0.5, 0.5);
        }
        return box;
    }

    @Override
    public void render(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Direction direction;
        matrixStack.push();
        if (((LivingEntityRenderState)livingEntityRenderState).isInPose(EntityPose.SLEEPING) && (direction = ((LivingEntityRenderState)livingEntityRenderState).sleepingDirection) != null) {
            float f = ((LivingEntityRenderState)livingEntityRenderState).standingEyeHeight - 0.1f;
            matrixStack.translate((float)(-direction.getOffsetX()) * f, 0.0f, (float)(-direction.getOffsetZ()) * f);
        }
        float g = ((LivingEntityRenderState)livingEntityRenderState).baseScale;
        matrixStack.scale(g, g, g);
        this.setupTransforms(livingEntityRenderState, matrixStack, ((LivingEntityRenderState)livingEntityRenderState).bodyYaw, g);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.scale(livingEntityRenderState, matrixStack);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        ((EntityModel)this.model).setAngles(livingEntityRenderState);
        boolean bl = this.isVisible(livingEntityRenderState);
        boolean bl2 = !bl && !((LivingEntityRenderState)livingEntityRenderState).invisibleToPlayer;
        RenderLayer renderLayer = this.getRenderLayer(livingEntityRenderState, bl, bl2, ((LivingEntityRenderState)livingEntityRenderState).hasOutline);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            int j = LivingEntityRenderer.getOverlay(livingEntityRenderState, this.getAnimationCounter(livingEntityRenderState));
            int k = bl2 ? 0x26FFFFFF : Colors.WHITE;
            int l = ColorHelper.mix(k, this.getMixColor(livingEntityRenderState));
            ((Model)this.model).render(matrixStack, vertexConsumer, 1, j, l);
        }
        if (this.shouldRenderFeatures(livingEntityRenderState)) {
            for (FeatureRenderer<S, M> featureRenderer : this.features) {
                featureRenderer.render(matrixStack, vertexConsumerProvider, 1, livingEntityRenderState, ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw, ((LivingEntityRenderState)livingEntityRenderState).pitch);
            }
        }
        matrixStack.pop();
        super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, 1);
    }

    protected boolean shouldRenderFeatures(S state) {
        return true;
    }

    protected int getMixColor(S state) {
        return -1;
    }

    public abstract Identifier getTexture(S var1);

    @Nullable
    protected RenderLayer getRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier identifier = this.getTexture(state);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        }
        if (showBody) {
            return ((Model)this.model).getLayer(identifier);
        }
        if (showOutline) {
            return RenderLayer.getOutline(identifier);
        }
        return null;
    }

    public static int getOverlay(LivingEntityRenderState state, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(state.hurt));
    }

    protected boolean isVisible(S state) {
        return !((LivingEntityRenderState)state).invisible;
    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean isShaking(S state) {
        return ((LivingEntityRenderState)state).shaking;
    }

    protected void setupTransforms(S state, MatrixStack matrices, float bodyYaw, float baseHeight) {
        if (this.isShaking(state)) {
            bodyYaw += (float)(Math.cos((float)MathHelper.floor(((LivingEntityRenderState)state).age) * 3.25f) * Math.PI * (double)0.4f);
        }
        if (!((LivingEntityRenderState)state).isInPose(EntityPose.SLEEPING)) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        }
        if (((LivingEntityRenderState)state).deathTime > 0.0f) {
            float f = (((LivingEntityRenderState)state).deathTime - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f * this.getLyingPositionRotationDegrees()));
        } else if (((LivingEntityRenderState)state).usingRiptide) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - ((LivingEntityRenderState)state).pitch));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(((LivingEntityRenderState)state).age * -75.0f));
        } else if (((LivingEntityRenderState)state).isInPose(EntityPose.SLEEPING)) {
            Direction direction = ((LivingEntityRenderState)state).sleepingDirection;
            float g = direction != null ? LivingEntityRenderer.getYaw(direction) : bodyYaw;
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(this.getLyingPositionRotationDegrees()));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        } else if (((LivingEntityRenderState)state).flipUpsideDown) {
            matrices.translate(0.0f, (((LivingEntityRenderState)state).height + 0.1f) / baseHeight, 0.0f);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        }
    }

    protected float getLyingPositionRotationDegrees() {
        return 90.0f;
    }

    protected float getAnimationCounter(S state) {
        return 0.0f;
    }

    protected void scale(S state, MatrixStack matrices) {
    }

    @Override
    protected boolean hasLabel(T livingEntity, double d) {
        boolean bl;
        if (((Entity)livingEntity).isSneaky()) {
            float f = 32.0f;
            if (d >= 1024.0) {
                return false;
            }
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        boolean bl2 = bl = !((Entity)livingEntity).isInvisibleTo(clientPlayerEntity);
        if (livingEntity != clientPlayerEntity) {
            Team abstractTeam = ((Entity)livingEntity).getScoreboardTeam();
            Team abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
            if (abstractTeam != null) {
                AbstractTeam.VisibilityRule visibilityRule = ((AbstractTeam)abstractTeam).getNameTagVisibilityRule();
                switch (visibilityRule) {
                    case ALWAYS: {
                        return bl;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (((AbstractTeam)abstractTeam).shouldShowFriendlyInvisibles() || bl);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
                    }
                }
                return true;
            }
        }
        return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !((Entity)livingEntity).hasPassengers();
    }

    public static boolean shouldFlipUpsideDown(LivingEntity entity) {
        String string;
        if ((entity instanceof PlayerEntity || entity.hasCustomName()) && ("Dinnerbone".equals(string = Formatting.strip(entity.getName().getString())) || "Grumm".equals(string))) {
            PlayerEntity playerEntity;
            return !(entity instanceof PlayerEntity) || (playerEntity = (PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE);
        }
        return false;
    }

    @Override
    protected float getShadowRadius(S livingEntityRenderState) {
        return super.getShadowRadius(livingEntityRenderState) * ((LivingEntityRenderState)livingEntityRenderState).baseScale;
    }

    @Override
    public void updateRenderState(T livingEntity, S livingEntityRenderState, float f) {
        BlockItem blockItem;
        super.updateRenderState(livingEntity, livingEntityRenderState, f);
        float g = MathHelper.lerpAngleDegrees(f, ((LivingEntity)livingEntity).lastHeadYaw, ((LivingEntity)livingEntity).headYaw);
        ((LivingEntityRenderState)livingEntityRenderState).bodyYaw = LivingEntityRenderer.clampBodyYaw(livingEntity, g, f);
        ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw = MathHelper.wrapDegrees(g - ((LivingEntityRenderState)livingEntityRenderState).bodyYaw);
        ((LivingEntityRenderState)livingEntityRenderState).pitch = ((Entity)livingEntity).getLerpedPitch(f);
        ((LivingEntityRenderState)livingEntityRenderState).customName = ((Entity)livingEntity).getCustomName();
        ((LivingEntityRenderState)livingEntityRenderState).flipUpsideDown = LivingEntityRenderer.shouldFlipUpsideDown(livingEntity);
        if (((LivingEntityRenderState)livingEntityRenderState).flipUpsideDown) {
            ((LivingEntityRenderState)livingEntityRenderState).pitch *= -1.0f;
            ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw *= -1.0f;
        }
        if (!((Entity)livingEntity).hasVehicle() && ((LivingEntity)livingEntity).isAlive()) {
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress = ((LivingEntity)livingEntity).limbAnimator.getAnimationProgress(f);
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude = ((LivingEntity)livingEntity).limbAnimator.getAmplitude(f);
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress = 0.0f;
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude = 0.0f;
        }
        Entity entity = ((Entity)livingEntity).getVehicle();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            ((LivingEntityRenderState)livingEntityRenderState).headItemAnimationProgress = livingEntity2.limbAnimator.getAnimationProgress(f);
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).headItemAnimationProgress = ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress;
        }
        ((LivingEntityRenderState)livingEntityRenderState).baseScale = ((LivingEntity)livingEntity).getScale();
        ((LivingEntityRenderState)livingEntityRenderState).ageScale = ((LivingEntity)livingEntity).getScaleFactor();
        ((LivingEntityRenderState)livingEntityRenderState).pose = ((Entity)livingEntity).getPose();
        ((LivingEntityRenderState)livingEntityRenderState).sleepingDirection = ((LivingEntity)livingEntity).getSleepingDirection();
        if (((LivingEntityRenderState)livingEntityRenderState).sleepingDirection != null) {
            ((LivingEntityRenderState)livingEntityRenderState).standingEyeHeight = ((Entity)livingEntity).getEyeHeight(EntityPose.STANDING);
        }
        ((LivingEntityRenderState)livingEntityRenderState).shaking = ((Entity)livingEntity).isFrozen();
        ((LivingEntityRenderState)livingEntityRenderState).baby = ((LivingEntity)livingEntity).isBaby();
        ((LivingEntityRenderState)livingEntityRenderState).touchingWater = ((Entity)livingEntity).isTouchingWater();
        ((LivingEntityRenderState)livingEntityRenderState).usingRiptide = ((LivingEntity)livingEntity).isUsingRiptide();
        ((LivingEntityRenderState)livingEntityRenderState).hurt = ((LivingEntity)livingEntity).hurtTime > 0 || ((LivingEntity)livingEntity).deathTime > 0;
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.HEAD);
        ItemConvertible itemConvertible = itemStack.getItem();
        if (itemConvertible instanceof BlockItem && (itemConvertible = (blockItem = (BlockItem)itemConvertible).getBlock()) instanceof AbstractSkullBlock) {
            AbstractSkullBlock abstractSkullBlock = (AbstractSkullBlock)itemConvertible;
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType = abstractSkullBlock.getSkullType();
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullProfile = itemStack.get(DataComponentTypes.PROFILE);
            ((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.clear();
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType = null;
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullProfile = null;
            if (!ArmorFeatureRenderer.hasModel(itemStack, EquipmentSlot.HEAD)) {
                this.itemModelResolver.updateForLivingEntity(((LivingEntityRenderState)livingEntityRenderState).headItemRenderState, itemStack, ItemDisplayContext.HEAD, (LivingEntity)livingEntity);
            } else {
                ((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.clear();
            }
        }
        ((LivingEntityRenderState)livingEntityRenderState).deathTime = ((LivingEntity)livingEntity).deathTime > 0 ? (float)((LivingEntity)livingEntity).deathTime + f : 0.0f;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ((LivingEntityRenderState)livingEntityRenderState).invisibleToPlayer = ((LivingEntityRenderState)livingEntityRenderState).invisible && ((Entity)livingEntity).isInvisibleTo(minecraftClient.player);
        ((LivingEntityRenderState)livingEntityRenderState).hasOutline = minecraftClient.hasOutline((Entity)livingEntity);
    }

    @Override
    protected void appendHitboxes(T livingEntity, ImmutableList.Builder<EntityHitbox> builder, float f) {
        Box box = ((Entity)livingEntity).getBoundingBox();
        float g = 0.01f;
        EntityHitbox entityHitbox = new EntityHitbox(box.minX - ((Entity)livingEntity).getX(), ((Entity)livingEntity).getStandingEyeHeight() - 0.01f, box.minZ - ((Entity)livingEntity).getZ(), box.maxX - ((Entity)livingEntity).getX(), ((Entity)livingEntity).getStandingEyeHeight() + 0.01f, box.maxZ - ((Entity)livingEntity).getZ(), 1.0f, 0.0f, 0.0f);
        builder.add((Object)entityHitbox);
    }

    private static float clampBodyYaw(LivingEntity entity, float degrees, float tickProgress) {
        Entity entity2 = entity.getVehicle();
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            float f = MathHelper.lerpAngleDegrees(tickProgress, livingEntity.lastBodyYaw, livingEntity.bodyYaw);
            float g = 85.0f;
            float h = MathHelper.clamp(MathHelper.wrapDegrees(degrees - f), -85.0f, 85.0f);
            f = degrees - h;
            if (Math.abs(h) > 50.0f) {
                f += h * 0.2f;
            }
            return f;
        }
        return MathHelper.lerpAngleDegrees(tickProgress, entity.lastBodyYaw, entity.bodyYaw);
    }

    @Override
    protected float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((S)((LivingEntityRenderState)state));
    }
}

