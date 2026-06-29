/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.component.type;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record EnchantableComponent(int value) {
    final static public Codec<EnchantableComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.POSITIVE_INT.fieldOf("value").forGetter(EnchantableComponent::value)).apply((Applicative)instance, EnchantableComponent::new));
    final static public PacketCodec<ByteBuf, EnchantableComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, EnchantableComponent::value, EnchantableComponent::new);

    public EnchantableComponent {
        if (i <= 0) {
            throw new IllegalArgumentException("Enchantment value must be positive, but was " + i);
        }
    }
}

