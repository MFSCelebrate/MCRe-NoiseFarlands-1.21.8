/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.ValueObject;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerInfo
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="name")
    @Nullable
    private String name;
    @SerializedName(value="uuid")
    @JsonAdapter(value=UUIDTypeAdapter.class)
    private UUID uuid;
    @SerializedName(value="operator")
    private boolean operator;
    @SerializedName(value="accepted")
    private boolean accepted;
    @SerializedName(value="online")
    private boolean online;

    public String getName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isOperator() {
        return this.operator;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}

