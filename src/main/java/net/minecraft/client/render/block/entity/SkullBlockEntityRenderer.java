/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PiglinHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SkullBlockEntityRenderer
implements BlockEntityRenderer<SkullBlockEntity> {
    final private Function<SkullBlock.SkullType, SkullBlockEntityModel> models;
    final static private Map<SkullBlock.SkullType, Identifier> TEXTURES = Util.make(Maps.newHashMap(), map -> {
        map.put(SkullBlock.Type.SKELETON, Identifier.ofVanilla("textures/entity/skeleton/skeleton.png"));
        map.put(SkullBlock.Type.WITHER_SKELETON, Identifier.ofVanilla("textures/entity/skeleton/wither_skeleton.png"));
        map.put(SkullBlock.Type.ZOMBIE, Identifier.ofVanilla("textures/entity/zombie/zombie.png"));
        map.put(SkullBlock.Type.CREEPER, Identifier.ofVanilla("textures/entity/creeper/creeper.png"));
        map.put(SkullBlock.Type.DRAGON, Identifier.ofVanilla("textures/entity/enderdragon/dragon.png"));
        map.put(SkullBlock.Type.PIGLIN, Identifier.ofVanilla("textures/entity/piglin/piglin.png"));
        map.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
    });

    @Nullable
    public static SkullBlockEntityModel getModels(LoadedEntityModels models, SkullBlock.SkullType type) {
        if (type instanceof SkullBlock.Type) {
            SkullBlock.Type type2 = (SkullBlock.Type)type;
            return switch (type2) {
                default -> throw new MatchException(null, null);
                case SkullBlock.Type.SKELETON -> new SkullEntityModel(models.getModelPart(EntityModelLayers.SKELETON_SKULL));
                case SkullBlock.Type.WITHER_SKELETON -> new SkullEntityModel(models.getModelPart(EntityModelLayers.WITHER_SKELETON_SKULL));
                case SkullBlock.Type.PLAYER -> new SkullEntityModel(models.getModelPart(EntityModelLayers.PLAYER_HEAD));
                case SkullBlock.Type.ZOMBIE -> new SkullEntityModel(models.getModelPart(EntityModelLayers.ZOMBIE_HEAD));
                case SkullBlock.Type.CREEPER -> new SkullEntityModel(models.getModelPart(EntityModelLayers.CREEPER_HEAD));
                case SkullBlock.Type.DRAGON -> new DragonHeadEntityModel(models.getModelPart(EntityModelLayers.DRAGON_SKULL));
                case SkullBlock.Type.PIGLIN -> new PiglinHeadEntityModel(models.getModelPart(EntityModelLayers.PIGLIN_HEAD));
            };
        }
        return null;
    }

    public SkullBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        LoadedEntityModels loadedEntityModels = context.getLoadedEntityModels();
        this.models = Util.memoize(type -> SkullBlockEntityRenderer.getModels(loadedEntityModels, type));
    }

    @Override
    public void render(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, Vec3d vec3d) {
        float g = skullBlockEntity.getPoweredTicks(f);
        BlockState blockState = skullBlockEntity.getCachedState();
        boolean bl = blockState.getBlock() instanceof WallSkullBlock;
        Direction direction = bl ? blockState.get(WallSkullBlock.FACING) : null;
        int k = bl ? RotationPropertyHelper.fromDirection(direction.getOpposite()) : blockState.get(SkullBlock.ROTATION);
        float h = RotationPropertyHelper.toDegrees(k);
        SkullBlock.SkullType skullType = ((AbstractSkullBlock)blockState.getBlock()).getSkullType();
        SkullBlockEntityModel skullBlockEntityModel = this.models.apply(skullType);
        RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, skullBlockEntity.getOwner());
        SkullBlockEntityRenderer.renderSkull(direction, h, g, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
    }

    public static void renderSkull(@Nullable Direction direction, float yaw, float animationProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, SkullBlockEntityModel model, RenderLayer renderLayer) {
        matrices.push();
        if (direction == null) {
            matrices.translate(0.5f, 0.0f, 0.5f);
        } else {
            float f = 0.25f;
            matrices.translate(0.5f - (float)direction.getOffsetX() * 0.25f, 0.25f, 0.5f - (float)direction.getOffsetZ() * 0.25f);
        }
        matrices.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        model.setHeadRotation(animationProgress, yaw, 0.0f);
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    public static RenderLayer getRenderLayer(SkullBlock.SkullType type, @Nullable ProfileComponent profile) {
        if (type != SkullBlock.Type.PLAYER || profile == null) {
            return SkullBlockEntityRenderer.getCutoutRenderLayer(type, null);
        }
        return SkullBlockEntityRenderer.getTranslucentRenderLayer(MinecraftClient.getInstance().getSkinProvider().getSkinTextures(profile.gameProfile()).texture());
    }

    public static RenderLayer getCutoutRenderLayer(SkullBlock.SkullType type, @Nullable Identifier texture) {
        return RenderLayer.getEntityCutoutNoCullZOffset(texture != null ? texture : TEXTURES.get(type));
    }

    public static RenderLayer getTranslucentRenderLayer(Identifier texture) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}

