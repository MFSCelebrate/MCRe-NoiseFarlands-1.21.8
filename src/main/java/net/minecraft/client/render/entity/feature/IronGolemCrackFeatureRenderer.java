/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IronGolemCrackFeatureRenderer
extends FeatureRenderer<IronGolemEntityRenderState, IronGolemEntityModel> {
    final static private Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES = ImmutableMap.of((Object)((Object)Cracks.CrackLevel.LOW), (Object)Identifier.ofVanilla("textures/entity/iron_golem/iron_golem_crackiness_low.png"), (Object)((Object)Cracks.CrackLevel.MEDIUM), (Object)Identifier.ofVanilla("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), (Object)((Object)Cracks.CrackLevel.HIGH), (Object)Identifier.ofVanilla("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCrackFeatureRenderer(FeatureRendererContext<IronGolemEntityRenderState, IronGolemEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IronGolemEntityRenderState ironGolemEntityRenderState, float f, float g) {
        if (ironGolemEntityRenderState.invisible) {
            return;
        }
        Cracks.CrackLevel crackLevel = ironGolemEntityRenderState.crackLevel;
        if (crackLevel == Cracks.CrackLevel.NONE) {
            return;
        }
        Identifier identifier = CRACK_TEXTURES.get((Object)crackLevel);
        IronGolemCrackFeatureRenderer.renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, 1, ironGolemEntityRenderState, -1);
    }
}

