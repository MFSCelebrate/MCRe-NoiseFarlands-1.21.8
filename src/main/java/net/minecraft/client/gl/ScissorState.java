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
public class ScissorState {
    private boolean enabled;
    private int x;
    private int y;
    private int width;
    private int height;

    public void enable(int x, int y, int width, int height) {
        this.enabled = true;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean method_72091() {
        return this.enabled;
    }

    public int method_72092() {
        return this.x;
    }

    public int method_72093() {
        return this.y;
    }

    public int method_72094() {
        return this.width;
    }

    public int method_72095() {
        return this.height;
    }
}

