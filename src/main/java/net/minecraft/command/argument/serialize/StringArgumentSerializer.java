/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType$StringType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class StringArgumentSerializer
implements ArgumentSerializer<StringArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeEnumConstant((Enum<?>)properties.type);
    }

    @Override
    public Properties net_minecraft_command_argument_serialize_StringArgumentSerializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
        StringArgumentType.StringType stringType = packetByteBuf.readEnumConstant(StringArgumentType.StringType.class);
        return new Properties(stringType);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        jsonObject.addProperty("type", switch (properties.type) {
            default -> throw new MatchException(null, null);
            case StringArgumentType.StringType.SINGLE_WORD -> "word";
            case StringArgumentType.StringType.QUOTABLE_PHRASE -> "phrase";
            case StringArgumentType.StringType.GREEDY_PHRASE -> "greedy";
        });
    }

    @Override
    public Properties getArgumentTypeProperties(StringArgumentType stringArgumentType) {
        return new Properties(stringArgumentType.getType());
    }

    @Override
    public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
        return this.net_minecraft_command_argument_serialize_StringArgumentSerializer$Properties_fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<StringArgumentType> {
        final StringArgumentType.StringType type;

        public Properties(StringArgumentType.StringType type) {
            this.type = type;
        }

        @Override
        public StringArgumentType com_mojang_brigadier_arguments_StringArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return switch (this.type) {
                default -> throw new MatchException(null, null);
                case StringArgumentType.StringType.SINGLE_WORD -> StringArgumentType.word();
                case StringArgumentType.StringType.QUOTABLE_PHRASE -> StringArgumentType.string();
                case StringArgumentType.StringType.GREEDY_PHRASE -> StringArgumentType.greedyString();
            };
        }

        @Override
        public ArgumentSerializer<StringArgumentType, ?> getSerializer() {
            return StringArgumentSerializer.this;
        }

        @Override
        public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return this.com_mojang_brigadier_arguments_StringArgumentType_createType(commandRegistryAccess);
        }
    }
}

