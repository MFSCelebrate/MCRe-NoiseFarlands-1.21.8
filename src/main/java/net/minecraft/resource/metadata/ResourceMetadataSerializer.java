/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;

public record ResourceMetadataSerializer<T>(String name, Codec<T> codec) {
}

