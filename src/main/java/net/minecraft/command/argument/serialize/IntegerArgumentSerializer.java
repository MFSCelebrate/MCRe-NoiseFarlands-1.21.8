/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class IntegerArgumentSerializer
implements ArgumentSerializer<IntegerArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != Integer.MIN_VALUE;
        boolean bl2 = properties.max != Integer.MAX_VALUE;
        packetByteBuf.net_minecraft_network_PacketByteBuf_writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeInt(properties.min);
        }
        if (bl2) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeInt(properties.max);
        }
    }

    @Override
    public Properties net_minecraft_command_argument_serialize_IntegerArgumentSerializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        int i = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
        int j = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
        return new Properties(1, j);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != Integer.MIN_VALUE) {
            jsonObject.addProperty("min", (Number)properties.min);
        }
        if (properties.max != Integer.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)properties.max);
        }
    }

    @Override
    public Properties getArgumentTypeProperties(IntegerArgumentType integerArgumentType) {
        return new Properties(integerArgumentType.getMinimum(), integerArgumentType.getMaximum());
    }

    @Override
    public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
        return this.net_minecraft_command_argument_serialize_IntegerArgumentSerializer$Properties_fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<IntegerArgumentType> {
        final int min;
        final int max;

        Properties(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public IntegerArgumentType com_mojang_brigadier_arguments_IntegerArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return IntegerArgumentType.integer((int)this.min, (int)this.max);
        }

        @Override
        public ArgumentSerializer<IntegerArgumentType, ?> getSerializer() {
            return IntegerArgumentSerializer.this;
        }

        @Override
        public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return this.com_mojang_brigadier_arguments_IntegerArgumentType_createType(commandRegistryAccess);
        }
    }
}

