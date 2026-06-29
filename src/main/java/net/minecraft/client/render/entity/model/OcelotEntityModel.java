/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.FelineEntityModel;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityModel
extends FelineEntityModel<FelineEntityRenderState> {
    public OcelotEntityModel(ModelPart modelPart) {
        super(modelPart);
    }
}

