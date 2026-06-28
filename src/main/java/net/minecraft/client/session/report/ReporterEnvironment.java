/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ClientInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$RealmInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ThirdPartyServerInfo
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.RealmsServer;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ReporterEnvironment(String clientVersion, @Nullable Server server) {
    public static ReporterEnvironment ofIntegratedServer() {
        return ReporterEnvironment.ofServer(null);
    }

    public static ReporterEnvironment ofThirdPartyServer(String ip) {
        return ReporterEnvironment.ofServer(new Server.ThirdParty(ip));
    }

    public static ReporterEnvironment ofRealm(RealmsServer server) {
        return ReporterEnvironment.ofServer(new Server.Realm(server));
    }

    public static ReporterEnvironment ofServer(@Nullable Server server) {
        return new ReporterEnvironment(ReporterEnvironment.getVersion(), server);
    }

    public AbuseReportRequest.ClientInfo toClientInfo() {
        return new AbuseReportRequest.ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
    }

    @Nullable
    public AbuseReportRequest.ThirdPartyServerInfo toThirdPartyServerInfo() {
        Server server = this.server;
        if (server instanceof Server.ThirdParty) {
            Server.ThirdParty thirdParty = (Server.ThirdParty)server;
            return new AbuseReportRequest.ThirdPartyServerInfo(thirdParty.ip);
        }
        return null;
    }

    @Nullable
    public AbuseReportRequest.RealmInfo toRealmInfo() {
        Server server = this.server;
        if (server instanceof Server.Realm) {
            Server.Realm realm = (Server.Realm)server;
            return new AbuseReportRequest.RealmInfo(String.valueOf(realm.realmId()), realm.slotId());
        }
        return null;
    }

    private static String getVersion() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1.21.8");
        if (MinecraftClient.getModStatus().isModded()) {
            stringBuilder.append(" (modded)");
        }
        return stringBuilder.toString();
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Server {

        @Environment(value=EnvType.CLIENT)
        public record Realm(long realmId, int slotId) implements Server
        {
            public Realm(RealmsServer server) {
                this(server.id, server.activeSlot);
            }
        }

        @Environment(value=EnvType.CLIENT)
        public static final class ThirdParty
        extends Record
        implements Server {
            final String ip;

            public ThirdParty(String string) {
                this.ip = string;
            }

            @Override
            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this);
            }

            @Override
            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this);
            }

            @Override
            public final boolean equals(Object object) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ThirdParty.class, "ip", "ip"}, this, object);
            }

            public String ip() {
                return this.ip;
            }
        }
    }
}

