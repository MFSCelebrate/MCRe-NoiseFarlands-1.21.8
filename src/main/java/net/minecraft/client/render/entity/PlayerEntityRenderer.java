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

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
    public PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim) {
        super(ctx, new PlayerEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new ArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)), ctx.getEquipmentRenderer()));
        this.addFeature(new PlayerHeldItemFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<PlayerEntityModel>(this, ctx));
        this.addFeature(new Deadmau5FeatureRenderer(this, ctx.getEntityModels()));
        this.addFeature(new CapeFeatureRenderer(this, ctx.getEntityModels(), ctx.getEquipmentModelLoader()));
        this.addFeature(new HeadFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel>(this, ctx.getEntityModels()));
        this.addFeature(new ElytraFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel>(this, ctx.getEntityModels(), ctx.getEquipmentRenderer()));
        this.addFeature(new ShoulderParrotFeatureRenderer(this, ctx.getEntityModels()));
        this.addFeature(new TridentRiptideFeatureRenderer(this, ctx.getEntityModels()));
        this.addFeature(new StuckStingersFeatureRenderer<PlayerEntityModel>(this, ctx));
    }

    @Override
    protected boolean shouldRenderFeatures(PlayerEntityRenderState playerEntityRenderState) {
        return !playerEntityRenderState.spectator;
    }

    @Override
    public Vec3d getPositionOffset(PlayerEntityRenderState playerEntityRenderState) {
        Vec3d vec3d = super.getPositionOffset(playerEntityRenderState);
        if (playerEntityRenderState.isInSneakingPose) {
            return vec3d.add(0.0, (double)(playerEntityRenderState.baseScale * -2.0f) / 16.0, 0.0);
        }
        return vec3d;
    }

    private static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Arm arm) {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack itemStack2 = player.getStackInHand(Hand.OFF_HAND);
        BipedEntityModel.ArmPose armPose = PlayerEntityRenderer.getArmPose(player, itemStack, Hand.MAIN_HAND);
        BipedEntityModel.ArmPose armPose2 = PlayerEntityRenderer.getArmPose(player, itemStack2, Hand.OFF_HAND);
        if (armPose.isTwoHanded()) {
            BipedEntityModel.ArmPose armPose3 = armPose2 = itemStack2.isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
        }
        if (player.getMainArm() == arm) {
            return armPose;
        }
        return armPose2;
    }

    private static BipedEntityModel.ArmPose getArmPose(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        }
        if (!player.handSwinging && stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(stack)) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
            UseAction useAction = stack.getUseAction();
            if (useAction == UseAction.BLOCK) {
                return BipedEntityModel.ArmPose.BLOCK;
            }
            if (useAction == UseAction.BOW) {
                return BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAction == UseAction.SPEAR) {
                return BipedEntityModel.ArmPose.THROW_SPEAR;
            }
            if (useAction == UseAction.CROSSBOW) {
                return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useAction == UseAction.SPYGLASS) {
                return BipedEntityModel.ArmPose.SPYGLASS;
            }
            if (useAction == UseAction.TOOT_HORN) {
                return BipedEntityModel.ArmPose.TOOT_HORN;
            }
            if (useAction == UseAction.BRUSH) {
                return BipedEntityModel.ArmPose.BRUSH;
            }
        }
        return BipedEntityModel.ArmPose.ITEM;
    }

    @Override
    public Identifier getTexture(PlayerEntityRenderState playerEntityRenderState) {
        return playerEntityRenderState.skinTextures.texture();
    }

    @Override
    protected void scale(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack) {
        float f = 0.9375f;
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderLabelIfPresent(PlayerEntityRenderState playerEntityRenderState, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        if (playerEntityRenderState.playerName != null) {
            super.renderLabelIfPresent(playerEntityRenderState, playerEntityRenderState.playerName, matrixStack, vertexConsumerProvider, i);
            Objects.requireNonNull(this.getTextRenderer());
            matrixStack.translate(0.0f, 0.25875f, 0.0f);
        }
        super.renderLabelIfPresent(playerEntityRenderState, text, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    public PlayerEntityRenderState net_minecraft_client_render_entity_state_PlayerEntityRenderState_createRenderState() {
        return new PlayerEntityRenderState();
    }

    @Override
    public void updateRenderState(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f) {
        ItemStack itemStack;
        super.updateRenderState(abstractClientPlayerEntity, playerEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState(abstractClientPlayerEntity, playerEntityRenderState, f, this.itemModelResolver);
        playerEntityRenderState.leftArmPose = PlayerEntityRenderer.getArmPose(abstractClientPlayerEntity, Arm.LEFT);
        playerEntityRenderState.rightArmPose = PlayerEntityRenderer.getArmPose(abstractClientPlayerEntity, Arm.RIGHT);
        playerEntityRenderState.skinTextures = abstractClientPlayerEntity.getSkinTextures();
        playerEntityRenderState.stuckArrowCount = abstractClientPlayerEntity.getStuckArrowCount();
        playerEntityRenderState.stingerCount = abstractClientPlayerEntity.getStingerCount();
        playerEntityRenderState.itemUseTimeLeft = abstractClientPlayerEntity.getItemUseTimeLeft();
        playerEntityRenderState.handSwinging = abstractClientPlayerEntity.handSwinging;
        playerEntityRenderState.spectator = abstractClientPlayerEntity.isSpectator();
        playerEntityRenderState.hatVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.HAT);
        playerEntityRenderState.jacketVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.JACKET);
        playerEntityRenderState.leftPantsLegVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
        playerEntityRenderState.rightPantsLegVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
        playerEntityRenderState.leftSleeveVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
        playerEntityRenderState.rightSleeveVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
        playerEntityRenderState.capeVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE);
        PlayerEntityRenderer.updateGliding(abstractClientPlayerEntity, playerEntityRenderState, f);
        PlayerEntityRenderer.updateCape(abstractClientPlayerEntity, playerEntityRenderState, f);
        if (playerEntityRenderState.squaredDistanceToCamera < 100.0) {
            Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
            ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
            if (scoreboardObjective != null) {
                ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(abstractClientPlayerEntity, scoreboardObjective);
                MutableText text = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, scoreboardObjective.getNumberFormatOr(StyledNumberFormat.EMPTY));
                playerEntityRenderState.playerName = Text.empty().append(text).append(ScreenTexts.SPACE).append(scoreboardObjective.getDisplayName());
            } else {
                playerEntityRenderState.playerName = null;
            }
        } else {
            playerEntityRenderState.playerName = null;
        }
        playerEntityRenderState.leftShoulderParrotVariant = PlayerEntityRenderer.getShoulderParrotVariant(abstractClientPlayerEntity, true);
        playerEntityRenderState.rightShoulderParrotVariant = PlayerEntityRenderer.getShoulderParrotVariant(abstractClientPlayerEntity, false);
        playerEntityRenderState.id = abstractClientPlayerEntity.getId();
        playerEntityRenderState.name = abstractClientPlayerEntity.getGameProfile().getName();
        playerEntityRenderState.spyglassState.clear();
        if (playerEntityRenderState.isUsingItem && (itemStack = abstractClientPlayerEntity.getStackInHand(playerEntityRenderState.activeHand)).isOf(Items.SPYGLASS)) {
            this.itemModelResolver.updateForLivingEntity(playerEntityRenderState.spyglassState, itemStack, ItemDisplayContext.HEAD, abstractClientPlayerEntity);
        }
    }

    private static void updateGliding(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickProgress) {
        state.glidingTicks = (float)player.getGlidingTicks() + tickProgress;
        Vec3d vec3d = player.getRotationVec(tickProgress);
        Vec3d vec3d2 = player.lerpVelocity(tickProgress);
        if (vec3d2.horizontalLengthSquared() > (double)1.0E-5f && vec3d.horizontalLengthSquared() > (double)1.0E-5f) {
            state.applyFlyingRotation = true;
            double d = vec3d2.getHorizontal().normalize().dotProduct(vec3d.getHorizontal().normalize());
            double e = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
            state.flyingRotation = (float)(Math.signum(e) * Math.acos(Math.min(1.0, Math.abs(d))));
        } else {
            state.applyFlyingRotation = false;
            state.flyingRotation = 0.0f;
        }
    }

    private static void updateCape(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickProgress) {
        double d = MathHelper.lerp((double)tickProgress, player.lastCapeX, player.capeX) - MathHelper.lerp((double)tickProgress, player.lastX, player.getX());
        double e = MathHelper.lerp((double)tickProgress, player.lastCapeY, player.capeY) - MathHelper.lerp((double)tickProgress, player.lastY, player.getY());
        double f = MathHelper.lerp((double)tickProgress, player.lastCapeZ, player.capeZ) - MathHelper.lerp((double)tickProgress, player.lastZ, player.getZ());
        float g = MathHelper.lerpAngleDegrees(tickProgress, player.lastBodyYaw, player.bodyYaw);
        double h = MathHelper.sin(g * ((float)Math.PI / 180));
        double i = -MathHelper.cos(g * ((float)Math.PI / 180));
        state.field_53536 = (float)e * 10.0f;
        state.field_53536 = MathHelper.clamp(state.field_53536, -6.0f, 32.0f);
        state.field_53537 = (float)(d * h + f * i) * 100.0f;
        state.field_53537 *= 1.0f - state.getGlidingProgress();
        state.field_53537 = MathHelper.clamp(state.field_53537, 0.0f, 150.0f);
        state.field_53538 = (float)(d * i - f * h) * 100.0f;
        state.field_53538 = MathHelper.clamp(state.field_53538, -20.0f, 20.0f);
        float j = MathHelper.lerp(tickProgress, player.lastStrideDistance, player.strideDistance);
        float k = MathHelper.lerp(tickProgress, player.lastDistanceMoved, player.distanceMoved);
        state.field_53536 += MathHelper.sin(k * 6.0f) * 32.0f * j;
    }

    @Nullable
    private static ParrotEntity.Variant getShoulderParrotVariant(AbstractClientPlayerEntity player, boolean left) {
        NbtCompound nbtCompound;
        NbtCompound nbtCompound2 = nbtCompound = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        if (nbtCompound.isEmpty()) {
            return null;
        }
        EntityType entityType = nbtCompound.get("id", EntityType.CODEC).orElse(null);
        if (entityType == EntityType.PARROT) {
            return nbtCompound.get("Variant", ParrotEntity.Variant.INDEX_CODEC).orElse(ParrotEntity.Variant.RED_BLUE);
        }
        return null;
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture, boolean sleeveVisible) {
        this.renderArm(matrices, vertexConsumers, light, skinTexture, ((PlayerEntityModel)this.model).rightArm, sleeveVisible);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture, boolean sleeveVisible) {
        this.renderArm(matrices, vertexConsumers, light, skinTexture, ((PlayerEntityModel)this.model).leftArm, sleeveVisible);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture, ModelPart arm, boolean sleeveVisible) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        arm.resetTransform();
        arm.visible = true;
        playerEntityModel.leftSleeve.visible = sleeveVisible;
        playerEntityModel.rightSleeve.visible = sleeveVisible;
        playerEntityModel.leftArm.roll = -0.1f;
        playerEntityModel.rightArm.roll = 0.1f;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skinTexture)), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected void setupTransforms(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, float f, float g) {
        float h = playerEntityRenderState.leaningPitch;
        float i = playerEntityRenderState.pitch;
        if (playerEntityRenderState.isGliding) {
            super.setupTransforms(playerEntityRenderState, matrixStack, f, g);
            float j = playerEntityRenderState.getGlidingProgress();
            if (!playerEntityRenderState.usingRiptide) {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(j * (-90.0f - i)));
            }
            if (playerEntityRenderState.applyFlyingRotation) {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(playerEntityRenderState.flyingRotation));
            }
        } else if (h > 0.0f) {
            super.setupTransforms(playerEntityRenderState, matrixStack, f, g);
            float j = playerEntityRenderState.touchingWater ? -90.0f - i : -90.0f;
            float k = MathHelper.lerp(h, 0.0f, j);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(k));
            if (playerEntityRenderState.isSwimming) {
                matrixStack.translate(0.0f, -1.0f, 0.3f);
            }
        } else {
            super.setupTransforms(playerEntityRenderState, matrixStack, f, g);
        }
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PlayerEntityRenderState)state);
    }

    @Override
    protected boolean shouldRenderFeatures(LivingEntityRenderState state) {
        return this.shouldRenderFeatures((PlayerEntityRenderState)state);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_PlayerEntityRenderState_createRenderState();
    }

    @Override
    public Vec3d getPositionOffset(EntityRenderState state) {
        return this.getPositionOffset((PlayerEntityRenderState)state);
    }
}

