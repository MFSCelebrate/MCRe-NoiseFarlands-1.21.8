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
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StyledNumberFormat
implements NumberFormat {
    final static public NumberFormatType<StyledNumberFormat> TYPE = new NumberFormatType<StyledNumberFormat>(){
        final static private MapCodec<StyledNumberFormat> CODEC = Style.Codecs.MAP_CODEC.xmap(StyledNumberFormat::new, format -> format.style);
        final static private PacketCodec<RegistryByteBuf, StyledNumberFormat> PACKET_CODEC = PacketCodec.tuple(Style.Codecs.PACKET_CODEC, format -> format.style, StyledNumberFormat::new);

        @Override
        public MapCodec<StyledNumberFormat> getCodec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, StyledNumberFormat> getPacketCodec() {
            return PACKET_CODEC;
        }
    };
    final static public StyledNumberFormat EMPTY = new StyledNumberFormat(Style.EMPTY);
    final static public StyledNumberFormat RED = new StyledNumberFormat(Style.EMPTY.withColor(Formatting.RED));
    final static public StyledNumberFormat YELLOW = new StyledNumberFormat(Style.EMPTY.withColor(Formatting.YELLOW));
    final Style style;

    public StyledNumberFormat(Style style) {
        this.style = style;
    }

    @Override
    public MutableText format(int number) {
        return Text.literal(Integer.toString(number)).fillStyle(this.style);
    }

    public NumberFormatType<StyledNumberFormat> getType() {
        return TYPE;
    }
}

