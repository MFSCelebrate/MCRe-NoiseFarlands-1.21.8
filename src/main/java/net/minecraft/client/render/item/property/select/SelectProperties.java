/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.select;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.select.ChargeTypeProperty;
import net.minecraft.client.render.item.property.select.ComponentSelectProperty;
import net.minecraft.client.render.item.property.select.ContextDimensionProperty;
import net.minecraft.client.render.item.property.select.ContextEntityTypeProperty;
import net.minecraft.client.render.item.property.select.CustomModelDataStringProperty;
import net.minecraft.client.render.item.property.select.DisplayContextProperty;
import net.minecraft.client.render.item.property.select.ItemBlockStateProperty;
import net.minecraft.client.render.item.property.select.LocalTimeProperty;
import net.minecraft.client.render.item.property.select.MainHandProperty;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.item.property.select.TrimMaterialProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class SelectProperties {
    final static public Codecs.IdMapper<Identifier, SelectProperty.Type<?, ?>> ID_MAPPER = new Codecs.IdMapper();
    final static public Codec<SelectProperty.Type<?, ?>> CODEC = ID_MAPPER.getCodec(Identifier.CODEC);

    public static void bootstrap() {
        ID_MAPPER.put(Identifier.ofVanilla("custom_model_data"), CustomModelDataStringProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("main_hand"), MainHandProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("charge_type"), ChargeTypeProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("trim_material"), TrimMaterialProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("block_state"), ItemBlockStateProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("display_context"), DisplayContextProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("local_time"), LocalTimeProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("context_entity_type"), ContextEntityTypeProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("context_dimension"), ContextDimensionProperty.TYPE);
        ID_MAPPER.put(Identifier.ofVanilla("component"), ComponentSelectProperty.getTypeInstance());
    }
}

