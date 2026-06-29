/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.mojang.serialization.Codec;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class SimpleBlockStateModel
implements BlockStateModel {
    final private BlockModelPart part;

    public SimpleBlockStateModel(BlockModelPart part) {
        this.part = part;
    }

    @Override
    public void addParts(Random random, List<BlockModelPart> parts) {
        parts.add(this.part);
    }

    @Override
    public Sprite particleSprite() {
        return this.part.particleSprite();
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(ModelVariant variant) implements BlockStateModel.Unbaked
    {
        final static public Codec<Unbaked> CODEC = ModelVariant.CODEC.xmap(Unbaked::new, Unbaked::variant);

        @Override
        public BlockStateModel bake(Baker baker) {
            return new SimpleBlockStateModel(this.variant.bake(baker));
        }

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            this.variant.resolve(resolver);
        }
    }
}

