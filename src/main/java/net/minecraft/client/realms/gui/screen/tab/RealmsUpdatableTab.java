/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen.tab;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(value=EnvType.CLIENT)
public interface RealmsUpdatableTab {
    public void update(RealmsServer var1);

    default public void onLoaded(RealmsServer server) {
    }

    default public void onUnloaded(RealmsServer server) {
    }
}

