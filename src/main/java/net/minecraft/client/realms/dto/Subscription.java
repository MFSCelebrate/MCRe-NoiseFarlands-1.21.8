/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Subscription
extends ValueObject {
    final static private Logger LOGGER = LogUtils.getLogger();
    public long startDate;
    public int daysLeft;
    public SubscriptionType type = SubscriptionType.NORMAL;

    public static Subscription parse(String json) {
        Subscription subscription = new Subscription();
        try {
            JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
            subscription.startDate = JsonUtils.getLongOr("startDate", jsonObject, 0L);
            subscription.daysLeft = JsonUtils.getIntOr("daysLeft", jsonObject, 0);
            subscription.type = Subscription.typeFrom(JsonUtils.getNullableStringOr("subscriptionType", jsonObject, SubscriptionType.NORMAL.name()));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse Subscription: {}", (Object)exception.getMessage());
        }
        return subscription;
    }

    private static SubscriptionType typeFrom(String subscriptionType) {
        try {
            return SubscriptionType.valueOf(subscriptionType);
        }
        catch (Exception exception) {
            return SubscriptionType.NORMAL;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class SubscriptionType
    extends Enum<SubscriptionType> {
        final static public SubscriptionType NORMAL = new SubscriptionType();
        final static public SubscriptionType RECURRING = new SubscriptionType();
        final static private SubscriptionType[] field_19445;

        public static SubscriptionType[] values() {
            return (SubscriptionType[])field_19445.clone();
        }

        public static SubscriptionType valueOf(String name) {
            return Enum.valueOf(SubscriptionType.class, name);
        }

        private static SubscriptionType[] method_36850() {
            return new SubscriptionType[]{NORMAL, RECURRING};
        }

        static {
            field_19445 = SubscriptionType.method_36850();
        }
    }
}

