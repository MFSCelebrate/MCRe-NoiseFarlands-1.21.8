/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.GeometryBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;

@Environment(value=EnvType.CLIENT)
public record ModelVariant(Identifier modelId, ModelState modelState) implements BlockModelPart.Unbaked
{
    final static public MapCodec<ModelVariant> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("model").forGetter(ModelVariant::modelId), (App)ModelState.CODEC.forGetter(ModelVariant::modelState)).apply((Applicative)instance, ModelVariant::new));
    final static public Codec<ModelVariant> CODEC = MAP_CODEC.codec();

    public ModelVariant(Identifier model) {
        this(model, ModelState.DEFAULT);
    }

    public ModelVariant withRotationX(AxisRotation amount) {
        return this.setState(this.modelState.setRotationX(amount));
    }

    public ModelVariant withRotationY(AxisRotation amount) {
        return this.setState(this.modelState.setRotationY(amount));
    }

    public ModelVariant withUVLock(boolean uvLock) {
        return this.setState(this.modelState.setUVLock(uvLock));
    }

    public ModelVariant withModel(Identifier modelId) {
        return new ModelVariant(modelId, this.modelState);
    }

    public ModelVariant setState(ModelState modelState) {
        return new ModelVariant(this.modelId, modelState);
    }

    public ModelVariant with(ModelVariantOperator variantOperator) {
        return (ModelVariant)variantOperator.apply(this);
    }

    @Override
    public BlockModelPart bake(Baker baker) {
        return GeometryBakedModel.create(baker, this.modelId, this.modelState.asModelBakeSettings());
    }

    @Override
    public void resolve(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.modelId);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelVariant.class, "modelLocation;modelState", "modelId", "modelState"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelVariant.class, "modelLocation;modelState", "modelId", "modelState"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelVariant.class, "modelLocation;modelState", "modelId", "modelState"}, this, o);
    }

    @Environment(value=EnvType.CLIENT)
    public record ModelState(AxisRotation x, AxisRotation y, boolean uvLock) {
        final static public MapCodec<ModelState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)AxisRotation.CODEC.optionalFieldOf("x", (Object)AxisRotation.R0).forGetter(ModelState::x), (App)AxisRotation.CODEC.optionalFieldOf("y", (Object)AxisRotation.R0).forGetter(ModelState::y), (App)Codec.BOOL.optionalFieldOf("uvlock", (Object)false).forGetter(ModelState::uvLock)).apply((Applicative)instance, ModelState::new));
        final static public ModelState DEFAULT = new ModelState(AxisRotation.R0, AxisRotation.R0, false);

        public ModelBakeSettings asModelBakeSettings() {
            ModelRotation modelRotation = ModelRotation.rotate(this.x, this.y);
            return this.uvLock ? modelRotation.getUVModel() : modelRotation;
        }

        public ModelState setRotationX(AxisRotation amount) {
            return new ModelState(amount, this.y, this.uvLock);
        }

        public ModelState setRotationY(AxisRotation amount) {
            return new ModelState(this.x, amount, this.uvLock);
        }

        public ModelState setUVLock(boolean uvLock) {
            return new ModelState(this.x, this.y, uvLock);
        }
    }
}

