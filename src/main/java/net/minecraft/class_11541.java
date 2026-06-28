/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class class_11541 {
    final static private List<String> field_61073 = List.of("i3-1000g1", "i3-1000g4", "i3-1000ng4", "i3-1005g1", "i3-l13g4", "i5-1030g4", "i5-1030g7", "i5-1030ng7", "i5-1034g1", "i5-1035g1", "i5-1035g4", "i5-1035g7", "i5-1038ng7", "i5-l16g7", "i7-1060g7", "i7-1060ng7", "i7-1065g7", "i7-1068g7", "i7-1068ng7");
    final static private List<String> field_61074 = List.of("x6211e", "x6212re", "x6214re", "x6413e", "x6414re", "x6416re", "x6425e", "x6425re", "x6427fe");
    final static private List<String> field_61075 = List.of("j6412", "j6413", "n4500", "n4505", "n5095", "n5095a", "n5100", "n5105", "n6210", "n6211");
    final static private List<String> field_61076 = List.of("6805", "j6426", "n6415", "n6000", "n6005");
    @Nullable
    static private class_11541 field_61070;
    final private WeakReference<GpuDevice> field_61071;
    final private boolean field_61072;

    private class_11541(GpuDevice gpuDevice) {
        this.field_61071 = new WeakReference<GpuDevice>(gpuDevice);
        this.field_61072 = class_11541.method_72244(gpuDevice);
    }

    public static class_11541 method_72243(GpuDevice gpuDevice) {
        class_11541 lv = field_61070;
        if (lv == null || lv.field_61071.get() != gpuDevice) {
            field_61070 = lv = new class_11541(gpuDevice);
        }
        return lv;
    }

    public boolean method_72242() {
        return this.field_61072;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean method_72244(GpuDevice gpuDevice) {
        String string = GLX._getCpuInfo().toLowerCase(Locale.ROOT);
        String string2 = gpuDevice.getRenderer().toLowerCase(Locale.ROOT);
        if (!string.contains("intel")) return false;
        if (!string2.contains("intel")) return false;
        if (string2.contains("mesa")) {
            return false;
        }
        if (string2.endsWith("gen11")) {
            return true;
        }
        if (!string2.contains("uhd graphics") && !string2.contains("iris")) {
            return false;
        }
        if (string.contains("atom")) {
            if (field_61074.stream().anyMatch(string::contains)) return true;
        }
        if (string.contains("celeron")) {
            if (field_61075.stream().anyMatch(string::contains)) return true;
        }
        if (string.contains("pentium")) {
            if (field_61076.stream().anyMatch(string::contains)) return true;
        }
        if (!field_61073.stream().anyMatch(string::contains)) return false;
        return true;
    }
}

