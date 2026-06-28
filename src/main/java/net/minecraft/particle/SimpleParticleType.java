/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class SimpleParticleType
extends ParticleType<SimpleParticleType>
implements ParticleEffect {
    final private MapCodec<SimpleParticleType> codec = MapCodec.unit(this::getType);
    final private PacketCodec<RegistryByteBuf, SimpleParticleType> packetCodec = PacketCodec.unit(this);

    protected SimpleParticleType(boolean alwaysShow) {
        super(alwaysShow);
    }

    public SimpleParticleType net_minecraft_particle_SimpleParticleType_getType() {
        return this;
    }

    @Override
    public MapCodec<SimpleParticleType> getCodec() {
        return this.codec;
    }

    @Override
    public PacketCodec<RegistryByteBuf, SimpleParticleType> getPacketCodec() {
        return this.packetCodec;
    }

    public ParticleType net_minecraft_particle_ParticleType_getType() {
        return this.net_minecraft_particle_SimpleParticleType_getType();
    }
}

