/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.ServerConfigEntry;
import org.jetbrains.annotations.Nullable;

public class OperatorEntry
extends ServerConfigEntry<GameProfile> {
    final private int permissionLevel;
    final private boolean bypassPlayerLimit;

    public OperatorEntry(GameProfile profile, int permissionLevel, boolean bypassPlayerLimit) {
        super(profile);
        this.permissionLevel = permissionLevel;
        this.bypassPlayerLimit = bypassPlayerLimit;
    }

    public OperatorEntry(JsonObject json) {
        super(OperatorEntry.getProfileFromJson(json));
        this.permissionLevel = json.has("level") ? json.get("level").getAsInt() : 0;
        this.bypassPlayerLimit = json.has("bypassesPlayerLimit") && json.get("bypassesPlayerLimit").getAsBoolean();
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public boolean canBypassPlayerLimit() {
        return this.bypassPlayerLimit;
    }

    @Override
    protected void write(JsonObject json) {
        if (this.getKey() == null) {
            return;
        }
        json.addProperty("uuid", ((GameProfile)this.getKey()).getId().toString());
        json.addProperty("name", ((GameProfile)this.getKey()).getName());
        json.addProperty("level", (Number)this.permissionLevel);
        json.addProperty("bypassesPlayerLimit", Boolean.valueOf(this.bypassPlayerLimit));
    }

    @Nullable
    private static GameProfile getProfileFromJson(JsonObject json) {
        UUID uUID;
        if (!json.has("uuid") || !json.has("name")) {
            return null;
        }
        String string = json.get("uuid").getAsString();
        try {
            uUID = UUID.fromString(string);
        }
        catch (Throwable throwable) {
            return null;
        }
        return new GameProfile(uUID, json.get("name").getAsString());
    }
}

