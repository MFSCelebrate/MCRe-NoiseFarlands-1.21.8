/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.BundleSelectedItemModel;
import net.minecraft.client.render.item.model.CompositeItemModel;
import net.minecraft.client.render.item.model.ConditionItemModel;
import net.minecraft.client.render.item.model.EmptyItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class ItemModelTypes {
    final static public Codecs.IdMapper<Identifier, MapCodec<? extends ItemModel.Unbaked>> ID_MAPPER = new Codecs.IdMapper();
    final static public Codec<ItemModel.Unbaked> CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatch(ItemModel.Unbaked::getCodec, codec -> codec);

    public static void bootstrap() {
        ID_MAPPER.put(Identifier.ofVanilla("empty"), EmptyItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("model"), BasicItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("range_dispatch"), RangeDispatchItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("special"), SpecialItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("composite"), CompositeItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("bundle/selected_item"), BundleSelectedItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("select"), SelectItemModel.Unbaked.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("condition"), ConditionItemModel.Unbaked.CODEC);
    }
}

