/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class ConstantArgumentSerializer<A extends ArgumentType<?>>
implements ArgumentSerializer<A, Properties> {
    final private Properties properties;

    private ConstantArgumentSerializer(Function<CommandRegistryAccess, A> typeSupplier) {
        this.properties = new Properties(typeSupplier);
    }

    public static <T extends ArgumentType<?>> ConstantArgumentSerializer<T> of(Supplier<T> typeSupplier) {
        return new ConstantArgumentSerializer<ArgumentType>(commandRegistryAccess -> (ArgumentType)typeSupplier.get());
    }

    public static <T extends ArgumentType<?>> ConstantArgumentSerializer<T> of(Function<CommandRegistryAccess, T> typeSupplier) {
        return new ConstantArgumentSerializer<T>(typeSupplier);
    }

    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
    }

    @Override
    public Properties net_minecraft_command_argument_serialize_ConstantArgumentSerializer$Properties_fromPacket(PacketByteBuf packetByteBuf) {
        return this.properties;
    }

    @Override
    public Properties getArgumentTypeProperties(A argumentType) {
        return this.properties;
    }

    @Override
    public ArgumentSerializer.ArgumentTypeProperties getArgumentTypeProperties(ArgumentType argumentType) {
        return this.getArgumentTypeProperties(argumentType);
    }

    @Override
    public ArgumentSerializer.ArgumentTypeProperties net_minecraft_command_argument_serialize_ArgumentSerializer$ArgumentTypeProperties_fromPacket(PacketByteBuf buf) {
        return this.net_minecraft_command_argument_serialize_ConstantArgumentSerializer$Properties_fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<A> {
        final private Function<CommandRegistryAccess, A> typeSupplier;

        public Properties(Function<CommandRegistryAccess, A> typeSupplier) {
            this.typeSupplier = typeSupplier;
        }

        @Override
        public A createType(CommandRegistryAccess commandRegistryAccess) {
            return (ArgumentType)this.typeSupplier.apply(commandRegistryAccess);
        }

        @Override
        public ArgumentSerializer<A, ?> getSerializer() {
            return ConstantArgumentSerializer.this;
        }
    }
}

