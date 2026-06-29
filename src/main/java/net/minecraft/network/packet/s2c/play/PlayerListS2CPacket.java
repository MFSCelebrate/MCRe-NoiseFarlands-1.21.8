/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Multimap
 *  com.mojang.authlib.GameProfile
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    final static public PacketCodec<RegistryByteBuf, PlayerListS2CPacket> CODEC = Packet.createCodec(PlayerListS2CPacket::write, PlayerListS2CPacket::new);
    final private EnumSet<Action> actions;
    final private List<Entry> entries;

    public PlayerListS2CPacket(EnumSet<Action> actions, Collection<ServerPlayerEntity> players) {
        this.actions = actions;
        this.entries = players.stream().map(Entry::new).toList();
    }

    public PlayerListS2CPacket(Action action, ServerPlayerEntity player) {
        this.actions = EnumSet.of(action);
        this.entries = List.of(new Entry(player));
    }

    public static PlayerListS2CPacket entryFromPlayer(Collection<ServerPlayerEntity> players) {
        EnumSet<Action[]> enumSet = EnumSet.of(Action.ADD_PLAYER, new Action[]{Action.INITIALIZE_CHAT, Action.UPDATE_GAME_MODE, Action.UPDATE_LISTED, Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME, Action.UPDATE_HAT, Action.UPDATE_LIST_ORDER});
        return new PlayerListS2CPacket(enumSet, players);
    }

    private PlayerListS2CPacket(RegistryByteBuf buf) {
        this.actions = buf.readEnumSet(Action.class);
        this.entries = buf.readList(buf2 -> {
            Serialized serialized = new Serialized(buf2.readUuid());
            for (Action action : this.actions) {
                action.reader.read(serialized, (RegistryByteBuf)((Object)buf2));
            }
            return serialized.toEntry();
        });
    }

    private void write(RegistryByteBuf buf) {
        buf.writeEnumSet(this.actions, Action.class);
        buf.writeCollection(this.entries, (buf2, entry) -> {
            buf2.writeUuid(entry.profileId());
            for (Action action : this.actions) {
                action.writer.write((RegistryByteBuf)((Object)buf2), (Entry)entry);
            }
        });
    }

    @Override
    public PacketType<PlayerListS2CPacket> getPacketType() {
        return PlayPackets.PLAYER_INFO_UPDATE;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerList(this);
    }

    public EnumSet<Action> getActions() {
        return this.actions;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public List<Entry> getPlayerAdditionEntries() {
        return this.actions.contains((Object)Action.ADD_PLAYER) ? this.entries : List.of();
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("actions", this.actions).add("entries", this.entries).toString();
    }

    public static final class Entry
    extends Record {
        final private UUID profileId;
        @Nullable
        final private GameProfile profile;
        final private boolean listed;
        final private int latency;
        final private GameMode gameMode;
        @Nullable
        final private Text displayName;
        final boolean showHat;
        final int listOrder;
        @Nullable
        final PublicPlayerSession.Serialized chatSession;

        Entry(ServerPlayerEntity player) {
            this(player.getUuid(), player.getGameProfile(), true, player.networkHandler.getLatency(), player.getGameMode(), player.getPlayerListName(), player.isPartVisible(PlayerModelPart.HAT), player.getPlayerListOrder(), Nullables.map(player.getSession(), PublicPlayerSession::toSerialized));
        }

        public Entry(UUID uUID, @Nullable GameProfile gameProfile, boolean bl, int i, GameMode gameMode, @Nullable Text text, boolean bl2, int j, @Nullable PublicPlayerSession.Serialized serialized) {
            this.profileId = uUID;
            this.profile = gameProfile;
            this.listed = bl;
            this.latency = 1;
            this.gameMode = gameMode;
            this.displayName = text;
            this.showHat = bl2;
            this.listOrder = j;
            this.chatSession = serialized;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this, object);
        }

        public UUID profileId() {
            return this.profileId;
        }

        @Nullable
        public GameProfile profile() {
            return this.profile;
        }

        public boolean listed() {
            return this.listed;
        }

        public int latency() {
            return this.latency;
        }

        public GameMode gameMode() {
            return this.gameMode;
        }

        @Nullable
        public Text displayName() {
            return this.displayName;
        }

        public boolean showHat() {
            return this.showHat;
        }

        public int listOrder() {
            return this.listOrder;
        }

        @Nullable
        public PublicPlayerSession.Serialized chatSession() {
            return this.chatSession;
        }
    }

    public static final class Action
    extends Enum<Action> {
        final static public Action ADD_PLAYER = new Action((serialized, buf) -> {
            GameProfile gameProfile = new GameProfile(serialized.profileId, buf.readString(16));
            gameProfile.getProperties().putAll((Multimap)PacketCodecs.PROPERTY_MAP.decode(buf));
            serialized.gameProfile = gameProfile;
        }, (buf, entry) -> {
            GameProfile gameProfile = Objects.requireNonNull(entry.profile());
            buf.writeString(gameProfile.getName(), 16);
            PacketCodecs.PROPERTY_MAP.encode(buf, gameProfile.getProperties());
        });
        final static public Action INITIALIZE_CHAT = new Action((serialized, buf) -> {
            serialized.session = buf.readNullable(PublicPlayerSession.Serialized::fromBuf);
        }, (buf, entry) -> buf.writeNullable(entry.chatSession, PublicPlayerSession.Serialized::write));
        final static public Action UPDATE_GAME_MODE = new Action((serialized, buf) -> {
            serialized.gameMode = GameMode.byIndex(buf.readVarInt());
        }, (buf, entry) -> buf.writeVarInt(entry.gameMode().getIndex()));
        final static public Action UPDATE_LISTED = new Action((serialized, buf) -> {
            serialized.listed = buf.readBoolean();
        }, (buf, entry) -> buf.net_minecraft_network_PacketByteBuf_writeBoolean(entry.listed()));
        final static public Action UPDATE_LATENCY = new Action((serialized, buf) -> {
            serialized.latency = buf.readVarInt();
        }, (buf, entry) -> buf.writeVarInt(entry.latency()));
        final static public Action UPDATE_DISPLAY_NAME = new Action((serialized, buf) -> {
            serialized.displayName = PacketByteBuf.readNullable(buf, TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC);
        }, (buf, entry) -> PacketByteBuf.writeNullable(buf, entry.displayName(), TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC));
        final static public Action UPDATE_LIST_ORDER = new Action((serialized, buf) -> {
            serialized.listOrder = buf.readVarInt();
        }, (buf, entry) -> buf.writeVarInt(entry.listOrder));
        final static public Action UPDATE_HAT = new Action((serialized, buf) -> {
            serialized.showHat = buf.readBoolean();
        }, (buf, entry) -> buf.net_minecraft_network_PacketByteBuf_writeBoolean(entry.showHat));
        final Reader reader;
        final Writer writer;
        final static private Action[] field_29141;

        public static Action[] values() {
            return (Action[])field_29141.clone();
        }

        public static Action valueOf(String string) {
            return Enum.valueOf(Action.class, string);
        }

        private Action(Reader reader, Writer writer) {
            this.reader = reader;
            this.writer = writer;
        }

        private static Action[] method_36951() {
            return new Action[]{ADD_PLAYER, INITIALIZE_CHAT, UPDATE_GAME_MODE, UPDATE_LISTED, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, UPDATE_LIST_ORDER, UPDATE_HAT};
        }

        static {
            field_29141 = Action.method_36951();
        }

        public static interface Reader {
            public void read(Serialized var1, RegistryByteBuf var2);
        }

        public static interface Writer {
            public void write(RegistryByteBuf var1, Entry var2);
        }
    }

    static class Serialized {
        final UUID profileId;
        @Nullable
        GameProfile gameProfile;
        boolean listed;
        int latency;
        GameMode gameMode = GameMode.DEFAULT;
        @Nullable
        Text displayName;
        boolean showHat;
        int listOrder;
        @Nullable
        PublicPlayerSession.Serialized session;

        Serialized(UUID profileId) {
            this.profileId = profileId;
        }

        Entry toEntry() {
            return new Entry(this.profileId, this.gameProfile, this.listed, this.latency, this.gameMode, this.displayName, this.showHat, this.listOrder, this.session);
        }
    }
}

