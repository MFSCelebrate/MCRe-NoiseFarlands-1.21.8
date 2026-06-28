/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;

@Environment(value=EnvType.CLIENT)
public record MultipartModelComponent(Optional<MultipartModelCondition> selector, BlockStateModel.Unbaked model) {
    final static public Codec<MultipartModelComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)MultipartModelCondition.CODEC.optionalFieldOf("when").forGetter(MultipartModelComponent::selector), (App)BlockStateModel.Unbaked.CODEC.fieldOf("apply").forGetter(MultipartModelComponent::model)).apply((Applicative)instance, MultipartModelComponent::new));

    public <O, S extends State<O, S>> Predicate<S> init(StateManager<O, S> value) {
        return this.selector.map(multipartModelCondition -> multipartModelCondition.instantiate(value)).orElse(state -> true);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MultipartModelComponent.class, "condition;variant", "selector", "model"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MultipartModelComponent.class, "condition;variant", "selector", "model"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MultipartModelComponent.class, "condition;variant", "selector", "model"}, this, object);
    }
}

