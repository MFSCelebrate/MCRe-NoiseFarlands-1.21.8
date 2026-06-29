/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface Positioner {
    public Positioner net_minecraft_client_gui_widget_Positioner_margin(int var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_margin(int var1, int var2);

    public Positioner net_minecraft_client_gui_widget_Positioner_margin(int var1, int var2, int var3, int var4);

    public Positioner net_minecraft_client_gui_widget_Positioner_marginLeft(int var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_marginTop(int var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_marginRight(int var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_marginBottom(int var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_marginX(int var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_marginY(int var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_relative(float var1, float var2);

    public Positioner net_minecraft_client_gui_widget_Positioner_relativeX(float var1);

    public Positioner net_minecraft_client_gui_widget_Positioner_relativeY(float var1);

    default public Positioner alignLeft() {
        return this.net_minecraft_client_gui_widget_Positioner_relativeX(0.0f);
    }

    default public Positioner alignHorizontalCenter() {
        return this.net_minecraft_client_gui_widget_Positioner_relativeX(0.5f);
    }

    default public Positioner alignRight() {
        return this.net_minecraft_client_gui_widget_Positioner_relativeX(1.0f);
    }

    default public Positioner alignTop() {
        return this.net_minecraft_client_gui_widget_Positioner_relativeY(0.0f);
    }

    default public Positioner alignVerticalCenter() {
        return this.net_minecraft_client_gui_widget_Positioner_relativeY(0.5f);
    }

    default public Positioner alignBottom() {
        return this.net_minecraft_client_gui_widget_Positioner_relativeY(1.0f);
    }

    public Positioner net_minecraft_client_gui_widget_Positioner_copy();

    public Impl toImpl();

    public static Positioner create() {
        return new Impl();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Impl
    implements Positioner {
        public int marginLeft;
        public int marginTop;
        public int marginRight;
        public int marginBottom;
        public float relativeX;
        public float relativeY;

        public Impl() {
        }

        public Impl(Impl original) {
            this.marginLeft = original.marginLeft;
            this.marginTop = original.marginTop;
            this.marginRight = original.marginRight;
            this.marginBottom = original.marginBottom;
            this.relativeX = original.relativeX;
            this.relativeY = original.relativeY;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_margin(int i) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_margin(i, i);
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_margin(int i, int j) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginX(i).net_minecraft_client_gui_widget_Positioner$Impl_marginY(j);
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_margin(int i, int j, int k, int l) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginLeft(i).net_minecraft_client_gui_widget_Positioner$Impl_marginRight(k).net_minecraft_client_gui_widget_Positioner$Impl_marginTop(j).net_minecraft_client_gui_widget_Positioner$Impl_marginBottom(l);
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_marginLeft(int i) {
            this.marginLeft = i;
            return this;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_marginTop(int i) {
            this.marginTop = i;
            return this;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_marginRight(int i) {
            this.marginRight = i;
            return this;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_marginBottom(int i) {
            this.marginBottom = i;
            return this;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_marginX(int i) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginLeft(i).net_minecraft_client_gui_widget_Positioner$Impl_marginRight(i);
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_marginY(int i) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginTop(i).net_minecraft_client_gui_widget_Positioner$Impl_marginBottom(i);
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_relative(float f, float g) {
            this.relativeX = f;
            this.relativeY = g;
            return this;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_relativeX(float f) {
            this.relativeX = f;
            return this;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_relativeY(float f) {
            this.relativeY = f;
            return this;
        }

        @Override
        public Impl net_minecraft_client_gui_widget_Positioner$Impl_copy() {
            return new Impl(this);
        }

        @Override
        public Impl toImpl() {
            return this;
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_copy() {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_copy();
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_relativeY(float relativeY) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_relativeY(relativeY);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_relativeX(float relativeX) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_relativeX(relativeX);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_relative(float x, float y) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_relative(x, y);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_marginY(int marginY) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginY(marginY);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_marginX(int marginX) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginX(marginX);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_marginBottom(int marginBottom) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginBottom(marginBottom);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_marginRight(int marginRight) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginRight(marginRight);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_marginTop(int marginTop) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginTop(marginTop);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_marginLeft(int marginLeft) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_marginLeft(marginLeft);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_margin(int left, int top, int right, int bottom) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_margin(left, top, right, bottom);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_margin(int x, int y) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_margin(x, y);
        }

        @Override
        public Positioner net_minecraft_client_gui_widget_Positioner_margin(int value) {
            return this.net_minecraft_client_gui_widget_Positioner$Impl_margin(value);
        }
    }
}

