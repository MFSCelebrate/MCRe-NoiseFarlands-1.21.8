/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class UniformType
extends Enum<UniformType> {
    final static public UniformType UNIFORM_BUFFER = new UniformType("ubo");
    final static public UniformType TEXEL_BUFFER = new UniformType("utb");
    final String name;
    final static private UniformType[] field_56751;

    public static UniformType[] values() {
        return (UniformType[])field_56751.clone();
    }

    public static UniformType valueOf(String string) {
        return Enum.valueOf(UniformType.class, string);
    }

    private UniformType(String name) {
        this.name = name;
    }

    private static UniformType[] method_67774() {
        return new UniformType[]{UNIFORM_BUFFER, TEXEL_BUFFER};
    }

    static {
        field_56751 = UniformType.method_67774();
    }
}

