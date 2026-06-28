/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.tooltip;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.ProfilesTooltipComponent;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;

@Environment(value=EnvType.CLIENT)
public interface TooltipComponent {
    public static TooltipComponent of(OrderedText text) {
        return new OrderedTextTooltipComponent(text);
    }

    public static TooltipComponent of(TooltipData tooltipData) {
        TooltipData tooltipData2 = tooltipData;
        Objects.requireNonNull(tooltipData2);
        TooltipData tooltipData3 = tooltipData2;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BundleTooltipData.class, ProfilesTooltipComponent.ProfilesData.class}, (Object)tooltipData3, n)) {
            case 0 -> {
                BundleTooltipData bundleTooltipData = (BundleTooltipData)tooltipData3;
                yield new BundleTooltipComponent(bundleTooltipData.contents());
            }
            case 1 -> {
                ProfilesTooltipComponent.ProfilesData profilesData = (ProfilesTooltipComponent.ProfilesData)tooltipData3;
                yield new ProfilesTooltipComponent(profilesData);
            }
            default -> throw new IllegalArgumentException("Unknown TooltipComponent");
        };
    }

    public int getHeight(TextRenderer var1);

    public int getWidth(TextRenderer var1);

    default public boolean isSticky() {
        return false;
    }

    default public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {
    }

    default public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
    }
}

