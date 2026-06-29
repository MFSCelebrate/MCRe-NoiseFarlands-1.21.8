/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetryEvent
 *  com.mojang.authlib.minecraft.TelemetrySession
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryEventType;

@Environment(value=EnvType.CLIENT)
public record SentTelemetryEvent(TelemetryEventType type, PropertyMap properties) {
    final static public Codec<SentTelemetryEvent> CODEC = TelemetryEventType.CODEC.dispatchStable(SentTelemetryEvent::type, TelemetryEventType::getCodec);

    public SentTelemetryEvent {
        propertyMap.keySet().forEach(property -> {
            if (!telemetryEventType.hasProperty(property)) {
                throw new IllegalArgumentException("Property '" + property.id() + "' not expected for event: '" + telemetryEventType.getId() + "'");
            }
        });
    }

    public TelemetryEvent createEvent(TelemetrySession session) {
        return this.type.createEvent(session, this.properties);
    }
}

