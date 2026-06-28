/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ItemGroup {
    final static Identifier ITEMS = ItemGroup.getTabTextureId("items");
    final private Text displayName;
    Identifier texture = ITEMS;
    boolean scrollbar = true;
    boolean renderName = true;
    boolean special = false;
    final private Row row;
    final private int column;
    final private Type type;
    @Nullable
    private ItemStack icon;
    private Collection<ItemStack> displayStacks = ItemStackSet.create();
    private Set<ItemStack> searchTabStacks = ItemStackSet.create();
    final private Supplier<ItemStack> iconSupplier;
    final private EntryCollector entryCollector;

    ItemGroup(Row row, int column, Type type, Text displayName, Supplier<ItemStack> iconSupplier, EntryCollector entryCollector) {
        this.row = row;
        this.column = column;
        this.displayName = displayName;
        this.iconSupplier = iconSupplier;
        this.entryCollector = entryCollector;
        this.type = type;
    }

    public static Identifier getTabTextureId(String name) {
        return Identifier.ofVanilla("textures/gui/container/creative_inventory/tab_" + name + ".png");
    }

    public static Builder create(Row location, int column) {
        return new Builder(location, column);
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public ItemStack getIcon() {
        if (this.icon == null) {
            this.icon = this.iconSupplier.get();
        }
        return this.icon;
    }

    public Identifier getTexture() {
        return this.texture;
    }

    public boolean shouldRenderName() {
        return this.renderName;
    }

    public boolean hasScrollbar() {
        return this.scrollbar;
    }

    public int getColumn() {
        return this.column;
    }

    public Row getRow() {
        return this.row;
    }

    public boolean hasStacks() {
        return !this.displayStacks.isEmpty();
    }

    public boolean shouldDisplay() {
        return this.type != Type.CATEGORY || this.hasStacks();
    }

    public boolean isSpecial() {
        return this.special;
    }

    public Type getType() {
        return this.type;
    }

    public void updateEntries(DisplayContext displayContext) {
        EntriesImpl entriesImpl = new EntriesImpl(this, displayContext.enabledFeatures);
        RegistryKey<ItemGroup> registryKey = Registries.ITEM_GROUP.getKey(this).orElseThrow(() -> new IllegalStateException("Unregistered creative tab: " + String.valueOf(this)));
        this.entryCollector.accept(displayContext, entriesImpl);
        this.displayStacks = entriesImpl.parentTabStacks;
        this.searchTabStacks = entriesImpl.searchTabStacks;
    }

    public Collection<ItemStack> getDisplayStacks() {
        return this.displayStacks;
    }

    public Collection<ItemStack> getSearchTabStacks() {
        return this.searchTabStacks;
    }

    public boolean contains(ItemStack stack) {
        return this.searchTabStacks.contains(stack);
    }

    public static final class Row
    extends Enum<Row> {
        final static public Row TOP = new Row();
        final static public Row BOTTOM = new Row();
        final static private Row[] field_41051;

        public static Row[] values() {
            return (Row[])field_41051.clone();
        }

        public static Row valueOf(String string) {
            return Enum.valueOf(Row.class, string);
        }

        private static Row[] method_47326() {
            return new Row[]{TOP, BOTTOM};
        }

        static {
            field_41051 = Row.method_47326();
        }
    }

    @FunctionalInterface
    public static interface EntryCollector {
        public void accept(DisplayContext var1, Entries var2);
    }

    public static final class Type
    extends Enum<Type> {
        final static public Type CATEGORY = new Type();
        final static public Type INVENTORY = new Type();
        final static public Type HOTBAR = new Type();
        final static public Type SEARCH = new Type();
        final static private Type[] field_41056;

        public static Type[] values() {
            return (Type[])field_41056.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static Type[] method_47327() {
            return new Type[]{CATEGORY, INVENTORY, HOTBAR, SEARCH};
        }

        static {
            field_41056 = Type.method_47327();
        }
    }

    public static class Builder {
        final static private EntryCollector EMPTY_ENTRIES = (displayContext, entries) -> {};
        final private Row row;
        final private int column;
        private Text displayName = Text.empty();
        private Supplier<ItemStack> iconSupplier = () -> ItemStack.EMPTY;
        private EntryCollector entryCollector = EMPTY_ENTRIES;
        private boolean scrollbar = true;
        private boolean renderName = true;
        private boolean special = false;
        private Type type = Type.CATEGORY;
        private Identifier texture = ITEMS;

        public Builder(Row row, int column) {
            this.row = row;
            this.column = column;
        }

        public Builder displayName(Text displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder icon(Supplier<ItemStack> iconSupplier) {
            this.iconSupplier = iconSupplier;
            return this;
        }

        public Builder entries(EntryCollector entryCollector) {
            this.entryCollector = entryCollector;
            return this;
        }

        public Builder special() {
            this.special = true;
            return this;
        }

        public Builder noRenderedName() {
            this.renderName = false;
            return this;
        }

        public Builder noScrollbar() {
            this.scrollbar = false;
            return this;
        }

        protected Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder texture(Identifier texture) {
            this.texture = texture;
            return this;
        }

        public ItemGroup build() {
            if ((this.type == Type.HOTBAR || this.type == Type.INVENTORY) && this.entryCollector != EMPTY_ENTRIES) {
                throw new IllegalStateException("Special tabs can't have display items");
            }
            ItemGroup itemGroup = new ItemGroup(this.row, this.column, this.type, this.displayName, this.iconSupplier, this.entryCollector);
            itemGroup.special = this.special;
            itemGroup.renderName = this.renderName;
            itemGroup.scrollbar = this.scrollbar;
            itemGroup.texture = this.texture;
            return itemGroup;
        }
    }

    static class EntriesImpl
    implements Entries {
        final public Collection<ItemStack> parentTabStacks = ItemStackSet.create();
        final public Set<ItemStack> searchTabStacks = ItemStackSet.create();
        final private ItemGroup group;
        final private FeatureSet enabledFeatures;

        public EntriesImpl(ItemGroup group, FeatureSet enabledFeatures) {
            this.group = group;
            this.enabledFeatures = enabledFeatures;
        }

        @Override
        public void add(ItemStack stack, StackVisibility visibility) {
            boolean bl;
            if (stack.getCount() != 1) {
                throw new IllegalArgumentException("Stack size must be exactly 1");
            }
            boolean bl2 = bl = this.parentTabStacks.contains(stack) && visibility != StackVisibility.SEARCH_TAB_ONLY;
            if (bl) {
                throw new IllegalStateException("Accidentally adding the same item stack twice " + stack.toHoverableText().getString() + " to a Creative Mode Tab: " + this.group.getDisplayName().getString());
            }
            if (stack.getItem().isEnabled(this.enabledFeatures)) {
                switch (visibility.ordinal()) {
                    case 0: {
                        this.parentTabStacks.add(stack);
                        this.searchTabStacks.add(stack);
                        break;
                    }
                    case 1: {
                        this.parentTabStacks.add(stack);
                        break;
                    }
                    case 2: {
                        this.searchTabStacks.add(stack);
                    }
                }
            }
        }
    }

    public static final class DisplayContext
    extends Record {
        final FeatureSet enabledFeatures;
        final private boolean hasPermissions;
        final private RegistryWrapper.WrapperLookup lookup;

        public DisplayContext(FeatureSet featureSet, boolean bl, RegistryWrapper.WrapperLookup wrapperLookup) {
            this.enabledFeatures = featureSet;
            this.hasPermissions = bl;
            this.lookup = wrapperLookup;
        }

        public boolean doesNotMatch(FeatureSet enabledFeatures, boolean hasPermissions, RegistryWrapper.WrapperLookup registries) {
            return !this.enabledFeatures.equals(enabledFeatures) || this.hasPermissions != hasPermissions || this.lookup != registries;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{DisplayContext.class, "enabledFeatures;hasPermissions;holders", "enabledFeatures", "hasPermissions", "lookup"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DisplayContext.class, "enabledFeatures;hasPermissions;holders", "enabledFeatures", "hasPermissions", "lookup"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DisplayContext.class, "enabledFeatures;hasPermissions;holders", "enabledFeatures", "hasPermissions", "lookup"}, this, object);
        }

        public FeatureSet enabledFeatures() {
            return this.enabledFeatures;
        }

        public boolean hasPermissions() {
            return this.hasPermissions;
        }

        public RegistryWrapper.WrapperLookup lookup() {
            return this.lookup;
        }
    }

    public static interface Entries {
        public void add(ItemStack var1, StackVisibility var2);

        default public void add(ItemStack stack) {
            this.add(stack, StackVisibility.PARENT_AND_SEARCH_TABS);
        }

        default public void add(ItemConvertible item, StackVisibility visibility) {
            this.add(new ItemStack(item), visibility);
        }

        default public void add(ItemConvertible item) {
            this.add(new ItemStack(item), StackVisibility.PARENT_AND_SEARCH_TABS);
        }

        default public void addAll(Collection<ItemStack> stacks, StackVisibility visibility) {
            stacks.forEach(stack -> this.add((ItemStack)stack, visibility));
        }

        default public void addAll(Collection<ItemStack> stacks) {
            this.addAll(stacks, StackVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    public static final class StackVisibility
    extends Enum<StackVisibility> {
        final static public StackVisibility PARENT_AND_SEARCH_TABS = new StackVisibility();
        final static public StackVisibility PARENT_TAB_ONLY = new StackVisibility();
        final static public StackVisibility SEARCH_TAB_ONLY = new StackVisibility();
        final static private StackVisibility[] field_40194;

        public static StackVisibility[] values() {
            return (StackVisibility[])field_40194.clone();
        }

        public static StackVisibility valueOf(String string) {
            return Enum.valueOf(StackVisibility.class, string);
        }

        private static StackVisibility[] method_45425() {
            return new StackVisibility[]{PARENT_AND_SEARCH_TABS, PARENT_TAB_ONLY, SEARCH_TAB_ONLY};
        }

        static {
            field_40194 = StackVisibility.method_45425();
        }
    }
}

