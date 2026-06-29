/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class KeyCodes {
    public static boolean isToggle(int keyCode) {
        return keyCode == 257 || keyCode == 32 || keyCode == 335;
    }
}

