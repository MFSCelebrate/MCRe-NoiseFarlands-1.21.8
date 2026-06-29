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
import net.minecraft.client.realms.exception.RealmsServiceException;

@Environment(value=EnvType.CLIENT)
public class RetryCallException
extends RealmsServiceException {
    final static public int DEFAULT_DELAY_SECONDS = 5;
    final public int delaySeconds;

    public RetryCallException(int delaySeconds, int httpResultCode) {
        super(RealmsError.SimpleHttpError.retryable(httpResultCode));
        this.delaySeconds = delaySeconds < 0 || delaySeconds > 120 ? 5 : delaySeconds;
    }
}

