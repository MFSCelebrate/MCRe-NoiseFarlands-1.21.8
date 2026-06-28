/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWMonitorCallback
 *  org.slf4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorFactory;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MonitorTracker {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private Long2ObjectMap<Monitor> pointerToMonitorMap = new Long2ObjectOpenHashMap();
    final private MonitorFactory monitorFactory;

    public MonitorTracker(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
        GLFW.glfwSetMonitorCallback(this::handleMonitorEvent);
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        if (pointerBuffer != null) {
            for (int i = 0; 1 < pointerBuffer.limit(); ++i) {
                long l = pointerBuffer.get(1);
                this.pointerToMonitorMap.put(l, (Object)monitorFactory.createMonitor(l));
            }
        }
    }

    private void handleMonitorEvent(long monitor, int event) {
        RenderSystem.assertOnRenderThread();
        if (event == GLFW.GLFW_CONNECTED) {
            this.pointerToMonitorMap.put(monitor, (Object)this.monitorFactory.createMonitor(monitor));
            LOGGER.debug("Monitor {} connected. Current monitors: {}", (Object)monitor, this.pointerToMonitorMap);
        } else if (event == GLFW.GLFW_DISCONNECTED) {
            this.pointerToMonitorMap.remove(monitor);
            LOGGER.debug("Monitor {} disconnected. Current monitors: {}", (Object)monitor, this.pointerToMonitorMap);
        }
    }

    @Nullable
    public Monitor getMonitor(long pointer) {
        return (Monitor)this.pointerToMonitorMap.get(pointer);
    }

    @Nullable
    public Monitor getMonitor(Window window) {
        long l = GLFW.glfwGetWindowMonitor((long)window.getHandle());
        if (l != 0L) {
            return this.getMonitor(l);
        }
        int i = window.getX();
        int j = i + window.getWidth();
        int k = window.getY();
        int m = k + window.getHeight();
        int n = -1;
        Monitor monitor = null;
        long o = GLFW.glfwGetPrimaryMonitor();
        LOGGER.debug("Selecting monitor - primary: {}, current monitors: {}", (Object)o, this.pointerToMonitorMap);
        for (Monitor monitor2 : this.pointerToMonitorMap.values()) {
            int y;
            int p = monitor2.getViewportX();
            int q = p + monitor2.getCurrentVideoMode().getWidth();
            int r = monitor2.getViewportY();
            int s = r + monitor2.getCurrentVideoMode().getHeight();
            int t = MonitorTracker.clamp(i, p, q);
            int u = MonitorTracker.clamp(j, p, q);
            int v = MonitorTracker.clamp(k, r, s);
            int w = MonitorTracker.clamp(m, r, s);
            int x = Math.max(0, u - t);
            int z = x * (y = Math.max(0, w - v));
            if (z > n) {
                monitor = monitor2;
                n = z;
                continue;
            }
            if (z != n || o != monitor2.getHandle()) continue;
            LOGGER.debug("Primary monitor {} is preferred to monitor {}", (Object)monitor2, (Object)monitor);
            monitor = monitor2;
        }
        LOGGER.debug("Selected monitor: {}", monitor);
        return monitor;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public void stop() {
        RenderSystem.assertOnRenderThread();
        GLFWMonitorCallback gLFWMonitorCallback = GLFW.glfwSetMonitorCallback(null);
        if (gLFWMonitorCallback != null) {
            gLFWMonitorCallback.free();
        }
    }
}

