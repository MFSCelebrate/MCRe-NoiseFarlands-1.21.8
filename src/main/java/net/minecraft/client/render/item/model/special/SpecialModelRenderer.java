/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public interface SpecialModelRenderer<T> {
    public void render(@Nullable T var1, ItemDisplayContext var2, MatrixStack var3, VertexConsumerProvider var4, int var5, int var6, boolean var7);

    public void collectVertices(Set<Vector3f> var1);

    @Nullable
    public T getData(ItemStack var1);

    @Environment(value=EnvType.CLIENT)
    public static interface Unbaked {
        @Nullable
        public SpecialModelRenderer<?> bake(LoadedEntityModels var1);

        public MapCodec<? extends Unbaked> getCodec();
    }
}

