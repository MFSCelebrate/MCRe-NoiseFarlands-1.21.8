/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.DontSerialize;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServer
extends ValueObject
implements RealmsSerializable {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int NO_PARENT = -1;
    final static public Text REALM_CLOSED_TEXT = Text.translatable("mco.play.button.realm.closed");
    @SerializedName(value="id")
    public long id = -1L;
    @Nullable
    @SerializedName(value="remoteSubscriptionId")
    public String remoteSubscriptionId;
    @Nullable
    @SerializedName(value="name")
    public String name;
    @SerializedName(value="motd")
    public String description = "";
    @SerializedName(value="state")
    public State state = State.CLOSED;
    @Nullable
    @SerializedName(value="owner")
    public String owner;
    @SerializedName(value="ownerUUID")
    @JsonAdapter(value=UUIDTypeAdapter.class)
    public UUID ownerUUID = Util.NIL_UUID;
    @SerializedName(value="players")
    public List<PlayerInfo> players = Lists.newArrayList();
    @SerializedName(value="slots")
    private List<RealmsSlot> emptySlots = RealmsServer.getEmptySlots();
    @DontSerialize
    public Map<Integer, RealmsSlot> slots = new HashMap<Integer, RealmsSlot>();
    @SerializedName(value="expired")
    public boolean expired;
    @SerializedName(value="expiredTrial")
    public boolean expiredTrial = false;
    @SerializedName(value="daysLeft")
    public int daysLeft;
    @SerializedName(value="worldType")
    public WorldType worldType = WorldType.NORMAL;
    @SerializedName(value="isHardcore")
    public boolean hardcore = false;
    @SerializedName(value="gameMode")
    public int gameMode = -1;
    @SerializedName(value="activeSlot")
    public int activeSlot = -1;
    @Nullable
    @SerializedName(value="minigameName")
    public String minigameName;
    @SerializedName(value="minigameId")
    public int minigameId = -1;
    @Nullable
    @SerializedName(value="minigameImage")
    public String minigameImage;
    @SerializedName(value="parentWorldId")
    public long parentWorldId = -1L;
    @Nullable
    @SerializedName(value="parentWorldName")
    public String parentWorldName;
    @SerializedName(value="activeVersion")
    public String activeVersion = "";
    @SerializedName(value="compatibility")
    public Compatibility compatibility = Compatibility.UNVERIFIABLE;
    @Nullable
    @SerializedName(value="regionSelectionPreference")
    public RealmsRegionSelectionPreference regionSelectionPreference;

    public String getDescription() {
        return this.description;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getMinigameName() {
        return this.minigameName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static RealmsServer parse(CheckedGson gson, String json) {
        try {
            RealmsServer realmsServer = gson.fromJson(json, RealmsServer.class);
            if (realmsServer == null) {
                LOGGER.error("Could not parse McoServer: {}", (Object)json);
                return new RealmsServer();
            }
            RealmsServer.replaceNullsWithDefaults(realmsServer);
            return realmsServer;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServer: {}", (Object)exception.getMessage());
            return new RealmsServer();
        }
    }

    public static void replaceNullsWithDefaults(RealmsServer server) {
        if (server.players == null) {
            server.players = Lists.newArrayList();
        }
        if (server.emptySlots == null) {
            server.emptySlots = RealmsServer.getEmptySlots();
        }
        if (server.slots == null) {
            server.slots = new HashMap<Integer, RealmsSlot>();
        }
        if (server.worldType == null) {
            server.worldType = WorldType.NORMAL;
        }
        if (server.activeVersion == null) {
            server.activeVersion = "";
        }
        if (server.compatibility == null) {
            server.compatibility = Compatibility.UNVERIFIABLE;
        }
        if (server.regionSelectionPreference == null) {
            server.regionSelectionPreference = RealmsRegionSelectionPreference.DEFAULT;
        }
        RealmsServer.sortInvited(server);
        RealmsServer.populateSlots(server);
    }

    private static void sortInvited(RealmsServer server) {
        server.players.sort((a, b) -> ComparisonChain.start().compareFalseFirst(b.isAccepted(), a.isAccepted()).compare((Comparable)((Object)a.getName().toLowerCase(Locale.ROOT)), (Comparable)((Object)b.getName().toLowerCase(Locale.ROOT))).result());
    }

    private static void populateSlots(RealmsServer server) {
        server.emptySlots.forEach(slot -> realmsServer.slots.put(slot.slotId, (RealmsSlot)slot));
        for (int i = 1; i <= 3; ++i) {
            if (server.slots.containsKey(i)) continue;
            server.slots.put(i, RealmsSlot.create(i));
        }
    }

    private static List<RealmsSlot> getEmptySlots() {
        ArrayList<RealmsSlot> list = new ArrayList<RealmsSlot>();
        list.add(RealmsSlot.create(1));
        list.add(RealmsSlot.create(2));
        list.add(RealmsSlot.create(3));
        return list;
    }

    public boolean isCompatible() {
        return this.compatibility.isCompatible();
    }

    public boolean needsUpgrade() {
        return this.compatibility.needsUpgrade();
    }

    public boolean needsDowngrade() {
        return this.compatibility.needsDowngrade();
    }

    public boolean shouldAllowPlay() {
        boolean bl = !this.expired && this.state == State.OPEN;
        return bl && (this.isCompatible() || this.needsUpgrade() || this.isPlayerOwner());
    }

    private boolean isPlayerOwner() {
        return MinecraftClient.getInstance().uuidEquals(this.ownerUUID);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.description, this.state, this.owner, this.expired});
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer realmsServer = (RealmsServer)o;
        return new EqualsBuilder().append(this.id, realmsServer.id).append((Object)this.name, (Object)realmsServer.name).append((Object)this.description, (Object)realmsServer.description).append((Object)this.state, (Object)realmsServer.state).append((Object)this.owner, (Object)realmsServer.owner).append(this.expired, realmsServer.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer net_minecraft_client_realms_dto_RealmsServer_clone() {
        RealmsServer realmsServer = new RealmsServer();
        realmsServer.id = this.id;
        realmsServer.remoteSubscriptionId = this.remoteSubscriptionId;
        realmsServer.name = this.name;
        realmsServer.description = this.description;
        realmsServer.state = this.state;
        realmsServer.owner = this.owner;
        realmsServer.players = this.players;
        realmsServer.emptySlots = this.emptySlots.stream().map(RealmsSlot::clone).toList();
        realmsServer.slots = this.cloneSlots(this.slots);
        realmsServer.expired = this.expired;
        realmsServer.expiredTrial = this.expiredTrial;
        realmsServer.daysLeft = this.daysLeft;
        realmsServer.worldType = this.worldType;
        realmsServer.hardcore = this.hardcore;
        realmsServer.gameMode = this.gameMode;
        realmsServer.ownerUUID = this.ownerUUID;
        realmsServer.minigameName = this.minigameName;
        realmsServer.activeSlot = this.activeSlot;
        realmsServer.minigameId = this.minigameId;
        realmsServer.minigameImage = this.minigameImage;
        realmsServer.parentWorldName = this.parentWorldName;
        realmsServer.parentWorldId = this.parentWorldId;
        realmsServer.activeVersion = this.activeVersion;
        realmsServer.compatibility = this.compatibility;
        realmsServer.regionSelectionPreference = this.regionSelectionPreference != null ? this.regionSelectionPreference.net_minecraft_client_realms_dto_RealmsRegionSelectionPreference_clone() : null;
        return realmsServer;
    }

    public Map<Integer, RealmsSlot> cloneSlots(Map<Integer, RealmsSlot> slots) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry<Integer, RealmsSlot> entry : slots.entrySet()) {
            map.put(entry.getKey(), new RealmsSlot(entry.getKey(), entry.getValue().options.net_minecraft_client_realms_dto_RealmsWorldOptions_clone(), entry.getValue().settings));
        }
        return map;
    }

    public boolean isPrerelease() {
        return this.parentWorldId != -1L;
    }

    public boolean isMinigame() {
        return this.worldType == WorldType.MINIGAME;
    }

    public String getWorldName(int slotId) {
        if (this.name == null) {
            return this.slots.get((Object)Integer.valueOf((int)slotId)).options.getSlotName(slotId);
        }
        return this.name + " (" + this.slots.get((Object)Integer.valueOf((int)slotId)).options.getSlotName(slotId) + ")";
    }

    public ServerInfo createServerInfo(String address) {
        return new ServerInfo(Objects.requireNonNullElse(this.name, "unknown server"), address, ServerInfo.ServerType.REALM);
    }

    public Object java_lang_Object_clone() throws CloneNotSupportedException {
        return this.net_minecraft_client_realms_dto_RealmsServer_clone();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class State
    extends Enum<State> {
        final static public State CLOSED = new State();
        final static public State OPEN = new State();
        final static public State UNINITIALIZED = new State();
        final static private State[] field_19436;

        public static State[] values() {
            return (State[])field_19436.clone();
        }

        public static State valueOf(String name) {
            return Enum.valueOf(State.class, name);
        }

        private static State[] method_36848() {
            return new State[]{CLOSED, OPEN, UNINITIALIZED};
        }

        static {
            field_19436 = State.method_36848();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class WorldType
    extends Enum<WorldType> {
        final static public WorldType NORMAL = new WorldType();
        final static public WorldType MINIGAME = new WorldType();
        final static public WorldType ADVENTUREMAP = new WorldType();
        final static public WorldType EXPERIENCE = new WorldType();
        final static public WorldType INSPIRATION = new WorldType();
        final static private WorldType[] field_19442;

        public static WorldType[] values() {
            return (WorldType[])field_19442.clone();
        }

        public static WorldType valueOf(String name) {
            return Enum.valueOf(WorldType.class, name);
        }

        private static WorldType[] method_36849() {
            return new WorldType[]{NORMAL, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
        }

        static {
            field_19442 = WorldType.method_36849();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Compatibility
    extends Enum<Compatibility> {
        final static public Compatibility UNVERIFIABLE = new Compatibility();
        final static public Compatibility INCOMPATIBLE = new Compatibility();
        final static public Compatibility RELEASE_TYPE_INCOMPATIBLE = new Compatibility();
        final static public Compatibility NEEDS_DOWNGRADE = new Compatibility();
        final static public Compatibility NEEDS_UPGRADE = new Compatibility();
        final static public Compatibility COMPATIBLE = new Compatibility();
        final static private Compatibility[] field_46702;

        public static Compatibility[] values() {
            return (Compatibility[])field_46702.clone();
        }

        public static Compatibility valueOf(String string) {
            return Enum.valueOf(Compatibility.class, string);
        }

        public boolean isCompatible() {
            return this == COMPATIBLE;
        }

        public boolean needsUpgrade() {
            return this == NEEDS_UPGRADE;
        }

        public boolean needsDowngrade() {
            return this == NEEDS_DOWNGRADE;
        }

        private static Compatibility[] method_54368() {
            return new Compatibility[]{UNVERIFIABLE, INCOMPATIBLE, RELEASE_TYPE_INCOMPATIBLE, NEEDS_DOWNGRADE, NEEDS_UPGRADE, COMPATIBLE};
        }

        static {
            field_46702 = Compatibility.method_54368();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class McoServerComparator
    implements Comparator<RealmsServer> {
        final private String refOwner;

        public McoServerComparator(String owner) {
            this.refOwner = owner;
        }

        @Override
        public int compare(RealmsServer realmsServer, RealmsServer realmsServer2) {
            return ComparisonChain.start().compareTrueFirst(realmsServer.isPrerelease(), realmsServer2.isPrerelease()).compareTrueFirst(realmsServer.state == State.UNINITIALIZED, realmsServer2.state == State.UNINITIALIZED).compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial).compareTrueFirst(Objects.equals(realmsServer.owner, this.refOwner), Objects.equals(realmsServer2.owner, this.refOwner)).compareFalseFirst(realmsServer.expired, realmsServer2.expired).compareTrueFirst(realmsServer.state == State.OPEN, realmsServer2.state == State.OPEN).compare(realmsServer.id, realmsServer2.id).result();
        }

        @Override
        public int compare(Object one, Object two) {
            return this.compare((RealmsServer)one, (RealmsServer)two);
        }
    }
}

