/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityModel
extends EntityModel<EndCrystalEntityRenderState> {
    final static private String OUTER_GLASS = "outer_glass";
    final static private String INNER_GLASS = "inner_glass";
    final static private String BASE = "base";
    final static private float field_52906 = (float)Math.sin(0.7853981633974483);
    final public ModelPart base;
    final public ModelPart outerGlass;
    final public ModelPart innerGlass;
    final public ModelPart cube;

    public EndCrystalEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.base = modelPart.getChild(BASE);
        this.outerGlass = modelPart.getChild(OUTER_GLASS);
        this.innerGlass = this.outerGlass.getChild(INNER_GLASS);
        this.cube = this.innerGlass.getChild(EntityModelPartNames.CUBE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 0.875f;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        ModelPartData modelPartData2 = modelPartData.addChild(OUTER_GLASS, modelPartBuilder, ModelTransform.origin(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(INNER_GLASS, modelPartBuilder, ModelTransform.NONE.withScale(0.875f));
        modelPartData3.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE.withScale(0.765625f));
        modelPartData.addChild(BASE, ModelPartBuilder.create().uv(0, 16).cuboid(-6.0f, 0.0f, -6.0f, 12.0f, 4.0f, 12.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(EndCrystalEntityRenderState endCrystalEntityRenderState) {
        super.setAngles(endCrystalEntityRenderState);
        this.base.visible = endCrystalEntityRenderState.baseVisible;
        float f = endCrystalEntityRenderState.age * 3.0f;
        float g = EndCrystalEntityRenderer.getYOffset(endCrystalEntityRenderState.age) * 16.0f;
        this.outerGlass.originY += g / 2.0f;
        this.outerGlass.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(f).rotateAxis(1.0471976f, field_52906, 0.0f, field_52906));
        this.innerGlass.rotate(new Quaternionf().setAngleAxis(1.0471976f, field_52906, 0.0f, field_52906).rotateY(f * ((float)Math.PI / 180)));
        this.cube.rotate(new Quaternionf().setAngleAxis(1.0471976f, field_52906, 0.0f, field_52906).rotateY(f * ((float)Math.PI / 180)));
    }
}

