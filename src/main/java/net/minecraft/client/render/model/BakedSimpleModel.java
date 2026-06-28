/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BakedSimpleModel
extends SimpleModel {
    final static public boolean DEFAULT_AMBIENT_OCCLUSION = true;
    final static public UnbakedModel.GuiLight DEFAULT_GUI_LIGHT = UnbakedModel.GuiLight.BLOCK;

    public UnbakedModel getModel();

    @Nullable
    public BakedSimpleModel getParent();

    public static ModelTextures getTextures(BakedSimpleModel model) {
        ModelTextures.Builder builder = new ModelTextures.Builder();
        for (BakedSimpleModel bakedSimpleModel = model; bakedSimpleModel != null; bakedSimpleModel = bakedSimpleModel.getParent()) {
            builder.addLast(bakedSimpleModel.getModel().textures());
        }
        return builder.build(model);
    }

    default public ModelTextures getTextures() {
        return BakedSimpleModel.getTextures(this);
    }

    public static boolean getAmbientOcclusion(BakedSimpleModel model) {
        while (model != null) {
            Boolean boolean_ = model.getModel().ambientOcclusion();
            if (boolean_ != null) {
                return boolean_;
            }
            model = model.getParent();
        }
        return true;
    }

    default public boolean getAmbientOcclusion() {
        return BakedSimpleModel.getAmbientOcclusion(this);
    }

    public static UnbakedModel.GuiLight getGuiLight(BakedSimpleModel model) {
        while (model != null) {
            UnbakedModel.GuiLight guiLight = model.getModel().guiLight();
            if (guiLight != null) {
                return guiLight;
            }
            model = model.getParent();
        }
        return DEFAULT_GUI_LIGHT;
    }

    default public UnbakedModel.GuiLight getGuiLight() {
        return BakedSimpleModel.getGuiLight(this);
    }

    public static Geometry getGeometry(BakedSimpleModel model) {
        while (model != null) {
            Geometry geometry = model.getModel().geometry();
            if (geometry != null) {
                return geometry;
            }
            model = model.getParent();
        }
        return Geometry.EMPTY;
    }

    default public Geometry getGeometry() {
        return BakedSimpleModel.getGeometry(this);
    }

    default public BakedGeometry bakeGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
        return this.getGeometry().bake(textures, baker, settings, this);
    }

    public static Sprite getParticleTexture(ModelTextures textures, Baker baker, SimpleModel model) {
        return baker.getSpriteGetter().get(textures, "particle", model);
    }

    default public Sprite getParticleTexture(ModelTextures textures, Baker baker) {
        return BakedSimpleModel.getParticleTexture(textures, baker, this);
    }

    public static Transformation extractTransformation(BakedSimpleModel model, ItemDisplayContext mode) {
        while (model != null) {
            Transformation transformation;
            ModelTransformation modelTransformation = model.getModel().transformations();
            if (modelTransformation != null && (transformation = modelTransformation.getTransformation(mode)) != Transformation.IDENTITY) {
                return transformation;
            }
            model = model.getParent();
        }
        return Transformation.IDENTITY;
    }

    public static ModelTransformation copyTransformations(BakedSimpleModel model) {
        Transformation transformation = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.HEAD);
        Transformation transformation6 = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.GUI);
        Transformation transformation7 = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.GROUND);
        Transformation transformation8 = BakedSimpleModel.extractTransformation(model, ItemDisplayContext.FIXED);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    default public ModelTransformation getTransformations() {
        return BakedSimpleModel.copyTransformations(this);
    }
}

