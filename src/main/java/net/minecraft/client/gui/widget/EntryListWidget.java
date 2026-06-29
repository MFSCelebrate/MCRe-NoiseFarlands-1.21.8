/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class EntryListWidget<E extends Entry<E>>
extends ContainerWidget {
    final static private Identifier MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/menu_list_background.png");
    final static private Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");
    final protected MinecraftClient client;
    final protected int itemHeight;
    final private List<E> children = new Entries();
    protected boolean centerListVertically = true;
    private boolean renderHeader;
    protected int headerHeight;
    @Nullable
    private E selected;
    @Nullable
    private E hoveredEntry;

    public EntryListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
        super(0, y, width, height, ScreenTexts.EMPTY);
        this.client = client;
        this.itemHeight = itemHeight;
    }

    public EntryListWidget(MinecraftClient client, int width, int height, int y, int itemHeight, int headerHeight) {
        this(client, width, height, y, itemHeight);
        this.renderHeader = true;
        this.headerHeight = headerHeight;
    }

    @Nullable
    public E getSelectedOrNull() {
        return this.selected;
    }

    public void setSelected(int index) {
        if (index == -1) {
            this.setSelected(null);
        } else if (this.getEntryCount() != 0) {
            this.setSelected(this.getEntry(index));
        }
    }

    public void setSelected(@Nullable E entry) {
        this.selected = entry;
    }

    public E getFirst() {
        return (E)((Entry)this.children.get(0));
    }

    @Nullable
    public E getFocused() {
        return (E)((Entry)super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected void clearEntries() {
        this.children.clear();
        this.selected = null;
    }

    public void replaceEntries(Collection<E> newEntries) {
        this.clearEntries();
        this.children.addAll(newEntries);
    }

    protected E getEntry(int index) {
        return (E)((Entry)this.children().get(index));
    }

    protected int addEntry(E entry) {
        this.children.add(entry);
        return this.children.size() - 1;
    }

    protected void addEntryToTop(E entry) {
        double d = (double)this.getMaxScrollY() - this.getScrollY();
        this.children.add(0, entry);
        this.setScrollY((double)this.getMaxScrollY() - d);
    }

    protected boolean removeEntryWithoutScrolling(E entry) {
        double d = (double)this.getMaxScrollY() - this.getScrollY();
        boolean bl = this.removeEntry(entry);
        this.setScrollY((double)this.getMaxScrollY() - d);
        return bl;
    }

    protected int getEntryCount() {
        return this.children().size();
    }

    protected boolean isSelectedEntry(int index) {
        return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
    }

    @Nullable
    protected final E getEntryAtPosition(double x, double y) {
        int i = this.getRowWidth() / 2;
        int j = this.getX() + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(y - (double)this.getY()) - this.headerHeight + (int)this.getScrollY() - 4;
        int n = m / this.itemHeight;
        if (x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount()) {
            return (E)((Entry)this.children().get(n));
        }
        return null;
    }

    public void position(int width, ThreePartsLayoutWidget layout) {
        this.position(width, layout.getContentHeight(), layout.getHeaderHeight());
    }

    public void position(int width, int height, int y) {
        this.setDimensions(width, height);
        this.setPosition(0, y);
        this.refreshScroll();
    }

    @Override
    protected int getContentsHeightWithPadding() {
        return this.getEntryCount() * this.itemHeight + this.headerHeight + 4;
    }

    protected void renderHeader(DrawContext context, int x, int y) {
    }

    protected void renderDecorations(DrawContext context, int mouseX, int mouseY) {
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        this.drawMenuListBackground(context);
        this.enableScissor(context);
        if (this.renderHeader) {
            int i = this.getRowLeft();
            int j = this.getY() + 4 - (int)this.getScrollY();
            this.renderHeader(context, i, j);
        }
        this.renderList(context, mouseX, mouseY, deltaTicks);
        context.disableScissor();
        this.drawHeaderAndFooterSeparators(context);
        this.drawScrollbar(context);
        this.renderDecorations(context, mouseX, mouseY);
    }

    protected void drawHeaderAndFooterSeparators(DrawContext context) {
        Identifier identifier = this.client.world == null ? Screen.HEADER_SEPARATOR_TEXTURE : Screen.INWORLD_HEADER_SEPARATOR_TEXTURE;
        Identifier identifier2 = this.client.world == null ? Screen.FOOTER_SEPARATOR_TEXTURE : Screen.INWORLD_FOOTER_SEPARATOR_TEXTURE;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY() - 2, 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier2, this.getX(), this.getBottom(), 0.0f, 0.0f, this.getWidth(), 2, 32, 2);
    }

    protected void drawMenuListBackground(DrawContext context) {
        Identifier identifier = this.client.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.getRight(), this.getBottom() + (int)this.getScrollY(), this.getWidth(), this.getHeight(), 32, 32);
    }

    protected void enableScissor(DrawContext context) {
        context.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
    }

    protected void centerScrollOn(E entry) {
        this.setScrollY(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - this.height / 2);
    }

    protected void ensureVisible(E entry) {
        int k;
        int i = this.getRowTop(this.children().indexOf(entry));
        int j = i - this.getY() - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }
        if ((k = this.getBottom() - i - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int amount) {
        this.setScrollY(this.getScrollY() + (double)amount);
    }

    @Override
    protected double getDeltaYPerScroll() {
        return (double)this.itemHeight / 2.0;
    }

    @Override
    protected int getScrollbarX() {
        return this.getRowRight() + 6 + 2;
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return Optional.ofNullable(this.getEntryAtPosition(mouseX, mouseY));
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        Element entry = this.getFocused();
        if (entry != focused && entry instanceof ParentElement) {
            ParentElement parentElement = (ParentElement)entry;
            parentElement.setFocused(null);
        }
        super.setFocused(focused);
        int i = this.children.indexOf(focused);
        if (i >= 0) {
            Entry entry2 = (Entry)this.children.get(i);
            this.setSelected(entry2);
            if (this.client.getNavigationType().isKeyboard()) {
                this.ensureVisible(entry2);
            }
        }
    }

    @Nullable
    protected E getNeighboringEntry(NavigationDirection direction) {
        return (E)this.getNeighboringEntry(direction, entry -> true);
    }

    @Nullable
    protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate) {
        return this.getNeighboringEntry(direction, predicate, this.getSelectedOrNull());
    }

    @Nullable
    protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate, @Nullable E selected) {
        int i;
        switch (direction) {
            default: {
                throw new MatchException(null, null);
            }
            case RIGHT: 
            case LEFT: {
                int n = 0;
                break;
            }
            case UP: {
                int n = -1;
                break;
            }
            case DOWN: {
                int n = 1;
            }
        }
        if (!this.children().isEmpty() && 1 != 0) {
            int j = selected == null ? (1 > 0 ? 0 : this.children().size() - 1) : this.children().indexOf(selected) + 1;
            for (int k = j; k >= 0 && k < this.children.size(); k += 1) {
                Entry entry = (Entry)this.children().get(k);
                if (!predicate.test(entry)) continue;
                return (E)entry;
            }
        }
        return null;
    }

    protected void renderList(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.getRowLeft();
        int j = this.getRowWidth();
        int k = this.itemHeight - 4;
        int l = this.getEntryCount();
        for (int m = 0; m < l; ++m) {
            int n = this.getRowTop(m);
            int o = this.getRowBottom(m);
            if (o < this.getY() || n > this.getBottom()) continue;
            this.renderEntry(context, mouseX, mouseY, deltaTicks, m, i, n, j, k);
        }
    }

    protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        E entry = this.getEntry(index);
        ((Entry)entry).drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
        if (this.isSelectedEntry(index)) {
            int i = this.isFocused() ? -1 : -8355712;
            this.drawSelectionHighlight(context, y, entryWidth, entryHeight, i, -16777216);
        }
        ((Entry)entry).render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
    }

    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int i = this.getX() + (this.width - entryWidth) / 2;
        int j = this.getX() + (this.width + entryWidth) / 2;
        context.fill(i, y - 2, j, y + entryHeight + 2, borderColor);
        context.fill(i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
    }

    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    public int getRowTop(int index) {
        return this.getY() + 4 - (int)this.getScrollY() + index * this.itemHeight + this.headerHeight;
    }

    public int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    public int getRowWidth() {
        return 220;
    }

    @Override
    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        }
        if (this.hoveredEntry != null) {
            return Selectable.SelectionType.HOVERED;
        }
        return Selectable.SelectionType.NONE;
    }

    @Nullable
    protected E remove(int index) {
        Entry entry = (Entry)this.children.get(index);
        if (this.removeEntry((Entry)this.children.get(index))) {
            return (E)entry;
        }
        return null;
    }

    protected boolean removeEntry(E entry) {
        boolean bl = this.children.remove(entry);
        if (bl && entry == this.getSelectedOrNull()) {
            this.setSelected(null);
        }
        return bl;
    }

    @Nullable
    protected E getHoveredEntry() {
        return this.hoveredEntry;
    }

    void setEntryParentList(Entry<E> entry) {
        entry.parentList = this;
    }

    protected void appendNarrations(NarrationMessageBuilder builder, E entry) {
        int i;
        List<E> list = this.children();
        if (list.size() > 1 && (i = list.indexOf(entry)) != -1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.list", 2, list.size()));
        }
    }

    @Override
    @Nullable
    public Element getFocused() {
        return this.getFocused();
    }

    @Environment(value=EnvType.CLIENT)
    class Entries
    extends AbstractList<E> {
        final private List<E> entries = Lists.newArrayList();

        Entries() {
        }

        @Override
        public E get(int i) {
            return (Entry)this.entries.get(i);
        }

        @Override
        public int size() {
            return this.entries.size();
        }

        @Override
        public E set(int i, E entry) {
            Entry entry2 = (Entry)this.entries.set(i, entry);
            EntryListWidget.this.setEntryParentList(entry);
            return entry2;
        }

        @Override
        public void add(int i, E entry) {
            this.entries.add(i, entry);
            EntryListWidget.this.setEntryParentList(entry);
        }

        @Override
        public E remove(int i) {
            return (Entry)this.entries.remove(i);
        }

        @Override
        public Object remove(int index) {
            return this.remove(index);
        }

        @Override
        public void add(int index, Object entry) {
            this.add(index, (E)((Entry)entry));
        }

        @Override
        public Object set(int index, Object entry) {
            return this.set(index, (E)((Entry)entry));
        }

        @Override
        public Object get(int index) {
            return this.get(index);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static abstract class Entry<E extends Entry<E>>
    implements Element {
        @Deprecated
        EntryListWidget<E> parentList;

        protected Entry() {
        }

        @Override
        public void setFocused(boolean focused) {
        }

        @Override
        public boolean isFocused() {
            return this.parentList.getFocused() == this;
        }

        public abstract void render(DrawContext var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        public void drawBorder(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
        }
    }
}

