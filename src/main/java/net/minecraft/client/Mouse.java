/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2i
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWDropCallback
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.mojang.logging.LogUtils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Colors;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Smoother;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Mouse {
    final static private Logger LOGGER = LogUtils.getLogger();
    final private MinecraftClient client;
    private boolean leftButtonClicked;
    private boolean middleButtonClicked;
    private boolean rightButtonClicked;
    private double x;
    private double y;
    private int controlLeftClicks;
    private int activeButton = -1;
    private boolean hasResolutionChanged = true;
    private int field_1796;
    private double glfwTime;
    final private Smoother cursorXSmoother = new Smoother();
    final private Smoother cursorYSmoother = new Smoother();
    private double cursorDeltaX;
    private double cursorDeltaY;
    final private Scroller scroller;
    private double lastTickTime = Double.MIN_VALUE;
    private boolean cursorLocked;

    public Mouse(MinecraftClient client) {
        this.client = client;
        this.scroller = new Scroller();
    }

    private void onMouseButton(long window, int button, int action, int mods) {
        int i;
        boolean bl;
        block32: {
            Window window2 = this.client.getWindow();
            if (window != window2.getHandle()) {
                return;
            }
            this.client.getInactivityFpsLimiter().onInput();
            if (this.client.currentScreen != null) {
                this.client.setNavigationType(GuiNavigationType.MOUSE);
            }
            boolean bl2 = bl = action == 1;
            if (MinecraftClient.IS_SYSTEM_MAC && button == 0) {
                if (bl) {
                    if ((mods & 2) == 2) {
                        button = 1;
                        ++this.controlLeftClicks;
                    }
                } else if (this.controlLeftClicks > 0) {
                    button = 1;
                    --this.controlLeftClicks;
                }
            }
            i = button;
            if (bl) {
                if (this.client.options.getTouchscreen().getValue().booleanValue() && this.field_1796++ > 0) {
                    return;
                }
                this.activeButton = i;
                this.glfwTime = GlfwUtil.getTime();
            } else if (this.activeButton != -1) {
                if (this.client.options.getTouchscreen().getValue().booleanValue() && --this.field_1796 > 0) {
                    return;
                }
                this.activeButton = -1;
            }
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen == null) {
                    if (!this.cursorLocked && bl) {
                        this.lockCursor();
                    }
                } else {
                    double d = this.getScaledX(window2);
                    double e = this.getScaledY(window2);
                    Screen screen = this.client.currentScreen;
                    if (bl) {
                        screen.applyMousePressScrollNarratorDelay();
                        try {
                            if (screen.mouseClicked(d, e, i)) {
                                return;
                            }
                            break block32;
                        }
                        catch (Throwable throwable) {
                            CrashReport crashReport = CrashReport.create(throwable, "mouseClicked event handler");
                            screen.addCrashReportSection(crashReport);
                            CrashReportSection crashReportSection = crashReport.addElement("Mouse");
                            this.addCrashReportSection(crashReportSection, window2);
                            crashReportSection.add("Button", i);
                            throw new CrashException(crashReport);
                        }
                    }
                    try {
                        if (screen.mouseReleased(d, e, i)) {
                            return;
                        }
                    }
                    catch (Throwable throwable) {
                        CrashReport crashReport = CrashReport.create(throwable, "mouseReleased event handler");
                        screen.addCrashReportSection(crashReport);
                        CrashReportSection crashReportSection = crashReport.addElement("Mouse");
                        this.addCrashReportSection(crashReportSection, window2);
                        crashReportSection.add("Button", i);
                        throw new CrashException(crashReport);
                    }
                }
            }
        }
        if (this.client.currentScreen == null && this.client.getOverlay() == null) {
            if (i == 0) {
                this.leftButtonClicked = bl;
            } else if (i == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                this.middleButtonClicked = bl;
            } else if (i == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                this.rightButtonClicked = bl;
            }
            KeyBinding.setKeyPressed(InputUtil.Type.MOUSE.createFromCode(i), bl);
            if (bl) {
                if (this.client.player.isSpectator() && i == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                    this.client.inGameHud.getSpectatorHud().useSelectedCommand();
                } else {
                    KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(i));
                }
            }
        }
    }

    public void addCrashReportSection(CrashReportSection section, Window window) {
        section.add("Mouse location", () -> String.format(Locale.ROOT, "Scaled: (%f, %f). Absolute: (%f, %f)", Mouse.scaleX(window, this.x), Mouse.scaleY(window, this.y), this.x, this.y));
        section.add("Screen size", () -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", window.getScaledWidth(), window.getScaledHeight(), window.getFramebufferWidth(), window.getFramebufferHeight(), window.getScaleFactor()));
    }

    private void onMouseScroll(long window, double horizontal, double vertical) {
        if (window == MinecraftClient.getInstance().getWindow().getHandle()) {
            this.client.getInactivityFpsLimiter().onInput();
            boolean bl = this.client.options.getDiscreteMouseScroll().getValue();
            double d = this.client.options.getMouseWheelSensitivity().getValue();
            double e = (bl ? Math.signum(horizontal) : horizontal) * d;
            double f = (bl ? Math.signum(vertical) : vertical) * d;
            if (this.client.getOverlay() == null) {
                if (this.client.currentScreen != null) {
                    double g = this.getScaledX(this.client.getWindow());
                    double h = this.getScaledY(this.client.getWindow());
                    this.client.currentScreen.mouseScrolled(g, h, e, f);
                    this.client.currentScreen.applyMousePressScrollNarratorDelay();
                } else if (this.client.player != null) {
                    int i;
                    Vector2i vector2i = this.scroller.update(e, f);
                    if (vector2i.x == 0 && vector2i.y == 0) {
                        return;
                    }
                    int n = i = vector2i.y == 0 ? -vector2i.x : vector2i.y;
                    if (this.client.player.isSpectator()) {
                        if (this.client.inGameHud.getSpectatorHud().isOpen()) {
                            this.client.inGameHud.getSpectatorHud().cycleSlot(-i);
                        } else {
                            float j = MathHelper.clamp(this.client.player.getAbilities().getFlySpeed() + (float)vector2i.y * 0.005f, 0.0f, 0.2f);
                            this.client.player.getAbilities().setFlySpeed(j);
                        }
                    } else {
                        PlayerInventory playerInventory = this.client.player.getInventory();
                        playerInventory.setSelectedSlot(Scroller.scrollCycling(i, playerInventory.getSelectedSlot(), PlayerInventory.getHotbarSize()));
                    }
                }
            }
        }
    }

    private void onFilesDropped(long window, List<Path> paths, int invalidFilesCount) {
        this.client.getInactivityFpsLimiter().onInput();
        if (this.client.currentScreen != null) {
            this.client.currentScreen.onFilesDropped(paths);
        }
        if (invalidFilesCount > 0) {
            SystemToast.addFileDropFailure(this.client, invalidFilesCount);
        }
    }

    public void setup(long window2) {
        InputUtil.setMouseCallbacks(window2, (window, x, y) -> this.client.execute(() -> this.onCursorPos(window, x, y)), (window, button, action, modifiers) -> this.client.execute(() -> this.onMouseButton(window, button, action, modifiers)), (window, offsetX, offsetY) -> this.client.execute(() -> this.onMouseScroll(window, offsetX, offsetY)), (window, count, names) -> {
            int j;
            ArrayList<Path> list = new ArrayList<Path>(count);
            int i = 0;
            for (j = 0; j < count; ++j) {
                String string = GLFWDropCallback.getName((long)names, (int)j);
                try {
                    list.add(Paths.get(string, new String[0]));
                    continue;
                }
                catch (InvalidPathException invalidPathException) {
                    ++i;
                    LOGGER.error("Failed to parse path '{}'", (Object)string, (Object)invalidPathException);
                }
            }
            if (!list.isEmpty()) {
                j = i;
                this.client.execute(() -> this.onFilesDropped(window, list, j));
            }
        });
    }

    private void onCursorPos(long window, double x, double y) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
            return;
        }
        if (this.hasResolutionChanged) {
            this.x = x;
            this.y = y;
            this.hasResolutionChanged = false;
            return;
        }
        if (this.client.isWindowFocused()) {
            this.cursorDeltaX += x - this.x;
            this.cursorDeltaY += y - this.y;
        }
        this.x = x;
        this.y = y;
    }

    public void tick() {
        double d = GlfwUtil.getTime();
        double e = d - this.lastTickTime;
        this.lastTickTime = d;
        if (this.client.isWindowFocused()) {
            boolean bl;
            Screen screen = this.client.currentScreen;
            boolean bl2 = bl = this.cursorDeltaX != 0.0 || this.cursorDeltaY != 0.0;
            if (bl) {
                this.client.getInactivityFpsLimiter().onInput();
            }
            if (screen != null && this.client.getOverlay() == null && bl) {
                Window window = this.client.getWindow();
                double f = this.getScaledX(window);
                double g = this.getScaledY(window);
                try {
                    screen.mouseMoved(f, g);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "mouseMoved event handler");
                    screen.addCrashReportSection(crashReport);
                    CrashReportSection crashReportSection = crashReport.addElement("Mouse");
                    this.addCrashReportSection(crashReportSection, window);
                    throw new CrashException(crashReport);
                }
                if (this.activeButton != -1 && this.glfwTime > 0.0) {
                    double h = Mouse.scaleX(window, this.cursorDeltaX);
                    double i = Mouse.scaleY(window, this.cursorDeltaY);
                    try {
                        screen.mouseDragged(f, g, this.activeButton, h, i);
                    }
                    catch (Throwable throwable2) {
                        CrashReport crashReport2 = CrashReport.create(throwable2, "mouseDragged event handler");
                        screen.addCrashReportSection(crashReport2);
                        CrashReportSection crashReportSection2 = crashReport2.addElement("Mouse");
                        this.addCrashReportSection(crashReportSection2, window);
                        throw new CrashException(crashReport2);
                    }
                }
                screen.applyMouseMoveNarratorDelay();
            }
            if (this.isCursorLocked() && this.client.player != null) {
                this.updateMouse(e);
            }
        }
        this.cursorDeltaX = 0.0;
        this.cursorDeltaY = 0.0;
    }

    public static double scaleX(Window window, double x) {
        return x * (double)window.getScaledWidth() / (double)window.getWidth();
    }

    public double getScaledX(Window window) {
        return Mouse.scaleX(window, this.x);
    }

    public static double scaleY(Window window, double y) {
        return y * (double)window.getScaledHeight() / (double)window.getHeight();
    }

    public double getScaledY(Window window) {
        return Mouse.scaleY(window, this.y);
    }

    private void updateMouse(double timeDelta) {
        double j;
        double i;
        double d = this.client.options.getMouseSensitivity().getValue() * (double)0.6f + (double)0.2f;
        double e = d * d * d;
        double f = e * 8.0;
        if (this.client.options.smoothCameraEnabled) {
            double g = this.cursorXSmoother.smooth(this.cursorDeltaX * f, timeDelta * f);
            double h = this.cursorYSmoother.smooth(this.cursorDeltaY * f, timeDelta * f);
            i = g;
            j = h;
        } else if (this.client.options.getPerspective().isFirstPerson() && this.client.player.isUsingSpyglass()) {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            i = this.cursorDeltaX * e;
            j = this.cursorDeltaY * e;
        } else {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            i = this.cursorDeltaX * f;
            j = this.cursorDeltaY * f;
        }
        int k = 1;
        if (this.client.options.getInvertYMouse().getValue().booleanValue()) {
            k = -1;
        }
        this.client.getTutorialManager().onUpdateMouse(i, j);
        if (this.client.player != null) {
            this.client.player.changeLookDirection(i, j * (double)k);
        }
    }

    public boolean wasLeftButtonClicked() {
        return this.leftButtonClicked;
    }

    public boolean wasMiddleButtonClicked() {
        return this.middleButtonClicked;
    }

    public boolean wasRightButtonClicked() {
        return this.rightButtonClicked;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void onResolutionChanged() {
        this.hasResolutionChanged = true;
    }

    public boolean isCursorLocked() {
        return this.cursorLocked;
    }

    public void lockCursor() {
        if (!this.client.isWindowFocused()) {
            return;
        }
        if (this.cursorLocked) {
            return;
        }
        if (!MinecraftClient.IS_SYSTEM_MAC) {
            KeyBinding.updatePressedStates();
        }
        this.cursorLocked = true;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_DISABLED, this.x, this.y);
        this.client.setScreen(null);
        this.client.attackCooldown = 10000;
        this.hasResolutionChanged = true;
    }

    public void unlockCursor() {
        if (!this.cursorLocked) {
            return;
        }
        this.cursorLocked = false;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), InputUtil.GLFW_CURSOR_NORMAL, this.x, this.y);
    }

    public void setResolutionChanged() {
        this.hasResolutionChanged = true;
    }

    public void drawScaledPos(TextRenderer textRenderer, DrawContext context) {
        Window window = this.client.getWindow();
        double d = this.getScaledX(window);
        double e = this.getScaledY(window) - 8.0;
        String string = String.format(Locale.ROOT, "%.0f,%.0f", d, e);
        context.drawTextWithShadow(textRenderer, string, (int)d, (int)e, Colors.WHITE);
    }
}

