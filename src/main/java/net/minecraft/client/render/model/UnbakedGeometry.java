/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public record UnbakedGeometry(List<ModelElement> elements) implements Geometry
{
    @Override
    public BakedGeometry bake(ModelTextures modelTextures, Baker baker, ModelBakeSettings modelBakeSettings, SimpleModel simpleModel) {
        return UnbakedGeometry.bakeGeometry(this.elements, modelTextures, baker.getSpriteGetter(), modelBakeSettings, simpleModel);
    }

    public static BakedGeometry bakeGeometry(List<ModelElement> elements, ModelTextures textures, ErrorCollectingSpriteGetter errorCollectingSpriteGetter, ModelBakeSettings settings, SimpleModel model) {
        BakedGeometry.Builder builder = new BakedGeometry.Builder();
        for (ModelElement modelElement : elements) {
            modelElement.faces().forEach((direction, modelElementFace) -> {
                Sprite sprite = errorCollectingSpriteGetter.get(textures, modelElementFace.textureId(), model);
                if (modelElementFace.cullFace() == null) {
                    builder.add(UnbakedGeometry.bakeQuad(modelElement, modelElementFace, sprite, direction, settings));
                } else {
                    builder.add(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace()), UnbakedGeometry.bakeQuad(modelElement, modelElementFace, sprite, direction, settings));
                }
            });
        }
        return builder.build();
    }

    private static BakedQuad bakeQuad(ModelElement element, ModelElementFace face, Sprite sprite, Direction facing, ModelBakeSettings settings) {
        return BakedQuadFactory.bake(element.from(), element.to(), face, sprite, facing, settings, element.rotation(), element.shade(), element.lightEmission());
    }
}

