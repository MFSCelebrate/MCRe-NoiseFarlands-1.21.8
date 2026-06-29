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
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.item;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.registry.ContextSwapper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ItemAsset(ItemModel.Unbaked model, Properties properties, @Nullable ContextSwapper registrySwapper) {
    final static public Codec<ItemAsset> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ItemModelTypes.CODEC.fieldOf("model").forGetter(ItemAsset::model), (App)Properties.CODEC.forGetter(ItemAsset::properties)).apply((Applicative)instance, ItemAsset::new));

    public ItemAsset(ItemModel.Unbaked model, Properties properties) {
        this(model, properties, null);
    }

    public ItemAsset withContextSwapper(ContextSwapper contextSwapper) {
        return new ItemAsset(this.model, this.properties, contextSwapper);
    }

    @Environment(value=EnvType.CLIENT)
    public record Properties(boolean handAnimationOnSwap, boolean oversizedInGui) {
        final static public Properties DEFAULT = new Properties(true, false);
        final static public MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("hand_animation_on_swap", (Object)true).forGetter(Properties::handAnimationOnSwap), (App)Codec.BOOL.optionalFieldOf("oversized_in_gui", (Object)false).forGetter(Properties::oversizedInGui)).apply((Applicative)instance, Properties::new));
    }
}

