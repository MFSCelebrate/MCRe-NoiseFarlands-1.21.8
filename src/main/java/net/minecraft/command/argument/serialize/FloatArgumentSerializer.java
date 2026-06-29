/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class FloatArgumentSerializer
implements ArgumentSerializer<FloatArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != -3.4028235E38f;
        boolean bl2 = properties.max != Float.MAX_VALUE;
        packetByteBuf.net_minecraft_network_PacketByteBuf_writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeFloat(properties.min);
        }
        if (bl2) {
            packetByteBuf.net_minecraft_network_PacketByteBuf_writeFloat(properties.max);
        }
    }

    @Override
    public Properties net_minecraft_command_argument_serialize_FloatArgumentSerializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        float f = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readFloat() : -3.4028235E38f;
        float g = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
        return new Properties(f, g);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != -3.4028235E38f) {
            jsonObject.addProperty("min", (Number)Float.valueOf(properties.min));
        }
        if (properties.max != Float.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)Float.valueOf(properties.max));
        }
    }

    @Override
    public Properties getArgumentTypeProperties(FloatArgumentType floatArgumentType) {
        return new Properties(floatArgumentType.getMinimum(), floatArgumentType.getMaximum());
    }

    @Override
    public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
        return this.net_minecraft_command_argument_serialize_FloatArgumentSerializer$Properties_fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<FloatArgumentType> {
        final float min;
        final float max;

        Properties(float min, float max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public FloatArgumentType com_mojang_brigadier_arguments_FloatArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return FloatArgumentType.floatArg((float)this.min, (float)this.max);
        }

        @Override
        public ArgumentSerializer<FloatArgumentType, ?> getSerializer() {
            return FloatArgumentSerializer.this;
        }

        @Override
        public ArgumentType com_mojang_brigadier_arguments_ArgumentType_createType(CommandRegistryAccess commandRegistryAccess) {
            return this.com_mojang_brigadier_arguments_FloatArgumentType_createType(commandRegistryAccess);
        }
    }
}

