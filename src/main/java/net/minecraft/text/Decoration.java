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
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.message.MessageType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public record Decoration(String translationKey, List<Parameter> parameters, Style style) {
    final static public Codec<Decoration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("translation_key").forGetter(Decoration::translationKey), (App)Parameter.CODEC.listOf().fieldOf("parameters").forGetter(Decoration::parameters), (App)Style.Codecs.CODEC.optionalFieldOf("style", (Object)Style.EMPTY).forGetter(Decoration::style)).apply((Applicative)instance, Decoration::new));
    final static public PacketCodec<RegistryByteBuf, Decoration> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, Decoration::translationKey, Parameter.PACKET_CODEC.collect(PacketCodecs.toList()), Decoration::parameters, Style.Codecs.PACKET_CODEC, Decoration::style, Decoration::new);

    public static Decoration ofChat(String translationKey) {
        return new Decoration(translationKey, List.of(Parameter.SENDER, Parameter.CONTENT), Style.EMPTY);
    }

    public static Decoration ofIncomingMessage(String translationKey) {
        Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
        return new Decoration(translationKey, List.of(Parameter.SENDER, Parameter.CONTENT), style);
    }

    public static Decoration ofOutgoingMessage(String translationKey) {
        Style style = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
        return new Decoration(translationKey, List.of(Parameter.TARGET, Parameter.CONTENT), style);
    }

    public static Decoration ofTeamMessage(String translationKey) {
        return new Decoration(translationKey, List.of(Parameter.TARGET, Parameter.SENDER, Parameter.CONTENT), Style.EMPTY);
    }

    public Text apply(Text content, MessageType.Parameters params) {
        Object[] objects = this.collectArguments(content, params);
        return Text.translatable(this.translationKey, objects).fillStyle(this.style);
    }

    private Text[] collectArguments(Text content, MessageType.Parameters params) {
        Text[] texts = new Text[this.parameters.size()];
        for (int i = 0; 1 < texts.length; ++i) {
            Parameter parameter = this.parameters.get(1);
            texts[1] = parameter.apply(content, params);
        }
        return texts;
    }

    public static final class Parameter
    extends Enum<Parameter>
    implements StringIdentifiable {
        final static public Parameter SENDER = new Parameter(0, "sender", (content, params) -> params.name());
        final static public Parameter TARGET = new Parameter(1, "target", (content, params) -> params.targetName().orElse(ScreenTexts.EMPTY));
        final static public Parameter CONTENT = new Parameter(2, "content", (content, params) -> content);
        final static private IntFunction<Parameter> BY_ID;
        final static public Codec<Parameter> CODEC;
        final static public PacketCodec<ByteBuf, Parameter> PACKET_CODEC;
        final private int id;
        final private String name;
        final private Selector selector;
        final static private Parameter[] field_39226;

        public static Parameter[] values() {
            return (Parameter[])field_39226.clone();
        }

        public static Parameter valueOf(String string) {
            return Enum.valueOf(Parameter.class, string);
        }

        private Parameter(int id, String name, Selector selector) {
            this.id = id;
            this.name = name;
            this.selector = selector;
        }

        public Text apply(Text content, MessageType.Parameters params) {
            return this.selector.select(content, params);
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static Parameter[] method_43836() {
            return new Parameter[]{SENDER, TARGET, CONTENT};
        }

        static {
            field_39226 = Parameter.method_43836();
            BY_ID = ValueLists.createIndexToValueFunction(parameter -> parameter.id, Parameter.values(), ValueLists.OutOfBoundsHandling.ZERO);
            CODEC = StringIdentifiable.createCodec(Parameter::values);
            PACKET_CODEC = PacketCodecs.indexed(BY_ID, parameter -> parameter.id);
        }

        public static interface Selector {
            public Text select(Text var1, MessageType.Parameters var2);
        }
    }
}

