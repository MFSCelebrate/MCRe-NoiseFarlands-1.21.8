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
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ZombieEntityModel<S extends ZombieEntityRenderState>
extends AbstractZombieModel<S> {
    public ZombieEntityModel(ModelPart modelPart) {
        super(modelPart);
    }
}

