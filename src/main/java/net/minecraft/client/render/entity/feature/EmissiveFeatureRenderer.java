/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EmissiveFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    final private Identifier texture;
    final private AnimationAlphaAdjuster<S> animationAlphaAdjuster;
    final private ModelPartVisibility<S, M> modelPartVisibility;
    final private Function<Identifier, RenderLayer> renderLayerFunction;
    final private boolean ignoresInvisibility;

    public EmissiveFeatureRenderer(FeatureRendererContext<S, M> context, Identifier texture, AnimationAlphaAdjuster<S> animationAlphaAdjuster, ModelPartVisibility<S, M> modelPartVisibility, Function<Identifier, RenderLayer> renderLayerFunction, boolean ignoresInvisibility) {
        super(context);
        this.texture = texture;
        this.animationAlphaAdjuster = animationAlphaAdjuster;
        this.modelPartVisibility = modelPartVisibility;
        this.renderLayerFunction = renderLayerFunction;
        this.ignoresInvisibility = ignoresInvisibility;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g) {
        if (((LivingEntityRenderState)livingEntityRenderState).invisible && !this.ignoresInvisibility) {
            return;
        }
        if (!this.updateModelPartVisibility(livingEntityRenderState)) {
            return;
        }
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.renderLayerFunction.apply(this.texture));
        float h = this.animationAlphaAdjuster.apply(livingEntityRenderState, ((LivingEntityRenderState)livingEntityRenderState).age);
        int j = ColorHelper.getArgb(MathHelper.floor(h * 255.0f), 255, 255, 255);
        ((Model)this.getContextModel()).render(matrixStack, vertexConsumer, 1, LivingEntityRenderer.getOverlay(livingEntityRenderState, 0.0f), j);
        this.unhideAllModelParts();
    }

    private boolean updateModelPartVisibility(S state) {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getContextModel(), state);
        if (list.isEmpty()) {
            return false;
        }
        ((Model)this.getContextModel()).getParts().forEach(part -> {
            part.hidden = true;
        });
        list.forEach(part -> {
            part.hidden = false;
        });
        return true;
    }

    private void unhideAllModelParts() {
        ((Model)this.getContextModel()).getParts().forEach(part -> {
            part.hidden = false;
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static interface AnimationAlphaAdjuster<S extends LivingEntityRenderState> {
        public float apply(S var1, float var2);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ModelPartVisibility<S extends LivingEntityRenderState, M extends EntityModel<S>> {
        public List<ModelPart> getPartsToDraw(M var1, S var2);
    }
}

