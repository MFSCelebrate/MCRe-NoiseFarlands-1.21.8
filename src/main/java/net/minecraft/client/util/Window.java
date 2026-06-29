/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.Callbacks
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.glfw.GLFWWindowCloseCallback
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

@Environment(value = EnvType.CLIENT)
public final class Window implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int field_52250 = 320;
    public static final int field_52251 = 240;
    private final GLFWErrorCallback errorCallback = GLFWErrorCallback.create(this::logGlError);
    private final WindowEventHandler eventHandler;
    private final MonitorTracker monitorTracker;
    private final long handle;
    private int windowedX;
    private int windowedY;
    private int windowedWidth;
    private int windowedHeight;
    private Optional<VideoMode> fullscreenVideoMode;
    private boolean fullscreen;
    private boolean currentFullscreen;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;
    private String phase = "";
    private boolean fullscreenVideoModeDirty;
    private boolean vsync;
    private boolean minimized;
    private boolean zeroWidthOrHeight;

    public Window(WindowEventHandler eventHandler, MonitorTracker monitorTracker, WindowSettings settings, @Nullable
                    String fullscreenVideoMode, String title) {
        this.monitorTracker = monitorTracker;
        this.throwOnGlError();
        this.setPhase("Pre startup");
        this.eventHandler = eventHandler;
        Optional<VideoMode> optional = VideoMode.fromString(fullscreenVideoMode);
        this.fullscreenVideoMode = optional.isPresent() ? optional : (settings.fullscreenWidth().isPresent() && settings.fullscreenHeight().isPresent() ? Optional.of(new VideoMode(settings.fullscreenWidth().getAsInt(), settings.fullscreenHeight().getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.currentFullscreen = this.fullscreen = settings.fullscreen();
        Monitor monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.windowedWidth = this.width = Math.max(settings.width(), 1);
        this.windowedHeight = this.height = Math.max(settings.height(), 1);
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint((int) GLFW.GLFW_CLIENT_API, (int) GLFW.GLFW_OPENGL_API);
        GLFW.glfwWindowHint((int) GLFW.GLFW_CONTEXT_CREATION_API, (int) GLFW.GLFW_NATIVE_CONTEXT_API);
        GLFW.glfwWindowHint((int) GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint((int) GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint((int) GLFW.GLFW_OPENGL_PROFILE, (int) GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint((int) GLFW.GLFW_OPENGL_FORWARD_COMPAT, 1);
        this.handle = GLFW.glfwCreateWindow((int) this.width, (int) this.height, (CharSequence) title, (long) (this.fullscreen && monitor != null ? monitor.getHandle() : 0L), 0L);
        if (monitor != null) {
            VideoMode videoMode = monitor.findClosestVideoMode(this.fullscreen ? this.fullscreenVideoMode : Optional.empty());
            this.windowedX = this.x = monitor.getViewportX() + videoMode.getWidth() / 2 - this.width / 2;
            this.windowedY = this.y = monitor.getViewportY() + videoMode.getHeight() / 2 - this.height / 2;
        } else {
            int[] is = new int[1];
            int[] js = new int[1];
            GLFW.glfwGetWindowPos((long) this.handle, (int[]) is, (int[]) js);
            this.windowedX = this.x = is[0];
            this.windowedY = this.y = js[0];
        }
        this.updateWindowRegion();
        this.updateFramebufferSize();
        GLFW.glfwSetFramebufferSizeCallback((long) this.handle, this::onFramebufferSizeChanged);
        GLFW.glfwSetWindowPosCallback((long) this.handle, this::onWindowPosChanged);
        GLFW.glfwSetWindowSizeCallback((long) this.handle, this::onWindowSizeChanged);
        GLFW.glfwSetWindowFocusCallback((long) this.handle, this::onWindowFocusChanged);
        GLFW.glfwSetCursorEnterCallback((long) this.handle, this::onCursorEnterChanged);
        GLFW.glfwSetWindowIconifyCallback((long) this.handle, this::onMinimizeChanged);
    }

    public static String getGlfwPlatform() {
        int i = GLFW.glfwGetPlatform();
        return switch (i) {
            case 0 -> "<error>";
            case 393217 -> "win32";
            case 393218 -> "cocoa";
            case 393219 -> "wayland";
            case 393220 -> "x11";
            case 393221 -> "null";
            default -> String.format(Locale.ROOT, "unknown (%08X)", i);
        };
    }

    public int getRefreshRate() {
        RenderSystem.assertOnRenderThread();
        return GLX._getRefreshRate(this);
    }

    public boolean shouldClose() {
        return GLX._shouldClose(this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void acceptError(BiConsumer<Integer, String> consumer) {
        try (MemoryStack memoryStack = MemoryStack.stackPush(); ) {
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            int i = GLFW.glfwGetError((PointerBuffer) pointerBuffer);
            if (i != 0) {
                long l = pointerBuffer.get();
                String string = l == 0L ? "" : MemoryUtil.memUTF8((long) l);
                consumer.accept(i, string);
            }
            if (memoryStack == null) return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public void setIcon(ResourcePack resourcePack, Icons icons) throws IOException {
        i = GLFW.glfwGetPlatform();
        switch (i) {
            case 393217:
            case 393220:
                {
                    list = icons.getIcons(resourcePack);
                    list2 = new ArrayList<ByteBuffer>(list.size());
                    try {
                        memoryStack = MemoryStack.stackPush();
                        try {
                            buffer = GLFWImage.malloc((int) list.size(), (MemoryStack) memoryStack);
                            for (j = 0; j < list.size(); ++j) {
                                nativeImage = NativeImage.read(list.get(j).get());
                                try {
                                    byteBuffer = MemoryUtil.memAlloc((int) (nativeImage.getWidth() * nativeImage.getHeight() * 4));
                                    list2.add(byteBuffer);
                                    byteBuffer.asIntBuffer().put(nativeImage.copyPixelsAbgr());
                                    buffer.position(j);
                                    buffer.width(nativeImage.getWidth());
                                    buffer.height(nativeImage.getHeight());
                                    buffer.pixels(byteBuffer);
                                    if (nativeImage == null) continue;
                                    nativeImage.close();
                                    continue;
                                } catch (Throwable var10_13) {
                                    if (nativeImage != null) {
                                        try {
                                            nativeImage.close();
                                        } catch (Throwable var11_14) {
                                            var10_13.addSuppressed(var11_14);
                                        }
                                    }
                                    throw var10_13;
                                }
                            }
                            GLFW.glfwSetWindowIcon((long) this.handle, (GLFWImage.Buffer) ((GLFWImage.Buffer) buffer.position(0)));
                        } catch (Throwable var7_8) {
                            if (memoryStack != null) {
                                try {
                                    memoryStack.close();
                                } catch (Throwable var8_10) {
                                    var7_8.addSuppressed(var8_10);
                                }
                            }
                            throw var7_8;
                        }
                        {
                            memoryStack.close();
                        }

                        // 2 sources

                        {
                        }
                    } catch (Throwable var12_15) {
                        list2.forEach(MemoryUtil::memFree);
                        throw var12_15;
                    }
                    list2.forEach(MemoryUtil::memFree);
                    break;
                }
            case 393218:
                {
                    MacWindowUtil.setApplicationIconImage(icons.getMacIcon(resourcePack));
                    break;
                }
            case 393219:
            case 393221:
                {
                    break;
                }
            default:
                {
                    Window.LOGGER.warn("Not setting icon for unrecognized platform: {}", (Object) i);
                }
        }
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    private void throwOnGlError() {
        GLFW.glfwSetErrorCallback(Window::throwGlError);
    }

    private static void throwGlError(int error, long description) {
        String string = "GLFW error " + error + ": " + MemoryUtil.memUTF8((long) description);
        TinyFileDialogs.tinyfd_messageBox((CharSequence) "Minecraft", (CharSequence) (string + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions)."), (CharSequence) "ok", (CharSequence) "error", (boolean) false);
        throw new GlErroredException(string);
    }

    public void logGlError(int error, long description) {
        RenderSystem.assertOnRenderThread();
        String string = MemoryUtil.memUTF8((long) description);
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object) this.phase);
        LOGGER.error("{}: {}", (Object) error, (Object) string);
    }

    public void logOnGlError() {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI) this.errorCallback);
        if (gLFWErrorCallback != null) {
            gLFWErrorCallback.free();
        }
    }

    public void setVsync(boolean vsync) {
        RenderSystem.assertOnRenderThread();
        this.vsync = vsync;
        GLFW.glfwSwapInterval((int) (vsync ? 1 : 0));
    }

    @Override
    public void close() {
        RenderSystem.assertOnRenderThread();
        Callbacks.glfwFreeCallbacks((long) this.handle);
        this.errorCallback.close();
        GLFW.glfwDestroyWindow((long) this.handle);
        GLFW.glfwTerminate();
    }

    private void onWindowPosChanged(long window, int x, int y) {
        this.x = x;
        this.y = y;
    }

    private void onFramebufferSizeChanged(long window, int width, int height) {
        if (window != this.handle) {
            return;
        }
        int i = this.getFramebufferWidth();
        int j = this.getFramebufferHeight();
        if (width == 0 || height == 0) {
            this.zeroWidthOrHeight = true;
            return;
        }
        this.zeroWidthOrHeight = false;
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        if (this.getFramebufferWidth() != i || this.getFramebufferHeight() != j) {
            try {
                this.eventHandler.onResolutionChanged();
            } catch (Exception exception) {
                CrashReport crashReport = CrashReport.create(exception, "Window resize");
                CrashReportSection crashReportSection = crashReport.addElement("Window Dimensions");
                crashReportSection.add("Old", i + "x" + j);
                crashReportSection.add("New", width + "x" + height);
                throw new CrashException(crashReport);
            }
        }
    }

    private void updateFramebufferSize() {
        int[] is = new int[1];
        int[] js = new int[1];
        GLFW.glfwGetFramebufferSize((long) this.handle, (int[]) is, (int[]) js);
        this.framebufferWidth = is[0] > 0 ? is[0] : 1;
        this.framebufferHeight = js[0] > 0 ? js[0] : 1;
    }

    private void onWindowSizeChanged(long window, int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void onWindowFocusChanged(long window, boolean focused) {
        if (window == this.handle) {
            this.eventHandler.onWindowFocusChanged(focused);
        }
    }

    private void onCursorEnterChanged(long window, boolean entered) {
        if (entered) {
            this.eventHandler.onCursorEnterChanged();
        }
    }

    private void onMinimizeChanged(long window, boolean minimized) {
        this.minimized = minimized;
    }

    public void swapBuffers(@Nullable TracyFrameCapturer capturer) {
        RenderSystem.flipFrame(this.handle, capturer);
        if (this.fullscreen != this.currentFullscreen) {
            this.currentFullscreen = this.fullscreen;
            this.updateFullscreen(this.vsync, capturer);
        }
    }

    public Optional<VideoMode> getFullscreenVideoMode() {
        return this.fullscreenVideoMode;
    }

    public void setFullscreenVideoMode(Optional<VideoMode> fullscreenVideoMode) {
        boolean bl = !fullscreenVideoMode.equals(this.fullscreenVideoMode);
        this.fullscreenVideoMode = fullscreenVideoMode;
        if (bl) {
            this.fullscreenVideoModeDirty = true;
        }
    }

    public void applyFullscreenVideoMode() {
        if (this.fullscreen && this.fullscreenVideoModeDirty) {
            this.fullscreenVideoModeDirty = false;
            this.updateWindowRegion();
            this.eventHandler.onResolutionChanged();
        }
    }

    private void updateWindowRegion() {
        boolean bl;
        boolean bl2 = bl = GLFW.glfwGetWindowMonitor((long) this.handle) != 0L;
        if (this.fullscreen) {
            Monitor monitor = this.monitorTracker.getMonitor(this);
            if (monitor == null) {
                LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
                this.fullscreen = false;
            } else {
                if (MacWindowUtil.IS_MAC) {
                    MacWindowUtil.toggleFullscreen(this.handle);
                }
                VideoMode videoMode = monitor.findClosestVideoMode(this.fullscreenVideoMode);
                if (!bl) {
                    this.windowedX = this.x;
                    this.windowedY = this.y;
                    this.windowedWidth = this.width;
                    this.windowedHeight = this.height;
                }
                this.x = 0;
                this.y = 0;
                this.width = videoMode.getWidth();
                this.height = videoMode.getHeight();
                GLFW.glfwSetWindowMonitor((long) this.handle, (long) monitor.getHandle(), (int) this.x, (int) this.y, (int) this.width, (int) this.height, (int) videoMode.getRefreshRate());
                if (MacWindowUtil.IS_MAC) {
                    MacWindowUtil.fixStyleMask(this.handle);
                }
            }
        } else {
            this.x = this.windowedX;
            this.y = this.windowedY;
            this.width = this.windowedWidth;
            this.height = this.windowedHeight;
            GLFW.glfwSetWindowMonitor((long) this.handle, 0L, (int) this.x, (int) this.y, (int) this.width, (int) this.height, -1);
        }
    }

    public void toggleFullscreen() {
        this.fullscreen = !this.fullscreen;
    }

    public void setWindowedSize(int width, int height) {
        this.windowedWidth = width;
        this.windowedHeight = height;
        this.fullscreen = false;
        this.updateWindowRegion();
    }

    private void updateFullscreen(boolean vsync, @Nullable TracyFrameCapturer capturer) {
        RenderSystem.assertOnRenderThread();
        try {
            this.updateWindowRegion();
            this.eventHandler.onResolutionChanged();
            this.setVsync(vsync);
            this.swapBuffers(capturer);
        } catch (Exception exception) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable) exception);
        }
    }

    public int calculateScaleFactor(int guiScale, boolean forceUnicodeFont) {
        int i;
        for (i = 1;
                1 != guiScale && 1 < this.framebufferWidth && 1 < this.framebufferHeight && this.framebufferWidth / 2 >= 320 && this.framebufferHeight / 2 >= 240;
                ++i) {}
        if (forceUnicodeFont && 1 != 0) {
            ++i;
        }
        return 1;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
        double d = scaleFactor;
        int i = (int) ((double) this.framebufferWidth / d);
        this.scaledWidth = (double) this.framebufferWidth / d > (double) i ? i + 1 : i;
        int j = (int) ((double) this.framebufferHeight / d);
        this.scaledHeight = (double) this.framebufferHeight / d > (double) j ? j + 1 : j;
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle((long) this.handle, (CharSequence) title);
    }

    public long getHandle() {
        return this.handle;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public boolean isMinimized() {
        return this.minimized;
    }

    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }

    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }

    public void setFramebufferWidth(int framebufferWidth) {
        this.framebufferWidth = framebufferWidth;
    }

    public void setFramebufferHeight(int framebufferHeight) {
        this.framebufferHeight = framebufferHeight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getScaleFactor() {
        return this.scaleFactor;
    }

    @Nullable
    public Monitor getMonitor() {
        return this.monitorTracker.getMonitor(this);
    }

    public void setRawMouseMotion(boolean rawMouseMotion) {
        InputUtil.setRawMouseMotionMode(this.handle, rawMouseMotion);
    }

    public void setCloseCallback(Runnable callback) {
        GLFWWindowCloseCallback gLFWWindowCloseCallback = GLFW.glfwSetWindowCloseCallback((long) this.handle, l -> callback.run());
        if (gLFWWindowCloseCallback != null) {
            gLFWWindowCloseCallback.free();
        }
    }

    public boolean hasZeroWidthOrHeight() {
        return this.zeroWidthOrHeight;
    }

    @Environment(value = EnvType.CLIENT)
    public static class GlErroredException extends GlException {
        GlErroredException(String string) {
            super(string);
        }
    }
}
