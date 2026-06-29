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
import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class DecoratedPotModelRenderer
implements SpecialModelRenderer<Sherds> {
    final private DecoratedPotBlockEntityRenderer blockEntityRenderer;

    public DecoratedPotModelRenderer(DecoratedPotBlockEntityRenderer blockEntityRenderer) {
        this.blockEntityRenderer = blockEntityRenderer;
    }

    @Override
    @Nullable
    public Sherds net_minecraft_block_entity_Sherds_getData(ItemStack itemStack) {
        return itemStack.get(DataComponentTypes.POT_DECORATIONS);
    }

    @Override
    public void render(@Nullable Sherds sherds, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, boolean bl) {
        this.blockEntityRenderer.renderAsItem(matrixStack, vertexConsumerProvider, i, j, Objects.requireNonNullElse(sherds, Sherds.DEFAULT));
    }

    @Override
    public void collectVertices(Set<Vector3f> vertices) {
        this.blockEntityRenderer.collectVertices(vertices);
    }

    @Override
    @Nullable
    public Object java_lang_Object_getData(ItemStack stack) {
        return this.net_minecraft_block_entity_Sherds_getData(stack);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        final static public MapCodec<Unbaked> CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(LoadedEntityModels entityModels) {
            return new DecoratedPotModelRenderer(new DecoratedPotBlockEntityRenderer(entityModels));
        }
    }
}

