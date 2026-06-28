/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PieChart {
    final static public int field_52773 = 105;
    final static public int field_59836 = 10;
    final static private int field_52774 = 5;
    final private TextRenderer textRenderer;
    @Nullable
    private ProfileResult profileResult;
    private String currentPath = "root";
    private int bottomMargin = 0;

    public PieChart(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public void setProfileResult(@Nullable ProfileResult profileResult) {
        this.profileResult = profileResult;
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public void render(DrawContext context) {
        if (this.profileResult == null) {
            return;
        }
        List<ProfilerTiming> list = this.profileResult.getTimings(this.currentPath);
        ProfilerTiming profilerTiming = list.removeFirst();
        int i = context.getScaledWindowWidth() - 105 - 10;
        int j = i - 105;
        int k = i + 105;
        int l = list.size() * this.textRenderer.fontHeight;
        int m = context.getScaledWindowHeight() - this.bottomMargin - 5;
        int n = m - l;
        int o = 62;
        int p = n - 62 - 5;
        context.fill(j - 5, p - 62 - 5, k + 5, m + 5, -1873784752);
        context.addProfilerChart(list, j, p - 62 + 10, k, p + 62);
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        String string = ProfileResult.getHumanReadableName(profilerTiming.name);
        Object string2 = "";
        if (!"unspecified".equals(string)) {
            string2 = (String)string2 + "[0] ";
        }
        string2 = string.isEmpty() ? (String)string2 + "ROOT " : (String)string2 + string + " ";
        int q = -1;
        int r = p - 62;
        context.drawTextWithShadow(this.textRenderer, (String)string2, j, r, Colors.WHITE);
        string2 = decimalFormat.format(profilerTiming.totalUsagePercentage) + "%";
        context.drawTextWithShadow(this.textRenderer, (String)string2, k - this.textRenderer.getWidth((String)string2), r, Colors.WHITE);
        for (int s = 0; s < list.size(); ++s) {
            ProfilerTiming profilerTiming2 = list.get(s);
            StringBuilder stringBuilder = new StringBuilder();
            if ("unspecified".equals(profilerTiming2.name)) {
                stringBuilder.append("[?] ");
            } else {
                stringBuilder.append("[").append(s + 1).append("] ");
            }
            Object string3 = stringBuilder.append(profilerTiming2.name).toString();
            int t = n + s * this.textRenderer.fontHeight;
            context.drawTextWithShadow(this.textRenderer, (String)string3, j, t, profilerTiming2.getColor());
            string3 = decimalFormat.format(profilerTiming2.parentSectionUsagePercentage) + "%";
            context.drawTextWithShadow(this.textRenderer, (String)string3, k - 50 - this.textRenderer.getWidth((String)string3), t, profilerTiming2.getColor());
            string3 = decimalFormat.format(profilerTiming2.totalUsagePercentage) + "%";
            context.drawTextWithShadow(this.textRenderer, (String)string3, k - this.textRenderer.getWidth((String)string3), t, profilerTiming2.getColor());
        }
    }

    public void select(int index) {
        if (this.profileResult == null) {
            return;
        }
        List<ProfilerTiming> list = this.profileResult.getTimings(this.currentPath);
        if (list.isEmpty()) {
            return;
        }
        ProfilerTiming profilerTiming = list.remove(0);
        if (index == 0) {
            int i;
            if (!profilerTiming.name.isEmpty() && (i = this.currentPath.lastIndexOf(30)) >= 0) {
                this.currentPath = this.currentPath.substring(0, i);
            }
        } else if (--index < list.size() && !"unspecified".equals(list.get((int)index).name)) {
            if (!this.currentPath.isEmpty()) {
                this.currentPath = this.currentPath + "\u001e";
            }
            this.currentPath = this.currentPath + list.get((int)index).name;
        }
    }
}

