/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.PngMetadata;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ServerInfo {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int MAX_FAVICON_SIZE = 1024;
    public String name;
    public String address;
    public Text playerCountLabel;
    public Text label;
    @Nullable
    public ServerMetadata.Players players;
    public long ping;
    public int protocolVersion = SharedConstants.getGameVersion().protocolVersion();
    public Text version = Text.literal(SharedConstants.getGameVersion().name());
    public List<Text> playerListSummary = Collections.emptyList();
    private ResourcePackPolicy resourcePackPolicy = ResourcePackPolicy.PROMPT;
    @Nullable
    private byte[] favicon;
    private ServerType serverType;
    private Status status = Status.INITIAL;

    public ServerInfo(String name, String address, ServerType serverType) {
        this.name = name;
        this.address = address;
        this.serverType = serverType;
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("name", this.name);
        nbtCompound.putString("ip", this.address);
        nbtCompound.putNullable("icon", Codecs.BASE_64, this.favicon);
        nbtCompound.copyFromCodec(ResourcePackPolicy.CODEC, this.resourcePackPolicy);
        return nbtCompound;
    }

    public ResourcePackPolicy getResourcePackPolicy() {
        return this.resourcePackPolicy;
    }

    public void setResourcePackPolicy(ResourcePackPolicy resourcePackPolicy) {
        this.resourcePackPolicy = resourcePackPolicy;
    }

    public static ServerInfo fromNbt(NbtCompound root) {
        ServerInfo serverInfo = new ServerInfo(root.getString("name", ""), root.getString("ip", ""), ServerType.OTHER);
        serverInfo.setFavicon(root.get("icon", Codecs.BASE_64).orElse(null));
        serverInfo.setResourcePackPolicy(root.decode(ResourcePackPolicy.CODEC).orElse(ResourcePackPolicy.PROMPT));
        return serverInfo;
    }

    @Nullable
    public byte[] getFavicon() {
        return this.favicon;
    }

    public void setFavicon(@Nullable byte[] favicon) {
        this.favicon = favicon;
    }

    public boolean isLocal() {
        return this.serverType == ServerType.LAN;
    }

    public boolean isRealm() {
        return this.serverType == ServerType.REALM;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public void copyFrom(ServerInfo serverInfo) {
        this.address = serverInfo.address;
        this.name = serverInfo.name;
        this.favicon = serverInfo.favicon;
    }

    public void copyWithSettingsFrom(ServerInfo serverInfo) {
        this.copyFrom(serverInfo);
        this.setResourcePackPolicy(serverInfo.getResourcePackPolicy());
        this.serverType = serverInfo.serverType;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Nullable
    public static byte[] validateFavicon(@Nullable byte[] favicon) {
        if (favicon != null) {
            try {
                PngMetadata pngMetadata = PngMetadata.fromBytes(favicon);
                if (pngMetadata.width() <= 1024 && pngMetadata.height() <= 1024) {
                    return favicon;
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to decode server icon", (Throwable)iOException);
            }
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ResourcePackPolicy
    extends Enum<ResourcePackPolicy> {
        final static public ResourcePackPolicy ENABLED = new ResourcePackPolicy("enabled");
        final static public ResourcePackPolicy DISABLED = new ResourcePackPolicy("disabled");
        final static public ResourcePackPolicy PROMPT = new ResourcePackPolicy("prompt");
        final static public MapCodec<ResourcePackPolicy> CODEC;
        final private Text name;
        final static private ResourcePackPolicy[] RESOURCE_PACK_POLICIES;

        public static ResourcePackPolicy[] values() {
            return (ResourcePackPolicy[])RESOURCE_PACK_POLICIES.clone();
        }

        public static ResourcePackPolicy valueOf(String string) {
            return Enum.valueOf(ResourcePackPolicy.class, string);
        }

        private ResourcePackPolicy(String name) {
            this.name = Text.translatable("addServer.resourcePack." + name);
        }

        public Text getName() {
            return this.name;
        }

        private static ResourcePackPolicy[] method_36896() {
            return new ResourcePackPolicy[]{ENABLED, DISABLED, PROMPT};
        }

        static {
            RESOURCE_PACK_POLICIES = ResourcePackPolicy.method_36896();
            CODEC = Codec.BOOL.optionalFieldOf("acceptTextures").xmap(value -> value.map(acceptTextures -> acceptTextures != false ? ENABLED : DISABLED).orElse(PROMPT), value -> switch (value.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> Optional.of(true);
                case 1 -> Optional.of(false);
                case 2 -> Optional.empty();
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Status
    extends Enum<Status> {
        final static public Status INITIAL = new Status();
        final static public Status PINGING = new Status();
        final static public Status UNREACHABLE = new Status();
        final static public Status INCOMPATIBLE = new Status();
        final static public Status SUCCESSFUL = new Status();
        final static private Status[] field_47885;

        public static Status[] values() {
            return (Status[])field_47885.clone();
        }

        public static Status valueOf(String string) {
            return Enum.valueOf(Status.class, string);
        }

        private static Status[] method_55826() {
            return new Status[]{INITIAL, PINGING, UNREACHABLE, INCOMPATIBLE, SUCCESSFUL};
        }

        static {
            field_47885 = Status.method_55826();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class ServerType
    extends Enum<ServerType> {
        final static public ServerType LAN = new ServerType();
        final static public ServerType REALM = new ServerType();
        final static public ServerType OTHER = new ServerType();
        final static private ServerType[] field_45612;

        public static ServerType[] values() {
            return (ServerType[])field_45612.clone();
        }

        public static ServerType valueOf(String string) {
            return Enum.valueOf(ServerType.class, string);
        }

        private static ServerType[] method_52812() {
            return new ServerType[]{LAN, REALM, OTHER};
        }

        static {
            field_45612 = ServerType.method_52812();
        }
    }
}

