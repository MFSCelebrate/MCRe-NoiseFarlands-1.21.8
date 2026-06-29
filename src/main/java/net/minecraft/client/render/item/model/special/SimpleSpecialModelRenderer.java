/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SimpleSpecialModelRenderer
extends SpecialModelRenderer<Void> {
    @Override
    @Nullable
    default public Void java_lang_Void_getData(ItemStack itemStack) {
        return null;
    }

    @Override
    default public void render(@Nullable Void void_, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
        this.render(itemDisplayContext, matrixStack, vertexConsumerProvider, i, j, bl);
    }

    public void render(ItemDisplayContext var1, MatrixStack var2, VertexConsumerProvider var3, int var4, int var5, boolean var6);

    @Override
    @Nullable
    default public Object java_lang_Object_getData(ItemStack stack) {
        return this.java_lang_Void_getData(stack);
    }
}

