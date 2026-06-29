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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityRenderer
extends MobEntityRenderer<ShulkerEntity, ShulkerEntityRenderState, ShulkerEntityModel> {
    final static private Identifier TEXTURE = TexturedRenderLayers.SHULKER_TEXTURE_ID.getTextureId().withPath(string -> "textures/" + string + ".png");
    final static private Identifier[] COLORED_TEXTURES = (Identifier[])TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.stream().map(spriteId -> spriteId.getTextureId().withPath(string -> "textures/" + string + ".png")).toArray(Identifier[]::new);

    public ShulkerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ShulkerEntityModel(context.getPart(EntityModelLayers.SHULKER)), 0.0f);
    }

    @Override
    public Vec3d getPositionOffset(ShulkerEntityRenderState shulkerEntityRenderState) {
        return shulkerEntityRenderState.renderPositionOffset;
    }

    @Override
    public boolean shouldRender(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
        if (super.shouldRender(shulkerEntity, frustum, d, e, f)) {
            return true;
        }
        Vec3d vec3d = shulkerEntity.getRenderPositionOffset(0.0f);
        if (vec3d == null) {
            return false;
        }
        EntityType<?> entityType = shulkerEntity.getType();
        float g = entityType.getHeight() / 2.0f;
        float h = entityType.getWidth() / 2.0f;
        Vec3d vec3d2 = Vec3d.ofBottomCenter(shulkerEntity.getBlockPos());
        return frustum.isVisible(new Box(vec3d.x, vec3d.y + (double)g, vec3d.z, vec3d2.x, vec3d2.y + (double)g, vec3d2.z).expand(h, g, h));
    }

    @Override
    public Identifier getTexture(ShulkerEntityRenderState shulkerEntityRenderState) {
        return ShulkerEntityRenderer.getTexture(shulkerEntityRenderState.color);
    }

    @Override
    public ShulkerEntityRenderState net_minecraft_client_render_entity_state_ShulkerEntityRenderState_createRenderState() {
        return new ShulkerEntityRenderState();
    }

    @Override
    public void updateRenderState(ShulkerEntity shulkerEntity, ShulkerEntityRenderState shulkerEntityRenderState, float f) {
        super.updateRenderState(shulkerEntity, shulkerEntityRenderState, f);
        shulkerEntityRenderState.renderPositionOffset = Objects.requireNonNullElse(shulkerEntity.getRenderPositionOffset(f), Vec3d.ZERO);
        shulkerEntityRenderState.color = shulkerEntity.getColor();
        shulkerEntityRenderState.openProgress = shulkerEntity.getOpenProgress(f);
        shulkerEntityRenderState.headYaw = shulkerEntity.headYaw;
        shulkerEntityRenderState.shellYaw = shulkerEntity.bodyYaw;
        shulkerEntityRenderState.facing = shulkerEntity.getAttachedFace();
    }

    public static Identifier getTexture(@Nullable DyeColor shulkerColor) {
        if (shulkerColor == null) {
            return TEXTURE;
        }
        return COLORED_TEXTURES[shulkerColor.getIndex()];
    }

    @Override
    protected void setupTransforms(ShulkerEntityRenderState shulkerEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(shulkerEntityRenderState, matrixStack, f + 180.0f, g);
        matrixStack.multiply((Quaternionfc)shulkerEntityRenderState.facing.getOpposite().getRotationQuaternion(), 0.0f, 0.5f, 0.0f);
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ShulkerEntityRenderState_createRenderState();
    }
}

