/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class DoubleArgumentSerializer
implements ArgumentSerializer<DoubleArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != -1.7976931348623157E308;
        boolean bl2 = properties.max != Double.MAX_VALUE;
        packetByteBuf.net_minecraft_network_PacketByteBuf_writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeDouble(properties.min);
        }
        if (bl2) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeDouble(properties.max);
        }
    }

    @Override
    public Properties net_minecraft_command_argument_serialize_DoubleArgumentSerializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        double d = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readDouble() : -1.7976931348623157E308;
        double e = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
        return new Properties(d, e);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != -1.7976931348623157E308) {
            jsonObject.addProperty("min", (Number)properties.min);
        }
        if (properties.max != Double.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)properties.max);
        }
    }

    @Override
    public Properties getArgumentTypeProperties(DoubleArgumentType doubleArgumentType) {
        return new Properties(doubleArgumentType.getMinimum(), doubleArgumentType.getMaximum());
    }

    @Override
    public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
        return this.net_minecraft_command_argument_serialize_DoubleArgumentSerializer$Properties_fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<DoubleArgumentType> {
        final double min;
        final double max;

        Properties(double min, double max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public DoubleArgumentType com_mojang_brigadier_arguments_DoubleArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return DoubleArgumentType.doubleArg((double)this.min, (double)this.max);
        }

        @Override
        public ArgumentSerializer<DoubleArgumentType, ?> getSerializer() {
            return DoubleArgumentSerializer.this;
        }

        @Override
        public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return this.com_mojang_brigadier_arguments_DoubleArgumentType_createType(commandRegistryAccess);
        }
    }
}

