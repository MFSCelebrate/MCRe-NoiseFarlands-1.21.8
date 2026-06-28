/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AddressResolver;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.RedirectResolver;
import net.minecraft.client.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
public class AllowedAddressResolver {
    final static public AllowedAddressResolver DEFAULT = new AllowedAddressResolver(AddressResolver.DEFAULT, RedirectResolver.createSrv(), BlockListChecker.create());
    final private AddressResolver addressResolver;
    final private RedirectResolver redirectResolver;
    final private BlockListChecker blockListChecker;

    @VisibleForTesting
    AllowedAddressResolver(AddressResolver addressResolver, RedirectResolver redirectResolver, BlockListChecker blockListChecker) {
        this.addressResolver = addressResolver;
        this.redirectResolver = redirectResolver;
        this.blockListChecker = blockListChecker;
    }

    public Optional<Address> resolve(ServerAddress address) {
        Optional<Address> optional = this.addressResolver.resolve(address);
        if (optional.isPresent() && !this.blockListChecker.isAllowed(optional.get()) || !this.blockListChecker.isAllowed(address)) {
            return Optional.empty();
        }
        Optional<ServerAddress> optional2 = this.redirectResolver.lookupRedirect(address);
        if (optional2.isPresent()) {
            optional = this.addressResolver.resolve(optional2.get()).filter(this.blockListChecker::isAllowed);
        }
        return optional;
    }
}

