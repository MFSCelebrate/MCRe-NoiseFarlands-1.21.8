/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ClientConnectionState(GameProfile localGameProfile, WorldSession worldSession, DynamicRegistryManager.Immutable receivedRegistries, FeatureSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerInfo serverInfo, @Nullable Screen postDisconnectScreen, Map<Identifier, byte[]> serverCookies, @Nullable ChatHud.ChatState chatState, Map<String, String> customReportDetails, ServerLinks serverLinks) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientConnectionState.class, "localGameProfile;telemetryManager;receivedRegistries;enabledFeatures;serverBrand;serverData;postDisconnectScreen;serverCookies;chatState;customReportDetails;serverLinks", "localGameProfile", "worldSession", "receivedRegistries", "enabledFeatures", "serverBrand", "serverInfo", "postDisconnectScreen", "serverCookies", "chatState", "customReportDetails", "serverLinks"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientConnectionState.class, "localGameProfile;telemetryManager;receivedRegistries;enabledFeatures;serverBrand;serverData;postDisconnectScreen;serverCookies;chatState;customReportDetails;serverLinks", "localGameProfile", "worldSession", "receivedRegistries", "enabledFeatures", "serverBrand", "serverInfo", "postDisconnectScreen", "serverCookies", "chatState", "customReportDetails", "serverLinks"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientConnectionState.class, "localGameProfile;telemetryManager;receivedRegistries;enabledFeatures;serverBrand;serverData;postDisconnectScreen;serverCookies;chatState;customReportDetails;serverLinks", "localGameProfile", "worldSession", "receivedRegistries", "enabledFeatures", "serverBrand", "serverInfo", "postDisconnectScreen", "serverCookies", "chatState", "customReportDetails", "serverLinks"}, this, object);
    }
}

