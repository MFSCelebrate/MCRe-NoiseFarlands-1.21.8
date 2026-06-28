/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiling.jfr.event;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Period;
import jdk.jfr.StackTrace;
import net.minecraft.obfuscate.DontObfuscate;

@Name(value="minecraft.NetworkSummary")
@Label(value="Network Summary")
@Category(value={"Minecraft", "Network"})
@StackTrace(value=false)
@Period(value="10 s")
@DontObfuscate
public class NetworkSummaryEvent
extends Event {
    final static public String EVENT_NAME = "minecraft.NetworkSummary";
    final static public EventType TYPE = EventType.getEventType(NetworkSummaryEvent.class);
    @Name(value="remoteAddress")
    @Label(value="Remote Address")
    final public String remoteAddress;
    @Name(value="sentBytes")
    @Label(value="Sent Bytes")
    @DataAmount
    public long sentBytes;
    @Name(value="sentPackets")
    @Label(value="Sent Packets")
    public int sentPackets;
    @Name(value="receivedBytes")
    @Label(value="Received Bytes")
    @DataAmount
    public long receivedBytes;
    @Name(value="receivedPackets")
    @Label(value="Received Packets")
    public int receivedPackets;

    public NetworkSummaryEvent(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public static final class Recorder {
        final private AtomicLong sentBytes = new AtomicLong();
        final private AtomicInteger sentPackets = new AtomicInteger();
        final private AtomicLong receivedBytes = new AtomicLong();
        final private AtomicInteger receivedPackets = new AtomicInteger();
        final private NetworkSummaryEvent event;

        public Recorder(String remoteAddress) {
            this.event = new NetworkSummaryEvent(remoteAddress);
            this.event.begin();
        }

        public void addSentPacket(int bytes) {
            this.sentPackets.incrementAndGet();
            this.sentBytes.addAndGet(bytes);
        }

        public void addReceivedPacket(int bytes) {
            this.receivedPackets.incrementAndGet();
            this.receivedBytes.addAndGet(bytes);
        }

        public void commit() {
            this.event.sentBytes = this.sentBytes.get();
            this.event.sentPackets = this.sentPackets.get();
            this.event.receivedBytes = this.receivedBytes.get();
            this.event.receivedPackets = this.receivedPackets.get();
            this.event.commit();
        }
    }

    public static final class Names {
        final static public String REMOTE_ADDRESS = "remoteAddress";
        final static public String SENT_BYTES = "sentBytes";
        final static private String SENT_PACKETS = "sentPackets";
        final static public String RECEIVED_BYTES = "receivedBytes";
        final static private String RECEIVED_PACKETS = "receivedPackets";

        private Names() {
        }
    }
}

