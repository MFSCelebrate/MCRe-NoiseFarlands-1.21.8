/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.freetype.FT_Vector
 *  org.lwjgl.util.freetype.FreeType
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FreeTypeUtil {
    final static private Logger LOGGER = LogUtils.getLogger();
    final static public Object LOCK = new Object();
    static private long freeType = 0L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static long initialize() {
        Object object = LOCK;
        synchronized (object) {
            if (freeType != 0L) return freeType;
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                FreeTypeUtil.checkFatalError(FreeType.FT_Init_FreeType((PointerBuffer)pointerBuffer), "Initializing FreeType library");
                freeType = pointerBuffer.get();
                if (memoryStack == null) return freeType;
            }
            {
            }
            return freeType;
        }
    }

    public static void checkFatalError(int code, String description) {
        if (code != 0) {
            throw new IllegalStateException("FreeType error: " + FreeTypeUtil.getErrorMessage(code) + " (" + description + ")");
        }
    }

    public static boolean checkError(int code, String description) {
        if (code != 0) {
            LOGGER.error("FreeType error: {} ({})", (Object)FreeTypeUtil.getErrorMessage(code), (Object)description);
            return true;
        }
        return false;
    }

    private static String getErrorMessage(int code) {
        String string = FreeType.FT_Error_String((int)code);
        if (string != null) {
            return string;
        }
        return "Unrecognized error: 0x" + Integer.toHexString(code);
    }

    public static FT_Vector set(FT_Vector vec, float x, float y) {
        long l = Math.round(x * 64.0f);
        long m = Math.round(y * 64.0f);
        return vec.set(l, m);
    }

    public static float getX(FT_Vector vec) {
        return (float)vec.x() / 64.0f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void release() {
        Object object = LOCK;
        synchronized (object) {
            if (freeType != 0L) {
                FreeType.FT_Done_Library((long)freeType);
                freeType = 0L;
            }
        }
    }
}

