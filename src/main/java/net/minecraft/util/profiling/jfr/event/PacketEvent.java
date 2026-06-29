/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Category(value={"Minecraft", "Network"})
@StackTrace(value=false)
@Enabled(value=false)
public abstract class PacketEvent
extends Event {
    @Name(value="protocolId")
    @Label(value="Protocol Id")
    final public String protocolId;
    @Name(value="packetDirection")
    @Label(value="Packet Direction")
    final public String packetDirection;
    @Name(value="packetId")
    @Label(value="Packet Id")
    final public String packetId;
    @Name(value="remoteAddress")
    @Label(value="Remote Address")
    final public String remoteAddress;
    @Name(value="bytes")
    @Label(value="Bytes")
    @DataAmount
    final public int bytes;

    public PacketEvent(String protocolId, String packetDirection, String packetId, SocketAddress remoteAddress, int bytes) {
        this.protocolId = protocolId;
        this.packetDirection = packetDirection;
        this.packetId = packetId;
        this.remoteAddress = remoteAddress.toString();
        this.bytes = bytes;
    }

    public static final class Names {
        final static public String REMOTE_ADDRESS = "remoteAddress";
        final static public String PROTOCOL_ID = "protocolId";
        final static public String PACKET_DIRECTION = "packetDirection";
        final static public String PACKET_ID = "packetId";
        final static public String BYTES = "bytes";

        private Names() {
        }
    }
}

