/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL43
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLDebugMessageARBCallback
 *  org.lwjgl.opengl.GLDebugMessageARBCallbackI
 *  org.lwjgl.opengl.GLDebugMessageCallback
 *  org.lwjgl.opengl.GLDebugMessageCallbackI
 *  org.lwjgl.opengl.KHRDebug
 *  org.slf4j.Logger
 */
package net.minecraft.client.gl;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.util.Untracker;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GlDebug {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static private int DEBUG_MESSAGE_QUEUE_SIZE = 10;
    final private Queue<DebugMessage> debugMessages = EvictingQueue.create(10);
    @Nullable
    private volatile DebugMessage lastDebugMessage;
    final static private List<Integer> KHR_VERBOSITY_LEVELS = ImmutableList.of((Object)37190, (Object)37191, (Object)37192, (Object)33387);
    final static private List<Integer> ARB_VERBOSITY_LEVELS = ImmutableList.of((Object)37190, (Object)37191, (Object)37192);

    private static String unknown(int opcode) {
        return "Unknown (0x" + Integer.toHexString(opcode).toUpperCase() + ")";
    }

    public static String getSource(int opcode) {
        switch (opcode) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return GlDebug.unknown(opcode);
    }

    public static String getType(int opcode) {
        switch (opcode) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
            case 33384: {
                return "MARKER";
            }
        }
        return GlDebug.unknown(opcode);
    }

    public static String getSeverity(int opcode) {
        switch (opcode) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
            case 33387: {
                return "NOTIFICATION";
            }
        }
        return GlDebug.unknown(opcode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void onDebugMessage(int source, int type, int id, int severity, int length, long message, long l) {
        DebugMessage debugMessage;
        String string = GLDebugMessageCallback.getMessage((int)length, (long)message);
        Queue<DebugMessage> queue = this.debugMessages;
        synchronized (queue) {
            debugMessage = this.lastDebugMessage;
            if (debugMessage == null || !debugMessage.equals(source, type, id, severity, string)) {
                debugMessage = new DebugMessage(source, type, id, severity, string);
                this.debugMessages.add(debugMessage);
                this.lastDebugMessage = debugMessage;
            } else {
                ++debugMessage.count;
            }
        }
        LOGGER.info("OpenGL debug message: {}", (Object)debugMessage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> collectDebugMessages() {
        Queue<DebugMessage> queue = this.debugMessages;
        synchronized (queue) {
            ArrayList list = Lists.newArrayListWithCapacity((int)this.debugMessages.size());
            for (DebugMessage debugMessage : this.debugMessages) {
                list.add(String.valueOf(debugMessage) + " x " + debugMessage.count);
            }
            return list;
        }
    }

    @Nullable
    public static GlDebug enableDebug(int verbosity, boolean sync, Set<String> usedGlCaps) {
        if (verbosity <= 0) {
            return null;
        }
        GLCapabilities gLCapabilities = GL.getCapabilities();
        if (gLCapabilities.GL_KHR_debug && GlBackend.allowGlKhrDebug) {
            GlDebug glDebug = new GlDebug();
            usedGlCaps.add("GL_KHR_debug");
            GL11.glEnable((int)GL43.GL_DEBUG_OUTPUT);
            if (sync) {
                GL11.glEnable((int)GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
            }
            for (int i = 0; i < KHR_VERBOSITY_LEVELS.size(); ++i) {
                boolean bl = i < verbosity;
                KHRDebug.glDebugMessageControl((int)GL11.GL_DONT_CARE, (int)GL11.GL_DONT_CARE, (int)KHR_VERBOSITY_LEVELS.get(i), (int[])null, (boolean)bl);
            }
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create(glDebug::onDebugMessage), Untracker::untrack)), 0L);
            return glDebug;
        }
        if (gLCapabilities.GL_ARB_debug_output && GlBackend.allowGlArbDebugOutput) {
            GlDebug glDebug = new GlDebug();
            usedGlCaps.add("GL_ARB_debug_output");
            if (sync) {
                GL11.glEnable((int)GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
            }
            for (int i = 0; i < ARB_VERBOSITY_LEVELS.size(); ++i) {
                boolean bl = i < verbosity;
                ARBDebugOutput.glDebugMessageControlARB((int)GL11.GL_DONT_CARE, (int)GL11.GL_DONT_CARE, (int)ARB_VERBOSITY_LEVELS.get(i), (int[])null, (boolean)bl);
            }
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create(glDebug::onDebugMessage), Untracker::untrack)), 0L);
            return glDebug;
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    static class DebugMessage {
        final private int id;
        final private int source;
        final private int type;
        final private int severity;
        final private String message;
        int count = 1;

        DebugMessage(int source, int type, int id, int severity, String message) {
            this.id = id;
            this.source = source;
            this.type = type;
            this.severity = severity;
            this.message = message;
        }

        boolean equals(int source, int type, int id, int severity, String message) {
            return type == this.type && source == this.source && id == this.id && severity == this.severity && message.equals(this.message);
        }

        public String toString() {
            return "id=" + this.id + ", source=" + GlDebug.getSource(this.source) + ", type=" + GlDebug.getType(this.type) + ", severity=" + GlDebug.getSeverity(this.severity) + ", message='" + this.message + "'";
        }
    }
}

