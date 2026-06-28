/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class FixedNumberFormat
implements NumberFormat {
    final static public NumberFormatType<FixedNumberFormat> TYPE = new NumberFormatType<FixedNumberFormat>(){
        final static private MapCodec<FixedNumberFormat> CODEC = TextCodecs.CODEC.fieldOf("value").xmap(FixedNumberFormat::new, format -> format.text);
        final static private PacketCodec<RegistryByteBuf, FixedNumberFormat> PACKET_CODEC = PacketCodec.tuple(TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, format -> format.text, FixedNumberFormat::new);

        @Override
        public MapCodec<FixedNumberFormat> getCodec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FixedNumberFormat> getPacketCodec() {
            return PACKET_CODEC;
        }
    };
    final Text text;

    public FixedNumberFormat(Text text) {
        this.text = text;
    }

    @Override
    public MutableText format(int number) {
        return this.text.copy();
    }

    public NumberFormatType<FixedNumberFormat> getType() {
        return TYPE;
    }
}

