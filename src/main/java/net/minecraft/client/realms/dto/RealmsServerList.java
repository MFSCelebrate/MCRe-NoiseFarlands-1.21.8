/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.ValueObject;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServerList
extends ValueObject
implements RealmsSerializable {
    final static private Logger LOGGER = LogUtils.getLogger();
    @SerializedName(value="servers")
    public List<RealmsServer> servers = new ArrayList<RealmsServer>();

    public static RealmsServerList parse(CheckedGson gson, String json) {
        try {
            RealmsServerList realmsServerList = gson.fromJson(json, RealmsServerList.class);
            if (realmsServerList == null) {
                LOGGER.error("Could not parse McoServerList: {}", (Object)json);
                return new RealmsServerList();
            }
            realmsServerList.servers.forEach(RealmsServer::replaceNullsWithDefaults);
            return realmsServerList;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServerList: {}", (Object)exception.getMessage());
            return new RealmsServerList();
        }
    }
}

