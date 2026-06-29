/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DirectionalLayoutWidget
implements LayoutWidget {
    final private GridWidget grid;
    final private DisplayAxis axis;
    private int currentIndex = 0;

    private DirectionalLayoutWidget(DisplayAxis axis) {
        this(0, 0, axis);
    }

    public DirectionalLayoutWidget(int x, int y, DisplayAxis axis) {
        this.grid = new GridWidget(x, y);
        this.axis = axis;
    }

    public DirectionalLayoutWidget spacing(int spacing) {
        this.axis.setSpacing(this.grid, spacing);
        return this;
    }

    public Positioner copyPositioner() {
        return this.grid.copyPositioner();
    }

    public Positioner getMainPositioner() {
        return this.grid.getMainPositioner();
    }

    public <T extends Widget> T add(T widget, Positioner positioner) {
        return this.axis.add(this.grid, widget, this.currentIndex++, positioner);
    }

    public <T extends Widget> T add(T widget) {
        return this.add(widget, this.copyPositioner());
    }

    public <T extends Widget> T add(T widget, Consumer<Positioner> callback) {
        return this.axis.add(this.grid, widget, this.currentIndex++, Util.make(this.copyPositioner(), callback));
    }

    @Override
    public void forEachElement(Consumer<Widget> consumer) {
        this.grid.forEachElement(consumer);
    }

    @Override
    public void refreshPositions() {
        this.grid.refreshPositions();
    }

    @Override
    public int getWidth() {
        return this.grid.getWidth();
    }

    @Override
    public int getHeight() {
        return this.grid.getHeight();
    }

    @Override
    public void setX(int x) {
        this.grid.setX(x);
    }

    @Override
    public void setY(int y) {
        this.grid.setY(y);
    }

    @Override
    public int getX() {
        return this.grid.getX();
    }

    @Override
    public int getY() {
        return this.grid.getY();
    }

    public static DirectionalLayoutWidget vertical() {
        return new DirectionalLayoutWidget(DisplayAxis.VERTICAL);
    }

    public static DirectionalLayoutWidget horizontal() {
        return new DirectionalLayoutWidget(DisplayAxis.HORIZONTAL);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DisplayAxis
    extends Enum<DisplayAxis> {
        final static public DisplayAxis HORIZONTAL = new DisplayAxis();
        final static public DisplayAxis VERTICAL = new DisplayAxis();
        final static private DisplayAxis[] field_45405;

        public static DisplayAxis[] values() {
            return (DisplayAxis[])field_45405.clone();
        }

        public static DisplayAxis valueOf(String string) {
            return Enum.valueOf(DisplayAxis.class, string);
        }

        void setSpacing(GridWidget grid, int spacing) {
            switch (this.ordinal()) {
                case 0: {
                    grid.setColumnSpacing(spacing);
                    break;
                }
                case 1: {
                    grid.setRowSpacing(spacing);
                }
            }
        }

        public <T extends Widget> T add(GridWidget grid, T widget, int index, Positioner positioner) {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> grid.add(widget, 0, index, positioner);
                case 1 -> grid.add(widget, index, 0, positioner);
            };
        }

        private static DisplayAxis[] method_52743() {
            return new DisplayAxis[]{HORIZONTAL, VERTICAL};
        }

        static {
            field_45405 = DisplayAxis.method_52743();
        }
    }
}

