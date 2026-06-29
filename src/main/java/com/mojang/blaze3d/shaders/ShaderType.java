/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package com.mojang.blaze3d.shaders;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public enum ShaderType {
    VERTEX("vertex", ".vsh"),
    FRAGMENT("fragment", ".fsh");

    final static private ShaderType[] TYPES;
    final private String name;
    final private String extension;

    private ShaderType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    @Nullable
    public static ShaderType byLocation(Identifier id) {
        for (ShaderType shaderType : TYPES) {
            if (!id.getPath().endsWith(shaderType.extension)) continue;
            return shaderType;
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public ResourceFinder idConverter() {
        return new ResourceFinder("shaders", this.extension);
    }

    static {
        TYPES = ShaderType.values();
    }
}

