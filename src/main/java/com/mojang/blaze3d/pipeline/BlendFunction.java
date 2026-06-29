/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public record BlendFunction(SourceFactor sourceColor, DestFactor destColor, SourceFactor sourceAlpha, DestFactor destAlpha) {
    final static public BlendFunction LIGHTNING = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
    final static public BlendFunction GLINT = new BlendFunction(SourceFactor.SRC_COLOR, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
    final static public BlendFunction OVERLAY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
    final static public BlendFunction TRANSLUCENT = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    final static public BlendFunction TRANSLUCENT_PREMULTIPLIED_ALPHA = new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    final static public BlendFunction ADDITIVE = new BlendFunction(SourceFactor.ONE, DestFactor.ONE);
    final static public BlendFunction ENTITY_OUTLINE_BLIT = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
    final static public BlendFunction INVERT = new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);

    public BlendFunction(SourceFactor source, DestFactor dest) {
        this(source, dest, source, dest);
    }
}

