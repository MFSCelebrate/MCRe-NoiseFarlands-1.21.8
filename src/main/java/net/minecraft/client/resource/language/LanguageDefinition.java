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
package net.minecraft.client.resource.language;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record LanguageDefinition(String region, String name, boolean rightToLeft) {
    final static public Codec<LanguageDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.NON_EMPTY_STRING.fieldOf("region").forGetter(LanguageDefinition::region), (App)Codecs.NON_EMPTY_STRING.fieldOf("name").forGetter(LanguageDefinition::name), (App)Codec.BOOL.optionalFieldOf("bidirectional", (Object)false).forGetter(LanguageDefinition::rightToLeft)).apply((Applicative)instance, LanguageDefinition::new));

    public Text getDisplayText() {
        return Text.literal(this.name + " (" + this.region + ")");
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LanguageDefinition.class, "region;name;bidirectional", "region", "name", "rightToLeft"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LanguageDefinition.class, "region;name;bidirectional", "region", "name", "rightToLeft"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LanguageDefinition.class, "region;name;bidirectional", "region", "name", "rightToLeft"}, this, o);
    }
}

