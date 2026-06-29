/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.tab;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class GridScreenTab
implements Tab {
    final private Text title;
    final protected GridWidget grid = new GridWidget();

    public GridScreenTab(Text title) {
        this.title = title;
    }

    @Override
    public Text getTitle() {
        return this.title;
    }

    @Override
    public Text getNarratedHint() {
        return Text.empty();
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.grid.forEachChild(consumer);
    }

    @Override
    public void refreshGrid(ScreenRect tabArea) {
        this.grid.refreshPositions();
        SimplePositioningWidget.setPos(this.grid, tabArea, 0.5f, 0.16666667f);
    }
}

