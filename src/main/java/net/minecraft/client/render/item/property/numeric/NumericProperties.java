/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.BundleFullnessProperty;
import net.minecraft.client.render.item.property.numeric.CompassProperty;
import net.minecraft.client.render.item.property.numeric.CooldownProperty;
import net.minecraft.client.render.item.property.numeric.CountProperty;
import net.minecraft.client.render.item.property.numeric.CrossbowPullProperty;
import net.minecraft.client.render.item.property.numeric.CustomModelDataFloatProperty;
import net.minecraft.client.render.item.property.numeric.DamageProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.TimeProperty;
import net.minecraft.client.render.item.property.numeric.UseCycleProperty;
import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public class NumericProperties {
    final static public Codecs.IdMapper<Identifier, MapCodec<? extends NumericProperty>> ID_MAPPER = new Codecs.IdMapper();
    final static public MapCodec<NumericProperty> CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatchMap("property", NumericProperty::getCodec, codec -> codec);

    public static void bootstrap() {
        ID_MAPPER.put(Identifier.ofVanilla("custom_model_data"), CustomModelDataFloatProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("bundle/fullness"), BundleFullnessProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("damage"), DamageProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("cooldown"), CooldownProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("time"), TimeProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("compass"), CompassProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("crossbow/pull"), CrossbowPullProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("use_cycle"), UseCycleProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("use_duration"), UseDurationProperty.CODEC);
        ID_MAPPER.put(Identifier.ofVanilla("count"), CountProperty.CODEC);
    }
}

