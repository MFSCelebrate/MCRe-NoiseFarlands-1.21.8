/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TabButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TabNavigationWidget
extends AbstractParentElement
implements Drawable,
Selectable {
    final static private int field_42489 = -1;
    final static private int field_43076 = 400;
    final static private int field_43077 = 24;
    final static private int field_43078 = 14;
    final static private Text USAGE_NARRATION_TEXT = Text.translatable("narration.tab_navigation.usage");
    final private DirectionalLayoutWidget grid = DirectionalLayoutWidget.horizontal();
    private int tabNavWidth;
    final private TabManager tabManager;
    final private ImmutableList<Tab> tabs;
    final private ImmutableList<TabButtonWidget> tabButtons;

    TabNavigationWidget(int x, TabManager tabManager, Iterable<Tab> tabs) {
        this.tabNavWidth = x;
        this.tabManager = tabManager;
        this.tabs = ImmutableList.copyOf(tabs);
        this.grid.getMainPositioner().alignHorizontalCenter();
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Tab tab : tabs) {
            builder.add((Object)this.grid.add(new TabButtonWidget(tabManager, tab, 0, 24)));
        }
        this.tabButtons = builder.build();
    }

    public static Builder builder(TabManager tabManager, int width) {
        return new Builder(tabManager, width);
    }

    public void setWidth(int width) {
        this.tabNavWidth = width;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= (double)this.grid.getX() && mouseY >= (double)this.grid.getY() && mouseX < (double)(this.grid.getX() + this.grid.getWidth()) && mouseY < (double)(this.grid.getY() + this.grid.getHeight());
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (this.getFocused() != null) {
            this.getFocused().setFocused(focused);
        }
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        TabButtonWidget tabButtonWidget;
        if (focused instanceof TabButtonWidget && (tabButtonWidget = (TabButtonWidget)focused).isNarratable()) {
            super.setFocused(focused);
            this.tabManager.setCurrentTab(tabButtonWidget.getTab(), true);
        }
    }

    @Override
    @Nullable
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        TabButtonWidget tabButtonWidget;
        if (!this.isFocused() && (tabButtonWidget = this.getCurrentTabButton()) != null) {
            return GuiNavigationPath.of(this, GuiNavigationPath.of(tabButtonWidget));
        }
        if (navigation instanceof GuiNavigation.Tab) {
            return null;
        }
        return super.getNavigationPath(navigation);
    }

    @Override
    public List<? extends Element> children() {
        return this.tabButtons;
    }

    public List<Tab> getTabs() {
        return this.tabs;
    }

    @Override
    public Selectable.SelectionType getType() {
        return this.tabButtons.stream().map(ClickableWidget::getType).max(Comparator.naturalOrder()).orElse(Selectable.SelectionType.NONE);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        Optional<TabButtonWidget> optional = this.tabButtons.stream().filter(ClickableWidget::isHovered).findFirst().or(() -> Optional.ofNullable(this.getCurrentTabButton()));
        optional.ifPresent(button -> {
            this.appendNarrations(builder.nextMessage(), (TabButtonWidget)button);
            button.appendNarrations(builder);
        });
        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, USAGE_NARRATION_TEXT);
        }
    }

    protected void appendNarrations(NarrationMessageBuilder builder, TabButtonWidget button) {
        int i;
        if (this.tabs.size() > 1 && (i = this.tabButtons.indexOf((Object)button)) != -1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.tab", 2, this.tabs.size()));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR_TEXTURE, 0, this.grid.getY() + this.grid.getHeight() - 2, 0.0f, 0.0f, ((TabButtonWidget)this.tabButtons.get(0)).getX(), 2, 32, 2);
        int i = ((TabButtonWidget)this.tabButtons.get(this.tabButtons.size() - 1)).getRight();
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR_TEXTURE, i, this.grid.getY() + this.grid.getHeight() - 2, 0.0f, 0.0f, this.tabNavWidth, 2, 32, 2);
        for (TabButtonWidget tabButtonWidget : this.tabButtons) {
            tabButtonWidget.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return this.grid.getNavigationFocus();
    }

    public void init() {
        int i = Math.min(400, this.tabNavWidth) - 28;
        int j = MathHelper.roundUpToMultiple(i / this.tabs.size(), 2);
        for (TabButtonWidget tabButtonWidget : this.tabButtons) {
            tabButtonWidget.setWidth(j);
        }
        this.grid.refreshPositions();
        this.grid.setX(MathHelper.roundUpToMultiple((this.tabNavWidth - i) / 2, 2));
        this.grid.setY(0);
    }

    public void selectTab(int index, boolean clickSound) {
        if (this.isFocused()) {
            this.setFocused((Element)this.tabButtons.get(index));
        } else if (((TabButtonWidget)this.tabButtons.get(index)).isNarratable()) {
            this.tabManager.setCurrentTab((Tab)this.tabs.get(index), clickSound);
        }
    }

    public void setTabActive(int index, boolean active) {
        if (index >= 0 && index < this.tabButtons.size()) {
            ((TabButtonWidget)this.tabButtons.get((int)index)).active = active;
        }
    }

    public void setTabTooltip(int index, @Nullable Tooltip tooltip) {
        if (index >= 0 && index < this.tabButtons.size()) {
            ((TabButtonWidget)this.tabButtons.get(index)).setTooltip(tooltip);
        }
    }

    public boolean trySwitchTabsWithKey(int keyCode) {
        int i;
        if (Screen.hasControlDown() && (i = this.getTabForKey(keyCode)) != -1) {
            this.selectTab(MathHelper.clamp(i, 0, this.tabs.size() - 1), true);
            return true;
        }
        return false;
    }

    private int getTabForKey(int keyCode) {
        return this.getTabForKey(this.getCurrentTabIndex(), keyCode);
    }

    private int getTabForKey(int index, int keyCode) {
        if (keyCode >= 49 && keyCode <= 57) {
            return keyCode - 49;
        }
        if (keyCode == 258 && index != -1) {
            int i = Screen.hasShiftDown() ? index - 1 : index + 1;
            int j = Math.floorMod(1, this.tabs.size());
            if (((TabButtonWidget)this.tabButtons.get((int)j)).active) {
                return j;
            }
            return this.getTabForKey(j, keyCode);
        }
        return -1;
    }

    private int getCurrentTabIndex() {
        Tab tab = this.tabManager.getCurrentTab();
        int i = this.tabs.indexOf((Object)tab);
        return i != -1 ? i : -1;
    }

    @Nullable
    private TabButtonWidget getCurrentTabButton() {
        int i = this.getCurrentTabIndex();
        return i != -1 ? (TabButtonWidget)this.tabButtons.get(i) : null;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        final private int width;
        final private TabManager tabManager;
        final private List<Tab> tabs = new ArrayList<Tab>();

        Builder(TabManager tabManager, int width) {
            this.tabManager = tabManager;
            this.width = width;
        }

        public Builder tabs(Tab ... tabs) {
            Collections.addAll(this.tabs, tabs);
            return this;
        }

        public TabNavigationWidget build() {
            return new TabNavigationWidget(this.width, this.tabManager, this.tabs);
        }
    }
}

