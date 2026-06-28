/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class HangingSignBlockEntityRenderer
extends AbstractSignBlockEntityRenderer {
    final static private String PLANK = "plank";
    final static private String V_CHAINS = "vChains";
    final static private String NORMAL_CHAINS = "normalChains";
    final static private String CHAIN_L1 = "chainL1";
    final static private String CHAIN_L2 = "chainL2";
    final static private String CHAIN_R1 = "chainR1";
    final static private String CHAIN_R2 = "chainR2";
    final static private String BOARD = "board";
    final static public float MODEL_SCALE = 1.0f;
    final static private float TEXT_SCALE = 0.9f;
    final static private Vec3d TEXT_OFFSET = new Vec3d(0.0, -0.32f, 0.073f);
    final private Map<Variant, Model> models;

    public HangingSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
        Stream stream = WoodType.stream().flatMap(woodType -> Arrays.stream(AttachmentType.values()).map(attachmentType -> new Variant((WoodType)woodType, (AttachmentType)attachmentType)));
        this.models = (Map)stream.collect(ImmutableMap.toImmutableMap(variant -> variant, variant -> HangingSignBlockEntityRenderer.createModel(context.getLoadedEntityModels(), variant.woodType, variant.attachmentType)));
    }

    public static Model createModel(LoadedEntityModels models, WoodType woodType, AttachmentType attachmentType) {
        return new Model.SinglePartModel(models.getModelPart(EntityModelLayers.createHangingSign(woodType, attachmentType)), RenderLayer::getEntityCutoutNoCull);
    }

    @Override
    protected float getSignScale() {
        return 1.0f;
    }

    @Override
    protected float getTextScale() {
        return 0.9f;
    }

    public static void setAngles(MatrixStack matrices, float blockRotationDegrees) {
        matrices.translate(0.5, 0.9375, 0.5);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(blockRotationDegrees));
        matrices.translate(0.0f, -0.3125f, 0.0f);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state) {
        HangingSignBlockEntityRenderer.setAngles(matrices, blockRotationDegrees);
    }

    @Override
    protected Model getModel(BlockState state, WoodType woodType) {
        AttachmentType attachmentType = AttachmentType.from(state);
        return this.models.get(new Variant(woodType, attachmentType));
    }

    @Override
    protected SpriteIdentifier getTextureId(WoodType woodType) {
        return TexturedRenderLayers.getHangingSignTextureId(woodType);
    }

    @Override
    protected Vec3d getTextOffset() {
        return TEXT_OFFSET;
    }

    public static void renderAsItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Model model, SpriteIdentifier texture) {
        matrices.push();
        HangingSignBlockEntityRenderer.setAngles(matrices, 0.0f);
        matrices.scale(1.0f, -1.0f, -1.0f);
        VertexConsumer vertexConsumer = texture.getVertexConsumer(vertexConsumers, model::getLayer);
        model.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }

    public static TexturedModelData getTexturedModelData(AttachmentType attachmentType) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(BOARD, ModelPartBuilder.create().uv(0, 12).cuboid(-7.0f, 0.0f, -1.0f, 14.0f, 10.0f, 2.0f), ModelTransform.NONE);
        if (attachmentType == AttachmentType.WALL) {
            modelPartData.addChild(PLANK, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -6.0f, -2.0f, 16.0f, 2.0f, 4.0f), ModelTransform.NONE);
        }
        if (attachmentType == AttachmentType.WALL || attachmentType == AttachmentType.CEILING) {
            ModelPartData modelPartData2 = modelPartData.addChild(NORMAL_CHAINS, ModelPartBuilder.create(), ModelTransform.NONE);
            modelPartData2.addChild(CHAIN_L1, ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(-5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
            modelPartData2.addChild(CHAIN_L2, ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(-5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
            modelPartData2.addChild(CHAIN_R1, ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(5.0f, -6.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
            modelPartData2.addChild(CHAIN_R2, ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of(5.0f, -6.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        }
        if (attachmentType == AttachmentType.CEILING_MIDDLE) {
            modelPartData.addChild(V_CHAINS, ModelPartBuilder.create().uv(14, 6).cuboid(-6.0f, -6.0f, 0.0f, 12.0f, 6.0f, 0.0f), ModelTransform.NONE);
        }
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class AttachmentType
    extends Enum<AttachmentType>
    implements StringIdentifiable {
        final static public AttachmentType WALL = new AttachmentType("wall");
        final static public AttachmentType CEILING = new AttachmentType("ceiling");
        final static public AttachmentType CEILING_MIDDLE = new AttachmentType("ceiling_middle");
        final private String id;
        final static private AttachmentType[] field_55162;

        public static AttachmentType[] values() {
            return (AttachmentType[])field_55162.clone();
        }

        public static AttachmentType valueOf(String string) {
            return Enum.valueOf(AttachmentType.class, string);
        }

        private AttachmentType(String id) {
            this.id = id;
        }

        public static AttachmentType from(BlockState state) {
            if (state.getBlock() instanceof HangingSignBlock) {
                return state.get(Properties.ATTACHED) != false ? CEILING_MIDDLE : CEILING;
            }
            return WALL;
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static AttachmentType[] method_65243() {
            return new AttachmentType[]{WALL, CEILING, CEILING_MIDDLE};
        }

        static {
            field_55162 = AttachmentType.method_65243();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Variant
    extends Record {
        final WoodType woodType;
        final AttachmentType attachmentType;

        public Variant(WoodType woodType, AttachmentType attachmentType) {
            this.woodType = woodType;
            this.attachmentType = attachmentType;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Variant.class, "woodType;attachmentType", "woodType", "attachmentType"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Variant.class, "woodType;attachmentType", "woodType", "attachmentType"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Variant.class, "woodType;attachmentType", "woodType", "attachmentType"}, this, object);
        }

        public WoodType woodType() {
            return this.woodType;
        }

        public AttachmentType attachmentType() {
            return this.attachmentType;
        }
    }
}

