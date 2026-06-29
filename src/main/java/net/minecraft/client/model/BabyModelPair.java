/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(value=EnvType.CLIENT)
public record BabyModelPair<T extends Model>(T adultModel, T babyModel) {
    public T get(boolean baby) {
        return baby ? this.babyModel : this.adultModel;
    }
}

