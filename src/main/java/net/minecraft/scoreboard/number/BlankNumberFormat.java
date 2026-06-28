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

public class BlankNumberFormat
implements NumberFormat {
    final static public BlankNumberFormat INSTANCE = new BlankNumberFormat();
    final static public NumberFormatType<BlankNumberFormat> TYPE = new NumberFormatType<BlankNumberFormat>(){
        final static private MapCodec<BlankNumberFormat> CODEC = MapCodec.unit((Object)INSTANCE);
        final static private PacketCodec<RegistryByteBuf, BlankNumberFormat> PACKET_CODEC = PacketCodec.unit(INSTANCE);

        @Override
        public MapCodec<BlankNumberFormat> getCodec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, BlankNumberFormat> getPacketCodec() {
            return PACKET_CODEC;
        }
    };

    @Override
    public MutableText format(int number) {
        return Text.empty();
    }

    public NumberFormatType<BlankNumberFormat> getType() {
        return TYPE;
    }
}

