/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class FogData {
    public float environmentalStart;
    public float renderDistanceStart;
    public float environmentalEnd;
    public float renderDistanceEnd;
    public float skyEnd;
    public float cloudEnd;
}

