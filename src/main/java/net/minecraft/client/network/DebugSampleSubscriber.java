/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.util.EnumMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.DebugSampleSubscriptionC2SPacket;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.log.DebugSampleType;

@Environment(value=EnvType.CLIENT)
public class DebugSampleSubscriber {
    final static public int TIMEOUT = 5000;
    final private ClientPlayNetworkHandler networkHandler;
    final private DebugHud debugHud;
    final private EnumMap<DebugSampleType, Long> lastTime;

    public DebugSampleSubscriber(ClientPlayNetworkHandler handler, DebugHud hud) {
        this.debugHud = hud;
        this.networkHandler = handler;
        this.lastTime = new EnumMap(DebugSampleType.class);
    }

    public void tick() {
        if (this.debugHud.shouldRenderTickCharts()) {
            this.subscribe(DebugSampleType.TICK_TIME);
        }
    }

    private void subscribe(DebugSampleType type) {
        long l = Util.getMeasuringTimeMs();
        if (l > this.lastTime.getOrDefault((Object)type, 0L) + 5000L) {
            this.networkHandler.sendPacket(new DebugSampleSubscriptionC2SPacket(type));
            this.lastTime.put(type, l);
        }
    }
}

