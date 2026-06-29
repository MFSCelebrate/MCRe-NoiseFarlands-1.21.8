/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report.log;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public interface ChatLogEntry {
    final static public Codec<ChatLogEntry> CODEC = StringIdentifiable.createCodec(Type::values).dispatch(ChatLogEntry::getType, Type::getCodec);

    public Type getType();

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type>
    implements StringIdentifiable {
        final static public Type PLAYER = new Type("player", () -> ReceivedMessage.ChatMessage.CHAT_MESSAGE_CODEC);
        final static public Type SYSTEM = new Type("system", () -> ReceivedMessage.GameMessage.GAME_MESSAGE_CODEC);
        final private String id;
        final private Supplier<MapCodec<? extends ChatLogEntry>> codecSupplier;
        final static private Type[] field_40808;

        public static Type[] values() {
            return (Type[])field_40808.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private Type(String id, Supplier<MapCodec<? extends ChatLogEntry>> codecSupplier) {
            this.id = id;
            this.codecSupplier = codecSupplier;
        }

        private MapCodec<? extends ChatLogEntry> getCodec() {
            return this.codecSupplier.get();
        }

        @Override
        public String asString() {
            return this.id;
        }

        private static Type[] method_46542() {
            return new Type[]{PLAYER, SYSTEM};
        }

        static {
            field_40808 = Type.method_46542();
        }
    }
}

