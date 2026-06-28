/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class TimeArgumentType
implements ArgumentType<Integer> {
    final static private Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
    final static private SimpleCommandExceptionType INVALID_UNIT_EXCEPTION = new SimpleCommandExceptionType((Message)Text.translatable("argument.time.invalid_unit"));
    final static private Dynamic2CommandExceptionType TICK_COUNT_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType((value, minimum) -> Text.stringifiedTranslatable("argument.time.tick_count_too_low", minimum, value));
    final static private Object2IntMap<String> UNITS = new Object2IntOpenHashMap();
    final int minimum;

    private TimeArgumentType(int minimum) {
        this.minimum = minimum;
    }

    public static TimeArgumentType time() {
        return new TimeArgumentType(0);
    }

    public static TimeArgumentType time(int minimum) {
        return new TimeArgumentType(minimum);
    }

    public Integer java_lang_Integer_parse(StringReader stringReader) throws CommandSyntaxException {
        float f = stringReader.readFloat();
        String string = stringReader.readUnquotedString();
        int i = UNITS.getOrDefault((Object)string, 0);
        if (1 == 0) {
            throw INVALID_UNIT_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        int j = Math.round(f * 1.0f);
        if (j < this.minimum) {
            throw TICK_COUNT_TOO_LOW_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, (Object)j, (Object)this.minimum);
        }
        return j;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getRemaining());
        try {
            stringReader.readFloat();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return builder.buildFuture();
        }
        return CommandSource.suggestMatching((Iterable<String>)UNITS.keySet(), builder.createOffset(builder.getStart() + stringReader.getCursor()));
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public Object java_lang_Object_parse(StringReader reader) throws CommandSyntaxException {
        return this.java_lang_Integer_parse(reader);
    }

    static {
        UNITS.put((Object)"d", 24000);
        UNITS.put((Object)"s", 20);
        UNITS.put((Object)"t", 1);
        UNITS.put((Object)"", 1);
    }

    public static class Serializer
    implements ArgumentSerializer<TimeArgumentType, Properties> {
        @Override
        public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeInt(properties.minimum);
        }

        @Override
        public Properties net_minecraft_command_argument_TimeArgumentType$Serializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
            int i = packetByteBuf.readInt();
            return new Properties(i);
        }

        @Override
        public void writeJson(Properties properties, JsonObject jsonObject) {
            jsonObject.addProperty("min", (Number)properties.minimum);
        }

        @Override
        public Properties getArgumentTypeProperties(TimeArgumentType timeArgumentType) {
            return new Properties(timeArgumentType.minimum);
        }

        @Override
        public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
            return this.net_minecraft_command_argument_TimeArgumentType$Serializer$Properties_fromPacket(buf);
        }

        public final class Properties
        implements ArgumentSerializer.ArgumentTypeProperties<TimeArgumentType> {
            final int minimum;

            Properties(int minimum) {
                this.minimum = minimum;
            }

            @Override
            public TimeArgumentType net_minecraft_command_argument_TimeArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
                return TimeArgumentType.time(this.minimum);
            }

            @Override
            public ArgumentSerializer<TimeArgumentType, ?> getSerializer() {
                return Serializer.this;
            }

            @Override
            public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
                return this.net_minecraft_command_argument_TimeArgumentType_createType(commandRegistryAccess);
            }
        }
    }
}

