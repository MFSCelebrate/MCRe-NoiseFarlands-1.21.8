/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HorseMarkingFeatureRenderer
extends FeatureRenderer<HorseEntityRenderState, HorseEntityModel> {
    final static private Identifier INVISIBLE_ID = Identifier.ofVanilla("invisible");
    final static private Map<HorseMarking, Identifier> TEXTURES = Maps.newEnumMap(Map.of(HorseMarking.NONE, INVISIBLE_ID, HorseMarking.WHITE, Identifier.ofVanilla("textures/entity/horse/horse_markings_white.png"), HorseMarking.WHITE_FIELD, Identifier.ofVanilla("textures/entity/horse/horse_markings_whitefield.png"), HorseMarking.WHITE_DOTS, Identifier.ofVanilla("textures/entity/horse/horse_markings_whitedots.png"), HorseMarking.BLACK_DOTS, Identifier.ofVanilla("textures/entity/horse/horse_markings_blackdots.png")));

    public HorseMarkingFeatureRenderer(FeatureRendererContext<HorseEntityRenderState, HorseEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntityRenderState horseEntityRenderState, float f, float g) {
        Identifier identifier = TEXTURES.get((Object)horseEntityRenderState.marking);
        if (identifier == INVISIBLE_ID || horseEntityRenderState.invisible) {
            return;
        }
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(identifier));
        ((HorseEntityModel)this.getContextModel()).render(matrixStack, vertexConsumer, 1, LivingEntityRenderer.getOverlay(horseEntityRenderState, 0.0f));
    }
}

