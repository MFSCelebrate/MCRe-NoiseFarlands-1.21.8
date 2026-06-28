/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.ItemRenderState;

@Environment(value=EnvType.CLIENT)
public class KeyedItemRenderState
extends ItemRenderState {
    final private List<Object> modelKey = new ArrayList<Object>();

    @Override
    public void addModelKey(Object modelKey) {
        this.modelKey.add(modelKey);
    }

    public Object getModelKey() {
        return this.modelKey;
    }
}

