/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.net.HostAndPort
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.net.HostAndPort;
import com.mojang.logging.LogUtils;
import java.net.IDN;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public final class ServerAddress {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private HostAndPort hostAndPort;
    final static private ServerAddress INVALID = new ServerAddress(HostAndPort.fromParts((String)"server.invalid", 25565));

    public ServerAddress(String host, int port) {
        this(HostAndPort.fromParts((String)host, (int)port));
    }

    private ServerAddress(HostAndPort hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public String getAddress() {
        try {
            return IDN.toASCII(this.hostAndPort.getHost());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return "";
        }
    }

    public int getPort() {
        return this.hostAndPort.getPort();
    }

    public static ServerAddress parse(String address) {
        if (address == null) {
            return INVALID;
        }
        try {
            HostAndPort hostAndPort = HostAndPort.fromString((String)address).withDefaultPort(25565);
            if (hostAndPort.getHost().isEmpty()) {
                return INVALID;
            }
            return new ServerAddress(hostAndPort);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.info("Failed to parse URL {}", (Object)address, (Object)illegalArgumentException);
            return INVALID;
        }
    }

    public static boolean isValid(String address) {
        try {
            HostAndPort hostAndPort = HostAndPort.fromString((String)address);
            String string = hostAndPort.getHost();
            if (!string.isEmpty()) {
                IDN.toASCII(string);
                return true;
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return false;
    }

    static int portOrDefault(String port) {
        try {
            return Integer.parseInt(port.trim());
        }
        catch (Exception exception) {
            return 25565;
        }
    }

    public String toString() {
        return this.hostAndPort.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ServerAddress) {
            return this.hostAndPort.equals((Object)((ServerAddress)o).hostAndPort);
        }
        return false;
    }

    public int hashCode() {
        return this.hostAndPort.hashCode();
    }
}

