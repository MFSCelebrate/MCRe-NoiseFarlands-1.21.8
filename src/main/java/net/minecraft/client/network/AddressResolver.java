/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.ServerAddress;
import org.slf4j.Logger;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface AddressResolver {
    final static public Logger LOGGER = LogUtils.getLogger();
    final static public AddressResolver DEFAULT = address -> {
        try {
            InetAddress inetAddress = InetAddress.getByName(address.getAddress());
            return Optional.of(Address.create(new InetSocketAddress(inetAddress, address.getPort())));
        }
        catch (UnknownHostException unknownHostException) {
            LOGGER.debug("Couldn't resolve server {} address", (Object)address.getAddress(), (Object)unknownHostException);
            return Optional.empty();
        }
    };

    public Optional<Address> resolve(ServerAddress var1);
}

