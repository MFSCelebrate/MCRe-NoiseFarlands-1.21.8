/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.Handle;

@Environment(value=EnvType.CLIENT)
public interface FramePass {
    public <T> Handle<T> addRequiredResource(String var1, ClosableFactory<T> var2);

    public <T> void dependsOn(Handle<T> var1);

    public <T> Handle<T> transfer(Handle<T> var1);

    public void addRequired(FramePass var1);

    public void markToBeVisited();

    public void setRenderer(Runnable var1);
}

