/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.profiling.jfr.event;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Period;
import jdk.jfr.StackTrace;
import jdk.jfr.Timespan;
import net.minecraft.obfuscate.DontObfuscate;

@Name(value="minecraft.ServerTickTime")
@Label(value="Server Tick Time")
@Category(value={"Minecraft", "Ticking"})
@StackTrace(value=false)
@Period(value="1 s")
@DontObfuscate
public class ServerTickTimeEvent
extends Event {
    final static public String EVENT_NAME = "minecraft.ServerTickTime";
    final static public EventType TYPE = EventType.getEventType(ServerTickTimeEvent.class);
    @Name(value="averageTickDuration")
    @Label(value="Average Server Tick Duration")
    @Timespan
    final public long averageTickDurationNanos;

    public ServerTickTimeEvent(float averageTickMilliseconds) {
        this.averageTickDurationNanos = (long)(1000000.0f * averageTickMilliseconds);
    }

    public static class Names {
        final static public String AVERAGE_TICK_DURATION = "averageTickDuration";

        private Names() {
        }
    }
}

