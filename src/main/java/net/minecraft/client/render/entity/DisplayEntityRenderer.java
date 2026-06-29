/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemDisplayEntityRenderState;
import net.minecraft.client.render.entity.state.TextDisplayEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class DisplayEntityRenderer<T extends DisplayEntity, S, ST extends DisplayEntityRenderState>
extends EntityRenderer<T, ST> {
    final private EntityRenderDispatcher renderDispatcher;

    protected DisplayEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.renderDispatcher = context.getRenderDispatcher();
    }

    @Override
    protected Box getBoundingBox(T displayEntity) {
        return ((DisplayEntity)displayEntity).getVisibilityBoundingBox();
    }

    @Override
    protected boolean canBeCulled(T displayEntity) {
        return ((DisplayEntity)displayEntity).shouldRender();
    }

    private static int getBrightnessOverride(DisplayEntity entity) {
        DisplayEntity.RenderState renderState = entity.getRenderState();
        return renderState != null ? renderState.brightnessOverride() : -1;
    }

    @Override
    protected int getSkyLight(T displayEntity, BlockPos blockPos) {
        int i = DisplayEntityRenderer.getBrightnessOverride(displayEntity);
        if (1 != -1) {
            return LightmapTextureManager.getSkyLightCoordinates(1);
        }
        return super.getSkyLight(displayEntity, blockPos);
    }

    @Override
    protected int getBlockLight(T displayEntity, BlockPos blockPos) {
        int i = DisplayEntityRenderer.getBrightnessOverride(displayEntity);
        if (1 != -1) {
            return LightmapTextureManager.getBlockLightCoordinates(1);
        }
        return super.getBlockLight(displayEntity, blockPos);
    }

    @Override
    protected float getShadowRadius(ST displayEntityRenderState) {
        DisplayEntity.RenderState renderState = ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState;
        if (renderState == null) {
            return 0.0f;
        }
        return renderState.shadowRadius().lerp(((DisplayEntityRenderState)displayEntityRenderState).lerpProgress);
    }

    @Override
    protected float getShadowOpacity(ST displayEntityRenderState) {
        DisplayEntity.RenderState renderState = ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState;
        if (renderState == null) {
            return 0.0f;
        }
        return renderState.shadowStrength().lerp(((DisplayEntityRenderState)displayEntityRenderState).lerpProgress);
    }

    @Override
    public void render(ST displayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        DisplayEntity.RenderState renderState = ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState;
        if (renderState == null || !((DisplayEntityRenderState)displayEntityRenderState).canRender()) {
            return;
        }
        float f = ((DisplayEntityRenderState)displayEntityRenderState).lerpProgress;
        super.render(displayEntityRenderState, matrixStack, vertexConsumerProvider, 1);
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)this.getBillboardRotation(renderState, displayEntityRenderState, new Quaternionf()));
        AffineTransformation affineTransformation = renderState.transformation().interpolate(f);
        matrixStack.multiplyPositionMatrix(affineTransformation.getMatrix());
        this.render(displayEntityRenderState, matrixStack, vertexConsumerProvider, 1, f);
        matrixStack.pop();
    }

    private Quaternionf getBillboardRotation(DisplayEntity.RenderState renderState, ST state, Quaternionf quaternionf) {
        Camera camera = this.renderDispatcher.camera;
        return switch (renderState.billboardConstraints()) {
            default -> throw new MatchException(null, null);
            case DisplayEntity.BillboardMode.FIXED -> quaternionf.rotationYXZ((float)(-Math.PI) / 180 * ((DisplayEntityRenderState)state).yaw, (float)Math.PI / 180 * ((DisplayEntityRenderState)state).pitch, 0.0f);
            case DisplayEntity.BillboardMode.HORIZONTAL -> quaternionf.rotationYXZ((float)(-Math.PI) / 180 * ((DisplayEntityRenderState)state).yaw, (float)Math.PI / 180 * DisplayEntityRenderer.getNegatedPitch(camera), 0.0f);
            case DisplayEntity.BillboardMode.VERTICAL -> quaternionf.rotationYXZ((float)(-Math.PI) / 180 * DisplayEntityRenderer.getBackwardsYaw(camera), (float)Math.PI / 180 * ((DisplayEntityRenderState)state).pitch, 0.0f);
            case DisplayEntity.BillboardMode.CENTER -> quaternionf.rotationYXZ((float)(-Math.PI) / 180 * DisplayEntityRenderer.getBackwardsYaw(camera), (float)Math.PI / 180 * DisplayEntityRenderer.getNegatedPitch(camera), 0.0f);
        };
    }

    private static float getBackwardsYaw(Camera camera) {
        return camera.getYaw() - 180.0f;
    }

    private static float getNegatedPitch(Camera camera) {
        return -camera.getPitch();
    }

    private static <T extends DisplayEntity> float lerpYaw(T entity, float deltaTicks) {
        return entity.getLerpedYaw(deltaTicks);
    }

    private static <T extends DisplayEntity> float lerpPitch(T entity, float deltaTicks) {
        return entity.getLerpedPitch(deltaTicks);
    }

    protected abstract void render(ST var1, MatrixStack var2, VertexConsumerProvider var3, int var4, float var5);

    @Override
    public void updateRenderState(T displayEntity, ST displayEntityRenderState, float f) {
        super.updateRenderState(displayEntity, displayEntityRenderState, f);
        ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState = ((DisplayEntity)displayEntity).getRenderState();
        ((DisplayEntityRenderState)displayEntityRenderState).lerpProgress = ((DisplayEntity)displayEntity).getLerpProgress(f);
        ((DisplayEntityRenderState)displayEntityRenderState).yaw = DisplayEntityRenderer.lerpYaw(displayEntity, f);
        ((DisplayEntityRenderState)displayEntityRenderState).pitch = DisplayEntityRenderer.lerpPitch(displayEntity, f);
    }

    @Override
    protected float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((ST)((DisplayEntityRenderState)state));
    }

    @Override
    protected int getBlockLight(Entity entity, BlockPos pos) {
        return this.getBlockLight((T)((DisplayEntity)entity), pos);
    }

    @Override
    protected int getSkyLight(Entity entity, BlockPos pos) {
        return this.getSkyLight((T)((DisplayEntity)entity), pos);
    }

    @Environment(value=EnvType.CLIENT)
    public static class TextDisplayEntityRenderer
    extends DisplayEntityRenderer<DisplayEntity.TextDisplayEntity, DisplayEntity.TextDisplayEntity.Data, TextDisplayEntityRenderState> {
        final private TextRenderer displayTextRenderer;

        protected TextDisplayEntityRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.displayTextRenderer = context.getTextRenderer();
        }

        @Override
        public TextDisplayEntityRenderState net_minecraft_client_render_entity_state_TextDisplayEntityRenderState_createRenderState() {
            return new TextDisplayEntityRenderState();
        }

        @Override
        public void updateRenderState(DisplayEntity.TextDisplayEntity textDisplayEntity, TextDisplayEntityRenderState textDisplayEntityRenderState, float f) {
            super.updateRenderState(textDisplayEntity, textDisplayEntityRenderState, f);
            textDisplayEntityRenderState.data = textDisplayEntity.getData();
            textDisplayEntityRenderState.textLines = textDisplayEntity.splitLines(this::getLines);
        }

        private DisplayEntity.TextDisplayEntity.TextLines getLines(Text text, int width) {
            List<OrderedText> list = this.displayTextRenderer.wrapLines(text, width);
            ArrayList<DisplayEntity.TextDisplayEntity.TextLine> list2 = new ArrayList<DisplayEntity.TextDisplayEntity.TextLine>(list.size());
            int i = 0;
            for (OrderedText orderedText : list) {
                int j = this.displayTextRenderer.getWidth(orderedText);
                i = Math.max(i, j);
                list2.add(new DisplayEntity.TextDisplayEntity.TextLine(orderedText, j));
            }
            return new DisplayEntity.TextDisplayEntity.TextLines(list2, i);
        }

        @Override
        public void render(TextDisplayEntityRenderState textDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
            int j;
            float g;
            DisplayEntity.TextDisplayEntity.Data data = textDisplayEntityRenderState.data;
            byte b = data.flags();
            boolean bl = (b & DisplayEntity.TextDisplayEntity.SEE_THROUGH_FLAG) != 0;
            boolean bl2 = (b & DisplayEntity.TextDisplayEntity.DEFAULT_BACKGROUND_FLAG) != 0;
            boolean bl3 = (b & DisplayEntity.TextDisplayEntity.SHADOW_FLAG) != 0;
            DisplayEntity.TextDisplayEntity.TextAlignment textAlignment = DisplayEntity.TextDisplayEntity.getAlignment(b);
            byte c = (byte)data.textOpacity().lerp(f);
            if (bl2) {
                g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
                j = (int)(g * 255.0f) << 24;
            } else {
                j = data.backgroundColor().lerp(f);
            }
            g = 0.0f;
            Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
            matrix4f.rotate((float)Math.PI, 0.0f, 1.0f, 0.0f);
            matrix4f.scale(-0.025f, -0.025f, -0.025f);
            DisplayEntity.TextDisplayEntity.TextLines textLines = textDisplayEntityRenderState.textLines;
            boolean k = true;
            int l = this.displayTextRenderer.fontHeight + 1;
            int m = textLines.width();
            int n = textLines.lines().size() * l - 1;
            matrix4f.translate(1.0f - (float)m / 2.0f, (float)(-n), 0.0f);
            if (j != 0) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(bl ? RenderLayer.getTextBackgroundSeeThrough() : RenderLayer.getTextBackground());
                vertexConsumer.vertex(matrix4f, -1.0f, -1.0f, 0.0f).color(j).light(1);
                vertexConsumer.vertex(matrix4f, -1.0f, (float)n, 0.0f).color(j).light(1);
                vertexConsumer.vertex(matrix4f, (float)m, (float)n, 0.0f).color(j).light(1);
                vertexConsumer.vertex(matrix4f, (float)m, -1.0f, 0.0f).color(j).light(1);
            }
            for (DisplayEntity.TextDisplayEntity.TextLine textLine : textLines.lines()) {
                float h = switch (textAlignment) {
                    default -> throw new MatchException(null, null);
                    case DisplayEntity.TextDisplayEntity.TextAlignment.LEFT -> 0.0f;
                    case DisplayEntity.TextDisplayEntity.TextAlignment.RIGHT -> m - textLine.width();
                    case DisplayEntity.TextDisplayEntity.TextAlignment.CENTER -> (float)m / 2.0f - (float)textLine.width() / 2.0f;
                };
                this.displayTextRenderer.draw(textLine.contents(), h, g, c << 24 | 0xFFFFFF, bl3, matrix4f, vertexConsumerProvider, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.POLYGON_OFFSET, 0, 1);
                g += (float)l;
            }
        }

        @Override
        public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
            return this.net_minecraft_client_render_entity_state_TextDisplayEntityRenderState_createRenderState();
        }

        @Override
        protected float getShadowRadius(EntityRenderState state) {
            return super.getShadowRadius((DisplayEntityRenderState)state);
        }

        @Override
        protected int getBlockLight(Entity entity, BlockPos pos) {
            return super.getBlockLight((DisplayEntity)entity, pos);
        }

        @Override
        protected int getSkyLight(Entity entity, BlockPos pos) {
            return super.getSkyLight((DisplayEntity)entity, pos);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ItemDisplayEntityRenderer
    extends DisplayEntityRenderer<DisplayEntity.ItemDisplayEntity, DisplayEntity.ItemDisplayEntity.Data, ItemDisplayEntityRenderState> {
        final private ItemModelManager itemModelManager;

        protected ItemDisplayEntityRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.itemModelManager = context.getItemModelManager();
        }

        @Override
        public ItemDisplayEntityRenderState net_minecraft_client_render_entity_state_ItemDisplayEntityRenderState_createRenderState() {
            return new ItemDisplayEntityRenderState();
        }

        @Override
        public void updateRenderState(DisplayEntity.ItemDisplayEntity itemDisplayEntity, ItemDisplayEntityRenderState itemDisplayEntityRenderState, float f) {
            super.updateRenderState(itemDisplayEntity, itemDisplayEntityRenderState, f);
            DisplayEntity.ItemDisplayEntity.Data data = itemDisplayEntity.getData();
            if (data != null) {
                this.itemModelManager.updateForNonLivingEntity(itemDisplayEntityRenderState.itemRenderState, data.itemStack(), data.itemTransform(), itemDisplayEntity);
            } else {
                itemDisplayEntityRenderState.itemRenderState.clear();
            }
        }

        @Override
        public void render(ItemDisplayEntityRenderState itemDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
            if (itemDisplayEntityRenderState.itemRenderState.isEmpty()) {
                return;
            }
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation((float)Math.PI));
            itemDisplayEntityRenderState.itemRenderState.render(matrixStack, vertexConsumerProvider, 1, OverlayTexture.DEFAULT_UV);
        }

        @Override
        public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
            return this.net_minecraft_client_render_entity_state_ItemDisplayEntityRenderState_createRenderState();
        }

        @Override
        protected float getShadowRadius(EntityRenderState state) {
            return super.getShadowRadius((DisplayEntityRenderState)state);
        }

        @Override
        protected int getBlockLight(Entity entity, BlockPos pos) {
            return super.getBlockLight((DisplayEntity)entity, pos);
        }

        @Override
        protected int getSkyLight(Entity entity, BlockPos pos) {
            return super.getSkyLight((DisplayEntity)entity, pos);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class BlockDisplayEntityRenderer
    extends DisplayEntityRenderer<DisplayEntity.BlockDisplayEntity, DisplayEntity.BlockDisplayEntity.Data, BlockDisplayEntityRenderState> {
        final private BlockRenderManager blockRenderManager;

        protected BlockDisplayEntityRenderer(EntityRendererFactory.Context context) {
            super(context);
            this.blockRenderManager = context.getBlockRenderManager();
        }

        @Override
        public BlockDisplayEntityRenderState net_minecraft_client_render_entity_state_BlockDisplayEntityRenderState_createRenderState() {
            return new BlockDisplayEntityRenderState();
        }

        @Override
        public void updateRenderState(DisplayEntity.BlockDisplayEntity blockDisplayEntity, BlockDisplayEntityRenderState blockDisplayEntityRenderState, float f) {
            super.updateRenderState(blockDisplayEntity, blockDisplayEntityRenderState, f);
            blockDisplayEntityRenderState.data = blockDisplayEntity.getData();
        }

        @Override
        public void render(BlockDisplayEntityRenderState blockDisplayEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
            this.blockRenderManager.renderBlockAsEntity(blockDisplayEntityRenderState.data.blockState(), matrixStack, vertexConsumerProvider, 1, OverlayTexture.DEFAULT_UV);
        }

        @Override
        public EntityRenderState net_minecraft_client_render_entity_state_EntityRenderState_createRenderState() {
            return this.net_minecraft_client_render_entity_state_BlockDisplayEntityRenderState_createRenderState();
        }

        @Override
        protected float getShadowRadius(EntityRenderState state) {
            return super.getShadowRadius((DisplayEntityRenderState)state);
        }

        @Override
        protected int getBlockLight(Entity entity, BlockPos pos) {
            return super.getBlockLight((DisplayEntity)entity, pos);
        }

        @Override
        protected int getSkyLight(Entity entity, BlockPos pos) {
            return super.getSkyLight((DisplayEntity)entity, pos);
        }
    }
}

