/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.util;

import com.google.common.base.Charsets;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextVisitFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class Clipboard {
    final static public int GLFW_FORMAT_UNAVAILABLE = 65545;
    final private ByteBuffer clipboardBuffer = BufferUtils.createByteBuffer(8192);

    public String getClipboard(long window, GLFWErrorCallbackI errorCallback) {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)errorCallback);
        String string = GLFW.glfwGetClipboardString((long)window);
        string = string != null ? TextVisitFactory.validateSurrogates(string) : "";
        GLFWErrorCallback gLFWErrorCallback2 = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)gLFWErrorCallback);
        if (gLFWErrorCallback2 != null) {
            gLFWErrorCallback2.free();
        }
        return string;
    }

    private static void setClipboard(long window, ByteBuffer clipboardBuffer, byte[] content) {
        clipboardBuffer.clear();
        clipboardBuffer.put(content);
        clipboardBuffer.put(0);
        clipboardBuffer.flip();
        GLFW.glfwSetClipboardString((long)window, (ByteBuffer)clipboardBuffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled aggressive exception aggregation
     */
    public void setClipboard(long window, String string) {
        byte[] bs = string.getBytes(Charsets.UTF_8);
        int i = bs.length + 1;
        if (i < this.clipboardBuffer.capacity()) {
            Clipboard.setClipboard(window, this.clipboardBuffer, bs);
            return;
        }
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)i);
        try {
            Clipboard.setClipboard(window, byteBuffer, bs);
        }
        catch (Throwable throwable) {
            MemoryUtil.memFree((Buffer)byteBuffer);
            throw throwable;
        }
        MemoryUtil.memFree((Buffer)byteBuffer);
    }
}

