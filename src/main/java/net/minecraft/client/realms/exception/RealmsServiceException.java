/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;

@Environment(value=EnvType.CLIENT)
public class RealmsServiceException
extends Exception {
    final public RealmsError error;

    public RealmsServiceException(RealmsError error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return this.error.getErrorMessage();
    }
}

