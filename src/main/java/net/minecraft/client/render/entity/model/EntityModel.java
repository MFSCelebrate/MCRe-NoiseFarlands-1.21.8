/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class EntityModel<T extends EntityRenderState>
extends Model {
    final static public float field_52908 = -1.501f;

    protected EntityModel(ModelPart root) {
        this(root, RenderLayer::getEntityCutoutNoCull);
    }

    protected EntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
        super(modelPart, function);
    }

    public void setAngles(T state) {
        this.resetTransforms();
    }
}

