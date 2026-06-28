/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report.log;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public interface ReceivedMessage
extends ChatLogEntry {
    public static ChatMessage of(GameProfile gameProfile, SignedMessage message, MessageTrustStatus trustStatus) {
        return new ChatMessage(gameProfile, message, trustStatus);
    }

    public static GameMessage of(Text message, Instant timestamp) {
        return new GameMessage(message, timestamp);
    }

    public Text getContent();

    default public Text getNarration() {
        return this.getContent();
    }

    public boolean isSentFrom(UUID var1);

    @Environment(value=EnvType.CLIENT)
    public record ChatMessage(GameProfile profile, SignedMessage message, MessageTrustStatus trustStatus) implements ReceivedMessage
    {
        final static public MapCodec<ChatMessage> CHAT_MESSAGE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.GAME_PROFILE_WITH_PROPERTIES.fieldOf("profile").forGetter(ChatMessage::profile), (App)SignedMessage.CODEC.forGetter(ChatMessage::message), (App)MessageTrustStatus.CODEC.optionalFieldOf("trust_level", (Object)MessageTrustStatus.SECURE).forGetter(ChatMessage::trustStatus)).apply((Applicative)instance, ChatMessage::new));
        final static private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        @Override
        public Text getContent() {
            if (!this.message.filterMask().isPassThrough()) {
                Text text = this.message.filterMask().getFilteredText(this.message.getSignedContent());
                return text != null ? text : Text.empty();
            }
            return this.message.getContent();
        }

        @Override
        public Text getNarration() {
            Text text = this.getContent();
            Text text2 = this.getFormattedTimestamp();
            return Text.translatable("gui.chatSelection.message.narrate", this.profile.getName(), text, text2);
        }

        public Text getHeadingText() {
            Text text = this.getFormattedTimestamp();
            return Text.translatable("gui.chatSelection.heading", this.profile.getName(), text);
        }

        private Text getFormattedTimestamp() {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(this.message.getTimestamp(), ZoneOffset.systemDefault());
            return Text.literal(localDateTime.format(DATE_TIME_FORMATTER)).formatted(Formatting.ITALIC, Formatting.GRAY);
        }

        @Override
        public boolean isSentFrom(UUID uuid) {
            return this.message.canVerifyFrom(uuid);
        }

        public UUID getSenderUuid() {
            return this.profile.getId();
        }

        @Override
        public ChatLogEntry.Type getType() {
            return ChatLogEntry.Type.PLAYER;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChatMessage.class, "profile;message;trustLevel", "profile", "message", "trustStatus"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChatMessage.class, "profile;message;trustLevel", "profile", "message", "trustStatus"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChatMessage.class, "profile;message;trustLevel", "profile", "message", "trustStatus"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record GameMessage(Text message, Instant timestamp) implements ReceivedMessage
    {
        final static public MapCodec<GameMessage> GAME_MESSAGE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TextCodecs.CODEC.fieldOf("message").forGetter(GameMessage::message), (App)Codecs.INSTANT.fieldOf("time_stamp").forGetter(GameMessage::timestamp)).apply((Applicative)instance, GameMessage::new));

        @Override
        public Text getContent() {
            return this.message;
        }

        @Override
        public boolean isSentFrom(UUID uuid) {
            return false;
        }

        @Override
        public ChatLogEntry.Type getType() {
            return ChatLogEntry.Type.SYSTEM;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{GameMessage.class, "message;timeStamp", "message", "timestamp"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GameMessage.class, "message;timeStamp", "message", "timestamp"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GameMessage.class, "message;timeStamp", "message", "timestamp"}, this, object);
        }
    }
}

