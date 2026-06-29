/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.particle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;

public class VibrationParticleEffect
implements ParticleEffect {
    final static private Codec<PositionSource> POSITION_SOURCE_CODEC = PositionSource.CODEC.validate(positionSource -> positionSource instanceof EntityPositionSource ? DataResult.error(() -> "Entity position sources are not allowed") : DataResult.success((Object)positionSource));
    final static public MapCodec<VibrationParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)POSITION_SOURCE_CODEC.fieldOf("destination").forGetter(VibrationParticleEffect::getVibration), (App)Codec.INT.fieldOf("arrival_in_ticks").forGetter(VibrationParticleEffect::getArrivalInTicks)).apply((Applicative)instance, VibrationParticleEffect::new));
    final static public PacketCodec<RegistryByteBuf, VibrationParticleEffect> PACKET_CODEC = PacketCodec.tuple(PositionSource.PACKET_CODEC, VibrationParticleEffect::getVibration, PacketCodecs.VAR_INT, VibrationParticleEffect::getArrivalInTicks, VibrationParticleEffect::new);
    final private PositionSource destination;
    final private int arrivalInTicks;

    public VibrationParticleEffect(PositionSource destination, int arrivalInTicks) {
        this.destination = destination;
        this.arrivalInTicks = arrivalInTicks;
    }

    public ParticleType<VibrationParticleEffect> getType() {
        return ParticleTypes.VIBRATION;
    }

    public PositionSource getVibration() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}

