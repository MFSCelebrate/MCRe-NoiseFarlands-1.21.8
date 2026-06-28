/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public enum DepthTestFunction {
    NO_DEPTH_TEST,
    EQUAL_DEPTH_TEST,
    LEQUAL_DEPTH_TEST,
    LESS_DEPTH_TEST,
    GREATER_DEPTH_TEST;

}

