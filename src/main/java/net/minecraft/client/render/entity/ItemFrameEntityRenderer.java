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
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemFrameEntityRenderState;
import net.minecraft.client.render.model.BlockStateManagers;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderer<T extends ItemFrameEntity>
extends EntityRenderer<T, ItemFrameEntityRenderState> {
    final static public int GLOW_FRAME_BLOCK_LIGHT = 5;
    final static public int field_32933 = 30;
    final private ItemModelManager itemModelManager;
    final private MapRenderer mapRenderer;
    final private BlockRenderManager blockRenderManager;

    public ItemFrameEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
        this.mapRenderer = context.getMapRenderer();
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    protected int getBlockLight(T itemFrameEntity, BlockPos blockPos) {
        if (((Entity)itemFrameEntity).getType() == EntityType.GLOW_ITEM_FRAME) {
            return Math.max(5, super.getBlockLight(itemFrameEntity, blockPos));
        }
        return super.getBlockLight(itemFrameEntity, blockPos);
    }

    @Override
    public void render(ItemFrameEntityRenderState itemFrameEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float g;
        float f;
        super.render(itemFrameEntityRenderState, matrixStack, vertexConsumerProvider, 1);
        matrixStack.push();
        Direction direction = itemFrameEntityRenderState.facing;
        Vec3d vec3d = this.getPositionOffset(itemFrameEntityRenderState);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        double d = 0.46875;
        matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
        if (direction.getAxis().isHorizontal()) {
            f = 0.0f;
            g = 180.0f - direction.getPositiveHorizontalDegrees();
        } else {
            f = -90 * direction.getDirection().offset();
            g = 180.0f;
        }
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(g));
        if (!itemFrameEntityRenderState.invisible) {
            BlockState blockState = BlockStateManagers.getStateForItemFrame(itemFrameEntityRenderState.glow, itemFrameEntityRenderState.mapId != null);
            BlockStateModel blockStateModel = this.blockRenderManager.getModel(blockState);
            matrixStack.push();
            matrixStack.translate(-0.5f, -0.5f, -0.5f);
            BlockModelRenderer.render(matrixStack.peek(), vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolidZOffsetForward(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)), blockStateModel, 1.0f, 1.0f, 1.0f, 1, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        }
        if (itemFrameEntityRenderState.invisible) {
            matrixStack.translate(0.0f, 0.0f, 0.5f);
        } else {
            matrixStack.translate(0.0f, 0.0f, 0.4375f);
        }
        if (itemFrameEntityRenderState.mapId != null) {
            int j = itemFrameEntityRenderState.rotation % 4 * 2;
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)j * 360.0f / 8.0f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            float h = 0.0078125f;
            matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
            matrixStack.translate(-64.0f, -64.0f, 0.0f);
            matrixStack.translate(0.0f, 0.0f, -1.0f);
            int k = this.getLight(itemFrameEntityRenderState.glow, 15728850, 1);
            this.mapRenderer.draw(itemFrameEntityRenderState.mapRenderState, matrixStack, vertexConsumerProvider, true, k);
        } else if (!itemFrameEntityRenderState.itemRenderState.isEmpty()) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)itemFrameEntityRenderState.rotation * 360.0f / 8.0f));
            int j = this.getLight(itemFrameEntityRenderState.glow, 0xF000F0, 1);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            itemFrameEntityRenderState.itemRenderState.render(matrixStack, vertexConsumerProvider, j, OverlayTexture.DEFAULT_UV);
        }
        matrixStack.pop();
    }

    private int getLight(boolean glow, int glowLight, int regularLight) {
        return glow ? glowLight : regularLight;
    }

    @Override
    public Vec3d getPositionOffset(ItemFrameEntityRenderState itemFrameEntityRenderState) {
        return new Vec3d((float)itemFrameEntityRenderState.facing.getOffsetX() * 0.3f, -0.25, (float)itemFrameEntityRenderState.facing.getOffsetZ() * 0.3f);
    }

    @Override
    protected boolean hasLabel(T itemFrameEntity, double d) {
        return MinecraftClient.isHudEnabled() && this.dispatcher.targetedEntity == itemFrameEntity && ((ItemFrameEntity)itemFrameEntity).getHeldItemStack().getCustomName() != null;
    }

    @Override
    protected Text getDisplayName(T itemFrameEntity) {
        return ((ItemFrameEntity)itemFrameEntity).getHeldItemStack().getName();
    }

    @Override
    public ItemFrameEntityRenderState net_minecraft_client_render_entity_state_ItemFrameEntityRenderState_createRenderState() {
        return new ItemFrameEntityRenderState();
    }

    @Override
    public void updateRenderState(T itemFrameEntity, ItemFrameEntityRenderState itemFrameEntityRenderState, float f) {
        MapState mapState;
        MapIdComponent mapIdComponent;
        super.updateRenderState(itemFrameEntity, itemFrameEntityRenderState, f);
        itemFrameEntityRenderState.facing = ((AbstractDecorationEntity)itemFrameEntity).getHorizontalFacing();
        ItemStack itemStack = ((ItemFrameEntity)itemFrameEntity).getHeldItemStack();
        this.itemModelManager.updateForNonLivingEntity(itemFrameEntityRenderState.itemRenderState, itemStack, ItemDisplayContext.FIXED, (Entity)itemFrameEntity);
        itemFrameEntityRenderState.rotation = ((ItemFrameEntity)itemFrameEntity).getRotation();
        itemFrameEntityRenderState.glow = ((Entity)itemFrameEntity).getType() == EntityType.GLOW_ITEM_FRAME;
        itemFrameEntityRenderState.mapId = null;
        if (!itemStack.isEmpty() && (mapIdComponent = ((ItemFrameEntity)itemFrameEntity).getMapId(itemStack)) != null && (mapState = ((Entity)itemFrameEntity).net_minecraft_world_World_getWorld().getMapState(mapIdComponent)) != null) {
            this.mapRenderer.update(mapIdComponent, mapState, itemFrameEntityRenderState.mapRenderState);
            itemFrameEntityRenderState.mapId = mapIdComponent;
        }
    }

    @Override
    public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
        return this.net_minecraft_client_render_entity_state_ItemFrameEntityRenderState_createRenderState();
    }

    @Override
    protected Text getDisplayName(Entity entity) {
        return this.getDisplayName((T)((ItemFrameEntity)entity));
    }
}

