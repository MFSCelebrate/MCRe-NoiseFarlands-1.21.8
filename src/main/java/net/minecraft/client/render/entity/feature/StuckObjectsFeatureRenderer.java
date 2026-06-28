/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class StuckObjectsFeatureRenderer<M extends PlayerEntityModel>
extends FeatureRenderer<PlayerEntityRenderState, M> {
    final private Model model;
    final private Identifier texture;
    final private RenderPosition renderPosition;

    public StuckObjectsFeatureRenderer(LivingEntityRenderer<?, PlayerEntityRenderState, M> entityRenderer, Model model, Identifier texture, RenderPosition renderPosition) {
        super(entityRenderer);
        this.model = model;
        this.texture = texture;
        this.renderPosition = renderPosition;
    }

    protected abstract int getObjectCount(PlayerEntityRenderState var1);

    private void renderObject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float f, float directionX, float directionY) {
        float g = MathHelper.sqrt(f * f + directionY * directionY);
        float h = (float)(Math.atan2(f, directionY) * 57.2957763671875);
        float i = (float)(Math.atan2(directionX, g) * 57.2957763671875);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(h - 90.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(i));
        this.model.render(matrices, vertexConsumers.getBuffer(this.model.getLayer(this.texture)), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        int j = this.getObjectCount(playerEntityRenderState);
        if (j <= 0) {
            return;
        }
        Random random = Random.create(playerEntityRenderState.id);
        for (int k = 0; k < j; ++k) {
            matrixStack.push();
            ModelPart modelPart = ((PlayerEntityModel)this.getContextModel()).getRandomPart(random);
            ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
            modelPart.applyTransform(matrixStack);
            float h = random.nextFloat();
            float l = random.nextFloat();
            float m = random.nextFloat();
            if (this.renderPosition == RenderPosition.ON_SURFACE) {
                int n = random.nextInt(3);
                switch (n) {
                    case 0: {
                        h = StuckObjectsFeatureRenderer.method_62597(h);
                        break;
                    }
                    case 1: {
                        l = StuckObjectsFeatureRenderer.method_62597(l);
                        break;
                    }
                    default: {
                        m = StuckObjectsFeatureRenderer.method_62597(m);
                    }
                }
            }
            matrixStack.translate(MathHelper.lerp(h, cuboid.minX, cuboid.maxX) / 16.0f, MathHelper.lerp(l, cuboid.minY, cuboid.maxY) / 16.0f, MathHelper.lerp(m, cuboid.minZ, cuboid.maxZ) / 16.0f);
            this.renderObject(matrixStack, vertexConsumerProvider, 1, -(h * 2.0f - 1.0f), -(l * 2.0f - 1.0f), -(m * 2.0f - 1.0f));
            matrixStack.pop();
        }
    }

    private static float method_62597(float f) {
        return f > 0.5f ? 1.0f : 0.5f;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class RenderPosition
    extends Enum<RenderPosition> {
        final static public RenderPosition IN_CUBE = new RenderPosition();
        final static public RenderPosition ON_SURFACE = new RenderPosition();
        final static private RenderPosition[] field_53234;

        public static RenderPosition[] values() {
            return (RenderPosition[])field_53234.clone();
        }

        public static RenderPosition valueOf(String string) {
            return Enum.valueOf(RenderPosition.class, string);
        }

        private static RenderPosition[] method_62598() {
            return new RenderPosition[]{IN_CUBE, ON_SURFACE};
        }

        static {
            field_53234 = RenderPosition.method_62598();
        }
    }
}

