/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class LongArgumentSerializer
implements ArgumentSerializer<LongArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != Long.MIN_VALUE;
        boolean bl2 = properties.max != Long.MAX_VALUE;
        packetByteBuf.net_minecraft_network_PacketByteBuf_writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeLong(properties.min);
        }
        if (bl2) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeLong(properties.max);
        }
    }

    @Override
    public Properties net_minecraft_command_argument_serialize_LongArgumentSerializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        long l = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readLong() : Long.MIN_VALUE;
        long m = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readLong() : Long.MAX_VALUE;
        return new Properties(l, m);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != Long.MIN_VALUE) {
            jsonObject.addProperty("min", (Number)properties.min);
        }
        if (properties.max != Long.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)properties.max);
        }
    }

    @Override
    public Properties getArgumentTypeProperties(LongArgumentType longArgumentType) {
        return new Properties(longArgumentType.getMinimum(), longArgumentType.getMaximum());
    }

    @Override
    public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
        return this.net_minecraft_command_argument_serialize_LongArgumentSerializer$Properties_fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<LongArgumentType> {
        final long min;
        final long max;

        Properties(long min, long max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public LongArgumentType com_mojang_brigadier_arguments_LongArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return LongArgumentType.longArg((long)this.min, (long)this.max);
        }

        @Override
        public ArgumentSerializer<LongArgumentType, ?> getSerializer() {
            return LongArgumentSerializer.this;
        }

        @Override
        public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return this.com_mojang_brigadier_arguments_LongArgumentType_createType(commandRegistryAccess);
        }
    }
}

